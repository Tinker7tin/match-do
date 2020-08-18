package com.example.demo.disruptor;



import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class DisruptorData {
    private int type;
    private JSONObject jsonObject;

    public DisruptorData() {
    }

    public DisruptorData(int type, JSONObject jsonObject) {
        this.type = type;
        this.jsonObject = jsonObject;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
