package eventManagement;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseTest {

    protected static final Logger logger = LogManager.getLogger(BaseTest.class);

    @BeforeSuite
    public void setUpSuite() {
        enableRestAssuredLogging();
        logger.info("=== Test Suite Initialization Completed ===");
    }

    @AfterSuite
    public void tearDownSuite() {
        logger.info("=== Test Suite Execution Completed ===");

        // Optional: clean temp files or notify
        try {
            cleanTemporaryFiles();
        } catch (IOException e) {
            logger.error("Error during cleanup: " + e.getMessage());
        }
    }

    protected void enableRestAssuredLogging() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    protected String readFromFile(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath)).trim();
    }

    protected void writeToFile(String filePath, String data) throws IOException {
        Files.writeString(Paths.get(filePath), data);
    }

    protected void logResponse(io.restassured.response.Response response) {
        logger.info(response.prettyPrint());
    }

    protected void logInfo(String message) {
        logger.info(message);
    }

    protected void cleanTemporaryFiles() throws IOException {
        deleteIfExists("./src/test/resources/EventId.txt");
        deleteIfExists("./src/test/resources/bookingId.txt");
    }

    private void deleteIfExists(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            Files.delete(path);
            logger.info("Deleted temporary file: " + filePath);
        }
    }
}
