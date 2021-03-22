package io.everon.assignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.everon.assignment.dto.ChargingSessionDto;
import io.everon.assignment.dto.ChargingSessionSummaryDto;
import io.everon.assignment.dto.ChargingStatusEnum;
import io.everon.assignment.service.ChargingSessionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChargingSessionControllerTest {

    private static final String STATION_ID = "station-4567";

    @MockBean
    ChargingSessionServiceImpl service;

    @Autowired
    private MockMvc mockMvc;

    private ChargingSessionDto startedSession;

    private ChargingSessionDto stoppedSession;

    private final ObjectMapper mapper;

    ChargingSessionControllerTest() {
        this.mapper = new ObjectMapper();
    }

    @BeforeEach
    void setUp() {
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
    void createSession() throws Exception {
        when(service.createChargingSession(any(String.class))).thenReturn(startedSession);

        ChargingSessionDto requestPayload = ChargingSessionDto.builder().stationId(STATION_ID).build();

        mockMvc.perform(post("/chargingSessions")
                .content(mapper.writeValueAsString(requestPayload))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stationId").value(requestPayload.getStationId()))
                .andExpect(jsonPath("$.id").value(startedSession.getId()));
    }

    @Test
    void getChargingSessions() throws Exception {
        when(service.getAllChargingSessions()).thenReturn(singletonList(startedSession));

        mockMvc.perform(get("/chargingSessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(startedSession.getId()));
    }

    @Test
    void stopChargingSession() throws Exception {
        when(service.stopChargingSession(any(String.class))).thenReturn(stoppedSession);

        mockMvc.perform(put("/chargingSessions/{id}", startedSession.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(stoppedSession.getId()))
                .andExpect(jsonPath("$.status").value(ChargingStatusEnum.FINISHED.toString()));
    }

    @Test
    void getSummary() throws Exception {
        ChargingSessionSummaryDto summaryResponse = ChargingSessionSummaryDto.builder()
                .startedCount(1).stoppedCount(1).totalCount(2).build();

        when(service.getChargingSummary(any(Integer.class))).thenReturn(summaryResponse);

        mockMvc.perform(get("/chargingSessions/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(summaryResponse.getTotalCount()))
                .andExpect(jsonPath("$.startedCount").value(summaryResponse.getStartedCount()));
    }

    @Test
    void getSummaryWithDifferentTimeInterval() throws Exception {

        ChargingSessionSummaryDto summaryResponse = ChargingSessionSummaryDto.builder()
                .startedCount(2).stoppedCount(1).totalCount(3).build();

        when(service.getChargingSummary(any(Integer.class))).thenReturn(summaryResponse);

        mockMvc.perform(get("/chargingSessions/summary").param("timeInMinutes", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(summaryResponse.getTotalCount()))
                .andExpect(jsonPath("$.startedCount").value(summaryResponse.getStartedCount()));
    }

    @Test
    void stopSessionMultipleAttempts() throws Exception {
        when(service.stopChargingSession(any(String.class))).thenReturn(stoppedSession);

        mockMvc.perform(put("/chargingSessions/{id}", startedSession.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(stoppedSession.getId()))
                .andExpect(jsonPath("$.status").value(ChargingStatusEnum.FINISHED.toString()))
                .andExpect(jsonPath("$.stoppedAt").value(stoppedSession.getStoppedAt().toString()));

        mockMvc.perform(put("/chargingSessions/{id}", startedSession.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(stoppedSession.getId()))
                .andExpect(jsonPath("$.status").value(ChargingStatusEnum.FINISHED.toString()))
                .andExpect(jsonPath("$.stoppedAt").value(stoppedSession.getStoppedAt().toString()));
    }

    @Test
    void createSessionWithoutStationId() throws Exception {
        when(service.createChargingSession(any(String.class))).thenReturn(startedSession);

        ChargingSessionDto requestPayload = ChargingSessionDto.builder().stationId(null).build();

        mockMvc.perform(post("/chargingSessions")
                .content(mapper.writeValueAsString(requestPayload))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
