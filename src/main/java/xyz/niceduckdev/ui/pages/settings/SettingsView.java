package xyz.niceduckdev.ui.pages.settings;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import xyz.niceduckdev.ui.layout.Button;
import xyz.niceduckdev.ui.layout.Column;
import xyz.niceduckdev.ui.layout.mvp.View;
import xyz.niceduckdev.utilities.Vector2;

public class SettingsView extends View {
    private TextField input;
    private Slider slider;
    private CheckBox checkbox;
    private Button back;
    private Button save;

    private double volume = 50;

    @Override
    public void start() {
        back = new Button();
        back.setImage("back.png", new Vector2(200, 55));
        back.addHover();



        Column column = new Column();
        column.setSpacing(10);
        column.addAll(
            createUsernameInput(),
            createVolumeSlider(),
            createFullscreenToggle(),
            back
        );
        column.alignCenter();
        setCenter(column);
    }

    private Column createUsernameInput() {
        Label label = new Label("Username:");
        input = new TextField();
        save = new Button("Save");

        return new Column(
            label,
            input,
            save
        );
    }

    private Column createVolumeSlider() {
        Label label = new Label("Volume:");
        slider = new Slider(0, 100, volume);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(false);
        slider.setMajorTickUnit(100);
        slider.setMinorTickCount(5);

        slider.valueProperty().addListener((obs, oldValue, newValue) -> {
            volume = newValue.doubleValue();
        });

        return new Column(
            label,
            slider
        );
    }

    private Column createFullscreenToggle() {
        Label label = new Label("Fullscreen:");
        checkbox = new CheckBox();

        return new Column(
            label,
            checkbox
        );
    }

    public double getVolume() {
        return volume;
    }

    public TextField getInput() {
        return input;
    }

    public CheckBox getFullscreenToggle() {
        return checkbox;
    }

    public Button getBackButton() {
        return back;
    }

    public Button getSaveButton() {
        return save;
    }
}
