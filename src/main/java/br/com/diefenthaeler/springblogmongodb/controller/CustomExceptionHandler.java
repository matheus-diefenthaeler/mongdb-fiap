package br.com.diefenthaeler.springblogmongodb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleValidationException(
            MethodArgumentNotValidException ex
    ) {
        List<String> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField()
                        + ": " + fieldError.getDefaultMessage()).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(erros);

    }

}
