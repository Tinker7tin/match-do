package org.matcher.me.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
public class TradedOrder {
    private long buyOrderId;
    private long sellOrderId;
    private long price;
    private long amount;
    private long timestamp;

    public TradedOrder(long buyOrderId, long sellOrderId, long price, long amount) {
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.price = price;
        this.amount = amount;
        this.timestamp = System.currentTimeMillis();
    }
}
