package utilities;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import java.util.Set;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ListenerTest implements ITestListener {

	public static Logger loger = LogManager.getLogger("eventmanager");
	ExtentReports extent = ExtentReportConfig.extentReportGenerator();
	ExtentTest test;

	@Override
	public void onTestStart(ITestResult result) {
		String classname = result.getTestClass().getName();
		classname = classname.replace("testEndPoints.", "");
		loger.log(Level.INFO, "TestName: " + result.getMethod().getMethodName());
		loger.log(Level.INFO, "TestDescription: " + result.getMethod().getDescription());
		loger.log(Level.INFO, "Author Name: " + System.getProperty("user.name"));
		test = extent.createTest(result.getMethod().getMethodName()).info(result.getMethod().getDescription());
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		loger.log(Level.INFO, "Test Case: " + result.getMethod().getMethodName() + " is passed.\n");
		test.log(Status.PASS, MarkupHelper.createLabel(result.getName(), ExtentColor.GREEN));
	}

	@Override
	public void onTestFailure(ITestResult result) {
		loger.log(Level.INFO, result.getThrowable());
		loger.log(Level.INFO, "Test Case: " + result.getMethod().getMethodName() + " is failed.\n");
		test.fail(result.getThrowable());
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		loger.log(Level.INFO, result.getThrowable());
		loger.log(Level.INFO, "Test Case: " + result.getMethod().getMethodName() + " is skipped.\n");
	}

	@Override
	public void onFinish(ITestContext context) {
		loger.log(Level.INFO, "All data is being logged in log file\n");
		Set<ITestResult> failedTests = context.getFailedTests().getAllResults();
		for (ITestResult temp : failedTests) {
			ITestNGMethod method = temp.getMethod();
			if (context.getFailedTests().getResults(method).size() > 1) {
				failedTests.remove(temp);
			} else {
				if (context.getPassedTests().getResults(method).size() > 0) {
					failedTests.remove(temp);
				}
			}
		}
		extent.flush();
	}

}
