package org.order.me.model.enums;

import lombok.Getter;

@Getter
public enum OrderType{
    BUY(1),
    SELL(2);

    private int type;

    OrderType(int type) {
        this.type = type;
    }

    /**
     * 随机获得一个枚举
     */
    public static OrderType random() {
        int index = (int) (Math.random() * values().length);
        return values()[index];
    }
}
