package org.example.blackjack;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.example.blackjack.telegrambot.BlackJack;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
    private static final String TOKEN = "7786403468:AAEwhrybrtlTXhEj0fbC94-mxMQP_-BaSUc";
    private static final String USERNAME = "ncrp_blue_eyes_bot";

    private final Map<String, BlackJack> userGames = new HashMap<>();

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
            BlackJack game = userGames.computeIfAbsent(chatId, k -> new BlackJack());

            switch (userMessage) {
                case "/rules":
                    botResponse = "*Правила игры в Блэкджек*\n" +
                        "\n" +
                        "*1. Цель игры*  \n" +
                        "   - Набрать сумму очков, равную 21, или максимально близкую к ней, не превышая её.\n" +
                        "   - Победить дилера, у которого также есть цель набрать 21.\n" +
                        "\n" +
                        "*2. Правила начисления очков*\n" +
                        "   - Карты от 2 до 10 дают количество очков, равное их значению.\n" +
                        "   - Валет \\(J\\), Дама \\(Q\\) и Король \\(K\\) дают 10 очков.\n" +
                        "   - Туз \\(A\\) может дать 1 или 11 очков, в зависимости от выбора игрока, чтобы оптимально приблизиться к 21.\n" +
                        "\n" +
                        "*3. Начало игры*\n" +
                        "   - Игрок и дилер получают по две карты.\n" +
                        "   - Карты игрока видны ему сразу.\n" +
                        "   - Одна из карт дилера открыта, другая — закрыта.\n" +
                        "\n" +
                        "*4. Действия игрока*\n" +
                        "   - _Hit_: взять дополнительную карту.\n" +
                        "   - _Stand_: остановиться и не брать больше карт.\n" +
                        "   - Игрок может продолжать брать карты, пока не решит остановиться или не соберёт более 21 очка.\n" +
                        "\n" +
                        "*5. Окончание игры*\n" +
                        "   - Если игрок набирает более 21 очка, он проигрывает \\(перебор\\).\n" +
                        "   - После действий дилера определяется победитель, исходя из сравнений очков:\n" +
                        "     - Игрок выигрывает, если его количество очков ближе к 21, чем у дилера, или если у дилера происходит перебор.\n" +
                        "     - Дилер выигрывает, если его количество очков ближе к 21, чем у игрока.\n" +
                        "     - Если у игрока и дилера одинаковое количество очков, объявляется ничья.\n" +
                        "\n" +
                        "*6. Особые ситуации*\n" +
                        "   - *Blackjack*: если первые две карты игрока составляют ровно 21 \\(туз и карта с 10 очками\\), игрок сразу выигрывает, кроме случая, когда у дилера такой же Blackjack.\n" +
                        "\n" +
                        "Эти простые правила определяют основной ход игры в Блэкджек. Удачи за игровым столом!";
                    break;
                case "/start":
                    botResponse = "Блэкджек! Используй /startgame для начала новой партии.";
                    break;
                case "/startgame":
                    game.startNewGame();
                    botResponse = "Игра началась!\nТвои карты: " + game.playerHandToString();
                    break;
                case "/hit":
                    if (!game.playerHit() || game.isGameOver()) {
                        botResponse = getGameResult(game);
                    } else {
                        botResponse = "Ты взял карту: " + game.lastCardToString() + "\nРука: " + game.playerHandToString();
                    }
                    break;
                case "/stand":
                    game.playerStand();
                    botResponse = getGameResult(game);
                    break;
                default:
                    botResponse = "Неизвестная команда. Используйте /startgame для начала новой игры, /hit для взятия карты, или /stand для завершения хода.";
            }

            try {
                SendMessage message = new SendMessage(chatId, botResponse);
                execute(message);
            } catch (TelegramApiException e) {
                logger.error("Ошибка при отправке сообщения в чат ID: {}. Текст сообщения: '{}'", chatId, botResponse, e);

                try {
                    SendMessage errorMessage = new SendMessage(chatId, "Произошла ошибка при обработке команды. Попробуйте позже.");
                    execute(errorMessage);

                } catch (TelegramApiException nestedException) {
                    logger.error("Ошибка при отправке сообщения об ошибке в чат ID: {}", chatId, nestedException);
                }
            }
        }
    }

    private String getGameResult(BlackJack game) {
        int playerScore = game.getPlayerScore();
        int dealerScore = game.getDealerScore();
        String res = "Ваши карты: " + game.playerHandToString() + " (" + playerScore + ")\n" +
            "Карты дилера: " + game.dealerHandToString() + " (" + dealerScore + ")\n";
        if (game.isPlayerBust()) return res + "Ты проиграл (перебор)!";
        if (game.isDealerBust()) return res + "Дилер перебрал, ты выиграл!";
        if (playerScore > dealerScore) return res + "Ты выиграл!";
        if (playerScore < dealerScore) return res + "Ты проиграл!";
        return res + "Ничья!";
    }

    public void sendPacthNoteToUser(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}