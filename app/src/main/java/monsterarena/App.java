package monsterarena;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import monsterarena.model.MapSize;
import monsterarena.ui.StartScreen;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();

        MapSize mapSize = StartScreen.show(screen);
        if (mapSize == null) {
            screen.stopScreen();
            return;
        }

        Game game = new Game(screen, mapSize);
        game.startGameLoop();
    }
}
