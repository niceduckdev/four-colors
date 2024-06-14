package xyz.niceduckdev.cards;

import xyz.niceduckdev.Game;

public class Card {
    private CardType type;
    private CardColor color;

    public void setType(CardType type) {
        if (type == CardType.ChooseColor || type == CardType.PlusFour) {
            this.color = CardColor.Black;
            this.type = type;
            return;
        }

        this.type = type;
    }

    public void setColor(CardColor color) {
        Game.increaseColorCount();

        if (type == CardType.ChooseColor || type == CardType.PlusFour) {
            this.color = CardColor.Black;
            return;
        }

        this.color = color;
    }

    public CardType getType() {
        return type;
    }

    public CardColor getColor() {
        return color;
    }

    public String toString() {
        return String.format("%s_%s", getColor().toString().toLowerCase(), getType().toString().toLowerCase());
    }
}