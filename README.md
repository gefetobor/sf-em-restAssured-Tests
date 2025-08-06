# Event Management App – Test Plan

## 📌 Overview
This test suite is designed to validate the functionality, reliability, and correctness of the Event Management API. It covers core API endpoints related to events and bookings, ensuring that the service behaves as expected under various conditions.

## 🎯 Pre-requisites
- Java 8 or higher installed
- Maven installed and configured in your system PATH
- Backend API service running locally at: http://localhost:8080 

## 📂 How to run the tests

1. **Clone or Download the Test Suite**  
   Ensure you have the test source code available on your local machine.

2. **Start the Backend API**  
   Make sure the Event Management backend API is running locally on port 8080 or update the test configuration to match your API endpoint.

3. **Run Tests with Maven**  
   From the root directory of the test suite, execute the following command:
    
   - `mvn test` (For simple runs)
   OR
   - `mvn test -DbookingType=REGULAR -Dsurefire.suiteXmlFiles=testng.xml` (To override inputs at runtime) (sample: BookingTypes - VIP, REGULAR)

## 🧪 Execution Flow Summary
  - Tests use `Payload.java` to construct the request body.
  - `EventManagerEndPoint.java` sends the request using the correct HTTP method.
  - Response is validated using assertions and logged via `Log4j`.
  - Dynamic data (like IDs) is saved using `FileUtil`.
  - Reports generated with `ExtentReports`.
  - `ListenerTest` handles lifecycle logging and hooks into test results.
   
## 📦 Architecture Overview

**Package:** `eventManagement`

This package contains the core class for sending HTTP requests to the Event Management API.

    EventManagerEndPoint.java

  - Handles all types of HTTP requests (GET, POST, PUT, DELETE).
  - Injects headers like userId from config.
  - Dynamically switches based on method type.
  - Supports payload and path parameters.

**Package:** `requestBody`

This is used for request body creation.

    Payload.java

  - A reusable class representing fields for both event and booking requests.
  - Uses Jackson annotations to ignore null fields.
  - Includes getter and setter methods for serialization.

**Package:** `routes`

Centralized URL definitions for all API endpoints.

    Routes.java

  Contains static variables for each endpoint to ensure maintainability.
  Example:
  - `post_event_url = /v1/events`
  - `update_booking_url = /v1/bookings/{bookingReference}/cancel`
  
**Package:** `utilities`

Includes utility classes to support config handling, reporting, logging, and test listener hooks.

    ConfigReader.java

  Loads configuration from config.properties and exposes helper methods to fetch strings, integers, and booleans.

    
    FileUtil.java
    
  Provides `writeToFile()` method to store dynamic data (e.g., event and booking IDs).

    
    ExtentReportConfig.java

  Configures ExtentReports.
    - Sets report name, theme, and tester metadata.
    - Generates reports in `target/ExtentReport/`.

    
    ListenerTest.java (implements TestNG’s ITestListener)

  Hooks into test execution lifecycle (start, success, failure, etc.).
  - Logs test info via Log4j.
  - Ensures only unique failed test entries.
  - Flushes ExtentReport after test suite execution.


    File: log4j2.xml

  Handles logging configurations using Log4j2 framework.
    - Logs stored under `Logs/eventmanager.log`.
    - Appends time-stamped logs for each execution.
    - Logger name used: `eventmanager`.
    
    
    BookingDataProvider.java

  This class provides data-driven test inputs for booking-related test scenarios using TestNG's `@DataProvider` annotation.
  


**Package:** `testEndPoints`

Contains test cases that validate the Event Management API.

- **EventManagerEndPointTests.java**
  - Uses `@Test` annotations for methods like:
    - Fetching events
    - Creating bookings
    - Canceling bookings
    - Verifying capacity logic
  - Leverages Payload.java for dynamic request building.
  - Uses Assert and Hamcrest matchers for validation.
  - Saves dynamic data like event reference and booking ID to text files for later use.
  - Adds logging with Log4j and integrates ExtentReport via listener.

## 📊 Reporting
- ExtentReports: Generated in `/target/ExtentReport/`  
- Log4j: Logs output to eventmanager logger  

## 📦 Test Data Strategy
- Dynamic event IDs and booking references are saved to:  
  - `EventId.txt`  
  - `bookingId.txt`  
- Configurable fields (e.g. userId, bookingType) read from `config.properties`
- Data-driven tests utilizing:
   - @DataProvider to test multiple inputs for a single test case making a test method reusable and dynamic.
   - @parameter to inject a single value into the test, typically for environment or config-based inputs and control execution externally (CI/CD) so that data can be overridden at runtime if necessary.  

## 🧹 Maintenance Notes
- Review regression suite after each major feature addition  
- Keep test data dynamic to avoid conflicts  
- Prioritize test coverage for high-impact features  
