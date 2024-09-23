package com.sparta.spartaoutsourcing.option.entity;

import com.sparta.spartaoutsourcing.menu.entity.Menu;
import com.sparta.spartaoutsourcing.optionGroup.entity.OptionGroup;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class MenuOption {
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
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private OptionGroup optionGroup;

    public MenuOption(String name, Long price, OptionGroup optionGroup, Menu menu) {
        this.name = name;
        this.price = price;
        this.optionGroup = optionGroup;
        this.menu = menu;
    }

    public void setOptionGroup(OptionGroup optionGroup) { this.optionGroup = optionGroup; }

    public void setDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public void update(String name, Long price) {
        this.name = name;
        this.price = price;
    }
}
