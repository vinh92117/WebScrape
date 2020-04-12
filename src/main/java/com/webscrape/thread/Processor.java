package com.webscrape.thread;

import java.util.concurrent.Callable;

import com.fasterxml.jackson.databind.JsonNode;
import com.webscrape.util.JsonUtil;

public class Processor implements Callable<JsonNode> {
	JsonUtil jsonUtil = new JsonUtil();
	JsonNode node;
	
	public Processor(JsonNode node) {
		this.node = node;
	}

	public JsonNode call() {
		return jsonUtil.createOutputJson(node);
	}

}
