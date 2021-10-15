package com.unicefwinterizationplatform.winterization_android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by Tarek on 10/22/2014.
 */
public class VoucherAssessmentActivity extends HeaderActivity {

    final int ADD_CHILD_REQUEST = 0;
    final int EDIT_CHILD_REQUEST = 1;
    private ChildrenAdapter childrenAdapter;
    BeneficiaryObject beneficiaryObject;
    ListView childList;
    VoucherAssessmentActivity app = this;
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucherassessment);

        Log.d("ERROR IS HERE", "check error here");
        beneficiaryObject = getIntent().getParcelableExtra("BENEFICIARY");

        TextView idNumber = (TextView) findViewById(R.id.textView_idNumber);
        TextView phoneNumber = (TextView) findViewById(R.id.textView_familyPhoneNumber);
        TextView familyName  = (TextView) findViewById(R.id.textView_familySurname);
        TextView fatherText  = (TextView) findViewById(R.id.textView_fatherName);
        TextView motherText  = (TextView) findViewById(R.id.textView_motherName);
        TextView idTypeText  = (TextView) findViewById(R.id.textView_idType);

        TextView numberKidsText  = (TextView) findViewById(R.id.textView_numberOfKids);



        idNumber.setText(beneficiaryObject.getOfficialID());
        idTypeText.setText(beneficiaryObject.getIdType());
        phoneNumber.setText(beneficiaryObject.getPhoneNumber());
        familyName.setText(beneficiaryObject.getFamilyName());
        fatherText.setText(beneficiaryObject.getFirstName());
        motherText.setText(beneficiaryObject.getMiddleName());
        numberKidsText.setText(String.valueOf(beneficiaryObject.getChildrenList().size()));

        Button isCorrectButton = (Button) findViewById(R.id.button_checked);
        isCorrectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LinearLayout  checkLayout = (LinearLayout) findViewById(R.id.layout_checkButtons);

                LinearLayout mainLayout = (LinearLayout) findViewById(R.id.layout_childList);

                mainLayout.removeView(checkLayout);

                LinearLayout kitsLayout = (LinearLayout) findViewById(R.id.layout_children);
                kitsLayout.setVisibility(View.VISIBLE);
                Button completeButton = (Button) findViewById(R.id.button_complete);
                completeButton.setVisibility(View.VISIBLE);
            }
        });

        Button cancel = (Button) findViewById(R.id.button_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ArrayList<ChildObject> arr = new ArrayList<ChildObject>();

        arr = beneficiaryObject.childrenList;

        childrenAdapter = new ChildrenAdapter(this,"NONE",this,Constants.ROLE_VOUCHERS_REDEEMER);
        childrenAdapter.addList(arr);// new ListAdapter(this, android.R.layout.simple_list_item_1, arr);
        childList = (ListView) this.findViewById(R.id.voucher_list);
        childList.setAdapter(childrenAdapter);
        // setListViewHeightBasedOnChildren(childList);

        childList.setOnItemClickListener(onItemClickListener());


        Button addChildBtn = (Button) findViewById(R.id.add_child_btn);
        addChildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent =   new Intent(getApplicationContext(), VoucherDetailsActivity.class);
                intent.setAction(Constants.ADD_CHILD);
                startActivityForResult(intent, ADD_CHILD_REQUEST);

            }
        });

        Button completeBtn = (Button) findViewById(R.id.button_complete);
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   childrenAdapter.setForCompletion();
                        beneficiaryObject.childrenList = childrenAdapter.getList();
                        CouchBaseManager.getInstance(context).editBeneficiary(beneficiaryObject,"Vouchers Distributed");
                Intent backIntent = new Intent(context, MainMenuActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(backIntent);
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
            //    int pos = data.getIntExtra("position", 0);
            //    this.childrenAdapter.removeItemFromList(pos);
                // setListViewHeightBasedOnChildren(childList);
                ChildObject childObject = (ChildObject) data.getSerializableExtra("CHILD");
                int pos = data.getIntExtra("position", 0);
                this.childrenAdapter.editItemToList(childObject, pos);

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

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final TextView barcodeVal = (TextView) promptsView
                        .findViewById(R.id.textView_barcodeNumber);

                barcodeVal.setText(R.string.go_back_vouchers);
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


}
