package mate.academy.service;

import mate.academy.dto.request.CreateCartItemRequestDto;
import mate.academy.dto.request.UpdateCartItemRequestDto;
import mate.academy.dto.response.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getCart(Long userId);

    ShoppingCartDto addBook(Long userId, CreateCartItemRequestDto requestDto);

    ShoppingCartDto update(Long userId, Long cartItemId, UpdateCartItemRequestDto requestDto);

    void remove(Long userId, Long cartItemId);
}
