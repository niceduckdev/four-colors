package xyz.niceduckdev;

import xyz.niceduckdev.cards.Deck;

public class Player {
    private int id;
    private String username;
    private Deck cards = new Deck();
    private boolean tookCard;
    private boolean calledUno;
    private boolean playedCard;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public Deck getCards() {
        return cards;
    }

    public void setTookCard(boolean tookCard) {
        this.tookCard = tookCard;
    }

    public boolean hasTakenCard() {
        return tookCard;
    }

    public void setCalledUno(boolean calledUno) {
        this.calledUno = calledUno;
    }

    public boolean hasCalledUno() {
        return calledUno;
    }

    public void setPlayedCard(boolean playedCard) {
        this.playedCard = playedCard;
    }

    public boolean hasPlayedCard() {
        return playedCard;
    }
}
