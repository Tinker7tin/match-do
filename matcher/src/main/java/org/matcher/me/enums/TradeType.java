package org.matcher.me.enums;

import lombok.Getter;

@Getter
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

    public static TradeType getEnum(int type) {
        for (TradeType tradeType : TradeType.values()) {
            if (tradeType.getType() == type) {
                return tradeType;
            }
        }
        return null;
    }
}
