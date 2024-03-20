package xyz.niceduckdev.ui.layout;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import xyz.niceduckdev.Game;
import xyz.niceduckdev.utilities.Vector2;

public class Button extends javafx.scene.control.Button {
    public Button() {
        setPrefWidth(200);
        setPrefHeight(40);
    }

    public Button(String text) {
        setText(text);
    }

    public Button(String path, Vector2 size) {
        setImage(path, size);
    }

    public void setImage(String path, Vector2 size) {
        try {
            Image image = new Image(String.valueOf(getClass().getResource("/images/" + path)));
            ImageView view = new ImageView(image);
            view.setFitWidth(size.getX());
            view.setFitHeight(size.getY());
            setGraphic(view);

            setStyle("-fx-background-color: transparent;");
        }
        catch (Exception exception) {
            Game.log(String.format("Image %s was not found!", path));
        }
    }

    public void addHover() {
        DropShadow shadow = new DropShadow();
        setOnMouseEntered(e -> setEffect(shadow));
        setOnMouseExited(e -> setEffect(null));
    }
}