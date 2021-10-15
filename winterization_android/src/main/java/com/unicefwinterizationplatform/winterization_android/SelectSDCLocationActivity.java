package com.unicefwinterizationplatform.winterization_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.couchbase.lite.Document;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;

/**
 * Created by Tarek on 11/2/2014.
 */
public class SelectSDCLocationActivity extends HeaderActivity {

    private PCodeAdapter pcodeAdapter;
    ListView pcodeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdcselection);

      //  ArrayList<PCodeObject> arr = CouchBaseManager.getInstance(this).getSDCList();//getRowsWithView("byLoction","location","IS");

     //   pcodeAdapter = new PCodeAdapter(this,CouchBaseManager.getInstance(this).getSDCEnumerator());
       //// pcodeAdapter.addList(arr);// new ListAdapter(this, android.R.layout.simple_list_item_1, arr);
       pcodeAdapter = new PCodeAdapter(this, null);
        pcodeList = (ListView) this.findViewById(R.id.pcode_list);
        pcodeList.setAdapter(pcodeAdapter);

        pcodeList.setOnItemClickListener(onItemClickListener());


        EditText searchPcode = (EditText) findViewById(R.id.editText_pcodeSearch);

        searchPcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                pcodeAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private AdapterView.OnItemClickListener onItemClickListener(){

        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                QueryRow row = (QueryRow)pcodeAdapter.getItem(i);
                Document doc = row.getDocument();
                PCodeObject pcodeObject = new PCodeObject();
                pcodeObject.setPcodeID(doc.getCurrentRevision().getProperty("p_code").toString());
                pcodeObject.setPcodeName(doc.getCurrentRevision().getProperty("p_code_name").toString());
                pcodeObject.setPcodeLong(doc.getCurrentRevision().getProperty("longitude").toString());
                pcodeObject.setPcodeLat(doc.getCurrentRevision().getProperty("latitude").toString());
                Intent intent = new Intent(getApplicationContext(), BrowseBeneficiariesActivity.class);
                intent.putExtra("SDC_NAME", pcodeObject.getPcodeID());
                startActivity(intent);

            }
        };

    }



}