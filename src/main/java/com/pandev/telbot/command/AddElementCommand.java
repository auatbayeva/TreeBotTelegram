package com.pandev.telbot.command;
import com.pandev.telbot.service.CategoryService;
/**
 * Команда для добавления элементов в категорию.
 *
 * Поддерживает два режима работы:
 *  - Добавление корневого элемента: /addElement <elementName>
 *  - Добавление дочернего элемента: /addElement <parentName> <childName>
 */

public class AddElementCommand implements Command {

    private final CategoryService categoryService;

    /**
     * Конструктор класса.
     *
     * @param categoryService Сервис для работы с категориями.
     */
    public AddElementCommand(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Выполняет команду добавления элемента.
     *
     * @param args Массив аргументов команды.
     * @return Строка с результатом выполнения команды.
     */
    @Override
    public String execute(String[] args) {
        if (args.length == 1) {
            // Добавляем корневой элемент
            String elementName = args[0];
            categoryService.addCategory(elementName);
            return "Добавлен корневой элемент '" + elementName;
        } else if (args.length == 2) {
            // Добавляем дочерний элемент
            String parentName = args[0];
            String childName = args[1];
            boolean result = categoryService.addChild(parentName, childName);
            return result
                    ? "Дочерний элемент '" + childName + "' добавлен к родительскому '" + parentName + "'."
                    : "Родительский элемент '" + parentName + "' не найден.";
        } else {
            return "Неверное количество аргументов. Использование: /addElement <parent> <child> или /addElement <element>";
        }
    }
}
