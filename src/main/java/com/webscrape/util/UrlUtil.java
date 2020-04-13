package com.webscrape.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.JsonNode;

public class UrlUtil {
	private static final String HTTP = "http://";
	private static final String HTTPS = "https://";
	private static final String GET = "GET";
	private static final String ACCEPT = "Accept";
	private static final String APPLICATION_JSON = "application/json";
	
	public boolean isValidUrl(String url) {
		// If exception is thrown, URL string can not be converted, hence return false and don't shut down
		try {
			new URL(addUrlPrefix(url));
		} catch (MalformedURLException e) {
			return false;
		}		
		return true;
	}
	
	public int getUrlFileSize(String urlString) throws IOException {
		Logger logger = java.util.logging.Logger.getLogger(UrlUtil.class.getName());
		logger.info("Retrieving size from " + urlString);
		
		HttpURLConnection connection = (HttpURLConnection) new URL(addUrlPrefix(urlString)).openConnection();
		String encoding = connection.getContentEncoding();
		if (encoding == null)
			encoding = "UTF-8";
		
	    int size = IOUtils.toString(connection.getInputStream(), encoding).getBytes().length;
	    connection.disconnect();
        if (size < 0)
            throw new RuntimeException("Unable to retrieve size for " + urlString);
        
        return size;
	}
	
	public JsonNode extractJsonFromUrl(String url) throws IOException {
		try {
			URL jsonUrl = new URL(addUrlPrefix(url));
			
			HttpURLConnection connection = (HttpURLConnection) jsonUrl.openConnection();
			connection.setRequestMethod(GET);
			connection.setRequestProperty(ACCEPT, APPLICATION_JSON);
			
			if (isValidConnection(connection.getResponseCode())) 
				throw new RuntimeException(jsonUrl + " connection failed. HTTP error code : " + connection.getResponseCode());
			
			BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
			JsonNode json = new JsonUtil().convertToJson((br.readLine()));
			
			connection.disconnect();
			return json;
		} catch (IOException e) {
			throw e;
		}		
	}
	
	public boolean isValidConnection(int responseCode) {		
		return responseCode < 200 || responseCode >= 300;
	}
	
	private String addUrlPrefix(String url) {
		url = url.replace(HTTP, HTTPS);
		if (!url.contains(HTTPS))
			url = HTTPS + url;
		
		return url;
	}
}
