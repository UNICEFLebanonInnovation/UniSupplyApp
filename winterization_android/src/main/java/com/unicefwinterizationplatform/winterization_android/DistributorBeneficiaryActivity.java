package com.unicefwinterizationplatform.winterization_android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Tarek on 9/30/2014.
 */


public class DistributorBeneficiaryActivity extends HeaderActivity {

    final int EDIT_CHILD_REQUEST = 1;
    private ChildrenAdapter childrenAdapter;
    BeneficiaryObject beneficiaryObject;
    ListView childList;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitlistview);

        beneficiaryObject = getIntent().getParcelableExtra("BENEFICIARY");

        TextView idNumber = (TextView) findViewById(R.id.textView_idNumber);
        TextView phoneNumber = (TextView) findViewById(R.id.textView_familyPhoneNumber);
        TextView fullName  = (TextView) findViewById(R.id.textView_familySurname);
        TextView completed  = (TextView) findViewById(R.id.textView_completed);

        TextView siteName = (TextView) findViewById(R.id.textView_siteName);
        TextView pcodeNum = (TextView) findViewById(R.id.textView_PCode);

        idNumber.setText(beneficiaryObject.getOfficialID());
        phoneNumber.setText(beneficiaryObject.getPhoneNumber());
        fullName.setText(beneficiaryObject.getFirstName() + " " + beneficiaryObject.getMiddleName()+ " "+beneficiaryObject.getFamilyName());
        if(beneficiaryObject.isComplete())
        {
            completed.setText("The Distribution has been Completed on "+ beneficiaryObject.getCompletionDate());
            completed.setTextColor(getResources().getColor(R.color.Qi));
        }
        else
        {
            completed.setText("The Distribution has Not Yet Been Completed.");
            completed.setTextColor(getResources().getColor(R.color.red));
        }

        siteName.setText(beneficiaryObject.getPcode().getPcodeName());
        pcodeNum.setText(beneficiaryObject.getPcode().getPcodeID());
        Button isCorrectButton = (Button) findViewById(R.id.button_checked);
        isCorrectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout  checkLayout = (LinearLayout) findViewById(R.id.layout_checkButtons);

                LinearLayout mainLayout = (LinearLayout) findViewById(R.id.layout_kitList);

                mainLayout.removeView(checkLayout);

                LinearLayout kitsLayout = (LinearLayout) findViewById(R.id.layout_kits);
                kitsLayout.setVisibility(View.VISIBLE);
                Button completeButton = (Button) findViewById(R.id.button_complete);
                completeButton.setVisibility(View.VISIBLE);
                Button notCompleteButton = (Button) findViewById(R.id.button_notComplete);
                notCompleteButton.setVisibility(View.VISIBLE);


            }
        });

        Button cancel = (Button) findViewById(R.id.button_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent(context, ReaderActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                backIntent.setAction("lookup_barcode");
                //backIntent.putExtra("PCODE",beneficiaryObject.pcodeDist);
                startActivity(backIntent);
                //finish();
            }
        });

        ArrayList<ChildObject> arr = new ArrayList<ChildObject>();

        arr = beneficiaryObject.childrenList;

        childrenAdapter = new ChildrenAdapter(this,"NONE",Constants.ROLE_KITS_DISTRIBUTOR);
        childrenAdapter.addList(arr);// new ListAdapter(this, android.R.layout.simple_list_item_1, arr);
        childList = (ListView) this.findViewById(R.id.kit_list);
        childList.setAdapter(childrenAdapter);
        // setListViewHeightBasedOnChildren(childList);

        childList.setOnItemClickListener(onItemClickListener());

        Button completeBtn = (Button) findViewById(R.id.button_complete);
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateCompletion(true);
            }
        });

        Button notCompleteBtn = (Button) findViewById(R.id.button_notComplete);
        notCompleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateCompletion(false);
            }
        });
    }


    private AdapterView.OnItemClickListener onItemClickListener() {

        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.d("POSITION",i +"");
                Intent intent = new Intent(getApplicationContext(), EditKitView.class);
                ChildObject childObject = childrenAdapter.getList().get(i);
                intent.putExtra("CHILD", childObject);
                intent.putExtra("position", i);
                startActivityForResult(intent, EDIT_CHILD_REQUEST);
            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == 1) {
            if (resultCode == 1) {
                ChildObject childObject = (ChildObject)data.getSerializableExtra("CHILD");
                int pos = data.getIntExtra("position", 0);
                this.childrenAdapter.editItemToList(childObject, pos);

            } else if (resultCode == 2) {
                ChildObject childObject = (ChildObject)data.getSerializableExtra("CHILD");
                int pos = data.getIntExtra("position", 0);
                this.childrenAdapter.editItemToList(childObject, pos);

            }
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                Intent backIntent = new Intent(context, ReaderActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                backIntent.setAction("lookup_barcode");
                //backIntent.putExtra("PCODE",beneficiaryObject.pcodeDist);
                startActivity(backIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void validateCompletion(final boolean validate)
    {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final TextView barcodeVal = (TextView) promptsView
                .findViewById(R.id.textView_barcodeNumber);
        if (validate) {
            barcodeVal.setText("Are you sure you want to Complete?");
        }
        else
            barcodeVal.setText("Are you sure you want to finish distribution without completing?");
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                if (validate){
                                    childrenAdapter.setForCompletion();
                            }

                            Handler handler = new Handler();
                            handler.postDelayed(new

                            Runnable() {
                                public void run () {

                                    beneficiaryObject.childrenList = childrenAdapter.getList();
                                    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    Calendar calendar = GregorianCalendar.getInstance();
                                    String currentTimeString = dateFormatter.format(calendar.getTime());

                                    if (validate) {
                                        beneficiaryObject.setComplete(true);
                                        beneficiaryObject.setCompletionDate(currentTimeString);

                                        CouchBaseManager.getInstance(context).editBeneficiary(beneficiaryObject, "Household Completed");
                                    } else {
                                        int i =0;
                                        for(ChildObject childObject : beneficiaryObject.childrenList)
                                        {
                                           if (childObject.getStatus().equals("COMPLETED")){
                                               i++;
                                           }
                                        }

                                        if (i == beneficiaryObject.childrenList.size()){
                                            beneficiaryObject.setComplete(true);

                                        }
                                        else {
                                            beneficiaryObject.setComplete(false);
                                        }
                                        CouchBaseManager.getInstance(context).editBeneficiary(beneficiaryObject, "Household Partially Completed");

                                    }
                                    Intent intent = new Intent(context, DistributionCompletionActivity.class);
                                    // backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    // backIntent.setAction("searchBarcodes");
                                    intent.putExtra("BENEFICIARY", beneficiaryObject);
                                    startActivity(intent);
                                }
                            }

                            ,1000);
                        }
    })
                .setNegativeButton("No",
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backIntent = new Intent(context, ReaderActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        backIntent.setAction("lookup_barcode");
        startActivity(backIntent);
    }
}