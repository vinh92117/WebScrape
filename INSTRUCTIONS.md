This program takes in a URL or file as an argument. The URL must contain a JSON and the file must contain a URL to a 
JSON or a JSON itself. The input of the JSON(s) must have the following 3 elements: path, url, and size. The path will
be used as a key in the output JSON file to store the original URL and byte size retrieved from the URLs. Refer below for an example:

Input JSON:
{
  {
    "path": "some_path",
    "url": "www.someUrl.com",
    "size": 5000
  }...
}

Output JSON:
{
  {
    "some_path": [
      "url": "www.someUrl.com",
      "size": 5000
    ]...
  }
}

In order to run:
  1) maven build the pom.xml with clean, package, install and skip tests (there are currently no tests at the moment)
  2) go to the target folder and notice that there are 2 jar files. 
  
    - The uber jar file will contain all the dependencies required to run the jar. 
    - The webscraper jar will have the java classes but will not have the dependencies packaged into it. This means that if your machine does not have the required jars in respective directories (i.e ~/.m2 for mac / linux), it will not run.
    
  3) After maven building the pom.xml, move the jar file to desired directory
  4) Run the jar with the following commands: java -jar uber-webscrape-application-0.0.1-SNAPSHOT !{$FILE_NAME_OR_DIRECTORY_OR_URL_HERE}

Example: 

         java -jar uber-webscrape-application-0.0.1-SNAPSHOT jsonFile.json
         java -jar uber-webscrape-application-0.0.1-SNAPSHOT /diretory/to/file/jsonFile.json
         java -jar uber-webscrape-application-0.0.1-SNAPSHOT www.jsonFileUrl.com

  5) If the operation is successful, a Output.json file will be generated in the same directory of the jar file. Exceptions will be thrown if there are any errors along with logs stating why.
