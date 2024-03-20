package xyz.niceduckdev.utilities;

import xyz.niceduckdev.Bot;
import xyz.niceduckdev.Player;

import java.util.ArrayList;

public class Players {
    private static int botId = 8;

    public static Player get(int id, ArrayList<Player> players) {
        for (Player player : players) {
            if (player.getId() == id) {
                return player;
            }
        }

        return null;
    }

    public static Player getNext(int index, ArrayList<Player> players, boolean reverse) {
        if (reverse) {
            if (index - 1 <= 0) {
                return players.getLast();
            }

            return players.get(index - 1);
        }

        if (index + 1 >= players.size()) {
            return players.getFirst();
        }

        return players.get(index + 1);
    }

    public static Player createPlayer(int id) {
        Player player = new Player();
        player.setId(id);
        player.setUsername(String.format("Player %d", id));
        return player;
    }

    public static Bot createBot() {
        Bot bot = new Bot();
        bot.setId(botId);
        bot.setUsername(String.format("Bot %d", botId));
        botId += 1;
        return bot;
    }

    public static boolean matches(Player a, Player b) {
        return a.getId() == b.getId();
    }
}
