package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Exchanges {
	
	private String name;
	private String type;
	private String durable;
	@JsonProperty("auto_delete")
	private String autoDelete;
	private String internal;
	private Arguments arguments;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public String getInternal() {
		return internal;
	}
	public void setInternal(String internal) {
		this.internal = internal;
	}
	public Arguments getArguments() {
		return arguments;
	}
	public void setArguments(Arguments arguments) {
		this.arguments = arguments;
	}
	@Override
	public String toString() {
		return "Exchanges [name=" + name + ", type=" + type + ", durable=" + durable + ", autoDelete=" + autoDelete
				+ ", internal=" + internal + ", arguments=" + arguments + "]";
	}
}
