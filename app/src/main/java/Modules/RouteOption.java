package Modules;

import java.io.Serializable;

/**
 * Created by rahman on 12/13/16.
 */

public class RouteOption implements Serializable {
    private String totalDuration;
    private String departureTime;
    private String arrivalTime;

    //Transit Fields
    private String tName;
    private String tInstruction;
    private int tStops;
    private String tStepDuration;
    private String tShortName;
    private String tLongName;
    private String color;

    //Walking Fields
    private String wName;
    private String wInstruction;
    private String wDuration;
    private String wDistance;

    private String travel_mode;

    public void setTotalDuration(String duration) {
        totalDuration = duration;
    }

    public void setDepartureTime(String departure_Time) {
        departureTime = departure_Time;
    }

    public void setArrivalTime(String arrival_Time) {
        arrivalTime = arrival_Time;
    }

    public void settName(String name) {
        tName = name;
    }

    public void settInstruction(String instruc) {
        tInstruction = instruc;
    }

    public void settStops(int nStops) {
        tStops = nStops;
    }

    public void settStepDuration(String stDuration) {
        tStepDuration = stDuration;
    }

    public void settShortName(String sName) {
        tShortName = sName;
    }

    public void settLongName(String lName) {
        tLongName = lName;
    }

    public void setColor(String bColor) {
        color = bColor;
    }

    public void setwName(String name) {
        wName = name;
    }

    public void setwInstruction(String instruction) {
        wInstruction = instruction;
    }

    public void setwDuration(String duration) {
        wDuration = duration;
    }

    public void setwDistance(String distance) {
        wDistance = distance;
    }

    public void setTravel_mode(String tmode) {
        travel_mode = tmode;
    }


    public String getTotalDuration() {
        return totalDuration;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getwName() {
        return wName;
    }

    public String getwInstruction() {
        return wInstruction;
    }

    public String getwDuration() {
        return wDuration;
    }

    public String getwDistance() {
        return wDistance;
    }


    public String getInstruction() {
        return tInstruction;
    }

    public String getName() {
        return tName;
    }

    public String getStepDuration() {
        return tStepDuration;
    }

    public int getStops() {
        return tStops;
    }

    public String gettShortName() {
        return tShortName;
    }

    public String gettLongName() {
        return tLongName;

    }

    public String getTravel_mode() {
        return travel_mode;
    }

    public String getColor() {
        return color;
    }

}
