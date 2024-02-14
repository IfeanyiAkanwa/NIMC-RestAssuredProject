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

public class Harmonization extends TestBase {
	public static String baseUrl = System.getProperty("seamfix-url", "http://54.219.7.42:7110");

	@Parameters({ "dataEnv" })
	public void harmonizationBase(String envsValue, int code, String description, String message, String dataEnv, int statusCode,
			String methDesc) throws FileNotFoundException, IOException, ParseException {
		RestAssured.baseURI = baseUrl;

		JSONParser parser = new JSONParser();
		JSONObject config = (JSONObject) parser
				.parse(new FileReader("resources/" + dataEnv + "/harmonization.config.json"));
		JSONObject envs = (JSONObject) config.get(envsValue);

		String endpoint = (String) config.get("endpoint");
		String recordId = (String) envs.get("recordId");

		TestUtils.testTitle("Description");
		testInfo.get().info(methDesc);

		TestUtils.testTitle("<b>ENDPOINT</b>");
		testInfo.get().info(baseUrl + endpoint);

		TestUtils.testTitle("Request Body");
		testInfo.get().info("<b> recordId: </b> " + recordId);

		Response res = given().header("Content-Type", "application/x-www-form-urlencoded")
				.header("App-Id", "com.seamfix.nimc_apk").param("recordId", recordId).when().post(endpoint).then().extract()
				.response();

		TestUtils.testTitle("Response Body");
		String response = res.asString();
		testInfo.get().info(MarkupHelper.createCodeBlock(response));

		int statCode = res.getStatusCode();
		Assert.assertEquals(statCode, statusCode);

		JsonPath jsonRes = new JsonPath(response);
		int cod = jsonRes.getInt("code");
		String desc = jsonRes.getString("description");
		String mes = jsonRes.getString("message");
		Assert.assertEquals(cod, code);
		Assert.assertEquals(desc, description);
		Assert.assertEquals(mes, message);

	}
	@Parameters({ "dataEnv" })
	@Test(groups = { "Regression", "Health Check" })
	public void ValidCapturedRecordStatus(String dataEnv) throws FileNotFoundException, IOException, ParseException {

		harmonizationBase("validCapturedRecord", 0, "SYNC_CONFIRMED", "0:null", dataEnv, 200, "To check for valid captured record status");

	}
	@Parameters({ "dataEnv" })
	@Test
	public void invalidCapturedRecordStatus(String dataEnv) throws FileNotFoundException, IOException, ParseException {

		harmonizationBase("invalidCapturedRecord", -1, "No Harmonizer Record exists!", "No Harmonizer Record exists!", dataEnv, 200, "To check for invalid captured record status");

	}
	@Parameters({ "dataEnv" })
	@Test
	public void emptyCapturedRecordStatus(String dataEnv) throws FileNotFoundException, IOException, ParseException {

		harmonizationBase("emptyCapturedRecord", -1, "Enter a Record ID", "Enter a Record ID", dataEnv, 200, "To check for empty captured record status");

	}
}
