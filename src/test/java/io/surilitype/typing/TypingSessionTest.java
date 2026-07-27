package io.surilitype.typing;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;

class TypingSessionTest {

    @Test
    void finishTypingSession() {
        var sessionWord = TypingSession.of(
            new Mode.Words(2),
            List.of("the", "quick")
        );
        assertThat(sessionWord.isFinished()).isFalse();

        var sessionTime = TypingSession.of(
            new Mode.Time(Duration.ofMillis(0)),
            List.of("the", "quick")
        );
        assertThat(sessionTime.isFinished()).isTrue();
    }

    @Test
    void onCharDefaults() {
        var session = TypingSession.of(
            new Mode.Words(2),
            List.of("the", "quick")
        );

        assertThat(session.wordIndex()).isEqualTo(0);
        assertThat(session.charIndex()).isEqualTo(0);
    }

    @Test
    void onCharAdvance() {
        var session = TypingSession.of(
            new Mode.Words(2),
            List.of("the", "quick")
        );
        session.onChar('t');
        assertThat(session.currentWord().states()).isEqualTo(
            List.of(CharState.CORRECT, CharState.UNTYPED, CharState.UNTYPED)
        );

        assertThat(session.charIndex()).isEqualTo(1);
        assertThat(session.correctChars()).isEqualTo(1);
        assertThat(session.incorrectChars()).isEqualTo(0);
    }

    @Test
    void wrongCharMarksIncorrect() {
        var session = TypingSession.of(
            new Mode.Words(2),
            List.of("the", "quick")
        );
        session.onChar('x');
        assertThat(session.currentWord().states()).isEqualTo(
            List.of(CharState.INCORRECT, CharState.UNTYPED, CharState.UNTYPED)
        );
        assertThat(session.charIndex()).isEqualTo(1);
    }

    @Test
    void extraCharAfterWordComplete() {
        var session = TypingSession.of(
            new Mode.Words(2),
            List.of("the", "quick")
        );
        session.onChar('t').onChar('h').onChar('e').onChar('x');
        assertThat(session.currentWord().states()).isEqualTo(
            List.of(CharState.CORRECT, CharState.CORRECT, CharState.CORRECT)
        );
        assertThat(session.charIndex()).isEqualTo(4);
        assertThat(session.correctChars()).isEqualTo(3);
        assertThat(session.incorrectChars()).isEqualTo(1);
    }

    @Test
    void backspaceUndoesLastChar() {
        var session = TypingSession.of(
            new Mode.Words(2),
            List.of("the", "quick")
        );
        session.onChar('t').onBackspace();

        assertThat(session.currentWord().states()).isEqualTo(
            List.of(CharState.UNTYPED, CharState.UNTYPED, CharState.UNTYPED)
        );
        assertThat(session.charIndex()).isEqualTo(0);
        assertThat(session.correctChars()).isEqualTo(0);
        assertThat(session.incorrectChars()).isEqualTo(0);
    }

    @Test
    void backspaceRemovesExtraBeforeBaseChars() {
        var session = TypingSession.of(
            new Mode.Words(2),
            List.of("the", "quick")
        );
        session.onChar('t').onChar('h').onChar('e').onChar('x').onBackspace();

        assertThat(session.currentWord().extras()).isEmpty();
        assertThat(session.currentWord().states()).isEqualTo(
            List.of(CharState.CORRECT, CharState.CORRECT, CharState.CORRECT)
        );
        assertThat(session.charIndex()).isEqualTo(3);
        assertThat(session.correctChars()).isEqualTo(3);
        assertThat(session.incorrectChars()).isEqualTo(0);
    }

    @Test
    void spaceMidWordLeavesMissChars() {
        var session = TypingSession.of(
            new Mode.Words(2),
            List.of("the", "quick")
        );
        session.onChar('t').onSpace();

        assertThat(session.charIndex()).isEqualTo(0);
        assertThat(session.wordIndex()).isEqualTo(1);
        assertThat(session.correctChars()).isEqualTo(1);
        assertThat(session.incorrectChars()).isEqualTo(0);
    }

    @Test
    void backspaceReturnsToDirtyPreviousWord() {
        var session = TypingSession.of(
            new Mode.Words(2),
            List.of("the", "quick")
        );
        session.onChar('x').onSpace();

        assertThat(session.charIndex()).isEqualTo(0);
        assertThat(session.wordIndex()).isEqualTo(1);
        session.onBackspace();
        assertThat(session.wordIndex()).isEqualTo(0);
        assertThat(session.charIndex()).isEqualTo(2);
        assertThat(session.currentWord().states()).isEqualTo(
            List.of(CharState.INCORRECT, CharState.UNTYPED, CharState.UNTYPED)
        );
    }

    @Test
    void backspaceDontReturnToCleanPreviousWord() {
        var session = TypingSession.of(
            new Mode.Words(2),
            List.of("the", "quick")
        );
        session.onChar('t').onChar('h').onChar('e').onSpace();

        assertThat(session.charIndex()).isEqualTo(0);
        assertThat(session.wordIndex()).isEqualTo(1);

        session.onBackspace();
        assertThat(session.charIndex()).isEqualTo(0);
        assertThat(session.wordIndex()).isEqualTo(1);
    }

    @Test
    void backspaceAtSessionStartIsNoOp() {
        var session = TypingSession.of(
            new Mode.Words(2),
            List.of("the", "quick")
        );
        session.onBackspace();

        assertThat(session.charIndex()).isEqualTo(0);
        assertThat(session.wordIndex()).isEqualTo(0);
    }

    @Test
    void extrasCapIsEnforced() {
        var session = TypingSession.of(
            new Mode.Words(2),
            List.of("the", "quick")
        );
        session
            .onChar('t')
            .onChar('h')
            .onChar('e')
            .onChar('1')
            .onChar('2')
            .onChar('3')
            .onChar('4')
            .onChar('5')
            .onChar('6')
            .onChar('7')
            .onChar('8')
            .onChar('9')
            .onChar('1')
            .onChar('1')
            .onChar('1')
            .onChar('1')
            .onChar('4')
            .onChar('5')
            .onChar('6')
            .onChar('7')
            .onChar('8')
            .onChar('9')
            .onChar('0')
            .onChar('1')
            .onChar('2')
            .onChar('3')
            .onChar('4')
            .onChar('5');

        assertThat(session.charIndex()).isEqualTo(23);
        assertThat(session.currentWord().extras().length()).isEqualTo(20);
        assertThat(session.correctChars()).isEqualTo(3);
        assertThat(session.incorrectChars()).isEqualTo(20);
    }

    @Test
    void onSpaceLastWord() {
        var session = finishedSession();
        assertThat(session.wordIndex()).isEqualTo(1);
        assertThat(session.isFinished()).isTrue();
    }

    @Test
    void postFinishNoOp() {
        var session = TypingSession.of(
            new Mode.Words(1),
            List.of("the")
        );
        session.onChar('t').onSpace().onBackspace();
        assertThat(session.wordIndex()).isEqualTo(1);
        assertThat(session.charIndex()).isEqualTo(1);
        assertThat(session.correctChars()).isEqualTo(1);
        assertThat(session.incorrectChars()).isEqualTo(0);
    }

    @Test
    void inputAfterFinishIsNoOp() {
        var session = finishedSession();

        var cells = session.snapshot();
        var stats = session.stats();

        session.onChar('x').onSpace().onBackspace();

        assertThat(session.snapshot()).isEqualTo(cells);
        assertThat(session.stats()).isEqualTo(stats);
    }

    @Test
    void elapsedFreezesAtFinish() {
        var session = finishedSession();
        var stats1 = session.stats();
        assertThat(session.stats()).isEqualTo(stats1);
    }

    @Test
    void progressReflectsModeState() {
        var words = TypingSession.of(new Mode.Words(2), List.of("the", "quick"));
        assertThat(words.progress()).isEqualTo("0/2");
        words.onChar('t').onChar('h').onChar('e').onSpace();
        assertThat(words.progress()).isEqualTo("1/2");

        var time = TypingSession.of(new Mode.Time(Duration.ofSeconds(30)), List.of("the"));
        assertThat(time.progress()).isIn("30 s", "29 s");
    }

    private static TypingSession finishedSession() {
        var session = TypingSession.of(new Mode.Words(1), List.of("the"));
        session.onChar('t').onChar('h').onChar('e').onSpace();
        return session;
    }
}
