package com.example.pampam.exception;

import com.example.pampam.common.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(value = EcommerceApplicationException.class)
    public ResponseEntity<Object> handleEcommerceApplicationException(EcommerceApplicationException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(BaseResponse.failResponse(e.getErrorCode().getCode(), e.getErrorCode().getMessage()));
    }

    @ExceptionHandler(value = CustomJwtSignatureException.class)
    public ResponseEntity<Object> handleCustomJwtSignatureException(CustomJwtSignatureException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(BaseResponse.failResponse(e.getErrorCode().getCode(), e.getErrorCode().getMessage()));
    }

}
