package xyz.niceduckdev.save;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Save {
    private static final String path = System.getProperty("user.home") + "/four-colors/saves/";

    public static void saveData(SaveData data) throws IOException {
        try {
            createDirectory(path);
            FileWriter writer = new FileWriter(new File(path, data.getAddress() + ".save"));
            writer.write(data.toString());
            writer.close();
        } catch (IOException exception) {
            System.out.printf("Save '%s.save' could not be saved.\n" + exception.getMessage(), data.getAddress());
        }
    }

    private static void createDirectory(String path) {
        new File(path).mkdirs();
    }

    public static void loadData(String username) {
        try {
            Scanner reader = new Scanner(new File(path, username + ".save"));
            System.out.println(reader.nextLine());
            reader.close();
        } catch (IOException exception) {
            System.out.printf("Save '%s.save' could not be loaded.\n" + exception.getMessage(), username);
        }
    }
}