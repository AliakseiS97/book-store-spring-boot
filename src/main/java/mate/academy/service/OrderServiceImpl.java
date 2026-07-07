package mate.academy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.request.CreateOrderRequestDto;
import mate.academy.dto.request.UpdateOrderStatusRequestDto;
import mate.academy.dto.response.OrderItemResponseDto;
import mate.academy.dto.response.OrderResponseDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.OrderItemMapper;
import mate.academy.mapper.OrderMapper;
import mate.academy.model.CartItem;
import mate.academy.model.Order;
import mate.academy.model.OrderItem;
import mate.academy.model.ShoppingCart;
import mate.academy.model.Status;
import mate.academy.model.User;
import mate.academy.repository.OrderRepository;
import mate.academy.repository.ShoppingCartRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    @Transactional
    public OrderResponseDto placeOrder(CreateOrderRequestDto requestDto,
                                       Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("ShoppingCart not found")
                );
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Status.PENDING);
        order.setShippingAddress(requestDto.getShippingAddress());

        Set<OrderItem> orderItems = shoppingCart.getCartItems().stream()
                .map(c -> createOrderItem(c, order))
                .collect(Collectors.toSet());
        order.setOrderItems(orderItems);

        BigDecimal total = orderItems.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);

        Order savedOrder = orderRepository.save(order);
        shoppingCart.getCartItems().clear();
        shoppingCartRepository.save(shoppingCart);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getAllOrders(Authentication authentication, Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        Page<Order> orders = orderRepository.findAllByUser_Id(user.getId(), pageable);
        return orders.map(orderMapper::toDto);
    }

    @Override
    @Transactional
    public OrderResponseDto updateStatus(Long id, UpdateOrderStatusRequestDto dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found by id: " + id)
                );
        order.setStatus(dto.getStatus());
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponseDto> getAllOrderItem(Long orderId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found by id: " + orderId)
                );
        if (!user.getId().equals(order.getUser().getId())) {
            throw new EntityNotFoundException(
                    "Order not found by id: " + orderId);
        }
        return order.getOrderItems().stream().map(orderItemMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderItemResponseDto getOrderItemById(Long orderId, Long itemId,
                                                 Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found by id: " + orderId));
        if (!user.getId().equals(order.getUser().getId())) {
            throw new EntityNotFoundException(
                    "Order not found by id: " + orderId);
        }
        OrderItem orderItem = order.getOrderItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "OrderItem not found by id: " + itemId));
        return orderItemMapper.toDto(orderItem);
    }

    private OrderItem createOrderItem(CartItem cartItem, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setBook(cartItem.getBook());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getBook().getPrice());
        return orderItem;
    }
}
