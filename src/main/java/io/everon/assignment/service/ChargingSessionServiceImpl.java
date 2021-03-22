package io.everon.assignment.service;

import io.everon.assignment.dto.ChargingSessionDto;
import io.everon.assignment.dto.ChargingSessionSummaryDto;
import io.everon.assignment.dto.ChargingStatusEnum;
import io.everon.assignment.dto.ChargingSessionSummaryCollector;
import io.everon.assignment.exception.NotFoundException;
import io.everon.assignment.repository.SessionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class ChargingSessionServiceImpl implements ChargingSessionService {

    private final SessionsRepository repository;

    public ChargingSessionServiceImpl(SessionsRepository repository) {
        this.repository = repository;
    }

    @Override
    public ChargingSessionDto createChargingSession(String stationId) {
        ChargingSessionDto createdSession = ChargingSessionDto.builder()
                .id(UUID.randomUUID().toString())
                .startedAt(LocalDateTime.now())
                .stationId(stationId)
                .status(ChargingStatusEnum.IN_PROGRESS)
                .build();
      return repository.save(createdSession);
    }

    @Override
    public List<ChargingSessionDto> getAllChargingSessions() {
        return repository.findAll();
    }

    @Override
    public ChargingSessionDto stopChargingSession(final String sessionId) {

        ChargingSessionDto existingChargingSession = repository.findById(sessionId);

        if (isNull(existingChargingSession)) {
            throw new NotFoundException("Charging session for the given sessionId not found");
        }

        // Found session is already in FINISHED status
        if(existingChargingSession.getStatus().equals(ChargingStatusEnum.FINISHED)){
            log.debug("Session with id:{} is already marked as finished, update ignored", sessionId);
            return existingChargingSession;
        }

        existingChargingSession.setStoppedAt(LocalDateTime.now());
        existingChargingSession.setStatus(ChargingStatusEnum.FINISHED);

        return repository.save(existingChargingSession);
    }

    @Override
    public ChargingSessionSummaryDto getChargingSummary(int timeInMinutes) {

        ChargingSessionSummaryCollector stat = repository.getSessionSummaryWithinTimeLimit(timeInMinutes);

        return ChargingSessionSummaryDto.builder()
                .totalCount(Math.addExact((long)stat.getStartedSessions().getSum(), (long)stat.getStoppedSessions().getSum()))
                .startedCount((long)stat.getStartedSessions().getSum())
                .stoppedCount((long)stat.getStoppedSessions().getSum())
                .build();
    }
}
