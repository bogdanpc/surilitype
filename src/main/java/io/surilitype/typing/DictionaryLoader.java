package io.surilitype.typing;

import java.io.IOException;

public class DictionaryLoader {

    private volatile LoadState state = new LoadState.Loading();

    public LoadState state() {
        return state;
    }

    public static DictionaryLoader start() {
        var loader = new DictionaryLoader();
        Thread.ofVirtual().start(() -> {
            try {
                var words = WordList.loadWords();
                loader.state = new LoadState.Loaded(words);
            } catch (IOException e) {
                loader.state = new LoadState.Failed();
            }
        });
        return loader;
    }
}
