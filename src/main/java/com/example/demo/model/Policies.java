package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Policies {
	
	private String vhost;
	private String name;
	private String pattern;
	@JsonProperty("apply-to")
	private String applyTo;
	@JsonProperty("definition")
	private Definition definition;
	private int priority;
	public String getVhost() {
		return vhost;
	}
	public void setVhost(String vhost) {
		this.vhost = vhost;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getApplyTo() {
		return applyTo;
	}
	public void setApplyTo(String applyTo) {
		this.applyTo = applyTo;
	}
	public Definition getDefiniton() {
		return definition;
	}
	public void setDefiniton(Definition definition) {
		this.definition = definition;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	@Override
	public String toString() {
		return "Policies [vhost=" + vhost + ", name=" + name + ", pattern=" + pattern + ", applyTo=" + applyTo
				+ ", definition=" + definition + ", priority=" + priority + "]";
	}
}
