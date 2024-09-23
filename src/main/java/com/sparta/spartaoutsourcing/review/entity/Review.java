package com.sparta.spartaoutsourcing.review.entity;

import com.sparta.spartaoutsourcing.order.entity.Order;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modifiedAt;

    @Builder
    private Review(Order order, Integer rating, String content){
        this.rating = rating;
        this.content = content;
    }
}
