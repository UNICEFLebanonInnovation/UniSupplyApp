package com.unicefwinterizationplatform.winterization_android;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tarek on 10/3/2014.
 */
public class BeneficiaryObject implements Parcelable {


    String mainID;
    String creationDate;
    String completionDate;
    String criticality;
    PCodeObject pcode;
    PCodeObject pcodeDist;
    String officialID;
    boolean idDoesExist;
    String idNotes;
    String idType;
    String barcodeNum;
    String phoneNumber;
    boolean isShawish;
    String familyName;
    String firstName;
    String middleName;
    String relationshipType;
    boolean isComplete;
    String reasonForEdit;
    String dateOfBirth;
    String gender;
    String maritalStatus;
    String partnerName;
    String movingLoc;
    String disabilities;
    String phoneOwner;
    String assistanceType;
    String principleApplicant;
    String over18;
    String mothersName;
    String familyCount;
    String newCadasteral;
    String newDistrict;



    ArrayList<ChildObject> childrenList;
    ArrayList<FamilyObject>  familyList;


    public BeneficiaryObject() {

        childrenList = new ArrayList<ChildObject>();
    }
    public BeneficiaryObject(String mainID,String creationDate,String completionDate, String criticality, PCodeObject pcode ,PCodeObject pcodeDist , String  officialID, boolean idDoesExist,String idNotes, String idType, String barcodeNum,String phoneNumber,boolean isShawish, String familyName,String firstName, String middleName,String relationshipType,ArrayList<ChildObject> childrenList,boolean isComplete,String reasonForEdit,String maritalStatus, String gender, String dateOfBirth, String partnerName, String movingLoc, String phoneOwner, String disabilities, String assistanceType,String principleApplicant, String over18, String familyCount, String mothersName, ArrayList<FamilyObject> familyList,  String newCadasteral, String newDistrict)
   {
       this.mainID = mainID;
       this.creationDate = creationDate;
       this.completionDate = completionDate;
       this.pcode = pcode;
       this.pcodeDist =pcodeDist;
       this.officialID = officialID;
       this.idDoesExist = idDoesExist;
       this.idNotes = idNotes;
       this.idType = idType;
       this.barcodeNum = barcodeNum;
       this.phoneNumber = phoneNumber;
       this.familyName = familyName;
       this.firstName= firstName;
       this.middleName = middleName;
       this.relationshipType = relationshipType;
       this.criticality = criticality;
       this.childrenList = childrenList;
        this.isComplete = isComplete;
       this.isShawish = isShawish;
       this.reasonForEdit = reasonForEdit;
       this.maritalStatus = maritalStatus;
       this.gender = gender;
       this.dateOfBirth = dateOfBirth;
       this.partnerName = partnerName;
       this.movingLoc = movingLoc;
       this.disabilities = disabilities;
       this.phoneOwner = phoneOwner;
       this.assistanceType = assistanceType;
       this.principleApplicant = principleApplicant;
       this.over18 = over18;
       this.mothersName=mothersName;
       this.familyCount = familyCount;
       this.familyList = familyList;
       this.newCadasteral = newCadasteral;
       this.newDistrict = newDistrict;
   }

    public BeneficiaryObject(String mainID,String creationDate, String criticality, PCodeObject pcode , String officialID,String idType, String barcodeNum,String phoneNumber,String familyName,String firstName, String middleName,String maritalStatus, String gender, String dateOfBirth,String partnerName,String answer4, String newLocation, String phoneOwner, String disabilities, String assistanceType, String principleApplicant, String over18, String newCadasteral, String newDistrict)
    {
        this.mainID = mainID;
        this.creationDate = creationDate;
        this.pcode = pcode;
        this.officialID = officialID;
        this.idType = idType;
        this.barcodeNum = barcodeNum;
        this.phoneNumber = phoneNumber;

        this.familyName = familyName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.childrenList = new ArrayList<ChildObject>();
        this.criticality = criticality;
        this.maritalStatus = maritalStatus;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.partnerName = partnerName;
        this.movingLoc = answer4;
        this.phoneOwner = phoneOwner;
        this.disabilities = disabilities;
        this.assistanceType = assistanceType;
        this.over18 = over18;
        this.principleApplicant = principleApplicant;


    }

    public String getNewCadasteral() {
        return newCadasteral;
    }

    public void setNewCadasteral(String newCadasteral) {
        this.newCadasteral = newCadasteral;
    }

    public String getNewDistrict() {
        return newDistrict;
    }

    public void setNewDistrict(String newDistrict) {
        this.newDistrict = newDistrict;
    }

    public void setReasonForEdit(String reasonForEdit) {
        this.reasonForEdit = reasonForEdit;
    }

    public String getReasonForEdit() {
        return reasonForEdit;
    }

    public void setShawish(boolean isShawish) {
        this.isShawish = isShawish;
    }

    public boolean isShawish() {
        return isShawish;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setCriticality(String criticality) {
        this.criticality = criticality;
    }

    public void setPcode(PCodeObject pcode) {
        this.pcode = pcode;
    }

    public void setPcodeDist(PCodeObject pcodeDist) {
        this.pcodeDist = pcodeDist;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PCodeObject getPcode() {
        return pcode;
    }

    public PCodeObject getPcodeDist() {
        return pcodeDist;
    }

    public String getCreationDate() {
        return creationDate;

    }


    public String getCriticality() {
        return criticality;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setBarcodeNum(String barcodeNum) {
        this.barcodeNum = barcodeNum;
    }

    public void setChildrenList(ArrayList<ChildObject> childrenList) {
        this.childrenList = childrenList;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public void setMainID(String mainID) {
        this.mainID = mainID;
    }

    public void setOfficialID(String officialID) {
        this.officialID = officialID;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public ArrayList<ChildObject> getChildrenList() {
        return childrenList;
    }

    public String getBarcodeNum() {
        return barcodeNum;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getIdType() {
        return idType;
    }

    public String getMainID() {
        return mainID;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getOfficialID() {
        return officialID;
    }

    public void setIdDoesExist(boolean idDoesExist) {
        this.idDoesExist = idDoesExist;
    }

    public void setIdNotes(String idNotes) {
        this.idNotes = idNotes;
    }

    public boolean isIdDoesExist() {
        return idDoesExist;
    }

    public String getIdNotes() {
        return idNotes;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public boolean isComplete() {
        return isComplete;

    }


    public String getMovingLoc() {
        return movingLoc;
    }

    public void setMovingLoc(String movingLoc) {
        this.movingLoc = movingLoc;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }


    public String getDisabilities() {
        return disabilities;
    }

    public void setDisabilities(String disabilities) {
        this.disabilities = disabilities;
    }

    public String getPhoneOwner() {
        return phoneOwner;
    }

    public void setPhoneOwner(String phoneOwner) {
        this.phoneOwner = phoneOwner;
    }

    public String getAssistanceType() {
        return assistanceType;
    }

    public void setAssistanceType(String assistanceType) {
        this.assistanceType = assistanceType;
    }

    public String getPrincipleApplicant() {
        return principleApplicant;
    }

    public void setPrincipleApplicant(String principleApplicant) {
        this.principleApplicant = principleApplicant;
    }

    public String getOver18() {
        return over18;
    }

    public void setOver18(String over18) {
        this.over18 = over18;
    }

    public String getMothersName() {
        return mothersName;
    }

    public void setMothersName(String mothersName) {
        this.mothersName = mothersName;
    }

    public String getFamilyCount() {
        return familyCount;
    }

    public void setFamilyCount(String familyCount) {
        this.familyCount = familyCount;
    }

    public ArrayList<FamilyObject> getFamilyList() {
        return familyList;
    }

    public void setFamilyList(ArrayList<FamilyObject> familyList) {
        this.familyList = familyList;
    }

    @Override
    public int describeContents() { // (2)
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        out.writeString(mainID);
        out.writeString(creationDate);
        out.writeString(completionDate);
        out.writeString(criticality);
        out.writeParcelable(pcode, 0);
        out.writeParcelable(pcodeDist, 0);
        out.writeString(officialID);
        out.writeByte((byte) (idDoesExist ? 1 : 0));
        out.writeString(idNotes);
        out.writeString(idType);
        out.writeString(barcodeNum);
        out.writeString(phoneNumber);
        out.writeByte((byte) (isShawish ? 1 : 0));
        out.writeString(familyName);
        out.writeString(firstName);
        out.writeString(middleName);
        out.writeString(relationshipType);
        out.writeSerializable(childrenList);
        out.writeByte((byte) (isComplete ? 1 : 0));
        out.writeString(reasonForEdit);
        out.writeString(maritalStatus);
        out.writeString(gender);
        out.writeString(dateOfBirth);
        out.writeString(partnerName);
        out.writeString(movingLoc);
        out.writeString(phoneOwner);
        out.writeString(disabilities);
        out.writeString(assistanceType);
        out.writeString(principleApplicant);
        out.writeString(over18);
        out.writeString(mothersName);
        out.writeString(familyCount);
        out.writeSerializable(familyList);
        out.writeString(newCadasteral);
        out.writeString(newDistrict);


    }

    private static BeneficiaryObject readFromParcel(Parcel in) {


        String mainID = in.readString();
        String creationDate = in.readString();
        String completionDate = in.readString();
        String  criticality = in.readString();
        PCodeObject  pcode = in.readParcelable(PCodeObject.class.getClassLoader());
        PCodeObject  pcodeDist = in.readParcelable(PCodeObject.class.getClassLoader());
        String   officialID = in.readString();
        boolean idDoesExist = in.readByte() != 0;
        String idNotes = in.readString();
        String  idType = in.readString();
        String  barcodeNum = in.readString();
        String   phoneNumber = in.readString();
        boolean isShawish = in.readByte() != 0;
        String familyName = in.readString();
        String   firstName = in.readString();
        String   middleName = in.readString();
        String  relationshipType = in.readString();
        ArrayList<ChildObject> childObjects = (ArrayList<ChildObject>) in.readSerializable();
        boolean isComplete = in.readByte() != 0;
        String reasonForEdit = in.readString();
        String maritalStatus = in.readString();
        String gender = in.readString();
        String dateOfBirth = in.readString();
        String partnerName = in.readString();
        String answer4 = in.readString();
        String phoneOwner = in.readString();
        String disabilities = in.readString();
        String assistanceType = in.readString();
        String principleApplicant = in.readString();
        String over18 = in.readString();
        String mothersName = in.readString();
        String familyCount = in.readString();
        ArrayList<FamilyObject> familyObjects = (ArrayList<FamilyObject>) in.readSerializable();
        String newCadastral = in.readString();
        String newDistrict = in.readString();


        // in.readTypedList(childrenList,ChildObject.CREATOR);
       // ArrayList<ChildObject> childrenList = in.readArrayList(ChildObject.class.getClassLoader());

        BeneficiaryObject benny = new BeneficiaryObject(mainID, creationDate,completionDate,criticality,pcode,pcodeDist, officialID,idDoesExist,idNotes,idType,barcodeNum, phoneNumber,isShawish,familyName,firstName,middleName,relationshipType,childObjects,isComplete,reasonForEdit,maritalStatus,gender,dateOfBirth,partnerName,answer4, phoneOwner, disabilities, assistanceType,principleApplicant,over18,familyCount,mothersName,familyObjects,newCadastral,newDistrict);

        return benny;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public BeneficiaryObject createFromParcel(Parcel in) // (6)
        {
            return readFromParcel(in);
        }

        public BeneficiaryObject[] newArray(int size) { // (7)
            return new BeneficiaryObject[size];
        }
    };
}
