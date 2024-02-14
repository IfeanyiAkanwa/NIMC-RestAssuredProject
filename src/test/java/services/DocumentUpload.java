package services;

import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.simple.JSONArray;
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

public class DocumentUpload extends TestBase {

    private String url = "http://54.219.7.42:8585";

    @Parameters({"dataEnv"})
    public void documentUploadPayloadBase(String testCase, int responseCode, String message, String dataEnv, int statusCode, int filUploadStat, String filUploadMes,String description) throws FileNotFoundException, IOException, ParseException {
        RestAssured.baseURI = url;

        JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader("resources/"+dataEnv+"/documentUpload.config.json"));

        String endpoint = (String) config.get("endPoint");
        JSONArray requestBody = (JSONArray) config.get(testCase);
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
        int resCode = jsonRes.getInt("status");
        String mes = jsonRes.getString("message");

        int fileUploadStatus = jsonRes.getInt("fileUploadResponse[0].status");
        String fileUploadMessage = jsonRes.getString("fileUploadResponse[0].message");
        Assert.assertEquals(resCode, responseCode);
        Assert.assertEquals(mes, message);
        Assert.assertEquals(fileUploadStatus, filUploadStat);
        Assert.assertEquals(fileUploadMessage, filUploadMes);

    }


    @Parameters({"dataEnv"})
    @Test
    public void uploadValidFileValidEncodedDataValid(String dataEnv) throws FileNotFoundException, IOException, ParseException {
        documentUploadPayloadBase("uploadValidFileValidEncodedDataValid", 0, "Success", dataEnv, 200, 0, "Success", "Upload valid file, with valid encoded message and transaction ID");
    }

    @Parameters({"dataEnv"})
    @Test
    public void uploadValidFileEmptyEncodedData(String dataEnv) throws FileNotFoundException, IOException, ParseException {
        documentUploadPayloadBase("uploadValidFileEmptyEncodedData", 0, "Success", dataEnv, 200, -4, "Encoded Data is empty", "Upload valid file, with empty encoded message and transaction ID");
    }

    @Parameters({"dataEnv"})
    @Test
    public void uploadEmptyFileValidEncodedData2(String dataEnv) throws FileNotFoundException, IOException, ParseException {
        documentUploadPayloadBase("uploadEmptyFileValidEncodedData2", 0, "Success", dataEnv, 200, -4, "file name not provided", "Upload empty file, with valid encoded message and transaction ID");
    }


    @Parameters({"dataEnv"})
    @Test
    public void uploadEmptyFileValidEncodedData(String dataEnv) throws FileNotFoundException, IOException, ParseException {
        documentUploadPayloadBase("uploadEmptyFileValidEncodedData", 0, "Success", dataEnv, 200, -4, "file name not provided", "Upload empty file, with valid encoded message and transaction ID");
    }


    @Parameters({"dataEnv"})
    @Test
    public void uploadEmptyFileValidEncodedDataEmptyTrackingId(String dataEnv) throws FileNotFoundException, IOException, ParseException {
        documentUploadPayloadBase("uploadEmptyFileValidEncodedDataEmptyTrackingId", 0, "Success", dataEnv, 200, -3, "Tracking Id not provided", "Upload valid file, with valid encoded message and empty transaction ID");
    }


    @Parameters({"dataEnv"})
    @Test
    public void uploadValidFileValidEncodedDataInvalidTransactionID(String dataEnv) throws FileNotFoundException, IOException, ParseException {
        documentUploadPayloadBase("uploadValidFileValidEncodedDataInvalidTransactionID", 0, "Success", dataEnv, 200, -3, "Tracking Id is invalid", "Upload valid file, with valid encoded message and invalid transaction ID");
    }


    @Parameters({"dataEnv"})
    @Test
    public void uploadInvalidFileExtensionValidEncodedData(String dataEnv) throws FileNotFoundException, IOException, ParseException {
        documentUploadPayloadBase("uploadInvalidFileExtensionValidEncodedData", 0, "Success", dataEnv, 200, -4, "Invalid file extension", "Upload invalid file extension, with valid encoded message and invalid transaction ID");
    }


}
