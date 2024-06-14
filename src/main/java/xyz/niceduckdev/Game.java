package xyz.niceduckdev;

import javafx.application.Application;
import javafx.stage.Stage;
import xyz.niceduckdev.ui.Window;
import xyz.niceduckdev.ui.pages.game.GameModel;
import xyz.niceduckdev.ui.pages.game.GamePresenter;
import xyz.niceduckdev.ui.pages.game.GameView;
import xyz.niceduckdev.ui.pages.home.HomeModel;
import xyz.niceduckdev.ui.pages.home.HomePresenter;
import xyz.niceduckdev.ui.pages.home.HomeView;
import xyz.niceduckdev.ui.pages.lobby.LobbyModel;
import xyz.niceduckdev.ui.pages.lobby.LobbyPresenter;
import xyz.niceduckdev.ui.pages.lobby.LobbyView;
import xyz.niceduckdev.ui.pages.settings.SettingsModel;
import xyz.niceduckdev.ui.pages.settings.SettingsPresenter;
import xyz.niceduckdev.ui.pages.settings.SettingsView;
import xyz.niceduckdev.utilities.Vector2;

public class Game extends Application {
    public enum LogType { Client, Server }
    private static int colorCount = 0;

    public static void main(String[] args) {
        Window.addPage("Home", new HomeModel(), new HomeView(), new HomePresenter());
        Window.addPage("Settings", new SettingsModel(), new SettingsView(), new SettingsPresenter());
        Window.addPage("Lobby", new LobbyModel(), new LobbyView(), new LobbyPresenter());
        Window.addPage("Game", new GameModel(), new GameView(), new GamePresenter());

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Window.create("Four Colors", new Vector2(320, 180));
        Window.loadPage("Home");

        log("Launching window");
    }

    public static void log(String message) {
        System.out.printf("Debug: %s.\n", message);
    }
    public static void log(String message, LogType type) {
        if (type.equals(LogType.Client)) {
            System.out.printf("Client: %s.\n", message);
        }
        else if (type.equals(LogType.Server)) {
            System.out.printf("Server: %s.\n", message);
        }
    }

    public static void increaseColorCount() {
        colorCount += 1;
    }

    public static int getColorCount() {
        return colorCount;
    }
}