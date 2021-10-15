package com.unicefwinterizationplatform.winterization_android;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.Arrays;
import java.util.HashMap;


/**
 * Created by Tarek on 9/19/2014.
 */
public class BrowseBeneficiariesActivity extends HeaderActivity implements  AsyncGetHouseholdList {

    Context context = this;
    BeneficiaryAdapter beneficiaryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsebeneficiaries);

        ActionBar actionBar = getActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        final Spinner searchSpinner = (Spinner) findViewById(R.id.spinner_searchType);
        final EditText searchField = (EditText) findViewById(R.id.editText_searchBeneficiaries);
            /*searchField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                 //   beneficiaryAdapter.getFilter().filter(charSequence.toString());

                }

                @Override
                public void afterTextChanged(Editable editable) {
                  //  listView.setSelectionAfterHeaderView();

                }
            });*/



        Button searchBtn = (Button) findViewById(R.id.button_search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AsyncCaller caller = new AsyncCaller();
                caller.delegate = BrowseBeneficiariesActivity.this;
                caller.execute(searchField.getText().toString(),searchSpinner.getSelectedItem().toString());
            }
        });


        if (role.equals(Constants.ROLE_VOUCHERS_DISTRIBUTOR))
        {
            String [] s ={"By ID","By phone number"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(BrowseBeneficiariesActivity.this,android.R.layout.simple_spinner_item,s);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            searchSpinner.setAdapter(adapter);
        }

        if (role.equals(Constants.ROLE_VOUCHERS_REDEEMER))
        {
            String [] s ={"By ID","By phone number"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(BrowseBeneficiariesActivity.this,android.R.layout.simple_spinner_item,s);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            searchSpinner.setAdapter(adapter);
        }

     //   beneficiaryAdapter.setSearchFieldOrder((String)searchSpinner.getSelectedItem());

        /*searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                beneficiaryAdapter.setSearchFieldOrder ((String)adapterView.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/


        Button scanBtn = (Button) findViewById(R.id.button_scanBarcode);
        if(role.equals(Constants.ROLE_KITS_ASSESSOR)) {
            scanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getApplicationContext(), ReaderActivity.class);
                    intent.putExtra("barcodeType", "id");
                    intent.setAction("searchBarcodes");
                    startActivity(intent);
                }
            });
        }
        else if (role.equals(Constants.ROLE_KITS_DISTRIBUTOR) || role.equals(Constants.ROLE_VOUCHERS_DISTRIBUTOR))
        {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_browse);
            linearLayout.removeView(scanBtn);
            scanBtn.setVisibility(View.GONE);
           // Button searchSDCBtn = (Button) findViewById(R.id.button_searchSDCS);
            //searchSDCBtn.setVisibility(View.VISIBLE);

        }

      /*  QueryEnumerator enumerator = null;
        if (role.equals(Constants.ROLE_KITS_ASSESSOR))
        enumerator = CouchBaseManager.getInstance(this).getAssessments("byUserAndType", "assessment");//getRowsWithView("byBarcodeID", "assessment");
        else if(role.equals(Constants.ROLE_KITS_DISTRIBUTOR))
        enumerator = CouchBaseManager.getInstance(this).getAllRows("byBarcodeID", "assessment");*/

    /*    beneficiaryAdapter = new BeneficiaryAdapter(this,enumerator);
     final   ListView listView = (ListView)findViewById(R.id.listView_beneficiaries);
       // beneficiaryAdapter.addList(arr);
        listView.setAdapter(beneficiaryAdapter);
        listView.setOnItemClickListener(onItemCLickListener());*/

      /*  QueryEnumerator enumerator = null;
        if (role.equals(Constants.ROLE_KITS_ASSESSOR))
            enumerator = CouchBaseManager.getInstance(getApplicationContext()).getAssessments("byUserAndType", "assessment");//getRowsWithView("byBarcodeID", "assessment");
        else if(role.equals(Constants.ROLE_KITS_DISTRIBUTOR))
            enumerator = CouchBaseManager.getInstance(getApplicationContext()).getAllRows("byBarcodeID", "assessment");
*/
       // AsyncCaller caller = new AsyncCaller();
      //  caller.delegate = this;
      //  caller.execute();

    }

    public void getHouseholdList(QueryEnumerator enumerator){
        beneficiaryAdapter = new BeneficiaryAdapter(getApplicationContext(),enumerator);
        final   ListView listView = (ListView)findViewById(R.id.listView_beneficiaries);
        // beneficiaryAdapter.addList(arr);
        listView.setAdapter(beneficiaryAdapter);
        listView.setOnItemClickListener(onItemCLickListener());

    }

private AdapterView.OnItemClickListener onItemCLickListener()
{
    return new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            if(role.equals(Constants.ROLE_KITS_ASSESSOR)) {
                Intent intent = new Intent(getApplicationContext(), DetailedBeneficiaryActivity.class);
                QueryRow row = (QueryRow)beneficiaryAdapter.getItem(i);
                BeneficiaryObject beneficiaryObject = CouchBaseManager.getInstance(context).extractBeneficiary(row);

                intent.putExtra("BENEFICIARY",beneficiaryObject);

                startActivity(intent);
            }
            if(role.equals(Constants.ROLE_KITS_DISTRIBUTOR)) {

                Intent intent = new Intent(getApplicationContext(), DistributorBeneficiaryActivity.class);
                QueryRow row = (QueryRow)beneficiaryAdapter.getItem(i);

                BeneficiaryObject beneficiaryObject = CouchBaseManager.getInstance(context).extractBeneficiary(row);
                PCodeObject pcode = getIntent().getParcelableExtra("PCODE");
                beneficiaryObject.setPcodeDist(pcode);

                intent.putExtra("BENEFICIARY",beneficiaryObject);

                startActivity(intent);
            }
            else if(role.equals(Constants.ROLE_VOUCHERS_DISTRIBUTOR)) {

                Intent intent = new Intent(getApplicationContext(), VoucherAssessmentActivity.class);
                QueryRow row = (QueryRow)beneficiaryAdapter.getItem(i);
                BeneficiaryObject beneficiaryObject = CouchBaseManager.getInstance(context).extractBeneficiary(row);

                intent.putExtra("BENEFICIARY",beneficiaryObject);

                startActivity(intent);
            }
            else if(role.equals(Constants.ROLE_VOUCHERS_REDEEMER)) {

                Intent intent = new Intent(getApplicationContext(), VoucherAssessmentActivity.class);
                QueryRow row = (QueryRow)beneficiaryAdapter.getItem(i);
                BeneficiaryObject beneficiaryObject = CouchBaseManager.getInstance(context).extractBeneficiary(row);
                intent.putExtra("BENEFICIARY",beneficiaryObject);
                startActivity(intent);

            }
        }
    };
}



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private class AsyncCaller extends android.os.AsyncTask<String, Void, QueryEnumerator>
    {
        public AsyncGetHouseholdList delegate=null;
        ProgressDialog pdLoading = new ProgressDialog(BrowseBeneficiariesActivity.this);
       String searchStr = "";
       String searchSpin = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tSearching Households");
            pdLoading.show();
            pdLoading.setCancelable(false);
            pdLoading.setCanceledOnTouchOutside(false);
        }
        @Override
        protected QueryEnumerator doInBackground(String... params) {

            //
            Query query = null;
            searchStr = params[0];
            searchSpin = params[1];
            if(searchSpin.equals("By ID"))
            {


                String getSDC = getIntent().getStringExtra("SDC_NAME");



                com.couchbase.lite.View viewItems = CouchBaseManager.getInstance(context).createIDView(role,getSDC);
                query = viewItems.createQuery();
                query.setStartKey(searchStr);
                query.setEndKey(Arrays.asList(searchStr));
                query.setLimit(10);


            }

            if(searchSpin.equals("By pcode name"))
            {


                com.couchbase.lite.View viewItems = CouchBaseManager.getInstance(context).createPcodeView(role);
                query = viewItems.createQuery();
                query.setStartKey(searchStr);
                query.setEndKey(Arrays.asList(searchStr, new HashMap<String, Object>()));
                query.setLimit(10);
            }

            if(searchSpin.equals("By barcode number"))
            {
                com.couchbase.lite.View viewItems = CouchBaseManager.getInstance(context).createBarcodeView(role);
                query = viewItems.createQuery();
                query.setStartKey(searchStr);
                query.setEndKey(Arrays.asList(searchStr));

                query.setLimit(10);
            }

            if(searchSpin.equals("By phone number"))
            {
                String getSDC = getIntent().getStringExtra("SDC_NAME");

                com.couchbase.lite.View viewItems = CouchBaseManager.getInstance(context).createPhoneNumberView(role,getSDC);
                query = viewItems.createQuery();
                query.setStartKey(searchStr);
                query.setEndKey(Arrays.asList(searchStr));
                query.setLimit(10);

            }

            QueryEnumerator enumerator = null;

            try {
                enumerator = query.run();
            }
            catch (CouchbaseLiteException e)
            {

            }
               return enumerator;
        }

        @Override
        protected void onPostExecute(QueryEnumerator enumerator) {
            super.onPostExecute(enumerator);
            delegate.getHouseholdList(enumerator);

            pdLoading.dismiss();



        }

    }


    /*private class AsyncCaller extends android.os.AsyncTask<Void, Void, QueryEnumerator>
    {
        public AsyncGetHouseholdList delegate=null;
        String barCodeVal= "";
        ProgressDialog pdLoading = new ProgressDialog(BrowseBeneficiariesActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading all Households");
            pdLoading.show();
            pdLoading.setCancelable(false);
            pdLoading.setCanceledOnTouchOutside(false);
        }
        @Override
        protected QueryEnumerator doInBackground(Void... params) {

         //

            QueryEnumerator enumerator = null;
            if (role.equals(Constants.ROLE_KITS_ASSESSOR))
                enumerator = CouchBaseManager.getInstance(getApplicationContext()).getAssessments("byUserAndType", "assessment");//getRowsWithView("byBarcodeID", "assessment");
            else if(role.equals(Constants.ROLE_KITS_DISTRIBUTOR))
                enumerator = CouchBaseManager.getInstance(getApplicationContext()).getAllRows("byBarcodeID", "assessment");
            else if(role.equals(Constants.ROLE_VOUCHERS_DISTRIBUTOR))
                enumerator = CouchBaseManager.getInstance(getApplicationContext()).getAllRows("byVoucherID", "assessment-voucher");
            return enumerator;
        }

        @Override
        protected void onPostExecute(QueryEnumerator enumerator) {
            super.onPostExecute(enumerator);
            delegate.getHouseholdList(enumerator);

            pdLoading.dismiss();



        }

    }*/


}
