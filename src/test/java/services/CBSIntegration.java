package services;

import static io.restassured.RestAssured.given;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utils.TestBase;
import utils.TestUtils;

public class CBSIntegration extends TestBase {
	private String fetchEnrollmentCentreResourceURL;
	private String deviceUpdateNotificationResourceURL;
	private String newDeviceNotificationResourceURL;
	private String enrollmentNotificationResourceURL;
	private JsonObject deviceUpdateNotificationBody;
	private JsonObject enrollmentNotificationBody;
	private JsonObject newDeviceNotificationBody;
	public static String cbsUrl = System.getProperty("cbs-url", "https://nimctest.seamfix.com/cbs");

	@Parameters({ "dataEnv" })
	@BeforeMethod
	public void init(String dataEnv) throws JsonSyntaxException, IOException, ParseException {
		RestAssured.baseURI = cbsUrl;
		String path = "resources/" + dataEnv + "/cbsIntegration.config.json";

		JsonObject config = new Gson().fromJson(new String(Files.readAllBytes(Paths.get(path))), JsonObject.class);
		newDeviceNotificationResourceURL = (String) config.get("newDeviceNotificationResourceURL").getAsString();
		deviceUpdateNotificationResourceURL = (String) config.get("deviceUpdateNotificationResourceURL").getAsString();
		fetchEnrollmentCentreResourceURL = (String) config.get("fetchEnrollmentCentreResourceURL").getAsString();
		enrollmentNotificationResourceURL = (String) config.get("enrollmentNotificationResourceURL").getAsString();
		deviceUpdateNotificationBody = config.get("deviceUpdateNotificationBody").getAsJsonObject();
		enrollmentNotificationBody = config.get("enrollmentNotificationBody").getAsJsonObject();
		newDeviceNotificationBody = config.get("newDeviceNotificationBody").getAsJsonObject();
	}

	public void fetchEnrollmentCentresBase(int status, String message, int code, String dataEnv, int statusCode,
			String methDesc) throws IOException, ParseException {

		RestAssured.baseURI = cbsUrl;

		TestUtils.testTitle("<b>Description</b>");
		testInfo.get().info(methDesc);

		TestUtils.testTitle("<b>ENDPOINT</b>");
		testInfo.get().info(cbsUrl + fetchEnrollmentCentreResourceURL);

		Response res = given().header("Content-type", "application/json").

				when().get(fetchEnrollmentCentreResourceURL).then().assertThat().extract().response();

		TestUtils.testTitle("Response Body");
		String response = res.asString();
		testInfo.get().info(MarkupHelper.createCodeBlock(response));

		int statCode = res.getStatusCode();
		TestUtils.testTitle("Status Code");
		testInfo.get().info(Integer.toString(statCode));

		Assert.assertEquals(statCode, statusCode);

		JsonPath jsonRes = new JsonPath(response);
		int sta = jsonRes.getInt("status");
		int c = jsonRes.getInt("code");
		String mes = jsonRes.getString("message");

		Assert.assertEquals(sta, status);
		Assert.assertEquals(c, code);
		Assert.assertEquals(mes, message);

	}

	@Parameters({ "dataEnv" })
	@Test
	public void fetchEnrollmentCentres(String dataEnv) throws IOException, ParseException {
		fetchEnrollmentCentresBase(200, "OK", -1, dataEnv, 200, "To fetch enrollment centres");
	}

	public void newDeviceNotificationBase(JsonObject requestBody, int status, String message, String code,
			String dataEnv, int statusCode, String description) throws IOException, ParseException {

		RestAssured.baseURI = cbsUrl;

		String body = requestBody.toString();

		TestUtils.testTitle("<b>Description</b>");
		testInfo.get().info(description);

		TestUtils.testTitle("<b>ENDPOINT</b>");
		testInfo.get().info(cbsUrl + newDeviceNotificationResourceURL);

		TestUtils.testTitle("<b>Request Body</b>");
		testInfo.get().info(MarkupHelper.createCodeBlock(body));

		Response res = given().header("app-id", "com.seamfix.nimc_apk").header("Content-type", "application/json")
				.body(requestBody).

				when().post(newDeviceNotificationResourceURL).then().assertThat().extract().response();

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
		String cod = jsonRes.getString("code");

		Assert.assertEquals(sta, status);
		Assert.assertEquals(mes, message);
		Assert.assertEquals(cod, code);

	}

	@Parameters({ "dataEnv" })
	@Test
	public void newDeviceNotificationWithValidRequestID(String dataEnv) throws IOException, ParseException {
		newDeviceNotificationBase(newDeviceNotificationBody, -1, "Successful", "200", dataEnv, 200,
				"To check new device notification with valid requestID");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void newDeviceNotificationWithExistingRequestID(String dataEnv) throws IOException, ParseException {
		newDeviceNotificationBase(newDeviceNotificationBody, -1, "Duplicate request Id INFINIX-3565450991220082189",
				"409", dataEnv, 200, "To check new device notification with existing requestID");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void newDeviceNotificationWithEmptyRequestID(String dataEnv) throws IOException, ParseException {
		newDeviceNotificationBody.addProperty("requestId", "");
		newDeviceNotificationBase(newDeviceNotificationBody, 400, "Bad Parameters", null, dataEnv, 400,
				"To check new device notification with empty requestID");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void newDeviceNotificationWithEmptyESACode(String dataEnv) throws IOException, ParseException {
		newDeviceNotificationBody.addProperty("esaCode", "");
		newDeviceNotificationBase(newDeviceNotificationBody, 400, "Bad Parameters", null, dataEnv, 400,
				"To check new device notification with empty ESA code");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void newDeviceNotificationWithNonMatchingESAName(String dataEnv) throws IOException, ParseException {
		newDeviceNotificationBody.addProperty("esaCode", "NM0073");
		newDeviceNotificationBase(newDeviceNotificationBody, -1, "Invalid ESA name Seamfix", "400", dataEnv, 200,
				"To check new device notification with valid ESA code and non matching ESA name");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void newDeviceNotificationWithValidESACodeInvalidESAName(String dataEnv) throws IOException, ParseException {
		newDeviceNotificationBody.addProperty("esaName", "Seam");
		newDeviceNotificationBase(newDeviceNotificationBody, -1, "Invalid ESA name Seam", "400", dataEnv, 200,
				"To check new device notification with valid ESA code and invalid ESA name");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void newDeviceNotificationWithValidESACodeAndMatchingESAName(String dataEnv)
			throws IOException, ParseException {
		newDeviceNotificationBody.addProperty("esaName", "Seamfix");
		newDeviceNotificationBody.addProperty("esaCode", "NM0093");
		newDeviceNotificationBase(newDeviceNotificationBody, -1, "Duplicate request Id INFINIX-3565450991220082189",
				"409", dataEnv, 200, "To check new device notification with valid ESA code and matching ESA name");
	}

	public void enrollmentNotificationBase(JsonObject requestBody, int status, String message, String code,
			String dataEnv, int statusCode, String description) throws IOException, ParseException {

		RestAssured.baseURI = cbsUrl;

		String body = requestBody.toString();

		TestUtils.testTitle("<b>Description</b>");
		testInfo.get().info(description);

		TestUtils.testTitle("<b>ENDPOINT</b>");
		testInfo.get().info(cbsUrl + enrollmentNotificationResourceURL);

		TestUtils.testTitle("<b>Request Body</b>");
		testInfo.get().info(MarkupHelper.createCodeBlock(body));

		Response res = given().header("app-id", "com.seamfix.nimc_apk").header("Content-type", "application/json")
				.body(requestBody).

				when().post(enrollmentNotificationResourceURL).then().assertThat().extract().response();

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
		String cod = jsonRes.getString("code");

		Assert.assertEquals(sta, status);
		Assert.assertEquals(mes, message);
		Assert.assertEquals(cod, code);

	}

	@Parameters({ "dataEnv" })
	@Test
	public void enrollmentNotificationWithValidTrackingID(String dataEnv) throws IOException, ParseException {
		enrollmentNotificationBody.addProperty("trackingId", TestUtils.generateRandom(5, true, true));
		enrollmentNotificationBase(enrollmentNotificationBody, -1, "Request was successful!", "201", dataEnv, 200,
				"To check enrollment notification with existing tracking ID");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void enrollmentNotificationWithExistingTrackingID(String dataEnv) throws IOException, ParseException {
		enrollmentNotificationBody.addProperty("trackingId", "33110BA556");
		enrollmentNotificationBase(enrollmentNotificationBody, -1,
				"At least one notification for node X0-X0-X0-X0-X0  with tracking Id 33110BA556 already exists", "409",
				dataEnv, 200, "To check enrollment notification with existing tracking ID");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void enrollmentNotificationWithEmptyTrackingID(String dataEnv) throws IOException, ParseException {
		enrollmentNotificationBody.addProperty("trackingId", "");
		enrollmentNotificationBase(enrollmentNotificationBody, -1, "TrackingID should not be empty", "409", dataEnv,
				200, "To check enrollment notification with empty trackingID");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void enrollmentNotificationWithEmptyEnrollmentType(String dataEnv) throws IOException, ParseException {
		enrollmentNotificationBody.addProperty("enrollmentType", "");
		enrollmentNotificationBase(enrollmentNotificationBody, -1, "enrollmentType  not supported ", "400", dataEnv,
				200, "To check enrollment notification with empty enrollment type");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void enrollmentNotificationWithInvalidEnrollmentType(String dataEnv) throws IOException, ParseException {
		enrollmentNotificationBody.addProperty("enrollmentType", "enroll");
		enrollmentNotificationBase(enrollmentNotificationBody, -1, "enrollmentType enroll not supported ", "400",
				dataEnv, 200, "To check enrollment notification with invalid enrollment type");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void enrollmentNotificationWithInvalidTimeSentToBackendEnrollmentType(String dataEnv)
			throws IOException, ParseException {
		enrollmentNotificationBody.addProperty("timeSentToBackend", "03-04-2019");
		enrollmentNotificationBase(enrollmentNotificationBody, -1,
				"Invalid format for Time Sent to Backend. use yyyy-MM-dd'T'HH:mm:ss", "400", dataEnv, 200,
				"To check enrollment notification with invalid time sent to backend");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void enrollmentNotificationWithInvalidNodeIDEnrollmentType(String dataEnv)
			throws IOException, ParseException {
		enrollmentNotificationBody.addProperty("nodeId", "M0-T0-X0-X0-X0");
		enrollmentNotificationBase(enrollmentNotificationBody, -1, "Node with id M0-T0-X0-X0-X0 not found", "404",
				dataEnv, 200, "To check enrollment notification with invalid node Id");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void enrollmentNotificationWithEmptyNodeIDEnrollmentType(String dataEnv) throws IOException, ParseException {
		enrollmentNotificationBody.addProperty("nodeId", "");
		enrollmentNotificationBase(enrollmentNotificationBody, -1, "Node with id  not found", "404", dataEnv, 200,
				"To check enrollment notification with empty node Id");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void enrollmentNotificationWithEmptyESACodeEnrollmentType(String dataEnv)
			throws IOException, ParseException {
		enrollmentNotificationBody.addProperty("esaCode", "");
		enrollmentNotificationBase(enrollmentNotificationBody, -1, "esaCode: must not be blank", "400", dataEnv, 200,
				"To check enrollment notification with empty esaCode");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void enrollmentNotificationWithInvalidESACodeEnrollmentType(String dataEnv)
			throws IOException, ParseException {
		enrollmentNotificationBody.addProperty("esaCode", "100#");
		enrollmentNotificationBase(enrollmentNotificationBody, -1, "enter a valid esaCode", "400", dataEnv, 200,
				"To check enrollment notification with invalid esaCode");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void enrollmentNotificationWithInvalidDeviceIDEnrollmentType(String dataEnv)
			throws IOException, ParseException {
		enrollmentNotificationBody.addProperty("trackingId", TestUtils.generateRandom(5, true, true));
		enrollmentNotificationBody.addProperty("deviceId", "HENA-123");
		enrollmentNotificationBase(enrollmentNotificationBody, -1,
				"Node with identifier HENA-123 and provider Seamfix not found", "404", dataEnv, 200,
				"To check enrollment notification with invalid deviceID");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void enrollmentNotificationWithEmptyDeviceIDEnrollmentType(String dataEnv)
			throws IOException, ParseException {
		enrollmentNotificationBody.addProperty("trackingId", TestUtils.generateRandom(5, true, true));
		enrollmentNotificationBody.addProperty("deviceId", "");
		enrollmentNotificationBase(enrollmentNotificationBody, -1, "Device ID should not be empty", "404", dataEnv, 200,
				"To check enrollment notification with empty deviceID");
	}
	
//	public void deviceUpdateNotificationBase(JsonObject requestBody, int status, String message, String code,
//			String dataEnv, int statusCode, String description) throws IOException, ParseException {
//
//		RestAssured.baseURI = url;
//
//		String body = requestBody.toString();
//
//		TestUtils.testTitle("<b>Description</b>");
//		testInfo.get().info(description);
//
//		TestUtils.testTitle("<b>ENDPOINT</b>");
//		testInfo.get().info(baseUrl + deviceUpdateNotificationResourceURL);
//
//		TestUtils.testTitle("<b>Request Body</b>");
//		testInfo.get().info(MarkupHelper.createCodeBlock(body));
//
//		Response res = given().header("app-id", "com.seamfix.nimc_apk").header("Content-type", "application/json")
//				.body(requestBody).
//
//				when().post(deviceUpdateNotificationResourceURL).then().assertThat().extract().response();
//
//		TestUtils.testTitle("Response Body");
//		String response = res.asString();
//		testInfo.get().info(MarkupHelper.createCodeBlock(response));
//
//		int statCode = res.getStatusCode();
//		TestUtils.testTitle("Status Code");
//		testInfo.get().info(Integer.toString(statCode));
//
//		Assert.assertEquals(statCode, statusCode);
//
//		JsonPath jsonRes = new JsonPath(response);
//		int sta = jsonRes.getInt("status");
//		String mes = jsonRes.getString("message");
//		String cod = jsonRes.getString("code");
//
//		Assert.assertEquals(sta, status);
//		Assert.assertEquals(mes, message);
//		Assert.assertEquals(cod, code);
//
//	}

//	@Parameters({ "dataEnv" })
//	@Test(groups = { "Regression", "Health Check" })
//	public void verifyValidRequestIDDeviceUpdateNotification(String dataEnv) throws IOException, ParseException {
//		deviceUpdateNotificationBody.addProperty("requestId", TestUtils.generateRandom(5, true, true));
//		deviceUpdateNotificationBase(deviceUpdateNotificationBody, -1, "Notification Received", "200", dataEnv, 200,
//				"To verify device update notification with valid requestID");
//	}
//
//	@Parameters({ "dataEnv" })
//	@Test
//	public void verifyExistingRequestIDDeviceUpdateNotification(String dataEnv) throws IOException, ParseException {
//		deviceUpdateNotificationBody.addProperty("requestId", "INFINIX-42333509931887004-COM.SEAMFIX.NIMC_APK3");
//		deviceUpdateNotificationBase(deviceUpdateNotificationBody, -1,
//				"An update request for node HENA-359500002567921 with request id INFINIX-42333509931887004-COM.SEAMFIX.NIMC_APK3 already exists",
//				"409", dataEnv, 200, "To verify device update notification with existing requestID");
//	}
//
//	@Parameters({ "dataEnv" })
//	@Test
//	public void verifyEmptyRequestIDDeviceUpdateNotification(String dataEnv) throws IOException, ParseException {
//		deviceUpdateNotificationBody.addProperty("requestId", "");
//		deviceUpdateNotificationBase(deviceUpdateNotificationBody, 400, "Bad Parameters", null, dataEnv, 400,
//				"To verify device update notification with empty requestID");
//	}
//
//	@Parameters({ "dataEnv" })
//	@Test
//	public void verifyWrongESACodeDeviceUpdateNotification(String dataEnv) throws IOException, ParseException {
//		deviceUpdateNotificationBody.addProperty("esaCode", "NM0973");
//		deviceUpdateNotificationBase(deviceUpdateNotificationBody, -1, "ESA with code NM0973 not found", "400", dataEnv, 200,
//				"To verify device update notification with wrong ESA code");
//	}
//	
//	@Parameters({ "dataEnv" })
//	@Test
//	public void verifyEmptyESACodeDeviceUpdateNotification(String dataEnv) throws IOException, ParseException {
//		deviceUpdateNotificationBody.addProperty("esaCode", "");
//		deviceUpdateNotificationBase(deviceUpdateNotificationBody, 400, "Bad Parameters", null, dataEnv, 400,
//				"To verify device update notification with empty ESA code");
//	}
//
//	@Parameters({ "dataEnv" })
//	@Test
//	public void verifyValidESACodeDeviceUpdateNotification(String dataEnv) throws IOException, ParseException {
//		deviceUpdateNotificationBody.addProperty("requestId", TestUtils.generateRandom(5, true, true));
//		deviceUpdateNotificationBody.addProperty("esaCode", "NM0073");
//		deviceUpdateNotificationBase(deviceUpdateNotificationBody, -1, "Notification Received", "200", dataEnv, 200,
//				"To verify device update notification with valid ESA code");
//	}
//	@Parameters({ "dataEnv" })
//	@Test
//	public void verifyActiveStatusDeviceUpdateNotification(String dataEnv) throws IOException, ParseException {
//		deviceUpdateNotificationBody.addProperty("requestId", TestUtils.generateRandom(5, true, true));
//		deviceUpdateNotificationBody.addProperty("status", "ACTIVE");
//		deviceUpdateNotificationBase(deviceUpdateNotificationBody, -1, "Notification Received", "200", dataEnv, 200,
//				"To verify device update notification for active status");
//	}
//	
//	@Parameters({ "dataEnv" })
//	@Test
//	public void verifyEmptyStatusDeviceUpdateNotification(String dataEnv) throws IOException, ParseException {
//	deviceUpdateNotificationBody.addProperty("status", "");
//		deviceUpdateNotificationBase(deviceUpdateNotificationBody, 400, "Bad Parameters", null, dataEnv, 400,
//				"To verify device update notification with empty status");
//	}
//	
//	@Parameters({ "dataEnv" })
//	@Test
//	public void verifyInActiveStatusDeviceUpdateNotification(String dataEnv) throws IOException, ParseException {
//		deviceUpdateNotificationBody.addProperty("status", "INACTIVE");
//		deviceUpdateNotificationBase(deviceUpdateNotificationBody, -1, "Node Status INACTIVE not supported", "400", dataEnv, 200,
//				"To verify device update notification with empty status");
//	}
//	
//	@Parameters({ "dataEnv" })
//	@Test
//	public void verifyDeactivatedDeviceUpdateNotification(String dataEnv) throws IOException, ParseException {
//		deviceUpdateNotificationBody.addProperty("requestId", TestUtils.generateRandom(5, true, true));
//		deviceUpdateNotificationBody.addProperty("status", "DEACTIVATED");
//		deviceUpdateNotificationBase(deviceUpdateNotificationBody, -1, "Notification Received", "200", dataEnv, 200,
//				"To verify device update notification for active status");
//	}
}
