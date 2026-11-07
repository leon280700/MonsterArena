package monsterarena.ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Theme {

    private SimpleTheme theme;

    public Theme() {
        this.theme = SimpleTheme.makeTheme(
                true,
                TextColor.ANSI.WHITE, TextColor.ANSI.BLACK,
                TextColor.ANSI.BLACK, TextColor.ANSI.WHITE,
                TextColor.ANSI.BLACK, TextColor.ANSI.YELLOW,
                TextColor.ANSI.BLACK);
        this.theme.getDefaultDefinition().setPreLight(TextColor.ANSI.WHITE, TextColor.ANSI.BLACK_BRIGHT);
    }
}
