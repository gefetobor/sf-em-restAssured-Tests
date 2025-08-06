package testEndPoints;

import java.io.IOException;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import eventManagement.BaseTest;
//import com.github.javafaker.Faker;
import eventManagement.EventManagerEndPoint;
import io.restassured.response.Response;
import requestBody.Payload;
import routes.Routes;
import utilities.*;
import static org.hamcrest.Matchers.*;
import org.testng.Assert;


@Listeners(utilities.ListenerTest.class)
public class EventManagerEndPointTests extends BaseTest {

	//static Faker faker = new Faker();
	static Payload userpayload = new Payload();

	@Test(priority = 1)
	public void testGetRequest() throws IOException {

		String endpoint = Routes.get_event_url;
		String httpMethod = "get";
		
		enableRestAssuredLogging();
		Response response = EventManagerEndPoint.sendRequest(endpoint, httpMethod, null, null);
		String eventReferenceId = response.body().jsonPath().getString("data[0].reference").toString();
		writeToFile("./src/test/resources/EventId.txt", eventReferenceId);
		
		//Assertions
		response.then().statusCode(200)
		.body("status", equalTo("SUCCESS"))
		.body("data", notNullValue())
		.body("data.size()", greaterThan(0));
		
		// Logical check: availableCapacity should not exceed totalCapacity
		response.then().body("data.findAll { it.availableCapacity > it.totalCapacity }", empty());
		logResponse(response);


	}

	@Test(priority = 2)
	public void createNewBookingTest() throws IOException {
		String endpoint = Routes.post_booking_url;
		String httpMethod = "post";
		String eventReferenceId = readFromFile("./src/test/resources/EventId.txt");
		
		userpayload.setEventReference(eventReferenceId.toString());
		userpayload.setBookingType(ConfigReader.get("bookingType"));

		enableRestAssuredLogging();
		Response response = EventManagerEndPoint.sendRequest(endpoint, httpMethod, userpayload, null);

		String bookingId = response.body().jsonPath().getString("reference").toString();
		writeToFile("./src/test/resources/bookingId.txt", bookingId);
		
		// Assertions
		response.then().statusCode(200)
			.body("reference", notNullValue())
			.body("status", equalTo("SUCCESSFUL"))
			.body("eventName", notNullValue())
			.body("fee", greaterThan(0f));
		logResponse(response);

	}

	@Test(priority = 3)
	public void cancelBookingTest() throws IOException {
		String endpoint = Routes.update_booking_url;
		String httpMethod = "put";
		String bookingReference = readFromFile("./src/test/resources/bookingId.txt");
		enableRestAssuredLogging();
		Response response = EventManagerEndPoint.sendRequest(endpoint, httpMethod, null, bookingReference);
		
		// Assertions
		response.then().statusCode(anyOf(equalTo(200), equalTo(204)));
		logResponse(response);

	}
	
	@Parameters({"bookingType"})
	@Test(priority = 4)
	public void parameterizationTest(String bookingType) throws IOException {
		String endpoint = Routes.post_booking_url;
		String httpMethod = "post";
		String eventReferenceId = readFromFile("./src/test/resources/EventId.txt");
		userpayload.setEventReference(eventReferenceId.toString());
		userpayload.setBookingType(bookingType);

		enableRestAssuredLogging();
		Response response = EventManagerEndPoint.sendRequest(endpoint, httpMethod, userpayload, null);

		String bookingId = response.body().jsonPath().getString("reference").toString();
		writeToFile("./src/test/resources/bookingId.txt", bookingId);
		
		// Assertions
		response.then().statusCode(200)
			.body("reference", notNullValue())
			.body("status", equalTo("SUCCESSFUL"))
			.body("eventName", notNullValue())
			.body("fee", greaterThan(0f));
		logResponse(response);

	}
	
	@Test(dataProvider = "bookingTypes", dataProviderClass = BookingDataProvider.class, priority = 5)
	public void testWithDifferentBooKingTypes(String bookingType) throws IOException {
		String endpoint = Routes.post_booking_url;
		String httpMethod = "post";
		String eventReferenceId = readFromFile("./src/test/resources/EventId.txt");
		userpayload.setEventReference(eventReferenceId.toString());
		userpayload.setBookingType(bookingType);
		
		enableRestAssuredLogging();
		Response response = EventManagerEndPoint.sendRequest(endpoint, httpMethod, userpayload, null);
		
		String bookingId = response.body().jsonPath().getString("reference").toString();
		writeToFile("./src/test/resources/bookingId.txt", bookingId);
		
		// Assertions
		response.then().statusCode(200)
			.body("reference", notNullValue())
			.body("status", equalTo("SUCCESSFUL"))
			.body("eventName", notNullValue())
			.body("fee", greaterThan(0f));
		logInfo(bookingType);
		logResponse(response);

	}
	
	@Test(priority = 6)
	public void testAvailableCapacityReducesAfterBooking() throws IOException {
	    String eventEndpoint = Routes.get_event_url;
	    String bookingEndpoint = Routes.post_booking_url;

	    enableRestAssuredLogging();
	    // Step 1: Get event with available capacity
	    Response getResponse = EventManagerEndPoint.sendRequest(eventEndpoint, "get", null, null);

	    String reference = getResponse.jsonPath().getString("data.find { it.availableCapacity > 0 }.reference");
	    int availableBefore = getResponse.jsonPath()
	            .getInt("data.find { it.reference == '" + reference + "' }.availableCapacity");

	    Assert.assertNotNull(reference, "No event found with availableCapacity > 0");
	    Assert.assertTrue(availableBefore > 0, "Invalid available capacity");

	    // Step 2: Book the event
	    userpayload.setEventReference(reference);
	    userpayload.setBookingType(ConfigReader.get("bookingType"));

	    Response bookingResponse = EventManagerEndPoint.sendRequest(bookingEndpoint, "post", userpayload, null);
	    bookingResponse.then().statusCode(200).body("status", equalTo("SUCCESSFUL"));

	    // Step 3: Re-fetch the event after booking
	    Response getAfter = EventManagerEndPoint.sendRequest(eventEndpoint, "get", null, null);
	    int availableAfter = getAfter.jsonPath()
	            .getInt("data.find { it.reference == '" + reference + "' }.availableCapacity");

	    // Step 4: Assert that capacity reduced
	    Assert.assertEquals(availableAfter, availableBefore - 1,
	            "Available capacity did not reduce after booking");

	    // logging test details
	    logInfo("Event Reference: " + reference);
	    logInfo("Available Before: " + availableBefore + " | Available After: " + availableAfter);
	}


}
