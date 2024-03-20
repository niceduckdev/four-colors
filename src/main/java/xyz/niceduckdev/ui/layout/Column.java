package xyz.niceduckdev.ui.layout;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class Column extends VBox {
    public Column(Node... nodes) {
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
