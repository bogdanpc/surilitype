package io.surilitype.view;

import dev.tamboui.toolkit.app.ToolkitApp;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.elements.SpinnerElement;
import io.surilitype.history.Result;
import io.surilitype.typing.*;

import java.time.Instant;

import static dev.tamboui.toolkit.Toolkit.column;
import static dev.tamboui.toolkit.Toolkit.text;

public final class App extends ToolkitApp {

    private DictionaryLoader loader;
    private final Config config;
    private Element activeView;
    private Element menuView;
    private WordList wordList;

    public App(Config config) {
        this.config = config;
    }

    @Override
    protected void onStart() {
        super.onStart();
        loader = DictionaryLoader.start();
    }

    @Override
    protected Element render() {
        return switch (loader.state()) {
            case LoadState.Loading _ -> new SpinnerElement("Loading");
            case LoadState.Loaded(var words) -> column(loaded(words));
            case LoadState.Failed _ -> text("failed to load dictionary");
        };
    }

    Element loaded(WordList words) {
        this.wordList = words;
        if (activeView == null) {
            menuView = MenuView.create(config, this::onNav);
            activeView = menuView;
        }
        return activeView;
    }

    private void onNav(NavEvent e) {
        switch (e) {
            case NavEvent.StartTest(var cfg) -> startTest(cfg);
            case NavEvent.ShowAbout _ -> showAbout();
            case NavEvent.ShowMenu _ -> showMenu();
            case NavEvent.ShowResults(var stats) -> showResults(stats);
            case NavEvent.Quit _ -> quit();
        }
    }

    private void startTest(Config config) {
        var sampleCount = switch (config.mode()) {
            case Mode.Words w -> w.count();
            case Mode.Time _ -> 30;
        };

        var typingSession = TypingSession.of(
            config.mode(),
            wordList.sample(sampleCount)
        );
        activeView = TestView.create(typingSession, this::onNav);
    }

    private void showMenu() {
        activeView = menuView;
    }

    private void showAbout() {
        activeView = AboutView.create(this::onNav);
    }

    private void showResults(Stats stats) {
        var result = Result.from(stats, config, Instant.now());
        activeView = ResultsView.create(result, this::onNav);

    }
}
