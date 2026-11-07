package monsterarena.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;
import monsterarena.HighScore;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class EndScreen {

    public static boolean show(Screen screen, SimpleTheme theme, GridLayout layout, HighScore highScore) {
        AtomicBoolean wantToRestart = new AtomicBoolean(false);
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
        gui.setTheme(theme);

        BasicWindow window = new BasicWindow("Monster Arena");
        window.setHints(List.of(Window.Hint.CENTERED));

        Panel panel = new Panel();
        panel.setLayoutManager(layout);

        panel.addComponent(new Button("Neues Spiel", () -> {
            wantToRestart.set(true);
            window.close();
        }));
        panel.addComponent(new Button("Spiel beenden", window::close));

        panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

        Table<String> highScoreTable = new Table<>("Spieler", "Kills", "Spielfeld");
        for (HighScore.HighScoreEntry entry : highScore.getEntries()) {
            highScoreTable.getTableModel().addRow(entry.playerName(), String.valueOf(entry.kills()), entry.mapSize());
        }
        panel.addComponent(highScoreTable);

        window.setComponent(panel);
        gui.addWindowAndWait(window);
        return wantToRestart.get();
    }
}
