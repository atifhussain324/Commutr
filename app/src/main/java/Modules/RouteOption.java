package Modules;

import java.io.Serializable;

/**
 * Created by rahman on 12/13/16.
 */

public class RouteOption implements Serializable{
    private String totalDuration;
    private String departureTime;
    private String arrivalTime;
    private String name;
    private String instruction;
    private int stops;
    private String stepDuration;

    public void setTotalDuration(String duration){
        totalDuration = duration;
    }
    public void setDepartureTime(String departure_Time){
        departureTime = departure_Time;
    }
    public void setArrivalTime(String arrival_Time){
        arrivalTime = arrival_Time;
    }
    public void setName(String sName){
        name= sName;
    }
    public void setInstruction(String instruc){
        instruction= instruc;
    }
    public void setStops(int nStops){
        stops= nStops;
    }
    public void setStepDuration(String stDuration){
        stepDuration= stDuration;
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

    public String getInstruction(){
        return instruction;
    }
    public String getName(){
        return name;
    }
    public String getStepDuration(){
        return stepDuration;
    }
    public int getStops(){
        return stops;
    }
}
