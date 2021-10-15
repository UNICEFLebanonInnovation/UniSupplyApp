package com.unicefwinterizationplatform.winterization_android;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by Tarek on 10/2/2014.
 */
public class EditKitView extends HeaderActivity {

    ChildObject childObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editkit);
        ActionBar actionBar = getActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
         childObject = (ChildObject)getIntent().getSerializableExtra("CHILD");



        Spinner spinner = (Spinner) findViewById(R.id.spinner_kit);
        ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter(); //cast to an ArrayAdapter
        int spinnerPosition = myAdap.getPosition(childObject.getKit());
        spinner.setSelection(spinnerPosition);



        //  spinner.getSelectedItemId();
        //}

        Button editButton = (Button) findViewById(R.id.edit_kit_btn);


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  ChildObject childObject1 = getIntent().getParcelableExtra("CHILD");

                EditText reasonTxt = (EditText) findViewById(R.id.editText_reasonforedit);
                Spinner spinner = (Spinner) findViewById(R.id.spinner_kit);
                int position = getIntent().getIntExtra("position",0);


               // ChildObject child = new ChildObject(childObject.getAge(),childObject.getName(),childObject.getGender(), (String)spinner.getSelectedItem(),"COMPLETED", reasonTxt.getText().toString());
                childObject.setKit((String)spinner.getSelectedItem());
                childObject.setStatus("COMPLETED");
                childObject.setReasonForEdit(reasonTxt.getText().toString());
               Intent intent = new Intent();
                intent.putExtra("CHILD", childObject);
                intent.putExtra("position",position);

                setResult(1, intent);
                finish();
            }
        });

        Button removeButton = (Button) findViewById(R.id.remove_kit_btn);
       removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText reasonTxt = (EditText) findViewById(R.id.editText_reasonforedit);
                Spinner spinner = (Spinner) findViewById(R.id.spinner_kit);
                int position = getIntent().getIntExtra("position",0);

             //   ChildObject child = new ChildObject(childObject.getAge(),childObject.getName(),childObject.getGender(), (String)spinner.getSelectedItem(),"NOT_DISTRIBUTED",childObject.getReasonForEdit());

                childObject.setKit((String)spinner.getSelectedItem());
                childObject.setStatus("NOT_DISTRIBUTED");
                childObject.setReasonForEdit(reasonTxt.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("CHILD", childObject);
                intent.putExtra("position",position);

                setResult(1, intent);
                finish();
            }
        });

        Button completeKit = (Button) findViewById(R.id.complete_kit_btn);
        completeKit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              /*EditText reasonTxt = (EditText) findViewById(R.id.editText_reasonforedit);
                Spinner spinner = (Spinner) findViewById(R.id.spinner_kit);
                int position = getIntent().getIntExtra("position",0);

                //ChildObject child = new ChildObject(childObject.getAge(),childObject.getName(),childObject.getGender(), (String)spinner.getSelectedItem(),"COMPLETED",childObject.getReasonForEdit());
               childObject.setStatus("COMPLETED");
                Intent intent = new Intent();
                intent.putExtra("CHILD", childObject);
                intent.putExtra("position",position);
                setResult(1, intent);*/
                finish();
            }
        });

    }






}
