package org.example.blackjack;

import org.example.blackjack.telegram_bot.BlackJack;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class TelegramBot extends TelegramLongPollingBot {

    private static final String TOKEN = "7786403468:AAEwhrybrtlTXhEj0fbC94-mxMQP_-BaSUc";
    private static final String USERNAME = "ncrp_blue_eyes_bot";
    private Map<String, BlackJack> userGames = new HashMap<>();

    public TelegramBot(DefaultBotOptions options) {
        super(options);
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public String getBotUsername() {
        return USERNAME;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() != null && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String userMessage = update.getMessage().getText().toLowerCase();
            String botResponse;

            //Новый пользователь. Todo 2 человека
            BlackJack game = userGames.computeIfAbsent(chatId, k -> new BlackJack());

            switch (userMessage) {
                case "/start":
                    botResponse = "Блэкджек! Используй /startgame для начала новой партии.";
                    break;
                case "/startgame":
                    game.startNewGame();
                    botResponse = "Игра началась!\nТвои карты: " + game.playerHandToString();
                    break;
                case "/hit":
                    if (game.isGameOver()) {
                        botResponse = "Партия уже окончена. Начни новую игру с помощью /startgame.";
                    } else {
                        botResponse = game.playerHit();
                        if (game.isGameOver()) {
                            botResponse += "\nПартия окончена. Начни новую игру с помощью /startgame.";
                        }
                    }
                    break;
                case "/stand":
                    if (game.isGameOver()) {
                        botResponse = "Партия уже окончена. Начни новую игру с помощью /startgame.";
                    } else {
                        botResponse = game.playerStand();
                        botResponse += "\nПартия окончена. Начни новую игру с помощью /startgame.";
                    }
                    break;
                default:
                    botResponse = "Неизвестная команда. Используйте /startgame для начала новой игры, /hit для взятия карты, или /stand для завершения хода.";
            }

            try {
                SendMessage message = new SendMessage(chatId, botResponse);
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
