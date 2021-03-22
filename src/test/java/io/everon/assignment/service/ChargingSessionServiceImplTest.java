package io.everon.assignment.service;

import io.everon.assignment.dto.ChargingSessionDto;
import io.everon.assignment.dto.ChargingStatusEnum;
import io.everon.assignment.exception.NotFoundException;
import io.everon.assignment.repository.SessionsRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChargingSessionServiceImplTest {

    private static final String STATION_ID = "station-4567";

    @Mock
    SessionsRepositoryImpl repository;

    ChargingSessionServiceImpl service;

    private ChargingSessionDto stoppedSession;

    private ChargingSessionDto startedSession;

    @BeforeEach
    void setUp() {
        service = new ChargingSessionServiceImpl(repository);
        this.startedSession = ChargingSessionDto.builder().id(UUID.randomUUID().toString())
                .startedAt(LocalDateTime.now())
                .stationId(STATION_ID)
                .status(ChargingStatusEnum.IN_PROGRESS).build();


        this.stoppedSession = ChargingSessionDto.builder().id(UUID.randomUUID().toString())
                .startedAt(LocalDateTime.now().minusMinutes(5))
                .stoppedAt(LocalDateTime.now())
                .stationId(STATION_ID)
                .status(ChargingStatusEnum.FINISHED).build();
    }

    @Test
    void getChargingSession() {
        when(repository.findAll()).thenReturn(singletonList(startedSession));

        List<ChargingSessionDto> fetched = service.getAllChargingSessions();

        assertThat(fetched.get(0).getId()).isSameAs(startedSession.getId());
    }

    @Test
    void createSession() {
        when(repository.save(any(ChargingSessionDto.class))).thenReturn(startedSession);

        ChargingSessionDto created = service.createChargingSession(STATION_ID);

        assertThat(created.getStationId()).isSameAs(STATION_ID);
        assertThat(created.getId()).isSameAs(startedSession.getId());
    }

    @Test
    void stopChargingSession() {

        when(repository.save(any(ChargingSessionDto.class))).thenReturn(stoppedSession);
        when(repository.findById(any())).thenReturn(startedSession);

        ChargingSessionDto stopped = service.stopChargingSession(startedSession.getId());

        assertThat(stopped.getStationId()).isSameAs(STATION_ID);
        assertThat(stopped.getStatus()).isEqualTo(ChargingStatusEnum.FINISHED);
        assertThat(stopped.getId()).isSameAs(stoppedSession.getId());
    }

    @Test
    void stopChargingSessionMultipleUpdate() {

        when(repository.save(any(ChargingSessionDto.class))).thenReturn(stoppedSession);
        when(repository.findById(any())).thenReturn(startedSession, stoppedSession, stoppedSession);

        ChargingSessionDto stoppedFirstTime = service.stopChargingSession(startedSession.getId());
        ChargingSessionDto stoppedSecondTime = service.stopChargingSession(startedSession.getId());

        assertThat(stoppedSecondTime.getStatus()).isSameAs(stoppedFirstTime.getStatus());
        assertThat(stoppedSecondTime.getStoppedAt()).isEqualTo(stoppedFirstTime.getStoppedAt());
    }

    @Test
    void stopChargingSessionWithException() {
        String sessionId = startedSession.getId();
        NotFoundException thrown = assertThrows(NotFoundException.class,
                ()-> service.stopChargingSession(sessionId),
                "Expected exception is not thrown");

        assertEquals("Charging session for the given sessionId not found", thrown.getMessage());
    }
}
