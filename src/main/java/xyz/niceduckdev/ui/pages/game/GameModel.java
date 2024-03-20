package xyz.niceduckdev.ui.pages.game;

import com.esotericsoftware.kryonet.Connection;
import javafx.application.Platform;
import xyz.niceduckdev.Player;
import xyz.niceduckdev.network.Client;
import xyz.niceduckdev.ui.Page;
import xyz.niceduckdev.ui.Window;
import xyz.niceduckdev.ui.layout.mvp.Model;
import xyz.niceduckdev.network.Packets;
import java.util.ArrayList;

public class GameModel extends Model {
    private Player player;
    private Player currentPlayer;
    private String lastCard;
    private ArrayList<Player> players = new ArrayList<>();
    private String currentColorPath = "";

    @Override
    public void receive(Connection connection, Object object) {
        GameView view = (GameView) Window.getPage("Game").getView();
        GamePresenter presenter = (GamePresenter) Window.getPage("Game").getPresenter();

        switch (object) {
            case Packets.GetPlayers packet:
                players = packet.players;
                Platform.runLater(view::update);
                break;
            case Packets.GetPlayer packet:
                player = packet.player;
                break;
            case Packets.Response packet:
                if (packet.response.startsWith("lastcard_")) {
                    lastCard = packet.response.substring(9);
                }
                break;
            case Packets.Request packet:
                if (packet.request.startsWith("choosecolor")) {
                    Platform.runLater(presenter::chooseColor);
                }
                else if (packet.request.startsWith("color_")) {
                    String[] data = packet.request.split("_");
                    String color = data[1];
                    String type = data[2];

                    lastCard = String.format("%s_%s", color, type);
                    Platform.runLater(view::update);
                }
                break;
            default: break;
        }
    }

    public void playCard(int cardIndex) {
        Player player = getPlayer();

        Packets.Request request = new Packets.Request();
        request.request = String.format("playcard_%s_%s", player.getId(), cardIndex);
        Client.send(request);
    }

    public void takeCard() {
        Player player = getPlayer();

        Packets.Request request = new Packets.Request();
        request.request = String.format("takecard_%s", player.getId());
        Client.send(request);
    }

    public void chooseColor(String color) {
        Packets.Response responsePacket = new Packets.Response();
        responsePacket.response = String.format("color_%s", color);
        Client.send(responsePacket);

        Page page = Window.getPage("Game");
        GameView view = (GameView) page.getView();
        view.toggleChooseColor();
    }

    public void next() {
        Player player = getPlayer();

        Packets.Request request = new Packets.Request();
        request.request = String.format("next_%s", player.getId());
        Client.send(request);
    }

    public void uno() {
        Player player = getPlayer();

        Packets.Request request = new Packets.Request();
        request.request = String.format("uno_%s", player.getId());
        Client.send(request);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public String getLastCard() {
        return lastCard;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public String getCurrentColorPath() {
        return currentColorPath;
    }

    public void setCurrentColorPath(String path) {
        currentColorPath = path;
    }
}
