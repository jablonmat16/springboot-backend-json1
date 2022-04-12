package com.example.demo.model;

import java.io.Serializable;
import java.util.Arrays;

//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Json{
	
	//@Id
	@JsonProperty("rabbit_version")
	private String rabbitVersion;
	private String[] parameters;
	private Policies[] policies;
	private Queues[] queues;
	private Exchanges[] exchanges;
	private Bindings[] bindings;
	
//	public Json() {
//		
//	}
//	
//	public Json(String rabbitVersion, String parameters) {
//		super();
//		this.rabbitVersion = rabbitVersion;
//		this.parameters = parameters;
//	}

	public String getRabbitVersion() {
		return rabbitVersion;
	}
	public void setRabbitVersion(String rabbitVersion) {
		this.rabbitVersion = rabbitVersion;
	}
	public String[] getParameters() {
		return parameters;
	}
	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}
	public Policies[] getPolicies() {
		return policies;
	}
	public void setPolicies(Policies[] policies) {
		this.policies = policies;
	}
	public Queues[] getQueues() {
		return queues;
	}
	public void setQueues(Queues[] queues) {
		this.queues = queues;
	}
	public Exchanges[] getExchanges() {
		return exchanges;
	}
	public void setExchanges(Exchanges[] exchanges) {
		this.exchanges = exchanges;
	}
	public Bindings[] getBindings() {
		return bindings;
	}
	public void setBindings(Bindings[] bindings) {
		this.bindings = bindings;
	}
	@Override
	public String toString() {
		return "Json [rabbitVersion=" + rabbitVersion + ", parameters=" + Arrays.toString(parameters) + ", policies="
				+ Arrays.toString(policies) + ", queues=" + Arrays.toString(queues) + ", exchanges="
				+ Arrays.toString(exchanges) + ", bindings=" + Arrays.toString(bindings) + "]";
	}
	
//	public Json() {
//		
//	}
//	public Json(String rabbitVersion, String[] parameters, Policies[] policies, Queues[] queues, Exchanges[] exchanges,
//			String[] bindings) {
//		super();
//		this.rabbitVersion = rabbitVersion;
//		this.parameters = parameters;
//		this.policies = policies;
//		this.queues = queues;
//		this.exchanges = exchanges;
//		this.bindings = bindings;
//	}
	
}
