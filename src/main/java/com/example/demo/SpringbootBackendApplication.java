package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.model.Json;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class SpringbootBackendApplication {

	@Autowired
	//private RabbitTemplate rabbitTemplate;
	public static void main(String[] args) {
		SpringApplication.run(SpringbootBackendApplication.class, args);
//		String jsonString = "{\"rabbit_version\":\"3.8.11\",\"parameters\":\"yes\"}";
//		ObjectMapper mapper = new ObjectMapper();
//		//Json json = new Json();
//		try {
//			Json json = mapper.readValue(jsonString, Json.class);
//			System.out.println(json.getRabbitVersion());
//		} catch (JsonParseException e) {
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
	}
	
//	@Override
//	public void run(String... strings) throws Exception{	
//		rabbitTemplate.convertAndSend("MyTopicExchange", "topic", "hellooo");
//	}

}
