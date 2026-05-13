package mate.academy.service;

import mate.academy.dto.request.UserRegistrationRequestDto;
import mate.academy.dto.response.UserResponseDto;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto);
}
