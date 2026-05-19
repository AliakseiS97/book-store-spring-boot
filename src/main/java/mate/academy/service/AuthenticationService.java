package mate.academy.service;

import mate.academy.dto.request.UserLoginRequestDto;
import mate.academy.dto.response.UserLoginResponseDto;

public interface AuthenticationService {
    UserLoginResponseDto login(UserLoginRequestDto request);
}
