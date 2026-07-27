package io.surilitype.typing;

public sealed interface LoadState {
    record Loading() implements LoadState {}

    record Loaded(WordList words) implements LoadState {}

    record Failed() implements LoadState {}
}
