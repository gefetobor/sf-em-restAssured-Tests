package routes;

public class Routes {
 
    // Routes for event management app
    public static String baseUrl = "http://localhost:8080";
    
    public static String post_event_url = baseUrl + "/v1/events";
    
    public static String get_event_url = baseUrl + "/v1/events";
    
    public static String post_booking_url = baseUrl+ "/v1/bookings";
    
    public static String update_booking_url = baseUrl+ "/v1/bookings/{bookingReference}/cancel";

}
