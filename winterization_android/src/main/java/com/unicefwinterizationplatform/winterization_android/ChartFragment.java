package com.unicefwinterizationplatform.winterization_android;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by Tarek on 8/20/15.
 */
public class ChartFragment extends Fragment {

    private int mPageNumber;
    public static final String ARG_PAGE = "page";
    PieChart mChart;
    BarChart barChart;

    public void setmChart(PieChart mChart) {
        this.mChart = mChart;
    }

    public void setBarChart(BarChart barChart) {
        this.barChart = barChart;
    }

    public PieChart getmChart() {

        return mChart;
    }

    public BarChart getBarChart() {
        return barChart;
    }

    public static ChartFragment create(int pageNumber) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);


        fragment.setArguments(args);
        return fragment;
    }

    public ChartFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.

        int position = getArguments().getInt(ARG_PAGE);
        ViewGroup rootView = null;
        if (position ==0) {
             rootView = (ViewGroup) inflater
                    .inflate(R.layout.fragment_pie, container, false);
            mChart = (PieChart) rootView.findViewById(R.id.chart1);
         //   mChart.setData(generatePieData(rootView));

        }
        else if (position == 1)
        {
            rootView = (ViewGroup) inflater
                    .inflate(R.layout.fragment_bar, container, false);

            barChart = (BarChart) rootView.findViewById(R.id.chart2);
           // barChart.setData(generateBarData(rootView));

        }
        // Set the title view to show the page number.

        return rootView;
    }


public void generateBarData(Map<String,Object> quant,String name,View view){



    barChart.setDrawBarShadow(false);
    barChart.setDrawValueAboveBar(true);

    barChart.setDescription("");

    barChart.setMaxVisibleValueCount(10);

    barChart.setPinchZoom(false);

    barChart.setDrawGridBackground(false);

    XAxis xAxis = barChart.getXAxis();
    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    xAxis.setDrawGridLines(false);
    xAxis.setSpaceBetweenLabels(2);


    YAxis leftAxis = barChart.getAxisLeft();
    leftAxis.setLabelCount(8, false);
    leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
    leftAxis.setSpaceTop(15f);

    YAxis rightAxis = barChart.getAxisRight();
    rightAxis.setDrawGridLines(false);
    rightAxis.setLabelCount(8, false);
    rightAxis.setSpaceTop(15f);

    Legend l = barChart.getLegend();
    l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
    l.setForm(Legend.LegendForm.SQUARE);
    l.setFormSize(9f);
    l.setTextSize(11f);
    l.setXEntrySpace(4f);


    Set<String> keySet = quant.keySet();


        ArrayList<String> xVals = new ArrayList<String>();
        for (String key : keySet) {

            if (key != "total_del" && key != "total_quant")
                xVals.add(key);

        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < xVals.size(); i++) {
            Map<String,Object> itemMap = (Map<String,Object>)quant.get(xVals.get(i));

            int del = (Integer)itemMap.get("del");

            yVals1.add(new BarEntry(del, i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "Delivered - "+ name);
        set1.setBarSpacePercent(35f);
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        // data.setValueFormatter(new MyValueFormatter());
        data.setValueTextSize(10f);

        barChart.setData(data);

    barChart.highlightValues(null);

    barChart.invalidate();

}



    public void generateBarData2(Map<String,Object> childMap,String name,View view){


        Map<String,Object> ageMap = (Map<String,Object>) childMap.get("age");


        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);

        barChart.setDescription("");

        barChart.setMaxVisibleValueCount(10);

        barChart.setPinchZoom(false);

        barChart.setDrawGridBackground(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);


        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setSpaceTop(15f);


        Legend l = barChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);


        Set<String> keySet = ageMap.keySet();


        ArrayList<String> xVals = new ArrayList<String>();
        for (String key : keySet) {
            //if (key != "total_del" && key != "total_quant")
                xVals.add(key);
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < xVals.size(); i++) {
            //Map<String,Object> itemMap = (Map<String,Object>)ageMap.get(xVals.get(i));

            int del = (Integer)ageMap.get(xVals.get(i));
            yVals1.add(new BarEntry(del, i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "Ages in - "+ name);
        set1.setBarSpacePercent(10f);
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        // data.setValueFormatter(new MyValueFormatter());
        data.setValueTextSize(10f);

        barChart.setData(data);

        barChart.highlightValues(null);

        barChart.invalidate();

    }

    protected void generatePieData2(Map<String,Object> quant,String name,View view) {


        name = name.replace(" ","\n");


        mChart.setCenterText(name);
        mChart.disableScroll();
        mChart.setDescription("");
        mChart.setTouchEnabled(false);

        ArrayList<Entry> entries1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("Del.");
        xVals.add("Rem.");


        float rem = (Integer)quant.get("total_quant") - (Integer)quant.get("total_del");
        float del = (Integer)quant.get("total_del");
        float qu = (Integer)quant.get("total_quant");


        float remPer = (rem/qu)*100;
        float delPer = (del/qu)*100;


        entries1.add(new Entry(delPer, 0));
        entries1.add(new Entry(remPer, 1));



        PieDataSet ds1 = new PieDataSet(entries1, name);
        ds1.setValueFormatter(new PercentFormatter());
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(view.getResources().getColor(R.color.Qi));
        colors.add(view.getResources().getColor(R.color.red));

        ds1.setColors(colors);
        ds1.setSliceSpace(1f);
        ds1.setValueTextColor(Color.WHITE);
        ds1.setValueTextSize(8f);

        PieData d = new PieData(xVals, ds1);

         Legend l = mChart.getLegend();
          l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
         l.setXEntrySpace(7f);
          l.setYEntrySpace(0f);
          l.setYOffset(0f);

            mChart.setData(d);

            mChart.highlightValues(null);

            mChart.invalidate();

    }


    protected void generatePieData3(Map<String,Object> childMap,String name,View view) {


        name = name.replace(" ","\n");

        Map<String,Object> genderMap = (Map<String,Object>) childMap.get("gender");

        mChart.setCenterText(name);
        mChart.disableScroll();
        mChart.setDescription("");
        mChart.setTouchEnabled(false);

        ArrayList<Entry> entries1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("Male");
        xVals.add("Female");
        float m,f;
        if (genderMap.containsKey("Boy"))
            m = (Integer)genderMap.get("Boy");
        else
            m = 0;

        if (genderMap.containsKey("Girl"))
            f = (Integer)genderMap.get("Girl");
        else
            f = 0;

        float qu = m+f;


        float malePer = m;
        float femalePer = f;


        entries1.add(new Entry(malePer, 0));
        entries1.add(new Entry(femalePer, 1));



        PieDataSet ds1 = new PieDataSet(entries1, name);
        ds1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f",value);
            }
        });
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(view.getResources().getColor(R.color.LightSkyBlue));
        colors.add(view.getResources().getColor(R.color.HotPink));

        ds1.setColors(colors);
        ds1.setSliceSpace(1f);
        ds1.setValueTextColor(Color.WHITE);
        ds1.setValueTextSize(8f);

        PieData d = new PieData(xVals, ds1);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        mChart.setData(d);

        mChart.highlightValues(null);

        mChart.invalidate();

    }


    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}



