package com.unicefwinterizationplatform.winterization_android;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by Tarek on 9/22/2014.
 */
public class DetailedBeneficiaryActivity extends HeaderActivity {
    private ChildrenAdapter childrenAdapter;
    BeneficiaryObject beneficiaryObject;
    ListView childList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailedbeneficiary);

        ActionBar actionBar = getActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

         beneficiaryObject = getIntent().getParcelableExtra("BENEFICIARY");
        // PCodeObject pcode = getIntent().getParcelableExtra("PCODE");

        TextView pcodeText = (TextView) findViewById(R.id.textView_PCode);

        TextView latitudeView =  (TextView) findViewById(R.id.textView_latitude);
        TextView longitudeView =  (TextView) findViewById(R.id.textView_longitude);
        TextView altitudeView =  (TextView) findViewById(R.id.textView_altitude);

        TextView barcodeText = (TextView) findViewById(R.id.textView_barcodeNumber);
        TextView phoneText = (TextView) findViewById(R.id.textView_familyPhoneNumber);
        CheckBox shawish = (CheckBox) findViewById(R.id.checkBox_shawish);


        TextView officialIDText = (TextView) findViewById(R.id.textView_idNumber);
        CheckBox idExists = (CheckBox) findViewById(R.id.checkBox_idExists);
        TextView idNotes = (TextView) findViewById(R.id.textView_idNotes);
        TextView idTypeText = (TextView) findViewById(R.id.textView_IdType);

        TextView familyName = (TextView) findViewById(R.id.textView_familySurname);
        TextView firstName = (TextView) findViewById(R.id.textView_firstName);
        TextView middleName = (TextView) findViewById(R.id.textView_middleName);
        TextView relationshipType = (TextView) findViewById(R.id.textView_relationshipType);

        pcodeText.setText(beneficiaryObject.getPcode().getPcodeName());
        barcodeText.setText(beneficiaryObject.getBarcodeNum());
        idTypeText.setText(beneficiaryObject.getIdType());
        phoneText.setText(beneficiaryObject.getPhoneNumber());
        shawish.setChecked(beneficiaryObject.isShawish());
        officialIDText.setText(beneficiaryObject.getOfficialID());
        idExists.setChecked(beneficiaryObject.isIdDoesExist());
        idNotes.setText(beneficiaryObject.getIdNotes());
        familyName.setText(beneficiaryObject.getFamilyName());
        firstName.setText(beneficiaryObject.getFirstName());
        middleName.setText(beneficiaryObject.getMiddleName());
        relationshipType.setText(beneficiaryObject.getRelationshipType());

//        latitudeView.setText(beneficiaryObject.getLatitude());
//        longitudeView.setText(beneficiaryObject.getLongitude());
//        altitudeView.setText(beneficiaryObject.getAltitude());

        ArrayList<ChildObject> arr = beneficiaryObject.childrenList;

        childrenAdapter = new ChildrenAdapter(this,"IDLE",Constants.ROLE_KITS_ASSESSOR);
        childrenAdapter.addList(arr);// new ListAdapter(this, android.R.layout.simple_list_item_1, arr);
        childList = (ListView) this.findViewById(R.id.child_list);

        childList.setAdapter(childrenAdapter);


        Button editButton = (Button) findViewById(R.id.button_editResident);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddBeneficiaryActivity.class);
                intent.setAction("EDIT");
                intent.putExtra("BENEFICIARY",beneficiaryObject);
                startActivity(intent);
            }
        });



                LinearLayout layoutNotes = (LinearLayout) findViewById(R.id.layout_idNotes);

                if(idExists.isChecked())
                {
                    layoutNotes.setVisibility(View.VISIBLE);
                }
                else{
                    layoutNotes.setVisibility(View.GONE);
                }

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
    }



