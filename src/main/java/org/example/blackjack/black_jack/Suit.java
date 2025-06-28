package org.example.blackjack.black_jack;

public enum Suit {
    SPADE("♠️"),
    HEART("♥️"),
    DIAMOND("♦️"),
    CLUB("♣️");
    String value;

    Suit(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
