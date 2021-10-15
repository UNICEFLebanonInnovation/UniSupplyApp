package com.unicefwinterizationplatform.winterization_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by Tarek on 2/4/2015.
 */
public class RedeemVoucherActivity extends HeaderActivity {

    final int ADD_CHILD_REQUEST = 0;
    final int EDIT_CHILD_REQUEST = 1;
    private ChildrenAdapter childrenAdapter;
    BeneficiaryObject beneficiaryObject;
    String voucherVal;
    ListView childList;

    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeemvoucher);

        Log.d("ERROR IS HERE", "check error here");
        beneficiaryObject = getIntent().getParcelableExtra("BENEFICIARY");
        voucherVal = getIntent().getStringExtra("VOUCHER_CODE");


        TextView childName = (TextView) findViewById(R.id.textView_childsName);
        TextView voucherCode = (TextView) findViewById(R.id.textView_voucherCode);
        TextView ageText = (TextView) findViewById(R.id.textView_age);
        TextView genderText = (TextView) findViewById(R.id.textView_gender);
        TextView statusText = (TextView) findViewById(R.id.textView_status);

        TextView idNumber = (TextView) findViewById(R.id.textView_idNumber);
        TextView phoneNumber = (TextView) findViewById(R.id.textView_familyPhoneNumber);
        TextView familyName  = (TextView) findViewById(R.id.textView_familySurname);
        TextView fatherText  = (TextView) findViewById(R.id.textView_fatherName);
        TextView motherText  = (TextView) findViewById(R.id.textView_motherName);



        for (ChildObject child : beneficiaryObject.childrenList)
        {
            if (child.getVoucherCode().equals(voucherVal))
            {
                childName.setText(child.getName());
                voucherCode.setText(child.getVoucherCode());
                ageText.setText(child.getAge());
                genderText.setText(child.getGender());
                statusText.setText(child.getStatus());
                break;
            }
        }


        idNumber.setText(beneficiaryObject.getOfficialID());
        phoneNumber.setText(beneficiaryObject.getPhoneNumber());
        familyName.setText(beneficiaryObject.getFamilyName());
        fatherText.setText(beneficiaryObject.getFirstName());
        motherText.setText(beneficiaryObject.getMiddleName());


        Button completeBtn = (Button) findViewById(R.id.button_redeem);
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   childrenAdapter.setForCompletion();
                //beneficiaryObject.childrenList = childrenAdapter.getList();
               // CouchBaseManager.getInstance(context).editBeneficiary(beneficiaryObject,"Household Edited");
                finish();
            }
        });
    }


    private AdapterView.OnItemClickListener onItemClickListener(){

        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent =   new Intent(getApplicationContext(), VoucherDetailsActivity.class);
                ChildObject childObject = childrenAdapter.getList().get(i);

                intent.setAction(Constants.EDIT_CHILD);
                intent.putExtra("CHILD",childObject);
                intent.putExtra("position",i);
                startActivityForResult(intent, EDIT_CHILD_REQUEST);


            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                ChildObject childObject = (ChildObject) data.getSerializableExtra("CHILD");

                this.childrenAdapter.addItemToList(childObject);
                //  setListViewHeightBasedOnChildren(childList);

            }
        } else if (requestCode == 1) {
            if (resultCode == 1) {
                ChildObject childObject = (ChildObject) data.getSerializableExtra("CHILD");
                int pos = data.getIntExtra("position", 0);
                this.childrenAdapter.editItemToList(childObject, pos);
                // setListViewHeightBasedOnChildren(childList);

            } else if (resultCode == 2) {
                //   ChildObject childObject = data.getParcelableExtra("CHILD");
                int pos = data.getIntExtra("position", 0);
                this.childrenAdapter.removeItemFromList(pos);
                // setListViewHeightBasedOnChildren(childList);

            }
        } else   if (requestCode == 3) {
            if (resultCode == 3) {

                int pos = data.getIntExtra("position", 0);
                String voucherCode = data.getStringExtra("VOUCHER");
                this.childrenAdapter.setVoucherCodeInList(pos,voucherCode);
                //  setListViewHeightBasedOnChildren(childList);

            }
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
