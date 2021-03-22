package io.everon.assignment.repository;

import io.everon.assignment.dto.ChargingSessionDto;
import io.everon.assignment.dto.ChargingSessionSummaryCollector;
import io.everon.assignment.dto.ChargingStatusEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SessionsRepositoryImplTest {

    @Autowired
    SessionsRepository repository;

    ChargingSessionDto validSession;

    @BeforeEach
    void setUp() {
       validSession =  ChargingSessionDto.builder().id("12344")
                .startedAt(LocalDateTime.now().minusMinutes(5))
                .stoppedAt(LocalDateTime.now())
                .stationId("abced-2")
                .status(ChargingStatusEnum.FINISHED).build();

       repository.save(validSession);
    }

    @AfterEach
    void remove(){
        repository.delete(validSession.getId().toString());
    }

    @Test
    void save() {
        ChargingSessionDto result = repository.save(validSession);
        assertThat(validSession).isEqualTo(result);
    }

    @Test
    void findById() {
        ChargingSessionDto result = repository.findById(validSession.getId().toString());
        assertThat(validSession).isEqualTo(result);
    }

    @Test
    void findAll() {
        List<ChargingSessionDto> result = repository.findAll();
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void delete() {
        repository.delete(validSession.getId());
        List<ChargingSessionDto> result = repository.findAll();
        assertThat(result.size()).isZero();
    }

    @Test
    void summary() {
        ChargingSessionSummaryCollector summary = repository.getSessionSummaryWithinTimeLimit(1);
        assertThat(summary.getStartedSessions().getSum()).isZero();
        assertThat(summary.getStoppedSessions().getSum()).isEqualTo(1);
    }
}
