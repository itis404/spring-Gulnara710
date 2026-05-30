package ru.itis.ReadMe;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.itis.ReadMe.entity.UserEntity;
import ru.itis.ReadMe.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            UserEntity admin = UserEntity.builder()
                    .username("admin")
                    .email("admin@readme.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(UserEntity.Role.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("Админ создан: login=admin, password=admin123");
        } else {
            System.out.println("Админ уже существует");
        }
    }
}