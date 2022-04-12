package com.example.demo.model;

public class Bindings {
	private String source;
	private String destination;
	private String destination_type;
	private String routing_key;
	private Arguments arguments;
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getDestination_type() {
		return destination_type;
	}
	public void setDestination_type(String destination_type) {
		this.destination_type = destination_type;
	}
	public String getRouting_key() {
		return routing_key;
	}
	public void setRouting_key(String routing_key) {
		this.routing_key = routing_key;
	}
	public Arguments getArguments() {
		return arguments;
	}
	public void setArguments(Arguments arguments) {
		this.arguments = arguments;
	}
	@Override
	public String toString() {
		return "Bindings [source=" + source + ", destination=" + destination + ", destination_type=" + destination_type
				+ ", routing_key=" + routing_key + ", arguments=" + arguments + "]";
	}
}
