package io.surilitype.typing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public record Config(Mode mode, EnumSet<WordModifier> modifiers) {

    public static Config of(Properties properties) {
        var modifiers = modifiersFromProps(properties);

        try {
            var mode = modeFromProps(properties);
            return new Config(mode, modifiers);
        } catch (IllegalArgumentException e) {
            return new Config(defaultModeWords(), modifiers);
        }
    }

    public static Config load() {

        try (var fin = Files.newInputStream(Path.of("config.properties"))) {
            var props = new Properties();

            props.load(fin);
            return of(props);
        } catch (IOException e) {
            return defaults();
        }
    }

    private static Mode modeFromProps(Properties props) {
        return switch (props.getProperty("mode", "words")) {
            case "words" -> new Mode.Words(Integer.parseInt(props.getProperty("value", "25")));
            case "time" -> new Mode.Time(Duration.ofSeconds((Integer.parseInt(props.getProperty("value", "30")))));
            default -> throw new IllegalArgumentException("unknown mode type");
        };
    }

    private static EnumSet<WordModifier> modifiersFromProps(Properties props) {
        try {
            return Arrays.stream(props.getProperty("modifiers", "").split(","))
                .filter(not(String::isBlank))
                .map(name -> WordModifier.valueOf(name.toUpperCase()))
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(WordModifier.class)));
        } catch (IllegalArgumentException e) {
            return EnumSet.noneOf(WordModifier.class);
        }
    }


    public static Mode.Words defaultModeWords() {
        return new Mode.Words(25);
    }

    public static Mode.Time defaultModeTime() {
        return new Mode.Time(Duration.ofSeconds(30));
    }

    private static Config defaults() {
        return new Config(defaultModeWords(), EnumSet.noneOf(WordModifier.class));
    }

    public boolean hasPunctuation() {
        return modifiers.contains(WordModifier.PUNCTUATION);
    }

    public boolean hasNumbers() {
        return modifiers.contains(WordModifier.NUMBERS);
    }

    public boolean isWords() {
        return mode instanceof Mode.Words;
    }

    public boolean isTime() {
        return mode instanceof Mode.Time;
    }

    public Config withMode(Mode mode) {
        return new Config(mode, this.modifiers);
    }

}
