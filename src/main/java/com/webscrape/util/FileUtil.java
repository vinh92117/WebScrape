package com.webscrape.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FileUtil {
	
	public boolean isLocalFile(String directory) {
		return new File(directory).exists();
	}
	
	public String loadFileContents(String fileInput) throws IOException {
		return new String(Files.readAllBytes(Paths.get(fileInput)));
	}
	
	// Gets file directly as a JSON. If contents fails to parse, check if it's a URL to get a JSON from
	public JsonNode loadJsonContent(String inputFile) throws FileNotFoundException, IOException {		
		try {
			return new ObjectMapper().valueToTree(new JSONParser().parse(new FileReader(inputFile)));
		} catch (ParseException e) {
			UrlUtil urlUtil = new UrlUtil();
			String fileUrl = loadFileContents(inputFile);
			
			if (!urlUtil.isValidUrl(fileUrl))
				throw new RuntimeException("Invalid input");
			
			return urlUtil.extractJsonFromUrl(fileUrl);
		}
	}
}
