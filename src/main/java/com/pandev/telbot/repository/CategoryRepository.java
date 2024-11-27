package com.pandev.telbot.repository;

import com.pandev.telbot.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью Category.
 * Предоставляет стандартные CRUD-операции и дополнительные методы для работы с категориями.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Находит категорию по её названию.
     *
     * @param name Название категории.
     * @return Optional с категорией, если она найдена, или пустой Optional.
     */
    Optional<Category> findByName(String name);

    /**
     * Возвращает список всех категорий, подгружая связанные дочерние категории.
     * Используется аннотация @EntityGraph для оптимизации загрузки связанных данных.
     *
     * @return Список всех категорий с дочерними категориями.
     */
    @EntityGraph(attributePaths = {"children"})
    List<Category> findAll();

    /**
     * Удаляет указанную категорию из базы данных.
     *
     * @param category Категория для удаления.
     */
    void delete(Category category);
}
