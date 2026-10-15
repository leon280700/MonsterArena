package monsterarena;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import monsterarena.model.Direction;
import monsterarena.model.Enemy;
import monsterarena.model.Player;
import monsterarena.model.Position;

import java.io.IOException;

public class Game {

    Player player;

    Enemy enemy;

    Screen screen;

    public Game() {
        this.player = new Player();
        this.enemy = new Enemy();
        try {
            this.screen = new DefaultTerminalFactory().createScreen();
            screen.startScreen();
            screen.setCursorPosition(null);
            TextGraphics g = screen.newTextGraphics();
            setGameTitle(g);
            drawSprite(g, player.getPosition(), player.getSprite());
            drawSprite(g, enemy.getPosition(), enemy.getSprite());

            screen.refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void render() throws IOException {
        screen.clear();
        TextGraphics g = screen.newTextGraphics();
        setGameTitle(g);
        drawSprite(g, player.getPosition(), player.getSprite());
        drawSprite(g, enemy.getPosition(), enemy.getSprite());

        screen.refresh();
    }

    private void drawSprite(TextGraphics g, Position position, String[] sprite) {
        for (int row = 0; row < sprite.length; row++) {
            g.putString(position.x(), position.y() + row, sprite[row]);
        }
    }


    public void startGameLoop(){
        try {
            long lastEnemyMove = System.currentTimeMillis();
        while (true) {
            KeyStroke key = screen.pollInput();

            boolean gameOver = false;

            if (key != null) {
                if (listenForExitInputs(key)) break;

                Position playerBefore = player.getPosition();
                listenForMoveInputs(key);
                gameOver = pathCrossed(playerBefore, player.getPosition(), enemy.getPosition());
            }

            if (!gameOver && System.currentTimeMillis() - lastEnemyMove >= 1000) {
                Position enemyBefore = enemy.getPosition();
                moveEnemyTowardsPlayer();
                gameOver = pathCrossed(enemyBefore, enemy.getPosition(), player.getPosition());
                lastEnemyMove = System.currentTimeMillis();
            }

            if (gameOver) {
                break;
            }

            render();
            Thread.sleep(16);
        }
            endScreen();
            waitForExit();

        } catch (IOException | InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    private void waitForExit() throws IOException {
        while (true) {
            KeyStroke key = screen.readInput();
            if (listenForExitInputs(key)) break;
        }
    }

    private void endScreen() {
        screen.clear();
        TextGraphics g = screen.newTextGraphics();
        setEndScreenTitle(g);
    }

    private void moveEnemyTowardsPlayer() {
        Position playerPos = this.player.getPosition();
        Position enemyPos = this.enemy.getPosition();

        int distanceColumn = playerPos.x() - enemyPos.x();
        int distanceRow = playerPos.y() - enemyPos.y();

        if (Math.abs(distanceColumn) >= Math.abs(distanceRow)) {
            if (distanceColumn < 0)
                enemy.move(Direction.LEFT);
            else
                enemy.move(Direction.RIGHT);
        } else {
            if (distanceRow < 0)
                enemy.move(Direction.UP);
            else
                enemy.move(Direction.DOWN);
        }
    }

    private boolean pathCrossed(Position from, Position to, Position other) {
        if (to.equals(other)) {
            return true;
        }

        if (from.y() == to.y() && other.y() == from.y()) {
            int lo = Math.min(from.x(), to.x());
            int hi = Math.max(from.x(), to.x());
            return other.x() >= lo && other.x() <= hi;
        }

        if (from.x() == to.x() && other.x() == from.x()) {
            int lo = Math.min(from.y(), to.y());
            int hi = Math.max(from.y(), to.y());
            return other.y() >= lo && other.y() <= hi;
        }

        return false;
    }

    private static boolean listenForExitInputs(KeyStroke key) {
        return key.getKeyType() == KeyType.Escape || key.getKeyType() == KeyType.EOF
                || (key.getKeyType() == KeyType.Character && key.getCharacter() == 'q');
    }

    private void listenForMoveInputs(KeyStroke key) throws IOException {
        if (key.getKeyType() == KeyType.ArrowRight) {
            player.move(Direction.RIGHT);
        }

        if (key.getKeyType() == KeyType.ArrowLeft) {
            player.move(Direction.LEFT);
        }

        if (key.getKeyType() == KeyType.ArrowUp) {
            player.move(Direction.UP);
        }

        if (key.getKeyType() == KeyType.ArrowDown) {
            player.move(Direction.DOWN);
        }
    }

    private void setGameTitle(TextGraphics g) {
        g.setForegroundColor(TextColor.ANSI.YELLOW);
        g.putString(2, 1, "TUIGame – Druecke 'q' oder ESC zum Beenden.");
        g.setForegroundColor(TextColor.ANSI.WHITE);
    }
    private void setEndScreenTitle(TextGraphics g) {
        g.setForegroundColor(TextColor.ANSI.YELLOW);
        g.putString(2, 1, "GAME OVER – Druecke 'q' oder ESC zum Beenden.");
        try {
            screen.refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
