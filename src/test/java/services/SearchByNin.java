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

public class SearchByNin extends TestBase{
	private String url = "http://54.219.7.42:8585";
	
	@Parameters({"dataEnv"})
	public void searchByNin(String dataEnv, int status, String message, String key, String value, int sCode,String description) throws FileNotFoundException, IOException, ParseException {
		RestAssured.baseURI = url;
		
		JSONParser parser = new JSONParser();
		JSONObject config = (JSONObject) parser.parse(new FileReader("resources/"+dataEnv+"/searchByNin.config.json"));
		String endPoint = (String) config.get("endPoint");
		
		JsonObject requestBody = TestUtils.generateJson(dataEnv, "searchByNin.config.json", "params");
		JsonObject ninSearchDto = (JsonObject) requestBody.get("ninSearchDto");
		ninSearchDto.remove(key);
		ninSearchDto.addProperty(key, value);
		String body = requestBody.toString();

		TestUtils.testTitle("ENDPOINT");
		testInfo.get().info(url + endPoint);

		TestUtils.testTitle("Request Body");
		testInfo.get().info(MarkupHelper.createCodeBlock(body));
		
		Response res =	given().
				header("Content-Type","application/json").
				header("App-Id","com.seamfix.nimc_apk").
			    header("Host","41.223.47.82:8585").
				
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
	public void validNINSearch (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		searchByNin(dataEnv, 0, "Success", "nin", "1111111111", 200, "Search using a valid NIN");
	}

	@Parameters({"dataEnv"})
	@Test 
	public void InvalidNINSearch (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		searchByNin(dataEnv, -1, "Failed", "nin", "00000000", 200, "Search using an Invalid NIN");

	}
	
	@Parameters({"dataEnv"})
	@Test 
	public void emptyNINSearch (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		searchByNin(dataEnv, -1, "Failed", "nin", "", 200, "Search using an empty NIN");

	}
	
}
