package com.ufpb.br.apps4society.my_trace_table_manager.controller.exception;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.CellErrorResponse;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class ErrorExceptionHandler {

    @ExceptionHandler(TraceTableException.class)
    public ResponseEntity<ErrorResponse> traceTableErro(TraceTableException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse erroResponse = new ErrorResponse(Instant.now(),status.value(),e.getMessage(),request.getRequestURI());
        return ResponseEntity.status(status).body(erroResponse);
    }

    @ExceptionHandler(TraceTableDetailedException.class)
    public ResponseEntity<List<CellErrorResponse>> traceTableDetailedErro(
        TraceTableDetailedException e, HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getErrors());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> IOException(IOException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse erroResponse = new ErrorResponse(Instant.now(),status.value(),e.getMessage(),request.getRequestURI());
        return ResponseEntity.status(status).body(erroResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFoundErro(UserNotFoundException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse erroResponse = new ErrorResponse(Instant.now(),status.value(),e.getMessage(),request.getRequestURI());
        return ResponseEntity.status(status).body(erroResponse);
    }

    @ExceptionHandler(ThemeNotFoundException.class)
    public ResponseEntity<ErrorResponse> themeNotFoundErro(ThemeNotFoundException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse erroResponse = new ErrorResponse(Instant.now(),status.value(),e.getMessage(),request.getRequestURI());
        return ResponseEntity.status(status).body(erroResponse);
    }

    @ExceptionHandler(TraceNotFoundException.class)
    public ResponseEntity<ErrorResponse> traceNotFoundErro(TraceNotFoundException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse erroResponse = new ErrorResponse(Instant.now(),status.value(),e.getMessage(),request.getRequestURI());
        return ResponseEntity.status(status).body(erroResponse);
    }

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ErrorResponse> invalidUserErro(InvalidUserException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse erroResponse = new ErrorResponse(Instant.now(),status.value(),e.getMessage(),request.getRequestURI());
        return ResponseEntity.status(status).body(erroResponse);
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ErrorResponse> tokenErro(TokenException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.FORBIDDEN;
        ErrorResponse erroResponse = new ErrorResponse(Instant.now(),status.value(),e.getMessage(),request.getRequestURI());
        return ResponseEntity.status(status).body(erroResponse);
    }

    @ExceptionHandler(UserNotHavePermissionException.class)
    public ResponseEntity<ErrorResponse> userNotHavePermissionErro(UserNotHavePermissionException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.FORBIDDEN;
        ErrorResponse erroResponse = new ErrorResponse(Instant.now(),status.value(),e.getMessage(),request.getRequestURI());
        return ResponseEntity.status(status).body(erroResponse);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> userAlreadyExistsErro(UserAlreadyExistsException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse erroResponse = new ErrorResponse(Instant.now(),status.value(),e.getMessage(),request.getRequestURI());
        return ResponseEntity.status(status).body(erroResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validationErro(MethodArgumentNotValidException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ValidationError validationErro = new ValidationError(Instant.now(), status.value(), "Validation erro", request.getRequestURI());
        for (FieldError error: e.getFieldErrors()){
            validationErro.addErro(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(status).body(validationErro);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> validationErro(IllegalArgumentException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse erroResponse = new ErrorResponse(Instant.now(),status.value(),e.getMessage(),request.getRequestURI());
        return ResponseEntity.status(status).body(erroResponse);
    }
}
