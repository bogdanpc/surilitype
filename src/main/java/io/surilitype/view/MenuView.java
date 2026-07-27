package io.surilitype.view;

import static dev.tamboui.toolkit.Toolkit.*;

import dev.tamboui.layout.Rect;
import dev.tamboui.terminal.Frame;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.element.RenderContext;
import dev.tamboui.toolkit.element.Size;
import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.tui.event.KeyEvent;
import io.surilitype.typing.Config;

import java.util.function.Consumer;

public class MenuView implements Element {

    private Config config;
    private final Consumer<NavEvent> nav;

    int selected;

    private MenuView(Config config, Consumer<NavEvent> nav) {
        this.config = config;
        this.nav = nav;
    }

    @Override
    public void render(Frame frame, Rect area, RenderContext context) {
        build().render(frame, area, context);
    }

    @Override
    public String id() {
        return "menu-view";
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
        if (event.isCharIgnoreCase('q') || event.isCancel()) {
            nav.accept(new NavEvent.Quit());
            return EventResult.HANDLED;
        }

        if (event.isConfirm()) {
            handleEnter();
            return EventResult.HANDLED;
        }

        if (event.isLeft() || event.isFocusPrevious()) {
            moveCursor(-1);
            return EventResult.HANDLED;
        }
        if (event.isRight() || event.isFocusNext()) {
            moveCursor(1);
            return EventResult.HANDLED;
        }

        return EventResult.UNHANDLED;
    }

    private void handleEnter() {
        if (selected == 2) {
            config = config.withMode(Config.defaultModeWords());
            nav.accept(new NavEvent.StartTest(config));
        } else if (selected == 3) {
            config = config.withMode(Config.defaultModeTime());
            nav.accept(new NavEvent.StartTest(config));
        }
    }

    public static MenuView create(Config current, Consumer<NavEvent> nav) {
        return new MenuView(current, nav);
    }

    private String menuBar() {
        var punctuation = itemMarkupCheckbox(
            "@ punctuation",
            config.hasPunctuation(),
            selected == 0
        );
        var numbers = itemMarkupCheckbox(
            "# numbers",
            config.hasNumbers(),
            selected == 1
        );
        var words = itemMarkup("words", config.isWords(), selected == 2);
        var time = itemMarkup("time", config.isTime(), selected == 3);

        return (punctuation + "   " + numbers + "   [dim]|[/]   " + words + "   " + time);
    }

    private void moveCursor(int direction) {
        selected = Math.floorMod(selected + direction, 4);
    }

    private String itemMarkup(String label, boolean selected, boolean active) {
        var tag = selected ? "yellow" : "dim";
        tag += active ? " underlined" : "";
        return "[%s]%s[/]".formatted(tag, label);
    }

    private String itemMarkupCheckbox(
        String label,
        boolean selected,
        boolean active
    ) {
        var tag = selected ? "yellow" : "dim";
        tag += active ? " underlined" : "";
        return "(%s)[%s]%s[/]".formatted(selected ? "x" : "", tag, label);
    }

    private Element build() {
        return column(
            spacer(),
            markupText("[bold yellow]surilitype[/]").centered(),
            text(""),
            text(""),
            markupText(menuBar()).centered(),
            text(""),
            text("Enter to select · ← → navigate · Q to quit").dim().centered(),
            spacer(),
            footer()
        );
    }

    public static Element footer() {
        return row(
            markupText(
                "[dim]tamboui[/]  [blue underlined]https://tamboui.dev[/]"
            ),
            spacer(),
            markupText(
                "[dim]Monkeytype[/]  [blue underlined]https://monkeytype.com[/]"
            )
        );
    }
}
