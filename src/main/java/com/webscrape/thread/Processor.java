package com.webscrape.thread;

import java.io.IOException;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.databind.JsonNode;
import com.webscrape.util.JsonUtil;

public class Processor implements Callable<JsonNode> {
	private JsonNode node;
	
	public Processor(JsonNode node) {
		this.node = node;
	}

	public JsonNode call() throws IOException {
		return new JsonUtil().createOutputJson(node);
	}

}
