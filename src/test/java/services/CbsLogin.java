package services;

import com.aventstack.extentreports.markuputils.MarkupHelper;
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

public class CbsLogin extends TestBase {
	public static String baseUrl = System.getProperty("seamfix-url", "http://54.219.7.42:7110");

	@Parameters({ "dataEnv" })
	public void cbsLoginBase(String envsValue, int responseCode, String message, String dataEnv, int statusCode,
			String description) throws FileNotFoundException, IOException, ParseException {
		RestAssured.baseURI = baseUrl;

		JSONParser parser = new JSONParser();
		JSONObject config = (JSONObject) parser.parse(new FileReader("resources/" + dataEnv + "/CbsLogin.config.json"));
		JSONObject envs = (JSONObject) config.get(envsValue);

		String endpoint = (String) config.get("endpoint");
		String loginId = (String) envs.get("loginId");
		String pw = (String) envs.get("pw");
		String deviceIdentifier = (String) envs.get("deviceIdentifier");

		TestUtils.testTitle("Description");
		testInfo.get().info(description);

		TestUtils.testTitle("<b>ENDPOINT</b>");
		testInfo.get().info(baseUrl + endpoint);

		TestUtils.testTitle("Request Body");
		testInfo.get().info("<b> loginId: </b> " + loginId);
		testInfo.get().info("<b> pw: </b> " + pw);
		testInfo.get().info("<b> deviceIdentifier: </b> " + deviceIdentifier);

		Response res = given().header("Content-Type", "application/x-www-form-urlencoded")
				.header("App-Id", "com.seamfix.nimc_apk").header("X-APP-VERSION", "1.0")
				.header("X-DEVICE-ID",
						"AHVZ0xjS3gR1YJQFzpNJexTYyS5vrlpK21TThyPJZsa5tthZ6z7ncPXgfJZ0VFkhDTtdD5KgNIBFjAdFHRwSKa8S4_fTRWVypA")
				.header("X-TIMESTAMP", "1610985378")
				.header("X-USER-ID", "AHVZ0xjf2hXIibzchf9jI-wDFQumakfuTw39yj82v6AEyuO_FH9fRz1Rg_6DPauKWQ4phPP0uKx7")
				.header("SIGNATURE",
						"AHVZ0xiZxU0L4kC84GYRQMGWwKCm_8zeuEwhWl2E4HfZsYMfmJg1USddWjLo5aIIVT6O8ycfx4_OzKZMJGcKhEPx-QqTY81cIqMJftl8v52Mk-i4bOq2CzYkFFxvREy7szLQq2AsVK8j")
				.param("loginId", loginId).param("pw", pw).param("deviceIdentifier", deviceIdentifier).when()
				.post(endpoint).then().extract().response();

		TestUtils.testTitle("<b>Response Body</b>");
		String response = res.asString();
		testInfo.get().info(MarkupHelper.createCodeBlock(response));

		int statCode = res.getStatusCode();
		Assert.assertEquals(statCode, statusCode);

		JsonPath jsonRes = new JsonPath(response);
		int cod = jsonRes.getInt("status");
		String mes = jsonRes.getString("message");
		Assert.assertEquals(cod, responseCode);
		Assert.assertEquals(mes, message);

	}

	@Parameters({ "dataEnv" })
	@Test(groups = { "Regression", "Health Check" })
	public void ValidFepUserLogin(String dataEnv) throws FileNotFoundException, IOException, ParseException {

		cbsLoginBase("validLogin", 0, "Success", dataEnv, 200, "Valid Login with Valid CBS user");

	}

	@Parameters({ "dataEnv" })
	@Test
	public void invalidLoginIdAndPassword(String dataEnv) throws FileNotFoundException, IOException, ParseException {

		cbsLoginBase("invalidLoginIdAndPassword", -1, "Invalid login details!", dataEnv, 200,
				"Login with invalid CBS user");

	}

	@Parameters({ "dataEnv" })
	@Test
	public void emptyLoginId(String dataEnv) throws FileNotFoundException, IOException, ParseException {

		cbsLoginBase("emptyLoginId", -3, "Invalid input was provided", dataEnv, 200,
				"Login with empty user and a password");

	}

	@Parameters({ "dataEnv" })
	@Test
	public void emptyLoginIdEmptyPassword(String dataEnv) throws FileNotFoundException, IOException, ParseException {

		cbsLoginBase("emptyLoginIdEmptyPassword", -3, "Invalid input was provided", dataEnv, 200,
				"Login with empty user ID and empty password");

	}

	@Parameters({ "dataEnv" })
	@Test
	public void validLoginIdInvalidPassword(String dataEnv) throws FileNotFoundException, IOException, ParseException {

		cbsLoginBase("validLoginIdInvalidPassword", -1, "Invalid login details!", dataEnv, 200,
				"Login with a valid CBS user ID and invalid password");

	}

	@Parameters({ "dataEnv" })
	@Test
	public void validLoginIdEmptyPassword(String dataEnv) throws FileNotFoundException, IOException, ParseException {

		cbsLoginBase("validLoginIdEmptyPassword", -3, "Invalid input was provided", dataEnv, 200,
				"Login with a valid CBS user ID and empty password");

	}

	@Parameters({ "dataEnv" })
	@Test
	public void loginWithDeactivatedUser(String dataEnv) throws FileNotFoundException, IOException, ParseException {

		cbsLoginBase("loginWithDeactivatedUser", -1, "User not active!", dataEnv, 200,
				"Login with a deactivated CBS user");

	}

	@Parameters({ "dataEnv" })
	@Test
	public void loginWithWrongDeviceID(String dataEnv) throws FileNotFoundException, IOException, ParseException {

		cbsLoginBase("loginWithWrongDeviceID", -1, "Node with identifier DR and provider Seamfix not found", dataEnv,
				200, "Login with an invalid deviceID");

	}

	@Parameters({ "dataEnv" })
	@Test
	public void loginWithEmptyDeviceID(String dataEnv) throws FileNotFoundException, IOException, ParseException {

		cbsLoginBase("loginWithEmptyDeviceID", -19, "Unauthorized user login", dataEnv, 200,
				"Login with a empty Device ID");

	}
}
