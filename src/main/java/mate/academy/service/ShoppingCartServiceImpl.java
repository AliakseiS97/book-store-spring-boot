package mate.academy.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.request.CreateCartItemRequestDto;
import mate.academy.dto.request.UpdateCartItemRequestDto;
import mate.academy.dto.response.ShoppingCartDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.ShoppingCartMapper;
import mate.academy.model.CartItem;
import mate.academy.model.ShoppingCart;
import mate.academy.repository.BookRepository;
import mate.academy.repository.ShoppingCartRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartDto getCart(Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ShoppingCart not found by userId " + userId)
                );
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto addBook(Long userId, CreateCartItemRequestDto requestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ShoppingCart not found by userId " + userId)
                );
        Optional<CartItem> existing = shoppingCart.getCartItems().stream()
                .filter(i -> i.getBook().getId().equals(requestDto.getBookId())).findFirst();
        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + requestDto.getQuantity());
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setShoppingCart(shoppingCart);
            cartItem.setQuantity(requestDto.getQuantity());
            cartItem.setBook(bookRepository.findById(requestDto.getBookId()).orElseThrow(
                    () -> new EntityNotFoundException(
                            "Book not found by bookId " + requestDto.getBookId()))
            );
            shoppingCart.getCartItems().add(cartItem);
        }
        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto update(
            Long userId,
            Long cartItemId,
            UpdateCartItemRequestDto requestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ShoppingCart not found by userId " + userId)
                );
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
    public void remove(Long userId, Long cartItemId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ShoppingCart not found by userId " + userId)
                );
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
}
