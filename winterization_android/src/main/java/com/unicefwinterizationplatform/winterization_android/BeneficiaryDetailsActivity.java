package com.unicefwinterizationplatform.winterization_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Tarek on 10/19/15.
 */
public class BeneficiaryDetailsActivity extends HeaderActivity {

    private ChildrenAdapter childrenAdapter;
    private FamilyAdapter familyAdapter;

    BeneficiaryObject beneficiaryObject;
    ListView childList;
    ListView familyList;

    String assistanceType;
    int EDIT_FORM = 0;
    int EDIT_SURVEY = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beneficiarydetails);

        LinearLayout lin = (LinearLayout) findViewById(R.id.main_layout);

        String docID = getIntent().getStringExtra("DOC");
        assistanceType = getIntent().getStringExtra("ASSISTANCE");

        Document doc = CouchBaseManager.getInstance(getApplicationContext()).getDocumentFromID(docID);

        beneficiaryObject = CouchBaseManager.getInstance(getApplicationContext()).extractBeneficiary(doc);

        refreshBeneficiary(beneficiaryObject);

        Button editBeneficiary = (Button) findViewById(R.id.button_editBeneficiary);
        editBeneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),AddBeneficiaryActivity.class);
                intent.putExtra("BENEFICIARY", beneficiaryObject);
                intent.putExtra("ASSISTANCE", assistanceType);
                if (beneficiaryObject.getIdType().equals("UNHCR-recorded"))
                    intent.putExtra("SELECT","recorded");
                else if (beneficiaryObject.getIdType().equals("UNHCR-registered"))
                    intent.putExtra("SELECT","registered");
                else
                    intent.putExtra("SELECT","none");

                intent.setAction("EDIT");
                startActivityForResult(intent,EDIT_FORM);

            }
        });

        refreshSurveys(doc);

        Button deleteBenny = new Button(getApplicationContext());

        deleteBenny.setGravity(Gravity.CENTER);

        deleteBenny.setBackground(getResources().getDrawable(R.drawable.custombutton_red));

        final LinearLayout.LayoutParams lparams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.
                WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams2.gravity = Gravity.CENTER;
        lparams2.setMargins(10, 10, 10, 10);
        deleteBenny.setLayoutParams(lparams2);
        deleteBenny.setTextColor(getResources().getColor(android.R.color.black));
        deleteBenny.setText("Delete Household");

        deleteBenny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CouchBaseManager.getInstance(getApplicationContext()).removeBeneficiary(beneficiaryObject);
                finish();

            }
        });
        lin.addView(deleteBenny);




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_FORM)
        {

            if (resultCode == RESULT_OK) {
                beneficiaryObject = data.getParcelableExtra("BENEFICIARY");

                refreshBeneficiary(beneficiaryObject);
                //  setListViewHeightBasedOnChildren(childList);
            }


        }
        else    if (requestCode == EDIT_SURVEY)
        {

            if (resultCode == RESULT_OK) {
                String docID = getIntent().getStringExtra("DOC");

                Document doc = CouchBaseManager.getInstance(getApplicationContext()).getDocumentFromID(docID);

                removeSurveys(doc);

                refreshSurveys(doc);
            }

        }


    }

    public void refreshBeneficiary(BeneficiaryObject beneficiaryObject)
    {
        String idType = beneficiaryObject.getIdType();//(String)doc.getCurrentRevision().getProperty("id_type");

        TextView idNumberText = (TextView) findViewById(R.id.textView_idNumber);
        TextView idTypeText = (TextView) findViewById(R.id.textView_idType);
        TextView familyNameText = (TextView) findViewById(R.id.textView_familySurname);
        TextView firstNameText = (TextView) findViewById(R.id.textView_firstName);
        TextView middleNameText = (TextView) findViewById(R.id.textView_middleName);
        TextView relationshipType = (TextView) findViewById(R.id.textView_relationshipType);
        TextView phoneText = (TextView) findViewById(R.id.textView_phoneField);
        TextView genderText = (TextView) findViewById(R.id.textView_gender);
        TextView maritalText = (TextView) findViewById(R.id.textView_maritalField);
        TextView mothersText = (TextView) findViewById(R.id.textView_motherName);


        TextView dobText = (TextView) findViewById(R.id.textView_DoB);

        //   TextView question1Text = (TextView) findViewById(R.id.textView_question1);
     //   TextView question2Text = (TextView) findViewById(R.id.textView_question2);
        TextView principleApplicant= (TextView) findViewById(R.id.textView_pa);
        TextView phoneOwnerText = (TextView) findViewById(R.id.textView_phoneOwner);
        TextView familyMove = (TextView) findViewById(R.id.textView_familyMove);
        TextView over18Text = (TextView) findViewById(R.id.textview_over18);
        TextView assistanceTypeText = (TextView) findViewById(R.id.textView_assistanceType);
        TextView disabilitiesText = (TextView) findViewById(R.id.textView_disabilities);
        TextView barcodeText = (TextView) findViewById(R.id.textView_barcode);
        TextView familyCount = (TextView) findViewById(R.id.textView_familycount);
        LinearLayout familyLayout = (LinearLayout) findViewById(R.id.layout_familylist);






        idNumberText.setText("ID Number: "+beneficiaryObject.getOfficialID());
        idTypeText.setText("ID Type: "+ beneficiaryObject.getIdType());
        phoneText.setText("Phone No.: "+beneficiaryObject.getPhoneNumber());
        phoneOwnerText.setText("Phone Belongs to: "+ beneficiaryObject.getPhoneOwner());
        disabilitiesText.setText("Children with Disabilities: " + beneficiaryObject.getDisabilities());
        assistanceTypeText.setText("Assitance Type: "+ beneficiaryObject.getAssistanceType());


        if (assistanceType.equals("cash")) {
            if (idType.equals("UNHCR-registered") || idType.equals("UNHCR-recorded")) {

                // question1Text.setText("Do you have school-aged children(6-15) not attending school? "+beneficiaryObject.getAnswer1());
                // question2Text.setText("Do you have school-aged children(6-15) working? "+beneficiaryObject.getAnswer2());
                principleApplicant.setText("Is the Principal Applicant still present? " + beneficiaryObject.getPrincipleApplicant());
                if (beneficiaryObject.getNewDistrict().equals("") && beneficiaryObject.getNewCadasteral().equals(""))
                    familyMove.setText("Will the family move in the future? " + beneficiaryObject.getMovingLoc());
                else
                    familyMove.setText("Will the family move in the future? " + beneficiaryObject.getMovingLoc() + ": " + beneficiaryObject.getNewDistrict() +" - "+ beneficiaryObject.getNewCadasteral());

                over18Text.setText("Is their a member over 18 who will be able to collect the card? " + beneficiaryObject.getOver18());

                familyNameText.setVisibility(View.GONE);
                firstNameText.setVisibility(View.GONE);
                middleNameText.setVisibility(View.GONE);
                relationshipType.setVisibility(View.GONE);
                genderText.setVisibility(View.GONE);
                maritalText.setVisibility(View.GONE);
                dobText.setVisibility(View.GONE);
                mothersText.setVisibility(View.GONE);
                familyCount.setVisibility(View.GONE);
                familyLayout.setVisibility(View.GONE);




            } else {

                familyNameText.setText("Family Name: " + beneficiaryObject.getFamilyName());
                firstNameText.setText("First Name: " + beneficiaryObject.getFirstName());
                middleNameText.setText("Middle Name: " + beneficiaryObject.getMiddleName());
                relationshipType.setText("Relationship Type: " + beneficiaryObject.getRelationshipType());
                genderText.setText("Gender: " + beneficiaryObject.getGender());
                maritalText.setText("Marital Status: " + beneficiaryObject.getMaritalStatus());
                dobText.setText("Date of Birth: " + beneficiaryObject.getDateOfBirth());
                mothersText.setText("Mother's Name: " + beneficiaryObject.getMothersName());
                familyCount.setText("Number of Family Members: " + beneficiaryObject.getFamilyCount());


//            question1Text.setText("Do you have school-aged children(6-15) not attending school? "+ beneficiaryObject.getAnswer1());
//            question2Text.setText("Do you have school-aged children(6-15) working? "+beneficiaryObject.getAnswer2());
                if (beneficiaryObject.getNewCadasteral().equals("") || beneficiaryObject.getNewDistrict().equals(""))
                    familyMove.setText("Will the family move in the future? " + beneficiaryObject.getMovingLoc());
                else
                    familyMove.setText("Will the family move in the future? " + beneficiaryObject.getMovingLoc() + ": " + beneficiaryObject.getNewDistrict() +" - " + beneficiaryObject.getNewCadasteral());


                ArrayList<FamilyObject> arr2 = beneficiaryObject.getFamilyList();

                familyAdapter = new FamilyAdapter(this,"IDLE");
                familyAdapter.addList(arr2);// new ListAdapter(this, android.R.layout.simple_list_item_1, arr);
                familyList = (ListView) this.findViewById(R.id.family_list);

                familyList.setAdapter(familyAdapter);

                principleApplicant.setVisibility(View.GONE);
                over18Text.setVisibility(View.GONE);

            }

            barcodeText.setVisibility(View.GONE);
        }
        else if (assistanceType.equals("kits")){

            barcodeText.setText("Barcode Number: "+ beneficiaryObject.getBarcodeNum());
            familyNameText.setText("Family Name: " + beneficiaryObject.getFamilyName());
            firstNameText.setText("First Name: " + beneficiaryObject.getFirstName());
            middleNameText.setText("Middle Name: " + beneficiaryObject.getMiddleName());
            relationshipType.setText("Relationship Type: " + beneficiaryObject.getRelationshipType());
            genderText.setText("Gender: " + beneficiaryObject.getGender());
            maritalText.setText("Marital Status: " + beneficiaryObject.getMaritalStatus());
            dobText.setText("Date of Birth: " + beneficiaryObject.getDateOfBirth());
            mothersText.setText("Mother's Name: " + beneficiaryObject.getMothersName());
            familyCount.setText("Number of Family Members: " + beneficiaryObject.getFamilyCount());



//            question1Text.setText("Do you have school-aged children(6-15) not attending school? "+ beneficiaryObject.getAnswer1());
//            question2Text.setText("Do you have school-aged children(6-15) working? "+beneficiaryObject.getAnswer2());
            if (beneficiaryObject.getNewCadasteral().equals("") || beneficiaryObject.getNewDistrict().equals(""))
                familyMove.setText("Will the family move in the future? " + beneficiaryObject.getMovingLoc());
            else
                familyMove.setText("Will the family move in the future? " + beneficiaryObject.getMovingLoc() + ": " + beneficiaryObject.getNewDistrict() +" - " + beneficiaryObject.getNewCadasteral());


            principleApplicant.setVisibility(View.GONE);
            over18Text.setVisibility(View.GONE);
            idNumberText.setVisibility(View.GONE);
            idTypeText.setVisibility(View.GONE);
            familyLayout.setVisibility(View.GONE);


        }
        ArrayList<ChildObject> arr = beneficiaryObject.childrenList;

        childrenAdapter = new ChildrenAdapter(this,"IDLE",Constants.ROLE_KITS_ASSESSOR);
        childrenAdapter.addList(arr);// new ListAdapter(this, android.R.layout.simple_list_item_1, arr);
        childList = (ListView) this.findViewById(R.id.child_list);

        childList.setAdapter(childrenAdapter);

    }

    public void refreshSurveys(Document doc)
    {
        LinearLayout lin = (LinearLayout) findViewById(R.id.main_layout);
        ArrayList<Map<String,Object>> surveys = (ArrayList<Map<String,Object>>)doc.getCurrentRevision().getProperty("surveys");
        int i = 0;
        if(surveys != null) {
            for (Map<String, Object> survey : surveys) {
                LinearLayout subLin = new LinearLayout(getApplicationContext());
                subLin.setGravity(Gravity.CENTER);
                subLin.setOrientation(LinearLayout.VERTICAL);

                subLin.setBackground(getResources().getDrawable(R.drawable.customborder3));
                final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lparams.setMargins(10, 10, 10, 10);
                subLin.setLayoutParams(lparams);
                lin.addView(subLin);

                String key = (String) survey.keySet().toArray()[0];
                subLin.setTag(key);
                subLin.setId(100+i);
                TextView surveyTitle = new TextView(getApplicationContext());
                surveyTitle.setText(key);
                surveyTitle.setTextColor(getResources().getColor(android.R.color.black));
                surveyTitle.setTextSize(20);

                subLin.addView(surveyTitle);

                ArrayList<Map<String, Object>> questions = (ArrayList<Map<String, Object>>) survey.get(key);

                for (Map<String, Object> question : questions) {
                    String key2 = (String) question.keySet().toArray()[0];
                    Map<String, Object> singleq = (Map<String, Object>) question.get(key2);
                    LinearLayout subsubLin = new LinearLayout(getApplicationContext());
                    subsubLin.setGravity(Gravity.CENTER);
                    subsubLin.setOrientation(LinearLayout.VERTICAL);
                    subsubLin.setBackground(getResources().getDrawable(R.drawable.customborder));
                    subsubLin.setLayoutParams(lparams);
                    subLin.addView(subsubLin);

                    TextView questionText = new TextView(getApplicationContext());
                    questionText.setText("Q: " + singleq.get("question"));
                    questionText.setTextColor(getResources().getColor(android.R.color.black));
                    questionText.setTextSize(15);
                    subsubLin.addView(questionText);

                    TextView answerText = new TextView(getApplicationContext());
                    answerText.setText("A: " + singleq.get("answer"));
                    answerText.setTextColor(getResources().getColor(android.R.color.black));
                    answerText.setTextSize(15);
                    subsubLin.addView(answerText);

                }

                Button editSurvey = new Button(getApplicationContext());

                editSurvey.setGravity(Gravity.CENTER);
                editSurvey.setBackground(getResources().getDrawable(R.drawable.custombutton_green));

                final LinearLayout.LayoutParams lparams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.
                        WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lparams2.setMargins(10, 10, 10, 10);
                editSurvey.setLayoutParams(lparams2);
                editSurvey.setTextColor(getResources().getColor(android.R.color.black));
                editSurvey.setTag(key);
                editSurvey.setText("Edit Survey");

                editSurvey.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (view.getTag().equals("Survey")) {

                            Intent intent = new Intent(getApplicationContext(),SurveyActivity.class);
                            intent.putExtra("INC", 1);
                            intent.putExtra("BENEFICIARY",beneficiaryObject);
                            intent.setAction("EDIT");
                            startActivityForResult(intent,EDIT_SURVEY);
                        }
                    }
                });
                subLin.addView(editSurvey);

                i++;
            }
        }
    }


    public void removeSurveys(Document doc)
    {
        LinearLayout lin = (LinearLayout) findViewById(R.id.main_layout);
        ArrayList<Map<String,Object>> surveys = (ArrayList<Map<String,Object>>)doc.getCurrentRevision().getProperty("surveys");
        int i = 0;
        if(surveys != null) {
            for (Map<String, Object> survey : surveys) {

                LinearLayout subLin = (LinearLayout)findViewById(100+i);
                lin.removeView(subLin) ;
              i++;
            }
        }
    }

    }
