package mate.academy.mapper.config;

import mate.academy.dto.request.UserRegistrationRequestDto;
import mate.academy.dto.response.UserResponseDto;
import mate.academy.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MappingConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);
}
