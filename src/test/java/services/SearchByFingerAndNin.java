package services;

import static io.restassured.RestAssured.given;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.google.gson.JsonObject;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utils.TestBase;
import utils.TestUtils;

public class SearchByFingerAndNin extends TestBase{
	private String url = "http://54.219.7.42:8585";
	
	
	@Parameters({"dataEnv"})
	public void searchByFingerAndNin(String dataEnv, int status,String testCase, String message, String param, int sCode, String description) throws FileNotFoundException, IOException, ParseException {
		RestAssured.baseURI = url;
		
		JSONParser parser = new JSONParser();
		JSONObject config = (JSONObject) parser.parse(new FileReader("resources/"+dataEnv+"/searchByFingerAndNin.config.json"));
		String endPoint = (String) config.get("endPoint");

		
		JsonObject requestBody = TestUtils.generateJson(dataEnv, "searchByFingerAndNin.config.json", testCase);
		String body = requestBody.toString(); 

		TestUtils.testTitle("ENDPOINT");
		testInfo.get().info(url + endPoint);

		TestUtils.testTitle("Request Body");
		testInfo.get().info(MarkupHelper.createCodeBlock(body));
		
		TestUtils.testTitle("DESCRIPTION");
		testInfo.get().info(description);
		
		Response res =	given().
				header("Content-Type","application/json").
				header("App-Id","com.seamfix.nimc_apk").
			    
				
				body(requestBody).
				
			when().
				post(endPoint).
				then().assertThat().extract().response();
	
		TestUtils.testTitle("Response Body");
		String response = res.asString();
		testInfo.get().info(MarkupHelper.createCodeBlock(res.prettyPrint()));
		
		int statusCode = res.getStatusCode();
		Assert.assertEquals(statusCode, sCode);

		JsonPath jsonRes = new JsonPath(response);
		int stat = jsonRes.getInt("status");
		String mes = jsonRes.getString("message");
		Assert.assertEquals(stat, status);
		Assert.assertEquals(mes, message);
		
			
} 	
	
	@Parameters({"dataEnv"})
	@Test
	public void validFingerCodeAndNIN (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		searchByFingerAndNin(dataEnv, 0, "validFingerCodeAndNIN", "Success", "validFingerCodeAndNIN", 200, "Search using a Valid fingerCode and NIN");
	}

	@Parameters({"dataEnv"})
	@Test 
	public void invalidFingerCodeAndNIN (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		searchByFingerAndNin(dataEnv, 419, "invalidFingerCodeAndNIN","Invalid credentials", "invalidFingerCodeAndNIN", 200, "Search using an Invalid fingerCode and Invalid NIN");

	}
	
	@Parameters({"dataEnv"})
	@Test 
	public void validFingerCodeAndInvalidNIN (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		searchByFingerAndNin(dataEnv, -1, "validFingerCodeAndInvalidNIN", "Failed", "validFingerCodeAndInvalidNIN", 200, "Search using a Valid FingerCode and Invalid NIN");

	}
	
	@Parameters({"dataEnv"})
	@Test 
	public void invalidFingerCodeAndValidNIN (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		searchByFingerAndNin(dataEnv, 419, "invalidFingerCodeAndValidNIN", "Invalid credentials", "invalidFingerCodeAndValidNIN", 200, "Search using an Invalid FingerCode and valid NIN");

	}
	
	@Parameters({"dataEnv"})
	@Test 
	public void validFingerCodeAndEmptyNIN (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		searchByFingerAndNin(dataEnv, -1, "validFingerCodeAndEmptyNIN", "Failed", "validFingerCodeAndEmptyNIN", 200, "Search using a Valid FingerCode and Empty NIN");

	}
	
	
}
