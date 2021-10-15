package com.unicefwinterizationplatform.winterization_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.replicator.Replication;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Tarek on 12/1/2014.
 */
public class LoadingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadingscreen);
        try {
           CouchBaseManager.getInstance(this).startCBSetUp();
        }
        catch (Exception e)
        {

        }


        if (UserPrefs.isFirstSyncDone(getApplicationContext())) {

            ArrayList<PrefObject> prefList = UserPrefs.readSettingsList(getApplicationContext());
            boolean completed = false;
            for (Iterator<PrefObject> it = prefList.iterator(); it.hasNext(); ) {

                PrefObject pref = it.next();
                String prefName = pref.getPrefName();

                if (pref.isEnabled())
                {
                    try {
                        CouchBaseManager.getInstance(getApplicationContext()).PutCurrentSettings(pref);
                        CouchBaseManager.getInstance(getApplicationContext()).startPullReplication();
                        CouchBaseManager.getInstance(getApplicationContext()).pullReplication.addChangeListener(pullChangeListener());
                    }
                    catch (CouchbaseLiteException e){}
                   completed = true;
               break;

                }
            }

           if (completed == false)
            {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                finish();
            }
        }
        else
        {

            ArrayList<PrefObject> prefList = UserPrefs.readSettingsList(getApplicationContext());
            for (Iterator<PrefObject> it = prefList.iterator(); it.hasNext(); ) {

                PrefObject pref = it.next();
                String prefName = pref.getPrefName();

                if (pref.isEnabled())
                {
                    try {
                        CouchBaseManager.getInstance(getApplicationContext()).PutCurrentSettings(pref);
                    }
                    catch (CouchbaseLiteException e){}
                    break;

                }
            }

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            finish();
        }

    }

    private void loadValuesLocally(){
        CouchBaseManager.getInstance(getApplicationContext()).pullReplication.removeChangeListener(pullChangeListener());
        CouchBaseManager.getInstance(getApplicationContext()).stopPullReplication();
        new PcodeAsyncCaller().execute();
    }


    private Replication.ChangeListener pullChangeListener()
    {
        return new Replication.ChangeListener() {
            @Override
            public void changed(final Replication.ChangeEvent event) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.e("REP5","REP5");

                        TextView networkStatus = (TextView) findViewById(R.id.textView_progressDetails);


                        Replication replication = event.getSource();
                        //Log.v("STATUS",replication.getChangesCount() +"/"+ replication.getCompletedChangesCount() );

                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                        switch (replication.getStatus()) {
                            case REPLICATION_ACTIVE:
                                networkStatus.setText("Downloading Data...");

                                Log.e("STATUS",replication.getChangesCount() +"/"+ replication.getCompletedChangesCount() );
                                progressBar.setMax(replication.getChangesCount());
                                progressBar.setProgress(replication.getCompletedChangesCount());

                                break;
                            case REPLICATION_IDLE:
                                networkStatus.setText("Data Updated");
                                progressBar.setMax(replication.getChangesCount());
                                progressBar.setProgress(replication.getCompletedChangesCount());
                                loadValuesLocally();


                                break;
                            case REPLICATION_OFFLINE:
                                networkStatus.setText("Offline, please check network connection");
                                if (UserPrefs.isFirstSyncDone(getApplicationContext()))
                                {
                                    loadValuesLocally();
                                }


                                break;
                            case REPLICATION_STOPPED:
                                networkStatus.setText("Stopped");
                                if (UserPrefs.isFirstSyncDone(getApplicationContext()))
                                {
                                    loadValuesLocally();
                                }
                                break;
                        }

                    }
                });

            }
        };
    }


    private class PcodeAsyncCaller extends AsyncTask<Void, Void, Void>
    {
        TextView networkStatus = (TextView) LoadingActivity.this.findViewById(R.id.textView_progressDetails);



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            networkStatus.setText("");
            //this method will be running on UI thread
           // pdLoading.setMessage("\tLoading Site Data");
          //  pdLoading.show();
          //  pdLoading.setCancelable(false);
          //  pdLoading.setCanceledOnTouchOutside(false);
        }
        @Override
        protected Void doInBackground(Void... params) {

            //CouchBaseManager.getInstance(getApplicationContext()).createBarcodeView();
           // CouchBaseManager.getInstance(getApplicationContext()).createIDView();
            //CouchBaseManager.getInstance(getApplicationContext()).getVersioningView();

            UserPrefs.storeLatestVersionData(  CouchBaseManager.getInstance(getApplicationContext()).getLatestVersion(),getApplicationContext());
            String test = UserPrefs.getLatestVersionData(getApplicationContext());
            CouchBaseManager.getInstance(getApplicationContext()).getBarcodeRange("byBarcodeAsRange","barcodes");

            //CouchBaseManager.getInstance(getApplicationContext()).setPcodeList();
           // com.couchbase.lite.View viewItems = CouchBaseManager.getInstance(getApplicationContext()).createIDView(Constants.ROLE_KITS_ASSESSOR);



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread

            Map<String,String> usermap = UserPrefs.getUserProfile(getApplicationContext());
            String username = usermap.get("user");
            String password = usermap.get("pass");

            ArrayList<QueryRow> userArray =  CouchBaseManager.getInstance(getApplicationContext()).getRowsWithView("byUsername", "user");
            QueryRow selectedRow = null;
            for(QueryRow row : userArray)
            {
                Document doc = row.getDocument();

                String user = doc.getCurrentRevision().getProperty("username").toString();
                String pass = doc.getCurrentRevision().getProperty("password").toString();



                if(username.toLowerCase().equals(user.toLowerCase()) && password.equals(pass))
                {
                    selectedRow = row;



                    try {
                        String org = UserPrefs.getOrganisation(getApplicationContext());
                        CouchBaseManager.getInstance(getApplicationContext()).startCBLite(org);
                    }
                    catch (Exception e)
                    {

                    }
                    break;
                }

            }

            if(selectedRow != null)
            {
                //Set User prefs
                Document doc = selectedRow.getDocument();
                JSONObject userObj = new JSONObject(doc.getCurrentRevision().getProperties());
                UserPrefs.storeUserData(userObj, getApplicationContext());
               // ArrayList<String> roles = (ArrayList<String>)doc.getCurrentRevision().getProperty("roles");
               // UserPrefs.storeLoginData(roles.get(0),getApplicationContext());
                Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                finish();

                //   if (roles.get(0).equals(Constants.ROLE_KITS_ASSESSOR))
                //    {
                //new AsyncCaller().execute();
            }

            else {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                finish();
            }
           // pdLoading.dismiss();
        }

    }


    }
