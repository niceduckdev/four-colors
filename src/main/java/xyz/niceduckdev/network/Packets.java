package xyz.niceduckdev.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import xyz.niceduckdev.Bot;
import xyz.niceduckdev.Player;
import xyz.niceduckdev.cards.Card;
import xyz.niceduckdev.cards.CardColor;
import xyz.niceduckdev.cards.CardType;
import xyz.niceduckdev.cards.Deck;

import java.util.ArrayList;

public class Packets {
    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();

        kryo.register(GetPlayers.class);
        kryo.register(GetPlayer.class);

        kryo.register(Request.class);
        kryo.register(Response.class);

        kryo.register(ArrayList.class);
        kryo.register(Player.class);
        kryo.register(Bot.class);
        kryo.register(Deck.class);
        kryo.register(Card.class);
        kryo.register(CardType.class);
        kryo.register(CardColor.class);
    }

    public static class GetPlayers {
        public ArrayList<Player> players;
    }

    public static class GetPlayer {
        public Player player;
    }

    public static class Request {
        public String request;
    }

    public static class Response {
        public String response;
    }
}