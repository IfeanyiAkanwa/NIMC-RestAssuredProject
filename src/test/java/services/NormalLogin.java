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

public class NormalLogin extends TestBase {
	
	@Parameters({"dataEnv"})
	public void loginBase(String envsValue, int responseCode, String message, String dataEnv, int statusCode) throws FileNotFoundException, IOException, ParseException {
		RestAssured.baseURI = baseUrl;
		
		JSONParser parser = new JSONParser();
		JSONObject config = (JSONObject) parser.parse(new FileReader("resources/" + dataEnv + "/NormalLogin.config.json"));
		JSONObject envs = (JSONObject) config.get(envsValue);
		
		String endpoint = (String) config.get("endpoint");
		String email = (String) envs.get("email");
		String password = (String) envs.get("password");
		String deviceIdentifier = (String) envs.get("deviceIdentifier");
		
		TestUtils.testTitle("<b>ENDPOINT</b>");
		testInfo.get().info(baseUrl + endpoint);
		
		TestUtils.testTitle("Request Body");
		testInfo.get().info("<b> email: </b> " + email);
		testInfo.get().info("<b> pw: </b> " + password);
		
		Response res = given().
				header("Content-Type","application/x-www-form-urlencoded").
				header("App-Id","com.seamfix.nimc_apk").
				
				param("email", email).
				param("pw", password).
				param("deviceIdentifier", deviceIdentifier).
				
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
	@Test (groups = { "Regression","Health Check" })
	public void ValidFepUserLogin (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		loginBase("validLogin", 0, "Success", dataEnv, 200);
		
	}
	
	@Parameters({"dataEnv"})
	@Test
	public void InvalidEmail (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		loginBase("invalidEmail", -11, "User not found", dataEnv, 200);
		
	}
	
	
	@Parameters({"dataEnv"})
	@Test
	public void emptyEmail (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		loginBase("emptyEmail", -3, "Invalid input was provided", dataEnv, 200);
		
	}
	
	
	@Parameters({"dataEnv"})
	@Test
	public void emptyEmailEmptyPassword (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		loginBase("emptyEmailEmptyPassword", -3, "Invalid input was provided", dataEnv, 200);
		
	}
	
	@Parameters({"dataEnv"})
	@Test
	public void validEmailInvalidPassword (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		loginBase("validEmailInvalidPassword", -2, "Invalid credentials", dataEnv, 200);
		
	}
	
	@Parameters({"dataEnv"})
	@Test
	public void validEmailEmptyPassword (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		loginBase("validEmailEmptyPassword", -3, "Invalid input was provided", dataEnv, 200);
		
	}
	
	@Parameters({"dataEnv"})
	@Test
	public void loginWithDeactivatedUser (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		loginBase("loginWithDeactivatedUser", -4, "Inactive Account", dataEnv, 200);
		
	}
	
}
