package io.surilitype.typing;

import java.time.Duration;

public sealed interface Mode {
    record Words(int count) implements Mode {
    }

    record Time(Duration limit) implements Mode {
    }
}
