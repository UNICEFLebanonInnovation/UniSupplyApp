package com.unicefwinterizationplatform.winterization_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Tarek on 9/26/16.
 */
public class FamilyViewActivity extends HeaderActivity {

    FamilyObject familyObject;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(-1);
                finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        setResult(-1);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_familyview);

        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.layout_child_buttons);


        Button addButton = (Button) findViewById(R.id.add_family_btn);
        Button editButton = (Button) findViewById(R.id.edit_family_btn);
        Button removeButton = (Button) findViewById(R.id.remove_family_btn);

        Spinner idType = (Spinner) findViewById(R.id.spinner_idTypes);

        idType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EditText idNumber = (EditText) findViewById(R.id.editText_idNumber);
                Log.e("ID", parent.getSelectedItem().toString());
                if (parent.getSelectedItem().toString().equals("No ID"))//NO ID
                {
                    idNumber.setEnabled(false);
                }
                else
                {
                    idNumber.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        if (getIntent().getAction().equals(Constants.ADD_FAMILY)) {

            parentLayout.removeView(editButton);
            parentLayout.removeView(removeButton);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Spinner idType = (Spinner) findViewById(R.id.spinner_idTypes);




                    EditText idNumber = (EditText) findViewById(R.id.editText_idNumber);

                    if(idType.getSelectedItem().equals("----") || (idNumber.getText().toString().isEmpty() && idNumber.isEnabled()) )
                    {
                        Toast.makeText(getApplicationContext(),"Empty Fields",Toast.LENGTH_SHORT).show();
                    }
                    else {

                       if( idType.getSelectedItem().equals("No ID"))
                           idNumber.setText("");

                        FamilyObject familyObject = new FamilyObject(idType.getSelectedItem().toString(), idNumber.getText().toString());

                        Intent intent = new Intent();
                        intent.putExtra("FAMILY", familyObject);
                        setResult(0, intent);
                        finish();
                    }
                }
            });
        }

        if (getIntent().getAction().equals(Constants.EDIT_FAMILY)) {


            parentLayout.removeView(addButton);

            familyObject =(FamilyObject) getIntent().getSerializableExtra("FAMILY");

            TextView titleText = (TextView) findViewById(R.id.textView_titleText);
            titleText.setText("Edit Family Member");
            Spinner idTypeSpinner = (Spinner) findViewById(R.id.spinner_idTypes);
            ArrayAdapter myAdap = (ArrayAdapter) idTypeSpinner.getAdapter(); //cast to an ArrayAdapter
            int spinnerPosition = myAdap.getPosition(familyObject.getIdType());
            idTypeSpinner.setSelection(spinnerPosition);

            EditText idNumber = (EditText) findViewById(R.id.editText_idNumber);
            idNumber.setText(familyObject.getID());


            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Spinner idTypes = (Spinner) findViewById(R.id.spinner_idTypes);
                    EditText idNum = (EditText) findViewById(R.id.editText_idNumber);

                    if(idTypes.getSelectedItem().equals("----") || (idNum.getText().toString().isEmpty() && idNum.isEnabled()) )
                    {
                        Toast.makeText(getApplicationContext(),"Empty Fields",Toast.LENGTH_SHORT).show();
                    }
                    else {

                        if( idTypes.getSelectedItem().equals("No ID"))
                            idNum.setText("");

                        int position = getIntent().getIntExtra("position", 0);
                        familyObject.setIdType((String) idTypes.getSelectedItem());
                        familyObject.setID(idNum.getText() + "");
                        Intent intent = new Intent();
                        intent.putExtra("FAMILY", familyObject);
                        intent.putExtra("position", position);
                        setResult(1, intent);
                        finish();
                    }
                }
            });
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getIntent().getIntExtra("position",0);
                    Intent intent = new Intent();
                    intent.putExtra("position",position);
                    setResult(2, intent);
                    finish();
                }
            });
        }
    }

}
