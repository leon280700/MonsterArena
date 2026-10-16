package monsterarena.model;

import lombok.Data;

@Data
public class Enemy implements Entity {
    private String[] sprite = {"X"};

    private Position position;

    public Enemy(Position position) {
        this.position = position;
    }

    @Override
    public void move(Direction direction) {
        this.setPosition(new Position(position.x() + direction.getDx(), position.y() + direction.getDy()));
    }

}
