package com.unicefwinterizationplatform.winterization_android;

/**
 * Created by Tarek on 7/15/15.
 */

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.ProgressDialog;
        import android.content.DialogInterface;
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

        import com.couchbase.lite.Document;
        import com.couchbase.lite.LiveQuery;
        import com.couchbase.lite.QueryRow;
        import com.couchbase.lite.SavedRevision;
        import com.unicefwinterizationplatform.winterization_android.RVAdapter.OnItemClickListener;
        import java.util.ArrayList;
        import java.util.Iterator;
        import java.util.List;

public class PendingTab extends Fragment {

    private RVAdapter adapter;

    private ArrayList<QueryRow> rowArrayList;

    LiveQuery liveQuery;
    String searchText = "";
    EditText searchTxt;
    ImageButton searchBtn;
    ImageButton clearBtn;
    boolean assessments;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    rowArrayList = new ArrayList<QueryRow>();
    assessments = getArguments().getBoolean("ASSESSMENTS");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pending, container, false);

        searchTxt =(EditText) v.findViewById(R.id.search_cards);
        searchBtn = (ImageButton) v.findViewById(R.id.search_button);
        clearBtn = (ImageButton) v.findViewById(R.id.cancel_button);

        RecyclerView rv = (RecyclerView) v.findViewById(R.id.rv);

        com.couchbase.lite.View byPendingItem;
        if (assessments == false)
            byPendingItem = CouchBaseManager.getInstance(v.getContext()).startPendingItemsView(v.getContext(),UserPrefs.getViewIncrement(v.getContext()));
        else
            byPendingItem = CouchBaseManager.getInstance(v.getContext()).startAssessmentsView(v.getContext(),UserPrefs.getViewIncrement(v.getContext()));



        LinearLayoutManager llm = new LinearLayoutManager(v.getContext());
        rv.setLayoutManager(llm);
        adapter = new RVAdapter(v.getContext(), rowArrayList);
        adapter.setAssessments(assessments);
        rv.setAdapter(adapter);


        startLiveQuery(byPendingItem);


        adapter.SetOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {


                if (!assessments) {
                    Intent intent = new Intent(view.getContext(), DetailedDistActivity.class);
                    QueryRow row = adapter.getItem(position);
                    DistributionObject distributionObject = CouchBaseManager.getInstance(view.getContext()).extractDist(row);

                    intent.putExtra("DISTRIBUTION", distributionObject);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(view.getContext(), BeneficiaryDetailsActivity.class);
                    QueryRow row = adapter.getItem(position);
                    intent.putExtra("DOC", row.getDocumentId());
                    intent.putExtra("ASSISTANCE", "cash");
                    startActivity(intent);
                }

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

                Log.e("TEST",count+"");

                if (count>0)
                {
                    clearBtn.setVisibility(View.VISIBLE);
                }
                else{
                    clearBtn.setVisibility(View.GONE);
                  //  clearBtn.performClick();
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        ImageButton addAssessmentBtn  = (ImageButton) v.findViewById(R.id.addAssessmentBtn);
      //  ImageButton distributionBtn  = (ImageButton) v.findViewById(R.id.DistributionBtn);

        if (assessments == true) {

            addAssessmentBtn.setVisibility(View.VISIBLE);
           // distributionBtn.setVisibility(View.VISIBLE);

            addAssessmentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Type of Settlement")
                            .setPositiveButton("Informal Settlement", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(getActivity().getApplicationContext(), PCodeSelectionActivity.class);
                                    intent.putExtra("ASSISTANCE","cash");
                                    intent.putExtra("LOC_TYPE","IS");
                                    startActivity(intent);

                                }
                            }).setNegativeButton("Collective Shelter", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(getActivity().getApplicationContext(), PCodeSelectionActivity.class);
                                        intent.putExtra("ASSISTANCE","cash");
                                        intent.putExtra("LOC_TYPE","CS");

                                        startActivity(intent);
                                    }
                                }).show();
                }
            });

//            distributionBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    Intent intent = new Intent(getActivity().getApplicationContext(), ReaderActivity.class);
//                    intent.setAction("lookup_barcode");
//                    startActivity(intent);
//                }
//            });


        }
        else
        {
            addAssessmentBtn.setVisibility(View.GONE);
           // distributionBtn.setVisibility(View.GONE);
        }

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
            liveQuery.setDescending(true);
            liveQuery.addChangeListener(liveChangeQuery());

            liveQuery.start();

        }
    }


 private LiveQuery.ChangeListener liveChangeQuery(){
     return new LiveQuery.ChangeListener() {
         @Override
         public void changed(final LiveQuery.ChangeEvent event) {
             getActivity().runOnUiThread(new Runnable() {
                 public void run() {
                     //final ProgressDialog progressDialog = showLoadingSpinner();
                     adapter.clear();
                     adapter.clear2();
                     for (Iterator<QueryRow> it = event.getRows(); it.hasNext(); ) {
                         QueryRow row = it.next();

                            adapter.addItem(row);
                            adapter.addSearchItem(row);

                     }
                     adapter.notifyDataSetChanged();
                     //progressDialog.dismiss();
                 }
             });
         }
     };
 }




    private ProgressDialog showLoadingSpinner() {
        ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();
        return progress;
    }

}