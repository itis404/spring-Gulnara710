package ru.itis.ReadMe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationForm {
    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 3, max = 20, message = "В имени надо от 3 до 20 символов")
    private String username;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "в пароле надо минимум 6 символов")
    private String password;
}