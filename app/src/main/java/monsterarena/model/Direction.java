package monsterarena.model;

import lombok.Getter;

@Getter
public enum Direction {
    UP("^", 0, -1), DOWN("v", 0, 1), LEFT("<", -1, 0), RIGHT(">", 1, 0);

    private final String symbol;
    private final int dx, dy;

    Direction(String symbol, int dx, int dy) {
        this.symbol = symbol; this.dx = dx; this.dy = dy;
    }

}
