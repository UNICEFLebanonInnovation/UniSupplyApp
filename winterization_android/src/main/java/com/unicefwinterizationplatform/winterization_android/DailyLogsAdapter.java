package com.unicefwinterizationplatform.winterization_android;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.couchbase.lite.Document;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.SavedRevision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Tarek on 2/3/16.
 */
public class DailyLogsAdapter extends RecyclerView.Adapter<DailyLogsAdapter.DocViewHolder> {

    OnItemClickListener mItemClickListener;
    private ArrayList<QueryRow> list;
    private final Context context;
    private boolean assessments;

    DailyLogsAdapter (Context context,ArrayList<QueryRow> list){
        this.list = list;
        this.context = context;
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    public void clear()
    {
        list.clear();
    }

    public void clear2()
    {
        list.clear();
    }

    public void addItem(QueryRow item) {
        list.add(item);
    }

    public QueryRow getItem(int i)
    {
        return list.get(i);
    }


        @Override
    public DocViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);


        DocViewHolder dvh = new DocViewHolder(v,assessments);
        return dvh;
    }

    @Override
    public void onBindViewHolder(DocViewHolder holder, int position) {



        QueryRow row = list.get(position);

        SavedRevision currentRevision = row.getDocument().getCurrentRevision();

         holder.dateName.setText(currentRevision.getProperty("date").toString());

        ArrayList<Map<String,Object>> logs = (ArrayList<Map<String,Object>>)currentRevision.getProperty("logs");
        Collections.reverse(logs);
        for (Map<String,Object> log : logs)
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.
                    WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 5, 5, 5);
            TextView logText = new TextView(context);
            logText.setLayoutParams(params);
            logText.setTextColor(context.getResources().getColor(R.color.black));
            String text = "- <b><font color=\"#1399de\">[" + log.get("time") + " " + log.get("location") + "]</font>: </b> "+log.get("amount")+" of "+log.get("item_type")+" delivered" ;
            logText.setText(Html.fromHtml(text));
            logText.setTextSize(15);
            holder.lin.addView(logText);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public  class DocViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        TextView dateName;
        LinearLayout lin;

        DocViewHolder(View itemView,boolean assessements) {

            super(itemView);
                cv = (CardView) itemView.findViewById(R.id.cv);
                dateName = (TextView) itemView.findViewById(R.id.date);
                lin = (LinearLayout) itemView.findViewById(R.id.lin);


                itemView.setOnClickListener(this);
                  }


        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getPosition());
            }
        }
    }




    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
    }

}



