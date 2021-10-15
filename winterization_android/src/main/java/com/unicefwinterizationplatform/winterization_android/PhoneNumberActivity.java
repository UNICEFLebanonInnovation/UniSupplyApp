package com.unicefwinterizationplatform.winterization_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Tarek on 10/13/2014.
 */
public class PhoneNumberActivity extends HeaderActivity {


    Context context = this;
    ArrayAdapter phoneNumberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonenumber);

        ArrayList<String> arr = new ArrayList<String>();

        arr.add("03157168");
        arr.add("03157168");
        arr.add("03157168");
        arr.add("03157164");
        arr.add("03157168");
        arr.add("03157168");



        phoneNumberAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,arr);
        ListView listView = (ListView)findViewById(R.id.listView_beneficiaries);
        listView.setAdapter(phoneNumberAdapter);
        listView.setOnItemClickListener(onItemCLickListener());

        EditText searchField = (EditText) findViewById(R.id.editText_searchBeneficiaries);
        searchField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                PhoneNumberActivity.this.phoneNumberAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

    }

    private AdapterView.OnItemClickListener onItemCLickListener()
    {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(role.equals(Constants.ROLE_KITS_ASSESSOR)) {
                    Intent intent = new Intent(getApplicationContext(), DetailedBeneficiaryActivity.class);
                    startActivity(intent);
                }
                if(role.equals(Constants.ROLE_KITS_DISTRIBUTOR)) {

                    Intent intent = new Intent(getApplicationContext(), DistributorBeneficiaryActivity.class);
                    startActivity(intent);
                }

            }
        };
    }
}
