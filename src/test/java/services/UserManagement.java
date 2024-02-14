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

public class UserManagement extends TestBase {
	public static String cbsUrl = System.getProperty("cbs-url", "https://nimctest.seamfix.com/cbs");

	@Parameters({ "dataEnv" })
	public void userManagementBase(String testCase, int status, String message, String code, String dataEnv,
			int statusCode, String description) throws FileNotFoundException, IOException, ParseException {
		RestAssured.baseURI = cbsUrl;

		JSONParser parser = new JSONParser();
		JSONObject config = (JSONObject) parser
				.parse(new FileReader("resources/" + dataEnv + "/userManagement.config.json"));

		String endpoint = (String) config.get("endpoint");
		JsonObject requestBody = TestUtils.generateJson(dataEnv, "userManagement.config.json", testCase);
		String body = requestBody.toString();

		TestUtils.testTitle("Description");
		testInfo.get().info(description);

		TestUtils.testTitle("<b>ENDPOINT</b>");
		testInfo.get().info(cbsUrl + endpoint);

		TestUtils.testTitle("<b>Request Body</b>");
		testInfo.get().info(MarkupHelper.createCodeBlock(body));

		Response res = given().header("Content-Type", "application/json").header("X-DEVICE-ID", "X0-X0-X0-X0-X0")
				.header("X-ACCOUNT-ID", "NM0093")
				.header("Signature",
						"A18384A73411BBC1FA9FF3DDD426C05F9A1FAFAE81E9C61BEF8329477218E740565844FCB30DA1AD97435A693D966827D6F95D0C3BC580F34F6F135A08EA1813")
				.body(requestBody).when().post(endpoint).then().extract().response();
		
		TestUtils.testTitle("Response Body");
		String response = res.asString();
		testInfo.get().info(MarkupHelper.createCodeBlock(response));

		int statCode = res.getStatusCode();
		Assert.assertEquals(statCode, statusCode);

		JsonPath jsonRes = new JsonPath(response);
		int sta = jsonRes.getInt("status");
		String mes = jsonRes.getString("message");
		String cod = jsonRes.getString("code");
		Assert.assertEquals(sta, status);
		Assert.assertEquals(mes, message);
		Assert.assertEquals(cod, code);

	}

	@Parameters({ "dataEnv" })
	@Test(groups = { "Regression", "Health Check" })
	public void verifyValidDeviceUserLoginDetails(String dataEnv) throws FileNotFoundException, IOException, ParseException {

		userManagementBase("validDeviceUserLogin", -1, "Request was successful!", "200", dataEnv, 200, "To check for valid device user login");

	}
	@Parameters({ "dataEnv" })
	@Test
	public void verifyInvalidDeviceUserLoginDetails(String dataEnv) throws FileNotFoundException, IOException, ParseException {

		userManagementBase("invalidLoginDetails", -1, "Invalid login details!", "401", dataEnv, 200, "To check for invalid device user login details");

	}
	@Parameters({ "dataEnv" })
	@Test
	public void verifyValidLoginIDInvalidPassword(String dataEnv) throws FileNotFoundException, IOException, ParseException {

		userManagementBase("validLoginIDInvalidPassword", -1, "Invalid login details!", "401", dataEnv, 200, "To check for valid login ID and invalid password");

	}
	
	@Parameters({ "dataEnv" })
	@Test
	public void verifyInvalidLoginIDValidPassword(String dataEnv) throws FileNotFoundException, IOException, ParseException {

		userManagementBase("invalidLoginIDValidPassword", -1, "Invalid login details!", "401", dataEnv, 200, "To check for invalid login ID and valid password");

	}
	
	@Parameters({ "dataEnv" })
	@Test
	public void verifyEmptyLoginIDEmptyPassword(String dataEnv) throws FileNotFoundException, IOException, ParseException {

		userManagementBase("emptyLoginIDEmptyPassword", 400, "Bad Parameters", null, dataEnv, 400, "To check for empty login ID and empty password");

	}
	
	@Parameters({ "dataEnv" })
	@Test
	public void verifyEmptyDeviceIDValidLoginDetails(String dataEnv) throws FileNotFoundException, IOException, ParseException {

		userManagementBase("emptyDeviceIDValidLoginDetails", 400, "Bad Parameters", null, dataEnv, 400, "To check for empty device ID and valid login details");

	}
	@Parameters({ "dataEnv" })
	@Test
	public void verifyInvalidDeviceIDValidLoginDetails(String dataEnv) throws FileNotFoundException, IOException, ParseException {

		userManagementBase("invalidDeviceIDValidLoginDetails", -1, "Node with identifier X0-X0-TT-X0-X0 and provider Seamfix not found", "400", dataEnv, 200, "To check for invalid device ID and valid login details");

	}
}