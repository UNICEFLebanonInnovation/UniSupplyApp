package com.unicefwinterizationplatform.winterization_android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.QueryRow;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Tarek on 9/30/2014.
 */
public class PCodeSelectionActivity extends HeaderActivity {

    Context context = this;
    private PCodeAdapter pcodeAdapter;
    ProgressDialog   progress;
    ListView  pcodeList;
    LiveQuery liveQuery;
    String locType;

    ArrayList<String> cadasterList = new ArrayList<String>();
    ArrayList<String> districtList = new ArrayList<String>();
    JSONObject obj=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pcodeselection);


        //ArrayList<PCodeObject> arr = CouchBaseManager.getInstance(this).getPcodeList();//getRowsWithView("byLoction","location","IS");

        com.couchbase.lite.View byPcodeItem = CouchBaseManager.getInstance(getApplicationContext()).startPcodeItemsView(getApplicationContext(), UserPrefs.getUsername(getApplicationContext()));

        Intent loc = new Intent(getApplicationContext(), LocationService.class);
        loc.setAction("startListening");
        startService(loc);

        locType = getIntent().getStringExtra("LOC_TYPE");


        if (locType.equals("IS")) {
            ArrayList<QueryRow> arr = new ArrayList<QueryRow>();
            pcodeAdapter = new PCodeAdapter(this, arr);
            //pcodeAdapter.addList(arr);// new ListAdapter(this, android.R.layout.simple_list_item_1, arr);
            pcodeList = (ListView) this.findViewById(R.id.pcode_list);
            pcodeList.setAdapter(pcodeAdapter);
            pcodeList.setFastScrollEnabled(true);
            pcodeList.setFastScrollAlwaysVisible(true);
            pcodeList.setOnItemClickListener(onItemClickListener());
            startLiveQuery(byPcodeItem);
            EditText searchPcode = (EditText) findViewById(R.id.editText_pcodeSearch);

            searchPcode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    pcodeAdapter.getFilter().filter(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        Button noPcodeButton = (Button) findViewById(R.id.button_noPCode);

        noPcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getGeoData();

            }
        });

    }


    private void startLiveQuery(com.couchbase.lite.View view)
    {

        if (liveQuery == null) {

            liveQuery = view.createQuery().toLiveQuery();

            liveQuery.addChangeListener(liveChangeQuery());

            liveQuery.start();

        }
    }


    private LiveQuery.ChangeListener liveChangeQuery(){
        return new LiveQuery.ChangeListener() {
            @Override
            public void changed(final LiveQuery.ChangeEvent event) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        //final ProgressDialog progressDialog = showLoadingSpinner();
                        pcodeAdapter.clear();
                        pcodeAdapter.clear2();
                        for (Iterator<QueryRow> it = event.getRows(); it.hasNext(); ) {
                            QueryRow row = it.next();

                            pcodeAdapter.addItem(row);
                            pcodeAdapter.addSearchItem(row);

                        }
                        pcodeAdapter.notifyDataSetChanged();
                        //progressDialog.dismiss();
                    }
                });
            }
        };
    }

    private AdapterView.OnItemClickListener onItemClickListener(){

        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                QueryRow row = (QueryRow)pcodeAdapter.getItem(i);
                Document doc = row.getDocument();

                String id = doc.getId();
                    //getGeoData(doc);


                    setPcodeObject(doc,null);



        //       else {
          //      Intent intent = new Intent();
           //     intent.putExtra("PCODE", pcodeObject);
          //      setResult(RESULT_OK, intent);
          //      finish();
          //    }
           }
        };

    }



    private void setPcodeObject(Document doc, String lat, String lon)
    {
        PCodeObject pcodeObject = new PCodeObject();
        pcodeObject.setPcodeID(doc.getCurrentRevision().getProperty("p_code").toString());
        pcodeObject.setPcodeName(doc.getCurrentRevision().getProperty("p_code_name").toString());
        pcodeObject.setCadastral(doc.getCurrentRevision().getProperty("cadastral").toString());
        pcodeObject.setDistrict(doc.getCurrentRevision().getProperty("district").toString());
        pcodeObject.setLocationType(doc.getCurrentRevision().getProperty(locType).toString());

        pcodeObject.setPcodeLong(lon);
        pcodeObject.setPcodeLat(lat);





        if(getIntent().getAction() != null && getIntent().getAction().equals("REPLACE_PCODE"))
        {
            Intent intent = new Intent();
            intent.putExtra("PCODE", pcodeObject);
            setResult(RESULT_OK, intent);
            finish();

        }
        else{

            Intent intent = new Intent(getApplicationContext(),ConsentActivity.class);
            intent.putExtra("PCODE", pcodeObject);
            intent.putExtra("barcodeType", "id");
            String assistanceType = getIntent().getStringExtra("ASSISTANCE");
            intent.putExtra("ASSISTANCE", assistanceType);
            intent.setAction("searchAssessment");
            startActivity(intent);
        }
    }

    private void setPcodeObject(PCodeObject pcodeObject)
    {


        if(getIntent().getAction() != null && getIntent().getAction().equals("REPLACE_PCODE"))
        {
            Intent intent = new Intent();
            intent.putExtra("PCODE", pcodeObject);
            setResult(RESULT_OK, intent);
            finish();

        }
        else{

            Intent intent = new Intent(getApplicationContext(),ConsentActivity.class);
            intent.putExtra("PCODE", pcodeObject);
            intent.putExtra("barcodeType", "id");
            String assistanceType = getIntent().getStringExtra("ASSISTANCE");
            intent.putExtra("ASSISTANCE", assistanceType);
            intent.setAction("searchAssessment");
            startActivity(intent);
        }
    }


    private void setPcodeObject(Document doc, Location loc1)
    {
        PCodeObject pcodeObject = new PCodeObject();
        pcodeObject.setPcodeID(doc.getCurrentRevision().getProperty("p_code").toString());
        pcodeObject.setPcodeName(doc.getCurrentRevision().getProperty("p_code_name").toString());
        pcodeObject.setCadastral(doc.getCurrentRevision().getProperty("cadastral").toString());
        pcodeObject.setDistrict(doc.getCurrentRevision().getProperty("district").toString());



        pcodeObject.setLocationType(locType);

        if(loc1 == null) {
            pcodeObject.setPcodeLong(doc.getCurrentRevision().getProperty("longitude").toString());
            pcodeObject.setPcodeLat(doc.getCurrentRevision().getProperty("latitude").toString());
        }
        else
        {
            pcodeObject.setPcodeLong(loc1.getLongitude()+"");
            pcodeObject.setPcodeLat(loc1.getLatitude()+"");
        }



        if(getIntent().getAction() != null && getIntent().getAction().equals("REPLACE_PCODE"))
        {
            Intent intent = new Intent();
            intent.putExtra("PCODE", pcodeObject);
            setResult(RESULT_OK, intent);
            finish();

        }
        else{
            Intent intent = new Intent(getApplicationContext(),ConsentActivity.class);
            intent.putExtra("PCODE", pcodeObject);
            intent.putExtra("barcodeType", "id");
            String assistanceType = getIntent().getStringExtra("ASSISTANCE");
            intent.putExtra("ASSISTANCE", assistanceType);
            intent.setAction("searchAssessment");
            startActivity(intent);
        }
    }

    private void getGeoData( final Document doc)
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

                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.alert_geo, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final TextView GPSVal = (TextView) promptsView
                            .findViewById(R.id.textView_barcodeNumber);


                    GPSVal.setText("GPS Coordinates Retrieved:\n" +
                            "Latitude:"+location.getLatitude()+"\n" +
                            "Longitude:"+location.getLongitude()+"\n");
                    // set dialog message

                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Proceed",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            setPcodeObject(doc, location);
                                            dialog.cancel();
                                        }
                                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    dialog.cancel();
                                }
                            });


                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();



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

                    GPSVal.setText("Unable to get GPS location. Please change your position and try again, or enter coordinates manually.");
                    // set dialog message

                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Try Again",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                getGeoData(doc);
                                            }
                                        })
                        .setNegativeButton("Enter Manually", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                getGeoDataManually();
                            }
                        });


                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }


            }
        }, 2000);
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

                    LayoutInflater li = LayoutInflater.from(context);
                    final View promptsView = li.inflate(R.layout.alert_geo, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final TextView GPSVal = (TextView) promptsView
                            .findViewById(R.id.textView_geoData);

                    final Spinner districtSpinner = (Spinner) promptsView
                            .findViewById(R.id.edit_district);

                    final Spinner cadastralSpinner = (Spinner) promptsView
                            .findViewById(R.id.edit_cadastral);




                    try {
                        obj = new JSONObject(loadJSONFromAsset());
                        Iterator<?> keys = obj.keys();
                        Log.e("TEST", obj.toString());
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


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });


                    ArrayAdapter<CharSequence> cadastralAdapter = new ArrayAdapter<CharSequence>(promptsView.getContext(),R.layout.spinner_text);
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


                    GPSVal.setText("GPS Coordinates Retrieved:\n" +
                            "Latitude:"+location.getLatitude()+"\n" +
                            "Longitude:"+location.getLongitude()+"\n");

                    // set dialog message

                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Proceed",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            if (  districtSpinner.getSelectedItem().toString().equals("----") || cadastralSpinner.getSelectedItem().toString().equals("----")) {
                                                Toast.makeText(getApplicationContext(),"Empty Fields",Toast.LENGTH_SHORT).show();
                                            }
                                            else {

                                                PCodeObject naPcode = new PCodeObject();

                                                naPcode.setPcodeID("no pcode");
                                                naPcode.setPcodeName("no pcode");
                                                naPcode.setLocationType(locType);
                                                naPcode.setPcodeLat(location.getLatitude() + "");
                                                naPcode.setPcodeLong(location.getLongitude() + "");
                                                naPcode.setDistrict(districtSpinner.getSelectedItem() + "");
                                                naPcode.setCadastral(cadastralSpinner.getSelectedItem()+"");


                                                setPcodeObject(naPcode);
                                                dialog.cancel();
                                            }
                                        }
                                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    dialog.cancel();
                                }
                            });


                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();



                }
                else
                {


                    cadasterList.add("----");

                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final TextView GPSVal = (TextView) promptsView
                            .findViewById(R.id.textView_barcodeNumber);

                    GPSVal.setText("Unable to get GPS location. Please change your position and try again, or enter coordinates manually.");
                    // set dialog message

                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Try Again",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            getGeoData();
                                        }
                                    })
                            .setNegativeButton("Enter Manually", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    getGeoDataManually();
                                }
                            });


                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }


            }
        }, 2000);
    }

    private void getGeoDataManually()
    {

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.alert_geomanual, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText latText = (EditText) promptsView.findViewById(R.id.edit_latitude);
        final EditText longText = (EditText) promptsView.findViewById(R.id.edit_longitude);

        final Spinner districtSpinner = (Spinner) promptsView
                .findViewById(R.id.edit_district);

        final Spinner cadastralSpinner = (Spinner) promptsView
                .findViewById(R.id.edit_cadastral);

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



        // set dialog message

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Proceed",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (latText.getText().toString().isEmpty() || longText.getText().toString().isEmpty()|| districtSpinner.getSelectedItem().toString().equals("----") || cadastralSpinner.getSelectedItem().toString().equals("----")) {
                                    Toast.makeText(getApplicationContext(),"Empty Fields",Toast.LENGTH_SHORT).show();
                                }
                                else {

                                    PCodeObject naPcode = new PCodeObject();

                                    naPcode.setPcodeID("no pcode");
                                    naPcode.setPcodeName("no pcode");
                                    naPcode.setLocationType(locType);
                                    naPcode.setPcodeLat(latText.getText() + "");
                                    naPcode.setPcodeLong(longText.getText() + "");
                                    naPcode.setDistrict(districtSpinner.getSelectedItem() + "");
                                    naPcode.setCadastral(cadastralSpinner.getSelectedItem()+ "");

                                    setPcodeObject(naPcode);
                                    dialog.cancel();
                                }
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

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

    @Override
    public void onStop() {
        super.onStop();
        Intent loc = new Intent(getApplicationContext(), LocationService.class);

        stopService(loc);
        super.onDestroy();
    }

}
