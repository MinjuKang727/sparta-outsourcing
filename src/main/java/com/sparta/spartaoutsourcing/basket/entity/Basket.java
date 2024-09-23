package com.sparta.spartaoutsourcing.basket.entity;


import com.sparta.spartaoutsourcing.menu.entity.Menu;
import com.sparta.spartaoutsourcing.store.entity.Store;
import com.sparta.spartaoutsourcing.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Basket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    private int quantity;

    public Basket(User user, Menu menu, Store store, int quantity) {
        this.user = user;
        this.menu = menu;
        this.store = store;
        this.quantity = quantity;
    }

    public void update(int quantity) {
        this.quantity = quantity;
    }
}
