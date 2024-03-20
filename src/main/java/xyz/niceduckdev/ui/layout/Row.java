package xyz.niceduckdev.ui.layout;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class Row extends HBox {
    public Row(Node... nodes) {
        addAll(nodes);
    }

    public void add(Node node) {
        getChildren().add(node);
    }

    public void addAll(Node... nodes) {
        getChildren().addAll(nodes);
    }

    public void alignCenter() {
        setAlignment(Pos.CENTER);
    }
}
