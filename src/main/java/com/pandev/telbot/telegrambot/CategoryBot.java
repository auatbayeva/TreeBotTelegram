package com.pandev.telbot.telegrambot;

import com.pandev.telbot.command.CommandManager;
import com.pandev.telbot.service.CategoryService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

/**
 * Telegram-бот для работы с категориями.
 * Расширяет TelegramLongPollingBot для обработки входящих сообщений и выполнения команд.
 */
@Component
public class CategoryBot extends TelegramLongPollingBot {

    private final CommandManager commandManager; // Управляет выполнением команд.

    /**
     * Конструктор класса. Устанавливает связь с CategoryService и инициализирует CommandManager.
     *
     * @param categoryService Сервис для работы с категориями.
     */
    public CategoryBot(CategoryService categoryService) {
        categoryService.setCategoryBot(this); // Передаем бота в CategoryService для интеграции.
        this.commandManager = new CommandManager(categoryService); // Инициализируем менеджер команд.
    }

    /**
     * Метод, вызываемый при получении обновления (сообщения или команды) в Telegram.
     *
     * @param update Объект Update, содержащий данные о новом событии (например, сообщение от пользователя).
     */
    @Override
    public void onUpdateReceived(Update update) {
        // Проверяем, есть ли текстовое сообщение.
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText(); // Получаем текст сообщения.
            String[] parts = message.split(" ", 2); // Разделяем на команду и аргументы.
            String commandName = parts[0]; // Имя команды.
            String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0]; // Аргументы команды.

            String response;
            // Особая обработка команды /download, где передается объект Update.
            if ("/download".equals(commandName)) {
                response = commandManager.executeCommand(commandName, update);
            } else {
                response = commandManager.executeCommand(commandName, args);
            }
            sendMessage(update.getMessage().getChatId(), response); // Отправляем ответ пользователю.
        }
    }

    /**
     * Отправляет сообщение в Telegram-чат.
     *
     * @param chatId Идентификатор чата.
     * @param text   Текст сообщения.
     */
    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString()); // Устанавливаем ID чата.
        message.setText(text); // Устанавливаем текст сообщения.
        try {
            execute(message); // Отправляем сообщение через Telegram API.
        } catch (TelegramApiException e) {
            e.printStackTrace(); // Логируем ошибку, если отправка не удалась.
        }
    }

    /**
     * Возвращает имя бота, зарегистрированное в Telegram.
     *
     * @return Имя бота.
     */
    @Override
    public String getBotUsername() {
        return "YourUsername"; // Указанное имя бота.
    }

    /**
     * Возвращает токен бота, необходимый для работы с Telegram API.
     *
     * @return Токен бота.
     */
    @Override
    public String getBotToken() {
        return "YourToken"; // Токен, выданный Telegram.
    }
}
