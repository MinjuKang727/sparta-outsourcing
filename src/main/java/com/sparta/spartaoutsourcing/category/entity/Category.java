package com.sparta.spartaoutsourcing.category.entity;

import com.sparta.spartaoutsourcing.menu.entity.Menu;
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
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menus = new ArrayList<>(); ;

    public Category(String name) { this.name = name; }

    public void update(String name) { this.name = name; }

    public void setDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
}
