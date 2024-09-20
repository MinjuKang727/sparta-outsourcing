package com.sparta.spartaoutsourcing.domain.menu.entity;

import com.sparta.spartaoutsourcing.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public Menu(Store store, String menu, Long price) {
        this.store = store;
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
}