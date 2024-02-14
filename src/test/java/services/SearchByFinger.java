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

public class SearchByFinger extends TestBase{
	private String url = "http://54.219.7.42:8585";
	
	
	@Parameters({"dataEnv"})
	public void searchByFinger(String dataEnv, int status, String message, String key, String value, int sCode,String description) throws FileNotFoundException, IOException, ParseException {
		RestAssured.baseURI = url;
		
		JSONParser parser = new JSONParser();
		JSONObject config = (JSONObject) parser.parse(new FileReader("resources/"+dataEnv+"/searchByFinger.config.json"));
		String endPoint = (String) config.get("endPoint");
		
		JsonObject requestBody = TestUtils.generateJson(dataEnv, "searchByFinger.config.json", "params");
		JsonObject ninSearchDto = (JsonObject) requestBody.get("ninSearchDto");
		ninSearchDto.addProperty(key, value);
		String body = requestBody.toString();

		TestUtils.testTitle("ENDPOINT");
		testInfo.get().info(url + endPoint);

		TestUtils.testTitle("Request Body");
		testInfo.get().info(MarkupHelper.createCodeBlock(body));
		
		TestUtils.testTitle("DESCRIPTION");
		testInfo.get().info(description);
		
		Response res =	given().
				header("Content-Type","application/json").
				/*header("App-Id","com.seamfix.nimc_apk").*/
			    
				
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
	public void validFingerCode (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		searchByFinger(dataEnv, 0, "Success", "fingerCode", "3", 200, "Search using a valid fingerCode");
	}

	@Parameters({"dataEnv"})
	@Test 
	public void invalidFingerCode (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		searchByFinger(dataEnv, 419, "Invalid credentials", "fingerCode", "567", 200, "Search using an Invalid fingerCode");

	}
	
	@Parameters({"dataEnv"})
	@Test 
	public void invalidFingerString (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		searchByFinger(dataEnv, -1, "Failed", "fingerStringInBase64", "/6D/qAB7TklTVF9DT00", 200, "Search using an Invalid fingerString");

	}
	
	@Parameters({"dataEnv"})
	@Test 
	public void emptyFingerString (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		searchByFinger(dataEnv, -1, "Failed", "fingerStringInBase64", "", 200, "Search using an Empty fingerString");

	}
	
}

