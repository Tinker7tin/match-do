package org.matcher.me.enums;

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

    private int status;
    private OrderStatus(int status) {
        this.status = status;
    }

    public static OrderStatus getStatus(int status) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.getStatus() == status) {
                return orderStatus;
            }
        }
        return null;
    }
}
