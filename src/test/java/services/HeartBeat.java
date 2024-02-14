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
import io.restassured.response.Response;
import utils.TestBase;
import utils.TestUtils;

public class HeartBeat extends TestBase {
	public static String baseUrl = System.getProperty("seamfix-url", "http://54.219.7.42:7110");

	public static void heartBeatBase(String testCase, String response, String dataEnv, int statusCode,
			String description) throws IOException, ParseException {

		RestAssured.baseURI = baseUrl;

		JSONParser parser = new JSONParser();
		JSONObject config = (JSONObject) parser
				.parse(new FileReader("resources/" + dataEnv + "/heartBeat.config.json"));

		String endpoint = (String) config.get("endpoint");
		JsonObject requestBody = TestUtils.generateJson(dataEnv, "heartBeat.config.json", testCase);
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
		String resp = res.asString();
		testInfo.get().info(MarkupHelper.createCodeBlock(response));

		int statCode = res.getStatusCode();
		TestUtils.testTitle("Status Code");
		testInfo.get().info(Integer.toString(statCode));

		Assert.assertEquals(statCode, statusCode);
		Assert.assertEquals(resp, response);

	}

	@Parameters({ "dataEnv" })
	@Test
	public void verifyHeartBeat(String dataEnv) throws IOException, ParseException {
		heartBeatBase("verifyHeartBeat", "10", dataEnv, 200, "This message is to verify that heartbeat is returned");
	}
}
