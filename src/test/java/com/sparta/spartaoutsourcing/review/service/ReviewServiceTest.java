package com.sparta.spartaoutsourcing.review.service;

import com.sparta.spartaoutsourcing.order.entity.Order;
import com.sparta.spartaoutsourcing.order.entity.OrderState;
import com.sparta.spartaoutsourcing.order.repository.OrderRepository;
import com.sparta.spartaoutsourcing.review.entity.Review;
import com.sparta.spartaoutsourcing.review.repository.ReviewRepository;
import com.sparta.spartaoutsourcing.store.repository.StoreRepository;
import com.sparta.spartaoutsourcing.user.entity.User;
import com.sparta.spartaoutsourcing.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
    @InjectMocks
    ReviewService reviewService;

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    StoreRepository storeRepository;

    @Mock
    OrderRepository orderRepository;

    @Nested
    @DisplayName("ReviewService::createReview")
    class Test1 {
        @Test
        @DisplayName("이미 리뷰를 작성했으면 예외 발생")
        void test1() {
            // given
            Long orderId = 1L;
            given(reviewRepository.existsByOrder_Id(orderId)).willReturn(true);

            // when & then
            ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> reviewService.createReview(orderId, 1L, 1, ""));
            assertEquals(responseStatusException.getStatusCode(), HttpStatus.CONFLICT);
            assertEquals(responseStatusException.getReason(), "이미 리뷰를 작성하였습니다.");
        }

        @Test
        @DisplayName("주문이 없으면 예외 발생")
        void test2() {
            // given
            Long orderId = 1L;
            given(reviewRepository.existsByOrder_Id(orderId)).willReturn(false);
            given(orderRepository.findById(orderId)).willReturn(Optional.empty());

            // when & then
            ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> reviewService.createReview(orderId, 1L, 1, ""));
            assertEquals(responseStatusException.getStatusCode(), HttpStatus.UNPROCESSABLE_ENTITY);
            assertEquals(responseStatusException.getReason(), "해당하는 주문이 없습니다.");
        }

        @Test
        @DisplayName("주문자와 작성자가 다르면 예외 발생")
        void test3() {
            // given
            Long orderId = 1L;
            Long userId = 2L;
            Order order = new Order();
            User user = new User();
            ReflectionTestUtils.setField(user, "id", userId);
            ReflectionTestUtils.setField(order, "user", user);
            given(reviewRepository.existsByOrder_Id(orderId)).willReturn(false);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when & then
            ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> reviewService.createReview(orderId, 1L, 1, ""));
            assertEquals(responseStatusException.getStatusCode(), HttpStatus.FORBIDDEN);
            assertEquals(responseStatusException.getReason(), "자신이 한 주문에만 리뷰를 달 수 있습니다.");
        }

        @Test
        @DisplayName("배달 완료되지 않았으면 예외 발생")
        void test4() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            Order order = new Order();
            User user = new User();
            ReflectionTestUtils.setField(user, "id", userId);
            ReflectionTestUtils.setField(order, "user", user);
            ReflectionTestUtils.setField(order, "state", OrderState.REQUEST_ORDER);
            given(reviewRepository.existsByOrder_Id(orderId)).willReturn(false);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when & then
            ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> reviewService.createReview(orderId, 1L, 1, ""));
            assertEquals(responseStatusException.getStatusCode(), HttpStatus.CONFLICT);
            assertEquals(responseStatusException.getReason(), "배달이 완료되어야지 리뷰를 작성할 수 있습니다.");
        }

        @Test
        @DisplayName("리뷰 작성이 정상적으로 처리됨")
        void test5() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            Integer rating = 4;
            String content = "컨텐츠";
            Order order = new Order();
            User user = new User();
            ReflectionTestUtils.setField(user, "id", userId);
            ReflectionTestUtils.setField(order, "user", user);
            ReflectionTestUtils.setField(order, "state", OrderState.DELIVERED);
            given(reviewRepository.existsByOrder_Id(orderId)).willReturn(false);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(reviewRepository.save(any())).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

            // when
            Review review = reviewService.createReview(orderId, userId, rating, content);

            // then
            assertEquals(review.getRating(), rating);
            assertEquals(review.getContent(), content);
        }
    }

    @Nested
    @DisplayName("ReviewService::getStoreReviews")
    class Test2 {
        @Test
        @DisplayName("가게가 없으면 예외 발생")
        void test1() {
            // given
            Long storeId = 1L;
            given(storeRepository.existsById(storeId)).willReturn(false);

            // when & then
            ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> reviewService.getStoreReviews(storeId, 0, 5));
            assertEquals(responseStatusException.getStatusCode(), HttpStatus.NOT_FOUND);
            assertEquals(responseStatusException.getReason(), "해당하는 가게가 존재하지 않습니다.");
        }

        @Test
        @DisplayName("가게 리뷰가 조회된다.")
        void test2() {
            // given
            Long storeId = 1L;
            Integer min = 0;
            Integer max = 5;
            given(storeRepository.existsById(storeId)).willReturn(true);
            given(orderRepository.findByStore_IdAndReviewIsNotNullAndReview_RatingBetween(storeId, min, max)).willReturn(List.of());

            // when
            List<Review> reviews = reviewService.getStoreReviews(storeId, min, max);

            assertEquals(reviews.size(), 0);
        }
    }
}
