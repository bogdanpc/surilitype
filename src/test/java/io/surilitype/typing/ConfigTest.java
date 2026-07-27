package io.surilitype.typing;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigTest {

    @Test
    void noConfigFile() {
        var config = Config.load();
        assertEquals(new Mode.Words(25), config.mode());
    }

    @Test
    void garbageDecodesToDefault() {
        var p = new Properties();
        p.setProperty("mode", "xyz");
        p.setProperty("value", "banana");
        assertEquals(new Mode.Words(25), Config.of(p).mode());   // no throw
    }

    @Test
    void unknownModifierDropsAll() {
        var p = new Properties();
        p.setProperty("modifiers", "punctuation,bogus");
        assertEquals(EnumSet.noneOf(WordModifier.class), Config.of(p).modifiers());
    }

    @Test
    void knownModifiersParsed() {
        var p = new Properties();
        p.setProperty("modifiers", "punctuation,numbers");
        assertEquals(EnumSet.allOf(WordModifier.class), Config.of(p).modifiers());
    }
}
