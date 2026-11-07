package monsterarena.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import monsterarena.model.MapSize;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class StartScreen {

    public record InitialData(String playerName, MapSize mapSize, boolean closeGame) {}

    public static InitialData show(Screen screen, SimpleTheme theme, GridLayout layout) {
        AtomicBoolean closeGame = new AtomicBoolean(false);
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
        gui.setTheme(theme);

        BasicWindow window = new BasicWindow("Monster Arena");
        window.setHints(List.of(Window.Hint.CENTERED));

        AtomicReference<MapSize> selectedMapSize = new AtomicReference<>();

        Panel panel = new Panel();
        panel.setLayoutManager(layout);

        panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        panel.addComponent(new Label("Namen eingeben:"));
        TextBox textBox = new TextBox();
        panel.addComponent(textBox);
        panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

        panel.addComponent(new Label("Kartengröße wählen:"));
        RadioBoxList<MapSize> mapSizeList = new RadioBoxList<>();
        for (MapSize mapSize : MapSize.values()) {
            mapSizeList.addItem(mapSize);
        }
        mapSizeList.setCheckedItemIndex(1);
        panel.addComponent(mapSizeList);
        panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

        panel.addComponent(new Button("Spiel starten", () -> {
            selectedMapSize.set(mapSizeList.getCheckedItem());
            window.close();
        }));
        panel.addComponent(new Button("Spiel beenden", () -> {
            closeGame.set(true);
            window.close();
        }));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
        String playerName = textBox.getText();

        return new InitialData(playerName, selectedMapSize.get(), closeGame.get());
    }
}
