package com.unicefwinterizationplatform.winterization_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Tarek on 2/3/16.
 */


public class DailyLogsActivity extends HeaderActivity{

    DailyLogsAdapter adapter;
    LiveQuery liveQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);


        RecyclerView rv = (RecyclerView) findViewById(R.id.logView);



        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(llm);
        ArrayList<QueryRow> arr = new ArrayList<QueryRow>();
        adapter = new DailyLogsAdapter(getApplicationContext(), arr);
        rv.setAdapter(adapter);

        com.couchbase.lite.View byLogsItem = CouchBaseManager.getInstance(getApplicationContext()).startLogItemsView(getApplicationContext(), UserPrefs.getViewIncrement(getApplicationContext()));


        startLiveQuery(byLogsItem);

    }

    private void startLiveQuery(com.couchbase.lite.View view)
    {

        if (liveQuery == null) {

            liveQuery = view.createQuery().toLiveQuery();
            liveQuery.setDescending(true);
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
                        for (Iterator<QueryRow> it = event.getRows(); it.hasNext(); ) {
                            QueryRow row = it.next();
                            adapter.addItem(row);
                        }
                        adapter.notifyDataSetChanged();
                        //progressDialog.dismiss();
                    }
                });
            }
        };
    }
}
