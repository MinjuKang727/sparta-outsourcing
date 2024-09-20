package com.sparta.spartaoutsourcing.domain.review.entity;

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

    @OneToOne(optional = true)
    @JoinColumn
    ReviewComment reviewComment;

    @Builder
    private Review(Integer rating, String content){
        this.rating = rating;
        this.content = content;
    }
}
