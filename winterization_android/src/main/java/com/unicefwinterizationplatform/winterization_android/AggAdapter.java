package com.unicefwinterizationplatform.winterization_android;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Tarek on 8/14/15.
 */
public class AggAdapter  extends RecyclerView.Adapter<AggAdapter.DocViewHolder>{

    //List<Person> persons;
    OnItemClickListener mItemClickListener;
    private  Map<String,Object> list;
    boolean isAssessment;



    private SparseBooleanArray selectedItems;
    private Object[] mKeys;


    private final Context context;


    public void setSelectedItems(SparseBooleanArray selectedItems) {
        this.selectedItems = selectedItems;

    }

    public SparseBooleanArray getSelectedItems() {
        return selectedItems;
    }

    AggAdapter(Context context,Map<String,Object> list){
        this.list = list;
        mKeys = this.list.keySet().toArray(new String[list.size()]);
        this.context = context;
        selectedItems = new SparseBooleanArray();
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    public void clear()
    {
        list.clear();
    }

    public void setList(Map<String,Object> list)
    {
        this.list = list;
        mKeys = this.list.keySet().toArray(new String[list.size()]);
        notifyDataSetChanged();

    }

    public Object getItem(int i)
    {

        return list.get(mKeys[i]);
    }


    public boolean isAssessment() {
        return isAssessment;
    }

    public void setIsAssessment(boolean isAssessment) {
        this.isAssessment = isAssessment;
    }

    public Object getName(int i)
    {
        return mKeys[i];
    }



    @Override
    public DocViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (isAssessment())
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.agg_item, parent, false);
        else
             v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item, parent, false);
        DocViewHolder dvh = new DocViewHolder(v,isAssessment());
        return dvh;
    }

    @Override
    public void onBindViewHolder(DocViewHolder holder, int position) {

        if (!isAssessment()) {
            Map<String, Object> quantMap = (Map<String, Object>) list.get(mKeys[position]);

            int quantity = (Integer) quantMap.get("total_quant");

            int delivered = (Integer) quantMap.get("total_del");

            int remaining = quantity - delivered;

            holder.nameView.setText((String)mKeys[position]);
            holder.quantity.setText("Quantity: " + quantity);
            holder.delivered.setText("Delivered: " + delivered);
            holder.remaining.setText("Remaining: " + remaining);

            holder.progBar.setMax(quantity);
            holder.progBar.setProgress(delivered);


            float fl = ((float) delivered / (float) quantity) * 100;

            if (fl <= 34) {
                holder.progBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.progdraw_red));
            } else if (fl > 34 && fl <= 66) {
                holder.progBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.progdraw_orange));
            } else if (fl > 66) {
                holder.progBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.progdraw_green));
            }

            holder.lin.setSelected(selectedItems.get(position, false));

        }

        else
        {
            Map<String, Object> childMap = (Map<String, Object>) list.get(mKeys[position]);

            //Map<String,Object> locMap = (Map<String,Object>)mKeys[position];

            holder.locName.setText((String)mKeys[position]);



            Map<String, Object> genderMap = (Map<String,Object>)childMap.get("gender");


        //if (genderMap != null) {

            int m,f;
            if (genderMap.containsKey("Boy"))
                m = (Integer)genderMap.get("Boy");
            else
                m = 0;

            if (genderMap.containsKey("Girl"))
                f = (Integer)genderMap.get("Girl");
            else
                f = 0;

            int sum = m + f;
            holder.childCount.setText("Number of Children: " + sum + "");

            Map<String, Object> offIdMap = (Map<String,Object>)childMap.get("official_id");

            holder.assessmentCount.setText("Number of Assessments: " + offIdMap.size() + "");


            //  }


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

         LinearLayout lin;
         TextView nameView;
         TextView quantity;
         TextView delivered;
         TextView remaining;
         ProgressBar progBar;

         TextView locName;
         TextView childCount;
         TextView assessmentCount;



        DocViewHolder(View view,boolean isAssessment) {
            super(view);
            if (isAssessment){
                lin = (LinearLayout) view.findViewWithTag("lin");
                locName = (TextView) view.findViewWithTag("loc_name");
                assessmentCount = (TextView) view.findViewWithTag("assess_count");
                childCount = (TextView) view.findViewWithTag("child_count");
            }
            else {
                lin = (LinearLayout) view.findViewWithTag("lin");
                nameView = (TextView) view.findViewWithTag("name");
                remaining = (TextView) view.findViewWithTag("remaining");
                quantity = (TextView) view.findViewWithTag("quantity");
                delivered = (TextView) view.findViewWithTag("delivered");
                progBar = (ProgressBar) view.findViewWithTag("amount_left");
            }
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view,lin,getPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view ,LinearLayout lin,int position);
    }
}
