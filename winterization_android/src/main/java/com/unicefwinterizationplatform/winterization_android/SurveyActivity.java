package com.unicefwinterizationplatform.winterization_android;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.couchbase.lite.Document;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Tarek on 10/8/15.
 */
public class SurveyActivity extends HeaderActivity {

    int inc = 0;
    BeneficiaryObject beneficiaryObject;
    String assistanceType;
    Map<String,Object> questionsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surveylayout);



     beneficiaryObject =  getIntent().getParcelableExtra("BENEFICIARY");
     assistanceType = getIntent().getStringExtra("ASSISTANCE");

     if(getIntent().getSerializableExtra("SURVEY")==null)
     {
         questionsArray = new LinkedHashMap<String,Object>();
     }
     else
     {
         questionsArray = (Map<String,Object>)getIntent().getSerializableExtra("SURVEY");
     }



     inc = getIntent().getIntExtra("INC",1);

     Document doc = CouchBaseManager.getInstance(getApplicationContext()).getDocumentFromID("survey"+inc);
     //JSONObject surveyJson = null;

     String s  =  doc.getCurrentRevision().getProperties().toString();


     String name =  (String)doc.getProperty("survey_name");

     TextView surveyName = (TextView)findViewById(R.id.TextView_titleText);

     surveyName.setText(name);

     SurveyFragment surveyFragment = new SurveyFragment();
     surveyFragment.setSurveyJson(doc);
     surveyFragment.setInc(inc);


     surveyFragment.setBeneficiaryObject(beneficiaryObject);
        surveyFragment.setAssistance(assistanceType);
     surveyFragment.setSurvey(questionsArray);
     surveyFragment.setNextQuestion("Q1");

     if (getIntent().getAction() != null && getIntent().getAction().equals("EDIT"))
            surveyFragment.setEdit(true);

     FragmentManager fm = getFragmentManager();
     FragmentTransaction fragmentTransaction = fm.beginTransaction();
     fragmentTransaction.replace(R.id.frag_survey, surveyFragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }


    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
             //   this.finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}



