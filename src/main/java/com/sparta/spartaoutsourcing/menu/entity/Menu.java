package com.sparta.spartaoutsourcing.menu.entity;

import com.sparta.spartaoutsourcing.category.entity.Category;
import com.sparta.spartaoutsourcing.optionGroup.entity.OptionGroup;
import com.sparta.spartaoutsourcing.store.entity.Store;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String menuName;

    @Column(nullable = false)
    private int price;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    
    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionGroup> optionGroups = new ArrayList<>();


    public Menu(Store store, Category category, String menu, int price) {
        this.store = store;
        this.category = category;
        this.menuName = menu;
        this.price = price;
    }

    public Menu(Store store, String menu, int price) {
        this.store = store;
        this.menuName = menu;
        this.price = price;
    }


    public void update(String menu, int price) {
        this.menuName = menu;
        this.price = price;
    }

    public void setDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setCategory(Category category) { this.category = category; }

}