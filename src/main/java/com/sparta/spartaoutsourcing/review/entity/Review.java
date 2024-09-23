package com.sparta.spartaoutsourcing.review.entity;

import com.sparta.spartaoutsourcing.order.entity.Order;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Integer rating;

    @Column(nullable = false)
    String content;

    @OneToOne(optional = false)
    @JoinColumn
    Order order;

    @OneToOne(optional = true, mappedBy = "review")
    ReviewComment reviewComment;

    @Builder
    private Review(Order order, Integer rating, String content){
        this.rating = rating;
        this.content = content;
    }
}
