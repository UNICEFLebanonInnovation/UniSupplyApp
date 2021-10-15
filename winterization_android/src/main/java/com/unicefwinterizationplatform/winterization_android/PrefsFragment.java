package com.unicefwinterizationplatform.winterization_android;

/**
 * Created by Tarek on 8/30/15.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.text.InputType;
import android.text.method.KeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.replicator.Replication;



import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PrefsFragment extends PreferenceFragment
         {

    int deleteFlag = 0;



   PrefObject prefMap;
   //ArrayList<PrefObject> prefList;
   // String prefName;
    Preference initialiseSetting;
    int pos;

//     public ArrayList<PrefObject> getPrefList() {
//                 return prefList;
//             }

     public PrefObject getPrefMap() {
           return prefMap;
    }

    public void setPrefMap(PrefObject prefMap) {
           this.prefMap = prefMap;
       }

      public PrefsFragment()
    {

    }

    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
//        addPreferencesFromResource(R.xml.prefs);
        //prefName = getActivity().getIntent().getStringExtra("PREF_NAME");

        prefMap = (PrefObject)getArguments().get("PREF_MAP");
        //prefList = (ArrayList<PrefObject>) getArguments().get("PREF_LIST");
        pos = getArguments().getInt("POS");

        PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity().getApplicationContext());


        PreferenceCategory serverSettings = new PreferenceCategory(getActivity());
        serverSettings.setTitle(prefMap.getPrefName() + " Server Settings");
        preferenceScreen.addPreference(serverSettings);

        final SwitchPreference switchPref = new SwitchPreference(getActivity());
        switchPref.setTitle("Trigger Environment");
        switchPref.setChecked(prefMap.isEnabled());
        switchPref.setSummary("Turn on/off this environment");
        switchPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                prefMap.setEnabled((Boolean) o);
                initialiseSetting.setEnabled((Boolean) o);
                if ((Boolean) o) {
                    UserPrefs.enableSettings(getActivity().getApplicationContext(), prefMap.getPrefName());
                } else {
                    UserPrefs.disableSettings(getActivity().getApplicationContext(), prefMap.getPrefName());
                }

                try {
                    CouchBaseManager.getInstance(getActivity().getApplicationContext()).PutCurrentSettings(prefMap);
                } catch (CouchbaseLiteException e) {

                }


//                if ((Boolean) o) {
//
//                    for (int i = 0; i < prefList.size(); i++) {
//                        prefList.get(i).setEnabled(false);
//                    }
//
//
//                    try {
//                        CouchBaseManager.getInstance(getActivity().getApplicationContext()).PutCurrentSettings(prefName);
//                    } catch (CouchbaseLiteException e) {
//
//                    }
//                }
//
//                prefList.set(pos, prefMap);

                return true;
            }
        });
        serverSettings.addPreference(switchPref);


        final EditTextPreference serverUrlPref = new EditTextPreference(getActivity());
        serverUrlPref.setTitle("Server Base URL:");
        //serverUrlPref.setKey(- + "ServerURL");
        serverSettings.addPreference(serverUrlPref);
        serverUrlPref.setDialogTitle("Set Base Server URL:");
        serverUrlPref.setText(prefMap.getBaseUrl());
        serverUrlPref.setSummary(prefMap.getBaseUrl());



        serverUrlPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                prefMap.setBaseUrl((String) o);
                UserPrefs.updatedSettings(getActivity().getApplicationContext(), prefMap);
                preference.setSummary(o.toString());
                if (switchPref.isChecked()) {
                    try {
                        CouchBaseManager.getInstance(getActivity().getApplicationContext()).PutCurrentSettings(prefMap);
                    } catch (CouchbaseLiteException e) {


                    }
                }

                return true;
            }
        });


        final EditTextPreference serverPortPref = new EditTextPreference(getActivity());
        serverPortPref.setTitle("Server Port:");

        //serverUrlPref.setKey(- + "ServerURL");
        serverSettings.addPreference(serverPortPref);
        serverPortPref.setDialogTitle("Set Server Port:");
        serverPortPref.setSummary(prefMap.getPort());
        serverPortPref.setText(prefMap.getPort());
        serverPortPref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        serverPortPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                prefMap.setPort((String) o);
                UserPrefs.updatedSettings(getActivity().getApplicationContext(),prefMap);
                preference.setSummary(o.toString());
                if (switchPref.isChecked()) {
                    try {
                        CouchBaseManager.getInstance(getActivity().getApplicationContext()).PutCurrentSettings(prefMap);
                    } catch (CouchbaseLiteException e) {


                    }
                }

                return true;
            }
        });

        final EditTextPreference serverCBNamePref = new EditTextPreference(getActivity());
        serverCBNamePref.setTitle("Database Instance Name:");

        //serverUrlPref.setKey(- + "ServerURL");
        serverSettings.addPreference(serverCBNamePref);
        serverCBNamePref.setDialogTitle("Set Database Instance Name:");
        serverCBNamePref.setSummary(prefMap.getCbName());
        serverCBNamePref.setText(prefMap.getCbName());

        serverCBNamePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                prefMap.setCbName((String) o);
                UserPrefs.updatedSettings(getActivity().getApplicationContext(),prefMap);
                preference.setSummary(o.toString());
                if (switchPref.isChecked()) {
                    try {
                        CouchBaseManager.getInstance(getActivity().getApplicationContext()).PutCurrentSettings(prefMap);
                    } catch (CouchbaseLiteException e) {


                    }
                }

                return true;
            }
        });

        PreferenceCategory securitySettings = new PreferenceCategory(getActivity());
        securitySettings.setTitle(prefMap.getPrefName() + " Security Settings");
        preferenceScreen.addPreference(securitySettings);

        final EditTextPreference serverUsernamePref = new EditTextPreference(getActivity());

        serverUsernamePref.setTitle("Server Username:");

        //serverUsernamePref.setKey(prefName+"ServerName");
        securitySettings.addPreference(serverUsernamePref);
        serverUsernamePref.setDialogTitle("Set Server Username:");

        serverUsernamePref.setSummary(prefMap.getUsername());
        serverUsernamePref.setText(prefMap.getUsername());

        serverUsernamePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                prefMap.setUsername((String)o);
                UserPrefs.updatedSettings(getActivity().getApplicationContext(),prefMap);

                preference.setSummary(o.toString());
                if (switchPref.isChecked()) {
                    try {
                        CouchBaseManager.getInstance(getActivity().getApplicationContext()).PutCurrentSettings(prefMap);
                    } catch (CouchbaseLiteException e) {


                    }
                }
                return true;
            }
        });


        final EditTextPreference serverPasswordPref = new EditTextPreference(getActivity());

        serverPasswordPref.setSummary("Password Hidden");
        serverPasswordPref.setTitle("Server Password:");
        serverPasswordPref.setText(prefMap.getPassword());
        //serverPasswordPref.setKey(prefName + "ServerPassword");

        EditText et = serverPasswordPref.getEditText();
        et.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        et.setTransformationMethod(PasswordTransformationMethod.getInstance());
        securitySettings.addPreference(serverPasswordPref);
        serverPasswordPref.setDialogTitle("Set Server Password:");
        serverPasswordPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                prefMap.setPassword((String)o);
                UserPrefs.updatedSettings(getActivity().getApplicationContext(),prefMap);

                if (switchPref.isChecked()) {
                    try {
                        CouchBaseManager.getInstance(getActivity().getApplicationContext()).PutCurrentSettings(prefMap);
                    } catch (CouchbaseLiteException e) {


                    }
                }
                return true;
            }
        });

        PreferenceCategory testSettings = new PreferenceCategory(getActivity());
        testSettings.setTitle("Initialise Database");
        preferenceScreen.addPreference(testSettings);
        initialiseSetting = new Preference(getActivity());
        initialiseSetting.setEnabled(switchPref.isChecked());
        initialiseSetting.setTitle("Initialise Database");
        initialiseSetting.setSummary("Initialise Database for " + prefMap.getPrefName());
       // pref4.setKey(prefName + "Button");
        testSettings.addPreference(initialiseSetting);
        initialiseSetting.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {


                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                String url ="http://"+prefMap.getBaseUrl()+":"+prefMap.getPort()+"/"+prefMap.getCbName();

// Request a string response from the provided URL.



                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.e("TAGGED",response);
                                CouchBaseManager.getInstance(getActivity().getApplicationContext()).startPullReplication();
                                CouchBaseManager.getInstance(getActivity().getApplicationContext()).pullReplication.addChangeListener(pullChangeListener());

                                // Display the first 500 characters of the response string.
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        initialiseSetting.setSummary("Connection Down");
                        //
                        LayoutInflater li = LayoutInflater.from(getActivity().getApplicationContext());
                        View promptsView = li.inflate(R.layout.alert_generic, null);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(promptsView);

                        final LinearLayout linearLayout = (LinearLayout) promptsView
                                .findViewById(R.id.layout_gen);

                        TextView alertText = new TextView(getActivity().getApplicationContext());
                        linearLayout.addView(alertText);
                        alertText.setTextSize(20);
                        alertText.setTextColor(getResources().getColor(R.color.black));
                        alertText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        alertText.setGravity(Gravity.CENTER);

                        alertText.setText("Cannot Connect. Please check if the above settings are correct or contact support.");


                        // set dialog message
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                dialog.dismiss();

                                            }
                                        });


                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();


                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        //add params <key,value>
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> headers = new HashMap<String, String>();
                        // add headers <key,value>
                        String credentials = prefMap.getUsername()+":"+prefMap.getPassword();
                        String auth = "Basic "
                                + Base64.encodeToString(credentials.getBytes(),
                                Base64.NO_WRAP);
                        headers.put("Authorization", auth);
                        return headers;
                    }
                };
// Add the request to the RequestQueue.

                queue.add(stringRequest);

//                if (switchPref.isChecked()){
//                    try {
//                        CouchBaseManager.getInstance(getActivity().getApplicationContext()).PutCurrentSettings(prefMap);
//                    } catch (CouchbaseLiteException e) {
//                    }
//                }
//                CouchBaseManager.getInstance(getActivity().getApplicationContext()).startPullReplication();
//                CouchBaseManager.getInstance(getActivity().getApplicationContext()).pullReplication.addChangeListener(pullChangeListener());
                return true;
            }
        });


    //    final CheckBoxPreference assessmentPref = new CheckBoxPreference(getActivity());
    //    assessmentPref.setTitle("Use Beneficiary Assessments");
       // assessmentPref.setKey(prefName + "assessment");

   //     assessmentPref.setChecked(assessmentPref.isChecked());
        //boxPref.setSummary("");

 //       serverSettings.addPreference(assessmentPref);


        setPreferenceScreen(preferenceScreen);
    }

             private Replication.ChangeListener pullChangeListener()
             {
                 return new Replication.ChangeListener() {
                     @Override
                     public void changed(final Replication.ChangeEvent event) {
                         getActivity().runOnUiThread(new Runnable() {
                             @Override
                             public void run() {

                                 Log.e("REP1","REP1");
                                 //final Preference initialiseSetting = findPreference(prefName+"Button");

                                 Replication replication = event.getSource();
                                 Log.v("STATUS",replication.getChangesCount() +"/"+ replication.getCompletedChangesCount() );

                                 switch (replication.getStatus()) {
                                     case REPLICATION_ACTIVE:

                                         initialiseSetting.setSummary("Downloading Data.");

                                         break;
                                     case REPLICATION_IDLE:

                                         initialiseSetting.setSummary("Download Complete.");

                                         break;
                                     case REPLICATION_OFFLINE:

                                         initialiseSetting.setSummary("Offline, please check network settings.");

                                         break;
                                     case REPLICATION_STOPPED:

                                         initialiseSetting.setSummary("Connection Stopped.");


                                         break;
                                 }
                             }
                         });
                     }
                 };
             }
 }