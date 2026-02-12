package com.trademate.trademate.runner;

import com.trademate.trademate.domain.user.User;
import com.trademate.trademate.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserTestRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // ✅ 추가

    @Override
    public void run(String... args) {
        String email = "test@trademate.com";

        if (!userRepository.existsByEmail(email)) {
            User user = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode("1234")) // ✅ 암호화
                    .nickname("runner-user")                  // ✅ 닉네임 추가
                    .build();

            User saved = userRepository.save(user);
            System.out.println("[Runner] saved user id=" + saved.getId());
        } else {
            System.out.println("[Runner] user already exists");
        }
    }
}

