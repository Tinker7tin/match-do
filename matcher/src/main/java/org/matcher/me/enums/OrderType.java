package org.matcher.me.enums;

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
     * 获得对应枚举
     */
    public static OrderType getEnum(int type) {
        for (OrderType orderType : OrderType.values()) {
            if (orderType.getType() == type) {
                return orderType;
            }
        }
        return null;
    }
}
