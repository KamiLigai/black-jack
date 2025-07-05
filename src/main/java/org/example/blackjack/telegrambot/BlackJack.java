package org.example.blackjack.telegrambot;

import org.example.blackjack.blackjack.Card;
import org.example.blackjack.blackjack.Deck;

import java.util.ArrayList;
import java.util.List;

public class BlackJack {
    private Deck deck;
    private List<Card> playerHand;
    private List<Card> dealerHand;
    private boolean gameOver;

    public BlackJack() {
        this.gameOver = true;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void startNewGame() {
        deck = new Deck();
        deck.shuffle();
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();
        gameOver = false;

        playerHand.add(deck.draw());
        playerHand.add(deck.draw());
        dealerHand.add(deck.draw());
        dealerHand.add(deck.draw());
    }

    public boolean playerHit() {
        if (gameOver) {
            return false;
        }
        playerHand.add(deck.draw());
        if (isPlayerBust()) {
            gameOver = true;
        }
        return true;
    }

    public boolean playerStand() {
        if (gameOver) {
            return false;
        }

        while (Card.sumOfCards(dealerHand) < 17) {
            dealerHand.add(deck.draw());
        }

        gameOver = true;
        return true;
    }

    public int getPlayerScore() {
        return Card.sumOfCards(playerHand);
    }

    public int getDealerScore() {
        return Card.sumOfCards(dealerHand);
    }

    public boolean isPlayerBust() {
        return getPlayerScore() > 21;
    }

    public boolean isDealerBust() {
        return getDealerScore() > 21;
    }

    public String playerHandToString() {
        return handToString(playerHand);
    }

    public String dealerHandToString() {
        return handToString(dealerHand);
    }

    private String handToString(List<Card> hand) {
        StringBuilder sb = new StringBuilder();
        for (Card c : hand) {
            sb.append(c.toString()).append(" ");
        }
        return sb.toString().trim();
    }

    public String lastCardToString() {
        return playerHand.isEmpty() ? "" : playerHand.get(playerHand.size() - 1).toString();
    }
}
