package com.unicefwinterizationplatform.winterization_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Tarek on 10/21/15.
 */
public class CommentActivity extends HeaderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);


        Button commentButton = (Button) findViewById(R.id.button_comment);

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText commentText = (EditText) findViewById(R.id.editText_comment);
                BeneficiaryObject beneficiaryObject  =  getIntent().getParcelableExtra("BENEFICIARY");

                if (!commentText.getText().toString().isEmpty())

                {
                    CouchBaseManager.getInstance(getApplicationContext()).addComment(commentText.getText().toString(),beneficiaryObject);
                }

                Intent intent = new Intent(getApplicationContext(),AddSuccessActivity.class);
                intent.putExtra("BENEFICIARY",beneficiaryObject);
                intent.putExtra("ASSISTANCE",getIntent().getStringExtra("ASSISTANCE"));
                startActivity(intent);

            }
        });


    }
}
