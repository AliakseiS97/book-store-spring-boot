package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.request.CreateCartItemRequestDto;
import mate.academy.dto.request.UpdateCartItemRequestDto;
import mate.academy.dto.response.ShoppingCartDto;
import mate.academy.model.User;
import mate.academy.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart", description = "Operations with the user's shopping cart")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(
            summary = "Get the shopping cart",
            description = "Returns the shopping cart of the currently authenticated user"
    )
    @GetMapping
    public ShoppingCartDto getCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.getCart(user.getId());
    }

    @Operation(
            summary = "Add a book to the cart",
            description = "Adds a book with the given quantity to the cart; "
                    + "if the book is already present, increases its quantity"
    )
    @PostMapping
    public ShoppingCartDto addBook(
            Authentication authentication,
            @RequestBody @Valid CreateCartItemRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.addBook(user.getId(), requestDto);
    }

    @Operation(
            summary = "Update book quantity in the cart",
            description = "Updates the quantity of a specific cart item by its id"
    )
    @PutMapping("/cart-items/{cartItemId}")
    public ShoppingCartDto updateBook(
            Authentication authentication,
            @RequestBody @Valid UpdateCartItemRequestDto requestDto,
            @PathVariable Long cartItemId) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.update(user.getId(), cartItemId, requestDto);
    }

    @Operation(
            summary = "Remove a book from the cart",
            description = "Removes a specific cart item from the cart by its id"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/cart-items/{cartItemId}")
    public void deleteBook(
            Authentication authentication,
            @PathVariable Long cartItemId) {
        User user = (User) authentication.getPrincipal();
        shoppingCartService.remove(user.getId(), cartItemId);
    }
}
