package testEndPoints;

import java.io.IOException;

import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import eventManagement.BaseTest;
import eventManagement.EventManagerEndPoint;
import io.restassured.response.Response;
import requestBody.Payload;
import routes.Routes;
import utilities.*;

import static org.hamcrest.Matchers.*;
import org.testng.Assert;

@Listeners(utilities.ListenerTest.class)
public class EventManagerEndPointTests extends BaseTest {

    @Test(priority = 1)
    public void testGetRequest() throws IOException {
        String endpoint = Routes.get_event_url;
        Response response = EventManagerEndPoint.sendRequest(endpoint, "get", null, null);

        String eventReferenceId = response.jsonPath().getString("data[0].reference");
        writeToFile(ConfigReader.get("EVENT_ID_PATH"), eventReferenceId);

        response.then()
            .statusCode(200)
            .body("status", equalTo("SUCCESS"))
            .body("data", notNullValue())
            .body("data.size()", greaterThan(0))
            .body("data.findAll { it.availableCapacity > it.totalCapacity }", empty());

        logResponse(response);
    }

    @Test(priority = 2)
    public void createNewBookingTest() throws IOException {
        String endpoint = Routes.post_booking_url;
        String eventReferenceId = readFromFile(ConfigReader.get("EVENT_ID_PATH"));

        Payload payload = createPayload(eventReferenceId, ConfigReader.get("bookingType"));
        Response response = EventManagerEndPoint.sendRequest(endpoint, "post", payload, null);

        String bookingId = response.jsonPath().getString("reference");
        writeToFile(ConfigReader.get("BOOKING_ID_PATH"), bookingId);

        response.then()
            .statusCode(200)
            .body("reference", notNullValue())
            .body("status", equalTo("SUCCESSFUL"))
            .body("eventName", notNullValue())
            .body("fee", greaterThan(0f));

        logResponse(response);
    }

    @Test(priority = 3)
    public void cancelBookingTest() throws IOException {
        String endpoint = Routes.update_booking_url;
        String bookingReference = readFromFile(ConfigReader.get("BOOKING_ID_PATH"));

        Response response = EventManagerEndPoint.sendRequest(endpoint, "put", null, bookingReference);

        response.then().statusCode(anyOf(equalTo(200), equalTo(204)));

        logResponse(response);
    }

    @Parameters({"bookingType"})
    @Test(priority = 4)
    public void parameterizationTest(String bookingType) throws IOException {
        String endpoint = Routes.post_booking_url;
        String eventReferenceId = readFromFile(ConfigReader.get("EVENT_ID_PATH"));

        Payload payload = createPayload(eventReferenceId, bookingType);
        Response response = EventManagerEndPoint.sendRequest(endpoint, "post", payload, null);

        String bookingId = response.jsonPath().getString("reference");
        writeToFile(ConfigReader.get("BOOKING_ID_PATH"), bookingId);

        response.then()
            .statusCode(200)
            .body("reference", notNullValue())
            .body("status", equalTo("SUCCESSFUL"))
            .body("eventName", notNullValue())
            .body("fee", greaterThan(0f));

        logResponse(response);
    }

    @Test(dataProvider = "bookingTypes", dataProviderClass = BookingDataProvider.class, priority = 5)
    public void testWithDifferentBookingTypes(String bookingType) throws IOException {
        String endpoint = Routes.post_booking_url;
        String eventReferenceId = readFromFile(ConfigReader.get("EVENT_ID_PATH"));

        Payload payload = createPayload(eventReferenceId, bookingType);
        Response response = EventManagerEndPoint.sendRequest(endpoint, "post", payload, null);

        String bookingId = response.jsonPath().getString("reference");
        writeToFile(ConfigReader.get("BOOKING_ID_PATH"), bookingId);

        response.then()
            .statusCode(200)
            .body("reference", notNullValue())
            .body("status", equalTo("SUCCESSFUL"))
            .body("eventName", notNullValue())
            .body("fee", greaterThan(0f));

        logInfo("Booking type tested: " + bookingType);
        logResponse(response);
    }

    @Test(priority = 6)
    public void testAvailableCapacityReducesAfterBooking() throws IOException {
        String eventEndpoint = Routes.get_event_url;
        String bookingEndpoint = Routes.post_booking_url;

        // Step 1: Fetch event with available capacity
        Response getResponse = EventManagerEndPoint.sendRequest(eventEndpoint, "get", null, null);
        String reference = getResponse.jsonPath().getString("data.find { it.availableCapacity > 0 }.reference");
        int availableBefore = getResponse.jsonPath()
            .getInt("data.find { it.reference == '" + reference + "' }.availableCapacity");

        Assert.assertNotNull(reference, "No event found with availableCapacity > 0");
        Assert.assertTrue(availableBefore > 0, "Invalid available capacity");

        // Step 2: Book the event
        Payload payload = createPayload(reference, ConfigReader.get("bookingType"));
        Response bookingResponse = EventManagerEndPoint.sendRequest(bookingEndpoint, "post", payload, null);
        bookingResponse.then().statusCode(200).body("status", equalTo("SUCCESSFUL"));

        // Step 3: Fetch event again
        Response getAfter = EventManagerEndPoint.sendRequest(eventEndpoint, "get", null, null);
        int availableAfter = getAfter.jsonPath()
            .getInt("data.find { it.reference == '" + reference + "' }.availableCapacity");

        // Step 4: Validate that available capacity reduced by 1
        Assert.assertEquals(availableAfter, availableBefore - 1,
            "Available capacity did not reduce after booking");

        logInfo("Event Reference: " + reference);
        logInfo("Available Before: " + availableBefore + " | Available After: " + availableAfter);
    }
}
