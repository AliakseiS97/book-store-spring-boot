package mate.academy.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.request.CreateCartItemRequestDto;
import mate.academy.dto.request.UpdateCartItemRequestDto;
import mate.academy.dto.response.ShoppingCartDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.CartItemMapper;
import mate.academy.mapper.ShoppingCartMapper;
import mate.academy.model.CartItem;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.repository.BookRepository;
import mate.academy.repository.ShoppingCartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookRepository bookRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    @Transactional
    public ShoppingCartDto getCart(Long userId) {
        ShoppingCart shoppingCart = getOrCreateCart(userId);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addBook(Long userId, CreateCartItemRequestDto requestDto) {
        if (!bookRepository.existsById(requestDto.getBookId())) {
            throw new EntityNotFoundException(
                    "Book not found by bookId " + requestDto.getBookId());
        }
        ShoppingCart shoppingCart = getOrCreateCart(userId);
        Optional<CartItem> existing = shoppingCart.getCartItems().stream()
                .filter(i -> i.getBook().getId().equals(requestDto.getBookId())).findFirst();
        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + requestDto.getQuantity());
        } else {
            CartItem cartItem = cartItemMapper.toEntity(requestDto);
            cartItem.setShoppingCart(shoppingCart);
            shoppingCart.getCartItems().add(cartItem);
        }
        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto update(
            Long userId,
            Long cartItemId,
            UpdateCartItemRequestDto requestDto) {
        ShoppingCart shoppingCart = getOrCreateCart(userId);
        CartItem cartItem = shoppingCart.getCartItems()
                .stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException("CartItem not found by id " + cartItemId)
                );
        cartItem.setQuantity(requestDto.getQuantity());
        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public void remove(Long userId, Long cartItemId) {
        ShoppingCart shoppingCart = getOrCreateCart(userId);
        CartItem cartItem = shoppingCart.getCartItems()
                .stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException("CartItem not found by id " + cartItemId)
                );
        shoppingCart.getCartItems().remove(cartItem);
        shoppingCartRepository.save(shoppingCart);
    }

    private ShoppingCart getOrCreateCart(Long userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    ShoppingCart shoppingCart = new ShoppingCart();
                    User user = new User();
                    user.setId(userId);
                    shoppingCart.setUser(user);
                    return shoppingCartRepository.save(shoppingCart);
                });
    }
}
