package xyz.niceduckdev.ui;

import javafx.stage.Stage;
import xyz.niceduckdev.ui.layout.mvp.Model;
import xyz.niceduckdev.ui.layout.mvp.Presenter;
import xyz.niceduckdev.ui.layout.mvp.View;
import xyz.niceduckdev.utilities.Vector2;

import java.util.Collection;
import java.util.HashMap;

public class Window {
    private static Stage window;
    private static HashMap<String, Page> pages = new HashMap<>();

    public static void create(String title, Vector2 size) {
        window = new Stage();
        window.setTitle(title);
        window.setWidth(size.getX());
        window.setHeight(size.getY());
        window.show();
    }

    public static void close() {
        window.hide();
    }

    public static void loadPage(String name) {
        Page page = getPage(name.toLowerCase());
        window.setScene(page.getScene());
        page.reload();
    }

    public static void addPage(String name, Model model, View view, Presenter presenter) {
        pages.put(name.toLowerCase(), new Page(model, view, presenter));
    }

    public static Page getPage(String name) {
        return pages.get(name.toLowerCase());
    }

    public static Collection<Page> getPages() {
        return pages.values();
    }
}