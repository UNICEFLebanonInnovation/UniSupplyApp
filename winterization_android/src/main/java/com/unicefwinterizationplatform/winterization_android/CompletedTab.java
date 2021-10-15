package com.unicefwinterizationplatform.winterization_android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
public class CompletedTab extends Fragment {



    private RVAdapter adapter;

    private ArrayList<QueryRow> rowArrayList;

    LiveQuery liveQuery;

    EditText searchTxt;
    ImageButton searchBtn;
    ImageButton clearBtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rowArrayList = new ArrayList<QueryRow>();


    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pending, container, false);

        searchTxt =(EditText) v.findViewById(R.id.search_cards);
        searchBtn = (ImageButton) v.findViewById(R.id.search_button);
        clearBtn = (ImageButton) v.findViewById(R.id.cancel_button);

        RecyclerView rv = (RecyclerView) v.findViewById(R.id.rv);


        com.couchbase.lite.View byPendingItem = CouchBaseManager.getInstance(v.getContext()).startCompletedItemsView(v.getContext(),UserPrefs.getViewIncrement(v.getContext()));

        LinearLayoutManager llm = new LinearLayoutManager(v.getContext());
        rv.setLayoutManager(llm);
        adapter = new RVAdapter(v.getContext(), rowArrayList);
        rv.setAdapter(adapter);

        startLiveQuery(byPendingItem);

        adapter.SetOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent(view.getContext(), DetailedDistActivity.class);
                QueryRow row = adapter.getItem(position);
                DistributionObject distributionObject = CouchBaseManager.getInstance(view.getContext()).extractDist(row);


                intent.putExtra("DISTRIBUTION", distributionObject);
                startActivity(intent);

            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                        "Loading. Please wait...", true);
                dialog.show();
                adapter.doSearch(searchTxt.getText().toString());
                dialog.hide();

            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                        "Loading. Please wait...", true);
                dialog.show();
                searchTxt.setText("");
                clearBtn.setVisibility(View.GONE);
                adapter.doSearch("");
                dialog.hide();

            }
        });

        searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count > 0) {
                    clearBtn.setVisibility(View.VISIBLE);
                } else {
//                    clearBtn.performClick();
                    clearBtn.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        ImageButton addAssessmentBtn  = (ImageButton) v.findViewById(R.id.addAssessmentBtn);
        addAssessmentBtn.setVisibility(View.GONE);
        ImageButton distributionBtn  = (ImageButton) v.findViewById(R.id.DistributionBtn);
        distributionBtn.setVisibility(View.GONE);

        return v;
    }




    private void startLiveQuery(com.couchbase.lite.View view)
    {

        if (liveQuery == null) {

            liveQuery = view.createQuery().toLiveQuery();
            liveQuery.setDescending(true);
            liveQuery.addChangeListener(new LiveQuery.ChangeListener() {
                public void changed(final LiveQuery.ChangeEvent event) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                          //  final ProgressDialog progressDialog = showLoadingSpinner();
                            adapter.clear();
                            adapter.clear2();

                            for (Iterator<QueryRow> it = event.getRows(); it.hasNext(); ) {
                                QueryRow row = it.next();

                                adapter.addItem(row);
                                adapter.addSearchItem(row);
                            }
                            adapter.notifyDataSetChanged();
                          //  progressDialog.dismiss();
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
