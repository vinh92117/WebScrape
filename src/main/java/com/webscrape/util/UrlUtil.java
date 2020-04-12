package com.webscrape.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UrlUtil {
	private static final String HTTP = "http://";
	private static final String HTTPS = "https://";
	private static final String GET = "GET";
	private static final String ACCEPT = "Accept";
	private static final String APPLICATION_JSON = "application/json";

	private static Logger logger = java.util.logging.Logger.getLogger(UrlUtil.class.getName());
	
	public boolean isValidUrl(String url) {
			try {
				new URL(addUrlPrefix(url)).toURI();
			} catch (MalformedURLException e) {
				return false;
			} catch (URISyntaxException e) {
				return false;
			}
			return true;
	}
	
	public int getUrlFileSize(String urlString) {
		URLConnection connection = null;
		
	    try {
	    	URL url = new URL(addUrlPrefix(urlString));

			logger.info("Connecting to " + url);
	        connection = url.openConnection();
	        if (connection instanceof HttpURLConnection)
	            ((HttpURLConnection)connection).setRequestMethod(GET);
	        
	        connection.getInputStream();
	        
	        int size = connection.getContentLength();
	        if (size < 0) {
	            logger.log(Level.SEVERE ,"Unable to retrieve size for " + url);
	            //logger.shutDown(-1);
	        }
	        
	        return size;
	    } catch (IOException e) {
	        
	    	e.printStackTrace();
	        throw new RuntimeException(e);
	    } finally {
	        if (connection instanceof HttpURLConnection)
	            ((HttpURLConnection)connection).disconnect();
	    }
	}
	
	public String extractJsonFromUrl(String url) {
		String json = "";
		
		try {
			URL jsonUrl = new URL(addUrlPrefix(url));
			
			HttpURLConnection connection = (HttpURLConnection) jsonUrl.openConnection();
			connection.setRequestMethod(GET);
			connection.setRequestProperty(ACCEPT, APPLICATION_JSON);
			
			if (isValidConnection(connection.getResponseCode())) {
				logger.log(Level.SEVERE, jsonUrl + " connection failed. HTTP error code : " + connection.getResponseCode());
				System.exit(-1);
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
			json += br.readLine();

			connection.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return json;
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
