package io.everon.assignment.datasource;

import io.everon.assignment.dto.ChargingSessionDto;

import java.util.HashMap;
import java.util.Map;

/**
 * This class serves as an in-memory database
 * <br>
 * <p>
 * Map is being used as the main data structure. <br>
 * Key is the sessionId string generated from UUID class. Value being the ChargingSessionDto <br>
 * </p>
 * <br>
 * The map served is a singleton map as this is initialized within a static inner class.
 *
 * @author Krisshna Kumar Mone
 * @see ChargingSessionDto
 */
public class InMemDataSource {

    private InMemDataSource() {
    }

    /**
     * public method to return the singleton instance of the in-memory datasource
     * @return sessions
     */
    public static Map<String, ChargingSessionDto> getSessions() {
        return SingletonHolder.sessions;
    }

    private static class SingletonHolder {
        static final Map<String, ChargingSessionDto> sessions = new HashMap<>();
    }
}
