package xyz.niceduckdev.ui.layout.mvp;

import com.esotericsoftware.kryonet.Connection;

public abstract class Model {
    public void receive(Connection connection, Object object) {}
}