package com.example.laboratorio3.dto.response;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String path;
}