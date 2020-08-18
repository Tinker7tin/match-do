package com.example.demo.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonUtil {

    /**
     * 对象转json字符串
     * @param objectMapper
     * @param obj
     * @return
     */
    public static String objectToJson(ObjectMapper objectMapper, Object obj) {
        String jsonStr = null;
        try {
            jsonStr = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }
}
