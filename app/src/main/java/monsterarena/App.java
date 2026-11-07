package monsterarena;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import monsterarena.model.GameSettings;
import monsterarena.ui.EndScreen;
import monsterarena.ui.Layout;
import monsterarena.ui.StartScreen;
import monsterarena.ui.Theme;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        Screen screen = new DefaultTerminalFactory().createScreen();
        Theme theme = new Theme();
        Layout layout = new Layout();
        boolean restart;

        screen.startScreen();

        do {
        GameSettings gameSettings = StartScreen.show(screen, theme.getTheme(), layout.getLayout());
        if (gameSettings.mapSize() == null || gameSettings.playerName() == null || gameSettings.closeGame()) {
            screen.stopScreen();
            return;
        }

        Game game = new Game(screen, gameSettings);
        HighScore highScore = game.startGameLoop();

        restart = EndScreen.show(screen, theme.getTheme(), layout.getLayout(), highScore);
        } while(restart);
    }
}
