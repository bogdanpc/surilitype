package io.surilitype.view;

import io.surilitype.typing.Config;
import io.surilitype.typing.Stats;

public sealed interface NavEvent {
    record StartTest(Config config) implements NavEvent {}

    record ShowMenu() implements NavEvent {}

    record ShowAbout() implements NavEvent {}

    record ShowResults(Stats stats) implements NavEvent{}

    record Quit() implements NavEvent {}
}
