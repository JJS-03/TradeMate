package com.trademate.trademate.runner;

import com.trademate.trademate.domain.user.User;
import com.trademate.trademate.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserTestRunner implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        String email = "test@trademate.com";

        if (!userRepository.existsByEmail(email)) {
            User user = User.builder()
                    .email(email)
                    .password("1234")
                    .build();

            userRepository.save(user);
            System.out.println("[Runner] saved user id=" + user.getId());
        } else {
            System.out.println("[Runner] user already exists");
        }
    }
}
