package io.everon.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO provides summary count of charging sessions.
 *
 * @author Krisshna Kumar Mone
 */
@Data
@AllArgsConstructor
@Builder
public class ChargingSessionSummaryDto {

    private long totalCount;

    private long startedCount;

    private long stoppedCount;
}
