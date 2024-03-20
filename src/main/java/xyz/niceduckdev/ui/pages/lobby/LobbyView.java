package xyz.niceduckdev.ui.pages.lobby;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import xyz.niceduckdev.Player;
import xyz.niceduckdev.ui.Page;
import xyz.niceduckdev.ui.Window;
import xyz.niceduckdev.ui.layout.Button;
import xyz.niceduckdev.ui.layout.Column;
import xyz.niceduckdev.ui.layout.Row;
import xyz.niceduckdev.ui.layout.mvp.View;

import java.util.ArrayList;

public class LobbyView extends View {
    private Row players;
    private Row bots;
    private Button addBot;
    private Button start;
    private Button back;
    private Column column;

    @Override
    public void start() {
        players = new Row();
        bots = new Row();
        addBot = new Button("+");
        start = new Button("Start");
        back = new Button("Back");

        Row botsRow = new Row(
            bots,
            addBot
        );
        botsRow.setSpacing(10);
        botsRow.alignCenter();

        column = new Column(
            players,
            botsRow,
            start,
            back
        );

        players.setSpacing(10);
        players.alignCenter();

        bots.setSpacing(10);
        bots.alignCenter();

        column.setSpacing(10);
        column.alignCenter();
        setCenter(column);
    }

    @Override
    public void update() {
        Page page = Window.getPage("Lobby");
        LobbyPresenter presenter = (LobbyPresenter) page.getPresenter();

        players.getChildren().removeAll(players.getChildren());
        for (Player player : presenter.getPlayers()) {
            Button button = new Button(player.getUsername());
            button.setOnAction(event -> presenter.remove(player.getId()));
            players.add(button);
        }
    }

    public Button getBotButton() {
        return addBot;
    }

    public Button getStartButton() {
        return start;
    }

    public Button getBackButton() {
        return back;
    }

    public Row getPlayers() {
        return players;
    }
}