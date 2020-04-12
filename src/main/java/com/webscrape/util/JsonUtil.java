package com.webscrape.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonUtil {
	public final String URL = "url";
	public final String SIZE = "size";
	public final String PATH = "path";
	public final String OUTPUT_JSON = "Output.json";
	
	ObjectMapper mapper = new ObjectMapper();
	Logger logger = java.util.logging.Logger.getLogger(JsonUtil.class.getName());
	UrlUtil urlUtil = new UrlUtil();
	
	public JsonNode convertToJson(String input) throws IOException {
		JsonNode json = mapper.readTree(input);
		if (json == null || json.size() == 0) {
			logger.log(Level.SEVERE, "Contents provides an invalid JSON");
			throw new NullPointerException();
		}
		
		return json;
	}
	
	public JsonNode createOutputJson(JsonNode jsonInput) throws NullPointerException, RuntimeException {
		String url = jsonInput.get(URL).asText();
		
		if (jsonInput.get(PATH) == null) {
			logger.log(Level.SEVERE, "No path specified for " + url);
			throw new NullPointerException("Null path value in " + jsonInput);
		}

		JsonNode webInfo = createNewJson();
		((ObjectNode) webInfo).put(URL, url);
		
		int urlSize = urlUtil.getUrlFileSize(url);
		if (Integer.parseInt(jsonInput.get(SIZE).asText()) != urlSize) {
			logger.log(Level.SEVERE, "Size of page in input file is incorrect for URL - " + url + ". Received size is " + urlSize + ".");
			throw new RuntimeException("Retrieved unmatching size from " + url);
		}
		
		((ObjectNode) webInfo).put(SIZE, urlSize);
		
		return webInfo;
	}
	
	public void createJsonFile(JsonNode json) throws IOException {
		mapper.writeValue(new File(OUTPUT_JSON), json);
		logger.log(Level.INFO, "Operation successful. Generated " + OUTPUT_JSON + " file");
	}
	
	@SuppressWarnings("deprecation")
	public void appendJson(JsonNode root, String field, JsonNode toAppend) {
		((ObjectNode) root).put(field, toAppend);
	}
	
	// Check to see if the level of the json has all keys
	// if so, level is a single json with necessary fields
	public boolean isSingleLevelJson(JsonNode json) {
		if (!json.has(PATH) || !json.has(URL) || !json.has(SIZE))
			return false;
		
		return true;
	}
	
	public JsonNode createNewJson() {
		return mapper.createObjectNode();
	}
	
	public String prettyPrint(JsonNode json) throws JsonProcessingException {
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
	}

}
