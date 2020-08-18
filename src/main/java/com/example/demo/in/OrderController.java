package com.example.demo.in;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.disruptor.MessageProducer;
import com.example.demo.disruptor.RingBufferWorkerPoolFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/order")
public class OrderController {
    @RequestMapping("/add")
    public String addOrder(int type,double priceAdapter,int number){
        int num = 0;
        while (num<number){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", UUID.randomUUID().toString().replace("-",""));
            jsonObject.put("symbol","ETH_USDT");
            jsonObject.put("userCode","00007752");
            jsonObject.put("tradeType",type);
            jsonObject.put("price",1000+(1000*priceAdapter));
            jsonObject.put("number",1);
            jsonObject.put("time",new Date().getTime());


            MessageProducer producer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer("order");
            producer.onData(jsonObject, 1);
            num ++;
        }
        return "ok";
    }

}
