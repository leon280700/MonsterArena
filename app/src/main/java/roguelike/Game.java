package roguelike;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;

public class Game {

    Player player;

    Screen screen;

    public Game() {
        this.player = new Player();
        try {
            this.screen = new DefaultTerminalFactory().createScreen();
            screen.startScreen();
            screen.setCursorPosition(null);
            TextGraphics g = screen.newTextGraphics();
            setGameTitle(g);
            g.putString(player.getPosition()[0], player.getPosition()[1], player.getMODEL());
            screen.refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void render() throws IOException {
        screen.clear();
        TextGraphics g = screen.newTextGraphics();
        setGameTitle(g);
        g.putString(player.getPosition()[0], player.getPosition()[1], player.getMODEL());
        screen.refresh();
    }


    public void startGameLoop(){
        try {
        while (true) {
            KeyStroke key = screen.readInput();
            if (listenForExitInputs(key)) break;

            listenForMoveInputs(key);
        }

        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private static boolean listenForExitInputs(KeyStroke key) {
        return key.getKeyType() == KeyType.Escape || key.getKeyType() == KeyType.EOF
                || (key.getKeyType() == KeyType.Character && key.getCharacter() == 'q');
    }

    private void listenForMoveInputs(KeyStroke key) throws IOException {
        if (key.getKeyType() == KeyType.ArrowRight) {
            player.moveRight();
            render();
        }

        if (key.getKeyType() == KeyType.ArrowLeft) {
            player.moveLeft();
            render();
        }

        if (key.getKeyType() == KeyType.ArrowUp) {
            player.moveUp();
            render();
        }

        if (key.getKeyType() == KeyType.ArrowDown) {
            player.moveDown();
            render();
        }
    }

    private void setGameTitle(TextGraphics g) {
        g.setForegroundColor(TextColor.ANSI.YELLOW);
        g.putString(2, 1, "TUIGame - Lanterna laeuft. Druecke 'q' oder ESC zum Beenden.");
        g.setForegroundColor(TextColor.ANSI.WHITE);
    }
}
