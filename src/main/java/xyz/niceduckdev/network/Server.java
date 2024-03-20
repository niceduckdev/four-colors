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
    private int cardAmount;
    private boolean reverse;

    public static void create(int port) {
        try {
            createDecks();

            server = new com.esotericsoftware.kryonet.Server();
            Packets.register(server);
            server.bind(port, port);
            server.start();
            server.addListener(new Server());

            state = State.Lobby;

            Game.log(String.format("Starting server on %d", port), Game.LogType.Server);
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

        // Update players
        updatePlayers();

        // Select host player
        host = players.getFirst();

        // Set current turn
        if (players.size() == 1) {
            currentPlayer = players.getFirst();
        }
    }

    public void disconnected(Connection connection) {
        // Remove player
        Player player = Players.get(connection.getID(), players);
        players.remove(player);
        connection.close();

        // Update players
        updatePlayers();

        // Select new host player if needed
        if (!players.isEmpty()) {
            host = players.getFirst();
        }
    }

    public void received(Connection connection, Object object) {
        if (object instanceof Packets.Request packet) {
            if (packet.request.startsWith("username_")) {
                String[] data = packet.request.split("_");
                int id = Integer.parseInt(data[1]);
                Player player = Players.get(id, players);
                String username = data[2];

                // Do nothing is the username was not set
                if (username.equals("null") || player == null) {
                    return;
                }

                // Apply username and update players
                player.setUsername(username);
                updatePlayers();
            }
            else if (packet.request.startsWith("start_")) {
                Player player = Players.get(Integer.parseInt(packet.request.substring(6)), players);

                // Check if the player is the host
                if (!Players.matches(host, player)) {
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

                // Send the last card packet
                Packets.Response cardPacket = new Packets.Response();
                cardPacket.response = String.format("lastcard_%s", playedDeck.getLast().toString());
                server.sendToAllTCP(cardPacket);

                // Update players
                updatePlayers();

                // Set server state and current turn
                Server.state = State.Game;
                currentPlayer = players.getFirst();
            }
            else if (packet.request.startsWith("playcard_")) {
                String[] request = packet.request.split("_");
                Player player = Players.get(Integer.parseInt(request[1]), players);
                Card card = player.getCards().get(Integer.parseInt(request[2]));
                Card lastCard = playedDeck.getLast();

                // Check if it is players turn
                if (!Players.matches(currentPlayer, player)) {
                    return;
                }

                // Check if the play is valid
                if (!Cards.isValid(lastCard, card) && !card.getColor().equals(currentColor)) {
                    return;
                }

                // Play the card
                player.getCards().remove(card);
                playedDeck.add(card);
                player.setPlayedCard(true);

                // Update players
                updatePlayers();

                // Send the last card packet
                Packets.Response cardPacket = new Packets.Response();
                cardPacket.response = String.format("lastcard_%s", playedDeck.getLast().toString());
                server.sendToAllTCP(cardPacket);


                // Check if the card is special
                switch (card.getType()) {
                    case CardType.PlusTwo: cardAmount += 2; break;
                    case CardType.Skip: nextTurn(); break;
                    case CardType.Reverse: reverse = !reverse; break;
                    case CardType.ChooseColor: sendRequest(player, "choosecolor"); return;
                    case CardType.PlusFour:
                        cardAmount += 4;
                        sendRequest(player, "choosecolor");
                        return;
                    default: break;
                }

                nextTurn();
            }
            else if (packet.request.startsWith("takecard_")) {
                Player player = Players.get(Integer.parseInt(packet.request.substring(9)), players);

                // Check if it is players turn
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

                // Update players
                updatePlayers();

                if (!Cards.isValid(deck.getLast(), card)) {
                    nextTurn();
                }
            }
            else if (packet.request.startsWith("addbot_")) {
                Player player = Players.get(Integer.parseInt(packet.request.substring(7)), players);

                // Check if we are in the lobby
                if (state == State.Game) {
                    return;
                }

                // Check if the player is the host
                if (!Players.matches(host, player)) {
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

                // Update players
                updatePlayers();

                // Debug
                Game.log("Adding bot", Game.LogType.Server);
            }
            else if (packet.request.startsWith("removebot_")) {
                String[] request = packet.request.split("_");
                Player player = Players.get(Integer.parseInt(request[1]), players);
                Player bot = Players.get(Integer.parseInt(request[2]), players);

                // Check if we are in the lobby
                if (state == State.Game) {
                    return;
                }

                // Check if the player is the host
                if (!Players.matches(host, player)) {
                    return;
                }

                // Check if the player is a bot or a player
                if (bot instanceof Bot) {
                    players.remove(bot);
                    updatePlayers();
                }

                // Debug
                Game.log("Removing bot", Game.LogType.Server);
            }
            else if (packet.request.startsWith("next_")) {
                Player player = Players.get(Integer.parseInt(packet.request.substring(5)), players);

                // Check if its players turn
                if (!Players.matches(currentPlayer, player)) {
                    return;
                }

                // Check if the player has played or taken a card
                if (!player.hasPlayedCard() && !player.hasTakenCard()) {
                    return;
                }

                // Skip turn
                nextTurn();
            }
            else if (packet.request.startsWith("uno_")) {
                Player player = Players.get(Integer.parseInt(packet.request.substring(5)), players);

                // Check if its players turn
                if (!Players.matches(currentPlayer, player)) {
                    return;
                }

                // Call uno
                player.setCalledUno(true);
            }
        }
        else if (object instanceof Packets.Response packet) {
            if (packet.response.startsWith("color_")) {
                String color = packet.response.substring(6);
                String type = "";

                // Set the type of card
                if (playedDeck.getLast().getType().equals(CardType.ChooseColor)) {
                    type = "choosecolor";
                }
                else if (playedDeck.getLast().getType().equals(CardType.PlusFour)) {
                    type = "plusfour";
                }

                // Set the color
                switch (color) {
                    case "red": currentColor = CardColor.Red; break;
                    case "blue": currentColor = CardColor.Blue; break;
                    case "yellow": currentColor = CardColor.Yellow; break;
                    case "green": currentColor = CardColor.Green; break;
                    default: break;
                }

                // Send the card to all clients
                sendRequest(String.format("color_%s_%s", color, type));

                // Go to the next turn if it was a choosecolor card
                if (type.equals("choosecolor")) {
                    nextTurn();
                }
            }
        }
    }

    private void nextTurn() {
        // Check if player called Uno
        if (currentPlayer.getCards().size() == 2 && !currentPlayer.hasCalledUno()) {
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
        for (int i = 0; i < cardAmount; i++) {
            Card card = deck.getLast();
            currentPlayer.getCards().add(card);
            deck.remove(card);
        }
        cardAmount = 0;

        // Check if player is a bot
        if (currentPlayer instanceof Bot) {
            Bot bot = (Bot) currentPlayer;
            Card card = bot.chooseCard(playedDeck.getLast());

            // Take or play card
            if (card == null) {
                Card randomCard = deck.getFirst();
                bot.getCards().add(randomCard);
                deck.remove(randomCard);
            }
            else {
                bot.getCards().remove(card);
                playedDeck.add(card);

                if (card.getType() == CardType.ChooseColor || card.getType() == CardType.PlusFour) {
                    currentColor = CardColor.getRandom();
                    String type = "";

                    if (card.getType() == CardType.ChooseColor) {
                        type = "choosecolor";
                    }
                    else if (card.getType() == CardType.PlusFour) {
                        type = "plusfour";
                    }

                    // Send the card to all clients
                    sendRequest(String.format("color_%s_%s", currentColor.toString().toLowerCase(), type));
                }
            }

            // Send the last card packet
            Packets.Response cardPacket = new Packets.Response();
            cardPacket.response = String.format("lastcard_%s", playedDeck.getLast().toString());
            server.sendToAllTCP(cardPacket);

            nextTurn();
        }

        // Update players
        updatePlayers();
    }

    private static void createDecks() {
        deck = new Deck(Cards.getStartingCards());
        deck.shuffle();

        playedDeck = new Deck();
        Card card = deck.getLast();
        deck.remove(card);
        playedDeck.add(card);
    }
    
    private void sendRequest(String request) {
        Packets.Request packet = new Packets.Request();
        packet.request = request;
        server.sendToAllTCP(packet);
    }

    private void sendRequest(Player player, String request) {
        Packets.Request packet = new Packets.Request();
        packet.request = request;
        server.sendToTCP(player.getId(), packet);
    }

    private void updatePlayers() {
        Packets.GetPlayers packet = new Packets.GetPlayers();
        packet.players = players;
        server.sendToAllTCP(packet);
    }
}