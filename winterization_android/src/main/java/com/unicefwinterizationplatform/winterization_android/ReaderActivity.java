package com.unicefwinterizationplatform.winterization_android;

/**
 * Created by Tarek on 10/18/2014.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.couchbase.lite.Document;
import com.couchbase.lite.QueryRow;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import junit.framework.Test;

import org.w3c.dom.Comment;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ReaderActivity extends  HeaderActivity  implements AsyncResponse {

    Context context = this;
    Activity thisActivity = this;
    String selection = "none";
    String assistanceType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_barcode);
        assistanceType = getIntent().getStringExtra("ASSISTANCE");
        //callReader();

        if (getIntent().getAction().equals("set_barcode")|| getIntent().getAction().equals("lookup_barcode")) {
            Button registered = (Button) findViewById(R.id.button_registered);
            registered.setVisibility(View.GONE);
            Button recorded = (Button) findViewById(R.id.button_recorded);
            recorded.setVisibility(View.GONE);
            Button noBtn = (Button) findViewById(R.id.button_noUNHCR);
            noBtn.setVisibility(View.GONE);
            TextView question = (TextView) findViewById(R.id.question_text);
            question.setVisibility(View.GONE);

            callReader();

        }
        else{
            Button registered = (Button) findViewById(R.id.button_registered);
            registered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    selection = "registered";
                    callReader();

                }
            });

            Button recorded = (Button) findViewById(R.id.button_recorded);
            recorded.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    selection = "recorded";

                    callReader();

                }
            });


            Button noBtn = (Button) findViewById(R.id.button_noUNHCR);
            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, AddBeneficiaryActivity.class);
                    PCodeObject pCodeObject = getIntent().getParcelableExtra("PCODE");
                    BeneficiaryObject beneficiaryObject = new BeneficiaryObject();
                    beneficiaryObject.setPcode(pCodeObject);
                    intent.putExtra("SELECT", "none");
                    intent.putExtra("ASSISTANCE",assistanceType);
                    intent.putExtra("BENEFICIARY", beneficiaryObject);

                    startActivity(intent);

                }
            });
        }

    }
    public void scanBarcode(View view) {
        new IntentIntegrator(this).initiateScan();
    }

    public void scanBarcodeCustomLayout(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);

     //   integrator.initiateScan();
         //setCaptureLayout(R.layout.custom_capture_layout);
       // integrator.setLegacyCaptureLayout(R.layout.custom_legacy_capture_layout);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setOrientationLocked(false);
       // integrator.autoWide();

        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //Log.v("RESULT",result.getContents());
        if(result != null) {
            if(result.getContents() == null) {
                //Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                callOtherOptionsAlert();
            } else {

                callAlertBarcode(result.getContents());
            }
        } else {
// This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    public void callReader()
    {
       /* IntentIntegrator integrator = new IntentIntegrator(this);

        integrator.setCaptureActivity(AnyOrientationCaptureActivity.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan something");
        integrator.setOrientationLocked(false);
        integrator.initiateScan();*/

        new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
    }

    public void callAlertBarcode(final String barcodeValue) {


       boolean docExists = CouchBaseManager.getInstance(getApplicationContext()).checkDocumentFromID(barcodeValue);

     /// SPECIAL CASE/////
        if (docExists == false && getIntent().getAction().equals("lookup_barcode"))
        {
            docExists = CouchBaseManager.getInstance(getApplicationContext()).checkDocumentFromID(barcodeValue,"user5");
        }

        if (docExists == false && getIntent().getAction().equals("lookup_barcode"))
        {
            docExists = CouchBaseManager.getInstance(getApplicationContext()).checkDocumentFromCode(barcodeValue);
        }
    ////////////////////

        if (docExists == false) {
            if (getIntent().getAction().equals("searchAssessment")) {
                checkbarcode(barcodeValue,selection);
            } else if (getIntent().getAction().equals("set_barcode")) {
                addbarcode(barcodeValue);
            }
            else if(getIntent().getAction().equals("lookup_barcode")){
                Toast.makeText(getApplicationContext(), "Document does not Exist.", Toast.LENGTH_SHORT).show();
                callReader();

            }

        }

        else{

            if(getIntent().getAction().equals("lookup_barcode"))
            {
                Document doc = CouchBaseManager.getInstance(getApplicationContext()).getDocumentFromID(barcodeValue+":"+UserPrefs.getUsername(getApplicationContext()));
              //// SPECIAL CHECK//////
                if(doc.getCurrentRevisionId() == null)
                {
                    doc = CouchBaseManager.getInstance(getApplicationContext()).getDocumentFromID(barcodeValue+":user5");
                }
                if(doc.getCurrentRevisionId() == null)
                {
                    doc = CouchBaseManager.getInstance(getApplicationContext()).getDocumentFromID(barcodeValue);
                }


                lookupBarcode(barcodeValue, doc);

            }
            else {
                Toast.makeText(getApplicationContext(), "Same Document Exists", Toast.LENGTH_SHORT).show();
            }
        }

        /**Legacy Code Will Remove Later*/
       /* if (barcodeValue.length() == 10 && barcodeValue.matches("[0-9]+"))
        {

            if (getIntent().getAction().equals("searchBarcodes")) {



                BeneficiaryAsyncCaller beneficiaryAsyncCaller = new BeneficiaryAsyncCaller();
                beneficiaryAsyncCaller.delegate = this;
                beneficiaryAsyncCaller.execute(barcodeValue);


            } if (getIntent().getAction().equals("redeemVoucher")) {



            VoucherAsyncCaller voucherAsyncCaller = new VoucherAsyncCaller();
            voucherAsyncCaller.delegate = this;
            voucherAsyncCaller.execute(barcodeValue);


        } else if (getIntent().getAction().equals("addBarcode")) {


                 //BarcodeAsyncCaller asyncCaller = new BarcodeAsyncCaller();
                 //asyncCaller.delegate = this;
                 //asyncCaller.execute(barcodeValue);

                AsyncCaller asyncCaller = new AsyncCaller();
                asyncCaller.delegate = this;
                asyncCaller.execute(barcodeValue);

                /*
                BeneficiaryObject beneficiaryObject = null;

                try {
                    QueryRow row = CouchBaseManager.getInstance(context).doesBarcodeExist(barcodeValue);
                    if (row != null)
                        beneficiaryObject = CouchBaseManager.getInstance(context).extractBeneficiary(row);
                } catch (Exception e) {

                }


                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final TextView barcodeVal = (TextView) promptsView
                        .findViewById(R.id.textView_barcodeNumber);
                if (beneficiaryObject == null) {

                    barcodeVal.setText("Barcode Value is: " + barcodeValue);
                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Accept",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // get user input and set it to result
                                            // edit text
                                            //result.setText(userInput.getText());
                                            Intent intent = new Intent(context, AddSuccessActivity.class);
                                            BeneficiaryObject beneficiaryObject = getIntent().getParcelableExtra("BENEFICIARY");
                                            // PCodeObject pcode = getIntent().getParcelableExtra("PCODE");
                                            beneficiaryObject.setBarcodeNum(barcodeValue);
                                            try {
                                                //   CouchBaseManager.getInstance(this).testAdditionOne("TWO");
                                                String mainID = CouchBaseManager.getInstance(context).addBeneficiary(beneficiaryObject);
                                                beneficiaryObject.setMainID(mainID);
                                            } catch (Exception e) {

                                            }

                                            intent.putExtra("BENEFICIARY", beneficiaryObject);
                                            thisActivity.finish();
                                            startActivity(intent);

                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            callReader();
                                        }
                                    });
                } else {
                    barcodeVal.setText("Barcode " + barcodeValue + " is currently being used. please try again");
                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)

                            .setNegativeButton("Try Again",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            callReader();
                                        }
                                    });
                }
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();*/
           /* } else if (getIntent().getAction().equals("setBarcode")) {

                BarcodeAsyncCaller asyncCaller = new BarcodeAsyncCaller();
                asyncCaller.delegate = this;
                asyncCaller.execute(barcodeValue);

            } else if (getIntent().getAction().equals("addVoucher")) {

             //   BarcodeAsyncCaller asyncCaller = new BarcodeAsyncCaller();
             //   asyncCaller.delegate = this;
             //   asyncCaller.execute(barcodeValue);

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final TextView barcodeVal = (TextView) promptsView
                        .findViewById(R.id.textView_barcodeNumber);

                barcodeVal.setText("Barcode Value is: " + barcodeValue);
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Accept",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        Intent intent = new Intent();
                                        intent.putExtra("VOUCHER", barcodeValue);
                                        setResult(RESULT_OK, intent);
                                        finish();

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        callReader();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();


            } else if (getIntent().getAction().equals("addVoucher_styleTwo")) {

               // BarcodeAsyncCaller asyncCaller = new BarcodeAsyncCaller();
              //  asyncCaller.delegate = this;
              //  asyncCaller.execute(barcodeValue);

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final TextView barcodeVal = (TextView) promptsView
                        .findViewById(R.id.textView_barcodeNumber);

                barcodeVal.setText("Barcode Value is: " + barcodeValue);
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Accept",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        Intent intent = new Intent();
                                        int i = getIntent().getIntExtra("position", 0);
                                        intent.putExtra("VOUCHER", barcodeValue);
                                        intent.putExtra("position", i);
                                        setResult(3, intent);
                                        finish();

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        callReader();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();


            }
    }

        else{

            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final TextView barcodeVal = (TextView) promptsView
                    .findViewById(R.id.textView_barcodeNumber);

            barcodeVal.setText("Invalid Barcode Format, please try again.");
            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)

                    .setNegativeButton("Retry",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    callReader();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }*/
    }

    public void callOtherOptionsAlert()
    {

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.alert_otheroptions, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        LinearLayout rootLayout = (LinearLayout) promptsView.findViewById(R.id.layout_root);
        Button manualEntry = (Button) promptsView.findViewById(R.id.Button_manual);
       // Button addEntry = (Button) promptsView.findViewById(R.id.Button_addBenny);
       // Button AddEntry = (Button) promptsView.findViewById(R.id.Button_addBenny);
        Button backEntry = (Button) promptsView.findViewById(R.id.Button_back);


          manualEntry.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callManualAlert();
        }
    });

    /*    addEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddBeneficiaryActivity.class);
                PCodeObject pCodeObject = getIntent().getParcelableExtra("PCODE");
                BeneficiaryObject beneficiaryObject = new BeneficiaryObject();
                beneficiaryObject.setPcode(pCodeObject);
                intent.setAction("UNHCR");
                intent.putExtra("BENEFICIARY", beneficiaryObject);
                startActivity(intent);
            }
        });*/

        backEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(context, BrowseBeneficiariesActivity.class);
                thisActivity.finish();
                //startActivity(intent);

            }
        });

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                callReader();
                                ;
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void callManualAlert()
    {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.alert_manualbarcode, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        final EditText userInput2 = (EditText) promptsView
                .findViewById(R.id.validateID);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (userInput.getText().toString().equals(userInput2.getText().toString()))
                                    callAlertBarcode(userInput.getText().toString());
                                else
                                    Toast.makeText(getApplicationContext(),"Case numbers do not match", Toast.LENGTH_SHORT).show();
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

    public void barcodeRangeCheck(boolean isBarcodeInRange, String barcodeVal){

        if(isBarcodeInRange)
        {
            AsyncCaller asyncCaller = new AsyncCaller();
            asyncCaller.delegate = this;
            asyncCaller.execute(barcodeVal);
        }
        else {
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final TextView barcodeValue = (TextView) promptsView
                    .findViewById(R.id.textView_barcodeNumber);

            barcodeValue.setText("Barcode " + barcodeVal + " is not in Range, please try a valid barcode.");
            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)

                    .setNegativeButton("Try Again",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    callReader();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            {

            }
        }
    }


    public void lookupBarcode(final String barcodeValue,final Document doc){



        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final TextView barcodeVal = (TextView) promptsView
                .findViewById(R.id.textView_barcodeNumber);

        barcodeVal.setText("Barcode Value is: " + barcodeValue);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Accept",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                                Intent intent = new Intent(getApplicationContext(), DistributorBeneficiaryActivity.class);

                                BeneficiaryObject beneficiaryObject = CouchBaseManager.getInstance(getApplicationContext()).extractBeneficiary(doc);
                               // PCodeObject pcodeObject = getIntent().getParcelableExtra("PCODE");
                                //beneficiaryObject.setPcodeDist(pcodeObject);
                                intent.putExtra("BENEFICIARY", beneficiaryObject);
                                startActivity(intent);

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                callReader();
                            }
                        });

        alertDialogBuilder.show();

    }

    public void addbarcode(final String barcodeValue) {

        if (barcodeValue.matches("[A-Z][A-Z]\\d{5}-\\d{3}"))
        {

            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final TextView barcodeVal = (TextView) promptsView
                .findViewById(R.id.textView_barcodeNumber);

        barcodeVal.setText("Barcode Value is: " + barcodeValue);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Accept",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                                Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                                BeneficiaryObject beneficiaryObject = getIntent().getParcelableExtra("BENEFICIARY");
                                beneficiaryObject.setOfficialID(barcodeValue);
                                beneficiaryObject.setBarcodeNum(barcodeValue);
                                beneficiaryObject.setMainID(barcodeValue + ":" + beneficiaryObject.getPartnerName());
                                CouchBaseManager.getInstance(getApplicationContext()).addBeneficiary(beneficiaryObject);
                                intent.putExtra("ASSISTANCE", assistanceType);
                                intent.putExtra("BENEFICIARY", beneficiaryObject);
                                startActivity(intent);

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                callReader();
                            }
                        });

        alertDialogBuilder.show();
    }
        else{
            Toast.makeText(ReaderActivity.this,
                    "Barcode Number format is in Wrong Format..",
                    Toast.LENGTH_LONG).show();
            callReader();
        }

    }

   public void checkbarcode(final String barcodeValue, String type){


       if (type.equals("registered")) {
           if (barcodeValue.matches("\\d{3}[-]1[1-6][C]\\d{5}")) {



               LayoutInflater li = LayoutInflater.from(context);
               View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                       context);

               // set prompts.xml to alertdialog builder
               alertDialogBuilder.setView(promptsView);

               final TextView barcodeVal = (TextView) promptsView
                       .findViewById(R.id.textView_barcodeNumber);

               barcodeVal.setText("Barcode Value is: " + barcodeValue);
               // set dialog message
               alertDialogBuilder
                       .setCancelable(false)
                       .setPositiveButton("Accept",
                               new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int id) {


                                       Intent intent = new Intent(getApplicationContext(), AddBeneficiaryActivity.class);
                                       PCodeObject pcode = getIntent().getParcelableExtra("PCODE");
                                       BeneficiaryObject beneficiaryObject = new BeneficiaryObject();
                                       beneficiaryObject.setOfficialID(barcodeValue.toUpperCase());
                                       beneficiaryObject.setPcode(pcode);

                                       //beneficiaryObject.setMainID(barcodeValue);
                                       beneficiaryObject.setIdType("UNHCR");

                                       //BeneficiaryObject beneficiaryObject = CouchBaseManager.getInstance(context).extractBeneficiary(doc);
                                       intent.putExtra("BENEFICIARY", beneficiaryObject);
                                       intent.putExtra("ASSISTANCE", assistanceType);
                                       intent.putExtra("SELECT", selection);
                                       startActivity(intent);


                                   }
                               })
                       .setNegativeButton("Cancel",
                               new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int id) {
                                       dialog.cancel();
                                       callReader();
                                   }
                               });

               alertDialogBuilder.show();


           } else {
               Toast.makeText(ReaderActivity.this,
                       "Case Number format is Wrong.",
                       Toast.LENGTH_LONG).show();
           }
       }
       else if(type.equals("recorded"))
       {
           if (barcodeValue.matches("LEB-1[5-7][C]\\d{5}")) {


               LayoutInflater li = LayoutInflater.from(context);
               View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                       context);

               // set prompts.xml to alertdialog builder
               alertDialogBuilder.setView(promptsView);

               final TextView barcodeVal = (TextView) promptsView
                       .findViewById(R.id.textView_barcodeNumber);

               barcodeVal.setText("Barcode Value is: " + barcodeValue);
               // set dialog message
               alertDialogBuilder
                       .setCancelable(false)
                       .setPositiveButton("Accept",
                               new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int id) {


                                       Intent intent = new Intent(getApplicationContext(), AddBeneficiaryActivity.class);
                                       PCodeObject pcode = getIntent().getParcelableExtra("PCODE");
                                       BeneficiaryObject beneficiaryObject = new BeneficiaryObject();
                                       beneficiaryObject.setOfficialID(barcodeValue.toUpperCase());
                                       beneficiaryObject.setPcode(pcode);

                                       //beneficiaryObject.setMainID(barcodeValue);
                                       beneficiaryObject.setIdType("UNHCR");

                                       //BeneficiaryObject beneficiaryObject = CouchBaseManager.getInstance(context).extractBeneficiary(doc);
                                       intent.putExtra("BENEFICIARY", beneficiaryObject);
                                       intent.putExtra("ASSISTANCE", assistanceType);
                                       intent.putExtra("SELECT", selection);
                                       startActivity(intent);


                                   }
                               })
                       .setNegativeButton("Cancel",
                               new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int id) {
                                       dialog.cancel();
                                       callReader();
                                   }
                               });

               alertDialogBuilder.show();


           } else {
               Toast.makeText(ReaderActivity.this,
                       "Case Number format is Wrong.",
                       Toast.LENGTH_LONG).show();
           }
       }
   }

       public void processFinish(final QueryRow row, final String barcodeValue){

           BeneficiaryObject beneficiaryObject = null;

           try {
               if (row != null)
                   beneficiaryObject = CouchBaseManager.getInstance(context).extractBeneficiary(row);
           } catch (Exception e) {

           }

           if (getIntent().getAction().equals("addBarcode")) {
               LayoutInflater li = LayoutInflater.from(context);
               View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                       context);

               // set prompts.xml to alertdialog builder
               alertDialogBuilder.setView(promptsView);

               final TextView barcodeVal = (TextView) promptsView
                       .findViewById(R.id.textView_barcodeNumber);
               if (beneficiaryObject == null) {

                   barcodeVal.setText("Barcode Value is: " + barcodeValue);
                   // set dialog message
                   alertDialogBuilder
                           .setCancelable(false)
                           .setPositiveButton("Accept",
                                   new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int id) {
                                           // get user input and set it to result
                                           // edit text
                                           //result.setText(userInput.getText());
                                           Intent intent = new Intent(context, AddSuccessActivity.class);
                                           BeneficiaryObject beneficiaryObject = getIntent().getParcelableExtra("BENEFICIARY");
                                           // PCodeObject pcode = getIntent().getParcelableExtra("PCODE");
                                           beneficiaryObject.setBarcodeNum(barcodeValue);
                                           try {
                                               //   CouchBaseManager.getInstance(this).testAdditionOne("TWO");
                                             //  String mainID = CouchBaseManager.getInstance(context).addBeneficiary(beneficiaryObject);
                                             //  beneficiaryObject.setMainID(mainID);
                                           } catch (Exception e) {

                                           }


                                           if (beneficiaryObject.getMainID() != null || !beneficiaryObject.getMainID().equals("")) {
                                               intent.putExtra("BENEFICIARY", beneficiaryObject);
                                               thisActivity.finish();
                                               startActivity(intent);
                                           } else {
                                               LayoutInflater li = LayoutInflater.from(context);
                                               View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

                                               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                                       context);

                                               // set prompts.xml to alertdialog builder
                                               alertDialogBuilder.setView(promptsView);

                                               final TextView errorVal = (TextView) promptsView
                                                       .findViewById(R.id.textView_barcodeNumber);


                                               errorVal.setText("Error Saving Data, please rescan barcode or go back and check household data");
                                               // set dialog message
                                               alertDialogBuilder
                                                       .setCancelable(false)
                                                       .setNegativeButton("Cancel",
                                                               new DialogInterface.OnClickListener() {
                                                                   public void onClick(DialogInterface dialog, int id) {
                                                                       dialog.cancel();
                                                                       callReader();
                                                                   }
                                                               });

                                               // create alert dialog
                                               AlertDialog alertDialog = alertDialogBuilder.create();

                                               // show it
                                               alertDialog.show();
                                           }

                                       }
                                   })
                           .setNegativeButton("Cancel",
                                   new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int id) {
                                           dialog.cancel();
                                           callReader();
                                       }
                                   });
               } else {
                   barcodeVal.setText("Barcode " + barcodeValue + " is currently being used. please try again");
                   // set dialog message
                   alertDialogBuilder
                           .setCancelable(false)

                           .setNegativeButton("Try Again",
                                   new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int id) {
                                           dialog.cancel();
                                           callReader();
                                       }
                                   });
               }
               // create alert dialog
               AlertDialog alertDialog = alertDialogBuilder.create();

               // show it
               alertDialog.show();
           }

           else if (getIntent().getAction().equals("setBarcode")) {



               LayoutInflater li = LayoutInflater.from(context);
               View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                       context);

               // set prompts.xml to alertdialog builder
               alertDialogBuilder.setView(promptsView);

               final TextView barcodeVal = (TextView) promptsView
                       .findViewById(R.id.textView_barcodeNumber);

               barcodeVal.setText("Barcode Value is: " + barcodeValue);
               // set dialog message
               alertDialogBuilder
                       .setCancelable(false)
                       .setPositiveButton("Accept",
                               new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int id) {
                                       // get user input and set it to result
                                       // edit text
                                       //result.setText(userInput.getText());
                                       Intent intent = new Intent();
                                       intent.putExtra("BARCODE", barcodeValue);
                                       setResult(RESULT_OK, intent);

                                       thisActivity.finish();


                                   }
                               })
                       .setNegativeButton("Cancel",
                               new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int id) {
                                       dialog.cancel();
                                       callReader();
                                   }
                               });

               // create alert dialog
               AlertDialog alertDialog = alertDialogBuilder.create();

               // show it
               alertDialog.show();
           } else if (getIntent().getAction().equals("addVoucher")) {

               LayoutInflater li = LayoutInflater.from(context);
               View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                       context);

               // set prompts.xml to alertdialog builder
               alertDialogBuilder.setView(promptsView);

               final TextView barcodeVal = (TextView) promptsView
                       .findViewById(R.id.textView_barcodeNumber);

               barcodeVal.setText("Barcode Value is: " + barcodeValue);
               // set dialog message
               alertDialogBuilder
                       .setCancelable(false)
                       .setPositiveButton("Accept",
                               new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int id) {

                                       Intent intent = new Intent();
                                       intent.putExtra("VOUCHER", barcodeValue);
                                       setResult(RESULT_OK, intent);
                                       finish();

                                   }
                               })
                       .setNegativeButton("Cancel",
                               new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int id) {
                                       dialog.cancel();
                                       callReader();
                                   }
                               });

               // create alert dialog
               AlertDialog alertDialog = alertDialogBuilder.create();

               // show it
               alertDialog.show();


           } else if (getIntent().getAction().equals("addVoucher_styleTwo")) {

               LayoutInflater li = LayoutInflater.from(context);
               View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                       context);

               // set prompts.xml to alertdialog builder
               alertDialogBuilder.setView(promptsView);

               final TextView barcodeVal = (TextView) promptsView
                       .findViewById(R.id.textView_barcodeNumber);

               barcodeVal.setText("Barcode Value is: " + barcodeValue);
               // set dialog message
               alertDialogBuilder
                       .setCancelable(false)
                       .setPositiveButton("Accept",
                               new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int id) {

                                       Intent intent = new Intent();
                                       int i = getIntent().getIntExtra("position", 0);
                                       intent.putExtra("VOUCHER", barcodeValue);
                                       intent.putExtra("position", i);
                                       setResult(3, intent);
                                       finish();

                                   }
                               })
                       .setNegativeButton("Cancel",
                               new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int id) {
                                       dialog.cancel();
                                       callReader();
                                   }
                               });

               // create alert dialog
               AlertDialog alertDialog = alertDialogBuilder.create();

               // show it
               alertDialog.show();


           }



    }


    public void checkIfBeneficiaryExists(final BeneficiaryObject beneficiaryObject, final String barcodeVal)
    {


        if (beneficiaryObject != null) {
            if (role.equals(Constants.ROLE_KITS_ASSESSOR)) {

                if (getIntent().getStringExtra("" +
                        "").equals("id")) {
                    Intent intent = new Intent(context, DetailedBeneficiaryActivity.class);
                    //thisActivity.finish();
                    intent.putExtra("BENEFICIARY", beneficiaryObject);
                    startActivity(intent);
                } else if (getIntent().getStringExtra("barcodeType").equals("voucher")) {
                    Intent intent = new Intent(context, VoucherActivity.class);
                    //thisActivity.finish();
                    startActivity(intent);
                }
            } else if (role.equals(Constants.ROLE_KITS_DISTRIBUTOR)) {
                Intent intent = new Intent(context, DistributorBeneficiaryActivity.class);
                PCodeObject pcodeObject = getIntent().getParcelableExtra("PCODE");
                beneficiaryObject.setPcodeDist(pcodeObject);
                intent.putExtra("BENEFICIARY", beneficiaryObject);
                startActivity(intent);
            }
            else if (role.equals(Constants.ROLE_VOUCHERS_REDEEMER)) {
                for(int i = 0; i<beneficiaryObject.childrenList.size();i++)
                {
                    if(beneficiaryObject.childrenList.get(i).getVoucherCode().equals(barcodeVal))
                    {
                        beneficiaryObject.childrenList.get(i).setStatus("REDEEMED");
                    }
                }

                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Calendar calendar = GregorianCalendar.getInstance();
                String currentTimeString = dateFormatter.format(calendar.getTime());


                beneficiaryObject.setComplete(true);
                beneficiaryObject.setCompletionDate(currentTimeString);
                CouchBaseManager.getInstance(context).editBeneficiary(beneficiaryObject, "Voucher Redeemed");

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.alert_vouchercompleted, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder.setView(promptsView);

                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new

                                            Runnable() {
                                                public void run () {

                               alertDialog.cancel();
                                 callReader();
                                                }
                                            }

                        ,1500);

              // Intent intent = new Intent(context, RedeemVoucherActivity.class);
               // PCodeObject pcodeObject = getIntent().getParcelableExtra("PCODE");
              //  intent.putExtra("VOUCHER_CODE", barcodeVal);
              //  intent.putExtra("BENEFICIARY", beneficiaryObject);
              //  startActivity(intent);
            }

        } else {
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final TextView barcodeValText = (TextView) promptsView
                    .findViewById(R.id.textView_barcodeNumber);

            barcodeValText.setText("Barcode not found");
            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Try again",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    callReader();
                                }
                            })
                    .setNegativeButton("Go Back",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    thisActivity.finish();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
    }

    private class AsyncCaller extends android.os.AsyncTask<String, Void, QueryRow>
    {
        public AsyncResponse delegate=null;
        String barCodeVal= "";
        ProgressDialog pdLoading = new ProgressDialog(ReaderActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tChecking for Duplicate Values");
            pdLoading.show();
            pdLoading.setCancelable(false);
            pdLoading.setCanceledOnTouchOutside(false);
        }
        @Override
        protected QueryRow doInBackground(String... params) {

            barCodeVal = params[0];

            QueryRow row = CouchBaseManager.getInstance(context).doesBarcodeExist(params[0]);

            return row;
        }

        @Override
        protected void onPostExecute(QueryRow result) {
            super.onPostExecute(result);
            delegate.processFinish(result, barCodeVal);
            pdLoading.dismiss();
        }

    }

    private class BarcodeAsyncCaller extends android.os.AsyncTask<String, Void, Boolean>
    {
        public AsyncResponse delegate=null;
        String barCodeVal= "";
        ProgressDialog pdLoading = new ProgressDialog(ReaderActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tChecking if barcode is in range");
            pdLoading.show();
            pdLoading.setCancelable(false);
            pdLoading.setCanceledOnTouchOutside(false);
        }
        @Override
        protected Boolean doInBackground(String... params) {

            barCodeVal = params[0];
            boolean check = CouchBaseManager.getInstance(context).isBarcodeInRange(params[0]);
           // QueryRow row = CouchBaseManager.getInstance(context).doesBarcodeExist(params[0]);

            return check;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            delegate.barcodeRangeCheck(result, barCodeVal);
            pdLoading.dismiss();
        }

    }

    private class BeneficiaryAsyncCaller extends android.os.AsyncTask<String, Void, BeneficiaryObject>
    {
        public AsyncResponse delegate=null;
        String barCodeVal= "";
        ProgressDialog pdLoading = new ProgressDialog(ReaderActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLooking for Beneficiary");
            pdLoading.show();
            pdLoading.setCancelable(false);
            pdLoading.setCanceledOnTouchOutside(false);
        }
        @Override
        protected BeneficiaryObject doInBackground(String... params) {

            BeneficiaryObject beneficiaryObject = null;
            barCodeVal = params[0];
            try {
                QueryRow row = CouchBaseManager.getInstance(context).doesBarcodeExist(params[0]);
                if (row != null)
                    beneficiaryObject = CouchBaseManager.getInstance(context).extractBeneficiary(row);
                return beneficiaryObject;

                //  PCodeObject pcodeObject = getIntent().getParcelableExtra("PCODE");
                //  beneficiaryObject.setPcodeDist(pcodeObject);
            } catch (Exception e) {
                return beneficiaryObject;

            }
        }

        @Override
        protected void onPostExecute(BeneficiaryObject result) {
            super.onPostExecute(result);
            delegate.checkIfBeneficiaryExists(result, barCodeVal);
            pdLoading.dismiss();
        }

    }


    private class VoucherAsyncCaller extends android.os.AsyncTask<String, Void, BeneficiaryObject>
    {
        public AsyncResponse delegate=null;
        String barCodeVal= "";
        ProgressDialog pdLoading = new ProgressDialog(ReaderActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLooking up Vouchers");
            pdLoading.show();
            pdLoading.setCancelable(false);
            pdLoading.setCanceledOnTouchOutside(false);
        }
        @Override
        protected BeneficiaryObject doInBackground(String... params) {

            BeneficiaryObject beneficiaryObject = null;
            barCodeVal = params[0];
            try {
                QueryRow row = CouchBaseManager.getInstance(context).doesVoucherExist(params[0]);
                if (row != null)
                    beneficiaryObject = CouchBaseManager.getInstance(context).extractBeneficiary(row);
                return beneficiaryObject;

                //  PCodeObject pcodeObject = getIntent().getParcelableExtra("PCODE");
                //  beneficiaryObject.setPcodeDist(pcodeObject);
            } catch (Exception e) {
                return beneficiaryObject;

            }
        }

        @Override
        protected void onPostExecute(BeneficiaryObject result) {
            super.onPostExecute(result);
            delegate.checkIfBeneficiaryExists(result, barCodeVal);
            pdLoading.dismiss();
        }

    }

}
