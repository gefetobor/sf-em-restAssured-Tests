package requestBody;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payload {
	
    
    public  String eventReference;
    public  String bookingType;
    public  String bookingReference;
    public  String theme;
    public  String createdAt;
    public  String amount;
    public  String status;
    public  int totalCapacity;
    public  int availableCapacity;
    public  String startTime;
    public  String endTime;
    
    
    
    
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getTotalCapacity() {
		return totalCapacity;
	}
	public void setTotalCapacity(int totalCapacity) {
		this.totalCapacity = totalCapacity;
	}
	public int getAvailableCapacity() {
		return availableCapacity;
	}
	public void setAvailableCapacity(int availableCapacity) {
		this.availableCapacity = availableCapacity;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
	}
	public String getBookingReference() {
		return bookingReference;
	}
	public void setBookingReference(String bookingReference) {
		this.bookingReference = bookingReference;
	}
	public String getEventReference() {
		return eventReference;
	}
	public void setEventReference(String eventReference) {
		this.eventReference = eventReference;
	}
	public String getBookingType() {
		return bookingType;
	}
	public void setBookingType(String bookingType) {
		this.bookingType = bookingType;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

}
