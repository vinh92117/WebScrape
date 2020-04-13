package com.webscrape.thread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.webscrape.util.JsonUtil;

public class ThreadProcessor extends Thread {
	static JsonUtil jsonUtil = new JsonUtil();
	
	private static JsonNode jsonToPublish = jsonUtil.createNewJson();
	private static BlockingQueue<JsonNode> jsonNodes = new LinkedBlockingDeque<JsonNode>();
	private static ArrayList<Long> threadList = new ArrayList<Long>();
	
	Logger logger = java.util.logging.Logger.getLogger(JsonUtil.class.getName());
	
	public void run() {
		String path;
		try { 
			while (!jsonNodes.isEmpty()) {
				JsonNode node = jsonNodes.poll();
				path = node.get(jsonUtil.PATH).asText();
				if (jsonToPublish.has(path))
					throw new RuntimeException("Duplicate path found");
				
				jsonUtil.appendJson(jsonToPublish, path, jsonUtil.createOutputJson(node));
			}
			threadList.remove(this.getId());
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, "Duplicate paths found for ", e);
			System.exit(-1);
		} catch (IOException e) {
			logger.severe(e.toString());
			System.exit(-1);
		}
	}

	public JsonNode processJsonNodes(JsonNode json) throws InterruptedException {
		// Iterate through the input of json(s) to process and format into desired output
		if (!jsonUtil.isSingleLevelJson(json)) {
			for (JsonNode node : json) 
				jsonNodes.add(node);
		}
		else
			jsonNodes.add(json);

		// Utilize 2 threads per core to avoid exhausing the cpu
		int cores = Runtime.getRuntime().availableProcessors();
		for (int i = 0; i < cores * 2; i++) {
			ThreadProcessor processThreads = new ThreadProcessor();
			threadList.add(processThreads.getId());
			processThreads.start();
		}
		
		// Wait until the threads are complete with 1 second checks in between
		while (!threadList.isEmpty())
			Thread.sleep(1000);
		
		return jsonToPublish;
	}

}
