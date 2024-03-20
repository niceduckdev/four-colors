package xyz.niceduckdev.ui.pages.home;

import xyz.niceduckdev.ui.layout.mvp.Model;
import xyz.niceduckdev.ui.Window;
import xyz.niceduckdev.network.Server;

public class HomeModel extends Model {
    public void host() {
        Server.create(27960);
        Window.loadPage("Lobby");
    }

    public void join() {
        Window.loadPage("Lobby");
    }

    public void settings() {
        Window.loadPage("Settings");
    }

    public void quit() {
        if (Server.isRunning()) {
            Server.close();
        }

        Window.close();
    }
}
