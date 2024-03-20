package xyz.niceduckdev.ui.layout.mvp;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;

public class View extends BorderPane {
    public View() {
        Insets insets = new Insets(10);
        setPadding(insets);
    }

    public void start() {}
    public void update() {}

    public void destroy() {
        getChildren().removeAll(getChildren());
    }
}