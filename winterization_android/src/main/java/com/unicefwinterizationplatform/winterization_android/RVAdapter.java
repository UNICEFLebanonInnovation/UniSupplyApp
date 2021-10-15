package com.unicefwinterizationplatform.winterization_android;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.couchbase.lite.Document;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.SavedRevision;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Tarek on 7/16/15.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.DocViewHolder>{

    //List<Person> persons;
    OnItemClickListener mItemClickListener;
    private ArrayList<QueryRow> list;
    private ArrayList<QueryRow> searchList;
    private final Context context;
    private boolean assessments;

    RVAdapter(Context context,ArrayList<QueryRow> list){
        this.list = list;
        this.searchList = new ArrayList<QueryRow>(this.list);
        this.context = context;
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    public void clear()
    {
        searchList.clear();
    }

    public void clear2()
    {
        list.clear();
    }

    public void addItem(QueryRow item) {
        list.add(item);
    }
    public void addSearchItem(QueryRow item) {
        searchList.add(item);
    }

    public QueryRow getItem(int i)
    {
        return searchList.get(i);
    }

    public boolean doSearch(String search){

        clear();

        for (Iterator<QueryRow> it = list.iterator(); it.hasNext(); ) {
            QueryRow row = it.next();

            Document doc = row.getDocument();
            SavedRevision rev = doc.getCurrentRevision();

           // String res = (String)rev.getProperty("");
            String res;
            String res2;
            String res3;

              if(!assessments) {
                Map<String, Object> arr = (Map<String, Object>) rev.getProperty("location");
                res = (String) arr.get("p_code_name");
                ArrayList<Map<String, Object>> arr2 = ( ArrayList<Map<String, Object>> ) rev.getProperty("item_list");
                res2 =  arr2.get(0).get("item_type").toString();
                res3= (String) rev.getProperty("intervention");
             }
            else
            {
                res = (String) rev.getProperty("official_id");
                Map<String, Object> arr = (Map<String, Object>) rev.getProperty("location");
                res2 = (String) arr.get("p_code_name");
                res3 = (String) arr.get("p_code");
            }

            if (search.isEmpty())
            {
                addSearchItem(row);
            }
            else if (res.toLowerCase().contains(search.toLowerCase())) {
                addSearchItem(row);
            }
            else if (res2.toLowerCase().contains(search.toLowerCase())) {
                addSearchItem(row);
            }
            else if (res3.toLowerCase().contains(search.toLowerCase())) {
                addSearchItem(row);
            }
        }

        notifyDataSetChanged();
        return true;
    }


    @Override
    public DocViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (!assessments) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        }
        else
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.assess_item, parent, false);

        DocViewHolder dvh = new DocViewHolder(v,assessments);
        return dvh;
    }

    @Override
    public void onBindViewHolder(DocViewHolder holder, int position) {



        QueryRow row = searchList.get(position);

        SavedRevision currentRevision = row.getDocument().getCurrentRevision();

        if (!assessments) {

            String creationDateStr = (String) currentRevision.getProperty("creation_date");
            String[] arr = creationDateStr.split("T");
            holder.creationDate.setText(arr[0]);

            Boolean isComplete= (Boolean) currentRevision.getProperty("completed");

            int totalLeft = 0;
            int totalQuant = 0;

            ArrayList<Map<String, Object>> items = (ArrayList<Map<String, Object>>) currentRevision.getProperty("item_list");
            String itemName = "";
            for (Map<String, Object> item : items) {

                int i = 0;

                if (item.get("delivered") != null)
                    i = (Integer) item.get("delivered");
                else
                    i = 0;
                int j = (Integer) item.get("quantity");
                totalLeft += i;
                totalQuant += j;
                itemName = (String) item.get("item_type");
            }


            holder.itemName.setText(itemName);
            // holder.quantityLeft.setText("Items Left: "+ totalLeft+ "");
            holder.quantityLeft.setMax(totalQuant);
            holder.quantityLeft.setProgress(totalLeft);

            // holder.quantityLeft.set(context.getResources().getColor(R.color.red));

            float fl = ((float) totalLeft / (float) totalQuant) * 100;
            if(fl == 100) {
                holder.percentage.setText(String.format("%.0f%%", fl));
            }
            else{
                holder.percentage.setText(String.format("%.01f%%", fl));

            }

            if (fl <= 34) {
                holder.quantityLeft.setProgressDrawable(context.getResources().getDrawable(R.drawable.progdraw_red));
            } else if (fl > 34 && fl <= 66) {
                holder.quantityLeft.setProgressDrawable(context.getResources().getDrawable(R.drawable.progdraw_orange));
            } else if (fl > 66) {
                holder.quantityLeft.setProgressDrawable(context.getResources().getDrawable(R.drawable.progdraw_green));
            }

            if (isComplete && totalLeft != totalQuant)
            {
                holder.forceImg.setVisibility(View.VISIBLE);
            }
            else{
                holder.forceImg.setVisibility(View.GONE);

            }




            Map<String, Object> location = (Map<String, Object>) currentRevision.getProperty("location");
            Map<String, Object> locationParent;
            Map<String, Object> locationParent1;

            if (location != null) {

                String locationName = (String) location.get("p_code_name");
                holder.locationName.setText(locationName);
                holder.locationPlace.setText("N/A");

                if (location.get("parent") != null) {

                    locationParent = (Map<String, Object>) location.get("parent");
                    //String locationName = (String) locationParent.get("p_code_name");
                    // holder.locationPlace.setText(locationName);
                    String locationName1 = (String) locationParent.get("p_code_name");

                    holder.locationPlace.setText(locationName1);


                    if (locationParent.get("parent") != null) {

                        locationParent1 = (Map<String, Object>) locationParent.get("parent");

                        // String locationName1 = (String) locationParent1.get("p_code_name");

                        //   holder.locationPlace.setText(locationName + " - " + locationName1);

                    }
                }

            }

            String locationType = (String) location.get("location_type");


            if (locationType.equals("school")) {
                holder.typeImg.setImageResource(R.drawable.education);
            } else {
                holder.typeImg.setImageResource(R.drawable.health);
            }

            String intervention = "";
            if ((String)currentRevision.getProperty("intervention") != null )
                intervention = (String)currentRevision.getProperty("intervention");


            holder.interventionName.setText(intervention);

            //Log.d("HEIGHT",holder.cv.getMeasuredHeight()+"");
            // holder.criticality.getLayoutParams().height = holder.cv.getHeight() -10; //setMinimumHeight(holder.cv.getHeight());

            holder.criticality.setBackgroundColor(context.getResources().getColor(R.color.Qi));
        }
        else
        {
            holder.typeImg.setImageResource(R.drawable.boy);
            holder.typeImg2.setImageResource(R.drawable.girl);

            String creationDateStr = (String) currentRevision.getProperty("creation_date");
            String[] arr = creationDateStr.split("T");
            holder.creationDate.setText(arr[0]);

            holder.docName.setText((String)currentRevision.getProperty("official_id"));

            Map<String, Object> location = (Map<String, Object>) currentRevision.getProperty("location");

            ArrayList<?> childList = (ArrayList<?>) currentRevision.getProperty("child_list");

            holder.numOfChildren.setText("Number of Children: "+ childList.size());

            String locationName = (String) location.get("p_code_name");

            holder.locationPlace.setText(locationName);



        }
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public  class DocViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        TextView locationName;
        ProgressBar quantityLeft;
        ImageView typeImg;
        ImageView typeImg2;
        ImageView forceImg;


        TextView percentage;
        TextView creationDate;
        TextView itemName;
        View criticality;
        TextView locationPlace;
        TextView interventionName;

        TextView docName;
        TextView numOfChildren;

        DocViewHolder(View itemView,boolean assessements) {

            super(itemView);
            if (!assessements) {
                cv = (CardView) itemView.findViewById(R.id.cv);
                locationName = (TextView) itemView.findViewById(R.id.doc_name);
                quantityLeft = (ProgressBar) itemView.findViewById(R.id.amount_left);
                typeImg = (ImageView) itemView.findViewById(R.id.item_photo);
                criticality = (View) itemView.findViewById(R.id.critical);
                percentage = (TextView) itemView.findViewById(R.id.percentage);
                locationPlace = (TextView) itemView.findViewById(R.id.location_name);
                itemName = (TextView) itemView.findViewById(R.id.item_name);
                creationDate = (TextView) itemView.findViewById(R.id.creation_date);
                interventionName = (TextView) itemView.findViewById(R.id.intervention_name);
                forceImg = (ImageView) itemView.findViewById(R.id.item_forced);


                itemView.setOnClickListener(this);
            }
            else
            {
                cv = (CardView)itemView.findViewById(R.id.cv);
                docName = (TextView)itemView.findViewById(R.id.doc_name);
                typeImg = (ImageView)itemView.findViewById(R.id.item_photo);
                typeImg2 = (ImageView)itemView.findViewById(R.id.item_photo2);

                locationPlace = (TextView) itemView.findViewById(R.id.location_name);
                creationDate = (TextView)  itemView.findViewById(R.id.creation_date);
                numOfChildren = (TextView) itemView.findViewById(R.id.numberOfKids);

                itemView.setOnClickListener(this);

            }
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


    public boolean isAssessments() {
        return assessments;
    }

    public void setAssessments(boolean assessments) {
        this.assessments = assessments;
    }
}
