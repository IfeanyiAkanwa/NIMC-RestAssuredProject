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

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utils.TestBase;
import utils.TestUtils;

public class PasswordReset extends TestBase {
	
	@Parameters({"dataEnv"})
	public void passwordReset(String dataEnv,String envsValue, int status, String message, int statusCode, String Description) throws FileNotFoundException, IOException, ParseException {
		RestAssured.baseURI = baseUrl;
		
		JSONParser parser = new JSONParser();
		JSONObject config = (JSONObject) parser.parse(new FileReader("resources/"+dataEnv+"/passwordReset.config.json"));
		JSONObject envs = (JSONObject) config.get(envsValue);
		
		String endPoint = (String) config.get("endPoint");
		String email = (String) envs.get("email");
		
		TestUtils.testTitle("<b>ENDPOINT</b>");
		testInfo.get().info(baseUrl + endPoint);
		
		TestUtils.testTitle("<b>Description</b>");
		testInfo.get().info(Description);
		
		
		TestUtils.testTitle("Request Body");
		testInfo.get().info("<b> email: </b> " + email);

		Response res = given().
				header("Content-Type","application/x-www-form-urlencoded").
				header("App-Id","com.seamfix.nimc_apk").
				
				param("email", email).
				
			when().
				post(endPoint).
				then().assertThat().
				
				extract().response();
		
		
		TestUtils.testTitle("<b>Response Body</b>");
		testInfo.get().info(MarkupHelper.createCodeBlock(res.prettyPrint()));
		
		
		int statCode = res.getStatusCode();
		Assert.assertEquals(statCode, statusCode);
		
		
		String response = res.asString();
		JsonPath jsonRes = new JsonPath(response);
		int stat = jsonRes.getInt("status");
		String mes = jsonRes.getString("message");
		Assert.assertEquals(stat, status);
		Assert.assertEquals(mes, message);
		
	}
	
	
	@Parameters({"dataEnv"})
	@Test
	public void validPasswordReset (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		passwordReset(dataEnv, "validEmail", 0, "Success", 200, "Successful email validation");
		
	}
	
	
	@Parameters({"dataEnv"})
	@Test
	public void InvalidEmail (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		passwordReset(dataEnv, "invalidEmail", -11, "User not found", 200, "Unsuccessful email validation using invalid email");
		
	}
	
	@Parameters({"dataEnv"})
	@Test
	public void EmptyEmail (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		passwordReset(dataEnv, "emptyEmail", -3, "Invalid input was provided", 200, "Unsuccessful email validation using empty email field");
		
	}
	
	
	@Parameters({"dataEnv"})
	@Test
	public void DeactivatedEmail (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		passwordReset(dataEnv, "deactivatedEmail", -4, "Inactive Account", 200, "Unsuccessful email validation using deactivated email");
		
	}
	
	@Parameters({"dataEnv"})
	@Test
	public void EmailWithSpecialCharacters (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		passwordReset(dataEnv, "EmailWithSpecialCharacters", -3, "Invalid input was provided", 200, "Unsuccessful email validation using email with special characters");
		
	}
}
