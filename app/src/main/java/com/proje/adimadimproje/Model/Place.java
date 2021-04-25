package com.proje.adimadimproje.Model;

import java.io.Serializable;

public class Place implements Serializable {

    private String placeName;
    private  Double latitude;
    private  Double longitude;

    public Place(String placeName, Double latitude, Double longitude) {

        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
