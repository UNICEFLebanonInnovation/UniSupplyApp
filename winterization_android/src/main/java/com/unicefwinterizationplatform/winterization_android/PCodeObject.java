package com.unicefwinterizationplatform.winterization_android;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tarek on 9/30/2014.
 */
public class PCodeObject implements Parcelable  {


   private String pcodeID;
   private String locationType;
    private String cadastral;
    private String district;
   private String pcodeName;
    private String pcodeType;
    private String pcodeLat;
    private String pcodeLong;


    public PCodeObject(String pcodeID, String locationType, String cadastral, String district, String pcodeName, String pcodeLat, String pcodeLong, String pcodeType) {
        this.pcodeID = pcodeID;
        this.locationType = locationType;
        this.cadastral = cadastral;
        this.district = district;
        this.pcodeName = pcodeName;
        this.pcodeLat = pcodeLat;
        this.pcodeLong = pcodeLong;
        this.pcodeType = pcodeType;
    }

    public PCodeObject()
    {

    }



    public void setPcodeID(String pcodeID) {
        this.pcodeID = pcodeID;
    }

    public void setPcodeLat(String pcodeLat) {
        this.pcodeLat = pcodeLat;
    }

    public void setPcodeLong(String pcodeLong) {
        this.pcodeLong = pcodeLong;
    }

    public void setPcodeName(String pcodeName) {
        this.pcodeName = pcodeName;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getPcodeType() {
        return pcodeType;
    }

    public void setPcodeType(String pcodeType) {
        this.pcodeType = pcodeType;
    }

    public String getPcodeID() {
        return pcodeID;
    }

    public String getPcodeLat() {
        return pcodeLat;
    }

    public String getPcodeLong() {
        return pcodeLong;
    }

    public String getPcodeName() {
        return pcodeName;
    }

    public String getLocationType() {
        return locationType;
    }


    public String getCadastral() {
        return cadastral;
    }

    public void setCadastral(String cadastral) {
        this.cadastral = cadastral;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @Override
    public int describeContents() { // (2)
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) // (3)
    {
        out.writeString(pcodeID);
        out.writeString(pcodeName);
        out.writeString(pcodeLat);
        out.writeString(pcodeLong);
        out.writeString(locationType);
        out.writeString(district);
        out.writeString(cadastral);
        out.writeString(pcodeType);

    }

    private static PCodeObject readFromParcel(Parcel in) { // (4)
        String pcodeID = in.readString();
        String pcodeName = in.readString();

        String pcodeLat = in.readString();
        String pcodeLong = in.readString();
        String locationType = in.readString();
        String distict = in.readString();
        String cadastral = in.readString();
        String pcodeType = in.readString();

        return new PCodeObject(pcodeID, locationType, cadastral, distict, pcodeName,pcodeLat,pcodeLong, pcodeType);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() // (5)
    {
        public PCodeObject createFromParcel(Parcel in) // (6)
        {
            return readFromParcel(in);
        }

        public PCodeObject[] newArray(int size) { // (7)
            return new PCodeObject[size];
        }
    };
}


