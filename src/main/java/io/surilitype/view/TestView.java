package io.surilitype.view;

import static dev.tamboui.toolkit.Toolkit.*;
import static java.util.stream.Collectors.joining;

import dev.tamboui.layout.Rect;
import dev.tamboui.terminal.Frame;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.element.RenderContext;
import dev.tamboui.toolkit.element.Size;
import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import io.surilitype.typing.Cells;
import io.surilitype.typing.Stats;
import io.surilitype.typing.TypingSession;
import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.function.Consumer;

class TestView implements Element {

    private final TypingSession session;
    private final Consumer<NavEvent> nav;

    private TestView(TypingSession session, Consumer<NavEvent> nav) {
        this.session = session;
        this.nav = nav;
    }

    @Override
    public void render(Frame frame, Rect area, RenderContext context) {
        var all = new ArrayList<Element>();

        all.add(markupText("[bold yellow]surilitype[/]"));
        all.add(spacer());
        all.add(markupText("[bold yellow]" + session.progress() + "[/]"));

        all.add(markupText(cellsToMarkup(session.snapshot())).wrapWord());

        if (session.isFinished()) {
            all.add(text(formatStats(session.stats())).dim().centered());
        }

        all.add(spacer());
        all.add(text("esc - menu").dim().centered());
        all.add(spacer());
        all.add(MenuView.footer());
        column(all.toArray(Element[]::new)).render(frame, area, context);
    }

    @Override
    public Size preferredSize(
        int availableWidth,
        int availableHeight,
        RenderContext context
    ) {
        return Size.UNKNOWN;
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public String id() {
        return "test-view";
    }

    @Override
    public EventResult handleKeyEvent(KeyEvent event, boolean focused) {
        if (event.isCancel()) {
            nav.accept(new NavEvent.ShowMenu());
            return EventResult.HANDLED;
        }
        if (!focused) return EventResult.UNHANDLED;

        if (event.isDeleteBackward()) {
            session.onBackspace();
            return EventResult.HANDLED;
        }

        if (event.isChar(' ')) {
            session.onSpace();
            if (session.isFinished()) nav.accept(new NavEvent.ShowResults(session.stats()));
            return EventResult.HANDLED;
        }

        if (event.code() == KeyCode.CHAR) {
            session.onChar(event.string().charAt(0));
            if (session.isFinished()) nav.accept(new NavEvent.ShowResults(session.stats()));
            return EventResult.HANDLED;
        }

        return EventResult.UNHANDLED;
    }

    String cellsToMarkup(Cells cells) {
        return IntStream.range(0, cells.cells().size())
            .mapToObj(i -> {
                var c = cells.cells().get(i);
                var tag = switch (c.state()) {
                    case CORRECT -> "green";
                    case UNTYPED -> "dim";
                    case INCORRECT -> "red";
                    case EXTRA -> "red dim";
                };
                var caretTag = i == cells.caret() ? " reversed" : "";

                return "[" + tag + caretTag + "]" + escapeMarkup(c.c()) + "[/]";
            })
            .collect(joining());
    }

    String escapeMarkup(char c) {
        return switch (c) {
            case '[' -> "[[";
            case ']' -> "]]";
            default -> String.valueOf(c);
        };
    }

    String formatStats(Stats stats) {
        return "%d wpm · %d%% acc".formatted(
            Math.round(stats.wpm()),
            Math.round(stats.accuracy() * 100)
        );
    }

    public static TestView create(TypingSession session, Consumer<NavEvent> nav) {
        return new TestView(session, nav);
    }
}
