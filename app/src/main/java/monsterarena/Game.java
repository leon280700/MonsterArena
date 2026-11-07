package monsterarena;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.TerminalSize;
import monsterarena.model.*;
import monsterarena.ui.StartScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Game {

    Player player;

    Map map;

    ArrayList<Enemy> enemies = new ArrayList<>();

    ArrayList<Shot> shots = new ArrayList<>();

    HighScore highScore;

    Screen screen;

    int level = 1;

    int kills;

    public Game(Screen screen, StartScreen.InitialData initialData) throws IOException {
        this.map = new Map(initialData.mapSize());
        this.player = new Player(map.getCentre(), initialData.playerName());
        this.highScore = new HighScore();
        this.screen = screen;

        try {
            screen.startScreen();
            screen.setCursorPosition(null);
           render();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void render() throws IOException {
        screen.doResizeIfNecessary();
        screen.clear();
        TextGraphics g = screen.newTextGraphics();
        Offset offset = computeOffset(screen.getTerminalSize());

        setGameTitle(g);
        drawSprite(g, player.getPosition(), player.getSprite(), offset);

        drawMapSprite(g, offset);

        for (Enemy enemy : enemies) {
            drawSprite(g, enemy.getPosition(), enemy.getSprite(), offset);
        }
        for (Shot shot : shots) {
            drawSprite(g, shot.getPosition(), shot.getSprite(), offset);
        }
        screen.refresh();
    }

    private void drawSprite(TextGraphics g, Position position, String[] sprite, Offset offset) {
        for (int row = 0; row < sprite.length; row++) {
            g.putString(position.x() + offset.x(), position.y() + row + offset.y(), sprite[row]);
        }
    }

    private void drawMapSprite(TextGraphics g, Offset offset) {
        int mapXStart = map.getStart().x();
        int mapXEnd = map.getEnd().x();
        for (int x = mapXStart; x <= mapXEnd; x++){
            drawSprite(g, new Position(x, map.getStart().y()), map.getRowSprite(), offset);
            drawSprite(g, new Position(x, map.getEnd().y()), map.getRowSprite(), offset);
        }

        int mapYStart = map.getStart().y();
        int mapYEnd = map.getEnd().y();
        for (int y = mapYStart; y <= mapYEnd; y++){
            drawSprite(g, new Position(map.getStart().x(), y), map.getColumnSprite(), offset);
            drawSprite(g, new Position(map.getEnd().x(), y), map.getColumnSprite(), offset);
        }
    }


    public HighScore startGameLoop(){
        try {
            long lastEnemyMove = System.currentTimeMillis();
            int spawnedLevel = -1;

        while (true) {
            if (spawnedLevel != level) {
                spawnEnemies();
                spawnedLevel = level;
            }
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

            if (!gameOver && System.currentTimeMillis() - lastEnemyMove >= 500) {
                for (Enemy enemy : enemies) {
                    Position enemyBefore = enemy.getPosition();
                    moveEnemyTowardsPlayer(enemy);
                    if (pathCrossed(enemyBefore, enemy.getPosition(), player.getPosition())) {
                        gameOver = true;
                    }
                }
                lastEnemyMove = System.currentTimeMillis();
            }

            if (gameOver) break;


            shots.forEach(Shot::advance);
            shots.removeIf(Shot::isExhausted);
            resolveShotHits();

            if (enemies.isEmpty()) {
                level++;
            }

            render();
            Thread.sleep(16);
        }

        } catch (IOException | InterruptedException e){
            throw new RuntimeException(e);
        }
        highScore.writeToHighScore(player.getPlayerName(), kills, map.getMapSize().getText());

        return highScore;
    }

    private void spawnEnemies() {
        for (int i = 1; i <= level; i++) {
            Position position = generateRandomPositionInsideMapWithDistanceToPlayer();
            enemies.add(new Enemy(position));
        }
    }

    private Position generateRandomPositionInsideMapWithDistanceToPlayer() {
        Random r = new Random();

        int minX = map.getStart().x()+1;
        int maxX = map.getEnd().x()-1;
        int minY = map.getStart().y()+1;
        int maxY = map.getEnd().y()-1;

        Position playerPosition = player.getPosition();

        int x, y;

        do {
            x = r.nextInt((maxX - minX) + 1) + minX;
            y = r.nextInt((maxY - minY) + 1) + minY;
        } while (
                Math.abs(x - playerPosition.x()) < 5 ||
                        Math.abs(y - playerPosition.y()) < 5
        );

        return new Position(x, y);
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


    private boolean isWalkable(Direction direction){
        Position prevPlayerPos = player.getPosition();

        player.move(direction);
        Position nextPlayerPos = player.getPosition();
        player.setPosition(prevPlayerPos);

        return map.isPositionInside(nextPlayerPos);
    }

    private record Offset(int x, int y) {}

    private Offset computeOffset(TerminalSize terminalSize) {
        int desiredLeft = Math.max(0, (terminalSize.getColumns() - map.getMapSize().getWidth()) / 2);
        int desiredTop = Math.max(0, (terminalSize.getRows() - map.getMapSize().getHeight()) / 2);

        return new Offset(desiredLeft - map.getStart().x(), desiredTop - map.getStart().y());
    }
}
