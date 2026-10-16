package monsterarena.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Shot implements Entity{
    private String[] sprite = {"o"};

    private int range = 10;

    private int remainingRange = range;

    private Position position;

    private Direction direction;

    public Shot(Position position, Direction direction) {
        this.position = position;
        this.direction = direction;
    }

    public void advance(){
        this.move(direction);
        remainingRange--;
    }

    public boolean isExhausted(){
        return remainingRange <= 0;
    }
    @Override
    public void move(Direction direction) {
        this.setPosition(new Position(position.x() + direction.getDx(), position.y() + direction.getDy()));
    }
}
