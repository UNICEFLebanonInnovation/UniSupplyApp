package com.unicefwinterizationplatform.winterization_android;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by Tarek on 9/19/2014.
 */
public class AddBeneficiaryActivity extends HeaderActivity {

    final int ADD_CHILD_REQUEST = 0;
    final int EDIT_CHILD_REQUEST = 1;

    final int FAMILY_REQUEST = 3;


    final int EDIT_PCODE = 2;

    final int EDIT_BENEFICIARY = 1;

    Context context = this;
    AddBeneficiaryActivity app = this;
    private String selection="";
    private String assistanceType;
    int yy;
    int mm;
    int dd;

    private ChildrenAdapter childrenAdapter;
    private FamilyAdapter familyAdapter;
    //private  childrenAdapter;

    ListView childList;
    ListView familyList;

    ArrayList<String> cadasterList = new ArrayList<String>();
    ArrayList<String> districtList = new ArrayList<String>();
    JSONObject obj=null;

    int dis =0;

    int cad =0;


    BeneficiaryObject beneficiaryObject;
    TextView DoBView;


    // PCodeObject pcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbeneficiary);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //ChildrenAdapter childrenAdapter;

        selection = getIntent().getStringExtra("SELECT");
        assistanceType = getIntent().getStringExtra("ASSISTANCE");
        beneficiaryObject = getIntent().getParcelableExtra("BENEFICIARY");
       // final LinearLayout unhcrLayout = (LinearLayout) findViewById(R.id.layout_unhcrCheck);
        final LinearLayout fullLayout = (LinearLayout) findViewById(R.id.layout_fullLayout);




        fullLayout.setVisibility(View.VISIBLE);

        if (assistanceType.equals("cash")) {
            if (selection.equals("none")) {


            } else if (selection.equals("registered") || selection.equals("recorded")) {
                Spinner idTypes = (Spinner) findViewById(R.id.spinner_idTypes);
                // ArrayAdapter myAdap = (ArrayAdapter) idTypes.setAda //cast to an ArrayAdapter
                final ArrayAdapter<CharSequence> myAdap = new ArrayAdapter<CharSequence>(this, R.layout.support_simple_spinner_dropdown_item);
                myAdap.add("UNHCR-" + selection);
                idTypes.setAdapter(myAdap);
                int spinnerPosition = myAdap.getPosition("UNHCR-" + selection);
                idTypes.setSelection(spinnerPosition);
                idTypes.setEnabled(false);

                EditText idNumber = (EditText) findViewById(R.id.editText_idNumber);
                idNumber.setText(beneficiaryObject.getOfficialID());
                idNumber.setEnabled(false);


                LinearLayout idValLayout = (LinearLayout) findViewById(R.id.layout_idvalidation);
                idValLayout.setVisibility(View.GONE);

                LinearLayout firstLayout = (LinearLayout) findViewById(R.id.layout_firstname);
                firstLayout.setVisibility(View.GONE);

                LinearLayout middleName = (LinearLayout) findViewById(R.id.layout_middlename);
                middleName.setVisibility(View.GONE);

                LinearLayout lastName = (LinearLayout) findViewById(R.id.layout_surname);
                lastName.setVisibility(View.GONE);

                LinearLayout relationship = (LinearLayout) findViewById(R.id.layout_relationship);
                relationship.setVisibility(View.GONE);

                LinearLayout maritalStatus = (LinearLayout) findViewById(R.id.layout_maritalstatus);
                maritalStatus.setVisibility(View.GONE);

                LinearLayout gender = (LinearLayout) findViewById(R.id.layout_gender);
                gender.setVisibility(View.GONE);

                LinearLayout dob = (LinearLayout) findViewById(R.id.layout_dob);
                dob.setVisibility(View.GONE);

                LinearLayout pa = (LinearLayout) findViewById(R.id.layout_pa);
                pa.setVisibility(View.VISIBLE);

                LinearLayout over18 = (LinearLayout) findViewById(R.id.layout_over18);
                over18.setVisibility(View.VISIBLE);

                TextView disclaimer = (TextView) findViewById(R.id.textView_disclaimer);
                disclaimer.setVisibility(View.GONE);

                LinearLayout membersLayout = (LinearLayout) findViewById(R.id.layout_familymembers);
                membersLayout.setVisibility(View.GONE);

                LinearLayout mothersLayout = (LinearLayout) findViewById(R.id.layout_mothername);
                mothersLayout.setVisibility(View.GONE);

                LinearLayout familyLayout = (LinearLayout) findViewById(R.id.layout_familylist);
                familyLayout.setVisibility(View.GONE);


            }
        }
        else if (assistanceType.equals("kits")){

            LinearLayout idTypeLayout = (LinearLayout) findViewById(R.id.layout_idType);
            idTypeLayout.setVisibility(View.GONE);

            LinearLayout idLayout = (LinearLayout) findViewById(R.id.layout_id);
            idLayout.setVisibility(View.GONE);

            LinearLayout idValLayout = (LinearLayout) findViewById(R.id.layout_idvalidation);
            idValLayout.setVisibility(View.GONE);

            LinearLayout familyLayout = (LinearLayout) findViewById(R.id.layout_familylist);
            familyLayout.setVisibility(View.GONE);

            TextView disclaimer = (TextView) findViewById(R.id.textView_disclaimer);
            disclaimer.setVisibility(View.GONE);

        }

        Spinner movingSpinner = (Spinner) findViewById(R.id.spinner_moving);

        movingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                final LinearLayout cadastral = (LinearLayout) findViewById(R.id.layout_cadastral);
                final LinearLayout district = (LinearLayout) findViewById(R.id.layout_district);


                final Spinner cadastralSpinner = (Spinner) findViewById(R.id.editText_cadastral);
               final Spinner districtSpinner = (Spinner) findViewById(R.id.editText_district);

                String answer = (String) adapterView.getItemAtPosition(i);

                if (answer.equals("Lebanon")) {
                    cadastral.setVisibility(View.VISIBLE);
                    district.setVisibility(View.VISIBLE);

                    try {
                        obj = new JSONObject(loadJSONFromAsset());
                        Iterator<?> keys = obj.keys();
                        Log.e("TEST",obj.toString());
                        districtList.add("----");
                        while (keys.hasNext()){
                            String key = (String)keys.next();
                            Log.e("KEY",key);
                            districtList.add(key);
                        }

                        Collections.sort(districtList, String.CASE_INSENSITIVE_ORDER);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ArrayAdapter<CharSequence> disadapter = new ArrayAdapter<CharSequence>(getApplicationContext(),R.layout.spinner_text);
                    disadapter.addAll(districtList);
                    disadapter.setDropDownViewResource(R.layout.spinner_text);
                    districtSpinner.setAdapter(disadapter);
                    districtSpinner.setPrompt("Districts");
                    if (dis == 0 && getIntent().getAction() != null && getIntent().getAction().equals("EDIT"))
                    {
                        int spinnerPosition = disadapter.getPosition(beneficiaryObject.getNewDistrict());
                        districtSpinner.setSelection(spinnerPosition);
                        dis++;
                    }

                    districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            try {

                                cadasterList = (ArrayList<String>) JsonHelper.toList(obj.getJSONArray(String.valueOf(adapterView.getSelectedItem())));
                                // Collections.sort(cadasterList, new MapComparator("cerd_id"));
                                ArrayAdapter<CharSequence> cadasterAdapter = new ArrayAdapter<CharSequence>(getApplicationContext(), R.layout.spinner_text);
                                cadasterAdapter.setDropDownViewResource(R.layout.spinner_text);

                                for (String val : cadasterList) {
                                    cadasterAdapter.add(val);
                                }

                                cadastralSpinner.setAdapter(cadasterAdapter);

                                if (cad == 0 && getIntent().getAction() != null && getIntent().getAction().equals("EDIT"))
                                {
                                    int spinnerPosition = cadasterAdapter.getPosition(beneficiaryObject.getNewCadasteral());
                                    cadastralSpinner.setSelection(spinnerPosition);
                                    cad++;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });


                    ArrayAdapter<CharSequence> cadastralAdapter = new ArrayAdapter<CharSequence>(getApplicationContext(),R.layout.spinner_text);
                    cadastralAdapter.setDropDownViewResource(R.layout.spinner_text);

                    cadastralSpinner.setAdapter(cadastralAdapter);
                    cadastralSpinner.setPrompt("Cadastrals");

                    cadastralSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            ///selectedSchool = schoolList.get(i);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });



                } else {
                    cadastral.setVisibility(View.GONE);
                    district.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        Button dateOfBirthBtn = (Button) findViewById(R.id.button_dob);
        dateOfBirthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectDateFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "datePicker");

            }
        });

        DoBView = (TextView)findViewById(R.id.textView_DoB);


        TextView pcodeTextView = (TextView) findViewById(R.id.textView_PCode);
        pcodeTextView.setText(beneficiaryObject.getPcode().getPcodeName());
        pcodeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), PCodeSelectionActivity.class);
                intent.setAction("REPLACE_PCODE");
                intent.putExtra("LOC_TYPE", beneficiaryObject.getPcode().getLocationType());
                startActivityForResult(intent, EDIT_PCODE);
            }
        });

        TextView pcodeView =  (TextView) findViewById(R.id.textView_PCodeNo);

        pcodeView.setText(beneficiaryObject.getPcode().getPcodeID());

        TextView districtView =  (TextView) findViewById(R.id.textView_district);

        districtView.setText(beneficiaryObject.getPcode().getDistrict());

        TextView cadastralView =  (TextView) findViewById(R.id.textView_cadastral);

        cadastralView.setText(beneficiaryObject.getPcode().getCadastral());

       /* TextView latitudeView =  (TextView) findViewById(R.id.textView_latitude);
        TextView longitudeView =  (TextView) findViewById(R.id.textView_longitude);
        TextView altitudeView =  (TextView) findViewById(R.id.textView_altitude);

        latitudeView.setText(beneficiaryObject.getLatitude());
        longitudeView.setText(beneficiaryObject.getLongitude());
        altitudeView.setText(beneficiaryObject.getAltitude());*/



        ArrayList<ChildObject> arr = new ArrayList<ChildObject>();

        childrenAdapter = new ChildrenAdapter(this,"ADD", Constants.ROLE_KITS_ASSESSOR);
        childrenAdapter.addList(arr);// new ListAdapter(this, android.R.layout.simple_list_item_1, arr);
        childList = (ListView) this.findViewById(R.id.child_list);
        childList.setAdapter(childrenAdapter);
        // setListViewHeightBasedOnChildren(childList);

        childList.setOnItemClickListener(onItemClickListener());


        Button addChildBtn = (Button) findViewById(R.id.add_child_btn);
        addChildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getApplicationContext(), ChildViewActivity.class);
                intent.setAction(Constants.ADD_CHILD);
                startActivityForResult(intent, ADD_CHILD_REQUEST);

            }
        });


        ArrayList<FamilyObject> arr2 = new ArrayList<FamilyObject>();

        familyAdapter = new FamilyAdapter(this,"ADD");
        familyAdapter.addList(arr2);// new ListAdapter(this, android.R.layout.simple_list_item_1, arr);
        familyList = (ListView) this.findViewById(R.id.family_list);
        familyList.setAdapter(familyAdapter);
        // setListViewHeightBasedOnChildren(childList);

        familyList.setOnItemClickListener(onFamilyClickListener());


        Button addFamilyBtn = (Button) findViewById(R.id.add_family_btn);
        addFamilyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getApplicationContext(), FamilyViewActivity.class);
                intent.setAction(Constants.ADD_FAMILY);
                startActivityForResult(intent, FAMILY_REQUEST);

            }
        });


        Button addBenny = (Button) findViewById(R.id.button_addBeneficiary);
        TextView titleText = (TextView) findViewById(R.id.textView_titleText);

        if(getIntent().getAction()!= null && getIntent().getAction().equals("EDIT"))
        {
            addBenny.setText("Edit");
            titleText.setText("Edit Household");
        }



        addBenny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(validationCheck()) {
                    EditText idNumber = (EditText) findViewById(R.id.editText_idNumber);

                    Spinner idTypes = (Spinner) findViewById(R.id.spinner_idTypes);
                    EditText familyPhoneNumber = (EditText) findViewById(R.id.editText_familyPhoneNumber);
                    EditText familySurname = (EditText) findViewById(R.id.editText_familySurname);
                    EditText firstName = (EditText) findViewById(R.id.editText_firstName);
                    EditText middleName = (EditText) findViewById(R.id.editText_middleName);
                    EditText motherName = (EditText) findViewById(R.id.editText_motherName);
                    EditText familyCount = (EditText) findViewById(R.id.editText_familymembers);


                    Spinner over18Spinner = (Spinner) findViewById(R.id.spinner_over18);
                    Spinner principalApplicant = (Spinner) findViewById(R.id.spinner_pa);
                    Spinner relationshipType = (Spinner) findViewById(R.id.spinner_relationshipTypes);
                    Spinner genderType = (Spinner) findViewById(R.id.spinner_genderTypes);
                    Spinner maritalType = (Spinner) findViewById(R.id.spinner_maritalTypes);
                    EditText disabilities = (EditText) findViewById(R.id.editText_disabilities);
                    Spinner phoneOwner = (Spinner) findViewById(R.id.spinner_phoneTypes);


                    Spinner movingSpinner = (Spinner) findViewById(R.id.spinner_moving);
                    Spinner district = (Spinner) findViewById(R.id.editText_district);
                    Spinner cadastral = (Spinner) findViewById(R.id.editText_cadastral);


                    TextView dobView = (TextView) findViewById(R.id.textView_DoB);


                    beneficiaryObject.setOfficialID(idNumber.getText().toString());
                    beneficiaryObject.setIdType(idTypes.getSelectedItem().toString());
                    beneficiaryObject.setPhoneNumber(familyPhoneNumber.getText().toString());
                    beneficiaryObject.setFamilyName(familySurname.getText().toString());
                    beneficiaryObject.setFirstName(firstName.getText().toString());
                    beneficiaryObject.setMiddleName(middleName.getText().toString());
                    beneficiaryObject.setMothersName(motherName.getText().toString());
                    beneficiaryObject.setFamilyCount(familyCount.getText().toString());

                    beneficiaryObject.setRelationshipType(relationshipType.getSelectedItem().toString());
                    beneficiaryObject.setComplete(false);//
                    beneficiaryObject.setPcodeDist(new PCodeObject("", "", "", "", "", "", "", ""));
                    beneficiaryObject.setCriticality("0");
                    beneficiaryObject.setCompletionDate("");
                    //beneficiaryObject.setReasonForEdit("");
                    beneficiaryObject.setGender(genderType.getSelectedItem().toString());
                    beneficiaryObject.setMaritalStatus(maritalType.getSelectedItem().toString());
                    beneficiaryObject.setDateOfBirth(dobView.getText().toString());
                    //beneficiaryObject.setPhoneNumber()

                    beneficiaryObject.setPartnerName(UserPrefs.getOrganisation(getApplicationContext()));
                    beneficiaryObject.setMainID(beneficiaryObject.getOfficialID() + ":" + beneficiaryObject.getPartnerName());
                    beneficiaryObject.setAssistanceType(assistanceType);
                    beneficiaryObject.setOver18(over18Spinner.getSelectedItem().toString());
                    beneficiaryObject.setPrincipleApplicant(principalApplicant.getSelectedItem().toString());


                    beneficiaryObject.setMovingLoc(movingSpinner.getSelectedItem().toString());
                    if (beneficiaryObject.getMovingLoc().equals("Lebanon")) {
                        beneficiaryObject.setNewCadasteral(cadastral.getSelectedItem().toString());
                        beneficiaryObject.setNewDistrict(district.getSelectedItem().toString());
                    }
                    else
                    {
                        beneficiaryObject.setNewCadasteral("----");
                        beneficiaryObject.setNewDistrict("----");
                    }


                    beneficiaryObject.setDisabilities(disabilities.getText().toString());
                    beneficiaryObject.setPhoneOwner(phoneOwner.getSelectedItem().toString());


                    //beneficiaryObject.setPcodeDist(new PCodeObject("","","",""));


                    beneficiaryObject.setChildrenList(childrenAdapter.getList());

                    beneficiaryObject.setFamilyList(familyAdapter.getList());
                    ArrayList<ChildObject> childList = childrenAdapter.getList();


                    if (getIntent().getAction() != null && getIntent().getAction().equals("EDIT"))
                    {
                        CouchBaseManager.getInstance(context).editBeneficiary(beneficiaryObject, "edit");
                        Intent intent = new Intent();
                        intent.putExtra("BENEFICIARY", beneficiaryObject);

                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else {


                        if (assistanceType.equals("kits")) {

                            Intent intent = new Intent(getApplicationContext(), ReaderActivity.class);
                            intent.putExtra("BENEFICIARY", beneficiaryObject);
                            intent.putExtra("ASSISTANCE",assistanceType);
                            intent.setAction("set_barcode");
                            startActivity(intent);

                        } else if (assistanceType.equals("cash")) {
                            if (selection.equals("none")) {
                                String username = UserPrefs.getUsername(getApplicationContext());

                                if (!CouchBaseManager.getInstance(getApplicationContext()).checkDocumentFromID(beneficiaryObject.getOfficialID(),username)) {
                                    CouchBaseManager.getInstance(context).addBeneficiary(beneficiaryObject);
                                    Intent intent = new Intent(context, SurveyActivity.class);
                                    intent.putExtra("ASSISTANCE", assistanceType);
                                    intent.putExtra("INC", 1);
                                    intent.putExtra("BENEFICIARY", beneficiaryObject);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"ID already Taken",Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                CouchBaseManager.getInstance(context).addBeneficiary(beneficiaryObject);
                                Intent intent = new Intent(context, SurveyActivity.class);
                                intent.putExtra("ASSISTANCE", assistanceType);
                                intent.putExtra("INC", 1);
                                intent.putExtra("BENEFICIARY", beneficiaryObject);
                                startActivity(intent);
                            }
                        }
                    }
             /*       if(idTypes.getSelectedItem().equals("UNHCR")){
                        Intent intent = new Intent(context, SurveyActivity.class);
                        intent.putExtra("INC", 1);
                        intent.putExtra("BENEFICIARY",beneficiaryObject);
                        startActivity(intent);
                    }
                    else {*/
//                        Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
//                        intent.putExtra("BENEFICIARY", beneficiaryObject);
//
//                        //intent.setAction("addBarcode");
//                        //startActivityForResult(intent, 5);
//                        startActivity(intent);
                        //  }
                //    }
                }


            }
        });

        if (getIntent().getAction() != null && getIntent().getAction().equals("EDIT"))
        {
            setFields();
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

    private AdapterView.OnItemClickListener onFamilyClickListener(){

        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent =   new Intent(getApplicationContext(), FamilyViewActivity.class);
                FamilyObject familyObject = familyAdapter.getList().get(i);

                intent.setAction(Constants.EDIT_FAMILY);
                intent.putExtra("FAMILY",familyObject);
                intent.putExtra("position",i);
                startActivityForResult(intent, FAMILY_REQUEST);

            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0)
        {
            if (resultCode == RESULT_OK) {
                ChildObject childObject = (ChildObject)data.getSerializableExtra("CHILD");

                this.childrenAdapter.addItemToList(childObject);
                //  setListViewHeightBasedOnChildren(childList);

            }
        }
        else    if (requestCode == 1)
        {
            if (resultCode == 1) {
                ChildObject childObject = (ChildObject)data.getSerializableExtra("CHILD");
                int pos = data.getIntExtra("position",0);
                this.childrenAdapter.editItemToList(childObject,pos);
                // setListViewHeightBasedOnChildren(childList);
            }
            else   if (resultCode == 2) {
                //   ChildObject childObject = data.getParcelableExtra("CHILD");
                int pos = data.getIntExtra("position",0);
                this.childrenAdapter.removeItemFromList(pos);
                // setListViewHeightBasedOnChildren(childList);
            }
        }

        else    if (requestCode == EDIT_PCODE)
        {
            if (data.getParcelableExtra("PCODE")!= null) {
                PCodeObject pcode = data.getParcelableExtra("PCODE");
                beneficiaryObject.setPcode(pcode);
                TextView pcodeTextView = (TextView) findViewById(R.id.textView_PCode);
                pcodeTextView.setText(pcode.getPcodeName());

                TextView pcodeView =  (TextView) findViewById(R.id.textView_PCodeNo);

                pcodeView.setText(beneficiaryObject.getPcode().getPcodeID());
            //    TextView latitudeView = (TextView) findViewById(R.id.textView_latitude);
             //   TextView longitudeView = (TextView) findViewById(R.id.textView_longitude);

            //    latitudeView.setText(pcode.getPcodeLat());
             //   longitudeView.setText(pcode.getPcodeLong());
            }

        }

        else if (requestCode == FAMILY_REQUEST)
        {


            if (resultCode == -1){

            }

            else if (resultCode == 0){
                if(data.getSerializableExtra("FAMILY") != null) {
                    FamilyObject familyObject = (FamilyObject) data.getSerializableExtra("FAMILY");

                    this.familyAdapter.addItemToList(familyObject);
                }
            }
            else if (resultCode == 1){
                FamilyObject familyObject = (FamilyObject)data.getSerializableExtra("FAMILY");
                int pos = data.getIntExtra("position",0);
                this.familyAdapter.editItemToList(familyObject,pos);
        }
            else if(resultCode == 2)
            {
                int pos = data.getIntExtra("position",0);
                this.familyAdapter.removeItemFromList(pos);
            }


        }
    }



    private boolean validationCheck()
    {
        boolean isValid = true;
        EditText idNumber = (EditText) findViewById(R.id.editText_idNumber);
        EditText idValidation = (EditText) findViewById(R.id.editText_idValidation);



        EditText familyPhoneNumber = (EditText) findViewById(R.id.editText_familyPhoneNumber);
        EditText validatedPhoneNumber = (EditText) findViewById(R.id.editText_PhoneNumberValidation);
        EditText familySurname = (EditText) findViewById(R.id.editText_familySurname);
        EditText firstName = (EditText) findViewById(R.id.editText_firstName);
        EditText middleName = (EditText) findViewById(R.id.editText_middleName);
        EditText mothersName = (EditText) findViewById(R.id.editText_motherName);
        EditText familyCount = (EditText) findViewById(R.id.editText_familymembers);


        TextView motherText = (TextView) findViewById(R.id.textView_motherField);
        TextView familyText = (TextView) findViewById(R.id.textView_familymembers);


        TextView idField = (TextView) findViewById(R.id.textView_idNumField);
        TextView idValidationField = (TextView) findViewById(R.id.textView_idNumValidation);

        TextView phoneField = (TextView) findViewById(R.id.textView_phoneField);
        TextView phoneField2 = (TextView) findViewById(R.id.textView_phoneFieldVal);

        TextView surnameField = (TextView) findViewById(R.id.textView_surnameField);
        TextView firstField = (TextView) findViewById(R.id.textView_firstField);
        TextView middleField = (TextView) findViewById(R.id.textView_middleField);
        TextView relField = (TextView) findViewById(R.id.textView_relationshipField);
        TextView genderField = (TextView) findViewById(R.id.textView_genderField);
        TextView maritalField = (TextView) findViewById(R.id.textView_maritalField);


        Spinner idTypes = (Spinner) findViewById(R.id.spinner_idTypes);
        Spinner genderTypes = (Spinner) findViewById(R.id.spinner_genderTypes);
        Spinner maritalTypes = (Spinner) findViewById(R.id.spinner_maritalTypes);
        Spinner relTypes = (Spinner) findViewById(R.id.spinner_relationshipTypes);
      //  Spinner q1Types = (Spinner) findViewById(R.id.spinner_yesno);
      //  Spinner q2Types = (Spinner) findViewById(R.id.spinner_yesno2);
        Spinner movingSpinner = (Spinner) findViewById(R.id.spinner_moving);
        Spinner cadSpinner = (Spinner) findViewById(R.id.editText_cadastral);
        Spinner distSpinner = (Spinner) findViewById(R.id.editText_district);

        EditText answer4 = (EditText) findViewById(R.id.editText_where);

        LinearLayout firstLayout = (LinearLayout) findViewById(R.id.layout_firstname);

        LinearLayout middleLayout = (LinearLayout) findViewById(R.id.layout_middlename);

        LinearLayout lastLayout = (LinearLayout) findViewById(R.id.layout_surname);

        LinearLayout relationshipLayout = (LinearLayout) findViewById(R.id.layout_relationship);

        LinearLayout maritalStatusLayout = (LinearLayout) findViewById(R.id.layout_maritalstatus);

        LinearLayout genderLayout = (LinearLayout) findViewById(R.id.layout_gender);

        LinearLayout dobLayout = (LinearLayout) findViewById(R.id.layout_dob);

        LinearLayout idTypeLayout = (LinearLayout) findViewById(R.id.layout_idType);

        LinearLayout idLayout = (LinearLayout) findViewById(R.id.layout_id);

        LinearLayout idValidationLayout = (LinearLayout) findViewById(R.id.layout_idvalidation);

        LinearLayout mothersLayout = (LinearLayout) findViewById(R.id.layout_mothername);

        LinearLayout familyCountLayout = (LinearLayout) findViewById(R.id.layout_familymembers);

        LinearLayout familyListLayout = (LinearLayout) findViewById(R.id.layout_familylist);

        LinearLayout cadastralLay = (LinearLayout) findViewById(R.id.layout_cadastral);

        LinearLayout districtLay = (LinearLayout) findViewById(R.id.layout_district);



        //FOR RECORDED AND REGISTERED

        Spinner paSpinner = (Spinner) findViewById(R.id.spinner_pa);
        Spinner over18Spinner = (Spinner) findViewById(R.id.spinner_over18);


        //if (!idTypes.getSelectedItem().equals("No ID")) {
            if (idLayout.getVisibility() == View.VISIBLE &&  idNumber.getText().toString().trim().equals("")) {
                idField.setTextColor(getResources().getColor(R.color.red));
                isValid = false;
                Toast.makeText(AddBeneficiaryActivity.this,
                        "Empty Fields",
                        Toast.LENGTH_LONG).show();
            } else {
                idField.setTextColor(getResources().getColor(R.color.black));
            }
       // }



        Pattern p = Pattern.compile("^(03|30|31|32|33|34|35|36|37|38|39|70|71|76|78|79|81 )\\d{6}$");
        Matcher m = p.matcher(familyPhoneNumber.getText());
        //if (familyPhoneNumber.length() != 8 && )
        if (!m.matches())
        {
            phoneField.setTextColor(getResources().getColor(R.color.red));
            isValid = false;
            Toast.makeText(AddBeneficiaryActivity.this,
                    "Phone Number does not match mobile format",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            phoneField.setTextColor(getResources().getColor(R.color.black));
        }


        if(familyPhoneNumber.getVisibility() == View.VISIBLE && familyPhoneNumber.getText().toString().trim().equals(""))
        {
            phoneField.setTextColor(getResources().getColor(R.color.red));
            isValid = false;
            Toast.makeText(AddBeneficiaryActivity.this,
                    "Empty Fields",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            phoneField.setTextColor(getResources().getColor(R.color.black));
        }





        if(familyPhoneNumber.getVisibility() == View.VISIBLE && familyPhoneNumber.getText().toString().trim().equals(""))
        {
            phoneField.setTextColor(getResources().getColor(R.color.red));
            isValid = false;
            Toast.makeText(AddBeneficiaryActivity.this,
                    "Empty Fields",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            phoneField.setTextColor(getResources().getColor(R.color.black));
        }


        if(lastLayout.getVisibility() == View.VISIBLE && familySurname.getText().toString().trim().equals(""))
        {
            surnameField.setTextColor(getResources().getColor(R.color.red));
            isValid = false;
            Toast.makeText(AddBeneficiaryActivity.this,
                    "Empty Fields",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            surnameField.setTextColor(getResources().getColor(R.color.black));
        }

        if(firstLayout.getVisibility() == View.VISIBLE && firstName.getText().toString().trim().equals(""))
        {
            firstField.setTextColor(getResources().getColor(R.color.red));
            isValid = false;
            Toast.makeText(AddBeneficiaryActivity.this,
                    "Empty Fields",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            firstField.setTextColor(getResources().getColor(R.color.black));
        }

        if(middleLayout.getVisibility() == View.VISIBLE && middleName.getText().toString().trim().equals(""))
        {
            middleField.setTextColor(getResources().getColor(R.color.red));
            isValid = false;
            Toast.makeText(AddBeneficiaryActivity.this,
                    "Empty Fields",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            middleField.setTextColor(getResources().getColor(R.color.black));
        }

        if(mothersLayout.getVisibility() == View.VISIBLE && mothersName.getText().toString().trim().equals(""))
        {
            motherText.setTextColor(getResources().getColor(R.color.red));
            isValid = false;
            Toast.makeText(AddBeneficiaryActivity.this,
                    "Empty Fields",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            motherText.setTextColor(getResources().getColor(R.color.black));
        }

        if(familyCountLayout.getVisibility() == View.VISIBLE && familyCount.getText().toString().trim().equals(""))
        {
            familyText.setTextColor(getResources().getColor(R.color.red));
            isValid = false;
            Toast.makeText(AddBeneficiaryActivity.this,
                    "Empty Fields",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            familyText.setTextColor(getResources().getColor(R.color.black));
        }

        if(idTypeLayout.getVisibility() == View.VISIBLE  && idTypes.getSelectedItem().equals("----"))
        {
            isValid = false;
            Toast.makeText(AddBeneficiaryActivity.this,
                    "ID Type Blank",
                    Toast.LENGTH_LONG).show();
        }


        if(relationshipLayout.getVisibility() == View.VISIBLE && relTypes.getSelectedItem().equals("----"))
        {
            relField.setTextColor(getResources().getColor(R.color.red));

            isValid = false;
            Toast.makeText(AddBeneficiaryActivity.this,
                    "Relationship Type Blank",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            relField.setTextColor(getResources().getColor(R.color.black));

        }


        if(genderLayout.getVisibility() == View.VISIBLE && genderTypes.getSelectedItem().equals("----"))
        {
            genderField.setTextColor(getResources().getColor(R.color.red));
            isValid = false;
            Toast.makeText(AddBeneficiaryActivity.this,
                    "Gender Type Blank",
                    Toast.LENGTH_LONG).show();
        }
        else {
            genderField.setTextColor(getResources().getColor(R.color.black));
        }
        if(maritalStatusLayout.getVisibility() == View.VISIBLE && maritalTypes.getSelectedItem().equals("----"))
        {
            maritalField.setTextColor(getResources().getColor(R.color.red));

            isValid = false;
            Toast.makeText(AddBeneficiaryActivity.this,
                    "Marital Status Blank",
                    Toast.LENGTH_LONG).show();
        }
        else {
            maritalField.setTextColor(getResources().getColor(R.color.black));

        }
//        if(q1Types.getSelectedItem().equals("----"))
//        {
//            isValid = false;
//            Toast.makeText(AddBeneficiaryActivity.this,
//                    "Question 1 Type Blank",
//                    Toast.LENGTH_LONG).show();
//        }
//        if(q2Types.getSelectedItem().equals("----"))
//        {
//            isValid = false;
//            Toast.makeText(AddBeneficiaryActivity.this,
//                    "Question 2 Type Blank.",
//                    Toast.LENGTH_LONG).show();
//        }

        if(movingSpinner.getSelectedItem().equals("----"))
        {
            isValid = false;
            Toast.makeText(AddBeneficiaryActivity.this,
                    "Blank Fields.",
                    Toast.LENGTH_LONG).show();
        }

        if(districtLay.getVisibility() == View.VISIBLE && distSpinner.getSelectedItem().equals("----") )
        {
            isValid = false;
            Toast.makeText(AddBeneficiaryActivity.this,
                    "Blank Fields.",
                    Toast.LENGTH_LONG).show();
        }

        if(cadastralLay.getVisibility() == View.VISIBLE && cadSpinner.getSelectedItem().equals("----") )
        {
            isValid = false;
            Toast.makeText(AddBeneficiaryActivity.this,
                    "Blank Fields.",
                    Toast.LENGTH_LONG).show();
        }


//    else if (q4Types.getSelectedItem().equals("YES") && answer4.getText().toString().isEmpty())
//        {
//            isValid = false;
//            Toast.makeText(AddBeneficiaryActivity.this,
//                    "Please type in the new location.",
//                    Toast.LENGTH_LONG).show();
//        }

        if(childrenAdapter.getList().size()==0)
        {
            isValid = false;
            Toast.makeText(AddBeneficiaryActivity.this,
                    "Please Add Children",
                    Toast.LENGTH_LONG).show();

        }

        if(familyListLayout.getVisibility() == View.VISIBLE && familyAdapter.getList().size()==0)
        {
            isValid = false;
            Toast.makeText(AddBeneficiaryActivity.this,
                    "Please Add Family Members",
                    Toast.LENGTH_LONG).show();

        }




        if (!familyPhoneNumber.getText().toString().equals(validatedPhoneNumber.getText().toString()))
    {
        //phoneField.setTextColor(getResources().getColor(R.color.red));
        phoneField2.setTextColor(getResources().getColor(R.color.red));
        isValid = false;
        Toast.makeText(AddBeneficiaryActivity.this,
                "Phone number does not match",
                Toast.LENGTH_LONG).show();
    }
    else
    {
        // phoneField.setTextColor(getResources().getColor(R.color.black));
        phoneField2.setTextColor(getResources().getColor(R.color.black));

    }

        if (idValidationLayout.getVisibility() == View.VISIBLE && !idNumber.getText().toString().equals(idValidation.getText().toString()))
        {
            //phoneField.setTextColor(getResources().getColor(R.color.red));
            idValidationField.setTextColor(getResources().getColor(R.color.red));
            isValid = false;
            Toast.makeText(AddBeneficiaryActivity.this,
                    "ID number does not match",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            // phoneField.setTextColor(getResources().getColor(R.color.black));
            idValidationField.setTextColor(getResources().getColor(R.color.black));

        }

        return isValid;

    }




/*
    private class mylocationlistener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.d("LOCATION Lat", location.getLatitude() + "");
                Log.d("LOCATION Long", location.getLongitude() + "");
                Log.d("LOCATION Alt", location.getAltitude() + "");
                Log.d("LOCATION Accuracy", location.getAccuracy() + "");
                Toast.makeText(AddBeneficiaryActivity.this,
                       "Latitude:"+ location.getLatitude() + "\nLongitude:" + location.getLongitude() +"\nAltitude:"+ location.getAltitude() +"\nAccuracy:"+ location.getAccuracy(),
                       Toast.LENGTH_LONG).show();
            }
        }
        @Override
        public void onProviderDisabled(String provider) {
            Log.d("Provider Disabled", provider);

        }
        @Override
        public void onProviderEnabled(String provider) {
            Log.d("Provider Enabled", provider);

        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        //    Toast.makeText(AddResidentActivity.this,
           //         provider + " " + status,
          //          Toast.LENGTH_LONG).show();

            Log.d("Status Changed", provider +" " + status );
      //      Toast.makeText(AddBeneficiaryActivity.this,
      //              status + " " + provider,
      //              Toast.LENGTH_LONG).show();

        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final TextView barcodeVal = (TextView) promptsView
                        .findViewById(R.id.textView_barcodeNumber);

                barcodeVal.setText("Do you want reselect site?");
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        app.finish();

                                    }
                                })
                        .setNegativeButton("NO",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setFields() {
        beneficiaryObject = getIntent().getParcelableExtra("BENEFICIARY");

        //TextView barcodeText = (TextView) findViewById(R.id.editText_barcode);
        EditText idNumber = (EditText) findViewById(R.id.editText_idNumber);
        EditText idNumberVerification = (EditText) findViewById(R.id.editText_idValidation);

        idNumber.setEnabled(false);
        idNumberVerification.setEnabled(false);

        EditText familyPhoneNumber = (EditText) findViewById(R.id.editText_familyPhoneNumber);
        EditText validatedPhoneNumber = (EditText) findViewById(R.id.editText_PhoneNumberValidation);
        EditText familySurname = (EditText) findViewById(R.id.editText_familySurname);
        EditText firstName = (EditText) findViewById(R.id.editText_firstName);
        EditText middleName = (EditText) findViewById(R.id.editText_middleName);
        EditText disabilitiesText = (EditText) findViewById(R.id.editText_disabilities);
        Spinner phoneOwnerText = (Spinner) findViewById(R.id.spinner_phoneTypes);
        Spinner idTypes = (Spinner) findViewById(R.id.spinner_idTypes);
        Spinner genderTypes = (Spinner) findViewById(R.id.spinner_genderTypes);
        Spinner maritalTypes = (Spinner) findViewById(R.id.spinner_maritalTypes);
        Spinner relTypes = (Spinner) findViewById(R.id.spinner_relationshipTypes);
        Spinner movingSpinner = (Spinner) findViewById(R.id.spinner_moving);
        Spinner district = (Spinner) findViewById(R.id.editText_district);
        Spinner cadastral = (Spinner) findViewById(R.id.editText_cadastral);
        Spinner paSpinner = (Spinner) findViewById(R.id.spinner_pa);
        TextView dobText = (TextView)findViewById(R.id.textView_DoB);

        Spinner over18Spinner = (Spinner) findViewById(R.id.spinner_over18);


        ArrayAdapter myAdap;
        int spinnerPosition;

        idNumber.setText(beneficiaryObject.getOfficialID());
        idNumberVerification.setText(beneficiaryObject.getOfficialID());
        familyPhoneNumber.setText(beneficiaryObject.getPhoneNumber());
        validatedPhoneNumber.setText(beneficiaryObject.getPhoneNumber());
        disabilitiesText.setText(beneficiaryObject.getDisabilities());

        myAdap = (ArrayAdapter) phoneOwnerText.getAdapter();
        spinnerPosition = myAdap.getPosition(beneficiaryObject.getPhoneOwner());
        phoneOwnerText.setSelection(spinnerPosition);

        ArrayList<ChildObject> arr = beneficiaryObject.childrenList;

       // childrenAdapter = new ChildrenAdapter(this, "EDIT",Constants.ROLE_KITS_ASSESSOR);
        childrenAdapter.addList(arr);// new ListAdapter(this, android.R.layout.simple_list_item_1, arr);
        childList.setAdapter(childrenAdapter);



        if (assistanceType.equals("cash")) {

            if (selection.equals("none")) {

                familySurname.setText(beneficiaryObject.getFamilyName());
                middleName.setText(beneficiaryObject.getMiddleName());
                firstName.setText(beneficiaryObject.getFirstName());





                myAdap = (ArrayAdapter) idTypes.getAdapter();
                spinnerPosition = myAdap.getPosition(beneficiaryObject.getIdType());
                idTypes.setSelection(spinnerPosition);

                myAdap = (ArrayAdapter) genderTypes.getAdapter();
                spinnerPosition = myAdap.getPosition(beneficiaryObject.getGender());
                genderTypes.setSelection(spinnerPosition);

                myAdap = (ArrayAdapter) maritalTypes.getAdapter();
                spinnerPosition = myAdap.getPosition(beneficiaryObject.getMaritalStatus());
                maritalTypes.setSelection(spinnerPosition);

                myAdap = (ArrayAdapter) relTypes.getAdapter();
                spinnerPosition = myAdap.getPosition(beneficiaryObject.getRelationshipType());
                relTypes.setSelection(spinnerPosition);

                myAdap = (ArrayAdapter) movingSpinner.getAdapter();
                spinnerPosition = myAdap.getPosition(beneficiaryObject.getMovingLoc());
                movingSpinner.setSelection(spinnerPosition);

//                if (movingSpinner.getSelectedItem().equals("Lebanon"))
//                {
//                    myAdap = (ArrayAdapter) district.getAdapter();
//                    spinnerPosition = myAdap.getPosition(beneficiaryObject.getNewDistrict());
//                    district.setSelection(spinnerPosition);
//
//
//                    myAdap = (ArrayAdapter) cadastral.getAdapter();
//                    spinnerPosition = myAdap.getPosition(beneficiaryObject.getNewCadasteral());
//                    cadastral.setSelection(spinnerPosition);
//
//                }



                dobText.setText(beneficiaryObject.getDateOfBirth());

                ArrayList<FamilyObject> arr2 = beneficiaryObject.familyList;

                // childrenAdapter = new ChildrenAdapter(this, "EDIT",Constants.ROLE_KITS_ASSESSOR);
                familyAdapter.addList(arr2);// new ListAdapter(this, android.R.layout.simple_list_item_1, arr);
                familyList.setAdapter(familyAdapter);

                EditText mothersName = (EditText) findViewById(R.id.editText_motherName);
                mothersName.setText(beneficiaryObject.getMothersName());

                EditText familyCount = (EditText) findViewById(R.id.editText_familymembers);
                familyCount.setText(beneficiaryObject.getFamilyCount());





            }
            else if (selection.equals("registered") || selection.equals("recorded")) {

                myAdap = (ArrayAdapter) over18Spinner.getAdapter();
                spinnerPosition = myAdap.getPosition(beneficiaryObject.getOver18());
                over18Spinner.setSelection(spinnerPosition);

                myAdap = (ArrayAdapter) paSpinner.getAdapter();
                spinnerPosition = myAdap.getPosition(beneficiaryObject.getPrincipleApplicant());
                paSpinner.setSelection(spinnerPosition);

                myAdap = (ArrayAdapter) movingSpinner.getAdapter();
                spinnerPosition = myAdap.getPosition(beneficiaryObject.getMovingLoc());
                movingSpinner.setSelection(spinnerPosition);

//                if (movingSpinner.getSelectedItem().equals("Lebanon"))
//                {
//                    myAdap = (ArrayAdapter) district.getAdapter();
//                    spinnerPosition = myAdap.getPosition(beneficiaryObject.getNewDistrict());
//                    district.setSelection(spinnerPosition);
//
//
//                    myAdap = (ArrayAdapter) cadastral.getAdapter();
//                    spinnerPosition = myAdap.getPosition(beneficiaryObject.getNewCadasteral());
//                    cadastral.setSelection(spinnerPosition);
//
//                }



            }

        }
        else if(assistanceType.equals("kits")) {

            familySurname.setText(beneficiaryObject.getFamilyName());
            middleName.setText(beneficiaryObject.getMiddleName());
            firstName.setText(beneficiaryObject.getFirstName());



            myAdap = (ArrayAdapter) idTypes.getAdapter();
            spinnerPosition = myAdap.getPosition(beneficiaryObject.getIdType());
            idTypes.setSelection(spinnerPosition);

            myAdap = (ArrayAdapter) genderTypes.getAdapter();
            spinnerPosition = myAdap.getPosition(beneficiaryObject.getGender());
            genderTypes.setSelection(spinnerPosition);

            myAdap = (ArrayAdapter) maritalTypes.getAdapter();
            spinnerPosition = myAdap.getPosition(beneficiaryObject.getMaritalStatus());
            maritalTypes.setSelection(spinnerPosition);

            myAdap = (ArrayAdapter) relTypes.getAdapter();
            spinnerPosition = myAdap.getPosition(beneficiaryObject.getRelationshipType());
            relTypes.setSelection(spinnerPosition);

            myAdap = (ArrayAdapter) movingSpinner.getAdapter();
            spinnerPosition = myAdap.getPosition(beneficiaryObject.getMovingLoc());
            movingSpinner.setSelection(spinnerPosition);

//            if (movingSpinner.getSelectedItem().equals("Lebanon"))
//            {
//                myAdap = (ArrayAdapter) district.getAdapter();
//                spinnerPosition = myAdap.getPosition(beneficiaryObject.getNewDistrict());
//                district.setSelection(spinnerPosition);
//
//
//                myAdap = (ArrayAdapter) cadastral.getAdapter();
//                spinnerPosition = myAdap.getPosition(beneficiaryObject.getNewCadasteral());
//                cadastral.setSelection(spinnerPosition);
//
//            }


            EditText familyCount = (EditText) findViewById(R.id.editText_familymembers);
            familyCount.setText(beneficiaryObject.getFamilyCount());

            dobText.setText(beneficiaryObject.getDateOfBirth());


        }


    }


    public  class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


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
    }

    public String loadJSONFromAsset() throws JSONException {
        String json = null;
        try {
            InputStream is = getApplicationContext().getAssets().open("districts.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    public  void updateDisplay(int year,
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

