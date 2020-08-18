package com.example.demo.orderEvent.impl;


import com.example.demo.entity.MatchOrder;
import com.example.demo.entity.OrderBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

@Component
public class GtcMatchImpl {

    public MatchOrder autoMatch(MatchOrder matchOrder , OrderBook orderBook){
        if (orderBook == null)
            return null;

        NavigableMap<BigDecimal, LinkedHashMap<Long,MatchOrder>> ackListMap = orderBook.ackList;//ack list
        TreeMap<BigDecimal, LinkedHashMap<Long,MatchOrder>> bidListMap = orderBook.bidList;//bin list
        if (ackListMap.size()<=0||bidListMap.size()<=0){

        }
        //1st step   order add to book
        BigDecimal takerPirce = matchOrder.getPrice();
        boolean isBid = matchOrder.isBID();
        if (isBid){
            LinkedHashMap linkedHashMap = bidListMap.get(takerPirce);
            if (linkedHashMap==null||linkedHashMap.size()==0){
                LinkedHashMap newPriceHash = new LinkedHashMap();
                newPriceHash.put(matchOrder.getCreateTime(),matchOrder);
                bidListMap.put(takerPirce,newPriceHash);
            }else {
                linkedHashMap.put(matchOrder.getCreateTime(),matchOrder);
            }
        }

        //2nd step  do match
        //price whether Cross
        BigDecimal lowestBidPrice  = bidListMap.firstKey();
        BigDecimal highestAckPrice = ackListMap.firstKey();
        if (lowestBidPrice.compareTo(highestAckPrice)>=0){
            //TODO 不撮合 更新深度账单 返回空单
            return null;
        }
        Iterator<NavigableMap.Entry<BigDecimal, LinkedHashMap<Long,MatchOrder>>> iteratorACKTemp = ackListMap.entrySet().iterator();
        Iterator<Map.Entry<BigDecimal, LinkedHashMap<Long,MatchOrder>>> iteratorBIDTemp = bidListMap.entrySet().iterator();
        if (isBid){
            bidLoop: while (iteratorBIDTemp.hasNext()){
                BigDecimal bidPrice = iteratorBIDTemp.next().getKey();
                LinkedHashMap<Long,MatchOrder> bidLinkedMap = iteratorBIDTemp.next().getValue();
                ackLoop: while (iteratorACKTemp.hasNext()){
                    BigDecimal ackPrice = iteratorACKTemp.next().getKey();
                    if (bidPrice.compareTo(ackPrice)<=0){
                          = getTotalNum(bidLinkedMap);
                    }

                }



            }
        }else {

        }
        return null;
    }


    /**
     *
     * @param linkedHashMap
     * @return
     */
    private BigDecimal getTotalNum(LinkedHashMap linkedHashMap){
        Iterator<Map.Entry<Long, MatchOrder>> iterator = linkedHashMap.entrySet().iterator();
        BigDecimal bigDecimal = BigDecimal.ZERO;
        while (iterator.hasNext()){
            MatchOrder matchOrder = iterator.next().getValue();
            bigDecimal = bigDecimal.add(matchOrder.getNumber());
        }
        return bigDecimal;
    }


}
