package monsterarena.model;

import lombok.Data;

@Data
public class GameMap {

    private String[] rowSprite = new String[]{"–"};

    private String[] columnSprite = new String[]{"|"};

    private MapSize mapSize;

    private Position start;

    private Position end;

    public GameMap(MapSize mapSize) {
        this.mapSize = mapSize;

        this.start = new Position(0, 0);
        this.end = new Position(mapSize.getWidth(), mapSize.getHeight());
    }
    public boolean isPositionInside(Position position) {
        return position.x() < end.x() && position.x() > start.x() && position.y() < end.y() && position.y() > start.y();
    }

    public Position getCentre() {
        return new Position((end.x() + start.x()) / 2, (end.y() + start.y()) / 2);
    }
}