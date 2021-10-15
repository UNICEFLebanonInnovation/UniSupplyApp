package com.unicefwinterizationplatform.winterization_android;

import android.app.Activity;
import android.content.Intent;
import android.nfc.cardemulation.CardEmulation;
import android.support.v4.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.QueryRow;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.PercentFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by Tarek on 8/14/15.
 */
public class DataTab extends Fragment{


    private static final int NUM_PAGES = 2;

    LiveQuery liveQuery;
    boolean isAssessment;
    Map<String,Object> aggData;
    AggAdapter adapter;
     String selectedName;
     Map<String,Object> selectedQuant;
    ViewPager mPager;
    LinearLayout selectedLayout;
    private ScreenSlidePagerAdapter mPagerAdapter;
    ArrayList <ChartFragment> chartFragments;

    public String getSelectedName() {
        return selectedName;
    }

    public Map<String, Object> getSelectedQuant() {
        return selectedQuant;
    }

    public void setSelectedName(String selectedName) {
        this.selectedName = selectedName;
    }

    public void setSelectedQuant(Map<String, Object> selectedQuant) {
        this.selectedQuant = selectedQuant;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isAssessment = getArguments().getBoolean("ASSESSMENTS");

        aggData = new HashMap<String, Object>();
        chartFragments = new ArrayList<ChartFragment>();

            chartFragments.add(ChartFragment.create(0));
            chartFragments.add(ChartFragment.create(1));

    }


    @Override
    public void onStart() {
        super.onStart();

            if (getSelectedName() != null && getSelectedQuant() != null) {

                if(!isAssessment) {
                    ChartFragment pieChart = mPagerAdapter.getItem(0);

                    pieChart.generatePieData2(selectedQuant, selectedName, getView());
                    mPagerAdapter.setItem(pieChart, 0);
                    mPagerAdapter.notifyDataSetChanged();

                    ChartFragment barChart = mPagerAdapter.getItem(1);
                    barChart.generateBarData(selectedQuant, selectedName, getView());
                    mPagerAdapter.setItem(barChart, 1);
                    mPagerAdapter.notifyDataSetChanged();
                }
                else
                {
                    ChartFragment pieChart = mPagerAdapter.getItem(0);

                    pieChart.generatePieData3(selectedQuant, selectedName, getView());
                    mPagerAdapter.setItem(pieChart, 0);
                    mPagerAdapter.notifyDataSetChanged();

                    ChartFragment barChart = mPagerAdapter.getItem(1);
                    barChart.generateBarData2(selectedQuant, selectedName, getView());
                    mPagerAdapter.setItem(barChart, 1);
                    mPagerAdapter.notifyDataSetChanged();
                }
            }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_data, container, false);

        com.couchbase.lite.View byAggItem = null;

        if (!isAssessment)
            byAggItem = CouchBaseManager.getInstance(v.getContext()).startAggrigatedItemsView(v.getContext(), UserPrefs.getViewIncrement(v.getContext()));
        else
            byAggItem = CouchBaseManager.getInstance(v.getContext()).startAggrigatedItemsViewforAssessments(v.getContext(), UserPrefs.getViewIncrement(v.getContext()));

        startLiveQuery(byAggItem);

        RecyclerView rv = (RecyclerView) v.findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(v.getContext());
        rv.setLayoutManager(llm);


        adapter = new AggAdapter(v.getContext(), aggData);
        adapter.setIsAssessment(this.isAssessment);
        rv.setAdapter(adapter);

//
//        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                Log.e("SCROLL2", newState + "");
//
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                //Log.e("SCROLL",dy+"");
//            }
//        });

        mPager = (ViewPager) v.findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPagerAdapter.setList(chartFragments);
        mPager.setAdapter(mPagerAdapter);


        adapter.SetOnItemClickListener(new AggAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, LinearLayout lin, int position) {
                String name = (String) adapter.getName(position);
                Map<String, Object> quant = (Map<String, Object>) adapter.getItem(position);

                name = name.replace(" ", "\n");
                setSelectedName(name);
                setSelectedQuant(quant);


                if (!isAssessment) {
                    ChartFragment pieChart = mPagerAdapter.getItem(0);

                    pieChart.generatePieData2(quant, name, getView());

                    mPagerAdapter.setItem(pieChart, 0);
                    mPagerAdapter.notifyDataSetChanged();

                    ChartFragment barChart = (ChartFragment) mPagerAdapter.getItem(1);
                    barChart.generateBarData(quant, name, getView());
                    mPagerAdapter.setItem(barChart, 1);
                    mPagerAdapter.notifyDataSetChanged();

                } else {
                    ChartFragment pieChart = mPagerAdapter.getItem(0);

                    pieChart.generatePieData3(quant, name, getView());

                    mPagerAdapter.setItem(pieChart, 0);
                    mPagerAdapter.notifyDataSetChanged();

                    ChartFragment barChart = (ChartFragment) mPagerAdapter.getItem(1);
                    barChart.generateBarData2(quant, name, getView());
                    mPagerAdapter.setItem(barChart, 1);
                    mPagerAdapter.notifyDataSetChanged();
                }


                if (selectedLayout != null) {
                    selectedLayout.setSelected(false);
                }

                //if (adapter.getSelectedItems().get(position, false)) {
                adapter.getSelectedItems().delete(position);

                //       lin.setSelected(false);
                //   }
                //  else {
                adapter.getSelectedItems().put(position, true);
                lin.setSelected(true);
                selectedLayout = lin;
                //  }


            }
        });


        ImageButton logsBtn = (ImageButton)v.findViewById(R.id.logBtn);

        logsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),DailyLogsActivity.class);
                startActivity(intent);

            }
        });



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
                           aggData =  (Map<String,Object>)row.getValue();
                           adapter.setList(aggData);


                        }

                    }
                });
            }
        };




    }




    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<ChartFragment> chartFragments;
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public ChartFragment getItem(int position) {
            return chartFragments.get(position);
        }


        public void setItem(ChartFragment chartFragment,int position) {
             chartFragments.set(position,chartFragment);
            notifyDataSetChanged();
            notifyDataSetChanged();


        }
        public void setList(ArrayList<ChartFragment> chartFragments){
            this.chartFragments =chartFragments;
            notifyDataSetChanged();

        }



        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }


}
