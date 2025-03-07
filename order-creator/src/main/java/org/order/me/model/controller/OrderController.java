package org.order.me.model.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.order.me.model.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/create")
    public void createOrder(Integer size) {
        orderService.createOrder(size);
    }
}
