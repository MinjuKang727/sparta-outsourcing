package com.sparta.spartaoutsourcing.order.entity;



public enum OrderState {

    REQUEST_ORDER("주문 요청"),
    ACCEPT_ORDER("주문 수락"),
    REJECT_ORDER("주문 거절"),
    DELIVERING("배달중"),
    DELIVERED("배달 완료"),;

    private final String state;

    OrderState(String state) {
        this.state = state;
    }

    public String getStateName() {
        return state;
    }

}
