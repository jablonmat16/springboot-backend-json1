package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Definition {
	@JsonProperty("ha-mode")
	private String haMode;
	@JsonProperty("ha-params")
	private String haParams;
	@JsonProperty("ha-promote-on-failure")
	private String haPromoteOnFailure;
	@JsonProperty("ha-promote-on-shutdown")
	private String haPromoteOnShutdown;
	@JsonProperty("ha-sync-mode")
	private String haSyncMode;
	@JsonProperty("queue-mode")
	private String queueMode;
	public String getHaMode() {
		return haMode;
	}
	public void setHaMode(String haMode) {
		this.haMode = haMode;
	}
	public String getHaParams() {
		return haParams;
	}
	public void setHaParams(String haParams) {
		this.haParams = haParams;
	}
	public String getHaPromoteOnFailure() {
		return haPromoteOnFailure;
	}
	public void setHaPromoteOnFailure(String haPromoteOnFailure) {
		this.haPromoteOnFailure = haPromoteOnFailure;
	}
	public String getHaPromoteOnShutdown() {
		return haPromoteOnShutdown;
	}
	public void setHaPromoteOnShutdown(String haPromoteOnShutdown) {
		this.haPromoteOnShutdown = haPromoteOnShutdown;
	}
	public String getHaSyncMode() {
		return haSyncMode;
	}
	public void setHaSyncMode(String haSyncMode) {
		this.haSyncMode = haSyncMode;
	}
	public String getQueueMode() {
		return queueMode;
	}
	public void setQueueMode(String queueMode) {
		this.queueMode = queueMode;
	}
	@Override
	public String toString() {
		return "Definition [haMode=" + haMode + ", haParams=" + haParams + ", haPromoteOnFailure=" + haPromoteOnFailure
				+ ", haPromoteOnShutdown=" + haPromoteOnShutdown + ", haSyncMode=" + haSyncMode + ", queueMode="
				+ queueMode + "]";
	}
}
