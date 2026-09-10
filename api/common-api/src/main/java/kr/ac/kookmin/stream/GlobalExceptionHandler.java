package kr.ac.kookmin.stream;

import static kr.ac.kookmin.stream.logging.MdcKeys.REQUEST_ID;
import static kr.ac.kookmin.stream.logging.MdcKeys.USER_ID;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.common.ErrorStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException e) {
        log.warn("[requestId={}, userId={}] ({}) {}",
            MDC.get(REQUEST_ID), MDC.get(USER_ID), e.getErrorCode().name(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().status()).body(ApiResponse.error(e));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .findFirst().map(FieldError::getDefaultMessage)
            .orElse(CommonErrorCode.INVALID_INPUT.message());
        log.warn("[requestId={}, userId={}] (INVALID_INPUT) {}",
            MDC.get(REQUEST_ID), MDC.get(USER_ID), message);
        return ResponseEntity.status(CommonErrorCode.INVALID_INPUT.status())
            .body(ApiResponse.error(CommonErrorCode.INVALID_INPUT.name(), message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleInternal(Exception e) {
        log.error("[requestId={}, userId={}] {}",
            MDC.get(REQUEST_ID), MDC.get(USER_ID), e.getMessage(), e);
        return ResponseEntity.status(ErrorStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(CommonErrorCode.INTERNAL_SERVER_ERROR));
    }
}
