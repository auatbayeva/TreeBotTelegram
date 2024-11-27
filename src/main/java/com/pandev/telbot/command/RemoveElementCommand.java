package com.pandev.telbot.command;

import com.pandev.telbot.service.CategoryService;

/**
 * Команда для удаления элемента из дерева категорий.
 *
 * Поддерживает удаление как корневых, так и дочерних элементов.
 * При удалении элемента также удаляются все его дочерние элементы.
 */
public class RemoveElementCommand implements Command {
    private final CategoryService categoryService;

    /**
     * Конструктор класса.
     *
     * @param categoryService Сервис для работы с категориями.
     */
    public RemoveElementCommand(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Выполняет команду удаления элемента.
     *
     * Команда принимает один аргумент - имя элемента для удаления.
     * Если элемент найден, он удаляется вместе со всеми его дочерними элементами.
     *
     * @param args Массив аргументов команды.
     * @return Сообщение об успешном удалении или сообщение об ошибке.
     */
    @Override
    public String execute(String[] args) {
        if (args.length == 1) {
            String elementName = args[0];
            boolean result = categoryService.removeCategory(elementName);
            return result
                    ? "Element '" + elementName + "' removed along with its children."
                    : "Element '" + elementName + "' not found.";
        } else {
            return "Invalid number of arguments. Usage: /removeElement <element>";
        }
    }
}