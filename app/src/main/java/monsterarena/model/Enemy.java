package monsterarena.model;

import lombok.Data;

@Data
public class Enemy implements Entity {
    private String[] sprite = {"X"};

    private Position position = new Position(15,15);

    @Override
    public void move(Direction direction) {
        this.setPosition(new Position(position.x() + direction.getDx(), position.y() + direction.getDy()));
    }

}
