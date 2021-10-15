package com.unicefwinterizationplatform.winterization_android;

import android.app.DownloadManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.couchbase.lite.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tarek on 10/19/2014.
 */
public class BeneficiaryAdapter extends BaseAdapter implements Filterable { //BaseAdapter


  //  private ArrayList<QueryRow> beneficiaryList;
  //  private ArrayList<QueryRow> filteredBeneficiaryList;

    private QueryEnumerator queryEnumerator;
    private QueryEnumerator filteredQueryEnumerator;
    private LayoutInflater inflater;
    private String SearchFieldOrder;
    Context context;
//    private AddResidentActivity addResidentActivity;
    //   private EditResidentActivity editResidentActivity;



    public BeneficiaryAdapter(Context context, QueryEnumerator queryEnumerator)
    {
      ///  beneficiaryList =new ArrayList<QueryRow>();
        this.queryEnumerator = queryEnumerator;
        this.filteredQueryEnumerator = queryEnumerator;
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setSearchFieldOrder(String searchFieldOrder) {
            SearchFieldOrder = searchFieldOrder;
    }

    public String getSearchFieldOrder() {
        return SearchFieldOrder;
    }



 /*   public void  addList(ArrayList<QueryRow> list)
    {
        beneficiaryList = list;

        filteredBeneficiaryList = new ArrayList<QueryRow>();

        filteredBeneficiaryList = beneficiaryList;
    }*/

   /* public ArrayList<QueryRow>  getList()
    {
       // return filteredBeneficiaryList;
    }
*/
    @Override
    public int getCount() {
        return filteredQueryEnumerator != null ? filteredQueryEnumerator.getCount() : 0;
    }
    @Override
    public Object getItem(int i) {
        return filteredQueryEnumerator.getRow(i);
    }
  //  @Override
  // public long getItemId(int i) {
  //      return queryEnumerator.getRow(i).getSequenceNumber();
  //  }


  //  @Override
   // public int getCount() {
   //     return filteredBeneficiaryList.size();
  //  }

  //  @Override
   // public QueryRow getItem(int position) {
   //     return filteredBeneficiaryList.get(position);
   // }



    @Override
    public android.widget.Filter getFilter() {
        return new android.widget.Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();

               // ArrayList<QueryRow> FilteredArrList = new ArrayList<QueryRow>();
                QueryEnumerator filteredEnumerator = null;
                String role = UserPrefs.getUserPref(context);


                //<item>By ID</item>
                //<item>By altitude</item>
                //<item>By pcode name</item>
                //<item>By barcode number</item>
                if (charSequence == null || charSequence.length() == 0) {
                    // No filter implemented we return all the list
                    results.values = queryEnumerator;
                    results.count = queryEnumerator.getCount();
                }
                else {

                        if(getSearchFieldOrder().equals("By ID"))
                        {


                            com.couchbase.lite.View viewItems = CouchBaseManager.getInstance(context).createIDView(role,"");
                            Query query = viewItems.createQuery();
                            query.setStartKey(charSequence.toString());
                            query.setEndKey(Arrays.asList(charSequence.toString()));
                            query.setLimit(300);

                            try {
                                filteredEnumerator = query.run();
                            }
                            catch (CouchbaseLiteException e){}
                        }

                    if(getSearchFieldOrder().equals("By altitude"))
                    {
                    //    for (int i = 0; i < beneficiaryList.size(); i++) {
                    //        QueryRow row = beneficiaryList.get(i);
                    //        Document doc = row.getDocument();
                     //       String alt = doc.getCurrentRevision().getProperty("alt").toString();
                     //       if (alt.toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                     //           FilteredArrList.add(row);
                     //       }
                     //   }
                    }

                    if(getSearchFieldOrder().equals("By pcode name"))
                    {

                        com.couchbase.lite.View viewItems = CouchBaseManager.getInstance(context).createPcodeView(role);
                        Query query = viewItems.createQuery();
                        query.setStartKey(charSequence.toString());
                        query.setEndKey(Arrays.asList(charSequence.toString(), new HashMap<String, Object>()));
                        try {
                            filteredEnumerator = query.run();
                        }
                        catch (CouchbaseLiteException e){}

                     //   for (int i = 0; i < beneficiaryList.size(); i++) {
                     //       QueryRow row = beneficiaryList.get(i);
                    //        Document doc = row.getDocument();
                     //       Map<String,Object> pcodeData  = (Map<String,Object>)doc.getCurrentRevision().getProperty("location");
                      //      String pcodeName = (String)pcodeData.get("p_code_name");
                      //      String pcodeNameLowerCase = pcodeName.toLowerCase();
                     //       if (pcodeNameLowerCase.toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                      //          FilteredArrList.add(row);
                      //      }
                      //  }
                    }

                    if(getSearchFieldOrder().equals("By barcode number"))
                    {
                        com.couchbase.lite.View viewItems = CouchBaseManager.getInstance(context).createBarcodeView(role);
                        Query query = viewItems.createQuery();
                        query.setStartKey(charSequence.toString());
                        query.setEndKey(Arrays.asList(charSequence.toString()));

                        query.setLimit(300);

                        try {
                            filteredEnumerator = query.run();
                        }
                        catch (CouchbaseLiteException e){}

                      //  for (int i = 0; i < beneficiaryList.size(); i++) {
                       //     QueryRow row = beneficiaryList.get(i);
                      //      Document doc = row.getDocument();
                      //      String barcodeNum = doc.getCurrentRevision().getProperty("barcode_num").toString();
                      //      if (barcodeNum.toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                     //           FilteredArrList.add(row);
                      //      }
                       // }

                    }

                    if(getSearchFieldOrder().equals("By phone number"))
                    {
                        com.couchbase.lite.View viewItems = CouchBaseManager.getInstance(context).createPhoneNumberView(role,"");
                        Query query = viewItems.createQuery();
                        query.setStartKey(charSequence.toString());
                        query.setEndKey(Arrays.asList(charSequence.toString()));

                        query.setLimit(300);

                        try {
                            filteredEnumerator = query.run();
                        }
                        catch (CouchbaseLiteException e){}

                        //  for (int i = 0; i < beneficiaryList.size(); i++) {
                        //     QueryRow row = beneficiaryList.get(i);
                        //      Document doc = row.getDocument();
                        //      String barcodeNum = doc.getCurrentRevision().getProperty("barcode_num").toString();
                        //      if (barcodeNum.toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                        //           FilteredArrList.add(row);
                        //      }
                        // }

                    }


                    results.count = filteredEnumerator.getCount();//FilteredArrList.size();
                    results.values = filteredEnumerator; //FilteredArrList;

                }

                return results;
            }





            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredQueryEnumerator = (QueryEnumerator) filterResults.values;//(ArrayList<QueryRow>) filterResults.values;
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

            view = inflater.inflate(R.layout.beneficiary_item, null);
            view.setTag(new ViewHolder(view));

        }
        return view;
    }


    private void bind(QueryRow row, View view, final int position) {


        Document doc = row.getDocument();
        ViewHolder holder = (ViewHolder) view.getTag();
        String role = UserPrefs.getUserPref(context);

      //  if (!role.equals(Constants.ROLE_VOUCHERS_DISTRIBUTOR) || !role.equals(Constants.ROLE_VOUCHERS_REDEEMER ))
        holder.barcodeID.setText("Barcode: " + doc.getCurrentRevision().getProperty("barcode_num").toString());

        holder.familyName.setText("Household: " + doc.getCurrentRevision().getProperty("family_name").toString());


        Map<String,Object> pcodeData  = (Map<String,Object>)doc.getCurrentRevision().getProperty("location");


        if (role.equals(Constants.ROLE_VOUCHERS_DISTRIBUTOR) || role.equals(Constants.ROLE_VOUCHERS_REDEEMER)) {
            holder.barcodeID.setText("");
            holder.pcodeName.setText("Phone Number: " + doc.getCurrentRevision().getProperty("phone_number"));
            if (doc.getCurrentRevision().getProperty("id_type").toString().equals("NPTP")) {
                holder.familyName.setText("Full Name: " + doc.getCurrentRevision().getProperty("first_name").toString());
            }
            else
            {
                holder.familyName.setText("Household: " + doc.getCurrentRevision().getProperty("family_name").toString());
            }

        }
        else
        holder.pcodeName.setText("Site: "+ (String)pcodeData.get("p_code_name"));

        String critical = doc.getCurrentRevision().getProperty("criticality").toString();
        if(critical.equals("0"))
        holder.criticalVal.setBackgroundColor(view.getResources().getColor(R.color.Qi));
        else if(critical.equals("1"))
        holder.criticalVal.setBackgroundColor(view.getResources().getColor(R.color.DarkOrange));
        else if(critical.equals("2"))
        holder.criticalVal.setBackgroundColor(view.getResources().getColor(R.color.Red));

        holder.idNumber.setText("ID: "+doc.getCurrentRevision().getProperty("official_id").toString() );



    }

    static class ViewHolder {

        // if(UserPrefs.getUserPref(context).equals("Collector"))
        // {}
        final TextView barcodeID;
        final TextView idNumber;
        final TextView familyName;
        final TextView pcodeName;

        final ImageView criticalVal;



        ViewHolder(View view) {
            barcodeID = (TextView) view.findViewWithTag("barcodeID");
            idNumber = (TextView) view.findViewWithTag("idNumber");
            familyName = (TextView) view.findViewWithTag("familyName");
            criticalVal = (ImageView) view.findViewWithTag("criticalVal");
            pcodeName = (TextView) view.findViewWithTag("pcode");
        }
    }




}
