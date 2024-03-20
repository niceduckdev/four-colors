package xyz.niceduckdev.cards;

import java.util.*;

public class Deck extends ArrayList<Card> {
    private Random random;

    public Deck() {
        random = new Random();
    }

    public Deck(Card[] cards) {
        random = new Random();
        addAll(cards);
    }

    public Deck(ArrayList<Card> cards) {
        random = new Random();
        addAll(cards);
    }

    public Card getRandomCard() {
        Card card = get(random.nextInt(0, size()));
        remove(card);
        return card;
    }

    public ArrayList<Card> getRandomCards(int amount) {
        ArrayList<Card> randomCards = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Card card = get(random.nextInt(0, size()));
            remove(card);
            randomCards.add(card);
        }
        return randomCards;
    }

    public void addAll(Card[] cards) {
        addAll(Arrays.asList(cards));
    }

    public void shuffle() {
        Collections.shuffle(this);
    }

    public boolean hasPlusTwo() {
        for (Card card : this) {
            if (card.getType().equals(CardType.PlusTwo)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasPlusFour() {
        for (Card card : this) {
            if (card.getType().equals(CardType.PlusFour)) {
                return true;
            }
        }

        return false;
    }
}
