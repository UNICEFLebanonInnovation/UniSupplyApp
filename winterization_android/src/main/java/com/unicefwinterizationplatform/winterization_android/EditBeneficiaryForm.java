package com.unicefwinterizationplatform.winterization_android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;

/**
 * Created by Tarek on 10/20/15.
 */
public class EditBeneficiaryForm extends HeaderActivity {

    /**
     * Created by Tarek on 9/22/2014.
     */

        final int ADD_CHILD_REQUEST = 0;
        final int EDIT_CHILD_REQUEST = 1;
        final int EDIT_PCODE = 2;

        Activity thisActivity = this;
        BeneficiaryObject beneficiaryObject;
        Context context = this;
        private ChildrenAdapter childrenAdapter;
        ListView childList;
        int yy;
        int mm;
        int dd;
        TextView DoBView;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_editform);

            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            //ChildrenAdapter childrenAdapter;
            beneficiaryObject = getIntent().getParcelableExtra("BENEFICIARY");

            EditText phoneText = (EditText) findViewById(R.id.editText_familyPhoneNumber);

            final EditText officialIDText = (EditText) findViewById(R.id.editText_idNumber);
            Spinner idTypeSpinner = (Spinner) findViewById(R.id.spinner_idTypes);
            EditText familyName = (EditText) findViewById(R.id.editText_familySurname);
            EditText firstName = (EditText) findViewById(R.id.editText_firstName);
            EditText middleName = (EditText) findViewById(R.id.editText_middleName);
            Spinner relationshipTypeSpinner = (Spinner) findViewById(R.id.spinner_relationshipTypes);
            Spinner genderTypeSpinner = (Spinner) findViewById(R.id.spinner_genderTypes);
            Spinner maritalTypeSpinner = (Spinner) findViewById(R.id.spinner_maritalTypes);
            DoBView = (TextView) findViewById(R.id.textView_DoB);
            Spinner q1Types = (Spinner) findViewById(R.id.spinner_yesno);
            Spinner q2Types = (Spinner) findViewById(R.id.spinner_yesno2);
            Spinner q3Types = (Spinner) findViewById(R.id.spinner_pa);

            TextView surnameField = (TextView) findViewById(R.id.textView_surnameField);
            TextView firstField = (TextView) findViewById(R.id.textView_firstField);
            TextView middleField = (TextView) findViewById(R.id.textView_middleField);
            TextView relationshipField = (TextView) findViewById(R.id.textView_relationshipField);
            TextView genderField = (TextView) findViewById(R.id.textView_genderField);
            TextView maritalStatus = (TextView) findViewById(R.id.textView_maritalField);
            Button dob =  (Button) findViewById(R.id.button_dob);
            TextView dobField = (TextView) findViewById(R.id.textView_DoBField);
            Spinner q4Types = (Spinner) findViewById(R.id.spinner_yesno3);
            EditText answer4 = (EditText) findViewById(R.id.editText_where);

            officialIDText.setEnabled(false);
            idTypeSpinner.setEnabled(false);

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


            TextView pcodeSelect = (TextView) findViewById(R.id.textView_geolocationData);
            pcodeSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getApplicationContext(), PCodeSelectionActivity.class);
                    intent.setAction("REPLACE_PCODE");
                    startActivityForResult(intent, EDIT_PCODE);
                }
            });


            TextView pcodeView = (TextView) findViewById(R.id.textView_PCodeNo);

            pcodeView.setText(beneficiaryObject.getPcode().getPcodeID());

            pcodeSelect.setText(beneficiaryObject.getPcode().getPcodeName());

            if (beneficiaryObject.getIdType().equals("UNHCR")) {

                officialIDText.setText(beneficiaryObject.getOfficialID());
                officialIDText.setEnabled(false);

                ArrayList<String> spinnerArray = new ArrayList<String>();
                spinnerArray.add("UNHCR");

                ArrayAdapter<String> myAdap = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerArray); //(ArrayAdapter) idTypes.getAdapter(); //cast to an ArrayAdapter
                idTypeSpinner.setAdapter(myAdap);
                int spinnerPosition = myAdap.getPosition(beneficiaryObject.getIdType());
                idTypeSpinner.setSelection(spinnerPosition);
                idTypeSpinner.setEnabled(false);

                phoneText.setText(beneficiaryObject.getPhoneNumber());

//                ArrayAdapter myAdap5 = (ArrayAdapter) q1Types.getAdapter(); //cast to an ArrayAdapter
//                int spinnerPosition5 = myAdap5.getPosition(beneficiaryObject.getAnswer1());
//                q1Types.setSelection(spinnerPosition5);
//
//                ArrayAdapter myAdap6 = (ArrayAdapter) q2Types.getAdapter(); //cast to an ArrayAdapter
//                int spinnerPosition6 = myAdap6.getPosition(beneficiaryObject.getAnswer2());
//                q2Types.setSelection(spinnerPosition6);
//
//                ArrayAdapter myAdap7 = (ArrayAdapter) q3Types.getAdapter(); //cast to an ArrayAdapter
//                int spinnerPosition7 = myAdap7.getPosition(beneficiaryObject.getAnswer3());
//                q3Types.setSelection(spinnerPosition7);

                ArrayAdapter myAdap8 = (ArrayAdapter) q4Types.getAdapter(); //cast to an ArrayAdapter
                int spinnerPosition8 = myAdap8.getPosition(beneficiaryObject.getMovingLoc());
                q4Types.setSelection(spinnerPosition8);


                familyName.setVisibility(View.GONE);
                surnameField.setVisibility(View.GONE);
                middleField.setVisibility(View.GONE);
                firstField.setVisibility(View.GONE);
                firstName.setVisibility(View.GONE);
                middleName.setVisibility(View.GONE);
                relationshipTypeSpinner.setVisibility(View.GONE);
                genderTypeSpinner.setVisibility(View.GONE);
                maritalTypeSpinner.setVisibility(View.GONE);
                DoBView.setVisibility(View.GONE);

                 relationshipField.setVisibility(View.GONE);
                 genderField.setVisibility(View.GONE);
                 maritalStatus.setVisibility(View.GONE);
                 dob.setVisibility(View.GONE);
                 dobField.setVisibility(View.GONE);

            } else {

                officialIDText.setText(beneficiaryObject.getOfficialID());
                phoneText.setText(beneficiaryObject.getPhoneNumber());

                ArrayAdapter myAdap = (ArrayAdapter) idTypeSpinner.getAdapter(); //cast to an ArrayAdapter
                int spinnerPosition = myAdap.getPosition(beneficiaryObject.getIdType());
                idTypeSpinner.setSelection(spinnerPosition);

                familyName.setText(beneficiaryObject.getFamilyName());
                firstName.setText(beneficiaryObject.getFirstName());
                middleName.setText(beneficiaryObject.getMiddleName());

                ArrayAdapter myAdap2 = (ArrayAdapter) relationshipTypeSpinner.getAdapter(); //cast to an ArrayAdapter
                int spinnerPosition2 = myAdap2.getPosition(beneficiaryObject.getRelationshipType());
                relationshipTypeSpinner.setSelection(spinnerPosition2);


                ArrayAdapter myAdap3 = (ArrayAdapter) genderTypeSpinner.getAdapter(); //cast to an ArrayAdapter
                int spinnerPosition3 = myAdap3.getPosition(beneficiaryObject.getGender());
                genderTypeSpinner.setSelection(spinnerPosition3);

                ArrayAdapter myAdap4 = (ArrayAdapter) maritalTypeSpinner.getAdapter(); //cast to an ArrayAdapter
                int spinnerPosition4 = myAdap4.getPosition(beneficiaryObject.getMaritalStatus());
                maritalTypeSpinner.setSelection(spinnerPosition4);
//
//                ArrayAdapter myAdap5 = (ArrayAdapter) q1Types.getAdapter(); //cast to an ArrayAdapter
//                int spinnerPosition5 = myAdap5.getPosition(beneficiaryObject.getAnswer1());
//                q1Types.setSelection(spinnerPosition5);
//
//                ArrayAdapter myAdap6 = (ArrayAdapter) q2Types.getAdapter(); //cast to an ArrayAdapter
//                int spinnerPosition6 = myAdap6.getPosition(beneficiaryObject.getAnswer2());
//                q2Types.setSelection(spinnerPosition6);
//
//                ArrayAdapter myAdap7 = (ArrayAdapter) q3Types.getAdapter(); //cast to an ArrayAdapter
//                int spinnerPosition7 = myAdap7.getPosition(beneficiaryObject.getAnswer3());
//                q3Types.setSelection(spinnerPosition7);

                ArrayAdapter myAdap8 = (ArrayAdapter) q4Types.getAdapter(); //cast to an ArrayAdapter
                int spinnerPosition8 = myAdap8.getPosition(beneficiaryObject.getMovingLoc());
                q4Types.setSelection(spinnerPosition8);


                DoBView.setText(beneficiaryObject.getDateOfBirth());


                Button dateOfBirthBtn = (Button) findViewById(R.id.button_dob);
                dateOfBirthBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SelectDateFragment newFragment = new SelectDateFragment();
                        newFragment.show(getFragmentManager(), "datePicker");
                    }
                });


                q3Types.setVisibility(View.GONE);


            }


            ArrayList<ChildObject> arr = beneficiaryObject.childrenList;

            childrenAdapter = new ChildrenAdapter(this, "EDIT",Constants.ROLE_KITS_ASSESSOR);
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

                    Intent intent = new Intent(getApplicationContext(), ChildViewActivity.class);
                    intent.setAction(Constants.ADD_CHILD);
                    startActivityForResult(intent, ADD_CHILD_REQUEST);

                }
            });

            Button editBeneficiary = (Button) findViewById(R.id.button_saveBeneficiary);
            editBeneficiary.setText("Edit Form");
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


                                                    EditText phoneText = (EditText) findViewById(R.id.editText_familyPhoneNumber);
                                                    Spinner idTypeSpinner = (Spinner) findViewById(R.id.spinner_idTypes);
                                                    EditText officialIDText = (EditText) findViewById(R.id.editText_idNumber);
                                                    EditText familyName = (EditText) findViewById(R.id.editText_familySurname);
                                                    EditText firstName = (EditText) findViewById(R.id.editText_firstName);
                                                    EditText middleName = (EditText) findViewById(R.id.editText_middleName);
                                                    Spinner relationshipTypeSpinner = (Spinner) findViewById(R.id.spinner_relationshipTypes);
                                                    Spinner genderTypeSpinner = (Spinner) findViewById(R.id.spinner_genderTypes);
                                                    Spinner maritalTypeSpinner = (Spinner) findViewById(R.id.spinner_maritalTypes);
                                                    TextView dobView = (TextView) findViewById(R.id.textView_DoB);

                                                    Spinner q1Types = (Spinner) findViewById(R.id.spinner_yesno);
                                                    Spinner q2Types = (Spinner) findViewById(R.id.spinner_yesno2);
                                                    Spinner q3Types = (Spinner) findViewById(R.id.spinner_pa);
                                                    Spinner q4Types = (Spinner) findViewById(R.id.spinner_yesno3);
                                                    EditText answer4 = (EditText) findViewById(R.id.editText_where);

                                                    beneficiaryObject.setIdType(idTypeSpinner.getSelectedItem().toString());
                                                    beneficiaryObject.setPhoneNumber(phoneText.getText().toString());
                                                    beneficiaryObject.setOfficialID(officialIDText.getText().toString());
                                                    beneficiaryObject.setComplete(false);
                                                    beneficiaryObject.setFamilyName(familyName.getText().toString());
                                                    beneficiaryObject.setFirstName(firstName.getText().toString());
                                                    beneficiaryObject.setMiddleName(middleName.getText().toString());
                                                    beneficiaryObject.setRelationshipType(relationshipTypeSpinner.getSelectedItem().toString());
                                                    beneficiaryObject.setMaritalStatus(maritalTypeSpinner.getSelectedItem().toString());
                                                    beneficiaryObject.setGender(genderTypeSpinner.getSelectedItem().toString());
                                                    beneficiaryObject.setDateOfBirth(dobView.getText().toString());
                                                    beneficiaryObject.setPartnerName(UserPrefs.getOrganisation(getApplicationContext()));
//                                                    beneficiaryObject.setAnswer1(q1Types.getSelectedItem().toString());
//                                                    beneficiaryObject.setAnswer2(q2Types.getSelectedItem().toString());
//                                                    if (beneficiaryObject.getIdType().equals("UNHCR"))
//                                                         beneficiaryObject.setAnswer3(q3Types.getSelectedItem().toString());

                                                    beneficiaryObject.setMovingLoc(q4Types.getSelectedItem().toString());

                                                    CouchBaseManager.getInstance(context).editBeneficiary(beneficiaryObject,"HouseHold Edited");


                                                } catch (Exception e) {
                                                    Log.e("ERROR1",e.getMessage());
                                                }

                                                Intent intent = new Intent();
                                                intent.putExtra("BENEFICIARY", beneficiaryObject);
                                                setResult(RESULT_OK, intent);
                                                finish();


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

            if (requestCode == 0) {
                if (resultCode == RESULT_OK) {
                    ChildObject childObject = (ChildObject) data.getSerializableExtra("CHILD");
                    this.childrenAdapter.addItemToList(childObject);
                }
            } else if (requestCode == 1) {
                if (resultCode == 1) {
                    ChildObject childObject = (ChildObject) data.getSerializableExtra("CHILD");
                    int pos = data.getIntExtra("position", 0);
                    this.childrenAdapter.editItemToList(childObject, pos);

                } else if (resultCode == 2) {
                    //   ChildObject childObject = data.getParcelableExtra("CHILD");
                    int pos = data.getIntExtra("position", 0);
                    this.childrenAdapter.removeItemFromList(pos);

                }
            } else if (requestCode == 2) {
                if (resultCode == RESULT_OK) {

                    PCodeObject pcodeObject = data.getParcelableExtra("PCODE");
                    beneficiaryObject.setPcode(pcodeObject);
                    //pcode = pcodeObject;

                    TextView pcodeSelect = (TextView) findViewById(R.id.textView_geolocationData);
                    pcodeSelect.setText(pcodeObject.getPcodeName());


                    TextView pcodeView = (TextView) findViewById(R.id.textView_PCodeNo);

                    pcodeView.setText(beneficiaryObject.getPcode().getPcodeID());

                    //TextView latText = (TextView) findViewById(R.id.editText_latitude);
                    // TextView longText = (TextView) findViewById(R.id.editText_longitude);

                    // latText.setText(pcodeObject.getPcodeLat());
                    // longText.setText(pcodeObject.getPcodeLong());
                }
            } else if (requestCode == 3) {
                if (resultCode == RESULT_OK) {

                    String barcodeVal = data.getStringExtra("BARCODE");
                    //TextView barcodeField = (TextView) findViewById(R.id.editText_barcodeNumber);
                    // barcodeField.setText(barcodeVal);
                }

            }


        }


        private AdapterView.OnItemClickListener onItemClickListener() {

            return new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent intent = new Intent(getApplicationContext(), ChildViewActivity.class);
                    ChildObject childObject = childrenAdapter.getList().get(i);
                    intent.setAction(Constants.EDIT_CHILD);
                    intent.putExtra("CHILD", childObject);
                    intent.putExtra("position", i);
                    startActivityForResult(intent, EDIT_CHILD_REQUEST);
                }
            };

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    // app icon in action bar clicked; goto parent activity.
                    setResult(RESULT_CANCELED, null);
                    this.finish();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }


        private boolean validationCheck() {
            boolean isValid = true;
            // TextView barcodeText = (TextView) findViewById(R.id.editText_barcodeNumber);
            EditText phoneText = (EditText) findViewById(R.id.editText_familyPhoneNumber);
            EditText officialIDText = (EditText) findViewById(R.id.editText_idNumber);
            // EditText idNotes = (EditText) findViewById(R.id.editText_idNotes);
            EditText familyName = (EditText) findViewById(R.id.editText_familySurname);
            EditText firstName = (EditText) findViewById(R.id.editText_firstName);
            EditText middleName = (EditText) findViewById(R.id.editText_middleName);
            //CheckBox idCheck = (CheckBox) findViewById(R.id.checkBox_idExists);
            //EditText reasonForEdit = (EditText) findViewById(R.id.editText_reasonforedit);


            TextView idField = (TextView) findViewById(R.id.textView_idNumField);
            TextView phoneField = (TextView) findViewById(R.id.textView_phoneField);
              TextView surnameField = (TextView) findViewById(R.id.textView_surnameField);
              TextView firstField = (TextView) findViewById(R.id.textView_firstField);
              TextView middleField = (TextView) findViewById(R.id.textView_middleField);

              Spinner idTypes = (Spinner) findViewById(R.id.spinner_idTypes);
              Spinner genderTypes = (Spinner) findViewById(R.id.spinner_genderTypes);
              Spinner maritalTypes = (Spinner) findViewById(R.id.spinner_maritalTypes);
             Spinner relTypes = (Spinner) findViewById(R.id.spinner_relationshipTypes);
            Spinner q1Types = (Spinner) findViewById(R.id.spinner_yesno);
            Spinner q2Types = (Spinner) findViewById(R.id.spinner_yesno2);
            Spinner q3Types = (Spinner) findViewById(R.id.spinner_pa);
            Spinner q4Types = (Spinner) findViewById(R.id.spinner_yesno3);
            EditText answer4 = (EditText) findViewById(R.id.editText_where);


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


            if (phoneText.getText().toString().trim().equals("")) {
                phoneField.setTextColor(getResources().getColor(R.color.red));
                isValid = false;
                Toast.makeText(EditBeneficiaryForm.this,
                        "Empty Fields",
                        Toast.LENGTH_LONG).show();
            } else {
                phoneField.setTextColor(getResources().getColor(R.color.black));
            }

        if(familyName.getVisibility() == View.VISIBLE && familyName.getText().toString().trim().equals(""))
        {
            surnameField.setTextColor(getResources().getColor(R.color.red));
            isValid = false;
            Toast.makeText(EditBeneficiaryForm.this,
                    "Empty Fields",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            surnameField.setTextColor(getResources().getColor(R.color.black));
        }

        if(firstName.getVisibility() == View.VISIBLE && firstName.getText().toString().trim().equals(""))
        {
            firstField.setTextColor(getResources().getColor(R.color.red));
            isValid = false;
            Toast.makeText(EditBeneficiaryForm.this,
                    "Empty Fields",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            firstField.setTextColor(getResources().getColor(R.color.black));
        }

        if(middleName.getVisibility() == View.VISIBLE && middleName.getText().toString().trim().equals(""))
        {
            middleField.setTextColor(getResources().getColor(R.color.red));
            isValid = false;
            Toast.makeText(EditBeneficiaryForm.this,
                    "Empty Fields",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            middleField.setTextColor(getResources().getColor(R.color.black));
        }
        if(idTypes.getVisibility() == View.VISIBLE && idTypes.getSelectedItem().equals("----"))
        {
            isValid = false;
            Toast.makeText(EditBeneficiaryForm.this,
                    "ID Type Blank",
                    Toast.LENGTH_LONG).show();
        }


        if(relTypes.getVisibility() == View.VISIBLE && relTypes.getSelectedItem().equals("----"))
        {
            isValid = false;
            Toast.makeText(EditBeneficiaryForm.this,
                    "Relationship Type Blank",
                    Toast.LENGTH_LONG).show();
        }
        if(genderTypes.getVisibility() == View.VISIBLE && genderTypes.getSelectedItem().equals("----"))
        {
            isValid = false;
            Toast.makeText(EditBeneficiaryForm.this,
                    "Gender Type Blank",
                    Toast.LENGTH_LONG).show();
        }
        if(maritalTypes.getVisibility() == View.VISIBLE && maritalTypes.getSelectedItem().equals("----"))
        {
            isValid = false;
            Toast.makeText(EditBeneficiaryForm.this,
                    "Marital status Blank",
                    Toast.LENGTH_LONG).show();
        }
//            if (q1Types.getSelectedItem().equals("----")) {
//                isValid = false;
//                Toast.makeText(EditBeneficiaryForm.this,
//                        "Question 1  Blank",
//                        Toast.LENGTH_LONG).show();
//            }
//            if (q2Types.getSelectedItem().equals("----")) {
//                isValid = false;
//                Toast.makeText(EditBeneficiaryForm.this,
//                        "Question 2  Blank",
//                        Toast.LENGTH_LONG).show();
//            }
            if(q4Types.getSelectedItem().equals("----"))
            {
                isValid = false;
                Toast.makeText(EditBeneficiaryForm.this,
                        "Question 2 Type Blank.",
                        Toast.LENGTH_LONG).show();
            }
            else if (q4Types.getSelectedItem().equals("YES") && answer4.getText().toString().isEmpty())
            {
                isValid = false;
                Toast.makeText(EditBeneficiaryForm.this,
                        "Please type in the new location.",
                        Toast.LENGTH_LONG).show();
            }

                if (q3Types.getVisibility() == View.VISIBLE && q3Types.getSelectedItem().equals("----")) {
                    isValid = false;
                    Toast.makeText(EditBeneficiaryForm.this,
                            "Question 3 Blank",
                            Toast.LENGTH_LONG).show();
                }


            if (childrenAdapter.getList().size() == 0) {
                isValid = false;
                Toast.makeText(EditBeneficiaryForm.this,
                        "Please Add Children",
                        Toast.LENGTH_LONG).show();

            }

            return isValid;

        }


        public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                final Calendar calendar = Calendar.getInstance();
                if (yy == 0) {
                    yy = calendar.get(Calendar.YEAR);
                    mm = calendar.get(Calendar.MONTH);
                    dd = calendar.get(Calendar.DAY_OF_MONTH);
                }
                return new DatePickerDialog(getActivity(), this, yy, mm, dd);
            }

            public void onDateSet(DatePicker view, int yy, int mm, int dd) {
                updateDisplay(yy, mm, dd);
            }
        }

        public void updateDisplay(int year,
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
        }



}
