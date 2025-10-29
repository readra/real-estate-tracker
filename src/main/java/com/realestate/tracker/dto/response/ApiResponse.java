package com.realestate.tracker.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공통 API Response DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String error;
    
    // 성공 응답 생성
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    // 성공 응답 생성 (메시지 없음)
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Success");
    }
    
    // 실패 응답 생성
    public static <T> ApiResponse<T> error(String errorMessage) {
        return ApiResponse.<T>builder()
            .success(false)
            .message("Error occurred")
            .error(errorMessage)
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    // 실패 응답 생성 (상세 메시지 포함)
    public static <T> ApiResponse<T> error(String message, String errorMessage) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .error(errorMessage)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
