package org.order.me.model.enums;

public enum TradeType {
    /**
     * 限价
     */
    LIMIT(1),
    /**
     * 市价
     */
    MARKET(2);

    private int type;

    TradeType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    /**
     * 随机获得一个枚举
     */
    public static TradeType random() {
        int index = (int) (Math.random() * TradeType.values().length);
        return TradeType.values()[index];
    }
}
