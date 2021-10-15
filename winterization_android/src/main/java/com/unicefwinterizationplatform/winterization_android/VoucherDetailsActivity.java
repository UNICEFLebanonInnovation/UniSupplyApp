package com.unicefwinterizationplatform.winterization_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

/**
 * Created by Tarek on 10/22/2014.
 */
public class VoucherDetailsActivity extends HeaderActivity {

    ChildObject childObject;
    final int VOUCHER_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucherdetails);

        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.layout_voucherDetails);

        Button addButton = (Button) findViewById(R.id.add_child_btn);
        Button editButton = (Button) findViewById(R.id.edit_child_btn);
        Button removeButton = (Button) findViewById(R.id.remove_child_btn);

        TextView voucherCodeText = (TextView) findViewById(R.id.textView_voucherCode);

        voucherCodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =   new Intent(getApplicationContext(), ReaderActivity.class);
                intent.setAction("addVoucher");
                startActivityForResult(intent, VOUCHER_REQUEST);
            }
        });

        if (getIntent().getAction().equals(Constants.ADD_CHILD)) {

            parentLayout.removeView(editButton);
            parentLayout.removeView(removeButton);
            editButton.setVisibility(View.GONE);
            removeButton.setVisibility(View.GONE);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    EditText nameText = (EditText) findViewById(R.id.editText_childName);
                    Spinner genderSpinner = (Spinner) findViewById(R.id.spinner_gender);
                    Spinner ageSpinner = (Spinner) findViewById(R.id.spinner_age);
                    TextView voucherCodeText = (TextView) findViewById(R.id.textView_voucherCode);


                    ChildObject childObject = new ChildObject();
                    childObject.setAge(ageSpinner.getSelectedItem().toString());
                    childObject.setGender(genderSpinner.getSelectedItem().toString());
                    childObject.setName(nameText.getText().toString());
                    childObject.setVoucherCode(voucherCodeText.getText().toString());
                    if(childObject.getVoucherCode().length() == 10)
                    {
                        childObject.setStatus("DISTRIBUTED");
                    }
                    else
                    {
                        childObject.setStatus("NOT_DISTRIBUTED");
                    }

                    Intent intent = new Intent();
                    intent.putExtra("CHILD", childObject);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }

        if (getIntent().getAction().equals(Constants.EDIT_CHILD)) {


            parentLayout.removeView(addButton);
            addButton.setVisibility(View.GONE);

            childObject =(ChildObject) getIntent().getSerializableExtra("CHILD");

            TextView titleText = (TextView) findViewById(R.id.textView_titleText);
            titleText.setText("Edit Child");
            EditText nameText = (EditText) findViewById(R.id.editText_childName);
            Spinner ageSpinner = (Spinner) findViewById(R.id.spinner_age);
            Spinner genderSpinner = (Spinner) findViewById(R.id.spinner_gender);
            ArrayAdapter gendAdapt = (ArrayAdapter) genderSpinner.getAdapter();
            int genPosition = gendAdapt.getPosition(childObject.getGender());
            genderSpinner.setSelection(genPosition);
            TextView voucherCodeEditText = (TextView) findViewById(R.id.textView_voucherCode);
           // Spinner ageSpinner = (Spinner) findViewById(R.id.spinner_age);
            ArrayAdapter myAdap = (ArrayAdapter) ageSpinner.getAdapter(); //cast to an ArrayAdapter
            int spinnerPosition = myAdap.getPosition(childObject.getAge());
            ageSpinner.setSelection(spinnerPosition);



            nameText.setText(childObject.getName());
            voucherCodeEditText.setText(childObject.getVoucherCode());

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText nameText = (EditText) findViewById(R.id.editText_childName);
                    Spinner ageSpinner = (Spinner) findViewById(R.id.spinner_age);
                    Spinner genderSpinner = (Spinner) findViewById(R.id.spinner_gender);

                    TextView voucherCodeEditText = (TextView) findViewById(R.id.textView_voucherCode);


                    int position = getIntent().getIntExtra("position",0);

                    childObject.setAge(ageSpinner.getSelectedItem().toString());
                    childObject.setGender(genderSpinner.getSelectedItem().toString());
                    childObject.setName(nameText.getText().toString());



                    childObject.setVoucherCode(voucherCodeEditText.getText().toString());
                    if(childObject.getVoucherCode().length() == 10)
                    {
                        childObject.setStatus("DISTRIBUTED");
                    }
                    else
                    {
                        childObject.setStatus("NOT_DISTRIBUTED");
                    }

                    //ChildObject child = new ChildObject((String)ageSpinner.getSelectedItem(),
                    // (String)genderSpinner.getSelectedItem(), "", childObject.getStatus(),"");
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
                   childObject.setStatus("DELETED");
                    Intent intent = new Intent();
                    intent.putExtra("CHILD", childObject);
                    intent.putExtra("position",position);

                    setResult(2, intent);
                    finish();
                }
            });



        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                TextView voucherCodeTextView = (TextView) findViewById(R.id.textView_voucherCode);

                String  voucherCode =  data.getStringExtra("VOUCHER");

                voucherCodeTextView.setText(voucherCode);



            }
        }

    }

}
