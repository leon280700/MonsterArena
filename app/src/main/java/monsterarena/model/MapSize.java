package monsterarena.model;

import lombok.Getter;

@Getter
public enum MapSize {
    SMALL("Klein",45,20), MEDIUM("Mittel", 75, 30), LARGE("Gross", 110,45);

    final String text;
    final int width;
    final int height;

    MapSize(String text, int width, int height) {
        this.text = text;
        this.width = width;
        this.height = height;
    }

}
