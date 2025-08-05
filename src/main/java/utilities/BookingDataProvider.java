package utilities;

import org.testng.annotations.DataProvider;

public class BookingDataProvider {

    @DataProvider(name = "bookingTypes") 
    public static Object[][] provideBookingTypes() {
        return new Object[][] {
            {"REGULAR"},
            {"VIP"}
           // {"STUDENT"}
        };
    }
}