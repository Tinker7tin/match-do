package com.example.demo.disruptor;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.MatchOrder;
import com.example.demo.orderEvent.OrderEvent;
import com.example.demo.orderEvent.OrderEventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DisruptorOrderConsumer {
    @Autowired
    private OrderEventContext orderEventContext;

    public DisruptorOrderConsumer() {
    }

    public void onEvent(DisruptorData disruptorData) throws Exception {

        //TODO 对应交易对线程是否开启

        //TODO 对应orderaciton
        JSONObject jsonObject = disruptorData.getJsonObject();
        MatchOrder matchOrder = new MatchOrder();
        matchOrder.setCode(jsonObject.getString("code"));
        matchOrder.setSymbol(jsonObject.getString("symbol"));
        matchOrder.setCreateTime(jsonObject.getLong("time"));
        matchOrder.setNumber(jsonObject.getBigDecimal("number"));
        matchOrder.setBID(jsonObject.getBoolean("isBID"));
        matchOrder.setPrice(jsonObject.getBigDecimal("price"));

        OrderEvent orderEvent = orderEventContext.getInstace(disruptorData.getType());
        orderEvent.event(matchOrder);



    }


}
