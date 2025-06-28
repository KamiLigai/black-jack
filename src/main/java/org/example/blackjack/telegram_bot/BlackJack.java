package org.example.blackjack.telegram_bot;

import org.example.blackjack.black_jack.Card;
import org.example.blackjack.black_jack.Deck;

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

    // Начать новую партию
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

    // Игрок берет карту
    public String playerHit() {
        if (gameOver) {
            return "Партия окончена. Начни новую!";
        }

        playerHand.add(deck.draw());

        return checkForResult();
    }

    // Игрок завершает ход, дилер доигрывает
    public String playerStand() {
        if (gameOver) {
            return "Партия окончена. Начни новую!";
        }

        // Дилер ходит
        while (Card.sumOfCards(dealerHand) < 17) {
            dealerHand.add(deck.draw());
        }

        gameOver = true;
        return getGameResult();
    }

    private String checkForResult() {
        int score = Card.sumOfCards(playerHand);
        if (score >= 21) {
            gameOver = true;
            return getGameResult();
        }
        return "Ты взял карту: " + lastCardToString(playerHand) + "\nРука: " + handToString(playerHand);
    }

    private String getGameResult() {
        int playerScore = Card.sumOfCards(playerHand);
        int dealerScore = Card.sumOfCards(dealerHand);

        String res = "Ваши карты: " + handToString(playerHand) + " (" + playerScore + ")\n" +
            "Карты дилера: " + handToString(dealerHand) + " (" + dealerScore + ")\n";

        if (playerScore > 21) return res + "Ты проиграл (перебор)!";
        if (dealerScore > 21) return res + "Дилер перебрал, ты выиграл!";
        if (playerScore > dealerScore) return res + "Ты выиграл!";
        if (playerScore < dealerScore) return res + "Ты проиграл!";
        return res + "Ничья!";
    }

    private String handToString(List<Card> hand) {
        StringBuilder sb = new StringBuilder();
        for (Card c : hand) {
            sb.append(c.toString()).append(" ");
        }
        return sb.toString().trim();
    }

    private String lastCardToString(List<Card> hand) {
        return hand.isEmpty() ? "" : hand.get(hand.size() - 1).toString();
    }

    public String playerHandToString() {
        return handToString(playerHand);
    }
}