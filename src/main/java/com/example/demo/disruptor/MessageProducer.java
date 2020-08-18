package com.example.demo.disruptor;

import com.alibaba.fastjson.JSONObject;
import com.lmax.disruptor.RingBuffer;

import java.util.Map;

public class MessageProducer {

	private String producerId;

	private RingBuffer<DisruptorData> ringBuffer;
	
	public MessageProducer(String producerId, RingBuffer<DisruptorData> ringBuffer) {
		this.producerId = producerId;
		this.ringBuffer = ringBuffer;
	}
	
	public void onData(JSONObject object, int type) {
		long sequence = ringBuffer.next();
		try {
			DisruptorData wapper = ringBuffer.get(sequence);
			wapper.setType(type);
			wapper.setJsonObject(object);
		} finally {
			ringBuffer.publish(sequence);
		}
	}

}
