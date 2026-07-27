# Surilitype

Java terminal typing test [monkeytype](https://github.com/monkeytypegame/monkeytype) style app that runs in your
console.

Built on the [tamboui](https://tamboui.dev) TUI library.

## Features

- Words mode (25 words) and time mode (30 seconds)
- Random words sampled from a bundled 1k English dictionary
- Live progress and per-character feedback while typing
- WPM and accuracy on a result screen after each test

## Requirements

- JDK 25+
- Maven 3.9+

## Controls

| Screen | Keys                                                  |
|--------|-------------------------------------------------------|
| Menu   | `←` `→` move, `Enter` start test, `q` or `Esc` quit   |
| Test   | type to play, `Backspace` correct, `Esc` back to menu |
| Result | `Enter` or `Esc` back to menu                         |

## Compile and run

```sh
mvn compile exec:java
```

## Run rests

```sh
mvn test
```

## Status

Early work in progress. Known gaps:

- The punctuation and numbers toggles are drawn in the menu but do nothing yet.
- `config.properties` is parsed on startup, but the values do not change the test that runs.

## License

MIT — see [LICENSE](LICENSE).
