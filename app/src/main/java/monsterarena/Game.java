package monsterarena;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import monsterarena.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Game {

    Player player;

    Map map;

    ArrayList<Enemy> enemies = new ArrayList<>();

    ArrayList<Shot> shots = new ArrayList<>();

    Screen screen;

    int kills;

    boolean won = false;

    public Game() {
        this.player = new Player();
        this.map = new Map();

        enemies.add(new Enemy(new Position(15, 15)));
        enemies.add(new Enemy(new Position(30, 30)));

        try {
            this.screen = new DefaultTerminalFactory().createScreen();
            screen.startScreen();
            screen.setCursorPosition(null);
            TextGraphics g = screen.newTextGraphics();
            setGameTitle(g);
            drawSprite(g, player.getPosition(), player.getSprite());

            drawMapSprite(g);

            for (Enemy enemy : enemies) {
                drawSprite(g, enemy.getPosition(), enemy.getSprite());
            }

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

        drawMapSprite(g);

        for (Enemy enemy : enemies) {
            drawSprite(g, enemy.getPosition(), enemy.getSprite());
        }
        for (Shot shot : shots) {
            drawSprite(g, shot.getPosition(), shot.getSprite());
        }
        screen.refresh();
    }

    private void drawSprite(TextGraphics g, Position position, String[] sprite) {
        for (int row = 0; row < sprite.length; row++) {
            g.putString(position.x(), position.y() + row, sprite[row]);
        }
    }

    private void drawMapSprite(TextGraphics g) {
        int mapXStart = map.getStart().x();
        int mapXEnd = map.getEnd().x();
        for (int x = mapXStart; x <= mapXEnd; x++){
            drawSprite(g, new Position(x, map.getStart().y()), map.getRowSprite());
            drawSprite(g, new Position(x, map.getEnd().y()), map.getRowSprite());
        }

        int mapYStart = map.getStart().y();
        int mapYEnd = map.getEnd().y();
        for (int y = mapYStart; y <= mapYEnd; y++){
            drawSprite(g, new Position(map.getStart().x(), y), map.getColumnSprite());
            drawSprite(g, new Position(map.getEnd().x(), y), map.getColumnSprite());
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
                for (Enemy enemy : enemies) {
                    if (pathCrossed(playerBefore, player.getPosition(), enemy.getPosition())) {
                        gameOver = true;
                    }
                }
                listenForAttackInputs(key);
            }

            if (!gameOver && System.currentTimeMillis() - lastEnemyMove >= 1000) {
                for (Enemy enemy : enemies) {
                    Position enemyBefore = enemy.getPosition();
                    moveEnemyTowardsPlayer(enemy);
                    if (pathCrossed(enemyBefore, enemy.getPosition(), player.getPosition())) {
                        gameOver = true;
                    }
                }
                lastEnemyMove = System.currentTimeMillis();
            }

            if (gameOver) {
                break;
            }

            shots.forEach(Shot::advance);
            shots.removeIf(Shot::isExhausted);
            resolveShotHits();

            if (enemies.isEmpty()) {
                won = true;
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

    private void resolveShotHits() {
        Iterator<Shot> shotIterator = shots.iterator();
        while (shotIterator.hasNext()) {
            Shot shot = shotIterator.next();
            Enemy hitEnemy = enemies.stream()
                    .filter(enemy -> enemy.getPosition().equals(shot.getPosition()))
                    .findFirst()
                    .orElse(null);
            if (hitEnemy != null) {
                enemies.remove(hitEnemy);
                shotIterator.remove();
                kills++;
            }
        }
    }

    private void listenForAttackInputs(KeyStroke key) {
        if (key.getKeyType() == KeyType.Tab) {
            Shot shot = player.attack();
            shots.add(shot);
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

    private void moveEnemyTowardsPlayer(Enemy enemy) {
        Position playerPos = this.player.getPosition();
        Position enemyPos = enemy.getPosition();

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
            walkOrDeny(Direction.RIGHT);
        }

        if (key.getKeyType() == KeyType.ArrowLeft) {
            walkOrDeny(Direction.LEFT);
        }

        if (key.getKeyType() == KeyType.ArrowUp) {
            walkOrDeny(Direction.UP);
        }

        if (key.getKeyType() == KeyType.ArrowDown) {
            walkOrDeny(Direction.DOWN);
        }
    }

    private void walkOrDeny(Direction direction){
        if (isWalkable(direction)){
            player.move(direction);
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
        g.putString(2, 2, "Kills: " + String.valueOf(kills));
        try {
            screen.refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isWalkable(Direction direction){
        Position prevPlayerPos = player.getPosition();

        player.move(direction);
        Position nextPlayerPos = player.getPosition();
        player.setPosition(prevPlayerPos);

        return map.isPositionInside(nextPlayerPos);
    }
}
