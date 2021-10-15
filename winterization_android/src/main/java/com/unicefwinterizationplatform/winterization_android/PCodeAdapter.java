package com.unicefwinterizationplatform.winterization_android;

import android.content.Context;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.couchbase.lite.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Filter;

/**
 * Created by Tarek on 10/1/2014.
 */
public class PCodeAdapter extends BaseAdapter implements Filterable,SectionIndexer {



    private Query query;
    private QueryEnumerator filteredQueryEnum;
    private ArrayList<QueryRow> pcodeList;
    private ArrayList<QueryRow> filteredPCodeList;

    private LayoutInflater inflater;
    Context context;
//    private AddResidentActivity addResidentActivity;
    //   private EditResidentActivity editResidentActivity;


    public PCodeAdapter(Context context,  ArrayList<QueryRow> pcodeList)
    {
        this.pcodeList = pcodeList;
        //this.query = query;
        this.filteredPCodeList = new ArrayList<QueryRow>(this.pcodeList);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void  addList(ArrayList<QueryRow> list)
    {
        pcodeList = list;

       // filteredPCodeList = new ArrayList<QueryRow>();

        //filteredPCodeList = pcodeList;
    }

    //public ArrayList<QueryRow>  getList()
  /*  {
        return filteredPCodeList;
    }
*/

    public void clear()
    {
        filteredPCodeList.clear();
    }

    public void clear2()
    {
        pcodeList.clear();
    }

    public void addItem(QueryRow item) {
        pcodeList.add(item);
    }
    public void addSearchItem(QueryRow item) {
        filteredPCodeList.add(item);
    }


    @Override
    public int getCount() {
        return filteredPCodeList != null ? filteredPCodeList.size() : 0;
    }
    @Override
    public Object getItem(int i) {
        return filteredPCodeList.get(i);
    }



    @Override
    public android.widget.Filter getFilter() {
        return new android.widget.Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                ArrayList<QueryRow> FilteredArrList = new ArrayList<QueryRow>();

               // QueryEnumerator filteredEnumerator = null;

                if (charSequence == null || charSequence.length() == 0) {
                    // No filter implemented we return all the list
                    results.values = pcodeList;
                    results.count = pcodeList.size();
                }
                else {
                    for (int i = 0; i < pcodeList.size(); i++) {
                        QueryRow data = pcodeList.get(i);
                       String pcodeName = data.getDocument().getCurrentRevision().getProperty("p_code_name").toString();
                       String pcode = data.getDocument().getCurrentRevision().getProperty("p_code").toString();
                        String district = data.getDocument().getCurrentRevision().getProperty("district").toString();
                        String cadastral = data.getDocument().getCurrentRevision().getProperty("cadastral").toString();

                        if (pcodeName.toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                            FilteredArrList.add(data);
                        }
                        else if (pcode.toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                                FilteredArrList.add(data);

                        }
                        else if (district.toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                            FilteredArrList.add(data);
                        }
                        else if (cadastral.toLowerCase().startsWith(charSequence.toString().toLowerCase())) {

                            FilteredArrList.add(data);
                        }


                        if (FilteredArrList.size() >=80)
                            break;
                    }
                    //PCodeObject noData = new PCodeObject("N/A","NO PCODE DATA FOUND","", "");
                    //FilteredArrList.add(noData);
                    //query.setStartKey(charSequence.toString());
                    //query.setEndKey(Arrays.asList(charSequence.toString()));
                    //query.setLimit(10);
                  /*  try {
                        filteredEnumerator = FilteredArrList;
                    }
                    catch (CouchbaseLiteException e){}*/

                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;

                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredPCodeList = (ArrayList<QueryRow>) filterResults.values;//(ArrayList<QueryRow>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflateIfRequired(view, position, parent);
        bind((QueryRow)getItem(position), view, position);
        return view;
    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {

                view = inflater.inflate(R.layout.pcode_item, null);
                view.setTag(new ViewHolder(view));

        }
        return view;
    }


    private void bind(QueryRow row, View view, final int position) {

             Document doc = row.getDocument();
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.pcodeID.setText("" + doc.getCurrentRevision().getProperty("p_code").toString());
            holder.pcodeName.setText("" + doc.getCurrentRevision().getProperty("p_code_name").toString());
            holder.cadastral.setText("District: " + doc.getCurrentRevision().getProperty("district").toString());
            holder.district.setText("Cadasteral: " + doc.getCurrentRevision().getProperty("cadastral").toString());



        if((position%2)==0)
            holder.backround.setBackgroundColor(context.getResources().getColor(R.color.white));
        else
            holder.backround.setBackgroundColor(context.getResources().getColor(R.color.Unicef_Light));





    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int i) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int i) {
        return 0;
    }

    static class ViewHolder {

        // if(UserPrefs.getUserPref(context).equals("Collector"))
        // {}
        final TextView pcodeID;
        final TextView pcodeName;
        final TextView cadastral;
        final TextView district;
        final LinearLayout backround;


        ViewHolder(View view) {
            pcodeID = (TextView) view.findViewWithTag("pcodeID");
            pcodeName = (TextView) view.findViewWithTag("pcodeName");
            cadastral = (TextView) view.findViewWithTag("cadastral");
            district = (TextView) view.findViewWithTag("district");
            backround = (LinearLayout) view.findViewWithTag("pcode_back");

        }
    }




}
