package com.cgherghina.friendzone;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Message implements Serializable {
    static final long serialVersionUID = 1L; //assign a long value

    public String sender;
    public String messageText;
    public String hour;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Message(String sender, String messageText, String hour) {
        this.sender = sender;
        this.messageText = messageText;
        this.hour = hour;
    }

    public String getSender() {
        return sender;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getHour() {
        return hour;
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        result.put("sender", sender);
        result.put("messageText", messageText);
        result.put("hour", hour);

        return result;
    }
}
