package roguelike;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.util.Arrays;

public class Game {

    Player player;

    Enemy enemy;

    Screen screen;

    private long lastEnemyMove;

    public Game() {
        this.player = new Player();
        this.enemy = new Enemy();
        try {
            this.screen = new DefaultTerminalFactory().createScreen();
            screen.startScreen();
            screen.setCursorPosition(null);
            TextGraphics g = screen.newTextGraphics();
            setGameTitle(g);
            g.putString(player.getPosition()[0], player.getPosition()[1], player.getMODEL());
            g.putString(enemy.getPosition()[0], enemy.getPosition()[1], enemy.getMODEL());

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
        g.putString(enemy.getPosition()[0], enemy.getPosition()[1], enemy.getMODEL());

        screen.refresh();
    }


    public void startGameLoop(){
        try {
        lastEnemyMove = System.currentTimeMillis();
        while (true) {
            KeyStroke key = screen.pollInput();

            boolean gameOver = false;

            if (key != null) {
                if (listenForExitInputs(key)) break;

                int[] playerBefore = player.getPosition().clone();
                listenForMoveInputs(key);
                gameOver = pathCrossed(playerBefore, player.getPosition(), enemy.getPosition());
            }

            if (!gameOver && System.currentTimeMillis() - lastEnemyMove >= 1000) {
                int[] enemyBefore = enemy.getPosition().clone();
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
        int[] playerPos = this.player.getPosition();
        int[] enemyPos = this.enemy.getPosition();

        int distanceColumn = playerPos[0] - enemyPos[0];
        int distanceRow = playerPos[1] - enemyPos[1];

        if (Math.abs(distanceColumn) >= Math.abs(distanceRow)) {
            if (distanceColumn < 0)
                enemy.moveLeft();
            else
                enemy.moveRight();
        } else {
            if (distanceRow < 0)
                enemy.moveUp();
            else
                enemy.moveDown();
        }
    }

    private boolean pathCrossed(int[] from, int[] to, int[] other) {
        if (Arrays.equals(to, other)) {
            return true;
        }

        if (from[1] == to[1] && other[1] == from[1]) {
            int lo = Math.min(from[0], to[0]);
            int hi = Math.max(from[0], to[0]);
            return other[0] >= lo && other[0] <= hi;
        }

        if (from[0] == to[0] && other[0] == from[0]) {
            int lo = Math.min(from[1], to[1]);
            int hi = Math.max(from[1], to[1]);
            return other[1] >= lo && other[1] <= hi;
        }

        return false;
    }

    private static boolean listenForExitInputs(KeyStroke key) {
        return key.getKeyType() == KeyType.Escape || key.getKeyType() == KeyType.EOF
                || (key.getKeyType() == KeyType.Character && key.getCharacter() == 'q');
    }

    private void listenForMoveInputs(KeyStroke key) throws IOException {
        if (key.getKeyType() == KeyType.ArrowRight) {
            player.moveRight();
        }

        if (key.getKeyType() == KeyType.ArrowLeft) {
            player.moveLeft();
        }

        if (key.getKeyType() == KeyType.ArrowUp) {
            player.moveUp();
        }

        if (key.getKeyType() == KeyType.ArrowDown) {
            player.moveDown();
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
