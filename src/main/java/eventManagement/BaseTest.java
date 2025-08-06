package eventManagement;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import requestBody.Payload;
import utilities.ConfigReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
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
        try {
            cleanTemporaryFiles();
        } catch (IOException e) {
            logger.error("Error during temporary file cleanup: {}", e.getMessage(), e);
        }
    }

    @BeforeMethod
    public void beforeEachTest() {
        // Can be extended to reset state before each test if needed
    }

    protected void enableRestAssuredLogging() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    protected String readFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }
        return Files.readString(path).trim();
    }

    protected void writeToFile(String filePath, String data) throws IOException {
        Path path = Paths.get(filePath);
        Files.writeString(path, data);
    }

    protected void logResponse(Response response) {
        if (response != null) {
            logger.info(response.prettyPrint());
        } else {
            logger.warn("Attempted to log a null response.");
        }
    }

    protected void logInfo(String message) {
        logger.info(message);
    }

    protected Payload createPayload(String eventReference, String bookingType) {
        Payload payload = new Payload();
        payload.setEventReference(eventReference);
        payload.setBookingType(bookingType);
        return payload;
    }

    private void cleanTemporaryFiles() throws IOException {
        deleteFileIfExists(ConfigReader.get("EVENT_ID_PATH"));
        deleteFileIfExists(ConfigReader.get("BOOKING_ID_PATH"));
    }

    private void deleteFileIfExists(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            Files.delete(path);
            logger.info("Deleted temporary file: {}", filePath);
        }
    }
}
