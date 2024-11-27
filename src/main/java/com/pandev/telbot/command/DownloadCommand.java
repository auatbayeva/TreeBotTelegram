package com.pandev.telbot.command;

import com.pandev.telbot.service.CategoryService;
import com.pandev.telbot.telegrambot.CategoryBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.ByteArrayInputStream;

/**
 * Команда для загрузки Excel-файла с деревом категорий.
 *
 * Эта команда требует взаимодействия с пользователем и не может быть выполнена с помощью текстовых команд.
 * Для выполнения команды необходимо отправить соответствующее сообщение боту.
 */
public class DownloadCommand implements Command {
    private final CategoryService categoryService;

    /**
     * Конструктор класса.
     *
     * @param categoryService Сервис для работы с категориями, используемый для генерации Excel-файла.
     */
    public DownloadCommand(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Этот метод не используется для данной команды, так как она требует взаимодействия с пользователем.
     *
     * @param args Массив аргументов команды (не используется).
     * @return Сообщение о том, что команда требует взаимодействия с пользователем.
     */
    @Override
    public String execute(String[] args) {
        return "This command requires user interaction and cannot be executed via text commands.";
    }

    /**
     * Выполняет команду загрузки Excel-файла.
     *
     * 1. Генерирует Excel-файл с деревом категорий, используя сервис CategoryService.
     * 2. Создает объект SendDocument для отправки файла.
     * 3. Устанавливает получателя, файл и описание файла.
     * 4. Отправляет файл пользователю через бота.
     *
     * @param update Объект, содержащий информацию о входящем сообщении.
     * @return Сообщение об успешной отправке файла или сообщение об ошибке.
     */
    @Override
    public String execute(Update update) {
        try {
            // Генерация Excel-файла
            byte[] fileBytes = categoryService.generateCategoryTreeExcel();

            // Создание документа для отправки
            InputFile inputFile = new InputFile(new ByteArrayInputStream(fileBytes), "categories.xlsx");
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(update.getMessage().getChatId());
            sendDocument.setDocument(inputFile);
            sendDocument.setCaption("Дерево категорий в Excel");

            // Отправляем файл через бота
            CategoryBot categoryBot = new CategoryBot(categoryService);
            categoryBot.execute(sendDocument); // Замените `yourBotInstance` на объект вашего бота

            return "Файл успешно отправлен.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при создании или отправке Excel-файла.";
        }
    }
}