package xyz.niceduckdev.ui;

import javafx.scene.Scene;
import xyz.niceduckdev.ui.layout.mvp.Model;
import xyz.niceduckdev.ui.layout.mvp.Presenter;
import xyz.niceduckdev.ui.layout.mvp.View;

public class Page {
    private Model model;
    private View view;
    private Presenter presenter;
    private Scene scene;

    public Page(Model model, View view, Presenter presenter) {
        this.model = model;
        this.view = view;
        this.presenter = presenter;

        scene = new Scene(this.view);
    }

    public Model getModel() {
        return model;
    }

    public View getView() {
        return view;
    }

    public Presenter getPresenter() {
        return presenter;
    }

    public Scene getScene() {
        return scene;
    }

    public void reload() {
        getView().destroy();

        getView().start();
        getView().update();
        getPresenter().addEvents();
    }
}