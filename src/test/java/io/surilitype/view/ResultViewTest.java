package io.surilitype.view;

import static org.assertj.core.api.Assertions.assertThat;

import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import io.surilitype.history.Result;
import io.surilitype.typing.Mode;
import io.surilitype.view.NavEvent.ShowMenu;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class ResultViewTest {

    @Test
    void enterNavigatesToMenu() {
        var captured = new AtomicReference<NavEvent>();
        var nav = (Consumer<NavEvent>) captured::set;

        var view = ResultsView.create(
            new Result(58, 0.96, new Mode.Words(25), Set.of(), Instant.EPOCH),
            nav
        );
        var result = view.handleKeyEvent(KeyEvent.ofKey(KeyCode.ENTER), true);
        assertThat(result).isEqualTo(EventResult.HANDLED);
        assertThat(captured.get()).isInstanceOf(ShowMenu.class);
    }
}
