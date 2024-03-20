package xyz.niceduckdev.cards;

import java.util.Random;

public enum CardColor {
    Red,
    Blue,
    Yellow,
    Green,
    Black;


    public static CardColor getRandom() {
        int random = new Random().nextInt(4);

        return switch (random) {
            case 0 -> CardColor.Red;
            case 1 -> CardColor.Blue;
            case 2 -> CardColor.Yellow;
            case 3 -> CardColor.Green;
            default -> null;
        };
    }
}
