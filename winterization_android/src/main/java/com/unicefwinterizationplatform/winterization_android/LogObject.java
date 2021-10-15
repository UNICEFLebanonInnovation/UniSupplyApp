package com.unicefwinterizationplatform.winterization_android;

import java.io.Serializable;

/**
 * Created by Tarek on 2/4/16.
 */
public class LogObject implements Serializable {
    private String place;
    private String time;
    private String amount;
    private String itemName;
    private String comment;
    private boolean forceComplete;

    public LogObject() {


    }

    public LogObject(String place, String time, String amount, String itemName, String comment, boolean forceComplete) {
        this.place = place;
        this.time = time;
        this.amount = amount;
        this.itemName = itemName;
        this.comment = comment;
        this.forceComplete = forceComplete;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isForceComplete() {
        return forceComplete;
    }

    public void setForceComplete(boolean forceComplete) {
        this.forceComplete = forceComplete;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getTime() {
        return time;
    }

}