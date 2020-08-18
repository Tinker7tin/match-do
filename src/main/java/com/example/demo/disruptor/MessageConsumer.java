package com.example.demo.disruptor;

import com.lmax.disruptor.WorkHandler;

/**
 * @author Alienware
 *
 */
public abstract class MessageConsumer implements WorkHandler<DisruptorData> {

	protected String consumerId;
	
	public MessageConsumer(String consumerId) {
		this.consumerId = consumerId;
	}

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}
	

}
