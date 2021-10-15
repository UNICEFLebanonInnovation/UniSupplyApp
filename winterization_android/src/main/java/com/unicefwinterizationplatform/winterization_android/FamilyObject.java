package com.unicefwinterizationplatform.winterization_android;

import java.io.Serializable;

/**
 * Created by Tarek on 9/25/16.
 */
public class FamilyObject implements Serializable {


    String idType;
    String ID;

    public FamilyObject(String idType, String ID) {
        this.idType = idType;
        this.ID = ID;
    }

    public FamilyObject()
    {

    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
