package roguelike;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Player implements Entity{
    final String MODEL = "@";

    private int[] position = new int[]{3,2};

    @Override
    public void moveLeft() {
        int[] desiredPosition = new int[]{this.position[0] - 2, this.position[1]};
        this.setPosition(desiredPosition);
    }

    @Override
    public void moveRight() {
        int[] desiredPosition = new int[]{this.position[0] + 2, this.position[1]};
        this.setPosition(desiredPosition);
    }

    @Override
    public void moveUp() {
        int[] desiredPosition = new int[]{this.position[0], this.position[1] - 1};
        this.setPosition(desiredPosition);
    }

    @Override
    public void moveDown() {
        int[] desiredPosition = new int[]{this.position[0], this.position[1] + 1};
        this.setPosition(desiredPosition);
    }
}
