package com.unicefwinterizationplatform.winterization_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Tarek on 11/24/2014.
 */
public class DistributionCompletionActivity extends HeaderActivity {

    BeneficiaryObject beneficiaryObject;
    Context context = this;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_completeddistribution);
        beneficiaryObject = (BeneficiaryObject)getIntent().getParcelableExtra("BENEFICIARY");

        Button newDist = (Button) findViewById(R.id.button_newDistribution);
        newDist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent backIntent = new Intent(context, ReaderActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                backIntent.setAction("lookup_barcode");
                startActivity(backIntent);
            }
        });


        Button homeButton = (Button) findViewById(R.id.button_goToHomeScreen);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent(context, TabActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(backIntent);
            }
        });

    }
}