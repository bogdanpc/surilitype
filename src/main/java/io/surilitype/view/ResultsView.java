package io.surilitype.view;

import static dev.tamboui.toolkit.Toolkit.column;
import static dev.tamboui.toolkit.Toolkit.markupText;
import static dev.tamboui.toolkit.Toolkit.spacer;
import static dev.tamboui.toolkit.Toolkit.text;

import dev.tamboui.layout.Rect;
import dev.tamboui.terminal.Frame;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.element.RenderContext;
import dev.tamboui.toolkit.element.Size;
import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.tui.event.KeyEvent;
import io.surilitype.history.Result;
import io.surilitype.typing.Mode;
import java.util.function.Consumer;

class ResultsView implements Element {

    private final Consumer<NavEvent> nav;
    private final Result result;

    private ResultsView(Result result, Consumer<NavEvent> nav) {
        this.result = result;
        this.nav = nav;
    }

    @Override
    public String id() {
        return "results-view";
    }

    @Override
    public void render(Frame frame, Rect area, RenderContext context) {
        column(
            markupText("[bold yellow]surilitype[/]"),
            spacer(),
            markupText(
                "[bold]%d[/] wpm".formatted(Math.round(result.wpm()))
            ).centered(),
            markupText(
                "%d%% acc".formatted(Math.round(result.accuracy() * 100))
            ).centered(),
            markupText(modeLabel()).dim().centered(),
            spacer(),
            text("enter · esc → menu").dim().centered(),
            MenuView.footer()
        ).render(frame, area, context);
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
    public EventResult handleKeyEvent(KeyEvent event, boolean focused) {
        if (event.isConfirm() || event.isCancel()) {
            nav.accept(new NavEvent.ShowMenu());
            return EventResult.HANDLED;
        }

        return EventResult.UNHANDLED;
    }

    public static ResultsView create(Result result, Consumer<NavEvent> nav) {
        return new ResultsView(result, nav);
    }

    private String modeLabel() {
        return switch (result.mode()) {
            case Mode.Words w -> "words " + w.count();
            case Mode.Time t -> "time " + t.limit().toSeconds() + "s";
        };
    }
}
