package com.pandev.telbot.service;

import com.pandev.telbot.model.Category;
import com.pandev.telbot.repository.CategoryRepository;
import com.pandev.telbot.telegrambot.CategoryBot;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с категориями.
 * Предоставляет методы для выполнения CRUD-операций, управления иерархией категорий
 * и генерации отчетов в формате Excel. Также включает интеграцию с Telegram Bot API.
 */
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private CategoryBot categoryBot; // Интеграция с Telegram Bot

    /**
     * Конструктор сервиса.
     *
     * @param categoryRepository Репозиторий для работы с сущностью Category.
     */
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Возвращает список всех категорий с их дочерними элементами.
     *
     * @return Список всех категорий.
     */
    public List<Category> viewTree() {
        return categoryRepository.findAll();
    }

    /**
     * Добавляет новую категорию в базу данных.
     *
     * @param name Название новой категории.
     */
    public void addCategory(String name) {
        Category category = new Category(name);
        categoryRepository.save(category);
    }

    /**
     * Добавляет дочернюю категорию к родительской.
     *
     * @param parentName Название родительской категории.
     * @param childName  Название дочерней категории.
     * @return true, если добавление успешно; false, если родительская категория не найдена.
     */
    public boolean addChild(String parentName, String childName) {
        Optional<Category> parent = categoryRepository.findByName(parentName);
        if (parent.isPresent()) {
            Category child = new Category(childName);
            child.setParent(parent.get());
            categoryRepository.save(child);
            return true;
        }
        return false;
    }

    /**
     * Удаляет категорию по её названию.
     *
     * @param name Название категории для удаления.
     * @return true, если категория успешно удалена; false, если категория не найдена.
     */
    public boolean removeCategory(String name) {
        Optional<Category> category = categoryRepository.findByName(name);
        if (category.isPresent()) {
            categoryRepository.delete(category.get());
            return true;
        }
        return false;
    }

    /**
     * Находит категорию по её названию. Если категория не найдена, выбрасывается исключение.
     *
     * @param name Название категории.
     * @return Найденная категория.
     */
    @Transactional
    public Category findCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    /**
     * Генерирует Excel-файл с иерархией категорий.
     *
     * @return Байтовый массив с данными Excel-файла.
     * @throws IOException Если возникает ошибка при записи данных в файл.
     */
    public byte[] generateCategoryTreeExcel() throws IOException {
        List<Category> categories = categoryRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Categories");

            // Создаем заголовки столбцов
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Category Name");
            headerRow.createCell(1).setCellValue("Parent Name");

            // Заполняем данные о категориях
            int rowNum = 1;
            for (Category category : categories) {
                rowNum = addCategoryToSheet(sheet, category, rowNum, null);
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * Рекурсивно добавляет категорию и её дочерние элементы в Excel-лист.
     *
     * @param sheet       Лист Excel-документа.
     * @param category    Категория для добавления.
     * @param rowNum      Номер текущей строки.
     * @param parentName  Название родительской категории.
     * @return Следующий номер строки.
     */
    private int addCategoryToSheet(Sheet sheet, Category category, int rowNum, String parentName) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(category.getName());
        row.createCell(1).setCellValue(parentName != null ? parentName : "Root");

        for (Category child : category.getChildren()) {
            rowNum = addCategoryToSheet(sheet, child, rowNum, category.getName());
        }
        return rowNum;
    }

    /**
     * Сохраняет категорию в базу данных с указанием родительской категории.
     *
     * @param categoryName Название новой категории.
     * @param parentCategory Название родительской категории.
     */
    public void saveCategory(String categoryName, String parentCategory) {
        Category category = new Category();
        category.setName(categoryName);
        if (parentCategory != null) {
            Optional<Category> parent = categoryRepository.findByName(parentCategory);
            category.setParent(parent.get());
        }
        categoryRepository.save(category);
    }

    /**
     * Устанавливает объект CategoryBot для работы с Telegram Bot API.
     *
     * @param categoryBot Экземпляр CategoryBot.
     */
    public void setCategoryBot(CategoryBot categoryBot) {
        this.categoryBot = categoryBot;
    }

    /**
     * Скачивает файл из Telegram по указанному fileId.
     *
     * @param fileId Уникальный идентификатор файла в Telegram.
     * @return Поток данных файла.
     * @throws TelegramApiException Если возникает ошибка при работе с Telegram API.
     * @throws IOException Если возникает ошибка при скачивании файла.
     */
    public InputStream downloadFile(String fileId) throws TelegramApiException, IOException {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        File file = categoryBot.execute(getFile);

        String fileUrl = "https://api.telegram.org/file/bot" + categoryBot.getBotToken() + "/" + file.getFilePath();
        HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
        return connection.getInputStream();
    }
}
