package com.pandev.telbot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Сущность, представляющая категорию.
 * Категория может иметь иерархическую структуру с родительскими и дочерними отношениями.
 * Каждая категория имеет уникальный идентификатор, название, родительскую категорию и список дочерних категорий.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    /**
     * Уникальный идентификатор категории.
     * Генерируется автоматически с использованием стратегии IDENTITY.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название категории.
     */
    private String name;

    /**
     * Родительская категория для текущей категории.
     * Используется для построения иерархических связей.
     */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    /**
     * Список дочерних категорий, связанных с этой категорией.
     * Связь управляется каскадными операциями, используется ленивый (LAZY) режим загрузки.
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Category> children = new ArrayList<>();

    /**
     * Конструктор для создания категории с заданным названием.
     *
     * @param name Название категории.
     */
    public Category(String name) {
        this.name = name;
    }

    /**
     * Устанавливает родительскую категорию для текущей категории.
     *
     * @param parent Родительская категория.
     */
    public void setParent(Category parent) {
        this.parent = parent;
    }
}
