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

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utils.TestBase;
import utils.TestUtils;

public class VendorManagement extends TestBase {

	public static String cbsUrl = System.getProperty("cbs-url", "https://nimctest.seamfix.com/cbs");

	public static void vendorManagementBase(String testCase, int status, String message, int code, String dataEnv,
			int statusCode, String methDesc) throws IOException, ParseException {

		RestAssured.baseURI = cbsUrl;

		JSONParser parser = new JSONParser();
		JSONObject config = (JSONObject) parser
				.parse(new FileReader("resources/" + dataEnv + "/vendorManagement.config.json"));

		String endpoint = (String) config.get(testCase);

		TestUtils.testTitle("<b>Description</b>");
		testInfo.get().info(methDesc);

		TestUtils.testTitle("<b>ENDPOINT</b>");
		testInfo.get().info(cbsUrl + endpoint);

		Response res = given().header("app-id", "com.seamfix.nimc_apk").header("Content-type", "application/json").

				when().get(endpoint).then().assertThat().extract().response();

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
		int c = jsonRes.getInt("code");

		Assert.assertEquals(sta, status);
		Assert.assertEquals(mes, message);
		Assert.assertEquals(c, code);

	}

	@Parameters({ "dataEnv" })
	@Test(groups = { "Regression", "Health Check" })
	public void verifyValidVendorStatus(String dataEnv) throws IOException, ParseException {
		vendorManagementBase("validVendorResourceURL", -1, "OK", 200, dataEnv, 200,
				"To get the status of a valid vendor");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void verifyInvalidDeviceIDVendorStatus(String dataEnv) throws IOException, ParseException {
		vendorManagementBase("invalidDeviceIDVendorResourceURL", -1,
				"Node with identifier X0-RR-X0-X0 and provider Seamfix not found", 400, dataEnv, 200,
				"To get the status of a vendor with Invalid DeviceID");
	}

	@Parameters({ "dataEnv" })
	@Test
	public void verifyInvalidESACodeVendorStatus(String dataEnv) throws IOException, ParseException {
		vendorManagementBase("invalidESACodeVendorResourceURL", -1, "Entity not found", 400, dataEnv, 200,
				"To get the status of a vendor with Invalid ESA code");
	}

}
