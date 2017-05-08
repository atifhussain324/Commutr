package Modules;

import java.io.Serializable;

/**
 * Created by Atif on 5/7/17.
 */

public class NearbyStations implements Serializable {
    private String stationName;
    private String imageRef;

    public void setStationName(String name) {
        stationName = name;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }

    public String getImageRef() {
        return imageRef;
    }

    public String getStationName() {
        return stationName;
    }
}
