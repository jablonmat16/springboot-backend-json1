package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Queues {
	private String name;
	private String durable;
	@JsonProperty("auto_delete")
	private String autoDelete;
	private Arguments arguments;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDurable() {
		return durable;
	}
	public void setDurable(String durable) {
		this.durable = durable;
	}
	public String getAutoDelete() {
		return autoDelete;
	}
	public void setAutoDelete(String autoDelete) {
		this.autoDelete = autoDelete;
	}
	public Arguments getArguments() {
		return arguments;
	}
	public void setArguments(Arguments arguments) {
		this.arguments = arguments;
	}
	@Override
	public String toString() {
		return "Queues [name=" + name + ", durable=" + durable + ", autoDelete=" + autoDelete + ", arguments="
				+ arguments + "]";
	}
}
