package com.trademate.trademate.auth.service;

import com.trademate.trademate.auth.dto.LoginRequest;
import com.trademate.trademate.auth.dto.LoginResponse;
import com.trademate.trademate.auth.jwt.JwtProvider;
import com.trademate.trademate.common.exception.InvalidCredentialsException;
import com.trademate.trademate.domain.user.User;
import com.trademate.trademate.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;



    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        String token = jwtProvider.createAccessToken(user.getId(), user.getEmail());

        return new LoginResponse(user.getId(), user.getEmail(), token);
    }
}
