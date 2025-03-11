package org.matcher.me;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.matcher.me.engine.GlobalDisruptor;
import org.matcher.me.engine.OrderBook;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InitProcess {

    @PostConstruct
    public void init() {
        String symbol = "BTC/USDT";
        OrderBook orderBook = new OrderBook(symbol);
        GlobalDisruptor.registerSymbol(symbol,orderBook);
        log.info("==========初始化构建订单簿【{}】========",symbol);
    }

}
