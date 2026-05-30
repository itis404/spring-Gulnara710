package ru.itis.ReadMe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostForm {
    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(max = 200, message = "Заголовок должен быть не больше 200 символов")
    private String title;

    @NotBlank(message = "Содержание не может быть пустым")
    private String content;

    private String hashtagsInput;
}
