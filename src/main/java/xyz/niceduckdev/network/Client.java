package xyz.niceduckdev.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import xyz.niceduckdev.Game;
import xyz.niceduckdev.save.Save;
import xyz.niceduckdev.save.SaveData;
import xyz.niceduckdev.ui.Page;
import xyz.niceduckdev.ui.Window;
import xyz.niceduckdev.utilities.State;

import java.util.Collection;

public class Client extends Listener {
    private static String username;
    private static com.esotericsoftware.kryonet.Client client;
    private static Collection<Page> pages;
    private static State state = State.Lobby;

    public static void connect(String address, int port) {
        pages = Window.getPages();

        try {
            client = new com.esotericsoftware.kryonet.Client();
            Packets.register(client);
            client.addListener(new Client());
            client.start();
            client.connect(5000, address, port, port);

            Game.log(String.format("Connecting to %s:%d", address, port), Game.LogType.Client);
            Save.saveData(new SaveData(address, port));
        }
        catch (Exception exception) {
            disconnect();
            Window.loadPage("Home");

            Game.log(String.format("Error connecting to %s:%d", address, port), Game.LogType.Client);
        }
    }

    public static boolean isConnected() {
        if (client == null) {
            return false;
        }

        return client.isConnected();
    }

    public static void disconnect() {
        client.stop();
        client = null;

        Game.log("Disconnecting", Game.LogType.Client);
    }

    @Override
    public void received(Connection connection, Object object) {
        for (Page page : pages) {
            page.getModel().receive(connection, object);
        }
    }

    public static void send(Object object) {
        client.sendTCP(object);
    }

    public static void setState(State state) {
        Client.state = state;
    }

    public static State getState() {
        return state;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Client.username = username;
    }
}