package com.unicefwinterizationplatform.winterization_android;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Tarek on 7/23/15.
 */



public class DistributionObject implements Parcelable {


    String docID;
    String name;
    String partnerName;
    String assessmentType;
    String creationDate;
    String distributionDate;
    String criticality;
    PCodeObject pcode;
    String latitude;
    String longitude;
    String altitude;
    String officialID;
    boolean idDoesExist;
    String idNotes;
    String idType;
    String barcodeNum;
    String phoneNumber;
    String familyName;
    String firstName;
    String middleName;
    String relationshipType;
    String interventionTitle;
    boolean isComplete;

    ArrayList<ChildObject> childrenList;
    ArrayList<ItemObject> itemList;


    public DistributionObject() {

    }

    public DistributionObject(String docID, String name, String partnerName, String assessmentType, String creationDate, String distributionDate, String criticality, PCodeObject pcode, String latitude, String longitude, String altitude, String officialID, boolean idDoesExist, String idNotes, String idType, String barcodeNum, String phoneNumber, String familyName, String firstName, String middleName, String relationshipType, boolean isComplete, ArrayList<ChildObject> childrenList, ArrayList<ItemObject> itemList, String interventionTitle) {
        this.docID = docID;
        this.name = name;
        this.partnerName = partnerName;
        this.assessmentType = assessmentType;
        this.creationDate = creationDate;
        this.distributionDate = distributionDate;
        this.criticality = criticality;
        this.pcode = pcode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.officialID = officialID;
        this.idDoesExist = idDoesExist;
        this.idNotes = idNotes;
        this.idType = idType;
        this.barcodeNum = barcodeNum;
        this.phoneNumber = phoneNumber;
        this.familyName = familyName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.relationshipType = relationshipType;
        this.isComplete = isComplete;
        this.childrenList = childrenList;
        this.itemList = itemList;
        this.interventionTitle = interventionTitle;
    }

    public String getInterventionTitle() {
        return interventionTitle;
    }

    public void setInterventionTitle(String interventionTitle) {
        this.interventionTitle = interventionTitle;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setDistributionDate(String distributionDate) {
        this.distributionDate = distributionDate;
    }

    public void setCriticality(String criticality) {
        this.criticality = criticality;
    }

    public void setPcode(PCodeObject pcode) {
        this.pcode = pcode;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public void setOfficialID(String officialID) {
        this.officialID = officialID;
    }

    public void setIdDoesExist(boolean idDoesExist) {
        this.idDoesExist = idDoesExist;
    }

    public void setIdNotes(String idNotes) {
        this.idNotes = idNotes;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public void setBarcodeNum(String barcodeNum) {
        this.barcodeNum = barcodeNum;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public void setChildrenList(ArrayList<ChildObject> childrenList) {
        this.childrenList = childrenList;
    }

    public void setItemList(ArrayList<ItemObject> itemList) {
        this.itemList = itemList;
    }

    public String getDocID() {
        return docID;
    }

    public String getName() {
        return name;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getDistributionDate() {
        return distributionDate;
    }

    public String getCriticality() {
        return criticality;
    }

    public PCodeObject getPcode() {
        return pcode;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public String getOfficialID() {
        return officialID;
    }

    public boolean isIdDoesExist() {
        return idDoesExist;
    }

    public String getIdNotes() {
        return idNotes;
    }

    public String getIdType() {
        return idType;
    }

    public String getBarcodeNum() {
        return barcodeNum;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public ArrayList<ChildObject> getChildrenList() {
        return childrenList;
    }

    public ArrayList<ItemObject> getItemList() {
        return itemList;
    }

    public static Creator getCreator() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {



        out.writeString(docID);
        out.writeString(name);
        out.writeString(partnerName);
        out.writeString(assessmentType);
        out.writeString(creationDate);
        out.writeString(distributionDate);
        out.writeString(criticality);
        out.writeParcelable(pcode, 0);
        out.writeString(latitude);
        out.writeString(longitude);
        out.writeString(altitude);
        out.writeString(officialID);
        out.writeByte((byte) (idDoesExist ? 1 : 0));
        out.writeString(idNotes);
        out.writeString(idType);
        out.writeString(barcodeNum);
        out.writeString(phoneNumber);
        out.writeString(familyName);
        out.writeString(firstName);
        out.writeString(middleName);
        out.writeString(relationshipType);
        out.writeSerializable(childrenList);
        out.writeSerializable(itemList);
        out.writeByte((byte) (isComplete ? 1 : 0));
        out.writeString(interventionTitle);


    }


    private static DistributionObject readFromParcel(Parcel in) {


        String docID= in.readString();
        String name= in.readString();
        String partnerName= in.readString();
        String assessmentType= in.readString();
        String creationDate= in.readString();
        String distributionDate= in.readString();
        String criticality= in.readString();
        PCodeObject pcode = in.readParcelable(PCodeObject.class.getClassLoader());
        String latitude= in.readString();
        String longitude= in.readString();
        String altitude= in.readString();
        String officialID= in.readString();
        boolean idDoesExist= in.readByte() != 0;
        String idNotes= in.readString();
        String idType= in.readString();
        String barcodeNum= in.readString();
        String phoneNumber= in.readString();
        String familyName= in.readString();
        String firstName= in.readString();
        String middleName= in.readString();
        String relationshipType= in.readString();
        ArrayList<ChildObject> childrenList= (ArrayList<ChildObject>) in.readSerializable();
        ArrayList<ItemObject> itemList= (ArrayList<ItemObject>) in.readSerializable();
        boolean isComplete= in.readByte() != 0;
        String interventionTitle= in.readString();


        DistributionObject distributionObject = new DistributionObject(docID,name,partnerName,assessmentType,creationDate,distributionDate,criticality,pcode,latitude,longitude,altitude,officialID,idDoesExist,idNotes,idType,barcodeNum,phoneNumber,familyName,firstName,middleName,relationshipType,isComplete,childrenList,itemList, interventionTitle);
        return distributionObject;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public DistributionObject createFromParcel(Parcel in) // (6)
        {
            return readFromParcel(in);
        }

        public DistributionObject[] newArray(int size) { // (7)
            return new DistributionObject[size];
        }
    };
}
