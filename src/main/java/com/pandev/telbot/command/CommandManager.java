package com.pandev.telbot.command;

import com.pandev.telbot.service.CategoryService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

/**
 * Менеджер команд для Telegram-бота.
 *
 * Этот класс отвечает за регистрацию и выполнение команд, которые пользователь может отправить боту.
 * Он поддерживает два способа выполнения команд:
 *  - **Использование массива аргументов:** Для простых команд с фиксированным набором аргументов.
 *  - **Использование объекта `Update`:** Для более сложных команд, требующих доступа к полной информации о входящем сообщении.
 */
public class CommandManager {
    /**
     * Хранилище для зарегистрированных команд.
     * Ключ - имя команды (например, "/viewTree"), значение - экземпляр класса, реализующего интерфейс Command.
     */
    private final Map<String, Command> commands = new HashMap<>();

    /**
     * Конструктор класса.
     *
     * Инициализирует хранилище команд и регистрирует доступные команды.
     *
     * @param categoryService Сервис для работы с категориями.
     */
    public CommandManager(CategoryService categoryService) {
        commands.put("/viewTree", new ViewTreeCommand(categoryService));
        commands.put("/addElement", new AddElementCommand(categoryService));
        commands.put("/removeElement", new RemoveElementCommand(categoryService));
        commands.put("/help", new HelpCommand());
        commands.put("/download", new DownloadCommand(categoryService));
    }

    /**
     * Выполняет команду, используя массив аргументов.
     *
     * @param commandName Имя команды.
     * @param args Массив аргументов команды.
     * @return Результат выполнения команды или сообщение об ошибке, если команда не найдена.
     */
    public String executeCommand(String commandName, String[] args) {
        Command command = commands.get(commandName);
        if (command != null) {
            return command.execute(args);
        } else {
            return "Неизвестная команда. Введите /help для получения списка доступных команд.";
        }
    }

    /**
     * Выполняет команду, используя объект Update.
     *
     * @param commandName Имя команды.
     * @param update Объект, содержащий информацию о входящем сообщении.
     * @return Результат выполнения команды или сообщение об ошибке, если команда не найдена.
     */
    public String executeCommand(String commandName, Update update) {
        Command command = commands.get(commandName);
        if (command != null) {
            return command.execute(update);
        } else {
            return "Неизвестная команда. Введите /help для получения списка доступных команд.";
        }
    }
}