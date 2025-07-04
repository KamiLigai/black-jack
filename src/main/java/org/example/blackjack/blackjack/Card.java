package org.example.blackjack.blackjack;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Card implements Cloneable {
    public final static String CARD_BACK = "|??| ";
    private final Suit suit;
    private final Rank rank;
    private final String text;
    private volatile int value;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
        this.text = "|" + suit.getValue() + rank.getValue() + "| ";
        this.value = getValueByRank(rank);
    }

    public int getValueByRank(Rank rank) {
        switch (rank) {
            case ACE:
                return 11;
            case TWO:
                return 2;
            case THREE:
                return 3;
            case FOUR:
                return 4;
            case FIVE:
                return 5;
            case SIX:
                return 6;
            case SEVEN:
                return 7;
            case EIGHT:
                return 8;
            case NINE:
                return 9;
            case TEN:
            case JACK:
            case QUEUE:
            case KING:
                return 10;
        }
        return -1;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static int sumOfCards(List<Card> cards) {
        int sum = cards.stream().mapToInt(Card::getValue).sum();
        boolean haveACE = true;
        while (sum > 21 && haveACE) {
            int i;
            for (i = 0; i < cards.size(); i++) {
                Card c = cards.get(i);
                if (c.getValue() == 11) {
                    try {
                        Card clone = c.clone();
                        clone.setValue(1);
                        cards.set(i, clone);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            if (i >= cards.size()) {
                haveACE = false;
            }
            sum = cards.stream().mapToInt(Card::getValue).sum();
        }
        return sum;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public Card clone() throws CloneNotSupportedException {
        return (Card) super.clone();
    }
}
