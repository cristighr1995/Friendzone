package com.cgherghina.friendzone;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    static final long serialVersionUID = 1L; //assign a long value

    public String username;
    public String latitude;
    public String longitude;
    public String hour;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String latitude, String longitude, String hour) {
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hour = hour;
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        result.put("username", username);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("hour", hour);

        return result;
    }

    public String getUsername() {
        return username;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getHour() {
        return hour;
    }
}
