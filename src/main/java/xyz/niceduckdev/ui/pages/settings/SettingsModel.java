package xyz.niceduckdev.ui.pages.settings;

import xyz.niceduckdev.network.Client;
import xyz.niceduckdev.ui.Page;
import xyz.niceduckdev.ui.Window;
import xyz.niceduckdev.ui.layout.mvp.Model;

public class SettingsModel extends Model {
    public void back() {
        Window.loadPage("Home");
    }

    public void save() {
        Page page = Window.getPage("Settings");
        SettingsPresenter presenter = (SettingsPresenter) page.getPresenter();
        Client.setUsername(presenter.getUsername());
    }
}
