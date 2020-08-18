package com.example.demo.orderEvent;


import com.example.demo.enums.EnumOrderType;
import com.example.demo.util.SpringBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderEventContext {

    /**
     * 拿到执行器
     * @param sign
     * @return
     */
    public OrderEvent getInstace(int sign){
        Map<Integer, String> allClazz = EnumOrderType.getAllClazz();
        String clazz = allClazz.get(sign);
        OrderEvent orderEvent = null;
        if ( (clazz == null) || (clazz.isEmpty())){
            try {
                orderEvent = (OrderEvent) SpringBeanFactory.getBean(Class.forName(clazz));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return orderEvent;
    }

}
