package io.surilitype.typing;

import java.time.Duration;

public record Stats(double wpm, double rawWpm, double accuracy) {
    public static final double CHARS_PER_WORD = 5.0;
    public static final double MILLIS_PER_MINUTE = 60000.0;

    static Stats of(int correctChars, int incorrectChars, Duration elapsed) {

        double total = correctChars + incorrectChars;
        var acc = total == 0 ? 1 : correctChars / total;

        if (elapsed == null || elapsed.toMillis() <= 0) {
            return new Stats(0, 0, acc);
        }

        var elapsedM = elapsed.toMillis() / MILLIS_PER_MINUTE;
        var wpm = (correctChars / CHARS_PER_WORD) / elapsedM;
        var rawWpm = ((correctChars + incorrectChars) / CHARS_PER_WORD) / elapsedM;

        return new Stats(wpm, rawWpm, acc);
    }
}
