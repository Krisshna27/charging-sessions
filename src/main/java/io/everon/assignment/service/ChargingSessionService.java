package io.everon.assignment.service;

import io.everon.assignment.dto.ChargingSessionDto;
import io.everon.assignment.dto.ChargingSessionSummaryDto;

import java.util.*;

/**
 * Provides business implementations for managing charging sessions and computing session stat's
 *
 * @author Krisshna Kumar Mone
 */
public interface ChargingSessionService {

    /**
     * Method to create a charging session
     * @param stationId value that determines a charging station
     * @return chargingSessionDto
     */
    ChargingSessionDto createChargingSession(String stationId);

    /**
     * Method to fetch all charging sessions
     * @return List of charging sessions
     */
    List<ChargingSessionDto> getAllChargingSessions();

    /**
     * To update a existing charging session as finished.
     * Also records the stoppedAt attribute at the invokation of API.
     * @param sessionId uniqueId of the session to be marked as finished
     * @return chargingSessionDto with updated status
     */
    ChargingSessionDto stopChargingSession(String sessionId);

    /**
     * Computes the statistics of charging sessions handled so far
     * @param timeInMinutes sessions to be considered for statistics compute, created or modified after the specified limit
     * @return chargingSessionSummaryDto
     */
    ChargingSessionSummaryDto getChargingSummary(int timeInMinutes);
}
