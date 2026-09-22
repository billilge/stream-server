package kr.ac.kookmin.stream.api.common;

import static kr.ac.kookmin.stream.logging.MdcKeys.REQUEST_ID;
import static kr.ac.kookmin.stream.logging.MdcKeys.USER_ID;

import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
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
        // 인증 실패(401)는 인터넷에 노출된 서버에서 스캐너·봇으로 인해 상시 발생하는 정상 트래픽이므로 DEBUG로 낮춘다
        if (e.getErrorCode().status() == ErrorStatus.UNAUTHORIZED) {
            log.debug("[requestId={}, userId={}] ({}) {}",
                MDC.get(REQUEST_ID), MDC.get(USER_ID), e.getErrorCode().name(), e.getMessage());
        } else {
            log.warn("[requestId={}, userId={}] ({}) {}",
                MDC.get(REQUEST_ID), MDC.get(USER_ID), e.getErrorCode().name(), e.getMessage());
        }
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("[requestId={}, userId={}] (INVALID_INPUT) {}",
            MDC.get(REQUEST_ID), MDC.get(USER_ID), e.getMessage());
        return ResponseEntity.status(CommonErrorCode.INVALID_INPUT.status())
            .body(ApiResponse.error(CommonErrorCode.INVALID_INPUT.name(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleInternal(Exception e) {
        log.error("[requestId={}, userId={}] {}",
            MDC.get(REQUEST_ID), MDC.get(USER_ID), e.getMessage(), e);
        return ResponseEntity.status(ErrorStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(CommonErrorCode.INTERNAL_SERVER_ERROR));
    }
}
