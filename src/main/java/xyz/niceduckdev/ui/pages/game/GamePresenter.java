package xyz.niceduckdev.ui.pages.game;

import javafx.scene.Node;
import xyz.niceduckdev.Player;
import xyz.niceduckdev.ui.Page;
import xyz.niceduckdev.ui.Window;
import xyz.niceduckdev.ui.layout.Button;
import xyz.niceduckdev.ui.layout.Column;
import xyz.niceduckdev.ui.layout.Row;
import xyz.niceduckdev.ui.layout.mvp.Presenter;

import java.util.ArrayList;

public class GamePresenter extends Presenter {
    @Override
    public void addEvents() {
        Page page = Window.getPage("Game");
        GameView view = (GameView) page.getView();
        GameModel model = (GameModel) page.getModel();

        int id = getPlayer().getId();
        Node rowOrColumn = view.getCards().get(id);
        if (rowOrColumn instanceof Row) {
            for (int i = 0; i < ((Row) rowOrColumn).getChildren().size(); i++) {
                int index = i;

                Button button = (Button) ((Row) rowOrColumn).getChildren().get(index);
                button.setOnAction(event -> playCard(index));
            }
        }
        else if (rowOrColumn instanceof Column) {
            for (int i = 0; i < ((Column) rowOrColumn).getChildren().size(); i++) {
                int index = i;

                Button button = (Button) ((Column) rowOrColumn).getChildren().get(index);
                button.setOnAction(event -> playCard(index));
            }
        }

        for (Node node : view.getColors().getChildren()) {
            Button button = (Button) node;
            button.setOnAction(event -> model.chooseColor(button.getText().toLowerCase()));
        }

        view.getDeck().setOnAction(event -> model.takeCard());
        view.getNextButton().setOnAction(event -> model.next());
        view.getUnoButton().setOnAction(event -> model.uno());
    }

    private void playCard(int cardIndex) {
        GameModel model = (GameModel) Window.getPage("Game").getModel();
        model.playCard(cardIndex);
    }

    public void chooseColor() {
        GameView view = (GameView) Window.getPage("Game").getView();
        view.toggleChooseColor();
    }

    public Player getPlayer() {
        GameModel model = (GameModel) Window.getPage("Game").getModel();
        return model.getPlayer();
    }

    public ArrayList<Player> getPlayers() {
        GameModel model = (GameModel) Window.getPage("Game").getModel();
        return model.getPlayers();
    }

    public String getLastCard() {
        GameModel model = (GameModel) Window.getPage("Game").getModel();
        return model.getLastCard();
    }
}
