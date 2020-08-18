package com.example.demo.entity;


import java.math.BigDecimal;


public class MatchOrder {

    private String code;
    private String uid;
    /**
     * 交易对
     */
    private String symbol;
    /**
     *  BID true
     *  ACK false
     */
    private boolean isBID ;
    /**
     * 单价
     */
    private BigDecimal price = BigDecimal.ZERO;
    /**
     * 数量
     */
    private BigDecimal number = BigDecimal.ZERO;
    /**
     * 创建时间
     */
    private long createTime = 0;

    private boolean delflg = false;	// 订单删除标志，撤单时不用立刻从列表里面摘除







    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public boolean isBID() {
        return isBID;
    }

    public void setBID(boolean BID) {
        isBID = BID;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getNumber() {
        return number;
    }

    public void setNumber(BigDecimal number) {
        this.number = number;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isDelflg() {
        return delflg;
    }

    public void setDelflg(boolean delflg) {
        this.delflg = delflg;
    }
}
