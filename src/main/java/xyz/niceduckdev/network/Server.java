package xyz.niceduckdev.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import xyz.niceduckdev.Bot;
import xyz.niceduckdev.Game;
import xyz.niceduckdev.Player;
import xyz.niceduckdev.cards.*;
import xyz.niceduckdev.utilities.Players;
import xyz.niceduckdev.utilities.State;
import java.util.ArrayList;

public class Server extends Listener {
    private static com.esotericsoftware.kryonet.Server server;
    private static final int maxPlayers = 4;
    private static Player host;
    private static ArrayList<Player> players = new ArrayList<>();
    private static Player currentPlayer;
    private static CardColor currentColor;
    private static State state = State.Lobby;

    private static Deck deck, playedDeck;
    private int cardsToTake;
    private boolean reverse;

    public static void create(int port) {
        try {
            createDecks();

            server = new com.esotericsoftware.kryonet.Server();
            Packets.register(server);
            server.bind(port, port);
            server.start();
            server.addListener(new Server());

            Game.log(String.format("Starting server on %d", port), Game.LogType.Server);
            currentPlayer = players.getFirst();
            state = State.Lobby;
        }
        catch (Exception exception) {
            Game.log(String.format("Error starting server on %d", port), Game.LogType.Server);
        }
    }
    public static boolean isRunning() {
        return (server != null);
    }
    public static void close() {
        server.close();
        server = null;
    }

    public void connected(Connection connection) {
        // Check if there are more than 4 players
        if (players.size() >= maxPlayers || state == State.Game) {
            connection.close();

            return;
        }

        // Add player
        Player player = Players.createPlayer(connection.getID());
        Deck cards = new Deck(deck.getRandomCards(7));
        player.getCards().addAll(cards);
        players.add(player);

        // Send player packet
        Packets.GetPlayer playerPacket = new Packets.GetPlayer();
        playerPacket.player = player;
        server.sendToTCP(player.getId(), playerPacket);

        // Send players packet
        sendPlayers();

        // Select host player
        host = players.getFirst();
    }
    public void disconnected(Connection connection) {
        // Remove player
        Player player = Players.get(connection.getID(), players);
        players.remove(player);

        // Send players packet
        sendPlayers();

        // Select new host player if needed
        if (players.isEmpty()) {
            return;
        }
        host = players.getFirst();

        connection.close();
    }
    public void received(Connection connection, Object object) {
        if (object instanceof Packets.Request packet) {
            if (packet.request.startsWith("start_")) {
                // Check if player that wants to start is host player
                int id = Integer.parseInt(packet.request.substring(6));
                if (id != host.getId()) {
                    return;
                }

                // Check if there are enough players
                if (players.size() <= 1) {
                    return;
                }

                // Send start packet
                Packets.Response response = new Packets.Response();
                response.response = "start";
                server.sendToAllTCP(response);

                // Send players packet
                sendPlayers();

                // Send the last card packet
                Packets.Response cardPacket = new Packets.Response();
                cardPacket.response = String.format("lastcard_%s", playedDeck.getLast().toString());
                server.sendToAllTCP(cardPacket);

                // Set server state
                Server.state = State.Game;

                // Set the current turn
                currentPlayer = players.getFirst();
            }

            else if (packet.request.startsWith("playcard_")) {
                String[] data = packet.request.split("_");
                int id = Integer.parseInt(data[1]);
                Player player = Players.get(id, players);

                if (!Players.matches(currentPlayer, player)) {
                    return;
                }

                Card card = player.getCards().get(Integer.parseInt(data[2]));
                Card lastCard = playedDeck.getLast();

                // Check if the play is valid
                if (!Cards.isValid(lastCard, card) && !card.getColor().equals(currentColor)) {
                    return;
                }

                // Play the card
                player.getCards().remove(card);
                playedDeck.add(card);
                player.setPlayedCard(true);

                // Send players packet
                sendPlayers();

                // Send the last card packet
                Packets.Response cardPacket = new Packets.Response();
                cardPacket.response = String.format("lastcard_%s", playedDeck.getLast().toString());
                server.sendToAllTCP(cardPacket);

                // Check if the card is special
                switch (card.getType()) {
                    case CardType.PlusTwo:
                        cardsToTake += 2;
                        break;
                    case CardType.Skip:
                        // setCurrentPlayer();
                        return;
                    case CardType.Reverse:
                        reverse = !reverse;
                        break;
                    case CardType.ChooseColor:
                        Packets.Request chooseColorRequest = new Packets.Request();
                        chooseColorRequest.request = "choosecolor";
                        server.sendToTCP(player.getId(), chooseColorRequest);
                        return;
                    case CardType.PlusFour:
                        cardsToTake += 4;

                        Packets.Request plusFourRequest = new Packets.Request();
                        plusFourRequest.request = "choosecolor";
                        server.sendToTCP(player.getId(), plusFourRequest);
                        return;
                    default: break;
                }

                nextTurn();
            }

            else if (packet.request.startsWith("takecard_")) {
                int id = Integer.parseInt(packet.request.substring(9));
                Player player = Players.get(id, players);

                if (!Players.matches(currentPlayer, player)) {
                    return;
                }

                if (player.hasTakenCard()) {
                    return;
                }

                // Give the card to the player
                Card card = deck.getLast();
                player.getCards().add(card);
                deck.remove(card);
                player.setTookCard(true);

                // Send players packet
                sendPlayers();

                if (!Cards.isValid(deck.getLast(), card)) {
                    nextTurn();
                }
            }

            else if (packet.request.startsWith("addbot_")) {
                // Check if we are in the lobby
                if (state == State.Game) {
                    return;
                }

                // Check if the player is the host
                int id = Integer.parseInt(packet.request.substring(7));
                if (id != host.getId()) {
                    return;
                }

                // Check if there are more than 4 players
                if (players.size() >= maxPlayers || state == State.Game) {
                    return;
                }

                // Add bot
                Bot bot = Players.createBot();
                Deck cards = new Deck(deck.getRandomCards(7));
                bot.getCards().addAll(cards);
                players.add(bot);

                // Send players packet
                sendPlayers();

                // Debug
                Game.log("Adding bot", Game.LogType.Server);
            }

            else if (packet.request.startsWith("remove_")) {
                // Check if we are in the lobby
                if (state == State.Game) {
                    return;
                }

                String[] data = packet.request.split("_");
                Player client = Players.get(Integer.parseInt(data[1]), players);
                Player bot = Players.get(Integer.parseInt(data[2]), players);

                // Check if the ids are valid
                if (client == null || bot == null) {
                    return;
                }

                // Check if the player is the host
                if (client.getId() != host.getId()) {
                    return;
                }
                // Check if the player is a bot or a player
                if (bot instanceof Bot) {
                    players.remove(bot);
                    sendPlayers();
                }

                // Debug
                Game.log("Removing bot", Game.LogType.Server);
            }

            else if (packet.request.startsWith("username_")) {
                String[] data = packet.request.split("_");
                int id = Integer.parseInt(data[1]);
                Player player = Players.get(id, players);
                String username = data[2];

                if (username.equals("null") || player == null) {
                    return;
                }

                player.setUsername(username);
                sendPlayers();
            }
            else if (packet.request.startsWith("next_")) {
                int id = Integer.parseInt(packet.request.substring(5));
                Player player = Players.get(id, players);

                if (!Players.matches(currentPlayer, player)) {
                    return;
                }

                nextTurn();
            }
            else if (packet.request.startsWith("uno_")) {
                int id = Integer.parseInt(packet.request.substring(5));
                Player player = Players.get(id, players);

                if (!Players.matches(currentPlayer, player)) {
                    return;
                }

                player.setCalledUno(true);
            }
        }
        else if (object instanceof Packets.Response packet) {
            if (packet.response.startsWith("color_")) {
                String color = packet.response.substring(6);
                String type = "";

                if (playedDeck.getLast().getType().equals(CardType.ChooseColor)) {
                    type = "choosecolor";
                }
                else if (playedDeck.getLast().getType().equals(CardType.PlusFour)) {
                    type = "plusfour";
                }

                switch (color) {
                    case "red": currentColor = CardColor.Red; break;
                    case "blue": currentColor = CardColor.Blue; break;
                    case "yellow": currentColor = CardColor.Yellow; break;
                    case "green": currentColor = CardColor.Green; break;
                    default: break;
                }

                Packets.Request requestPacket = new Packets.Request();
                requestPacket.request = String.format("color_%s_%s", color, type);
                server.sendToAllTCP(requestPacket);
                nextTurn();
            }
        }
    }

    private void nextTurn() {
        // Check if player called Uno
        if (currentPlayer.getCards().size() == 1 && !currentPlayer.hasCalledUno()) {
            ArrayList<Card> cards = deck.getRandomCards(7);
            deck.remove(cards);
            currentPlayer.getCards().addAll(cards);
        }

        // Reset stats
        currentPlayer.setTookCard(false);
        currentPlayer.setCalledUno(false);
        currentPlayer.setPlayedCard(false);

        // Set the current player
        int currentIndex = players.indexOf(currentPlayer);
        currentPlayer = Players.getNext(currentIndex, players, reverse);

        // Add x cards to the players deck
        for (int i = 0; i < cardsToTake; i++) {
            Card card = deck.getLast();
            currentPlayer.getCards().add(card);
            deck.remove(card);
        }
        cardsToTake = 0;

        // Check if player is a bot
        if (currentPlayer instanceof Bot) {
            Bot bot = (Bot) currentPlayer;
            // logica voor bot hier
        }

        // Send players packet
        sendPlayers();
    }

    private static void createDecks() {
        deck = new Deck(Cards.getStartingCards());
        deck.shuffle();

        playedDeck = new Deck();
        Card card = deck.getLast();
        deck.remove(card);
        playedDeck.add(card);
    }

    private void sendPlayers() {
        Packets.GetPlayers packet = new Packets.GetPlayers();
        packet.players = players;
        server.sendToAllTCP(packet);
    }
}