package com.example.demo.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Compare;
import com.example.demo.model.Json;
import com.example.demo.model.Policies;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/")
public class JsonController {
	@Autowired
	private RabbitTemplate rabbitTemplate;
	//NOTE TO TEAM: ASK ABOUT ADD OPERATION
	//REPOSITORY FOR POSSIBLE FUTURE DATABASE

	@GetMapping("/jsons")
	public List<Json> getAllJsons(){
		return getJsons();
	}

	@GetMapping("/compare")
	public List<Compare> getAllComparisons(){
		return getComparisons();
	}
	
	private static final Configuration configuration = Configuration.builder()
		    .jsonProvider(new JacksonJsonNodeJsonProvider())
		    .mappingProvider(new JacksonMappingProvider())
		    .build();
	
	@GetMapping("/merged")
	public JsonNode merge() {
		String json = new Gson().toJson(getJsons().get(1));
		List<Compare> compare = getComparisons();
		JsonNode updatedJson = JsonPath.using(configuration).parse(json).json();
		for(int i = 0; i < compare.size(); i++) {
			if(compare.get(i).getOp() == "replace")
				updatedJson = JsonPath.using(configuration).parse(updatedJson).set("$" + compare.get(i).getPath2(), compare.get(i).getValue()).json();
		}
		return updatedJson;
	}
	
	public List<Compare> func(String path, Type mapType, Gson g, List<Compare> compare, Map <String,MapDifference.ValueDifference<Object>> differences){
		for(Map.Entry<String,MapDifference.ValueDifference<Object>> entry : differences.entrySet()) {
			
			if(entry.getValue().leftValue().toString().charAt(0) == '[') {
				String json1string = entry.getValue().leftValue().toString().substring(1, entry.getValue().leftValue().toString().length() - 1);
				String[] ary = json1string.split("},"); 
				for(int i = 0; i < ary.length; i++) {
					Map<String, Object> firstMapLocal = g.fromJson(entry.getValue().leftValue().toString(), mapType);
					Map<String, Object> secondMapLocal = g.fromJson(entry.getValue().rightValue().toString(), mapType);
					MapDifference<String, Object> differenceLocal = Maps.difference(firstMapLocal, secondMapLocal);
					Map <String,MapDifference.ValueDifference<Object>> differencesLocal = differenceLocal.entriesDiffering();
					path = path + "/" + i;
					if(!differencesLocal.isEmpty())
						func(path, mapType, g, compare, differencesLocal);
				}
			}
			else {
				//if(entry.getValue().leftValue().toString().charAt(0) == '{') {
					System.out.println(differences.toString());
					System.out.println(entry.getValue().leftValue().toString());
					System.out.println(entry.getValue().rightValue().toString());
					if(entry.getValue().leftValue().toString().charAt(0) == '{') {
						Map<String, Object> firstMapLocal = g.fromJson(entry.getValue().leftValue().toString(), mapType);
						Map<String, Object> secondMapLocal = g.fromJson(entry.getValue().rightValue().toString(), mapType);
						MapDifference<String, Object> differenceLocal = Maps.difference(firstMapLocal, secondMapLocal);
						Map <String,MapDifference.ValueDifference<Object>> differencesLocal = differenceLocal.entriesDiffering();
						System.out.println(differencesLocal.toString());
						path = path + "/" + entry.getKey();
						if(differencesLocal.size() >= 1 && differencesLocal.toString().charAt(0) == '{')
							func(path, mapType, g, compare, differencesLocal);
					}
					else {
						path = path + "/" +entry.getKey();
						compare.add(new Compare());
						if(entry.getValue().rightValue().toString() == "")
							compare.get(compare.size()-1).setOp("add");
						else
							compare.get(compare.size()-1).setOp("replace");
						compare.get(compare.size()-1).setPath(path);
						compare.get(compare.size()-1).setValue(entry.getValue().leftValue());
						path = path.substring(0, path.length() - entry.getKey().length() - 1);
					}
				//}
				//else
				//	path = path + "/" +entry.getKey();
				
			}
			
//			compare.add(new Compare());
//			if(entry.getValue().rightValue().toString() == "")
//				compare.get(compare.size()-1).setOp("add");
//			else
//				compare.get(compare.size()-1).setOp("replace");
//			compare.get(compare.size()-1).setPath(path);
//			compare.get(compare.size()-1).setValue(entry.getValue().leftValue());
			//System.out.println(entry.getValue());
		}
		
		
		return compare;
	}
	
	public List<Compare> getComparisons() {
		List<Compare> compare = new ArrayList<>();
		Gson g = new Gson();
		List<Json> json = getJsons();
		int len1 = 0;
		int len2 = 0;
		JsonElement json1;
		JsonElement json2;
		String temp;
		String path = "";
		json1 = g.toJsonTree(json.get(0));
		json2 = g.toJsonTree(json.get(1));
		String test = "{\"rabbit_version\":\"3.8.11\",\"policies\":{\"vhost\":\"QLAB02\",\"name\":\"HA_Mirror\",\"pattern\"\r\n"
				+ ":\"^(?!logQueue).*$\",\"apply-to\":\"queues\",\"definition\":{\"ha-mode\":\"exactly\",\"ha-params\":2,\"ha-promote-on-failure\":\"always\",\"ha-promote-on-shutdown\":\"always\",\"ha-syncmode\":\"automatic\",\"queuemode\":\"lazy\"},\"priority\":99}}";
		String test2 = "{\"rabbit_version\":\"3.8.11\",\"policies\":{\"vhost\":\"ILAB02\",\"name\":\"HA_Mirror\",\"pattern\":\r\n"
				+ "\"^(?!logQueue).*$\",\"apply-to\":\"queues\",\"definition\":{\"ha-mode\":\"exactly\",\"ha-params\":3,\"ha-promote-on-failure\":\"never\",\"ha-promote-on-shutdown\":\"always\",\"ha-sync-mode\":\"automatic\",\"queuemode\":\"lazy\"},\"priority\":99}}";
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		Map<String, Object> firstMap = g.fromJson(test, mapType);
		Map<String, Object> secondMap = g.fromJson(test2, mapType);
		MapDifference<String, Object> difference = Maps.difference(firstMap, secondMap);
		Map <String,MapDifference.ValueDifference<Object>> differences = difference.entriesDiffering();
		//System.out.printl
//		Map<String, Object> firstMapLocal = g.fromJson(differences.get("policies").leftValue().toString(), mapType);
//		Map<String, Object> secondMapLocal = g.fromJson(differences.get("policies").rightValue().toString(), mapType);
//		MapDifference<String, Object> differenceLocal = Maps.difference(firstMapLocal, secondMapLocal);
//		Map <String,MapDifference.ValueDifference<Object>> differencesLocal = differenceLocal.entriesDiffering();
		func(path, mapType, g, compare, differences);
		//String[] ary = differences.get("queues").leftValue().toString().split("},"); 
		//System.out.println(differences.get("queues").leftValue().toString());
		//System.out.println(ary[0]);
//		if(!Objects.equals(json.get(0).getRabbitVersion(), json.get(1).getRabbitVersion())) {
//			temp = json.get(1).getRabbitVersion() == null ? "add" : "replace";
//			compare.add(new Compare());
//			compare.get(compare.size()-1).setOp(temp);
//			compare.get(compare.size()-1).setPath("/rabbit_version");
//			compare.get(compare.size()-1).setPath2(".rabbit_version");
//			compare.get(compare.size()-1).setValue(json.get(0).getRabbitVersion());
//		}		
//		
//		//Policies, Queues, Exchanges, and Bindings
//		for(int x = 1; x <= 4; x++) {
//			switch(x) {
//				case 1:	len1 = json.get(0).getPolicies().length;
//						len2 = json.get(1).getPolicies().length;
//						break;
//				case 2: len1 = json.get(0).getQueues().length;
//						len2 = json.get(1).getQueues().length;
//						break;
//				case 3: len1 = json.get(0).getExchanges().length;
//						len2 = json.get(1).getExchanges().length;
//						break;
//				case 4: len1 = json.get(0).getBindings().length;
//						len2 = json.get(1).getBindings().length;
//						break;
//				default:len1 = 0;
//						len2 = 0;
//						break;
//			}
//			for(int i = 0; i < Math.min(len1, len2); i++) {
//				if(x == 1) {
//					json1 = g.toJsonTree(json.get(0).getPolicies()[i]);
//					json2 = g.toJsonTree(json.get(1).getPolicies()[i]);
//				}
//				else if(x == 2) {
//					json1 = g.toJsonTree(json.get(0).getQueues()[i]);
//					json2 = g.toJsonTree(json.get(1).getQueues()[i]);
//				}
//				else if (x == 3) {
//					json1 = g.toJsonTree(json.get(0).getExchanges()[i]);
//					json2 = g.toJsonTree(json.get(1).getExchanges()[i]);
//				}
//				else {
//					json1 = g.toJsonTree(json.get(0).getBindings()[i]);
//					json2 = g.toJsonTree(json.get(1).getBindings()[i]);
//				}
//				Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
//				Map<String, Object> firstMap = g.fromJson(json1.toString(), mapType);
//				Map<String, Object> secondMap = g.fromJson(json2.toString(), mapType);
//				MapDifference<String, Object> difference = Maps.difference(firstMap, secondMap);
//				Map <String,MapDifference.ValueDifference<Object>> differences = difference.entriesDiffering();
//				System.out.println(differences);
//				//system
//				Iterator<Map.Entry<String,MapDifference.ValueDifference<Object>>> itr = differences.entrySet().iterator();
//				while(itr.hasNext()) {
//					Map.Entry<String,MapDifference.ValueDifference<Object>> entry = itr.next();
//					compare.add(new Compare());
//					compare.get(compare.size()-1).setOp("replace");
//					if(x == 1) {
//						compare.get(compare.size()-1).setPath("/policies/" + i + "/" + entry.getKey());
//						compare.get(compare.size()-1).setPath2(".policies[" + i + "]." + entry.getKey());
//					}
//					else if(x == 2) {
//						compare.get(compare.size()-1).setPath("/queues/" + i + "/" + entry.getKey());
//						compare.get(compare.size()-1).setPath2(".queues[" + i + "]." + entry.getKey());
//					}
//					else if(x == 3) {
//						compare.get(compare.size()-1).setPath("/exchanges/" + i + "/" + entry.getKey());
//						compare.get(compare.size()-1).setPath2(".exchanges[" + i + "]." + entry.getKey());
//					}
//					else {
//						compare.get(compare.size()-1).setPath("/bindings/" + i + "/" + entry.getKey());
//						compare.get(compare.size()-1).setPath2(".bindings[" + i + "]." + entry.getKey());
//					}
//					compare.get(compare.size()-1).setValue(entry.getValue().leftValue());
//				}
//			}
//			int lenQ = Math.max(len1, len2) - Math.min(len1, len2);
//			
//			for(int i = Math.min(len1, len2); i < lenQ; i++) {
//				compare.add(new Compare());
//				compare.get(compare.size()-1).setOp("add");
//				if(x == 1) {
//					compare.get(compare.size()-1).setPath("/policies/" + i);
//					compare.get(compare.size()-1).setPath2(".policies" + "[" + i + "]");
//					compare.get(compare.size()-1).setValue(json.get(0).getPolicies()[i]);
//				}
//				else if(x == 2) {
//					compare.get(compare.size()-1).setPath("/queues/" + i);
//					compare.get(compare.size()-1).setPath2(".queues" + "[" + i + "]");
//					compare.get(compare.size()-1).setValue(json.get(0).getQueues()[i]);
//				}
//				else if(x == 3) {
//					compare.get(compare.size()-1).setPath("/exchanges/" + i);
//					compare.get(compare.size()-1).setPath2(".exchanges" + "[" + i + "]");
//					compare.get(compare.size()-1).setValue(json.get(0).getExchanges()[i]);
//				}
//				else {
//					compare.get(compare.size()-1).setPath("/bindings/" + i);
//					compare.get(compare.size()-1).setPath2(".bindings" + "[" + i + "]");
//					compare.get(compare.size()-1).setValue(json.get(0).getBindings()[i]);
//				}
//			}
//		}
		return compare;
	}
	
	
	//grabs json from git source and turns it into an object
	private List<Json> getJsons(){
		List<Json> json = new ArrayList<Json>();
		RestTemplate restTemplate = new RestTemplate();
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setSupportedMediaTypes(List.of(MediaType.TEXT_PLAIN));
		restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
		json.add(restTemplate.getForEntity
		("https://raw.githubusercontent.com/jablonmat16/jsons/main/qlab.json", Json.class).getBody());
		json.add(restTemplate.getForEntity
		("https://raw.githubusercontent.com/jablonmat16/jsons/main/ilab.json", Json.class).getBody());
		
	return json;
	}

	@DeleteMapping("/compare{path}")
	public ResponseEntity<Map<String, Boolean>> deleteCompare(@PathVariable String path){
		System.out.println(path);
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		return ResponseEntity.ok(response);
	}
	
	public void send() {
	List<Json> jsonList = getJsons();
	Json json = jsonList.get(0);
	                try {
	            ObjectMapper mapper = new ObjectMapper();
	            String jsonString = mapper.writeValueAsString(json);
	            rabbitTemplate.convertAndSend("TestExchange", "test", jsonString);
	        } catch (JsonProcessingException e) {
	            e.printStackTrace();
	        }    
    }
}