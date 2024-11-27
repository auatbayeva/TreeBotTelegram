package com.pandev.telbot.command;

import com.pandev.telbot.model.Category;
import com.pandev.telbot.service.CategoryService;

import java.util.List;

/**
 * Команда для отображения дерева категорий.
 *
 * При выполнении этой команды бот возвращает текстовое представление дерева категорий,
 * отформатированное с использованием отступов для визуализации иерархии.
 */
public class ViewTreeCommand implements Command {
    private final CategoryService categoryService;

    /**
     * Конструктор класса.
     *
     * @param categoryService Сервис для работы с категориями.
     */
    public ViewTreeCommand(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Выполняет команду отображения дерева категорий.
     *
     * 1. Получает дерево категорий из сервиса.
     * 2. Форматирует дерево в текстовое представление с использованием отступов.
     * 3. Возвращает отформатированное представление дерева.
     *
     * @param args Массив аргументов команды (не используется).
     * @return Текстовое представление дерева категорий.
     */
    @Override
    public String execute(String[] args) {
        List<Category> tree = categoryService.viewTree();
        StringBuilder sb = new StringBuilder("Категории:\n");
        for (Category category : tree) {
            sb.append(formatCategory(category, 0));
        }
        return sb.toString();
    }

    /**
     * Рекурсивно форматирует категорию и ее дочерние категории с использованием отступов.
     *
     * @param category Категория для форматирования.
     * @param level Уровень вложенности категории.
     * @return Отформатированное представление категории и ее дочерних категорий.
     */
    private String formatCategory(Category category, int level) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ".repeat(level)).append("- ").append(category.getName()).append("\n");
        for (Category child : category.getChildren()) {
            sb.append(formatCategory(child, level + 1));
        }
        return sb.toString();
    }
}