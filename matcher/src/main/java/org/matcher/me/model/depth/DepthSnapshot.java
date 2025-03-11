package org.matcher.me.model.depth;

import lombok.Getter;

import java.util.List;

@Getter
public class DepthSnapshot {

    // Getters
    private final List<PriceLevel> bids; // 买盘（价格降序）
    private final List<PriceLevel> asks; // 卖盘（价格升序）

    public DepthSnapshot(List<PriceLevel> bids, List<PriceLevel> asks) {
        this.bids = bids;
        this.asks = asks;
    }

}
