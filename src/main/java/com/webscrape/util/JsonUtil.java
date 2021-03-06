package com.webscrape.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonUtil {
	public final String URL = "url";
	public final String SIZE = "size";
	public final String PATH = "path";
	public final String OUTPUT_JSON = "Output.json";
	
	ObjectMapper mapper = new ObjectMapper();
	UrlUtil urlUtil = new UrlUtil();
	
	public JsonNode convertToJson(String input) throws IOException {
		JsonNode json = mapper.readTree(input);
		if (json == null || json.size() == 0)
			throw new NullPointerException("Invalid JSON from provided contents");
		
		return json;
	}
	
	// Process incoming JSONs by calling to their designated URLs and updating the size
	public JsonNode processJson(JsonNode jsonInput) throws MalformedURLException, IOException {
		String url = jsonInput.get(URL).asText();
		int urlSize = urlUtil.getUrlFileSize(url);
		if (Integer.parseInt(jsonInput.get(SIZE).asText()) != urlSize)
			throw new RuntimeException("Size of page in input JSON is incorrect for URL: " + url + "\nReceived size is " + urlSize);
		
		((ObjectNode) jsonInput).put(SIZE, urlSize);
		
		return jsonInput;
	}
	
	// Process jsons and return only the web info
	public JsonNode getWebInfo(JsonNode jsonInput) {
		JsonNode webInfo = createNewJson();
		((ObjectNode) webInfo).put(URL, jsonInput.get(URL).asText());
		((ObjectNode) webInfo).put(SIZE, jsonInput.get(SIZE).asText());
		return webInfo;
	}
	
	// Take the JSON input and return only the web info so that it can be stored into a JSON with their path as the key
	public JsonNode createOutputJson(JsonNode jsonInput) throws IOException {
		JsonNode webInfo = createNewJson();
		
		String url = jsonInput.get(URL).asText();
		((ObjectNode) webInfo).put(URL, url);
		
		int urlSize = urlUtil.getUrlFileSize(url);
		if (Integer.parseInt(jsonInput.get(SIZE).asText()) != urlSize)
			throw new RuntimeException("Size of page in input JSON is incorrect for URL: " + url + "\nReceived size is " + urlSize);
		
		((ObjectNode) webInfo).put(SIZE, urlSize);
		
		return webInfo;
	}
	
	public void createJsonFile(JsonNode json) throws IOException {
		mapper.writeValue(new File(OUTPUT_JSON), json);
		java.util.logging.Logger.getLogger(JsonUtil.class.getName()).info("Operation successful. Generated " + OUTPUT_JSON + " file");
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

}
