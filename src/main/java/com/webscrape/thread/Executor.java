package com.webscrape.thread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.webscrape.util.JsonUtil;

public class Executor {
	JsonUtil jsonUtil = new JsonUtil();
	
	public JsonNode execute(JsonNode json) throws InterruptedException, ExecutionException {
		// Utilize 2 threads per core to avoid exhausting the CPU
		int cores = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(cores * 2);
		
		// Create a json to hold all output jsons and publish
		// Check if its a single level json to prevent having path, url and size from being their own json
        JsonNode jsonToPublish = jsonUtil.createNewJson();
        if (!jsonUtil.isSingleLevelJson(json)) {
	        for (JsonNode node : json)
	        	jsonUtil.appendJson(jsonToPublish, node.get(jsonUtil.PATH).asText(), process(executor, node).get());
        }
        else
        	jsonUtil.appendJson(jsonToPublish, json.get(jsonUtil.PATH).asText(), process(executor, json).get());
        	

		executor.awaitTermination(1, TimeUnit.SECONDS);
 
        executor.shutdown();
        return jsonToPublish;
	}
	
	private Future<JsonNode> process(ExecutorService executor, JsonNode json) {
        return (Future<JsonNode>) executor.submit(new Processor(json));
	}
}
