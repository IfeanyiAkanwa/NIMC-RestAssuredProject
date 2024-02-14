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

public class ChangePassword extends TestBase{
	
	@Parameters({"dataEnv"})
	public void chanagePasswordBase(String envsValue, int responseCode, String message, String dataEnv, int statusCode, String Description) throws FileNotFoundException, IOException, ParseException {
		RestAssured.baseURI = baseUrl;
		
		JSONParser parser = new JSONParser();
		JSONObject config = (JSONObject) parser.parse(new FileReader("resources/" + dataEnv + "/changePassword.config.json"));
		JSONObject envs = (JSONObject) config.get(envsValue);
		
		String endpoint = (String) config.get("endpoint");
		String email = (String) envs.get("email");
		String oldPassword = (String) envs.get("oldPwd");
		String newPassword = (String) envs.get("new_pw");
		
		
		TestUtils.testTitle("<b>Description</b>");
		testInfo.get().info(Description);
		
		TestUtils.testTitle("<b>ENDPOINT</b>");
		testInfo.get().info(baseUrl + endpoint);
		
		TestUtils.testTitle("Request Body");
		testInfo.get().info("<b> email: </b> " + email);
		testInfo.get().info("<b> oldPwd: </b> " + oldPassword);
		testInfo.get().info("<b> new_pw: </b> " + newPassword);

		Response res = given().
				header("Content-Type","application/x-www-form-urlencoded").
				header("App-Id","com.seamfix.nimc_apk").
				
				param("email", email).
				param("oldPwd", oldPassword).
				param("new_pw", newPassword).
				
			when().
				post(endpoint).
			then().
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
	public void InvalidEmail (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		chanagePasswordBase("InvalidUsername", 25, "No user found", dataEnv, 200, "Change password of an invalid email");
		
	}
	
	
	@Parameters({"dataEnv"})
	@Test
	public void ValidUsernameInvalidOldPassword (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		chanagePasswordBase("ValidUsernameInvalidOldPassword", 26, "Invalid credentials ", dataEnv, 200, "Change password of a valid user but with a wrong old password");
		
	}
	
	@Parameters({"dataEnv"})
	@Test
	public void changePasswordOfDeactivatedUser (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		chanagePasswordBase("DeactivatedUSer", -4, "Inactive Account", dataEnv, 200, "Change password of a deactivated user");
		
	}
	
	
	@Parameters({"dataEnv"})
	@Test
	public void changePasswordToDifferentPasswordViolatingFormat (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		chanagePasswordBase("changePasswordToDifferentPasswordViolatingFormat", 99, "Password must contain at least 10 Characters with at least 1 LowerCase, 1 UpperCase, 1 Number, and 1 Symbol", dataEnv, 200, "Change password of a valid user to a new password that violates the password format");
		
	}
	
	
	@Parameters({"dataEnv"})
	@Test
	public void changePasswordToSameOldPasword (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		chanagePasswordBase("changePasswordToSameOldPasword", 0, "Success", dataEnv, 200, "Change password of a valid user to the same old password");
		
	}
	
	@Parameters({"dataEnv"})
	@Test
	public void changePasswordToDifferentPassword (String dataEnv) throws FileNotFoundException, IOException, ParseException {
		
		chanagePasswordBase("changePasswordToDifferentPassword", 0, "Success", dataEnv, 200, "Change password of a valid user to a valid new password");
		//Change back to old password
		chanagePasswordBase("changePasswordBAckToOldPassword", 0, "Success", dataEnv, 200, "Change password to the default password fro this account");
		
	}
	
	
	
}
