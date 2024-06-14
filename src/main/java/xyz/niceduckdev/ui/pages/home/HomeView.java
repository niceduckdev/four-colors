package xyz.niceduckdev.ui.pages.home;

import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import xyz.niceduckdev.ui.layout.Button;
import xyz.niceduckdev.ui.layout.Column;
import xyz.niceduckdev.ui.layout.Row;
import xyz.niceduckdev.ui.layout.mvp.View;
import xyz.niceduckdev.utilities.Vector2;

public class HomeView extends View {
    private Button host;
    private Button join;
    private Button settings;
    private Button quit;
    private ImageView hand;
    private TextField input;

    @Override
    public void start() {
        host = new Button();
        join = new Button();
        settings = new Button();
        quit = new Button();

        input = new TextField();

        String path = String.valueOf(getClass().getResource("/images/hand.png"));
        hand = new ImageView(new Image(path));

        host.setImage("host.png", new Vector2(200, 55));
        join.setImage("join.png", new Vector2(200, 55));
        settings.setImage("settings.png", new Vector2(200, 55));
        quit.setImage("quit.png", new Vector2(200, 55));

        hand.setFitWidth(500);
        hand.setFitHeight(300);

        host.addHover();
        join.addHover();
        settings.addHover();
        quit.addHover();

        Row row = new Row();
        Column column = new Column();
        column.addAll(
            host,
            join,
            settings,
            quit
        );
        column.setSpacing(10);
        column.alignCenter();

        row.addAll(
            column,
            hand
        );
        row.setSpacing(50);
        row.alignCenter();
        setCenter(row);

        setRight(input);
    }

    public Button getHostButton() {
        return host;
    }

    public Button getJoinButton() {
        return join;
    }

    public Button getSettingsButton() {
        return settings;
    }

    public Button getQuitButton() {
        return quit;
    }

    public TextField getInput() {
        return input;
    }
}