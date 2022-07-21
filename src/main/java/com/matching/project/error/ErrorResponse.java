package com.matching.project.error;

import com.matching.project.dto.ResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class ErrorResponse {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;
    private final String code;
    private final List<String> message;

    public static ResponseEntity<ResponseDto> toResponseEntity(ErrorCode errorCode) {
        List<String> message = new ArrayList<>();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new ResponseDto(ErrorResponse.builder()
                        .status(errorCode.getHttpStatus().value())
                        .error(errorCode.getHttpStatus().name())
                        .code(errorCode.name())
                        .message(message)
                        .build(), null));
    }

    public static ResponseEntity<ResponseDto> toResponseEntity(ErrorCode errorCode, BindingResult bindingResult) {
        List<String> message = new ArrayList<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            message.add(fieldError.getDefaultMessage());
        }

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new ResponseDto(ErrorResponse.builder()
                        .status(errorCode.getHttpStatus().value())
                        .error(errorCode.getHttpStatus().name())
                        .code(errorCode.name())
                        .message(message)
                        .build(), null));
    }
}
