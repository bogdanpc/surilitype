package io.surilitype.typing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public record WordList(List<String> words) {
    public List<String> sample(int n) {
        var r = new Random();
        return r.ints(n, 0, words().size()).mapToObj(words::get).toList();
    }

    public static WordList loadWords() throws IOException {
        try (
            var in = WordList.class.getResourceAsStream("/words/english-1k.txt")
        ) {
            var reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8)
            );
            return new WordList(reader.readAllLines());
        }
    }
}
