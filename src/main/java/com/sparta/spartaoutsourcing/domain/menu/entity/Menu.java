package com.sparta.spartaoutsourcing.domain.menu.entity;

import com.sparta.spartaoutsourcing.domain.category.entity.Category;
import com.sparta.spartaoutsourcing.domain.optionGroup.entity.OptionGroup;
import com.sparta.spartaoutsourcing.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

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


    public Menu(Store store, Category category, String menu, Long price) {
        this.store = store;
        this.category = category;
        this.name = menu;
        this.price = price;
    }


    public void update(String menu, Long price) {
        this.name = menu;
        this.price = price;
    }

    public void setDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setCategory(Category category) { this.category = category; }

}