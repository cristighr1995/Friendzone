package com.cgherghina.friendzone;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ConversationItem implements Serializable {
    static final long serialVersionUID = 1L; //assign a long value

    public String receiver;
    public String lastMessageText;
    public String lastHour;

    public ConversationItem() {

    }

    public ConversationItem(String receiver, String lastMessageText, String lastHour) {
        this.receiver = receiver;
        this.lastMessageText = lastMessageText;
        this.lastHour = lastHour;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getLastMessageText() {
        return lastMessageText;
    }

    public String getLastHour() {
        return lastHour;
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        result.put("receiver", receiver);
        result.put("lastMessageText", lastMessageText);
        result.put("lastHour", lastHour);

        return result;
    }

}
