package io.everon.assignment.repository;

import io.everon.assignment.datasource.InMemDataSource;
import io.everon.assignment.dto.ChargingSessionDto;
import io.everon.assignment.dto.ChargingSessionSummaryCollector;
import io.everon.assignment.dto.ChargingStatusEnum;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

@Repository
public class SessionsRepositoryImpl implements SessionsRepository {

    @Override
    public ChargingSessionDto save(ChargingSessionDto objChargingSessionDto) {
        InMemDataSource.getSessions().put(objChargingSessionDto.getId(), objChargingSessionDto);
        return objChargingSessionDto;
    }

    @Override
    public ChargingSessionDto findById(String id) {
        return InMemDataSource.getSessions().get(id);
    }

    @Override
    public ChargingSessionSummaryCollector getSessionSummaryWithinTimeLimit(int timeInMinutes) {
        LocalDateTime timeLimit = LocalDateTime.now().minusMinutes(timeInMinutes);
        return InMemDataSource.getSessions().values().stream()
                .filter(chargingSessionDto -> compareDate.test(chargingSessionDto, timeLimit))
                .collect(ChargingSessionSummaryCollector.collector(timeLimit));
    }

    private final BiPredicate<ChargingSessionDto, LocalDateTime> compareDate =
            (chargingSessionDto, currentTime) ->
                    isSessionStartTimeAfterTimeLimit(chargingSessionDto, currentTime)
                            || isSessionFinishedAfterTimeLimit(chargingSessionDto, currentTime);

    private boolean isSessionStartTimeAfterTimeLimit(ChargingSessionDto chargingSessionDto, LocalDateTime currentTime) {
        return chargingSessionDto.getStartedAt().isAfter(currentTime);
    }

    private boolean isSessionFinishedAfterTimeLimit(ChargingSessionDto chargingSessionDto, LocalDateTime currentTime) {
        return chargingSessionDto.getStatus().equals(ChargingStatusEnum.FINISHED) &&
                chargingSessionDto.getStoppedAt().isAfter(currentTime);
    }

    @Override
    public List<ChargingSessionDto> findAll() {
        return new ArrayList<>(InMemDataSource.getSessions().values());
    }

    @Override
    public void delete(String id) {
        InMemDataSource.getSessions().remove(id);
    }
}
