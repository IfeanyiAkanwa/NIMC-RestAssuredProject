package services;

import static io.restassured.RestAssured.given;

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

public class BlackList extends TestBase {
	public static String baseUrl = System.getProperty("seamfix-url", "http://54.219.7.42:7110");

	public static void blacklistBase(String testCase, String kitStatus, String dataEnv, int statusCode,
			String description) throws IOException, ParseException {

		RestAssured.baseURI = baseUrl;

		JSONParser parser = new JSONParser();
		JSONObject config = (JSONObject) parser
				.parse(new FileReader("resources/" + dataEnv + "/blacklist.config.json"));

		String endpoint = (String) config.get("endpoint");
		JsonObject requestBody = TestUtils.generateJson(dataEnv, "blacklist.config.json", testCase);
		String body = requestBody.toString();

		TestUtils.testTitle("<b>Description</b>");
		testInfo.get().info(description);

		TestUtils.testTitle("<b>ENDPOINT</b>");
		testInfo.get().info(baseUrl + endpoint);

		TestUtils.testTitle("<b>Request Body</b>");
		testInfo.get().info(MarkupHelper.createCodeBlock(body));

		Response res = given().header("app-id", "com.seamfix.nimc_apk").header("Content-type", "application/json")
				.body(requestBody).

				when().post(endpoint).then().assertThat().extract().response();

		TestUtils.testTitle("Response Body");
		String response = res.asString();
		testInfo.get().info(MarkupHelper.createCodeBlock(response));

		int statCode = res.getStatusCode();
		TestUtils.testTitle("Status Code");
		testInfo.get().info(Integer.toString(statCode));

		Assert.assertEquals(statCode, statusCode);

		JsonPath jsonRes = new JsonPath(response);
		String kitSta = jsonRes.getString("kitStatus");
		Assert.assertEquals(kitSta, kitStatus);

	}

	@Parameters({ "dataEnv" })
	@Test
	public void verifyWhitelistedDevice(String dataEnv) throws IOException, ParseException {
		blacklistBase("verifyWhitelistedDevice", "N", dataEnv, 200, "Verify Whitelisted Device");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void verifyBlacklistedDevice(String dataEnv) throws IOException, ParseException {
		blacklistBase("verifyBlacklistedDevice", "Y", dataEnv, 200, "Verify Blacklisted Device");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void verifyEmptyDeviceID(String dataEnv) throws IOException, ParseException {
		blacklistBase("verifyEmptyDeviceID", "Enter DeviceID", dataEnv, 200, "Verify Empty DeviceID");
	}
}