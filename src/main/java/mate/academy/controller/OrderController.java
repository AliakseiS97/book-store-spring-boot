package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.request.CreateOrderRequestDto;
import mate.academy.dto.request.UpdateOrderStatusRequestDto;
import mate.academy.dto.response.OrderItemResponseDto;
import mate.academy.dto.response.OrderResponseDto;
import mate.academy.service.OrderService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Orders", description = "Operations with the user's orders")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation(
            summary = "Place an order",
            description = "Creates an order from the current user's shopping cart "
                    + "and clears the cart afterwards"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public OrderResponseDto placeOrder(
            @Valid @RequestBody CreateOrderRequestDto requestDto,
            Authentication authentication) {
        return orderService.placeOrder(requestDto, authentication);
    }

    @Operation(
            summary = "Get order history",
            description = "Returns the order history of the current user; "
                    + "supports pagination via page and size, and sorting via sort=field,asc"
    )
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<OrderResponseDto> getAllOrders(
            Authentication authentication,
            @ParameterObject Pageable pageable) {
        return orderService.getAllOrders(authentication, pageable);
    }

    @Operation(
            summary = "Update order status",
            description = "Admin-only: updates the status of an order by its id")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponseDto updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequestDto dto) {
        return orderService.updateStatus(id, dto);
    }

    @Operation(
            summary = "Get items of an order",
            description = "Returns all items of the specified order")
    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasRole('USER')")
    public List<OrderItemResponseDto> getAllOrderItems(
            @PathVariable Long orderId,
            Authentication authentication) {
        return orderService.getAllOrderItem(orderId, authentication);
    }

    @Operation(
            summary = "Get a specific order item",
            description = "Returns a single item from the specified order by item id")
    @GetMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public OrderItemResponseDto getOrderItemById(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            Authentication authentication) {
        return orderService.getOrderItemById(orderId, itemId, authentication);
    }
}
