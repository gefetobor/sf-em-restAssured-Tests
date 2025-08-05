package eventManagement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utilities.ConfigReader;


public class EventManagerEndPoint {

	public static Response sendRequest(String endpoint, String httpMethod, Object payload, String bookingReference)
			throws IOException {


		String userId = ConfigReader.get("userId");
		Response response = null;
		switch (httpMethod.toLowerCase()) { 
			
		case "get":
			response = RestAssured.given().when().get(endpoint).then().extract().response();
			break;
		case "post":
			response = RestAssured.given().header("Content-Type", "application/json")
					.header("userId", userId).contentType(ContentType.JSON).accept(ContentType.JSON)
					.body(payload).when().post(endpoint).then().extract().response();
			break;
		case "put":
			response = RestAssured.given().header("Content-Type", "application/json")
					.header("userId", userId).pathParam("bookingReference", bookingReference).contentType(ContentType.JSON)
					.when().put(endpoint).then().extract().response();
			break;
		case "delete":
			response = RestAssured.given().header("Content-Type", "application/json")
					.pathParam("bookingReference", bookingReference).when().delete(endpoint).then()
					.extract().response();
			break;
		default:
			System.out.println("Unsupported HTTP method");
			break;
		}
		return response;
	}
}
