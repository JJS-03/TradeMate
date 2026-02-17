package com.trademate.trademate.user.service;

import com.trademate.trademate.common.exception.DuplicateEmailException;
import com.trademate.trademate.domain.user.User;
import com.trademate.trademate.domain.user.UserRepository;
import com.trademate.trademate.user.dto.MeResponse;
import com.trademate.trademate.user.dto.SignUpRequest;
import com.trademate.trademate.user.dto.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignUpResponse signUp(SignUpRequest request) {

        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException();
        }


        String encoded = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getEmail(),
                encoded,
                request.getNickname()
        );

        User saved = userRepository.save(user);

        return new SignUpResponse(saved.getId(), saved.getEmail(), saved.getNickname());

    }
    @Transactional(readOnly = true)
    public MeResponse getMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return new MeResponse(user.getId(), user.getEmail(), user.getNickname());
    }
}
