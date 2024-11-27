package com.pandev.telbot.command;

import com.pandev.telbot.service.CategoryService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.InputStream;
/**
 * Команда не релизована
 *
 */
public class UploadCommand implements Command {
    private final CategoryService categoryService;

    public UploadCommand(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public String execute(String[] args) {
        return "Команда /upload должна быть вызвана с прикрепленным Excel-документом.";
    }

    @Override
    public String execute(Update update) {
        if (update.hasMessage() && update.getMessage().hasDocument()) {
            try {
                // Получение документа
                String fileId = update.getMessage().getDocument().getFileId();

                // Загрузка файла через Telegram API
                InputStream fileStream = categoryService.downloadFile(fileId); // Реализуйте метод downloadFile

                // Парсинг Excel-файла
                Workbook workbook = new XSSFWorkbook(fileStream);
                Sheet sheet = workbook.getSheetAt(0);

                for (Row row : sheet) {
                    // Считывание данных из строк (пример: первая колонка - название категории, вторая - родительская категория)
                    Cell categoryNameCell = row.getCell(0);
                    Cell parentCategoryCell = row.getCell(1);

                    if (categoryNameCell == null || categoryNameCell.getStringCellValue().isEmpty()) {
                        continue; // Пропускаем пустые строки
                    }

                    String categoryName = categoryNameCell.getStringCellValue();
                    String parentCategory = parentCategoryCell != null ? parentCategoryCell.getStringCellValue() : null;

                    // Сохранение в базе данных
                    categoryService.saveCategory(categoryName, parentCategory); // Реализуйте метод saveCategory
                }

                return "Файл успешно обработан и данные сохранены в базе.";
            } catch (Exception e) {
                e.printStackTrace();
                return "Ошибка при обработке файла: " + e.getMessage();
            }
        } else {
            return "Пожалуйста, прикрепите Excel-документ с деревом категорий.";
        }
    }
}
