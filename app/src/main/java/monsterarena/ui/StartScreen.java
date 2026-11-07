package monsterarena.ui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import monsterarena.model.MapSize;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class StartScreen {

    public record InitialData(String playerName, MapSize mapSize) {}

    public static InitialData show(Screen screen) throws IOException {
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

        BasicWindow window = new BasicWindow("Monster Arena");
        window.setHints(List.of(Window.Hint.CENTERED));

        AtomicReference<MapSize> selectedMapSize = new AtomicReference<>();

        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label("Monster Arena"));

        panel.addComponent(new Label("Namen eingeben:"));
        TextBox textBox = new TextBox();
        panel.addComponent(textBox);

        panel.addComponent(new Label("Kartengröße wählen:"));
        RadioBoxList<MapSize> mapSizeList = new RadioBoxList<>();
        for (MapSize mapSize : MapSize.values()) {
            mapSizeList.addItem(mapSize);
        }
        mapSizeList.setCheckedItemIndex(1);
        panel.addComponent(mapSizeList);

        panel.addComponent(new Button("Spiel starten", () -> {
            selectedMapSize.set(mapSizeList.getCheckedItem());
            window.close();
        }));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
        String playerName = textBox.getText();

        return new InitialData(playerName, selectedMapSize.get());
    }
}
