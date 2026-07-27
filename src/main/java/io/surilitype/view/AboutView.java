package io.surilitype.view;

import dev.tamboui.layout.Rect;
import dev.tamboui.terminal.Frame;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.element.RenderContext;
import dev.tamboui.toolkit.element.Size;
import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.tui.event.KeyEvent;

import java.util.function.Consumer;

import static dev.tamboui.toolkit.Toolkit.column;
import static dev.tamboui.toolkit.Toolkit.markupText;
import static dev.tamboui.toolkit.Toolkit.text;


public class AboutView implements Element {
    private final Consumer<NavEvent> nav;

    private AboutView(Consumer<NavEvent> nav) {
        this.nav = nav;
    }

    @Override
    public void render(Frame frame, Rect area, RenderContext context) {
        column(
            text(""),
            markupText("""
                [bold yellow]surilitype[/]
                A terminal port of Monkeytype, built in Java with tamboui.
                  [dim]Monkeytype[/]   https://monkeytype.com
                  [dim]tamboui[/]      https://tamboui.dev
                """),
            text("M menu · Q quit").dim().centered()
        ).render(frame, area, context);
    }

    @Override
    public String id() {
        return "about-view";
    }

    @Override
    public Size preferredSize(int availableWidth, int availableHeight, RenderContext context) {
        return Size.UNKNOWN;
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public EventResult handleKeyEvent(KeyEvent event, boolean focused) {
        if (event.isCancel()) {
            nav.accept(new NavEvent.ShowMenu());
            return EventResult.HANDLED;
        }

        return EventResult.UNHANDLED;
    }

    public static AboutView create(Consumer<NavEvent> nav) {
        return new AboutView(nav);
    }
}
