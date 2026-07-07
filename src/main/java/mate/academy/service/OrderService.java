package mate.academy.service;

import java.util.List;
import mate.academy.dto.request.CreateOrderRequestDto;
import mate.academy.dto.request.UpdateOrderStatusRequestDto;
import mate.academy.dto.response.OrderItemResponseDto;
import mate.academy.dto.response.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface OrderService {

    OrderResponseDto placeOrder(CreateOrderRequestDto requestDto, Authentication authentication);

    Page<OrderResponseDto> getAllOrders(Authentication authentication, Pageable pageable);

    OrderResponseDto updateStatus(Long id, UpdateOrderStatusRequestDto dto);

    List<OrderItemResponseDto> getAllOrderItem(Long orderId, Authentication authentication);

    OrderItemResponseDto getOrderItemById(Long orderId,Long itemId, Authentication authentication);
}
