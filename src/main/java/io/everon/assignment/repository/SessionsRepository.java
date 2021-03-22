package io.everon.assignment.repository;

import io.everon.assignment.dto.ChargingSessionDto;
import io.everon.assignment.dto.ChargingSessionSummaryCollector;

import java.util.List;

/**
 * Repository interface which provides basic operations to modify a record within the data source. <br>
 * This interface can be extended to be used as an JPA repository if a database needs to be included.
 * <br>
 * <br>
 *
 * @author Krisshna Kumar Mone
 */
public interface SessionsRepository {

    /**
     *Method to create or update a charging session
     * @param objChargingSessionDto request payload to be created or updated
     * @return ChargingSessionDto
     */
    ChargingSessionDto save(ChargingSessionDto objChargingSessionDto);

    /**
     * Method to find a charging session by the id
     * @param id uniqueId of a session object
     * @return ChargingSessionDto
     */
    ChargingSessionDto findById(String id);

    /**
     * Method to list all the charging sessions
     * @return List of charging sessions
     */
    List<ChargingSessionDto> findAll();

    /**
     * Method to delete a specific charging session
     * @param id unique if of the session to be deleted
     */
    void delete(String id);

    /**
     * Method to return summary of all the charging sessions which had be stored or modified
     * @param timeInMinutes Time limit to determine the no. of sessions to be allowed based on start and stop time of session.
     * @return ChargingSessionSummaryCollector
     * @see ChargingSessionSummaryCollector
     */
    ChargingSessionSummaryCollector getSessionSummaryWithinTimeLimit(int timeInMinutes);
}
