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

import utils.ConnectDB;

import com.aventstack.extentreports.markuputils.MarkupHelper;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utils.TestBase;
import utils.TestUtils;

public class ValidateEmailForOnboarding extends TestBase{
	
	
	@Parameters({"dataEnv"})
	public static void ValidateEmailForOnboardingBase(String envsValue, int responseCode, String message, String dataEnv, int statusCode, String Description) throws FileNotFoundException, IOException, ParseException {
		RestAssured.baseURI = baseUrl;
		
		JSONParser parser = new JSONParser();
		JSONObject config = (JSONObject) parser.parse(new FileReader("resources/" + dataEnv + "/validateEmailforOnboarding.config.json"));
		JSONObject envs = (JSONObject) config.get(envsValue);
		
		String endpoint = (String) config.get("endpoint");
		String email = (String) envs.get("email");
		
		
		TestUtils.testTitle("<b>Description</b>");
		testInfo.get().info(Description);
		
		TestUtils.testTitle("<b>ENDPOINT</b>");
		testInfo.get().info(baseUrl + endpoint);
		
		TestUtils.testTitle("Request Body");
		testInfo.get().info("<b> email: </b> " + email);
		

		Response res = given().
				header("Content-Type","application/x-www-form-urlencoded").
				header("App-Id","com.seamfix.nimc_apk").
				
				param("email", email).
				
			when().
				post(endpoint).
				then().assertThat().
				
				extract().response();
		
		
		TestUtils.testTitle("<b>Response Body</b>");
		testInfo.get().info(MarkupHelper.createCodeBlock(res.prettyPrint()));
		
		
		int statCode = res.getStatusCode();
		Assert.assertEquals(statCode, statusCode);
		
		
		String response = res.asString();
		JsonPath jsonRes = new JsonPath(response);
		int cod = jsonRes.getInt("status");
		String mes = jsonRes.getString("message");
		Assert.assertEquals(cod, responseCode);
		Assert.assertEquals(mes, message);
		
	}
	
	
	@Parameters({"dataEnv"})
	@Test
	public void ValidEmailThatHasNotBeenOnboarded (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		ValidateEmailForOnboardingBase("ValidEmailThatHasNotBeenOnboarded", 0, "Successfully generated OTP.", dataEnv, 200, "Validate a valid email that has not been onboarded");
	
	}
	
	
	@Parameters({"dataEnv"})
	@Test
	public void ValidEmailThatHasBeenOnboarded (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		ValidateEmailForOnboardingBase("ValidEmailThatHasBeenOnboarded", -8, "Agent is already onboarded", dataEnv, 200, "Validate valid email that has been onboarded");
		
	}
	
	
	@Parameters({"dataEnv"})
	@Test
	public void InvalidEmail (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		ValidateEmailForOnboardingBase("InvalidEmail", -2, "Failed authentication", dataEnv, 200, "Validate invalid email that wants to be onboarded");
		
	}
	
	
	@Parameters({"dataEnv"})
	@Test
	public void DeactivatedUser (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		ValidateEmailForOnboardingBase("DeactivatedUser", -4, "Your account was deactivated. Please contact support", dataEnv, 200, "Validate deactivated email that wants to be onboarded");
		
	}
	
	
	@Parameters({"dataEnv"})
	@Test
	public void emptyEmail (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		ValidateEmailForOnboardingBase("EmptyEmail", -3, "Invalid input was provided", dataEnv, 200, "Validate empty email");
		
	}
	
	
}
