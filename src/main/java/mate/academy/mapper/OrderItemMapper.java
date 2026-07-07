package mate.academy.mapper;

import mate.academy.dto.response.OrderItemResponseDto;
import mate.academy.mapper.config.MappingConfig;
import mate.academy.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfig.class)
public interface OrderItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    OrderItemResponseDto toDto(OrderItem orderItem);
}
