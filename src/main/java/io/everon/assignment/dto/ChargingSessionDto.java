package io.everon.assignment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

/**
 * Represents a charging session
 *
 * @author Krisshna Kumar Mone
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChargingSessionDto {

    private String id;

    @NotEmpty
    private String stationId;

    private LocalDateTime startedAt;

    private LocalDateTime stoppedAt;

    private ChargingStatusEnum status;
}
