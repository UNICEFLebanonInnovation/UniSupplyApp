package com.unicefwinterizationplatform.winterization_android;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Tarek on 9/21/2014.
 */
public class ChildObject implements Serializable{


    public enum Status {
        INITIALIZED,EDITED,NOT_DISTRIBUTED,COMPLETE
    }


    String age;
    String gender;
    String kit;
    String status;
    String name;
    String voucherCode;
    String reasonForEdit;

    public ChildObject()
    {

    }
    public ChildObject(String age,String gender,String kit,String status,String reasonForEdit)
    {
        this.age = age;
        this.gender = gender;
        this.kit = kit;
        this.status = status;
        this.reasonForEdit = reasonForEdit;
        this.name = "";
        this.voucherCode="";
    }


    public ChildObject(String age,String name,String gender,String kit,String status,String reasonForEdit)
    {
        this.age = age;
        this.gender = gender;
        this.kit = kit;
        this.status = status;
        this.reasonForEdit = reasonForEdit;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public void  setGender(String gender)
    {
        this.gender = gender;
    }

    public   String getAge()
    {
        return this.age;
    }

    public  String getGender()
    {
        return  this.gender;
    }

    public void setKit(String kit)
    {
        this.kit = kit;
    }

    public void  setStatus(String status)
    {
        this.status = status;
    }

    public   String getKit()
    {
        return this.kit;
    }

    public  String getStatus()
    {
        return  this.status;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setReasonForEdit(String reasonForEdit) {
        this.reasonForEdit = reasonForEdit;
    }

    public String getReasonForEdit() {
        return reasonForEdit;
    }
/*
    @Override
    public int describeContents() { // (2)
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        out.writeString(age);
        out.writeString(name);
        out.writeString(gender);
        out.writeString(kit);
        out.writeString(status);
        out.writeString(reasonForEdit);
    }

    private static ChildObject readFromParcel(Parcel in) {
        String age = in.readString();
        String name = in.readString();
        String gender = in.readString();
        String kit = in.readString();
        String status = in.readString();
        String reasonForEdit = in.readString();

        return new ChildObject(age,name, gender,kit,status,reasonForEdit);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public ChildObject createFromParcel(Parcel in) // (6)
        {
            return readFromParcel(in);
        }

        public ChildObject[] newArray(int size) { // (7)
            return new ChildObject[size];
        }
    };

    */
}

