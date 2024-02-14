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

public class PersonalizationEncryption extends TestBase {

    private static String url = "http://54.219.7.42:8585";

    @Parameters({"dataEnv"})
    public void PersonalizationEncryptionBase(String testCase, int responseCode, String message, String dataEnv, int statusCode, String description) throws FileNotFoundException, IOException, ParseException {
        RestAssured.baseURI = url;

        JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader("resources/"+dataEnv+"/personalizationEncryption.config.json"));

        String endpoint = (String) config.get("endpoint");
        JsonObject requestBody = TestUtils.generateJson(dataEnv, "personalizationEncryption.config.json", testCase);
        String body = requestBody.toString();

        TestUtils.testTitle("<b>Description</b>");
        testInfo.get().info(description);

        TestUtils.testTitle("<b>ENDPOINT</b>");
        testInfo.get().info(url + endpoint);

        TestUtils.testTitle("<b>Request Body</b>");
        testInfo.get().info(MarkupHelper.createCodeBlock(body));

        Response res =	given().
                header("app-id","com.seamfix.nimc_apk").
                header("Content-type", "application/json").

                body(requestBody).

                when().
                post(endpoint).
                then().assertThat().extract().response();

        TestUtils.testTitle("Response Body");
        testInfo.get().info(MarkupHelper.createCodeBlock(res.prettyPrint()));

        int statCode = res.getStatusCode();
        TestUtils.testTitle("Status Code");
        testInfo.get().info(Integer.toString( statCode ));


        Assert.assertEquals(statCode, statusCode);


        String response = res.asString();
        JsonPath jsonRes = new JsonPath(response);
        int status = jsonRes.getInt("status");
        String mes = jsonRes.getString("message");
        Assert.assertEquals(status, responseCode);
        Assert.assertEquals(mes, message);


    }


    @Parameters({"dataEnv"})
    @Test
    public void validClientRequestValidEncryptedNin(String dataEnv) throws IOException, ParseException {
        PersonalizationEncryptionBase("validClientRequestValidEncryptedNin", 0, "Success", dataEnv, 200, "This is a personalization request with valid client request with a valid encrypted NIN");
    }

    @Parameters({"dataEnv"})
    @Test
    public void validClientRequestInvalidEncryptedNin(String dataEnv) throws IOException, ParseException {
        PersonalizationEncryptionBase("validClientRequestInvalidEncryptedNin", -1, "Success", dataEnv, 400, "This is a personalization request with valid client request with an invalid encrypted NIN");
    }

    @Parameters({"dataEnv"})
    @Test
    public void invalidClientRequestValidEncryptedNin(String dataEnv) throws IOException, ParseException {
        PersonalizationEncryptionBase("invalidClientRequestValidEncryptedNin", -1, "Success", dataEnv, 400, "This is a personalization request with invalid client request with an valid encrypted NIN");
    }


}
