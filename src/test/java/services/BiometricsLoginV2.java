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
import utils.TestUtils;
import utils.TestBase;

public class BiometricsLoginV2 extends TestBase {
	private static String url = "http://54.219.7.42:8585";
	

	@Parameters({"dataEnv"})
	public void biometricLoginBase(String testCase, int responseCode, String message, String dataEnv, int statusCode) throws FileNotFoundException, IOException, ParseException {
		RestAssured.baseURI = url;
		
		JSONParser parser = new JSONParser();
		JSONObject config = (JSONObject) parser.parse(new FileReader("resources/"+dataEnv+"/biometricsLoginV2.config.json"));

		
		String endpoint = (String) config.get("endpoint");
		String requestBody = TestUtils.generateString2("biometricsLoginV2.config.json",testCase,dataEnv);
		
		TestUtils.testTitle("<b>ENDPOINT</b>");
		testInfo.get().info(url + endpoint);
		
		TestUtils.testTitle("<b>Request Body</b>");
		testInfo.get().info(MarkupHelper.createCodeBlock(requestBody));
		
		Response res =	given().
				header("App-Id","com.seamfix.nimc_apk").
				header("Content-Type","application/json").
				
				body(requestBody).
				
			when().
				post(endpoint).
				then().assertThat().extract().response();
		
		TestUtils.testTitle("Response Body");
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
	public void validUsernameValidfingerPrintAndFingerCode(String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		biometricLoginBase("validUsernameValidfingerPrintAndFingerCode", 0, "Success", dataEnv, 200);
		
	}
	
	@Parameters({"dataEnv"})
	@Test
	public void invalidUsernameValidfingerPrintAndFingerCode(String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		biometricLoginBase("invalidUsernameValidfingerPrintAndFingerCode", -24, "Invalid Frontend Partner Vendor", dataEnv, 200);
		
	}
	
	@Parameters({"dataEnv"})
	@Test
	public void validUsernameValidfingerPrintAndInvalidFingerCode(String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		biometricLoginBase("validUsernameValidfingerPrintAndInvalidFingerCode", 419, "Invalid credentials", dataEnv, 200);
		
	}
	
	@Parameters({"dataEnv"})
	@Test
	public void validUsernameInvalidfingerPrintAndValidFingerCode(String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		biometricLoginBase("validUsernameInvalidfingerPrintAndValidFingerCode", 419, "Invalid credentials", dataEnv, 200);
		
	}
	
	@Parameters({"dataEnv"})
	@Test
	public void deactivatedUserValidfingerCode(String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		biometricLoginBase("deactivatedUserValidfingerCode", -4, "Inactive Account", dataEnv, 200);
		
	}
	
}
