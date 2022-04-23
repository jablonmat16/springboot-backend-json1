package com.example.demo.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.EnumSet;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.zjsonpatch.JsonDiff;
import com.flipkart.zjsonpatch.JsonPatch;
import com.flipkart.zjsonpatch.JsonPatchApplicationException;
import com.flipkart.zjsonpatch.DiffFlags;
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

	public JsonNode compare = createCompare();
	public JsonNode merged;
	
	public static JsonNode createCompare() {
		ArrayList<String> json = getJsons();
		
		ObjectMapper mapper = new ObjectMapper();
		
		JsonNode source = null;
		JsonNode target = null;
		//JsonNode merged = null;
		
		String jsons1 = json.get(0);
		String jsons2 = json.get(1);
		
		try {
			source = mapper.readTree(jsons1);
			target = mapper.readTree(jsons2);
			//merged = mapper.readTree("{\"rabbit_version\":\"3.8.11\",\"parameters\":[],\"policies\":[{\"vhost\":\"QLAB02\",\"name\":\"HA_Mirror\",\"pattern\":\"^(?!logQueue).*$\",\"apply-to\":\"queues\",\"definition\":{\"ha-mode\":\"exactly\",\"ha-params\":2,\"ha-promote-on-failure\":\"always\",\"ha-promote-on-shutdown\":\"always\",\"ha-sync-mode\":\"automatic\",\"queue-mode\":\"lazy\",\"hapromote-on-failure\":\"always\",\"ha-syncmode\":\"automatic\",\"queuemode\":\"lazy\"},\"priority\":99}],\"queues\":[{\"name\":\"OfflineProductCatalogQueueQLAB02\",\"durable\":true,\"auto_delete\":false,\"arguments\":{}},{\"name\":\"PushDealerDataQueueQLAB02\",\"durable\":true,\"auto_delete\":false,\"arguments\":{}},{\"name\":\"OfflineProductCatalogQueue\",\"durable\":true,\"auto_delete\":false,\"arguments\":{}},{\"name\":\"FAMS.ACCOUNTACTIVITY.ChangeMsisdnCorrelatorEvent.Q.QLAB02\",\"durable\":true,\"auto_delete\":false,\"arguments\":{\"x-queue-type\":\"classic\"}},{\"name\":\"FAMS.ACCOUNTACTIVITY.CHANGEMSISDNTOWCS.INBOUND.EVENT.Q.QLAB02\",\"durable\":true,\"auto_delete\":false,\"arguments\":{\"x-queue-type\":\"classic\"}},{\"name\":\"UpdateRateplanClassificationsQueue\",\"durable\":true,\"auto_delete\":false,\"arguments\":{}},{\"name\":\"PushDealerDataQueue\",\"durable\":true,\"auto_delete\":false,\"arguments\":{}},{\"name\":\"logQueue\",\"durable\":true,\"auto_delete\":false,\"arguments\":{}},{\"name\":\"UpdateRateplanClassificationsQueueQLAB02\",\"durable\":true,\"auto_delete\":false,\"arguments\":{}}],\"exchanges\":[{\"name\":\"FAMS.ACCOUNTACTIVITY.CHANGEMSISDNTOWCS.INBOUND.EVENT.E.QLAB02\",\"type\":\"direct\",\"durable\":true,\"auto_delete\":false,\"internal\":false,\"arguments\":{}},{\"name\":\"springCloudHystrixStream\",\"type\":\"topic\",\"durable\":true,\"auto_delete\":false,\"internal\":false,\"arguments\":{}},{\"name\":\"FAMS.ACCOUNTACTIVITY.ChangeMsisdnCorrelatorEvent.E.QLAB02\",\"type\":\"direct\",\"durable\":true,\"auto_delete\":false,\"internal\":false,\"arguments\":{}}],\"bindings\":[{\"source\":\"FAMS.ACCOUNTACTIVITY.CHANGEMSISDNTOWCS.INBOUND.EVENT.E.QLAB02\",\"destination\":\"FAMS.ACCOUNTACTIVITY.CHANGEMSISDNTOWCS.INBOUND.EVENT.Q.QLAB02\",\"destination_type\":\"queue\",\"routing_key\":\"RSP_CLOUD\",\"arguments\":{}},{\"source\":\"FAMS.ACCOUNTACTIVITY.ChangeMsisdnCorrelatorEvent.E.QLAB02\",\"destination\":\"FAMS.ACCOUNTACTIVITY.ChangeMsisdnCorrelatorEvent.Q.QLAB02\",\"destination_type\":\"queue\",\"routing_key\":\"\",\"arguments\":{}}]}");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} 
		
		//System.out.println(source);
		
		//System.out.println(jsons1);
		JsonNode patch = null;
		EnumSet<DiffFlags> flags = DiffFlags.dontNormalizeOpIntoMoveAndCopy().clone();
		

		patch = JsonDiff.asJson(target, source, flags);
		
		target = JsonPatch.apply(patch, source);
		return patch;
	}
	
	@GetMapping("/jsons")
	public ArrayList<String> getAllJsons(){
		return getJsons();
	}

	@GetMapping("/compare")
	public JsonNode getAllComparisons(){
		
		return compare;
	}
	
	//@PostMapping("/compare")
	
	private static final Configuration configuration = Configuration.builder()
		    .jsonProvider(new JacksonJsonNodeJsonProvider())
		    .mappingProvider(new JacksonMappingProvider())
		    .build();
	
//	@GetMapping("/merged")
//	public JsonNode merge() {
//		String json = new Gson().toJson(getJsons().get(1));
//		JsonNode compare = getComparisons();
//		JsonNode updatedJson = JsonPath.using(configuration).parse(json).json();
////		for(int i = 0; i < compare.size(); i++) {
////			if(compare.get(i).getOp() == "replace")
////				updatedJson = JsonPath.using(configuration).parse(updatedJson).set("$" + compare.get(i).getPath2(), compare.get(i).getValue()).json();
////		}
//		return updatedJson;
//	}
	
	
	//grabs json from git source and turns it into an object
	private static ArrayList<String> getJsons(){
		ArrayList<String> jsons = new ArrayList<String>();
		RestTemplate restTemplate = new RestTemplate();
		jsons.add(restTemplate.getForEntity
		("https://raw.githubusercontent.com/jablonmat16/jsons/main/qlab.json", String.class).getBody());
		jsons.add(restTemplate.getForEntity
		("https://raw.githubusercontent.com/jablonmat16/jsons/main/ilab.json", String.class).getBody());
		
		
		return jsons;
	}

	@PostMapping("/compare")
	public JsonNode updateEnvironment(@RequestBody JsonNode environment) {
		compare = environment;
		System.out.println(environment);
		
		List<Compare> updatedCompare = new ArrayList<>();
		
		ObjectMapper objectMapper = new ObjectMapper();
		List<Compare> comparison = null;
		try {		
			comparison = objectMapper.readValue(environment.toString(), new TypeReference<List<Compare>>(){});
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		for(int i = 0; i < comparison.size(); i++) {
			if(comparison.get(i).isSelect()) {
				updatedCompare.add(new Compare());
				updatedCompare.get(updatedCompare.size()-1).setOp(comparison.get(i).getOp());
				updatedCompare.get(updatedCompare.size()-1).setPath(comparison.get(i).getPath());
				updatedCompare.get(updatedCompare.size()-1).setValue(comparison.get(i).getValue());
			}
		}
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode returned = mapper.valueToTree(updatedCompare);
		
		System.out.println(returned.toString());
		compare = returned;
		JsonNode target = null;
		try {
			target = JsonPatch.apply(returned, mapper.readTree(getJsons().get(1)));
		} catch (JsonProcessingException | JsonPatchApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		merged = target;
		rabbitTemplate.convertAndSend("TestExchange", "test", target.toString());
		return compare;
	}
	
	@GetMapping("/merged")
	public JsonNode merge() {
		return merged;
	}
	
	public void send(JsonNode json) {
		rabbitTemplate.convertAndSend("TestExchange", "test", json.toString());
    }
}
