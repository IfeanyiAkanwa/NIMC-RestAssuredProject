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

public class Configs extends TestBase {

	public static String middleWareUrl = System.getProperty("middleware-url", "http://54.219.7.42:8585");

	@Parameters({ "dataEnv" })
	public void configsBase(String dataEnv, int code, String description, int statusCode, String methDesc)
			throws FileNotFoundException, IOException, ParseException {
		RestAssured.baseURI = middleWareUrl;

		JSONParser parser = new JSONParser();
		JSONObject config = (JSONObject) parser.parse(new FileReader("resources/" + dataEnv + "/Configs.config.json"));
		String endPoint = (String) config.get("endPoint");

		JsonObject requestBody = TestUtils.generateJson(dataEnv, "Configs.config.json", "configs");
		String body = requestBody.toString();

		TestUtils.testTitle("ENDPOINT");
		testInfo.get().info(middleWareUrl + endPoint);

		TestUtils.testTitle("Request Body");
		testInfo.get().info(MarkupHelper.createCodeBlock(body));

		TestUtils.testTitle("DESCRIPTION");
		testInfo.get().info(methDesc);

		Response res = given().header("Content-Type", "application/json").

				body(requestBody).

				when().post(endPoint).then().assertThat().extract().response();

		TestUtils.testTitle("Response Body");
		String response = res.asString();
		testInfo.get().info(MarkupHelper.createCodeBlock(response));

		int sCode = res.getStatusCode();
		Assert.assertEquals(sCode, statusCode);

		JsonPath jsonRes = new JsonPath(response);
		String desc = jsonRes.getString("settings.description");
		int cod = jsonRes.getInt("settings.code");

		Assert.assertEquals(desc, description);
		Assert.assertEquals(cod, code);

	}

	@Parameters({ "dataEnv" })
	@Test
	public void validDeviceId(String dataEnv) throws FileNotFoundException, IOException, ParseException {
		configsBase(dataEnv, 0, "Settings successfully returned", 200, "This method is to ensure settings is returned");
	}

}