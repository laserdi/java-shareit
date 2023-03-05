package ru.practicum.shareit.exception.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.NotFoundRecordInBD;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.ValidateException;

@RestControllerAdvice
@Slf4j
@Getter
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleForBadRequest(final ValidateException ex) {
        String error = "Error message";
        String message = ex.getMessage();
        log.error(error + " — " + message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error message" + ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleForNotFound(final NotFoundRecordInBD ex) {
        String error = "Error 404. Not Found.";
        String message = ex.getMessage();
        log.error(error + " — " + message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error + " — " + message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleForUnsupportedStatus(final UnsupportedStatusException ex) {
//        String error = "{\n\"error\":\"Unknown state: UNSUPPORTED_STATUS\",\n\"message\":\"UNSUPPORTED_STATUS\"\n}";
//        String message = ex.getMessage();
//        log.error(error + " — " + message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\n\"error\":\"Unknown state: " +
                "UNSUPPORTED_STATUS\",\n\"message\":\"UNSUPPORTED_STATUS\"\n}");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleForMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        String error = "Error 400. Не правильное значение аргумента.\t" + ex.getMessage();
        String message = ex.getMessage();
        log.error(error);
//        System.out.println(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error 400. Не правильное значение аргумента.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<?> handleForPSQLException(final DataIntegrityViolationException ex) {
        String error = "Error 409. Не правильное значение аргумента при добавлении в БД.\nВероятное дублирование.\t" + ex.getMessage();
        String message = ex.getMessage();
        log.error(error);
//        System.out.println(message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
