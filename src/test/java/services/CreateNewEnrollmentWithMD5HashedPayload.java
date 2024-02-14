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

public class CreateNewEnrollmentWithMD5HashedPayload extends TestBase {

    private static String url = "http://54.219.7.42:8585";

    @Parameters({"dataEnv"})
    public void createNewEnrollmentWithMD5HashedPayloadBase(String testCase, int responseCode, String message, String dataEnv, int statusCode, String description) throws FileNotFoundException, IOException, ParseException {
        RestAssured.baseURI = url;

        JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader("resources/"+dataEnv+"/CreateNewEnrollmentWithMD5HashedPayload.config.json"));

        String endpoint = (String) config.get("endpoint");
        //String requestBody = TestUtils.generateString2("CreateNewEnrollmentWithMD5HashedPayload.config.json",testCase,dataEnv);
        JsonObject requestBody = TestUtils.generateJson(dataEnv, "CreateNewEnrollmentWithMD5HashedPayload.config.json", testCase);
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
    public void validFileNameAndValidEncryptedData(String dataEnv) throws IOException, ParseException {
        createNewEnrollmentWithMD5HashedPayloadBase("validFileNameAndValidEncryptedData", 0, "Success", dataEnv, 200, "Valid File name and Valid Encrypted data was passed");

    }

    @Parameters({"dataEnv"})
    @Test
    public void invalidFileNameAndValidEncryptedData(String dataEnv) throws IOException, ParseException {
        createNewEnrollmentWithMD5HashedPayloadBase("invalidFileNameAndValidEncryptedData", -5, "invalid digest", dataEnv, 200, "Invalid filename with Valid encrypted data was passed");

    }

    @Parameters({"dataEnv"})
    @Test
    public void validFileNameAndInvalidEncryptedData(String dataEnv) throws IOException, ParseException {
        createNewEnrollmentWithMD5HashedPayloadBase("validFileNameAndInvalidEncryptedData", -5, "invalid digest", dataEnv, 200, "Valid file name with the encrypted data tampered with");

    }

}
