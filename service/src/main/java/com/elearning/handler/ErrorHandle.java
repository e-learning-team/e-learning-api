package com.elearning.handler;

import com.elearning.models.wrapper.ObjectResponseWrapper;
import com.sun.jdi.InternalException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandle extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ObjectResponseWrapper handleInternalException(Exception ex) {
        if (ex instanceof BadCredentialsException){
            return ObjectResponseWrapper.builder().status(0).message(ex.getMessage()).build();
        }
        printLogException(ex);
        return ObjectResponseWrapper.builder().status(0).message(ex.getMessage()).build();
    }
    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ObjectResponseWrapper handleSignatureException(SignatureException ex){
            return ObjectResponseWrapper.builder().status(0).message(ex.getMessage()).build();
    }
    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ObjectResponseWrapper handleAccessDeniedException(AccessDeniedException ex){
        return ObjectResponseWrapper.builder().status(0).message(ex.getMessage()).build();
    }
    @ExceptionHandler({ExpiredJwtException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ObjectResponseWrapper handleExpiredJwtException(ExpiredJwtException ex){
        return ObjectResponseWrapper.builder().status(0).message(ex.getMessage()).build();
    }
    @ExceptionHandler(ServiceException.class)
    protected ObjectResponseWrapper handleServiceException(ServiceException e) {
        printLogException(e);
        return ObjectResponseWrapper.builder().status(0).message(e.getReason()).build();
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    protected ObjectResponseWrapper handleConstraintViolationException(ConstraintViolationException e) {
        return ObjectResponseWrapper.builder().status(0).message(e.getMessage()).build();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status, @NonNull WebRequest request) {
        final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        return ResponseEntity.badRequest().body(ObjectResponseWrapper.builder()
                .status(0)
                .message(fieldErrors.get(0).getDefaultMessage())
                .build());
    }

    private void printLogException(Exception e) {
        e.printStackTrace();
        log.error("=========================================");
        log.error("EXCEPTION: {}", e.getMessage());
        for (int i = 0; i < e.getStackTrace().length; i++) {
            log.error(e.getStackTrace()[i].toString());
            if (i == 4) {
                break;
            }
        }
        log.error("==========================================");
    }

}
