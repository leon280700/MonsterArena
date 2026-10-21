package monsterarena.model;

import lombok.Data;

@Data
public class Map {

    private String[] rowSprite = new String[]{"–"};

    private String[] columnSprite = new String[]{"|"};

    private Position start = new Position(15, 15);

    private Position end = new Position(90, 45);

    public boolean isPositionInside(Position position) {
        return position.x() < end.x() && position.x() > start.x() && position.y() < end.y() && position.y() > start.y();
    }

}