package org.matcher.me.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.matcher.me.enums.OrderStatus;
import org.matcher.me.enums.OrderType;
import org.matcher.me.enums.TradeType;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEntity {

    /**
     * 订单ID
     */
    private long orderId;
    /**
     * 交易对
     */
    private String symbol;
    /**
     * 交易类型 1买 2卖
     */
    private OrderType type;
    /**
     * 价格
     */
    private long price;
    /**
     * 交易数量
     */
    private long amount;
    /**
     * 未成交数量
     */
    private long remaining;
    /**
     * 交易类型  1限价 2市价
     */
    private TradeType tradeType;
    /**
     * 交易状态（0 未成交 1 部分成交 2完全成交 3撤销）
     */
    private OrderStatus status;
}
