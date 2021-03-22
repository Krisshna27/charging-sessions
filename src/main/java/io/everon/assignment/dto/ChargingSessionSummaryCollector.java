package io.everon.assignment.dto;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.function.BiPredicate;
import java.util.stream.Collector;

import static java.util.Objects.nonNull;


/**
 * Custom collector consumer {@link java.util.stream.Collector} to allow for counting of in-progress and finished sessionsœ
 *
 * @author Krisshna Kumar Mone
 * @see Collector
 */
@ToString
@Getter
public class ChargingSessionSummaryCollector {
    private DoubleSummaryStatistics startedSessions = new DoubleSummaryStatistics();
    private DoubleSummaryStatistics stoppedSessions = new DoubleSummaryStatistics();

    private final BiPredicate<LocalDateTime, LocalDateTime> isSessionWithinTimeLimit = LocalDateTime::isAfter;

    /**
     *To collect the count of in-progress and finished sessions within the time limit specified.
     *
     * @param timeLimit This param specifies the records created or modified after the given time value
     * @return ChargingSessionSummaryCollector
     */
    public static Collector<ChargingSessionDto, ?, ChargingSessionSummaryCollector> collector(LocalDateTime timeLimit) {
        return Collector.of(ChargingSessionSummaryCollector::new,
                (chargingSessionSummaryCollector, session) -> chargingSessionSummaryCollector.accept(session, timeLimit),
                ChargingSessionSummaryCollector::combine);
    }

    private void accept(ChargingSessionDto session, LocalDateTime timeLimit) {
        startedSessions.accept(isSessionWithinTimeLimit.test(session.getStartedAt(), timeLimit)? 1 : 0);
        if(nonNull(session.getStoppedAt())) {
            stoppedSessions.accept(isSessionWithinTimeLimit.test(session.getStoppedAt(), timeLimit) ? 1 : 0);
        }
    }

    private ChargingSessionSummaryCollector combine(ChargingSessionSummaryCollector stat) {
        startedSessions.combine(stat.startedSessions);
        stoppedSessions.combine(stat.stoppedSessions);
        return this;
    }
}
