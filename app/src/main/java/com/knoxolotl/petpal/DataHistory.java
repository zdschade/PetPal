package com.knoxolotl.petpal;

import android.provider.ContactsContract;

import java.io.Serializable;

public class DataHistory implements Serializable {
    // Item history. For example, user1@petpal.com fed Max at 7:00am

    private String item_time;
    private String username;
    private String log_time;
    private String type;

    public DataHistory(String item_time, String username, String log_time) {
        this.item_time = item_time;
        this.username = username;
        this.log_time = log_time;
    }

    public DataHistory() {}

    public String getItem_time() {
        return item_time;
    }

    public void setItem_time(String item_time) {
        this.item_time = item_time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLog_time() {
        return log_time;
    }

    public String getLog_date() {
        return log_time.split("-")[0];
    }

    public void setLog_time(String log_time) {
        this.log_time = log_time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
