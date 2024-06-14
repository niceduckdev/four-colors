package xyz.niceduckdev.ui.pages.home;

import xyz.niceduckdev.Game;
import xyz.niceduckdev.save.Save;
import xyz.niceduckdev.save.SaveData;
import xyz.niceduckdev.ui.layout.mvp.Model;
import xyz.niceduckdev.ui.Window;
import xyz.niceduckdev.network.Server;

import java.io.IOException;

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

    public void fieldChanged() {
        int colorCount = Game.getColorCount();
        SaveData data = new SaveData("color count", colorCount);
        try {
            Save.saveData(data);
        } catch (IOException e) {
            Game.log("Error saving color count");
        }
    }
}
