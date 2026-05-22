package ru.itis.ReadMe.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.itis.ReadMe.dto.ErrorResponse;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // Обработка всех исключений
    @ExceptionHandler(Exception.class)
    public Object handleAllExceptions(Exception ex, HttpServletRequest request) {
        // Логируем стектрейс
        log.error("Exception occurred: ", ex);

        // Определяем, AJAX ли запрос
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        if (isAjax) {
            // Для AJAX возвращаем JSON с ошибкой
            ErrorResponse errorResponse = new ErrorResponse(
                    "Внутренняя ошибка сервера",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    request.getRequestURI(),
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } else {
            // Для обычного запроса – страница ошибки
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Произошла непредвиденная ошибка. Пожалуйста, попробуйте позже.");
            mav.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            mav.addObject("path", request.getRequestURI());
            return mav;
        }
    }

    // Можно добавить обработку специфических исключений, например:
    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: ", ex);
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        if (isAjax) {
            ErrorResponse errorResponse = new ErrorResponse(
                    ex.getMessage(),
                    HttpStatus.NOT_FOUND.value(),
                    request.getRequestURI(),
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } else {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", ex.getMessage());
            mav.addObject("status", HttpStatus.NOT_FOUND.value());
            mav.addObject("path", request.getRequestURI());
            return mav;
        }
    }
}
