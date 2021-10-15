package com.unicefwinterizationplatform.winterization_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.*;

/**
 * Created by Tarek on 10/11/2014.
 */
public class ChildViewActivity extends HeaderActivity {

ChildObject childObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_childview);

        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.layout_child_buttons);


        Button addButton = (Button) findViewById(R.id.add_child_btn);
        Button editButton = (Button) findViewById(R.id.edit_child_btn);
        Button removeButton = (Button) findViewById(R.id.remove_child_btn);


        if (getIntent().getAction().equals(Constants.ADD_CHILD)) {

            parentLayout.removeView(editButton);
            parentLayout.removeView(removeButton);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Spinner ageSpinner = (Spinner) findViewById(R.id.spinner_age);
                    Spinner genderSpinner = (Spinner) findViewById(R.id.spinner_gender);
                    ChildObject childObject = new ChildObject((String) ageSpinner.getSelectedItem(), (String) genderSpinner.getSelectedItem(), "", "ALLOCATED", "");
                    childObject.setKit(setKitForChild(ageSpinner.getSelectedItemPosition()));
                    Intent intent = new Intent();
                    intent.putExtra("CHILD", childObject);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }

        if (getIntent().getAction().equals(Constants.EDIT_CHILD)) {


            parentLayout.removeView(addButton);

            childObject =(ChildObject) getIntent().getSerializableExtra("CHILD");

            TextView titleText = (TextView) findViewById(R.id.textView_titleText);
            titleText.setText("Edit Child");
            Spinner ageSpinner = (Spinner) findViewById(R.id.spinner_age);
            ArrayAdapter myAdap = (ArrayAdapter) ageSpinner.getAdapter(); //cast to an ArrayAdapter
            int spinnerPosition = myAdap.getPosition(childObject.getAge());
            ageSpinner.setSelection(spinnerPosition);
            Spinner genderSpinner = (Spinner) findViewById(R.id.spinner_gender);
            if(childObject.getGender().equals("Boy"))
                genderSpinner.setSelection(0);
            else
                genderSpinner.setSelection(1);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Spinner ageSpinner = (Spinner) findViewById(R.id.spinner_age);
                    Spinner genderSpinner = (Spinner) findViewById(R.id.spinner_gender);
                    int position = getIntent().getIntExtra("position",0);
                    //ChildObject child = new ChildObject();
                    childObject.setAge((String)ageSpinner.getSelectedItem());
                    childObject.setGender((String)genderSpinner.getSelectedItem());
                    childObject.setKit(setKitForChild(ageSpinner.getSelectedItemPosition()));
                    Intent intent = new Intent();
                    intent.putExtra("CHILD", childObject);
                    intent.putExtra("position",position);
                    setResult(1, intent);
                    finish();
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


    public String setKitForChild(int position)
    {

        String kitVal= "";
       switch(position)
       {
           case 0:
               kitVal = "3 months";
               break;
           case 1:
               kitVal = "12 months";
               break;
           case 2:
           case 3:
               kitVal = "2 years";
               break;
           case 4:
               kitVal = "3 years";
               break;
           case 5:
           case 6:
               kitVal = "5 years";
               break;
           case 7:
           case 8:
               kitVal = "7 years";
               break;
           case 9:
           case 10:
               kitVal = "9 years";
               break;
           case 11:
           case 12:
           case 13:
               kitVal = "12 years";
               break;
           case 14:
           case 15:
               kitVal = "14 years";
               break;
       }

        return kitVal;
    }
}
