This program takes in a URL or file as an argument. The URL must lead to a JSON and the file must contain a URL to a 
JSON or a JSON itself. The input of the JSON must have the following 3 elements: path, url, and size. The path will
be used as a key in the output JSON file in which the key will have the original url value and the size in bytes in the size
object. Refer to below for an example:

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

In order to run, maven build the pom.xml with clean, package, install (there are currently no tests at the moment). After wards,
go to the target folder and notice that there are 2 jar files. The uber jar file will contain all the dependencies required
to run the jar. The webscraper jar will have the java classes but will not have the dependencies packaged into it; this means
that if your machine does not have the required jars in respective directories (I.E .~/m2 for mac / linux), it will not
run.

After maven building the pom.xml, move the jar file to desired directory. Run the jar with the following commands:
java -jar uber-webscrape-application-0.0.1-SNAPSHOT !{$FILE_NAME_OR_DIRECTORY_OR_URL_HERE}

Example: java -jar uber-webscrape-application-0.0.1-SNAPSHOT MyJsonFile.json

The file Output.json will be generated if the operation is successful. Exceptions will be thrown if there are any 
errors along with logs stating with.


