package com.webscrape.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {
	
	public boolean isLocalFile(String directory) {
		return new File(directory).exists();
	}
	
	public String loadFileContents(String fileInput) throws IOException {
		return new String(Files.readAllBytes(Paths.get(fileInput)));
	}
}
