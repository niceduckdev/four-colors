package xyz.niceduckdev;

import xyz.niceduckdev.cards.Card;
import xyz.niceduckdev.cards.Cards;

public class Bot extends Player {
    public Card chooseCard(Card lastCard) {
        for (Card card : getCards()) {
            if (Cards.isValid(lastCard, card)) {
                return card;
            }
        }
        return null;
    }
}
