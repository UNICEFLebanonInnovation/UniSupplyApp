package com.unicefwinterizationplatform.winterization_android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.QueryRow;
import com.unicefwinterizationplatform.winterization_android.RVAdapter.OnItemClickListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Tarek on 7/20/15.
 */
public class HeadTab extends Fragment
{
    private RVAdapter adapter;

    private ArrayList<QueryRow> rowArrayList;

    LiveQuery liveQuery;

    protected View v;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rowArrayList = new ArrayList<QueryRow>();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pending, container, false);



        RecyclerView rv = (RecyclerView) v.findViewById(R.id.rv);


        //com.couchbase.lite.View byPendingItem = CouchBaseManager.getInstance(v.getContext()).startCompletedItemsView();

        LinearLayoutManager llm = new LinearLayoutManager(v.getContext());
        rv.setLayoutManager(llm);
        adapter = new RVAdapter(v.getContext(), rowArrayList);
        rv.setAdapter(adapter);

       // startLiveQuery(byPendingItem);

        adapter.SetOnItemClickListener(new RVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Toast.makeText(view.getContext(), "TAREK " + String.valueOf(position), Toast.LENGTH_LONG).show();
            }
        });
        return v;
    }


    protected void startLiveQuery(com.couchbase.lite.View view)
    {

        if (liveQuery == null) {

            liveQuery = view.createQuery().toLiveQuery();

            liveQuery.addChangeListener(new LiveQuery.ChangeListener() {
                public void changed(final LiveQuery.ChangeEvent event) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            final ProgressDialog progressDialog = showLoadingSpinner();
                            adapter.clear();
                            for (Iterator<QueryRow> it = event.getRows(); it.hasNext(); ) {
                                adapter.addItem(it.next());
                            }
                            adapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        }
                    });
                }
            });

            liveQuery.start();

        }
    }


    private ProgressDialog showLoadingSpinner() {
        ProgressDialog progress = new ProgressDialog(getView().getContext());
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();
        return progress;
    }
}
