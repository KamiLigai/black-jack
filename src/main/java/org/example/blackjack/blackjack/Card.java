package org.example.blackjack.blackjack;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public record Card(Suit suit, Rank rank) {
    public final static String CARD_BACK = "|??| ";

    @Override
    public String toString() {
        return "|" + suit.getValue() + rank.getValue() + "| ";
    }
}