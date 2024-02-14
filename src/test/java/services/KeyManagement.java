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

public class KeyManagement extends TestBase {
	private String PKIKeyPairResourceURL;
	private String generateKeyCollectionResourceURL;
	private String publicKeyResourceURL;
	private String generateKeyResourceURL;
	public static String baseUrl = System.getProperty("seamfix-url", "http://54.219.7.42:7110");

	@Parameters({ "dataEnv" })
	@BeforeMethod
	public void init(String dataEnv) throws JsonSyntaxException, IOException, ParseException {
		RestAssured.baseURI = baseUrl;
		String path = "resources/" + dataEnv + "/keyManagement.config.json";

		JsonObject config = new Gson().fromJson(new String(Files.readAllBytes(Paths.get(path))), JsonObject.class);
		PKIKeyPairResourceURL = (String) config.get("PKIKeyPairResourceURL").getAsString();
		generateKeyCollectionResourceURL = (String) config.get("generateKeyCollectionResourceURL").getAsString();
		publicKeyResourceURL = (String) config.get("publicKeyResourceURL").getAsString();
		generateKeyResourceURL = (String) config.get("generateKeyResourceURL").getAsString();
	}

	public void getPKIKeyPairBase(String privateString, String publicString, String dataEnv, int statusCode,
			String description) throws IOException, ParseException {

		TestUtils.testTitle("<b>Description</b>");
		testInfo.get().info(description);

		TestUtils.testTitle("<b>ENDPOINT</b>");
		testInfo.get().info(baseUrl + PKIKeyPairResourceURL);

		Response res = given().header("app-id", "com.test.test").header("Content-type", "application/json").

				when().get(PKIKeyPairResourceURL).then().assertThat().extract().response();

		TestUtils.testTitle("Response Body");
		String response = res.asString();
		testInfo.get().info(MarkupHelper.createCodeBlock(response));

		int statCode = res.getStatusCode();
		TestUtils.testTitle("Status Code");
		testInfo.get().info(Integer.toString(statCode));

		Assert.assertEquals(statCode, statusCode);

		JsonPath jsonRes = new JsonPath(response);
		String pri = jsonRes.getString("private");
		String pr = pri.substring(0, 4);
		String pub = jsonRes.getString("public");
		String pu = pub.substring(0, 4);
		Assert.assertEquals(pr, privateString);
		Assert.assertEquals(pu, publicString);

	}

	@Parameters({ "dataEnv" })
	@Test
	public void verifyPrivateandPublicKeys(String dataEnv) throws IOException, ParseException {
		getPKIKeyPairBase("MIIE", "MIIB", dataEnv, 200, "Verify that public and private keys are returned");
	}

	public void getPublicKeyBase(String key, String dataEnv, int statusCode, String description)
			throws IOException, ParseException {

		TestUtils.testTitle("<b>Description</b>");
		testInfo.get().info(description);

		TestUtils.testTitle("<b>ENDPOINT</b>");
		testInfo.get().info(baseUrl + publicKeyResourceURL);

		Response res = given().header("app-id", "com.test.test").header("Content-type", "application/json").

				when().get(publicKeyResourceURL).then().assertThat().extract().response();

		TestUtils.testTitle("Response Body");
		String response = res.asString();
		testInfo.get().info(MarkupHelper.createCodeBlock(response));

		int statCode = res.getStatusCode();
		TestUtils.testTitle("Status Code");
		testInfo.get().info(Integer.toString(statCode));

		Assert.assertEquals(statCode, statusCode);

		JsonPath jsonRes = new JsonPath(response);
		String ke = jsonRes.getString("key");
		String k = ke.substring(0, 4);
		Assert.assertEquals(k, key);
	}

	@Parameters({ "dataEnv" })
	@Test
	public void verifyPublicKey(String dataEnv) throws IOException, ParseException {
		getPublicKeyBase("MIIB", dataEnv, 200, "Verify that public key is returned");
	}
//	public void generateKeyCollectionBase(String testCase, int id, String currentPrivateKey, String dataEnv,
//			int statusCode, String description) throws IOException, ParseException {
//
//		JsonObject requestBody = TestUtils.generateJson(dataEnv, "keyManagement.config.json", testCase);
//		String body = requestBody.toString();
//
//		TestUtils.testTitle("<b>Description</b>");
//		testInfo.get().info(description);
//
//		TestUtils.testTitle("<b>ENDPOINT</b>");
//		testInfo.get().info(baseUrl + generateKeyCollectionResourceURL);
//
//		TestUtils.testTitle("<b>Request Body</b>");
//		testInfo.get().info(MarkupHelper.createCodeBlock(body));
//
//		Response res = given().header("app-id", "com.test.test").header("Content-type", "application/json").
//
//				body(requestBody).when().post(generateKeyCollectionResourceURL).then().assertThat().extract()
//				.response();
//
//		TestUtils.testTitle("Response Body");
//		testInfo.get().info(MarkupHelper.createCodeBlock(res.prettyPrint()));
//
//		int statCode = res.getStatusCode();
//		TestUtils.testTitle("Status Code");
//		testInfo.get().info(Integer.toString(statCode));
//
//		Assert.assertEquals(statCode, statusCode);
//
//		String response = res.asString();
//		JsonPath jsonRes = new JsonPath(response);
//		int i = jsonRes.getInt("id");
//		String cpk = jsonRes.getString("currentPrivateKey");
//		String c = cpk.substring(0, 4);
//		Assert.assertEquals(i, id);
//		Assert.assertEquals(c, currentPrivateKey);
//
//	}

//	@Parameters({ "dataEnv" })
//	@Test
//	public void verifyKeyCollection(String dataEnv) throws IOException, ParseException {
//		generateKeyCollectionBase("generateKeyCollection", 368559, "MIIE", dataEnv, 200,
//				"To verify that key collections are successfully generated");
//	}

//	public void generateKeyBase(String currentPrivateKey, String dataEnv, int statusCode, String description)
//			throws IOException, ParseException {
//
//		TestUtils.testTitle("<b>Description</b>");
//		testInfo.get().info(description);
//
//		TestUtils.testTitle("<b>ENDPOINT</b>");
//		testInfo.get().info(baseUrl + generateKeyResourceURL);
//
//		Response res = given().header("app-id", "com.test.test").header("Content-type", "application/json")
//
//				.when().get(generateKeyResourceURL).then().assertThat().extract().response();
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
//		String id = jsonRes.getString("id");
//		String cpk = jsonRes.getString("currentPrivateKey");
//		String c = cpk.substring(0, 4);
//		Assert.assertNotEquals(id, null);
//		Assert.assertEquals(c, currentPrivateKey);
//
//	}

//	@Parameters({ "dataEnv" })
//	@Test
//	public void verifyKeys(String dataEnv) throws IOException, ParseException {
//		generateKeyBase("MIIE", dataEnv, 200, "To verify that keys are successfully generated");
//	}

}