package Modules;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Atif on 11/27/16.
 */

public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;
    public List<LatLng> points;

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
