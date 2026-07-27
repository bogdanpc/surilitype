package io.surilitype.typing;

import io.surilitype.typing.Mode.Words;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TypingSession {

    private final Mode mode;
    private final List<Word> words;
    private final Instant startedAt;
    private int wordIndex;
    private int charIndex;
    private Word currentWord;
    private int correctChars;
    private int incorrectChars;
    private Instant endedAt;
    private static final int MAX_EXTRA_CHARS = 20;

    private TypingSession(Mode mode, List<String> texts, Instant startedAt) {
        this.mode = mode;
        this.words = texts.stream().map(Word::of).collect(Collectors.toList());
        this.startedAt = startedAt;
        this.currentWord = this.words.getFirst();
    }

    public static TypingSession of(Mode mode, List<String> words) {
        return new TypingSession(mode, words, Instant.now());
    }

    int wordIndex() {
        return wordIndex;
    }

    int charIndex() {
        return charIndex;
    }

    Word currentWord() {
        return currentWord;
    }

    int correctChars() {
        return correctChars;
    }

    int incorrectChars() {
        return incorrectChars;
    }

    public boolean isFinished() {
        return switch (mode) {
            case Words(var count) -> wordIndex >= count;
            case Mode.Time(Duration limit) -> elapsed().compareTo(limit) >= 0;
        };
    }

    /**
     * type a character into the current word
     *
     */
    public TypingSession onChar(char c) {
        if (isFinished()) return this;

        if (charIndex >= currentWord.text().length()) {
            var extraLength = currentWord().extras() == null ? 0 : currentWord().extras().length();

            if (extraLength >= MAX_EXTRA_CHARS) {
                return this;
            }
            incorrectChars++;
            currentWord = currentWord.appendChar(c);
            words.set(wordIndex, currentWord);
            charIndex++;

            return this;
        }
        var state = currentWord.text().charAt(charIndex) == c ? CharState.CORRECT : CharState.INCORRECT;

        if (state == CharState.CORRECT) {
            correctChars++;
        } else {
            incorrectChars++;
        }

        currentWord.states().set(charIndex, state);
        charIndex++;
        return this;
    }

    public TypingSession onSpace() {
        if (isFinished()) return this;

        wordIndex++;
        if (mode instanceof Words(var count) && wordIndex >= count) {
            endedAt = Instant.now();
            return this;
        }

        currentWord = words.get(wordIndex);
        charIndex = 0;
        return this;
    }

    public TypingSession onBackspace() {
        if (isFinished()) return this;

        if (currentWord.hasExtras()) {
            currentWord = currentWord.removeExtraLast();
            words.set(wordIndex, currentWord);
            charIndex--;
            incorrectChars--;
            return this;
        }

        // can backspace within word
        if (charIndex > 0) {
            charIndex--;
            var state = currentWord.states().get(charIndex);
            if (state == CharState.CORRECT) correctChars--;
            if (state == CharState.INCORRECT) incorrectChars--;
            currentWord.states().set(charIndex, CharState.UNTYPED);
            return this;
        }

        if (canBackspaceToPreviousWord()) {
            wordIndex--;
            currentWord = words.get(wordIndex);
            charIndex = currentWord.text().length() - 1;
            return this;
        }

        return this;
    }

    private boolean canBackspaceToPreviousWord() {
        return (
            charIndex == 0 &&
                wordIndex > 0 &&
                !words.get(wordIndex - 1).isCorrect()
        );
    }

    public Cells snapshot() {
        var cells = IntStream.range(0, words.size() + words.size() - 1)
            .mapToObj(i -> i % 2 == 0 ?
                this.wordToCell(words.get(i / 2))
                : Stream.of(new Cell(' ', (i - 1) / 2 < wordIndex ? CharState.CORRECT : CharState.UNTYPED))
            )
            .flatMap(UnaryOperator.identity())
            .toList();
        var caret = IntStream.range(0, wordIndex)
            .map(i -> words.get(i).size())
            .sum() + wordIndex + charIndex;

        return new Cells(cells, caret);
    }

    private Stream<Cell> wordToCell(Word word) {
        var fullText = word.text() + (word.extras() == null ? "" : word.extras());
        return IntStream.range(0, fullText.length()).mapToObj(i ->
            new Cell(fullText.charAt(i), i < word.text().length() ? word.states().get(i) : CharState.EXTRA)
        );
    }
    private Duration elapsed() {
        return Duration.between(startedAt, endedAt != null ? endedAt : Instant.now());
    }

    public Stats stats() {
        return Stats.of(correctChars, incorrectChars, elapsed());
    }

    public String progress() {

        return switch(mode) {
            case Mode.Time(var limit) -> timeRemaining(limit);
            case Mode.Words(var count) -> wordIndex + "/" + count;
        };
    }

    private String timeRemaining(Duration limit) {
        var timeLeft = limit.minus(elapsed());
        return Math.max(0, timeLeft.toSeconds()) + " s";
    }
}
