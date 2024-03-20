package xyz.niceduckdev.ui.pages.lobby;

import com.esotericsoftware.kryonet.Connection;
import javafx.application.Platform;
import xyz.niceduckdev.Player;
import xyz.niceduckdev.network.Client;
import xyz.niceduckdev.ui.layout.mvp.Model;
import xyz.niceduckdev.ui.Window;
import xyz.niceduckdev.ui.pages.game.GameModel;
import xyz.niceduckdev.network.Server;
import xyz.niceduckdev.network.Packets;
import xyz.niceduckdev.utilities.State;

import java.util.ArrayList;

public class LobbyModel extends Model {
    private Player player;
    private boolean sentUsername;
    private ArrayList<Player> players = new ArrayList<>();

    public void addBot() {
        Packets.Request packet = new Packets.Request();
        packet.request = String.format("addbot_%d", player.getId());
        Client.send(packet);
    }

    public void remove(int id) {
        Packets.Request packet = new Packets.Request();
        packet.request = String.format("removebot_%d_%d", player.getId(), id);
        Client.send(packet);
    }

    public void start() {
        Packets.Request packet = new Packets.Request();
        packet.request = String.format("start_%d", player.getId());
        Client.send(packet);
    }

    public void back() {
        Client.disconnect();
        Window.loadPage("Home");

        if (Server.isRunning()) {
            Server.close();
        }
    }

    @Override
    public void receive(Connection connection, Object object) {
        LobbyView view = (LobbyView) Window.getPage("Lobby").getView();

        switch (object) {
            case Packets.GetPlayers packet:
                players = packet.players;
                Platform.runLater(view::update);
                break;
            case Packets.GetPlayer packet:
                player = packet.player;

                if (sentUsername) {
                    break;
                }

                Packets.Request usernamePacket = new Packets.Request();
                usernamePacket.request = String.format("username_%s_%s", player.getId(), Client.getUsername());
                Client.send(usernamePacket);
                break;
            case Packets.Response response:
                if (response.response.equals("start")) {
                    Platform.runLater(() -> {
                        Window.loadPage("Game");
                        ((GameModel) Window.getPage("Game").getModel()).setPlayer(player);
                        Client.setState(State.Game);
                    });
                }
                break;
            default: break;
        }
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
}