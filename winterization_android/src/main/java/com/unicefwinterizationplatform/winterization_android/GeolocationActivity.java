package com.unicefwinterizationplatform.winterization_android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

/**
 * Created by Tarek on 9/30/2014.
 */
public class GeolocationActivity extends HeaderActivity {

    final int SET_PCODE = 0;
    int retries = 0;
    PCodeObject pcode;
    ProgressDialog progress;
    Context context = this;
    Intent loc;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geolocation);
        new AsyncCaller().execute();

        retries = 0;
        Button proceedButton = (Button) findViewById(R.id.button_setGeoData);
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView pcodeSite = (TextView) findViewById(R.id.textView_siteSelection);
                if(pcodeSite.getText().toString().equals(""))
                {
                    Toast.makeText(GeolocationActivity.this,
                            "ID Not Found in Database",
                            Toast.LENGTH_LONG).show();
                }
                else {


                        Intent intent = new Intent(getApplicationContext(), AddBeneficiaryActivity.class);
                        BeneficiaryObject beneficiaryObject = new BeneficiaryObject();
                        beneficiaryObject.setPcode(pcode);
                        TextView latText = (TextView) findViewById(R.id.editText_latitude);
                        TextView longText = (TextView) findViewById(R.id.editText_longitude);
                        TextView altText = (TextView) findViewById(R.id.editText_altitude);

//                        beneficiaryObject.setLatitude(latText.getText().toString());
//                        beneficiaryObject.setLongitude(longText.getText().toString());
//                        beneficiaryObject.setAltitude(altText.getText().toString());


                        intent.putExtra("BENEFICIARY", beneficiaryObject);
                    stopService(loc);
                        startActivity(intent);

                }
            }
        });

    /*    Button getGeoData =(Button) findViewById(R.id.button_geolocation);
        getGeoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(location != null) {
                    TextView Lat = (TextView) findViewById(R.id.editText_latitude);
                    Lat.setText(location.getLatitude() + "");

                    TextView Long = (TextView) findViewById(R.id.editText_longitude);
                    Long.setText(location.getLongitude() + "");


                    TextView Alt = (TextView) findViewById(R.id.editText_altitude);
                    Alt.setText(location.getAltitude() + "");
                }
            }
        });*/

        TextView pcodeSelect = (TextView) findViewById(R.id.textView_siteSelection);
        pcodeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loc = new Intent(getApplicationContext(), LocationService.class);
                loc.setAction("startListening");
                startService(loc);
                Intent intent = new Intent(getApplicationContext(),PCodeSelectionActivity.class);
                startActivityForResult(intent, SET_PCODE);
            }


        });

        Button getPCode = (Button) findViewById(R.id.button_getPCode);
        getPCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loc = new Intent(getApplicationContext(), LocationService.class);
                loc.setAction("startListening");
                startService(loc);
                Intent intent = new Intent(getApplicationContext(),PCodeSelectionActivity.class);
                startActivityForResult(intent, SET_PCODE);
            }
        });




    }



    private void getGeoData()
    {
        Handler handler = new Handler();
        progress = new ProgressDialog(context);
        progress.setTitle("Retrieving GPS Data");
        progress.setMessage("Please Wait while loading...");
        progress.show();

        handler.postDelayed(new Runnable() {
            public void run() {
                progress.dismiss();
                if(location != null) {

                    LinearLayout layout = (LinearLayout) findViewById(R.id.layout_geoData);
                    layout.setVisibility(View.VISIBLE);
                    Button getPCode = (Button) findViewById(R.id.button_getPCode);
                    getPCode.setVisibility(View.GONE);
                    TextView Lat = (TextView) findViewById(R.id.editText_latitude);
                    Lat.setText(location.getLatitude() + "");

                    TextView Long = (TextView) findViewById(R.id.editText_longitude);
                    Long.setText(location.getLongitude() + "");


                    TextView Alt = (TextView) findViewById(R.id.editText_altitude);
                    Alt.setText(location.getAltitude() + "");

                }
                else
                {


                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final TextView GPSVal = (TextView) promptsView
                            .findViewById(R.id.textView_barcodeNumber);

                    GPSVal.setText("Unable to get GPS location. Please change your position and try again.");
                    // set dialog message
                    if (retries < 2) {
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Try Again",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                getGeoData();
                                            }
                                        });

                    }
                    else if(retries>=2)
                    {
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Try Again",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                getGeoData();
                                            }
                                        })
                                .setNegativeButton("Proceed",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent = new Intent(getApplicationContext(), AddBeneficiaryActivity.class);
                                                BeneficiaryObject beneficiaryObject = new BeneficiaryObject();
                                                beneficiaryObject.setPcode(pcode);

//                                                beneficiaryObject.setLatitude("");
//                                                beneficiaryObject.setLongitude("");
//                                                beneficiaryObject.setAltitude("");

                                                intent.putExtra("BENEFICIARY", beneficiaryObject);
                                                stopService(loc);
                                                startActivity(intent);

                                            }
                                        });
                    }

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                    retries++;
                }


            }
        }, 2000);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                PCodeObject pcodeObject = data.getParcelableExtra("PCODE");

                pcode = pcodeObject;

                TextView pcodeSelect = (TextView) findViewById(R.id.textView_siteSelection);
                pcodeSelect.setText(pcode.getPcodeName());

                getGeoData();


            }
        }
    }

    private class AsyncCaller extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdLoading = new ProgressDialog(GeolocationActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading Site Data");
            pdLoading.show();
            pdLoading.setCancelable(false);
            pdLoading.setCanceledOnTouchOutside(false);
        }
        @Override
        protected Void doInBackground(Void... params) {


            //CouchBaseManager.getInstance(context).setPcodeList();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread

            pdLoading.dismiss();
        }

    }
    public interface AsyncResponse {
        void processFinish(String output);
    }
}