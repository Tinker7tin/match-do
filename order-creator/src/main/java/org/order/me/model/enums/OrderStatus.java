package org.order.me.model.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    /**
     * UNFILLED = 1;      // 未成交
     *   PARTIAL_FILL = 2;  // 部分成交
     *   FULLY_FILLED = 3;  // 完全成交
     *   CANCELLED = 4;     // 撤销
     */
    UNFILLED(1),
    PARTIAL_FILL(2),
    FULLY_FILLED(3),
    CANCELLED(4);

    private final int status;
    private OrderStatus(int status) {
        this.status = status;
    }

    /**
     * 随机获得一个枚举
     */
    public static OrderStatus random() {
        int index = (int) (Math.random() * values().length);
        return values()[index];
    }
}
