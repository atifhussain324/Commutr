package com.example.atif.maps_;

import java.io.Serializable;

/**
 * Created by rahman on 12/9/16.
 */

public class Alert implements Serializable{

    public Alert(){
        direction="";
        train="";
        status="";
    }
    /*
    public Alert(String route_direction,String train_name, String current_status){
        direction= route_direction;
        train = train_name;
        status = current_status;
    } */
    public void setDirection(String letter){
        if(letter.equalsIgnoreCase("N")){
            direction="NorthBound";
        }
        else
        {
            direction="SouthBound";
        }

    }


    public void setTrain(String trainNumber){
        train= trainNumber;
    }
    public void  setStatus(String currentStatus){
        status= currentStatus;
    }
    public String getDirection(){
        return direction;
    }
    public String getStatus(){
        return status;
    }
    public String getTrain(){
        return train;
    }

    private String direction;
    private String train;
    private String status;
}
