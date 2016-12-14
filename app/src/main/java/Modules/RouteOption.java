package Modules;

/**
 * Created by rahman on 12/13/16.
 */

public class RouteOption {
    private String totalDuration;
    private String departureTime;
    private String arrivalTime;

    public void setTotalDuration(String duration){
        totalDuration = duration;
    }
    public void setDepartureTime(String departure_Time){
        departureTime = departure_Time;
    }
    public void setArrivalTime(String arrival_Time){
        arrivalTime = arrival_Time;
    }
    public String getTotalDuration(){
        return totalDuration;
    }
    public String getDepartureTime(){
        return departureTime;
    }
    public String getArrivalTime(){
        return arrivalTime;
    }
}
