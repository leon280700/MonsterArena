package monsterarena.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.googlecode.lanterna.gui2.GridLayout;

@Getter
@AllArgsConstructor
public class Layout {

    private GridLayout layout;

    public Layout() {
        this.layout = new GridLayout(1);
        this.layout.setLeftMarginSize(3);
        this.layout.setRightMarginSize(3);
        this.layout.setTopMarginSize(1);
        this.layout.setBottomMarginSize(1);
    }
}
