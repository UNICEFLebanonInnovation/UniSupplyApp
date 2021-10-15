package com.unicefwinterizationplatform.winterization_android;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Tarek on 7/23/15.
 */
public class ItemObject implements Serializable {




    private Integer quantity;
    private Integer delivered;
    private Integer destroyed;
    private String itemID;
    private String itemType;
    private String deliveryStatus;
    private String comment;

    private ArrayList<Map<String,Object>> commentArray;


    public ItemObject() {
    }

    public ItemObject(Integer quantity, Integer delivered,Integer destroyed, String itemID, String itemType, String deliveryStatus,String comment) {
        this.quantity = quantity;
        this.delivered = delivered;
        this.destroyed = destroyed;
        this.itemID = itemID;
        this.itemType = itemType;
        this.deliveryStatus = deliveryStatus;
        this.comment = comment;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setDelivered(Integer delivered) {
        this.delivered = delivered;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDestroyed(Integer destroyed) {
        this.destroyed= destroyed;
    }



    public Integer getQuantity() {
        return quantity;
    }

    public Integer getDelivered() {
        return delivered;
    }

    public String getItemID() {
        return itemID;
    }

    public String getItemType() {
        return itemType;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public String getComment() {
        return comment;
    }

    public Integer getDestroyed() {
        return destroyed;
    }

    public void setCommentArray(ArrayList<Map<String,Object>> commentArray) {

        this.commentArray = commentArray;
    }

    public ArrayList<Map<String,Object>> getCommentArray() {
        return commentArray;
    }
}

