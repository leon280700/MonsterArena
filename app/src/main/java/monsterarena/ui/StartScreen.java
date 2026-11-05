package monsterarena.ui;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.RadioBoxList;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.screen.Screen;
import monsterarena.model.MapSize;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class StartScreen {

    public static MapSize show(Screen screen) throws IOException {
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

        BasicWindow window = new BasicWindow("TUIGame");
        window.setHints(List.of(Window.Hint.CENTERED));

        AtomicReference<MapSize> selectedMapSize = new AtomicReference<>();

        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label("TUIGame"));
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

        return selectedMapSize.get();
    }
}
