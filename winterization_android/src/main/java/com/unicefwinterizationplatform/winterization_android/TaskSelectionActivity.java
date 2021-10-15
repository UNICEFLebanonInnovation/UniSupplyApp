
package com.unicefwinterizationplatform.winterization_android;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.couchbase.lite.replicator.Replication;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Tarek on 10/31/2014.
 */
public class TaskSelectionActivity extends HeaderActivity  {

    Context context = this;
    JSONArray roles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // CouchBaseManager.getInstance(getApplicationContext()).stopReplication();
      //  CouchBaseManager.getInstance(getApplicationContext()).startSync();
        CouchBaseManager.getInstance(this).pullReplication.addChangeListener(pullChangeListener());
        CouchBaseManager.getInstance(this).pushReplication.addChangeListener(pushChangeListener());
        setContentView(R.layout.activity_taskselection);
        TextView progressText = (TextView) findViewById(R.id.textView_progress);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        int tot  =   CouchBaseManager.getInstance(this).getTotalPullDocs();
        int comp = CouchBaseManager.getInstance(this).getPullDocs();

        progressText.setText(comp +"/"+tot +"");
        progressBar.setMax(tot);
        progressBar.setProgress(comp);

        TextView progressTextPush = (TextView) findViewById(R.id.textView_progressPush);
        ProgressBar progressBarPush = (ProgressBar) findViewById(R.id.progressBarPush);
        int tot1  =   CouchBaseManager.getInstance(this).getTotalPullDocs();
        int comp1 = CouchBaseManager.getInstance(this).getPullDocs();

        progressTextPush.setText(comp1 +"/"+tot1 +"");
        progressBarPush.setMax(tot1);
        progressBarPush.setProgress(comp1);
        //TextView networkStatus = (TextView) findViewById(R.id.textView_networkStatus);

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

          TextView networkStatus = (TextView) findViewById(R.id.textView_networkStatus);
        networkStatus.setText(CouchBaseManager.getInstance(this).netStatus);

        if (!mWifi.isConnected()) {
            networkStatus.setText("Offline, please check network connection.");
        }

        JSONObject userObj = UserPrefs.getUserData(this);
        String firstRole = "";
        try {
            String r = userObj.getString("roles");
            //JSONObject jsnobject = new JSONObject(r);
            roles =  new JSONArray(r);
          firstRole =  roles.getString(0);
        }
        catch (JSONException e)
        {
            Log.e("exxception", e.getMessage());
        }

        Button assessorButton = (Button)this.findViewById(R.id.select_assessemt);
        assessorButton.setOnClickListener(assessmentButton());
        Button browseButton = (Button)this.findViewById(R.id.select_distribution);
        browseButton.setOnClickListener(distributionButton());
        Button voucherButton = (Button)this.findViewById(R.id.select_vouchers);
        voucherButton.setOnClickListener(voucherButton());

        if (firstRole.equals(Constants.ROLE_KITS_ASSESSOR))
        {

            assessorButton.setVisibility(View.VISIBLE);
            browseButton.setVisibility(View.VISIBLE);

            try{
                if (roles.length() >2)
                {
                    if (roles.getString(2).equals(Constants.ROLE_VOUCHERS_DISTRIBUTOR))
                    {
                        voucherButton.setVisibility(View.VISIBLE);
                    }
                    else
                        voucherButton.setVisibility(View.GONE);
                }
                else
                    voucherButton.setVisibility(View.GONE);

            }
            catch (Exception e)
            {

            }

        }

        else if (firstRole.equals(Constants.ROLE_VOUCHERS_DISTRIBUTOR) || firstRole.equals(Constants.ROLE_VOUCHERS_REDEEMER))
        {
            assessorButton.setVisibility(View.GONE);
            browseButton.setVisibility(View.GONE);
            voucherButton.setVisibility(View.VISIBLE);
        }



        Button forceSync = (Button)this.findViewById(R.id.button_forceSync);
        forceSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CouchBaseManager.getInstance(getApplicationContext()).stopReplication();
             //   CouchBaseManager.getInstance(getApplicationContext()).startSync();
                CouchBaseManager.getInstance(getApplicationContext()).pullReplication.addChangeListener(pullChangeListener());
                CouchBaseManager.getInstance(getApplicationContext()).pushReplication.addChangeListener(pushChangeListener());
            }
        });

    }


    private View.OnClickListener assessmentButton()
    {
        return  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userRole = "";// Constants.ROLE_KITS_ASSESSOR;
                try {
                   userRole = roles.getString(0);
                }
                catch (JSONException e)
                {

                }
                UserPrefs.storeLoginData(userRole, context);
                Intent intent =   new Intent(getApplicationContext(), MainMenuActivity.class);
                intent.setAction("doAssessments");
                startActivity(intent);


            }
        };
    }

    private View.OnClickListener voucherButton()
    {
        return  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userRole = "";// Constants.ROLE_KITS_ASSESSOR;
                try {
                    if(roles.getString(0).equals(Constants.ROLE_KITS_ASSESSOR))
                    userRole = roles.getString(3);
                    else if(roles.getString(0).equals(Constants.ROLE_VOUCHERS_DISTRIBUTOR))
                    userRole = roles.getString(0);
                    else if(roles.getString(0).equals(Constants.ROLE_VOUCHERS_REDEEMER))
                        userRole = roles.getString(0);
                }
                catch (JSONException e)
                {

                }
                UserPrefs.storeLoginData(userRole, context);
                Intent intent =   new Intent(getApplicationContext(), MainMenuActivity.class);

                intent.setAction("doVouchers");
                startActivity(intent);


            }
        };
    }

    private View.OnClickListener distributionButton()
    {
        return  new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userRole = "";//Constants.ROLE_KITS_ASSESSOR;
                try {
                    userRole = roles.getString(1);
                }
                catch (JSONException e)
                {

                }
                UserPrefs.storeLoginData(userRole, context);

                Intent intent =   new Intent(getApplicationContext(), MainMenuActivity.class);
                intent.setAction("doDistribution");
                startActivity(intent);


            }
        };
    }
    private Replication.ChangeListener pullChangeListener()
    {
        return new Replication.ChangeListener() {
            @Override
            public void changed(final Replication.ChangeEvent event) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                        TextView networkStatus = (TextView) findViewById(R.id.textView_networkStatus);
                        Log.e("REP8","REP8");

                        if (!mWifi.isConnected()) {
                            //   Toast.makeText(TaskSelectionActivity.this,
                            //           "NO WIFI",
                            ////           Toast.LENGTH_LONG).show();
                        }
                        Replication replication = event.getSource();

                        TextView progressText = (TextView) findViewById(R.id.textView_progress);
                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                        switch (replication.getStatus()) {
                            case REPLICATION_ACTIVE:
                                networkStatus.setText("Syncing Data...");

                                progressBar.setMax(replication.getChangesCount());
                                progressBar.setProgress(replication.getCompletedChangesCount());

                                progressText.setText(replication.getCompletedChangesCount()+"/"+replication.getChangesCount()+"");

                                break;
                            case REPLICATION_IDLE:
                                networkStatus.setText("Data Updated");


                                progressBar.setMax(replication.getChangesCount());
                                progressBar.setProgress(replication.getCompletedChangesCount());

                                progressText.setText(replication.getCompletedChangesCount()+"/"+replication.getChangesCount()+"");
                                break;
                            case REPLICATION_OFFLINE:
                                networkStatus.setText("Offline");
                                break;
                            case REPLICATION_STOPPED:
                                networkStatus.setText("Stopped");
                                break;
                        }

                    }
                });

            }
        };
    }

    private Replication.ChangeListener pushChangeListener()
    {
        return new Replication.ChangeListener() {
            @Override
            public void changed(final Replication.ChangeEvent event) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.e("REP9","REP9");

                        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                        TextView networkStatus = (TextView) findViewById(R.id.textView_networkStatus);

                        if (!mWifi.isConnected()) {
                            //   Toast.makeText(TaskSelectionActivity.this,
                            //           "NO WIFI",
                            ////           Toast.LENGTH_LONG).show();
                        }
                        Replication replication = event.getSource();

                        TextView progressText = (TextView) findViewById(R.id.textView_progressPush);
                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarPush);
                        switch (replication.getStatus()) {
                            case REPLICATION_ACTIVE:
                                networkStatus.setText("Syncing Data...");

                                progressBar.setMax(replication.getChangesCount());
                                progressBar.setProgress(replication.getCompletedChangesCount());

                                progressText.setText(replication.getCompletedChangesCount()+"/"+replication.getChangesCount()+"");

                                break;
                            case REPLICATION_IDLE:
                                networkStatus.setText("Data Updated");


                                progressBar.setMax(replication.getChangesCount());
                                progressBar.setProgress(replication.getCompletedChangesCount());

                                progressText.setText(replication.getCompletedChangesCount()+"/"+replication.getChangesCount()+"");
                                break;
                            case REPLICATION_OFFLINE:
                                networkStatus.setText("Offline");
                                break;
                            case REPLICATION_STOPPED:
                                networkStatus.setText("Stopped");
                                break;
                        }

                    }
                });

            }
        };
    }


}
