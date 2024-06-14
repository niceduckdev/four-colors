package xyz.niceduckdev.ui.pages.home;

import xyz.niceduckdev.ui.Page;
import xyz.niceduckdev.ui.layout.mvp.Presenter;
import xyz.niceduckdev.ui.Window;

public class HomePresenter extends Presenter {
    @Override
    public void addEvents() {
        Page page = Window.getPage("Home");
        HomeView view = (HomeView) page.getView();
        HomeModel model = (HomeModel) page.getModel();

        view.getHostButton().setOnAction(event -> model.host());
        view.getJoinButton().setOnAction(event -> model.join());
        view.getSettingsButton().setOnAction(event -> model.settings());
        view.getQuitButton().setOnAction(event -> model.quit());

        view.getInput().setOnAction(event -> model.fieldChanged());
    }

    public void fieldChanged() {

    }
}