package io.surilitype.view;

import static org.assertj.core.api.Assertions.assertThat;

import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import io.surilitype.typing.Mode;
import io.surilitype.typing.Stats;
import io.surilitype.typing.TypingSession;
import java.util.List;
import org.junit.jupiter.api.Test;

class TestViewTest {

    @Test
    void typingCharIsHandledAndAdvancesSession() {
        var session = TypingSession.of(new Mode.Words(1), List.of("the"));

        var testView = TestView.create(session, (NavEvent _) -> {});
        var result = testView.handleKeyEvent(
            KeyEvent.ofKey(KeyCode.ESCAPE),
            true
        );
        assertThat(result).isEqualTo(EventResult.HANDLED);

        result = testView.handleKeyEvent(
            KeyEvent.ofKey(KeyCode.BACKSPACE),
            true
        );
        assertThat(result).isEqualTo(EventResult.HANDLED);

        result = testView.handleKeyEvent(KeyEvent.ofChar(' '), true);
        assertThat(result).isEqualTo(EventResult.HANDLED);
        result = testView.handleKeyEvent(KeyEvent.ofChar('t'), true);
        assertThat(result).isEqualTo(EventResult.HANDLED);

        result = testView.handleKeyEvent(KeyEvent.ofChar('t'), false);
        assertThat(result).isEqualTo(EventResult.UNHANDLED);
    }

    @Test
    void markupEscapesLiteralBrackets() {
        var session = TypingSession.of(new Mode.Words(1), List.of("[the]"));
        var testView = TestView.create(session, (NavEvent _) -> {});
        var markup = testView.cellsToMarkup(session.snapshot());
        assertThat(markup).contains("[[").contains("]]").contains("[dim]t[/]");
    }

    @Test
    void formatStatsRendersRoundedWpmAndPercentAccuracy() {
        var session = TypingSession.of(new Mode.Words(1), List.of("the"));
        var testView = TestView.create(session, (NavEvent _) -> {});
        assertThat(testView.formatStats(new Stats(58.4, 60, 0.96))).isEqualTo(
            "58 wpm · 96% acc"
        );
    }
}
