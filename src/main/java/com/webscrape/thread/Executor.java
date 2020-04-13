package com.webscrape.thread;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.webscrape.util.JsonUtil;

public class Executor {
	JsonUtil jsonUtil = new JsonUtil();
	Logger logger = java.util.logging.Logger.getLogger(Executor.class.getName());
	BlockingQueue<Future<JsonNode>> nodeQueue = new LinkedBlockingQueue<Future<JsonNode>>(); 
	
	public JsonNode execute(JsonNode json) throws InterruptedException, ExecutionException, IOException {
		// Utilize 2 threads per core to avoid exhausting the CPU
		int cores = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(cores * 2);
		
		// Create a json to hold all output jsons and publish
		// Check if its a single level json to prevent having path, url and size from being their own json
        JsonNode jsonToPublish = jsonUtil.createNewJson();
        if (!jsonUtil.isSingleLevelJson(json)) {
	        for (JsonNode node : json) {
				if (!jsonUtil.isSingleLevelJson(node)) // check if node has required keys and values 
					throw new RuntimeException("Missing required keys - " + node);
	        	nodeQueue.add(process(executor, node));
	        }
        }
        else
        	nodeQueue.add(process(executor, json));

		executor.awaitTermination(1, TimeUnit.SECONDS);
        executor.shutdown();
        while (!nodeQueue.isEmpty()) {
        	JsonNode queueNode = nodeQueue.poll().get();
        	jsonUtil.appendJson(jsonToPublish, queueNode.get(jsonUtil.PATH).asText(), jsonUtil.getWebInfo(queueNode));
        }
 
        return jsonToPublish;
	}
	
	// Utilizes callable in Processor class
	private Future<JsonNode> process(ExecutorService executor, JsonNode json) {
        return executor.submit(new Processor(json));
	}
}
