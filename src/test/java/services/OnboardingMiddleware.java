package services;

import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import utils.TestBase;
import utils.TestUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static io.restassured.RestAssured.given;

public class OnboardingMiddleware extends TestBase {
	public static String middleWareUrl = System.getProperty("middleware-url", "http://54.219.7.42:8585");

	public static void onboardingMiddlewareBase(String testCase, int responseCode, String message, String dataEnv,
			int statusCode, String description) throws IOException, ParseException {

		RestAssured.baseURI = middleWareUrl;

		JSONParser parser = new JSONParser();
		JSONObject config = (JSONObject) parser
				.parse(new FileReader("resources/" + dataEnv + "/onboardingMiddleware.config.json"));

		String endpoint = (String) config.get("endpoint");
		JsonObject requestBody = TestUtils.generateJson(dataEnv, "onboardingMiddleware.config.json", testCase);
		String body = requestBody.toString();

		TestUtils.testTitle("<b>Description</b>");
		testInfo.get().info(description);

		TestUtils.testTitle("<b>ENDPOINT</b>");
		testInfo.get().info(middleWareUrl + endpoint);

		TestUtils.testTitle("<b>Request Body</b>");
		testInfo.get().info(MarkupHelper.createCodeBlock(body));

		Response res = given().header("app-id", "com.seamfix.nimc_apk").header("Content-type", "application/json").

				body(requestBody).

				when().post(endpoint).then().assertThat().extract().response();

		TestUtils.testTitle("Response Body");
		testInfo.get().info(MarkupHelper.createCodeBlock(res.prettyPrint()));

		int statCode = res.getStatusCode();
		TestUtils.testTitle("Status Code");
		testInfo.get().info(Integer.toString(statCode));

		Assert.assertEquals(statCode, statusCode);

		String response = res.asString();
		JsonPath jsonRes = new JsonPath(response);
		int status = jsonRes.getInt("status");
		String mes = jsonRes.getString("message");
		Assert.assertEquals(status, responseCode);
		Assert.assertEquals(mes, message);

	}

	@Parameters({ "dataEnv" })
	@Test
	public void validEmailToBeOnboardedUsedOtp(String dataEnv) throws IOException, ParseException {
		onboardingMiddlewareBase("ValidEmailToBeOnboardedUsedOtp", -1, "Invalid Otp", dataEnv, 200,
				"Onboarding a valid email address with a used Otp ");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void emptyEmailToBeOnboarded(String dataEnv) throws IOException, ParseException {
		onboardingMiddlewareBase("emptyEmailToBeOnboarded", -24, "Invalid Frontend Partner Vendor", dataEnv, 200,
				"Onboarding an empty email address with a used Otp");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void validEmailToBeOnboardedNoFingerprint(String dataEnv) throws IOException, ParseException {
		onboardingMiddlewareBase("validEmailToBeOnboardedNoFingerprint", 400, "Please provide fingerprints!", dataEnv,
				200, "Onboarding user without fingerprints");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void invalidEmailToBeOnboarded(String dataEnv) throws IOException, ParseException {
		onboardingMiddlewareBase("invalidEmailToBeOnboarded", -24, "Invalid Frontend Partner Vendor", dataEnv, 200,
				"Onboarding an invalid email address");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void validEmailEmptyOtp(String dataEnv) throws IOException, ParseException {
		onboardingMiddlewareBase("validEmailEmptyOtp", -1, "Empty Otp field", dataEnv, 200,
				"Onboarding an invalid email address");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void emptyOnboardedByEmail(String dataEnv) throws IOException, ParseException {
		onboardingMiddlewareBase("emptyOnboardedByEmail", -2, "Initiator user with email,  was not found", dataEnv, 200,
				"Empty email initiator ");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void invalidOnboardedByEmail(String dataEnv) throws IOException, ParseException {
		onboardingMiddlewareBase("invalidOnboardedByEmail", -2, "Initiator user with email,  was not found", dataEnv,
				200, "Invalid email initiator ");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void emptyMacAddress(String dataEnv) throws IOException, ParseException {
		onboardingMiddlewareBase("emptyMacAddress", -3, "Empty Mac address", dataEnv, 200, "Empty MAC address ");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void invalidDateStamp(String dataEnv) throws IOException, ParseException {
		onboardingMiddlewareBase("invalidDateStamp", -3, "Invalid date Stamp", dataEnv, 200, "Invalid date stamp ");
	}
}
