package services;

import java.io.IOException;
import java.sql.SQLException;

import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.markuputils.MarkupHelper;

import utils.ConnectDB;
import utils.TestBase;
import utils.TestUtils;

public class PayloadBackup extends TestBase {
	private static String payloadBackup;

	@Parameters({ "dataEnv" })
	@Test
	public void verifyPayloadBackupPath(String dataEnv) throws IOException, ParseException, SQLException {
		payloadBackup = ConnectDB.getPayloadBackup();
		
		TestUtils.testTitle("<b>Description</b>");
		testInfo.get().info("To verify that Payload Backup path is not empty/null");

		TestUtils.testTitle("Response Body");
		testInfo.get().info(MarkupHelper.createCodeBlock(payloadBackup));

		Assert.assertNotEquals(payloadBackup, null);
		Assert.assertNotEquals(payloadBackup, "");
	}
}