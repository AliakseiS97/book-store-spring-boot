package mate.academy.mapper;

import mate.academy.dto.response.OrderResponseDto;
import mate.academy.mapper.config.MappingConfig;
import mate.academy.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    OrderResponseDto toDto(Order order);
}
