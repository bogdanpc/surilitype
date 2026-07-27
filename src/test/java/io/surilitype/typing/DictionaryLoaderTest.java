package io.surilitype.typing;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class DictionaryLoaderTest {

    @Test
    void loadsEnglish1k() throws Exception {
        var loader = DictionaryLoader.start();
        var stopAt = System.nanoTime() + Duration.ofSeconds(3).toNanos();
        while (
                loader.state() instanceof LoadState.Loading &&
                System.nanoTime() < stopAt
            )
            Thread.sleep(10);
        assertThat(loader.state()).isInstanceOf(LoadState.Loaded.class);
        var loaded = (LoadState.Loaded) loader.state();
        assertThat(loaded.words().words()).hasSize(1000);
    }
}
