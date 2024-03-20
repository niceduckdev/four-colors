package xyz.niceduckdev.ui.pages.lobby;

import xyz.niceduckdev.Player;
import xyz.niceduckdev.network.Client;
import xyz.niceduckdev.ui.Page;
import xyz.niceduckdev.ui.layout.mvp.Presenter;
import xyz.niceduckdev.ui.Window;

import java.util.ArrayList;

public class LobbyPresenter extends Presenter {
    @Override
    public void addEvents() {
        Page page = Window.getPage("Lobby");
        LobbyView view = (LobbyView) page.getView();
        LobbyModel model = (LobbyModel) page.getModel();

        Client.connect("localhost", 27960);

        view.getBotButton().setOnAction(event -> model.addBot());
        view.getStartButton().setOnAction(event -> model.start());
        view.getBackButton().setOnAction(event -> model.back());
    }

    public ArrayList<Player> getPlayers() {
        LobbyModel model = (LobbyModel) Window.getPage("Lobby").getModel();
        return model.getPlayers();
    }

    public void remove(int id) {
        LobbyModel model = (LobbyModel) Window.getPage("Lobby").getModel();
        model.remove(id);
    }
}