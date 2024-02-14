package services;

import static io.restassured.RestAssured.given;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

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
import utils.ConnectDB;
import utils.TestBase;
import utils.TestUtils;



public class ValidateOtpForOnboarding extends TestBase{
	
	
	@Parameters({"dataEnv"})
	public void ValidateOtpForOnboardingBase(String envsValue, int responseCode, String message, String dataEnv, int statusCode, String Description, String validOtp) throws FileNotFoundException, IOException, ParseException, SQLException {
		RestAssured.baseURI = baseUrl;
		
		JSONParser parser = new JSONParser();
		JSONObject config = (JSONObject) parser.parse(new FileReader("resources/" + dataEnv + "/ValidateOtpForOnboarding.config.json"));
		JSONObject envs = (JSONObject) config.get(envsValue);
		
		String endpoint = (String) config.get("endpoint");
		String email = (String) envs.get("email");
		String otp;
		
		TestUtils.testTitle("<b>Description</b>");
		testInfo.get().info(Description);
		
		TestUtils.testTitle("<b>ENDPOINT</b>");
		testInfo.get().info(baseUrl + endpoint);
		
		TestUtils.testTitle("Request Body");
		testInfo.get().info("<b> email: </b> " + email);
		
		
		
		
		Response res;
		if(validOtp == "No") {
		otp = (String) envs.get("otp");
		testInfo.get().info("<b> otp: </b> " + otp);
		

		 res = given().
				header("Content-Type","application/x-www-form-urlencoded").
				
				param("email", email).
				param("otp", otp).
				
			when().
				post(endpoint).
				then().assertThat().
				
				extract().response();
		
		} else {
			otp = ConnectDB.getOTP();
			testInfo.get().info("<b> otp: </b> " + otp);
			

			 res = given().
					header("Content-Type","application/x-www-form-urlencoded").
		
					param("email", email).
					param("otp", otp).
					
				when().
					post(endpoint).
					then().assertThat().
					
					extract().response();
		}
		
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
	public void emptyEmailEmptyOtp (String dataEnv) throws FileNotFoundException, IOException, ParseException, SQLException {
		
		ValidateOtpForOnboardingBase("emptyEmailEmptyOtp", -3, "Invalid input was provided", dataEnv, 200, "Pass empty email and otp", "No");
		
	}
	
	
	@Parameters({"dataEnv"})
	@Test
	public void EmptyEmailValidOtp (String dataEnv) throws FileNotFoundException, IOException, ParseException, SQLException {
		
		ValidateOtpForOnboardingBase("EmptyEmailValidOtp", -3, "Invalid input was provided", dataEnv, 200, "Pass empty email and valid otp from DB", "Yes");
		
	}
	
	
	@Parameters({"dataEnv"})
	@Test
	public void validEmailThatHasNotBeenOnboardedEmptyOtp (String dataEnv) throws FileNotFoundException, IOException, ParseException, SQLException {
		
		ValidateOtpForOnboardingBase("validEmailThatHasNotBeenOnboardedEmptyOtp", -3, "Invalid input was provided", dataEnv, 200, "Pass valid email that has not been onboarded and empty OTP ", "No");
		
	}
	
	
	@Parameters({"dataEnv"})
	@Test
	public void validEmailThatHasBeenOnboardedValidOtp (String dataEnv) throws FileNotFoundException, IOException, ParseException, SQLException {

		ValidateEmailForOnboarding.ValidateEmailForOnboardingBase("ValidEmailThatHasNotBeenOnboarded", 0, "Successfully generated OTP.", dataEnv, 200, "Validate a valid email that has not been onboarded. The goal here is to generate otp that will be used in the next method");

		ValidateOtpForOnboardingBase("validEmailThatHasNotBeenOnboardedValidOtp", 0, "The specified OTP is valid.", dataEnv, 200, "Pass valid email that has been onboarded and valid OTP from DB ", "Yes");
		
	}
	
	
	@Parameters({"dataEnv"})
	@Test
	public void InvalidEmail (String dataEnv) throws FileNotFoundException, IOException, ParseException, SQLException {
		
		ValidateOtpForOnboardingBase("InvalidEmail", -19, "Invalid onboarding request.", dataEnv, 200, "Passing an invalid email ", "Yes");
		
	}
	
	
	@Parameters({"dataEnv"})
	@Test
	public void validEmailThatHasNotBeenOnboardedInvalidOtp (String dataEnv) throws FileNotFoundException, IOException, ParseException, SQLException {
		
		ValidateOtpForOnboardingBase("validEmailThatHasNotBeenOnboardedInvalidOtp", -19, "Invalid onboarding request.", dataEnv, 200, "Valid email that has not been onboarded and an invalid otp", "No");
		
	}
	
	
	@Parameters({"dataEnv"})
	@Test
	public void DeactivatedUser (String dataEnv) throws FileNotFoundException, IOException, ParseException, SQLException {
		
		ValidateOtpForOnboardingBase("DeactivatedUser", -4, "Inactive account", dataEnv, 200, "Passing a deactivated user", "Yes");
		
	}
}
