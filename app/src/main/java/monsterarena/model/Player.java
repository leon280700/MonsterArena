package monsterarena.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class Player implements Entity {
    private String[] sprite = {"v"};

    private Map<Direction, String> sprites = new HashMap<>();

    private Position position = new Position(3,2);

    private Direction facing = Direction.DOWN;

    public Player() {
        sprites.put(Direction.UP, "^");
        sprites.put(Direction.DOWN, "v");
        sprites.put(Direction.LEFT, "<");
        sprites.put(Direction.RIGHT, ">");
    }
    @Override
    public void move(Direction direction) {
        this.setPosition(new Position(position.x() + direction.getDx(), position.y() + direction.getDy()));
        this.setSprite(new String[]{direction.getSymbol()});
        this.setFacing(direction);
    }

    public Shot attack() {
        return new Shot(position, facing);
    }
}
