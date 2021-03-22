package io.everon.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * To provide common error response in case of exceptions.
 */
@Data
@AllArgsConstructor
@Builder
public class ErrorResponseDto {

    private List<String> errors;

    private String timeStamp;

    private String status;
}
