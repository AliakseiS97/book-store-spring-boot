package mate.academy.mapper;

import mate.academy.dto.response.ShoppingCartDto;
import mate.academy.mapper.config.MappingConfig;
import mate.academy.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {
    @Mapping(source = "user.id", target = "userId")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);
}
