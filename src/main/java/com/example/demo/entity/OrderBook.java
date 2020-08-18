package com.example.demo.entity;



import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class OrderBook {

    //类主键
    public String symbol;
    //市场名称
    public String marketName = null;
    //币种名称
    public String coinName = null;
    //市场Code
    public String marketCode = null;
    //币种Code
    public String coinCode = null;
    //卖盘队列
    public TreeMap<BigDecimal, LinkedHashMap<Long,MatchOrder>> bidList = null;
    //买盘队列 (原始)
    public TreeMap<BigDecimal, LinkedHashMap<Long,MatchOrder>> originalAckList = null;
    //买盘队列 (对应)
    public NavigableMap<BigDecimal, LinkedHashMap<Long,MatchOrder>> ackList = originalAckList==null ?null:originalAckList.descendingMap();


    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getMarketCode() {
        return marketCode;
    }

    public void setMarketCode(String marketCode) {
        this.marketCode = marketCode;
    }

    public String getCoinCode() {
        return coinCode;
    }

    public void setCoinCode(String coinCode) {
        this.coinCode = coinCode;
    }

    public TreeMap<BigDecimal, LinkedHashMap<Long, MatchOrder>> getBidList() {
        return bidList;
    }

    public void setBidList(TreeMap<BigDecimal, LinkedHashMap<Long, MatchOrder>> bidList) {
        this.bidList = bidList;
    }

    public TreeMap<BigDecimal, LinkedHashMap<Long, MatchOrder>> getOriginalAckList() {
        return originalAckList;
    }

    public void setOriginalAckList(TreeMap<BigDecimal, LinkedHashMap<Long, MatchOrder>> originalAckList) {
        this.originalAckList = originalAckList;
    }

    public NavigableMap<BigDecimal, LinkedHashMap<Long, MatchOrder>> getAckList() {
        return ackList;
    }

    public void setAckList(NavigableMap<BigDecimal, LinkedHashMap<Long, MatchOrder>> ackList) {
        this.ackList = ackList;
    }
}
