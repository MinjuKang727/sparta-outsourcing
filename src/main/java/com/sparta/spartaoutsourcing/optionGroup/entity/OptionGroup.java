package com.sparta.spartaoutsourcing.optionGroup.entity;

import com.sparta.spartaoutsourcing.menu.entity.Menu;
import com.sparta.spartaoutsourcing.option.entity.MenuOption;
import com.sparta.spartaoutsourcing.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class OptionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)  // menu_id는 필수
    private Menu menu;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; // 소유자 정보

    @OneToMany(mappedBy = "optionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuOption> menuOptions = new ArrayList<>();

    public OptionGroup(String name, Menu menu, User owner) {
        this.name = name;
        this.menu = menu; // menu 설정
        this.owner = owner;
    }

    public OptionGroup(String name, Menu menu, User owner, Long id) {
        this.name = name;
        this.menu = menu;
        this.owner = owner;
        this.id = id; // 생성자에서 ID 설정
    }

    public OptionGroup(String name) {
        this.name = name;
    }

    // menu 설정 메서드 추가
    public void setMenu(Menu menu) { this.menu = menu; }

    public void setDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public void update(String name) { this.name = name; }
}
