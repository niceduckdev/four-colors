package xyz.niceduckdev.ui.pages.settings;

import xyz.niceduckdev.ui.Page;
import xyz.niceduckdev.ui.Window;
import xyz.niceduckdev.ui.layout.mvp.Presenter;

public class SettingsPresenter extends Presenter {
    @Override
    public void addEvents() {
        Page page = Window.getPage("Settings");
        SettingsView view = (SettingsView) page.getView();
        SettingsModel model = (SettingsModel) page.getModel();


//        view.getFullscreenToggle().setOnAction(event -> {
//            toggleFullScreen(fullscreenCheckBox.isSelected());
//        });
        view.getSaveButton().setOnAction(event -> model.save());
        view.getBackButton().setOnAction(event -> model.back());
    }

    public String getUsername() {
        Page page = Window.getPage("Settings");
        SettingsView view = (SettingsView) page.getView();
        return view.getInput().getText();
    }
}
