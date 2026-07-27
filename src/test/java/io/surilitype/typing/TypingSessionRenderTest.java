package io.surilitype.typing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TypingSessionRenderTest {

    @Test
    void renderFreshSession() {
        var session = TypingSession.of(
            new Mode.Words(2),
            List.of("the", "quick")
        );

        var snapshot = session.snapshot();

        assertThat(snapshot.cells().size()).isEqualTo(9);
        assertThat(snapshot.cells()).isEqualTo(
            List.of(
                new Cell('t', CharState.UNTYPED),
                new Cell('h', CharState.UNTYPED),
                new Cell('e', CharState.UNTYPED),
                new Cell(' ', CharState.UNTYPED),
                new Cell('q', CharState.UNTYPED),
                new Cell('u', CharState.UNTYPED),
                new Cell('i', CharState.UNTYPED),
                new Cell('c', CharState.UNTYPED),
                new Cell('k', CharState.UNTYPED)
            )
        );
        assertThat(snapshot.caret()).isEqualTo(0);
    }

    @Test
    void spaceMovesCaretToNextWordStart() {
        var session = TypingSession.of(
            new Mode.Words(2),
            List.of("the", "quick")
        );
        session.onChar('t').onChar('h').onChar('e').onSpace();
        var snapshot = session.snapshot();
        assertThat(snapshot.caret()).isEqualTo(4);
    }

    @Test
    void firstCorrectCharAdvancesCaret() {
        var session = TypingSession.of(
            new Mode.Words(2),
            List.of("the", "quick")
        );
        session.onChar('t');

        var snapshot = session.snapshot();
        assertThat(snapshot.cells().getFirst()).isEqualTo(
            new Cell('t', CharState.CORRECT)
        );
        assertThat(snapshot.caret()).isEqualTo(1);
    }

    @Test
    @DisplayName(
        "characters typed beyond a word's length render as EXTRA cells"
    )
    void typingPastWordEndAddsExtraCells() {
        var session = TypingSession.of(
            new Mode.Words(2),
            List.of("the", "quick")
        );
        session.onChar('t').onChar('h').onChar('e').onChar('x').onChar('y');
        var snapshot = session.snapshot();

        assertThat(snapshot.cells().get(3)).isEqualTo(
            new Cell('x', CharState.EXTRA)
        );
        assertThat(snapshot.cells().get(4)).isEqualTo(
            new Cell('y', CharState.EXTRA)
        );
        assertThat(snapshot.caret()).isEqualTo(5);
    }
}
