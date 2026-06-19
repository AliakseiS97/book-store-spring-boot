package mate.academy.mapper;

import mate.academy.dto.request.CreateCartItemRequestDto;
import mate.academy.dto.response.CartItemDto;
import mate.academy.mapper.config.MappingConfig;
import mate.academy.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfig.class, uses = BookMapper.class)
public interface CartItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemDto toDto(CartItem cartItem);

    @Mapping(source = "bookId", target = "book", qualifiedByName = "bookFromId")
    CartItem toEntity(CreateCartItemRequestDto requestDto);
}
