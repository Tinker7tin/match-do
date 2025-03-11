package org.matcher.me.model.depth;

import lombok.Getter;

@Getter
public class PriceLevel {
    // Getters
    private final double price;   // 价格
    private final double quantity; // 总数量

    public PriceLevel(double price, double quantity) {
        this.price = price;
        this.quantity = quantity;
    }

}
