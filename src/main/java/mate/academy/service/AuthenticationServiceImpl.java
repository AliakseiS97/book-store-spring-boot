package mate.academy.service;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.request.UserLoginRequestDto;
import mate.academy.dto.response.UserLoginResponseDto;
import mate.academy.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto request) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);
        String webToken = jwtUtil.generateToken(authentication.getName());
        UserLoginResponseDto response = new UserLoginResponseDto();
        response.setToken(webToken);
        return response;
    }
}
