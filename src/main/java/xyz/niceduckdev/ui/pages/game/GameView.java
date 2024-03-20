package xyz.niceduckdev.ui.pages.game;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import xyz.niceduckdev.Player;
import xyz.niceduckdev.cards.Card;
import xyz.niceduckdev.network.Client;
import xyz.niceduckdev.ui.layout.Button;
import xyz.niceduckdev.ui.layout.Column;
import xyz.niceduckdev.ui.layout.Row;
import xyz.niceduckdev.ui.Window;
import xyz.niceduckdev.ui.layout.mvp.View;
import xyz.niceduckdev.utilities.Players;
import xyz.niceduckdev.utilities.State;
import xyz.niceduckdev.utilities.Vector2;

import java.util.ArrayList;
import java.util.HashMap;

public class GameView extends View {
    private HashMap<Integer, Node> cards = new HashMap<>();
    private Button deck, playedDeck;
    private Button next;
    private Button uno;
    private enum Orientation { Row, Column }
    private Row row;
    private Column column;
    private GridPane colors;

    @Override
    public void start() {
        deck = new Button("cards/card_back.png", new Vector2(36, 58));
        playedDeck = new Button();
        next = new Button("Next");
        uno = new Button("Uno");

        Row buttons = new Row(
            next,
            uno
        );
        buttons.alignCenter();

        row = new Row(
            deck,
            playedDeck
        );
        row.alignCenter();

        column = new Column(
            row,
            buttons
        );

        column.alignCenter();

        colors = new GridPane();
        colors.add(new Button("Red"), 0, 0);
        colors.add(new Button("Blue"), 1, 0);
        colors.add(new Button("Yellow"), 0, 1);
        colors.add(new Button("Green"), 1, 1);
        colors.setAlignment(Pos.CENTER);

        setCenter(column);
    }

    @Override
    public void update() {
        if (Client.getState() != State.Game) {
            return;
        }

        getChildren().removeAll(getChildren());

        start();
        updateCards();
        updateDeck();
    }

    private void updateCards() {
        GamePresenter presenter = (GamePresenter) Window.getPage("Game").getPresenter();
        ArrayList<Player> players = presenter.getPlayers();

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);

            switch (players.size()) {
                case 2:
                    switch (i) {
                        case 0: setBottom(getCards(player, Orientation.Row)); break;
                        case 1: setTop(getCards(player, Orientation.Row)); break;
                        default: break;
                    }
                    break;
                case 3:
                    switch (i) {
                        case 0: setLeft(getCards(player, Orientation.Column)); break;
                        case 1: setBottom(getCards(player, Orientation.Row)); break;
                        case 2: setRight(getCards(player, Orientation.Column)); break;
                        default: break;
                    }
                    break;
                case 4:
                    switch (i) {
                        case 0: setLeft(getCards(player, Orientation.Column)); break;
                        case 1: setBottom(getCards(player, Orientation.Row)); break;
                        case 2: setRight(getCards(player, Orientation.Column)); break;
                        case 3: setTop(getCards(player, Orientation.Row)); break;
                        default: break;
                    }
                    break;
                default: break;
            }
        }
    }
    private void updateDeck() {
        GamePresenter presenter = (GamePresenter) Window.getPage("Game").getPresenter();
        playedDeck.setImage(String.format("cards/%s.png", presenter.getLastCard()), new Vector2(36, 58));
    }

    private Node getCards(Player player, Orientation orientation) {
        GamePresenter presenter = (GamePresenter) Window.getPage("Game").getPresenter();
        Player client = presenter.getPlayer();

        if (orientation.equals(Orientation.Row)) {
            Row row = new Row();

            for (Card card : player.getCards()) {
                Button button = new Button();

                if (Players.matches(player, client)) {
                    button.setImage(String.format("cards/%s.png", card.toString()), new Vector2(36, 58));
                }
                else {
                    button.setImage("cards/card_back.png", new Vector2(36, 58));
                }

                row.add(button);
            }

            row.alignCenter();
            cards.put(player.getId(), row);

            presenter.addEvents();
            return row;
        }
        else if (orientation.equals(Orientation.Column)){
            Column column = new Column();

            for (Card card : player.getCards()) {
                Button button = new Button();

                if (player.getId() == client.getId()) {
                    button.setImage(String.format("cards/%s.png", card.toString()), new Vector2(36, 58));
                }
                else {
                    button.setImage("cards/card_back.png", new Vector2(36, 58));
                }

                column.add(button);
            }

            column.alignCenter();
            cards.put(player.getId(), column);

            presenter.addEvents();
            return column;
        }

        return null;
    }

    public HashMap<Integer, Node> getCards() {
        return cards;
    }

    public Button getDeck() {
        return deck;
    }

    public void toggleChooseColor() {
        if (getCenter().equals(column)) {
            setCenter(colors);
            return;
        }

        setCenter(column);
    }

    public GridPane getColors() {
        return colors;
    }

    public Button getNextButton() {
        return next;
    }

    public Button getUnoButton() {
        return uno;
    }
}