package com.webscrape.application;

import java.util.logging.Level;

import com.fasterxml.jackson.databind.JsonNode;
import com.webscrape.thread.Executor;
import com.webscrape.util.FileUtil;
import com.webscrape.util.JsonUtil;
import com.webscrape.util.UrlUtil;

public class WebScrape {
	static FileUtil fileUtil = new FileUtil();
	static JsonUtil jsonUtil = new JsonUtil();
	static UrlUtil urlUtil = new UrlUtil();
	
	public static void main(String[] args) {
		try {
			String input = args[0];
			
			// check if input is a local file or if it is a URL
			JsonNode jsonInput = jsonUtil.createNewJson();
			if (fileUtil.isLocalFile(input))
				jsonInput = fileUtil.loadJsonContent(input);
			
			else if (urlUtil.isValidUrl(input))
				jsonInput = urlUtil.extractJsonFromUrl(input);		
			
			if (jsonInput == null || jsonInput.size() == 0)
				throw new NullPointerException("Invalid input");
			
			// Create multiple threads to process json
			jsonInput = new Executor().execute(jsonInput);
			
			// Takes the resulting json and publishes to file
			jsonUtil.createJsonFile(jsonInput);
			System.exit(0);
		} catch (Exception e) {
			java.util.logging.Logger.getLogger(WebScrape.class.getName()).log(Level.SEVERE, "Shutting down", e);
			System.exit(-1);
		}
	}
}
