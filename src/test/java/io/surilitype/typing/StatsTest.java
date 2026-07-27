package io.surilitype.typing;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class StatsTest {

    @Test
    void ofZeroAndNull() {
        var stats = Stats.of(0, 0, null);
        assertThat(stats).isEqualTo(new Stats(0, 0, 1));

        stats = Stats.of(0, 0, Duration.ZERO);
        assertThat(stats).isEqualTo(new Stats(0, 0, 1));
    }
    @Test
    void allCorrectGivesEqualWpmAndFullAccuracy() {
        var stats = Stats.of(60, 0, Duration.ofMinutes(1));
        assertThat(stats).isEqualTo(new Stats(12, 12, 1.0));
    }

    @Test
    void errorsRaiseRawWpmAboveWpm() {
        var stats = Stats.of(30, 30, Duration.ofMinutes(1));
        assertThat(stats).isEqualTo(new Stats(6, 12, 0.5));
    }

    @Test
    void subMinuteDurationStillCountsTowardWpm() {
        var stats = Stats.of(30, 0, Duration.ofSeconds(30));
        assertThat(stats).isEqualTo(new Stats(12, 12, 1.0));
    }

    @Test
    void noKeystrokesGivesFullAccuracy() {
        var stats = Stats.of(0, 0, Duration.ofMinutes(5));
        assertThat(stats).isEqualTo(new Stats(0, 0, 1.0));
    }

    @Test
    void zeroElapsedZeroesWpmButKeepsAccuracy() {
        var stats = Stats.of(30, 10, Duration.ZERO);
        assertThat(stats).isEqualTo(new Stats(0, 0, 0.75));
    }

    @Test
    void zeroElapsedAndNoKeystrokesIsZeroWpmFullAccuracy() {
        var stats = Stats.of(0, 0, Duration.ZERO);
        assertThat(stats).isEqualTo(new Stats(0, 0, 1.0));
    }

    @Test
    void mixedKeystrokesGiveFractionalAccuracy() {
        var stats = Stats.of(40, 10, Duration.ofMinutes(1));
        assertThat(stats).isEqualTo(new Stats(8, 10, 0.8));
    }
}
