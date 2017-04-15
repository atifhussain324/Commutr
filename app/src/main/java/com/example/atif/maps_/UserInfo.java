package com.example.atif.maps_;

import android.net.Uri;

/**
 * Created by rahman on 4/12/17.
 */

public class UserInfo {
    public UserInfo(){
        firstName="";
        lastName="";
        reputation=0;
    }
    public void setFirstName(String fName){
        firstName = fName;
    }
    public void setLastName(String lName){
        lastName = lName;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public void incrementReputation(){
        reputation++;
    }
    public void decrementReputation(){
        reputation--;
    }
    public String getFirstName(){
        return firstName;
    }
    public String getLastName(){
        return lastName;
    }

    public int getReputation() {
        return reputation;
    }

    private String firstName;
    private String lastName;
    private int reputation;
}
