package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.HashSet;
import java.util.Optional;
import mate.academy.dto.request.CreateCartItemRequestDto;
import mate.academy.dto.request.UpdateCartItemRequestDto;
import mate.academy.dto.response.ShoppingCartDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.CartItemMapper;
import mate.academy.mapper.ShoppingCartMapper;
import mate.academy.model.Book;
import mate.academy.model.CartItem;
import mate.academy.model.ShoppingCart;
import mate.academy.repository.BookRepository;
import mate.academy.repository.ShoppingCartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTest {
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CartItemMapper cartItemMapper;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    void getCart_ExistingCart_ReturnsDto() {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(1L);
        when(shoppingCartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(shoppingCartMapper.toDto(cart)).thenReturn(expected);
        ShoppingCartDto actual = shoppingCartService.getCart(1L);
        assertEquals(expected.getId(), actual.getId());
    }

    @Test
    void getCart_NoCart_CreatesNewCart() {
        ShoppingCart created = new ShoppingCart();
        created.setId(1L);
        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(1L);
        when(shoppingCartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(created);
        when(shoppingCartMapper.toDto(created)).thenReturn(expected);
        ShoppingCartDto actual = shoppingCartService.getCart(1L);
        assertEquals(expected.getId(), actual.getId());
        verify(shoppingCartRepository).save(any(ShoppingCart.class));
    }

    @Test
    void addBook_NotExistingBook_ThrowsException() {
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto();
        requestDto.setBookId(99L);
        requestDto.setQuantity(1);
        when(bookRepository.existsById(99L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addBook(1L, requestDto));
        verify(shoppingCartRepository, never()).save(any(ShoppingCart.class));
    }

    @Test
    void addBook_NewBook_AddsCartItem() {
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto();
        requestDto.setBookId(1L);
        requestDto.setQuantity(2);
        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        cart.setCartItems(new HashSet<>());
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(2);
        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(1L);
        when(bookRepository.existsById(1L)).thenReturn(true);
        when(shoppingCartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemMapper.toEntity(requestDto)).thenReturn(cartItem);
        when(shoppingCartMapper.toDto(cart)).thenReturn(expected);
        ShoppingCartDto actual = shoppingCartService.addBook(1L, requestDto);
        assertEquals(1, cart.getCartItems().size());
        assertEquals(expected.getId(), actual.getId());
        verify(shoppingCartRepository).save(cart);
    }

    @Test
    void addBook_ExistingBook_IncreasesQuantity() {
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto();
        requestDto.setBookId(1L);
        requestDto.setQuantity(3);
        Book book = new Book();
        book.setId(1L);
        CartItem existing = new CartItem();
        existing.setId(1L);
        existing.setBook(book);
        existing.setQuantity(2);
        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        cart.setCartItems(new HashSet<>(java.util.Set.of(existing)));
        when(bookRepository.existsById(1L)).thenReturn(true);
        when(shoppingCartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(shoppingCartMapper.toDto(cart)).thenReturn(new ShoppingCartDto());
        shoppingCartService.addBook(1L, requestDto);
        assertEquals(5, existing.getQuantity());
        assertEquals(1, cart.getCartItems().size());
    }

    @Test
    void update_ExistingCartItem_UpdatesQuantity() {
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto();
        requestDto.setQuantity(7);
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQuantity(2);
        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        cart.setCartItems(new HashSet<>(java.util.Set.of(cartItem)));
        when(shoppingCartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(shoppingCartMapper.toDto(cart)).thenReturn(new ShoppingCartDto());
        shoppingCartService.update(1L, 1L, requestDto);
        assertEquals(7, cartItem.getQuantity());
        verify(shoppingCartRepository).save(cart);
    }

    @Test
    void update_NotExistingCartItem_ThrowsException() {
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto();
        requestDto.setQuantity(7);
        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        cart.setCartItems(new HashSet<>());
        when(shoppingCartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.update(1L, 99L, requestDto));
    }

    @Test
    void remove_ExistingCartItem_RemovesItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        cart.setCartItems(new HashSet<>(java.util.Set.of(cartItem)));
        when(shoppingCartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        shoppingCartService.remove(1L, 1L);
        assertEquals(0, cart.getCartItems().size());
        verify(shoppingCartRepository).save(cart);
    }

    @Test
    void remove_NotExistingCartItem_ThrowsException() {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        cart.setCartItems(new HashSet<>());
        when(shoppingCartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.remove(1L, 99L));
        verify(shoppingCartRepository, never()).save(any(ShoppingCart.class));
    }
}
