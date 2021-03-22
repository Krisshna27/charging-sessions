package io.everon.assignment.controller;

import io.everon.assignment.dto.ChargingSessionDto;
import io.everon.assignment.dto.ChargingSessionSummaryDto;
import io.everon.assignment.service.ChargingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * This controller provides REST endpoints to manage charging sessions at an EV Charging point
 * Apart from providing CRUD operations this class also provides summary of the charging sessions.
 * <br>
 * <br>
 *
 * @author Krisshna Kumar Mone
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chargingSessions")
public class ChargingSessionController {

    private final ChargingSessionService sessionManager;

    /**
     * This endpoint allows for creation of new charging session.
     * <p>
     * Returns one of the following status codes<br>
     * 201: successful creation of session<br>
     * 400: Mandatory field or request payload is missing<br>
     * </p>
     *
     * @param createSessionPayload Represents the session to be created for a specific charging station.
     *                             <i>stationId</i> within this payload is mandatory.
     *                             <br>
     * @return Response entity of newly created session object
     * @see io.everon.assignment.dto.ChargingSessionDto
     */
    @PostMapping
    public ResponseEntity<ChargingSessionDto> createChargingSession(@RequestBody @Valid ChargingSessionDto createSessionPayload) {
        return ResponseEntity.status(CREATED).body(sessionManager.createChargingSession(createSessionPayload.getStationId()));
    }

    /**
     * This endpoint allows for fetching all the charging sessions.
     * <br>
     * <br>
     * Returns the following status code<br>
     * 200: Successfully fetched all the sessions. If no sessions are present then an empty array is returned
     * <br>
     * <br>
     *
     * @return Response entity of list of sessions
     * @see io.everon.assignment.dto.ChargingSessionDto
     */
    @GetMapping
    public ResponseEntity<List<ChargingSessionDto>> getChargingSessions() {
        return ResponseEntity.status(OK).body(sessionManager.getAllChargingSessions());
    }

    /**
     * This endpoint stops an in-progress charging session.
     * <p>
     * Considering the idempotent nature of HTTP PUT.<br>
     * In case of already closed sessions, no modification is made, rather existing session is returned.<br>
     * <br>
     * Returns the following status code <br>
     * 200: Successfully updated the charging session <br>
     * 404: Session not found inorder to close the in_progress session <br>
     * <br>
     * </p>
     *
     * @param sessionId uniquely identifies an session
     *                  <br>
     * @return ChargingSessionDTO
     * @see io.everon.assignment.dto.ChargingSessionDto
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChargingSessionDto> stopChargingSession(@PathVariable("id") String sessionId) {
        return ResponseEntity.status(OK).body(sessionManager.stopChargingSession(sessionId));
    }

    /**
     * This endpoint gives the summary of charging session managed within the specified time limit.
     * <br>
     * <br>
     *
     * @param timeInMinutes The default time limit is 1. Minute is the default time unit followed
     * @return ChargingSessionSummaryDto
     * @see io.everon.assignment.dto.ChargingSessionSummaryDto
     */
    @GetMapping("/summary")
    public ResponseEntity<ChargingSessionSummaryDto> getChargingSummary(@RequestParam(required = false, defaultValue = "1") int timeInMinutes) {
        return ResponseEntity.status(OK).body(sessionManager.getChargingSummary(timeInMinutes));
    }
}
