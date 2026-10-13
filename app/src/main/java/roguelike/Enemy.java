package roguelike;

import lombok.Data;

@Data
public class Enemy implements Entity {
    final String MODEL = "X";

    private int[] position = new int[]{15,15};
    @Override
    public void moveLeft() {
        int[] desiredPosition = new int[]{this.position[0] - 1, this.position[1]};
        this.setPosition(desiredPosition);
    }

    @Override
    public void moveRight() {
        int[] desiredPosition = new int[]{this.position[0] + 1, this.position[1]};
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
