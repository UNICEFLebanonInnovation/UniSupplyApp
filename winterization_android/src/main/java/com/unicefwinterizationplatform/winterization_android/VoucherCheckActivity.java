package com.unicefwinterizationplatform.winterization_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.couchbase.lite.Document;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;

/**
 * Created by Tarek on 10/27/2014.
 */
public class VoucherCheckActivity extends HeaderActivity {

    ArrayList<QueryRow> arr;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkvoucher);

        arr = new ArrayList<QueryRow>();
        arr = CouchBaseManager.getInstance(this).getRowsWithView("byBarcodeID", "assessment");


        Button checkIdButton = (Button) findViewById(R.id.button_checkID);

        checkIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText idText = (EditText) findViewById(R.id.editText_idNumber);
                Spinner idType = (Spinner) findViewById(R.id.spinner_idTypes);

                String idNum = idText.getText().toString().trim();
                boolean valFound = false;
                QueryRow queryRow = null;

                for(QueryRow row : arr)
                {
                    Document doc = row.getDocument();

                    if(doc.getCurrentRevision().getProperty("id_type").toString().equals(idType.getSelectedItem().toString())) {


                        if (doc.getCurrentRevision().getProperty("official_id").toString().equals(idNum)) {
                            valFound = true;
                            queryRow = row;
                        }
                    }
                }

                if(valFound)
                {
                    Intent intent = new Intent(getApplicationContext(), VoucherAssessmentActivity.class);
                    BeneficiaryObject beneficiaryObject = CouchBaseManager.getInstance(context).extractBeneficiary(queryRow);
                    intent.putExtra("BENEFICIARY",beneficiaryObject);
                    startActivity(intent);
                }
                else
                {

                }

            }
        });

    }


}
