package com.webscrape.application;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.webscrape.thread.Executor;
import com.webscrape.util.FileUtil;
import com.webscrape.util.JsonUtil;
import com.webscrape.util.UrlUtil;

public class WebScrape {
	static Logger logger = java.util.logging.Logger.getLogger(WebScrape.class.getName());
	static FileUtil fileUtil = new FileUtil();
	static JsonUtil jsonUtil = new JsonUtil();
	static UrlUtil urlUtil = new UrlUtil();
	static Executor exeuctor = new Executor();
	
	public static void main(String[] args) {
		try {
			//String input = args[0];
			String input = "src/test/resources/jsonMultipleKeys.txt";
			if (!input.contains(System.getProperty("user.dir")))
				input = System.getProperty("user.dir") + "/" + input;
			
			// Check if input is a file or url. Load contents as string if it is a file
			String fileContents = input;
			if (fileUtil.isLocalFile(input))
				fileContents = fileUtil.loadFileContents(fileContents);
			
			// Process file and check if url or json
			if (urlUtil.isValidUrl(fileContents))
				fileContents = urlUtil.extractJsonFromUrl(fileContents);
			JsonNode jsonInput = jsonUtil.convertToJson(fileContents);
			
			// Create multiple threads to process json
			jsonInput = exeuctor.execute(jsonInput);
	
			// Takes the resulting json and publishes to file
			jsonUtil.createJsonFile(jsonInput);
			System.exit(0);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString());
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, e.toString());
		} catch (ExecutionException e) {
			logger.log(Level.SEVERE, e.toString());
		} finally {
			System.exit(-1);
		}
	}
}
