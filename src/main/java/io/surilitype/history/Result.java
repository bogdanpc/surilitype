package io.surilitype.history;

import java.time.Instant;
import java.util.Set;

import io.surilitype.typing.Config;
import io.surilitype.typing.Mode;
import io.surilitype.typing.Stats;
import io.surilitype.typing.WordModifier;

public record Result(
    double wpm,
    double accuracy,
    Mode mode,
    Set<WordModifier> modifiers,
    Instant timestamp
) {
    public static Result from(Stats stats, Config config, Instant now) {
        return new Result(
            stats.wpm(),
            stats.accuracy(),
            config.mode(),
            config.modifiers(),
            now
        );
    }
}
