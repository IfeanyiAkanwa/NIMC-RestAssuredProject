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

public class SyncProcessor extends TestBase {
	public static String baseUrl = System.getProperty("seamfix-url", "http://54.219.7.42:7110");

	public static void syncProcessorBase(String resourceUrl, String testCase, int status, String message,
			String dataEnv, int statusCode, String description) throws IOException, ParseException {

		RestAssured.baseURI = baseUrl;

		JSONParser parser = new JSONParser();
		JSONObject config = (JSONObject) parser
				.parse(new FileReader("resources/" + dataEnv + "/syncProcessor.config.json"));

		String endpoint = (String) config.get(resourceUrl);
		JsonObject requestBody = TestUtils.generateJson(dataEnv, "syncProcessor.config.json", testCase);
		String body = requestBody.toString();

		TestUtils.testTitle("<b>Description</b>");
		testInfo.get().info(description);

		TestUtils.testTitle("<b>ENDPOINT</b>");
		testInfo.get().info(baseUrl + endpoint);

		TestUtils.testTitle("<b>Request Body</b>");
		testInfo.get().info(MarkupHelper.createCodeBlock(body));

		Response res = given().header("app-id", "com.seamfix.nimc_apk").header("Content-type", "application/json")
				.header("X-APP-VERSION", "1.0")
				.header("X-DEVICE-ID",
						"AHVZ0xjS3gR1YJQFzpNJexTYyS5vrlpK21TThyPJZsa5tthZ6z7ncPXgfJZ0VFkhDTtdD5KgNIBFjAdFHRwSKa8S4_fTRWVypA")
				.header("X-TIMESTAMP", "1610985378")
				.header("X-USER-ID", "AHVZ0xjf2hXIibzchf9jI-wDFQumakfuTw39yj82v6AEyuO_FH9fRz1Rg_6DPauKWQ4phPP0uKx7")
				.header("SIGNATURE",
						"AHVZ0xiZxU0L4kC84GYRQMGWwKCm_8zeuEwhWl2E4HfZsYMfmJg1USddWjLo5aIIVT6O8ycfx4_OzKZMJGcKhEPx-QqTY81cIqMJftl8v52Mk-i4bOq2CzYkFFxvREy7szLQq2AsVK8j")

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
		int sta = jsonRes.getInt("status");
		String mes = jsonRes.getString("message");

		Assert.assertEquals(sta, status);
		Assert.assertEquals(mes, message);

	}

	@Parameters({ "dataEnv" })
	@Test
	public void createEnrollmentSyncProcessor(String dataEnv) throws IOException, ParseException {
		syncProcessorBase("createEnrollmentResourceURL", "createEnrollment", 1, "Sent", dataEnv, 200,
				"Check create enrollment");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void createEnrollmentSyncProcessorEmptyBody(String dataEnv) throws IOException, ParseException {
		syncProcessorBase("createEnrollmentResourceURL", "createEnrollmentEmptyFileNameEmptyContent", 1,
				"Enter filename and content", dataEnv, 200,
				"Check create enrollment with empty filename and empty content");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void updateEnrollmentSyncProcessor(String dataEnv) throws IOException, ParseException {
		syncProcessorBase("updateEnrollmentResourceURL", "updateEnrollment", 1, "Sent", dataEnv, 200,
				"Check update enrollment");
	}
}