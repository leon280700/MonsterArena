package monsterarena.model;

import lombok.Getter;

@Getter
public enum Difficulty {
    EASY("Easy"), MEDIUM("Medium"), HARD("Hard");

    String value;

    Difficulty(String value) {
        this.value = value;
    }

}
