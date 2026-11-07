package monsterarena;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import monsterarena.ui.StartScreen;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();

        StartScreen.InitialData initialData = StartScreen.show(screen);
        if (initialData.mapSize() == null || initialData.playerName() == null) {
            screen.stopScreen();
            return;
        }

        Game game = new Game(screen, initialData);
        game.startGameLoop();
    }
}
