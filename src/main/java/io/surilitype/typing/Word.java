package io.surilitype.typing;

import java.util.ArrayList;
import java.util.List;

record Word(String text, List<CharState> states, String extras) {
    public static Word of(String text) {
        var states = new ArrayList<CharState>(text.length());

        for (int i = 0; i < text.length(); i++) {
            states.add(CharState.UNTYPED);
        }

        return new Word(text, states, null);
    }

    Word appendChar(char c) {
        return new Word(text, states, extras == null ? "" + c : extras + c);
    }

    boolean hasExtras() {
        return extras != null && !extras.isEmpty();
    }

    Word removeExtraLast() {
        return new Word(text, states, extras.substring(0, extras.length() - 1));
    }

    boolean isCorrect() {
        return states().stream().allMatch(c -> c == CharState.CORRECT);
    }

    int size() {
        return text.length() + (extras == null ? 0 : extras.length());
    }
}
