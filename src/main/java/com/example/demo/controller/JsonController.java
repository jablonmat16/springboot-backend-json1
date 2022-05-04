package com.example.demo.controller;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.Compare;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import com.flipkart.zjsonpatch.JsonPatch;
import com.flipkart.zjsonpatch.JsonPatchApplicationException;
import com.flipkart.zjsonpatch.DiffFlags;

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
		
		String jsons1 = json.get(0);
		String jsons2 = json.get(1);
		
		try {
			source = mapper.readTree(jsons1);
			target = mapper.readTree(jsons2);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} 
		
		
		JsonNode patch = null;
		EnumSet<DiffFlags> flags = DiffFlags.dontNormalizeOpIntoMoveAndCopy().clone();
		

		patch = JsonDiff.asJson(target, source, flags);
		
		return patch;
	}
	
	@GetMapping("/jsons")
	public List<String> getAllJsons(){
		return getJsons();
	}

	@GetMapping("/compare")
	public JsonNode getAllComparisons(){
		
		return compare;
	}
	
	//grabs json from git source and turns it into an object
	private static ArrayList<String> getJsons(){
		ArrayList<String> jsons = new ArrayList<>();
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
		
		List<Compare> updatedCompare = new ArrayList<>();
		
		ObjectMapper objectMapper = new ObjectMapper();
		List<Compare> comparison = new ArrayList<>();
		try {		
			comparison = objectMapper.readValue(environment.toString(), new TypeReference<List<Compare>>(){});
		} catch (JsonProcessingException e) {
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
		
		compare = returned;
		JsonNode target = returned;
		try {
			//error occurs here/////////////
			target = JsonPatch.apply(returned, mapper.readTree(getJsons().get(1)));
		} catch (JsonProcessingException | JsonPatchApplicationException e) {
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
