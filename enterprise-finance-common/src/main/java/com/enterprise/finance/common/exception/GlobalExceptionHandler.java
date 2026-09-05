package com.enterprise.finance.common.exception;

import com.enterprise.finance.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public R<Void> handleBusiness(BusinessException ex) {
        return R.fail(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("validation failed");
        return R.fail(400, message);
    }

    @ExceptionHandler(Exception.class)
    public R<Void> handleOther(Exception ex) {
        log.error("unhandled exception", ex);
        return R.fail(ex.getMessage());
    }
}
