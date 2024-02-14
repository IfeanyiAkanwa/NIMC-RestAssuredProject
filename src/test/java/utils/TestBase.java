package utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import org.json.simple.parser.ParseException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import utils.SendMail;

public class TestBase {

	public static ExtentReports reports;
	public static ExtentHtmlReporter htmlReporter;
	private static ThreadLocal<ExtentTest> parentTest = new ThreadLocal<ExtentTest>();
	public static ThreadLocal<ExtentTest> testInfo = new ThreadLocal<ExtentTest>();

	public static String baseUrl = System.getProperty("seamfix-url", "http://54.219.7.42:7110");
	public static String middleWareUrl = System.getProperty("middleware-url", "http://54.219.7.42:8585");
	public static String cbsUrl = System.getProperty("cbs-url", "https://nimctest.seamfix.com/cbs");
	public static String recipients;

	@BeforeSuite
	@Parameters({ "groupReport", "dataEnv" })
	public void setUp(String groupReport, String dataEnv) throws IOException, ParseException {
		htmlReporter = new ExtentHtmlReporter(new File(System.getProperty("user.dir") + groupReport));
		reports = new ExtentReports();
		reports.setSystemInfo("Test Environment", baseUrl);
		reports.attachReporter(htmlReporter);
	}

	@BeforeClass
	public synchronized void beforeClass() {
		ExtentTest parent = reports.createTest(getClass().getName());
		parentTest.set(parent);

	}

	@BeforeMethod
	public synchronized void beforeMethod(Method method) {
		ExtentTest child = ((ExtentTest) parentTest.get()).createNode(method.getName());
		testInfo.set(child);
	}

	@AfterMethod
	public synchronized void afterMethod(ITestResult result) {

		for (String group : result.getMethod().getGroups())
			testInfo.get().assignCategory(group);

		if (result.getStatus() == ITestResult.FAILURE)
			testInfo.get().fail(result.getThrowable());
		else if (result.getStatus() == ITestResult.SKIP)
			testInfo.get().skip(result.getThrowable());
		else
			testInfo.get().pass("Test passed");

		reports.flush();
	}

	@Parameters({ "toMails", "groupReport" })
	@AfterSuite(description = "clean up report after test suite")
	public void cleanup(String toMails, String groupReport) {
		reports.flush();
		recipients = System.getProperty("email_list", toMails);
		SendMail.ComposeGmail("NIMC Services Automation Test Report<seamfix.test.report@gmail.com>", recipients,
				groupReport);

	}
}
