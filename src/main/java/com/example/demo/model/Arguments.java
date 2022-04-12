package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Arguments {
	@JsonProperty("x-queue-type")
	private String x_queue_type;

	public String getX_queue_type() {
		return x_queue_type;
	}

	public void setX_queue_type(String x_queue_type) {
		this.x_queue_type = x_queue_type;
	}

	@Override
	public String toString() {
		return "Arguments [x_queue_type=" + x_queue_type + "]";
	}

}
