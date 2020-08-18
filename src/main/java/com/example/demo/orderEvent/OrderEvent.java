package com.example.demo.orderEvent;


import com.example.demo.entity.MatchOrder;

public interface OrderEvent {

    Object event(MatchOrder matchOrder);

}
