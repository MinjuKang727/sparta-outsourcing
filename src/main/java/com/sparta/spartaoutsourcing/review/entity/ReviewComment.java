package com.sparta.spartaoutsourcing.review.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@NoArgsConstructor
@Getter
public class ReviewComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    Long id;

    @OneToOne(optional = false, mappedBy = "reviewComment")
    Review review;

    @Column(nullable = false)
    String content;

    @Builder
    private ReviewComment(Review review, String content) {
        this.review = review;
        this.content = content;
    }
}
