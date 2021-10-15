package com.unicefwinterizationplatform.winterization_android;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tarek on 9/30/2014.
 */
public class AddSuccessActivity extends HeaderActivity {

   BeneficiaryObject beneficiaryObject;
    String assistanceType;
   Context context = this;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);


        beneficiaryObject = getIntent().getParcelableExtra("BENEFICIARY");
        assistanceType = getIntent().getStringExtra("ASSISTANCE");
       // UserPrefs.incrementDeviceAssessment(context, beneficiaryObject.getPcode().getPcodeName());


       // TextView pcodeTxt = (TextView) findViewById(R.id.textView_PCode);
      //  pcodeTxt.setText( beneficiaryObject.getPcode().getPcodeName());

      //  TextView deviceIncrementTxt = (TextView) findViewById(R.id.textView_deviceIncrement);
      //  deviceIncrementTxt.setText( UserPrefs.getDeviceIncrement(context, beneficiaryObject.getPcode().getPcodeName()) + "");


        Button addNewBenny = (Button) findViewById(R.id.button_addNewBeneficiary);
        addNewBenny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.alert_newassessment, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final TextView pcodeText = (TextView) promptsView
                        .findViewById(R.id.textView_Pcode);

                pcodeText.setText("Current p-code: "+beneficiaryObject.getPcode().getPcodeName());
                Button newPcodeBtn = (Button) promptsView.findViewById(R.id.Button_newPcode);


                newPcodeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent backIntent = new Intent(context, PCodeSelectionActivity.class);
                        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        backIntent.putExtra("ASSISTANCE", assistanceType);
                        backIntent.putExtra("LOC_TYPE", beneficiaryObject.getPcode().getLocationType());

                        startActivity(backIntent);

                    }
                });

                Button samePcodeBtn = (Button) promptsView.findViewById(R.id.Button_samePcode);
                samePcodeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (assistanceType.equals("cash")) {
                            Intent backIntent = new Intent(context, ConsentActivity.class);
                            backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            backIntent.putExtra("PCODE", beneficiaryObject.getPcode());
                            BeneficiaryObject beneficiary = new BeneficiaryObject();
                            beneficiary.setPcode(beneficiaryObject.getPcode());
                            backIntent.putExtra("BENEFICIARY", beneficiary);
                            backIntent.putExtra("ASSISTANCE", assistanceType);
                            backIntent.setAction("searchAssessment");

                            // BeneficiaryObject newBeneficiary = new BeneficiaryObject();
                            //   newBeneficiary.setPcode(beneficiaryObject.getPcode());

                            //   backIntent.putExtra("BENEFICIARY", newBeneficiary);
                            startActivity(backIntent);
                        }
                        else if (assistanceType.equals("kits")){
                            Intent backIntent = new Intent(context, ConsentActivity.class);
                            backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            backIntent.putExtra("PCODE", beneficiaryObject.getPcode());
                            BeneficiaryObject beneficiary = new BeneficiaryObject();
                            beneficiary.setPcode(beneficiaryObject.getPcode());
                            backIntent.putExtra("BENEFICIARY", beneficiary);
                            backIntent.putExtra("ASSISTANCE", assistanceType);

                            // BeneficiaryObject newBeneficiary = new BeneficiaryObject();
                            //   newBeneficiary.setPcode(beneficiaryObject.getPcode());

                            //   backIntent.putExtra("BENEFICIARY", newBeneficiary);
                            startActivity(backIntent);
                        }
                    }
                });


                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                alertDialogBuilder.show();




            }
        });

        Button editNewBenny = (Button) findViewById(R.id.button_editCurrentBeneficiary);
        editNewBenny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, BeneficiaryDetailsActivity.class);
              //  backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("DOC", beneficiaryObject.mainID);
                intent.putExtra("ASSISTANCE",assistanceType);
              //  intent.setAction("editNewBeneficiary");
                startActivity(intent);
            }
        });

     /*   Button changeSite = (Button) findViewById(R.id.button_selectNewSite);
        changeSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent(context, PCodeSelectionActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(backIntent);
            }
        });*/

        Button homeButton = (Button) findViewById(R.id.button_goToHomeScreen);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent(context, TabActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                backIntent.setAction(beneficiaryObject.getAssistanceType());
                startActivity(backIntent);
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
//                Intent backIntent = new Intent(this, ConsentActivity.class);
//                backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                BeneficiaryObject newBeneficiary = new BeneficiaryObject();
//                newBeneficiary.setPcode(beneficiaryObject.getPcode());
//
//                backIntent.putExtra("BENEFICIARY", newBeneficiary);
//                backIntent.putExtra("ASSISTANCE", assistanceType);
//
//                startActivity(backIntent);
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

//


    }


    }
