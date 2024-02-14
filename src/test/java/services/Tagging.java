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

public class Tagging extends TestBase {
	public static String baseUrl = System.getProperty("seamfix-url", "http://54.219.7.42:7110");

	public static void taggingBase(String testCase, int code, String description, String dataEnv, int statusCode,
			String methDesc) throws IOException, ParseException {

		RestAssured.baseURI = baseUrl;

		JSONParser parser = new JSONParser();
		JSONObject config = (JSONObject) parser.parse(new FileReader("resources/" + dataEnv + "/tagging.config.json"));

		String endpoint = (String) config.get("endpoint");
		JsonObject requestBody = TestUtils.generateJson(dataEnv, "tagging.config.json", testCase);
		String body = requestBody.toString();

		TestUtils.testTitle("<b>Description</b>");
		testInfo.get().info(methDesc);

		TestUtils.testTitle("<b>ENDPOINT</b>");
		testInfo.get().info(baseUrl + endpoint);

		TestUtils.testTitle("<b>Request Body</b>");
		testInfo.get().info(MarkupHelper.createCodeBlock(body));

		Response res = given().header("Content-type", "application/json").body(requestBody).

				when().post(endpoint).then().assertThat().extract().response();

		TestUtils.testTitle("Response Body");
		String response = res.asString();
		testInfo.get().info(MarkupHelper.createCodeBlock(response));

		int statCode = res.getStatusCode();
		TestUtils.testTitle("Status Code");
		testInfo.get().info(Integer.toString(statCode));

		Assert.assertEquals(statCode, statusCode);

		JsonPath jsonRes = new JsonPath(response);
		int c = jsonRes.getInt("code");
		String desc = jsonRes.getString("description");

		Assert.assertEquals(c, code);
		Assert.assertEquals(desc, description);

	}

	@Parameters({ "dataEnv" })
	@Test
	public void verifyTaggedDeviceResponse(String dataEnv) throws IOException, ParseException {
		taggingBase("verifyTaggedDevice", 0, "Device is tagged", dataEnv, 200,
				"To verify the description for a tagged device");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void verifyUnTaggedDeviceResponse(String dataEnv) throws IOException, ParseException {
		taggingBase("verifyUnTaggedDevice", -18,
				"A device activation request has been sent to your FEP administrator.\nYou will be able to proceed as soon as your request is approved. For quick turnaround time, We recommend a follow up with your FEP Administrator. ",
				dataEnv, 200, "To verify the description for an untagged device");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void verifyEmptyDeviceIDResponse(String dataEnv) throws IOException, ParseException {
		taggingBase("verifyEmptyDeviceID", -1, "Enter a valid deviceID", dataEnv, 200,
				"To verify the description for an empty DeviceID");
	}
}
