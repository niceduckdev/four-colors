/* Kaj Botty & Jonah Aertssen */
package xyz.niceduckdev.save;

// An object that contains all variables to save.
public class SaveData {
    private String address;
    private int port;

    public SaveData(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    private int getPort() {
        return port;
    }

    public String toString() {
        return String.format("%s: %d", address, port);
    }
}