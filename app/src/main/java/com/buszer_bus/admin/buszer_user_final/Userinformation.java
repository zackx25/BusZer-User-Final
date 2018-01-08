package com.buszer_bus.admin.buszer_user_final;

/**
 * Created by Admin on 3/3/2017.
 */
public class Userinformation {
    public Double latitude, longitude;
   public Integer vacant_seats;

    public Userinformation(){

    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Integer getVacant_seats() {
        return vacant_seats;
    }

    public void setVacant_seats(Integer vacant_seats) {
        this.vacant_seats = vacant_seats;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
