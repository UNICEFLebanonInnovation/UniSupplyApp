package com.unicefwinterizationplatform.winterization_android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.replicator.Replication;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Tarek on 8/7/15.
 */
public class MainTab extends Fragment {

    LiveQuery liveQuery;

    TextView networkStatus,progressText,progressTextPush;
    ProgressBar progressBar,progressBarPush;
    View gpsView;
    BroadcastReceiver reciever;
    Location location;
    TextView accuracyView;
   // private PieChart mChart;
   // TextView remainingText;
    int remaining = 0;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       final View v = inflater.inflate(R.layout.fragment_main, container, false);



        getActivity().registerReceiver(reciever, new IntentFilter(LocationService.GPS_ACCURACY));

        gpsView= v.findViewById(R.id.ImageView_GPS_Status);
        accuracyView = (TextView) v.findViewById(R.id.text_signal);



        reciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                location = intent.getParcelableExtra("LOCATION");


                if(location.getAccuracy() >0 && location.getAccuracy() <16) {
                    gpsView.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
                    accuracyView.setText("Weak.");
                }
                else if(location.getAccuracy()>=16 && location.getAccuracy()<64)
                {
                    gpsView.setBackgroundColor(getActivity().getResources().getColor(R.color.Orange));
                    accuracyView.setText("Average.");

                }
                else if(location.getAccuracy()>=64 )
                {
                    gpsView.setBackgroundColor(getActivity().getResources().getColor(R.color.Qi));
                    accuracyView.setText("Strong.");
                }

            }


        };


        Intent loc = new Intent(v.getContext(), LocationService.class);
        loc.setAction("startListening");
        getActivity().startService(loc);

        CouchBaseManager.getInstance(v.getContext()).pullReplication.addChangeListener(pullChangeListener());
        CouchBaseManager.getInstance(v.getContext()).pushReplication.addChangeListener(pushChangeListener());

         progressText = (TextView) v.findViewById(R.id.textView_progress);
         progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        int tot  =   CouchBaseManager.getInstance(v.getContext()).getTotalPullDocs();
        int comp = CouchBaseManager.getInstance(v.getContext()).getPullDocs();

        progressText.setText(comp +"/"+tot +"");
        progressBar.setMax(tot);
        progressBar.setProgress(comp);

         progressTextPush = (TextView) v.findViewById(R.id.textView_progressPush);
         progressBarPush = (ProgressBar) v.findViewById(R.id.progressBarPush);
        int tot1  =   CouchBaseManager.getInstance(v.getContext()).getTotalPullDocs();
        int comp1 = CouchBaseManager.getInstance(v.getContext()).getPullDocs();

        progressTextPush.setText(comp1 +"/"+tot1 +"");
        progressBarPush.setMax(tot1);
        progressBarPush.setProgress(comp1);
        //TextView networkStatus = (TextView) findViewById(R.id.textView_networkStatus);

       // ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

//        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        networkStatus = (TextView) v.findViewById(R.id.textView_networkStatus);
        networkStatus.setText(CouchBaseManager.getInstance(v.getContext()).netStatus);

       // if (!mWifi.isConnected()) {
     //       networkStatus.setText("Offline, please check network connection.");
     //   }



        Button forceSync = (Button)v.findViewById(R.id.button_forceSync);
        forceSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CouchBaseManager.getInstance(view.getContext()).stopReplication();

               Map<String,String> prof =  UserPrefs.getUserProfile(view.getContext());

                String user = prof.get("user");

                try {
                    String org = UserPrefs.getOrganisation(getActivity());
                    CouchBaseManager.getInstance(view.getContext()).startCBLite(org);
                }
                catch (Exception e){}

                CouchBaseManager.getInstance(view.getContext()).pullReplication.addChangeListener(pullChangeListener());
                CouchBaseManager.getInstance(view.getContext()).pushReplication.addChangeListener(pushChangeListener());
            }
        });


        TextView partnerText = (TextView)v.findViewById(R.id.textView_partnerName);
        partnerText.setText("Partner Name: "+ UserPrefs.getOrganisation(v.getContext()));

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void startLiveQuery(com.couchbase.lite.View view)
    {

        if (liveQuery == null) {

            liveQuery = view.createQuery().toLiveQuery();

            liveQuery.addChangeListener(liveChangeQuery());

            liveQuery.start();

        }
    }

    //FOR PIE CHART IN CASE WE REUSE IT.
  /*  protected PieData generatePieData() {

        int count = 16;

        ArrayList<Entry> entries1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("Quarter 1");
        xVals.add("Quarter 2");
        xVals.add("Quarter 3");
        xVals.add("Quarter 4");
        xVals.add("Quarter 5");
        xVals.add("Quarter 6");
        xVals.add("Quarter 7");
        xVals.add("Quarter 8");
        xVals.add("Quarter 9");
        xVals.add("Quarter 10");
        xVals.add("Quarter 11");
        xVals.add("Quarter 12");
        xVals.add("Quarter 13");
        xVals.add("Quarter 14");
        xVals.add("Quarter 15");
        xVals.add("Quarter 16");


        for(int i = 0; i < count; i++) {
            xVals.add("entry" + (i+1));

            entries1.add(new Entry((float) (Math.random() * 60) + 40, i));
        }

        PieDataSet ds1 = new PieDataSet(entries1, "Remaining");
        ds1.setColors(ColorTemplate.PASTEL_COLORS);
        ds1.setSliceSpace(1f);
        ds1.setValueTextColor(Color.WHITE);
        ds1.setValueTextSize(8f);

        PieData d = new PieData(xVals, ds1);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        return d;


    }*/


    private LiveQuery.ChangeListener liveChangeQuery(){
        return new LiveQuery.ChangeListener() {
            @Override
            public void changed(final LiveQuery.ChangeEvent event) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        //final ProgressDialog progressDialog = showLoadingSpinner();
                        //adapter.clear();

                        for (Iterator<QueryRow> it = event.getRows(); it.hasNext(); ) {
                            QueryRow row = it.next();
                            ArrayList<Integer> rd =  (ArrayList<Integer>) row.getValue();

                            Integer rem = rd.get(1) - rd.get(0);
                            remaining = rem;
                       //     remainingText.setText(remaining+"");
                        }

                    }
                });
            }
        };
    }


    private Replication.ChangeListener pullChangeListener()
    {
        return new Replication.ChangeListener() {
            @Override
            public void changed(final Replication.ChangeEvent event) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //   ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        //   NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                        //      if (!mWifi.isConnected()) {
                        //   Toast.makeText(TaskSelectionActivity.this,
                        //           "NO WIFI",
                        ////           Toast.LENGTH_LONG).show();

                        //       }

                        Log.e("REP6","REP6");

                        Replication replication = event.getSource();



                        switch (replication.getStatus()) {
                            case REPLICATION_ACTIVE:
                                networkStatus.setText("Syncing Data...");

                                progressBar.setMax(replication.getChangesCount());
                                progressBar.setProgress(replication.getCompletedChangesCount());

                                progressText.setText(replication.getCompletedChangesCount() + "/" + replication.getChangesCount() + "");

                                break;
                            case REPLICATION_IDLE:
                                networkStatus.setText("Data Updated");
                                progressBar.setMax(replication.getChangesCount());
                                progressBar.setProgress(replication.getCompletedChangesCount());

                                progressText.setText(replication.getCompletedChangesCount() + "/" + replication.getChangesCount() + "");
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        //  NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                        //    if (!mWifi.isConnected()) {
                        //   Toast.makeText(TaskSelectionActivity.this,
                        //           "NO WIFI",
                        ////           Toast.LENGTH_LONG).show();
                        //     }

                        Log.e("REP","REP7");

                        Replication replication = event.getSource();

                        switch (replication.getStatus()) {
                            case REPLICATION_ACTIVE:
                                networkStatus.setText("Syncing Data...");

                                progressBar.setMax(replication.getChangesCount());
                                progressBar.setProgress(replication.getCompletedChangesCount());

                                progressText.setText(replication.getCompletedChangesCount() + "/" + replication.getChangesCount() + "");

                                break;
                            case REPLICATION_IDLE:
                                networkStatus.setText("Data Updated");


                                progressBar.setMax(replication.getChangesCount());
                                progressBar.setProgress(replication.getCompletedChangesCount());

                                progressText.setText(replication.getCompletedChangesCount() + "/" + replication.getChangesCount() + "");
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


    @Override
    public void onStop() {
        Intent loc = new Intent(getView().getContext(), LocationService.class);

        getActivity().stopService(loc);
        super.onDestroy();
    }


}
