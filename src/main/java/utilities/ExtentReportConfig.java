package utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;

public class ExtentReportConfig {
	static ExtentReports extent;

	public static ExtentReports extentReportGenerator() {
		String path = System.getProperty("user.dir") + File.separator + "target" + File.separator + "ExtentReport"
				+ File.separator + "report-" + System.currentTimeMillis() + ".html";
		ExtentSparkReporter reporter = new ExtentSparkReporter(path);
		reporter.config().setReportName("SecureFLag"); // Give report name
		reporter.config().setDocumentTitle("Event Management Test");
		reporter.config().setTheme(Theme.DARK);

		extent = new ExtentReports();
		extent.attachReporter(reporter);

		extent.setSystemInfo("TesterName", "Efe");
		extent.setSystemInfo("Test Reviewer", "Reviewer, Boss");// Set system info whatever you want to show in report
		return extent;
	}
	
}
