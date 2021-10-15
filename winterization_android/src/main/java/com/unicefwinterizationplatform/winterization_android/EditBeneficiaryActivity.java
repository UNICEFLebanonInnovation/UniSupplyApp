package com.unicefwinterizationplatform.winterization_android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Tarek on 9/22/2014.
 */
public class EditBeneficiaryActivity extends HeaderActivity {

    final int ADD_CHILD_REQUEST = 0;
    final int EDIT_CHILD_REQUEST = 1;
    final int EDIT_PCODE = 2;

    Activity thisActivity = this;
    BeneficiaryObject beneficiaryObject;
    Context context = this;
    private ChildrenAdapter childrenAdapter;
    ListView childList;
    private String idType;
    int yy;
    int mm;
    int dd;
    TextView DoBView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editbeneficiary);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //ChildrenAdapter childrenAdapter;
        beneficiaryObject = getIntent().getParcelableExtra("BENEFICIARY");

       // TextView barcodeText = (TextView) findViewById(R.id.editText_barcodeNumber);
        EditText phoneText = (EditText) findViewById(R.id.editText_familyPhoneNumber);
        //CheckBox shawishCheckBox = (CheckBox) findViewById(R.id.checkBox_shawish);

        final  EditText officialIDText = (EditText) findViewById(R.id.editText_idNumber);
       //final CheckBox idCheckBox = (CheckBox) findViewById(R.id.checkBox_idExists);
        //EditText idNotes = (EditText)  findViewById(R.id.editText_idNotes);
        Spinner idTypeSpinner = (Spinner) findViewById(R.id.spinner_idTypes);
        //EditText familyName = (EditText) findViewById(R.id.editText_familySurname);
       // EditText firstName = (EditText) findViewById(R.id.editText_firstName);
       // EditText middleName = (EditText) findViewById(R.id.editText_middleName);
      //  Spinner relationshipTypeSpinner = (Spinner) findViewById(R.id.spinner_relationshipTypes);
      //  Spinner genderTypeSpinner = (Spinner) findViewById(R.id.spinner_genderTypes);
     //   Spinner maritalTypeSpinner = (Spinner) findViewById(R.id.spinner_maritalTypes);
     //   TextView latText = (TextView) findViewById(R.id.editText_latitude);
     //   TextView longText = (TextView) findViewById(R.id.editText_longitude);
     //   TextView altText = (TextView) findViewById(R.id.editText_altitude);
     //   DoBView = (TextView) findViewById(R.id.textView_DoB);
        Spinner q1Types = (Spinner) findViewById(R.id.spinner_yesno);
        Spinner q2Types = (Spinner) findViewById(R.id.spinner_yesno2);
        Spinner q3Types = (Spinner) findViewById(R.id.spinner_pa);
        Spinner q4Types = (Spinner) findViewById(R.id.spinner_yesno3);

        q4Types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                EditText answer4 = (EditText) findViewById(R.id.editText_where);

                String answer = (String)adapterView.getItemAtPosition(i);

                if(answer.equals("YES"))
                {
                    answer4.setVisibility(View.VISIBLE);
                }
                else
                {
                    answer4.setVisibility(View.GONE);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



       // TextView reasonText = (TextView) findViewById(R.id.editText_reasonforedit);


        TextView pcodeSelect = (TextView) findViewById(R.id.textView_geolocationData);
        pcodeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),PCodeSelectionActivity.class);
                intent.setAction("REPLACE_PCODE");
                startActivityForResult(intent,EDIT_PCODE);
            }
        });


        TextView pcodeView =  (TextView) findViewById(R.id.textView_PCodeNo);

        pcodeView.setText(beneficiaryObject.getPcode().getPcodeID());

        pcodeSelect.setText(beneficiaryObject.getPcode().getPcodeName());
      //  latText.setText(beneficiaryObject.getPcode().getPcodeLat());
      //  longText.setText(beneficiaryObject.getPcode().getPcodeLong());
      //  altText.setText(beneficiaryObject.getAltitude());

//        beneficiaryObject.setLatitude(latText.getText().toString());
 //       beneficiaryObject.setLongitude(longText.getText().toString());
 //       beneficiaryObject.setAltitude(altText.getText().toString());
      //  barcodeText.setText(beneficiaryObject.getBarcodeNum());

      //  ArrayAdapter myAdap = (ArrayAdapter) idTypeSpinner.getAdapter(); //cast to an ArrayAdapter

        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("UNHCR");

        ArrayAdapter<String> myAdap = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, spinnerArray); //(ArrayAdapter) idTypes.getAdapter(); //cast to an ArrayAdapter
        idTypeSpinner.setAdapter(myAdap);
        int spinnerPosition = myAdap.getPosition(beneficiaryObject.getIdType());
        idTypeSpinner.setSelection(spinnerPosition);
        idTypeSpinner.setEnabled(false);


        //   phoneText.setText(beneficiaryObject.getPhoneNumber());
        officialIDText.setText(beneficiaryObject.getOfficialID());
        officialIDText.setEnabled(false);
      //  LinearLayout layoutNotes = (LinearLayout) findViewById(R.id.layout_idNotes);


      //  idNotes.setText(beneficiaryObject.getIdNotes());
      //  familyName.setText(beneficiaryObject.getFamilyName());
      //  firstName.setText(beneficiaryObject.getFirstName());
      //  middleName.setText(beneficiaryObject.getMiddleName());


       // ArrayAdapter myAdap2 = (ArrayAdapter) relationshipTypeSpinner.getAdapter(); //cast to an ArrayAdapter
       // int spinnerPosition2 = myAdap2.getPosition(beneficiaryObject.getRelationshipType());
       // relationshipTypeSpinner.setSelection(spinnerPosition2);


       // ArrayAdapter myAdap3 = (ArrayAdapter) genderTypeSpinner.getAdapter(); //cast to an ArrayAdapter
       // int spinnerPosition3 = myAdap3.getPosition(beneficiaryObject.getGender());
      //  genderTypeSpinner.setSelection(spinnerPosition3);

     //   ArrayAdapter myAdap4 = (ArrayAdapter) maritalTypeSpinner.getAdapter(); //cast to an ArrayAdapter
    //    int spinnerPosition4 = myAdap4.getPosition(beneficiaryObject.getMaritalStatus());
    //    maritalTypeSpinner.setSelection(spinnerPosition4);

   /*     ArrayAdapter myAdap5 = (ArrayAdapter) q1Types.getAdapter(); //cast to an ArrayAdapter
        int spinnerPosition5 = myAdap5.getPosition(beneficiaryObject.getAnswer1());
        q1Types.setSelection(spinnerPosition5);

        ArrayAdapter myAdap6 = (ArrayAdapter) q2Types.getAdapter(); //cast to an ArrayAdapter
        int spinnerPosition6 = myAdap6.getPosition(beneficiaryObject.getAnswer2());
        q2Types.setSelection(spinnerPosition6);

        ArrayAdapter myAdap7 = (ArrayAdapter) q3Types.getAdapter(); //cast to an ArrayAdapter
        int spinnerPosition7 = myAdap7.getPosition(beneficiaryObject.getAnswer2());
        q2Types.setSelection(spinnerPosition);*/

        //DoBView.setText(beneficiaryObject.getDateOfBirth());

       // TextView pcodeSelect = (TextView) findViewById(R.id.textView_geolocationData);


       // Button dateOfBirthBtn = (Button) findViewById(R.id.button_dob);
    //    dateOfBirthBtn.setOnClickListener(new View.OnClickListener() {
 //           @Override
  //          public void onClick(View view) {
    //            SelectDateFragment newFragment = new SelectDateFragment();
    //            newFragment.show(getFragmentManager(), "datePicker");
    //        }
   //     });

        ArrayList<ChildObject> arr = beneficiaryObject.childrenList;

        LinearLayout lin = new LinearLayout(this);

        childrenAdapter = new ChildrenAdapter(this,"EDIT",Constants.ROLE_KITS_ASSESSOR);
        childrenAdapter.addList(arr);// new ListAdapter(this, android.R.layout.simple_list_item_1, arr);
        childList = (ListView) this.findViewById(R.id.child_list);
        childList.setAdapter(childrenAdapter);
        childList.setOnItemClickListener(onItemClickListener());

        ActionBar actionBar = getActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        Button addChildBtn = (Button) findViewById(R.id.add_child_btn);
        addChildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =   new Intent(getApplicationContext(), ChildViewActivity.class);
                intent.setAction(Constants.ADD_CHILD);
                startActivityForResult(intent, ADD_CHILD_REQUEST);

            }
        });

        Button editBeneficiary = (Button) findViewById(R.id.button_saveBeneficiary);
        editBeneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final TextView barcodeVal = (TextView) promptsView
                        .findViewById(R.id.textView_barcodeNumber);

                barcodeVal.setText("Do you want to save?");
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result

                                        if (validationCheck()) {
                                            try {
                                                //   CouchBaseManager.getInstance(this).testAdditionOne("TWO");\


                                                // TextView barcodeText = (TextView) findViewById(R.id.editText_barcodeNumber);
                                                EditText phoneText = (EditText) findViewById(R.id.editText_familyPhoneNumber);
                                                //CheckBox shawishCheckBox = (CheckBox) findViewById(R.id.checkBox_shawish);
                                                Spinner idTypeSpinner = (Spinner) findViewById(R.id.spinner_idTypes);
                                                EditText officialIDText = (EditText) findViewById(R.id.editText_idNumber);
                                                //CheckBox idCheckBox = (CheckBox) findViewById(R.id.checkBox_idExists);
                                                //EditText idNotes = (EditText) findViewById(R.id.editText_idNotes);
                                                //EditText familyName = (EditText) findViewById(R.id.editText_familySurname);
                                                //EditText firstName = (EditText) findViewById(R.id.editText_firstName);
                                                //EditText middleName = (EditText) findViewById(R.id.editText_middleName);
                                                //Spinner relationshipTypeSpinner = (Spinner) findViewById(R.id.spinner_relationshipTypes);
                                                //Spinner genderTypeSpinner = (Spinner) findViewById(R.id.spinner_genderTypes);
                                                //Spinner maritalTypeSpinner = (Spinner) findViewById(R.id.spinner_maritalTypes);
                                                //TextView dobView = (TextView) findViewById(R.id.textView_DoB);

                                                //TextView latText = (TextView) findViewById(R.id.editText_latitude);
                                                //TextView longText = (TextView) findViewById(R.id.editText_longitude);
                                                //TextView altText = (TextView) findViewById(R.id.editText_altitude);
                                                Spinner q1Types = (Spinner) findViewById(R.id.spinner_yesno);
                                                Spinner q2Types = (Spinner) findViewById(R.id.spinner_yesno2);
                                                Spinner q3Types = (Spinner) findViewById(R.id.spinner_pa);
                                                Spinner q4Types = (Spinner) findViewById(R.id.spinner_yesno3);
                                                EditText answer4 = (EditText) findViewById(R.id.editText_where);

                                                //beneficiaryObject.setLatitude(latText.getText().toString());
                                                //beneficiaryObject.setLongitude(longText.getText().toString());
                                                //beneficiaryObject.setAltitude(altText.getText().toString());

                                                beneficiaryObject.setIdType(idTypeSpinner.getSelectedItem().toString());
                                                //   beneficiaryObject.setBarcodeNum(barcodeText.getText().toString());
                                                beneficiaryObject.setPhoneNumber(phoneText.getText().toString());
                                                //   beneficiaryObject.setShawish(shawishCheckBox.isChecked());
                                                beneficiaryObject.setOfficialID(officialIDText.getText().toString());
                                                // beneficiaryObject.setIdDoesExist(idCheckBox.isChecked());
                                                // beneficiaryObject.setIdNotes(idNotes.getText().toString());
                                                beneficiaryObject.setComplete(false);
                                                beneficiaryObject.setFamilyName("");
                                                beneficiaryObject.setFirstName("");
                                                beneficiaryObject.setMiddleName("");
                                                beneficiaryObject.setRelationshipType("");
                                                beneficiaryObject.setMaritalStatus("");
                                                beneficiaryObject.setGender("");
                                                beneficiaryObject.setDateOfBirth("");
                                                beneficiaryObject.setPartnerName(UserPrefs.getOrganisation(getApplicationContext()));
                                               // beneficiaryObject.setAnswer1(q1Types.getSelectedItem().toString());
                                               // beneficiaryObject.setAnswer2(q2Types.getSelectedItem().toString());
                                               // beneficiaryObject.setAnswer3(q3Types.getSelectedItem().toString());
                                                beneficiaryObject.setMovingLoc(q4Types.getSelectedItem().toString());

                                                beneficiaryObject.setMainID(beneficiaryObject.getOfficialID()+":"+beneficiaryObject.getPartnerName());


                                                // beneficiaryObject.setReasonForEdit(reasonText.getText().toString());
                                               /* if(!idCheckBox.isChecked()) {
                                                    beneficiaryObject.setOfficialID(officialIDText.getText().toString());
                                                    beneficiaryObject.setIdNotes("");
                                                }
                                                else {
                                                    beneficiaryObject.setOfficialID("");
                                                    beneficiaryObject.setIdNotes(idNotes.getText().toString());
                                                }*/
                                                CouchBaseManager.getInstance(context).addBeneficiary(beneficiaryObject);


                                            } catch (Exception e) {

                                            }

                                            Intent intent = new Intent(context, SurveyActivity.class);
                                            intent.putExtra("INC", 1);
                                            intent.putExtra("BENEFICIARY",beneficiaryObject);
                                            startActivity(intent);


                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();


            }
        });


       /* Button removeBeneficiary = (Button) findViewById(R.id.button_removeBeneficiary);
        removeBeneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final TextView barcodeVal = (TextView) promptsView
                        .findViewById(R.id.textView_barcodeNumber);

                barcodeVal.setText("Do you want to remove Beneficiary?");
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        try {
                                            //   CouchBaseManager.getInstance(this).testAdditionOne("TWO");
                                            CouchBaseManager.getInstance(context).removeBeneficiary(beneficiaryObject);
                                        }
                                        catch (Exception e)
                                        {

                                        }

                                        if (getIntent().getAction()!= null && getIntent().getAction().equals("editNewBeneficiary")) {
                                            finish();
                                        }
                                        else{
                                            Intent backIntent = new Intent(context, BrowseBeneficiariesActivity.class);
                                            backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(backIntent);
                                        }

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();


            }
        });*/

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0)
        {
            if (resultCode == RESULT_OK) {
                ChildObject childObject = (ChildObject) data.getSerializableExtra("CHILD");
                this.childrenAdapter.addItemToList(childObject);
            }
        }
        else    if (requestCode == 1)
        {
            if (resultCode == 1) {
                ChildObject childObject = (ChildObject)data.getSerializableExtra("CHILD");
                int pos = data.getIntExtra("position",0);
                this.childrenAdapter.editItemToList(childObject,pos);

            }
            else   if (resultCode == 2) {
                //   ChildObject childObject = data.getParcelableExtra("CHILD");
                int pos = data.getIntExtra("position",0);
                this.childrenAdapter.removeItemFromList(pos);

            }
        }

        else if(requestCode == 2){
            if (resultCode == RESULT_OK) {

                PCodeObject pcodeObject = data.getParcelableExtra("PCODE");
                beneficiaryObject.setPcode(pcodeObject);
                //pcode = pcodeObject;

                TextView pcodeSelect = (TextView) findViewById(R.id.textView_geolocationData);
                pcodeSelect.setText(pcodeObject.getPcodeName());


                TextView pcodeView =  (TextView) findViewById(R.id.textView_PCodeNo);

                pcodeView.setText(beneficiaryObject.getPcode().getPcodeID());

                //TextView latText = (TextView) findViewById(R.id.editText_latitude);
               // TextView longText = (TextView) findViewById(R.id.editText_longitude);

               // latText.setText(pcodeObject.getPcodeLat());
               // longText.setText(pcodeObject.getPcodeLong());
            }
        }

        else if(requestCode == 3) {
            if (resultCode == RESULT_OK) {

                String barcodeVal = data.getStringExtra("BARCODE");
                //TextView barcodeField = (TextView) findViewById(R.id.editText_barcodeNumber);
               // barcodeField.setText(barcodeVal);
            }

        }


    }



    private AdapterView.OnItemClickListener onItemClickListener(){

        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent =   new Intent(getApplicationContext(), ChildViewActivity.class);
                ChildObject childObject = childrenAdapter.getList().get(i);
                intent.setAction(Constants.EDIT_CHILD);
                intent.putExtra("CHILD",childObject);
                intent.putExtra("position",i);
                startActivityForResult(intent, EDIT_CHILD_REQUEST);


            }
        };

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    private boolean validationCheck()
    {
        boolean isValid = true;
       // TextView barcodeText = (TextView) findViewById(R.id.editText_barcodeNumber);
        EditText phoneText = (EditText) findViewById(R.id.editText_familyPhoneNumber);
        EditText officialIDText = (EditText) findViewById(R.id.editText_idNumber);
       // EditText idNotes = (EditText) findViewById(R.id.editText_idNotes);
        //EditText familyName = (EditText) findViewById(R.id.editText_familySurname);
        //EditText firstName = (EditText) findViewById(R.id.editText_firstName);
        //EditText middleName = (EditText) findViewById(R.id.editText_middleName);
        //CheckBox idCheck = (CheckBox) findViewById(R.id.checkBox_idExists);
        //EditText reasonForEdit = (EditText) findViewById(R.id.editText_reasonforedit);


        TextView idField = (TextView) findViewById(R.id.textView_idNumField);
      //  TextView idNotesField = (TextView) findViewById(R.id.textView_idNotes);
       // TextView barcodeField = (TextView) findViewById(R.id.textView_barcodeField);
        TextView phoneField = (TextView) findViewById(R.id.textView_phoneField);
      //  TextView surnameField = (TextView) findViewById(R.id.textView_surnameField);
      //  TextView firstField = (TextView) findViewById(R.id.textView_firstField);
      //  TextView middleField = (TextView) findViewById(R.id.textView_middleField);

      //  Spinner idTypes = (Spinner) findViewById(R.id.spinner_idTypes);
      //  Spinner genderTypes = (Spinner) findViewById(R.id.spinner_genderTypes);
      //  Spinner maritalTypes = (Spinner) findViewById(R.id.spinner_maritalTypes);
       // Spinner relTypes = (Spinner) findViewById(R.id.spinner_relationshipTypes);
        Spinner q1Types = (Spinner) findViewById(R.id.spinner_yesno);
        Spinner q2Types = (Spinner) findViewById(R.id.spinner_yesno2);
        Spinner q3Types = (Spinner) findViewById(R.id.spinner_pa);
        Spinner q4Types = (Spinner) findViewById(R.id.spinner_yesno3);
        EditText answer4 = (EditText) findViewById(R.id.editText_where);
       // TextView reasonForEditField = (TextView) findViewById(R.id.textView_reasonforedit_field);



       /* if(barcodeText.getText().toString().trim().equals(""))
        {
            barcodeField.setTextColor(getResources().getColor(R.color.red));
            isValid = false;
            Toast.makeText(EditBeneficiaryActivity.this,
                    "Empty Fields",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            idField.setTextColor(getResources().getColor(R.color.black));
        }*/

    /*    if(idCheck.isChecked()) {
            if (idNotes.getText().toString().trim().equals("")) {
                idNotesField.setTextColor(getResources().getColor(R.color.red));
                isValid = false;
                Toast.makeText(EditBeneficiaryActivity.this,
                        "Empty Fields",
                        Toast.LENGTH_LONG).show();
            }
            else
            {
                idNotesField.setTextColor(getResources().getColor(R.color.black));
            }
        }
        else
        {
            if (officialIDText.getText().toString().trim().equals("")) {
                idField.setTextColor(getResources().getColor(R.color.red));
                isValid = false;
                Toast.makeText(EditBeneficiaryActivity.this,
                        "Empty Fields",
                        Toast.LENGTH_LONG).show();
            }
            else
            {
                idField.setTextColor(getResources().getColor(R.color.black));
            }
        }*/



        if(phoneText.getText().toString().trim().equals(""))
        {
            phoneField.setTextColor(getResources().getColor(R.color.red));
            isValid = false;
            Toast.makeText(EditBeneficiaryActivity.this,
                    "Empty Fields",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            phoneField.setTextColor(getResources().getColor(R.color.black));
        }

     /*   if(familyName.getText().toString().trim().equals(""))
        {
            surnameField.setTextColor(getResources().getColor(R.color.red));
            isValid = false;
            Toast.makeText(EditBeneficiaryActivity.this,
                    "Empty Fields",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            surnameField.setTextColor(getResources().getColor(R.color.black));
        }

        if(firstName.getText().toString().trim().equals(""))
        {
            firstField.setTextColor(getResources().getColor(R.color.red));
            isValid = false;
            Toast.makeText(EditBeneficiaryActivity.this,
                    "Empty Fields",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            firstField.setTextColor(getResources().getColor(R.color.black));
        }

        if(middleName.getText().toString().trim().equals(""))
        {
            middleField.setTextColor(getResources().getColor(R.color.red));
            isValid = false;
            Toast.makeText(EditBeneficiaryActivity.this,
                    "Empty Fields",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            middleField.setTextColor(getResources().getColor(R.color.black));
        }
        if(idTypes.getSelectedItem().equals("----"))
        {
            isValid = false;
            Toast.makeText(EditBeneficiaryActivity.this,
                    "ID Type Blank",
                    Toast.LENGTH_LONG).show();
        }


        if(relTypes.getSelectedItem().equals("----"))
        {
            isValid = false;
            Toast.makeText(EditBeneficiaryActivity.this,
                    "Relationship Type Blank",
                    Toast.LENGTH_LONG).show();
        }
        if(genderTypes.getSelectedItem().equals("----"))
        {
            isValid = false;
            Toast.makeText(EditBeneficiaryActivity.this,
                    "Gender Type Blank",
                    Toast.LENGTH_LONG).show();
        }
        if(maritalTypes.getSelectedItem().equals("----"))
        {
            isValid = false;
            Toast.makeText(EditBeneficiaryActivity.this,
                    "Marital status Blank",
                    Toast.LENGTH_LONG).show();
        }*/
        if(q1Types.getSelectedItem().equals("----"))
        {
            isValid = false;
            Toast.makeText(EditBeneficiaryActivity.this,
                    "Question 1  Blank",
                    Toast.LENGTH_LONG).show();
        }
        if(q2Types.getSelectedItem().equals("----"))
        {
            isValid = false;
            Toast.makeText(EditBeneficiaryActivity.this,
                    "Question 2  Blank",
                    Toast.LENGTH_LONG).show();
        }
        if(q3Types.getSelectedItem().equals("----"))
        {
            isValid = false;
            Toast.makeText(EditBeneficiaryActivity.this,
                    "Question 3 Blank",
                    Toast.LENGTH_LONG).show();
        }
        if(q4Types.getSelectedItem().equals("----"))
        {
            isValid = false;
            Toast.makeText(EditBeneficiaryActivity.this,
                    "Question 2 Type Blank.",
                    Toast.LENGTH_LONG).show();
        }
        else if (q4Types.getSelectedItem().equals("YES") && answer4.getText().toString().isEmpty())
        {
            isValid = false;
            Toast.makeText(EditBeneficiaryActivity.this,
                    "Please type in the new location.",
                    Toast.LENGTH_LONG).show();
        }

       /* if(reasonForEdit.getText().toString().trim().equals(""))
        {
            reasonForEditField.setTextColor(getResources().getColor(R.color.red));
            isValid = false;
            Toast.makeText(EditBeneficiaryActivity.this,
                    "Empty Fields",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            middleField.setTextColor(getResources().getColor(R.color.black));
        }*/
        if(childrenAdapter.getList().size()==0)
        {
            isValid = false;
            Toast.makeText(EditBeneficiaryActivity.this,
                    "Please Add Children",
                    Toast.LENGTH_LONG).show();

        }

        return isValid;

    }


  /*  public  class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            if (yy ==0) {
                yy = calendar.get(Calendar.YEAR);
                mm = calendar.get(Calendar.MONTH);
                dd = calendar.get(Calendar.DAY_OF_MONTH);
            }
            return new DatePickerDialog(getActivity(), this, yy, mm, dd);
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            updateDisplay(yy, mm, dd);
        }
    }*/

   /* public  void updateDisplay(int year,
                               int month, int day) {

        yy = year;
        mm = month;
        dd = day;

        DoBView.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(day).append("-")
                        .append(month + 1).append("-")
                        .append(year).append(" "));
    }*/

}

