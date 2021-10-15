package com.unicefwinterizationplatform.winterization_android;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Document;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Tarek on 10/8/15.
 */
public class SurveyFragment extends Fragment {

    Document surveyJson;
    BeneficiaryObject  beneficiaryObject;
    String assistanceType;
    Map<String,Object> survey;
    boolean edit;

    String nextQuestion;
    LinearLayout lin;
    String value ="";
    int inc;
    Map<String,Object> questionLayout;
    final Fragment frag  = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //if (getArguments().getInt("INC") != null)
       // inc = getArguments().getInt("INC");

        //Inflate the layout for this fragment
        View root = inflater.inflate(
                R.layout.fragment_survey, container, false);

      //  Log.e("TEST",survey.toString());

        if (getNextQuestion() != null && surveyJson != null)
        {
            questionLayout = (Map<String,Object>)surveyJson.getProperty(getNextQuestion());

            lin = (LinearLayout) root.findViewById(R.id.farg_id);
            final TextView questionText = new TextView(root.getContext());
            questionText.setGravity(Gravity.CENTER);
            questionText.setTextSize(20);

            if ( questionLayout != null) {

                questionText.setText((String) questionLayout.get("question"));
                lin.addView(questionText);
            }

            String questionType;
            if (questionLayout != null)
                questionType = (String)questionLayout.get("type");
            else
            questionType = "";

            if (questionType.equals("binary"))
            {
                LinearLayout subLin = new LinearLayout(root.getContext());
                subLin.setGravity(Gravity.CENTER);
                subLin.setOrientation(LinearLayout.HORIZONTAL);
                final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lparams.setMargins(6,6,6,6);
                CheckBox yesBtn = new CheckBox(root.getContext());
                yesBtn.setLayoutParams(lparams);
                yesBtn.setText("YES");
                yesBtn.setTag("YES");
                yesBtn.setBackground(root.getResources().getDrawable(R.drawable.custombutton_green));
                yesBtn.setGravity(Gravity.CENTER);
                yesBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                     CheckBox noBtn =(CheckBox) lin.findViewWithTag("NO");
                     if (b == true) {
                         noBtn.setChecked(!b);
                     }

                    if (questionLayout.get("has_na") != null){

                            CheckBox naBtn =(CheckBox) lin.findViewWithTag("N/A");
                            if(b == true) {
                                naBtn.setChecked(!b);
                            }

                        }

                    }
                });

                subLin.addView(yesBtn);

                CheckBox noBtn = new CheckBox(root.getContext());
                noBtn.setLayoutParams(lparams);
                noBtn.setText("NO");
                noBtn.setTag("NO");
                noBtn.setBackground(root.getResources().getDrawable(R.drawable.custombutton_red));
                noBtn.setGravity(Gravity.CENTER);
                noBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                        CheckBox yesBtn =(CheckBox) lin.findViewWithTag("YES");
                        if(b == true) {
                            yesBtn.setChecked(!b);
                        }

                        if (questionLayout.get("has_na") != null){

                            CheckBox naBtn =(CheckBox) lin.findViewWithTag("N/A");
                            if(b == true) {
                                naBtn.setChecked(!b);
                            }

                        }

                    }
                });
                subLin.addView(noBtn);

                if(questionLayout.get("has_na") != null && (Boolean)questionLayout.get("has_na") == true)
                {
                    CheckBox naBtn = new CheckBox(root.getContext());
                    naBtn.setLayoutParams(lparams);
                    naBtn.setText("N/A");
                    naBtn.setTag("N/A");
                    naBtn.setBackground(root.getResources().getDrawable(R.drawable.custombutton));
                    naBtn.setGravity(Gravity.CENTER);
                    naBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                            CheckBox yesBtn =(CheckBox) lin.findViewWithTag("YES");
                            CheckBox noBtn =(CheckBox) lin.findViewWithTag("NO");
                            if(b == true) {
                                yesBtn.setChecked(!b);
                                noBtn.setChecked(!b);
                            }

                        }
                    });
                    subLin.addView(naBtn);
                }

                lin.addView(subLin);

                if (value.equals(yesBtn.getTag()))
                {
                    yesBtn.setChecked(true);
                }
                else if (value.equals(noBtn.getTag()))
                {
                    noBtn.setChecked(true);
                }


            }
            else if(questionType.equals("input"))
            {
                LinearLayout subLin = new LinearLayout(root.getContext());
                subLin.setGravity(Gravity.CENTER);
                subLin.setOrientation(LinearLayout.VERTICAL);


                EditText editText = new EditText(root.getContext());

                final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lparams.setMargins(6, 6, 6, 6);
                editText.setGravity(Gravity.CENTER);
                editText.setLayoutParams(lparams);
                if (questionLayout.get("limit")!=null){

                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter((Integer)questionLayout.get("limit"));

                    editText.setFilters(FilterArray);
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    editText.setTag("DIGITS");
                    editText.setHint("four digit code");
                }



                subLin.addView(editText);
                CheckBox naBtn = new CheckBox(root.getContext());

                naBtn.setText("N/A");
                naBtn.setTag("N/A");
                naBtn.setLayoutParams(lparams);
                //naBtn.setBackground(root.getResources().getDrawable(R.drawable.custombutton_red));
                naBtn.setGravity(Gravity.CENTER);


                subLin.addView(naBtn);
                lin.addView(subLin);

                if (value.equals((String)naBtn.getTag()))
                {
                    naBtn.setChecked(true);
                }
                else
                {
                    editText.setText(value);
                }



            }
            else if(questionType.equals("multiple_radio"))
            {
                LinearLayout subLin = new LinearLayout(root.getContext());
                subLin.setGravity(Gravity.CENTER);
                subLin.setOrientation(LinearLayout.VERTICAL);
                final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lparams.setMargins(6,6,6,6);
                RadioGroup radioGroup = new RadioGroup(root.getContext());
                radioGroup.setLayoutParams(lparams);
                radioGroup.setTag("RADIO");
                radioGroup.setGravity(Gravity.CENTER);
                radioGroup.setOrientation(RadioGroup.VERTICAL);
                ArrayList<String> vals = (ArrayList<String>) questionLayout.get("selection");
                for (int i =0;i<vals.size();i++)
                {
                    String val = vals.get(i);
                    RadioButton rb = new RadioButton(root.getContext());
                    rb.setText(val);
                    rb.setId(i+100);
                    radioGroup.addView(rb);
                }
                subLin.addView(radioGroup);
                lin.addView(subLin);

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {

                    }
                });


                if (!value.equals(""))
                {
                    RadioButton radiobutton = (RadioButton)root.findViewById(Integer.parseInt(value));
                    radiobutton.setChecked(true);
                }


            }






            Button prevButton = (Button) root.findViewById(R.id.buttonPrev);
            prevButton.setVisibility(View.VISIBLE);



            prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    String surveyName = (String)surveyJson.getProperty("survey_name");
                    if (survey.get(surveyName)!= null) {
                        ((ArrayList<Map<String,Object>>) survey.get(surveyName)).remove( ((ArrayList<Map<String,Object>>) survey.get(surveyName)).size()-1);

                    }

                    FragmentManager fm = getFragmentManager();

                    fm.popBackStack();



                }
            });


            Button nextButton = (Button) root.findViewById(R.id.buttonNext);
            nextButton.setVisibility(View.VISIBLE);



            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String questionType = (String)questionLayout.get("type");
                    if (questionType.equals("binary")){

                        CheckBox yesBtn = (CheckBox) lin.findViewWithTag("YES");
                        CheckBox noBtn = (CheckBox) lin.findViewWithTag("NO");
                        CheckBox naBtn = null;
                        if (questionLayout.get("has_na") != null)
                             naBtn = (CheckBox) lin.findViewWithTag("N/A");



                            if (naBtn == null) {
                                if (!yesBtn.isChecked() && !noBtn.isChecked()) {
                                    Toast.makeText(getActivity(),
                                            "Please select one of the check boxes.",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    saveBinary(yesBtn,noBtn, naBtn);
                                }
                            }
                        else{

                                if (!yesBtn.isChecked() && !noBtn.isChecked() && !naBtn.isChecked()) {
                                    Toast.makeText(getActivity(),
                                            "Please select one of the check boxes.",
                                            Toast.LENGTH_LONG).show();
                                } else {

                                    saveBinary(yesBtn,noBtn, naBtn);
                                }
                            }
                    }

                    else if (questionType.equals("input")){

                        EditText editText = (EditText) lin.findViewWithTag("DIGITS");
                        CheckBox naBox = (CheckBox) lin.findViewWithTag("N/A");



                        if (editText.getText().length() == 4 || naBox.isChecked()) {
                            String nextStr = (String) questionLayout.get("next");
                            String nextQ = nextStr;

                            if (naBox.isChecked())
                                value = (String)naBox.getTag();
                            else
                                value = editText.getText().toString();


                            Map<String,Object> answer = new HashMap<String, Object>();

                            answer.put("question", questionLayout.get("question"));
                            answer.put("answer",value);

                            Map<String,Object> questionKey = new HashMap<String, Object>();

                            questionKey.put(nextQuestion,answer);

                            String surveyName = (String)surveyJson.getProperty("survey_name");
                            if (survey.get(surveyName)!= null) {

                                ArrayList<Map<String, Object>> arr = (ArrayList<Map<String,Object>>) survey.get(surveyName);

                                int index = -1;

                                for (int i = 0;i<arr.size();i++)
                                {
                                    Map<String,Object>  qKey =  arr.get(i);
                                    if (qKey.containsKey(nextQuestion))
                                    {
                                        index = i;
                                    }
                                }

                                if (index == -1) {
                                    ((ArrayList<Map<String, Object>>) survey.get(surveyName)).add(questionKey);
                                }
                                else
                                {
                                    ((ArrayList<Map<String, Object>>) survey.get(surveyName)).set(index,questionKey);
                                }

                            }
                            else {
                                ArrayList<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
                                arr.add(questionKey);
                                survey.put(surveyName, arr);
                            }


                                SurveyFragment surveyFragment = new SurveyFragment();
                                surveyFragment.setSurveyJson(surveyJson);
                                surveyFragment.setNextQuestion(nextQ);
                                surveyFragment.setSurvey(survey);
                                surveyFragment.setBeneficiaryObject(beneficiaryObject);
                                surveyFragment.setAssistance(assistanceType);
                                surveyFragment.setInc(inc);
                                surveyFragment.setEdit(edit);

                            FragmentManager fm = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                                fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_left2, R.anim.slide_out_right2);
                                fragmentTransaction.replace(R.id.frag_survey, surveyFragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();

                        }
                        else {
                            Toast.makeText(getActivity(),
                                    "Please fill in PIN number or click the check box.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    else if (questionType.equals("multiple_radio")){

                        RadioGroup radioGroup = (RadioGroup)lin.findViewWithTag("RADIO");
                        RadioButton radioButton = (RadioButton)getActivity().findViewById(radioGroup.getCheckedRadioButtonId());

                        if (radioButton == null){
                            Toast.makeText(getActivity(),
                                    "Please select one of the fields.",
                                    Toast.LENGTH_LONG).show();
                        }

                        else {
                            String nextStr = (String) questionLayout.get("next");

                            String nextQ = nextStr;

                            value = radioButton.getText().toString();

                            Map<String,Object> answer = new HashMap<String, Object>();

                            answer.put("question", questionLayout.get("question"));
                            answer.put("answer",value);

                            Map<String,Object> questionKey = new HashMap<String, Object>();

                            questionKey.put(nextQuestion,answer);

                            String surveyName = (String)surveyJson.getProperty("survey_name");
                            if (survey.get(surveyName)!= null) {
                                ArrayList<Map<String, Object>> arr = (ArrayList<Map<String,Object>>) survey.get(surveyName);

                                int index = -1;

                                for (int i = 0;i<arr.size();i++)
                                {
                                    Map<String,Object>  qKey =  arr.get(i);
                                    if (qKey.containsKey(nextQuestion))
                                    {
                                        index = i;
                                    }
                                }

                                if (index == -1) {
                                    ((ArrayList<Map<String, Object>>) survey.get(surveyName)).add(questionKey);
                                }
                                else
                                {
                                    ((ArrayList<Map<String, Object>>) survey.get(surveyName)).set(index,questionKey);
                                }

                            }
                            else {
                                ArrayList<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
                                arr.add(questionKey);
                                survey.put(surveyName, arr);
                            }

                                SurveyFragment surveyFragment = new SurveyFragment();
                                surveyFragment.setSurveyJson(surveyJson);
                                surveyFragment.setSurvey(survey);
                                surveyFragment.setNextQuestion(nextQ);
                                surveyFragment.setEdit(edit);
                                surveyFragment.setBeneficiaryObject(beneficiaryObject);
                            surveyFragment.setAssistance(assistanceType);
                                surveyFragment.setInc(inc);
                                FragmentManager fm = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                                fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_left2, R.anim.slide_out_right2);
                                fragmentTransaction.replace(R.id.frag_survey, surveyFragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();

                            }
                        }

                    }




            });

            if (nextQuestion.equals("Q1"))
            {
                prevButton.setVisibility(View.INVISIBLE);
            }
            else
                prevButton.setVisibility(View.VISIBLE);


            if (nextQuestion.equals("DONE"))
            {
                nextButton.setVisibility(View.INVISIBLE);
                LinearLayout subLin = new LinearLayout(root.getContext());
                subLin.setGravity(Gravity.CENTER);
                subLin.setOrientation(LinearLayout.VERTICAL);
                final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lparams.setMargins(6,6,6,6);
                Button completeButton = new Button(root.getContext());
                completeButton.setLayoutParams(lparams);
                completeButton.setGravity(Gravity.CENTER);
                completeButton.setText("Save and Continue");
                completeButton.setBackground(root.getResources().getDrawable(R.drawable.custombutton_green));
                completeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CouchBaseManager.getInstance(getActivity().getApplicationContext()).addSurvey(survey,beneficiaryObject.getMainID());

                        if (!edit) {
                            int inc2 = inc + 1;

                            Document doc = CouchBaseManager.getInstance(getActivity().getApplicationContext()).getDocumentFromID("survey" + inc2);

                            if (doc.getProperties() == null) {
                                Intent intent = new Intent(getActivity().getApplicationContext(), CommentActivity.class);
                                intent.putExtra("BENEFICIARY", beneficiaryObject);
                                intent.putExtra("ASSISTANCE",assistanceType);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getActivity().getApplicationContext(), SurveyActivity.class);
                                intent.putExtra("INC", inc2);
                                intent.putExtra("BENEFICIARY", beneficiaryObject);
                                intent.putExtra("ASSISTANCE",assistanceType);

                                // intent.putExtra("SURVEY",survey);
                                startActivity(intent);
                            }
                        }
                        else
                        {
                            Intent intent = new Intent();
                            getActivity().setResult(getActivity().RESULT_OK, intent);
                            getActivity().finish();
                        }
                    }
                });

                subLin.addView(completeButton);
                lin.addView(subLin);

            }
            else
            {
                nextButton.setVisibility(View.VISIBLE);

            }

        }



        return root;//inflater.inflate(
        // R.layout.fragment_one, container, false);
    }


    public void saveBinary(CheckBox yesBtn, CheckBox noBtn,CheckBox naBtn)
    {
        Map<String, Object> nextMap = (Map<String, Object>) questionLayout.get("next");

        String nextQ = "";

        if (yesBtn.isChecked()) {
            nextQ = (String) nextMap.get("yes");
            value = (String) yesBtn.getTag();
        } else if (noBtn.isChecked()) {
            nextQ = (String) nextMap.get("no");
            value = (String) noBtn.getTag();
        } else if (naBtn != null && naBtn.isChecked()) {
            nextQ = (String) nextMap.get("N/A");
            value = (String) naBtn.getTag();
        }





        Map<String, Object> answer = new HashMap<String, Object>();

        answer.put("question", questionLayout.get("question"));
        answer.put("answer", value);


        Map<String, Object> questionKey = new HashMap<String, Object>();

        questionKey.put(nextQuestion, answer);

        String surveyName = (String) surveyJson.getProperty("survey_name");
        if (survey.get(surveyName) != null) {
            ArrayList<Map<String, Object>> arr = (ArrayList<Map<String,Object>>) survey.get(surveyName);

            int index = -1;

            for (int i = 0;i<arr.size();i++)
            {
                Map<String,Object>  qKey =  arr.get(i);
                if (qKey.containsKey(nextQuestion))
                {
                    index = i;
                }
            }

            if (index == -1) {
                ((ArrayList<Map<String, Object>>) survey.get(surveyName)).add(questionKey);
            }
            else
            {
                ((ArrayList<Map<String, Object>>) survey.get(surveyName)).set(index,questionKey);
            }
        } else {
            ArrayList<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
            arr.add(questionKey);
            survey.put(surveyName, arr);
        }


        SurveyFragment surveyFragment = new SurveyFragment();
        surveyFragment.setSurveyJson(surveyJson);
        surveyFragment.setNextQuestion(nextQ);
        surveyFragment.setSurvey(survey);
        surveyFragment.setBeneficiaryObject(beneficiaryObject);
        surveyFragment.setAssistance(assistanceType);
        surveyFragment.setInc(inc);
        surveyFragment.setEdit(edit);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_left2, R.anim.slide_out_right2);
        fragmentTransaction.replace(R.id.frag_survey, surveyFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public Document getSurveyJson() {
        return surveyJson;
    }

    public void setSurveyJson(Document surveyJson) {
        this.surveyJson = surveyJson;
    }

    public String getNextQuestion() {
        return nextQuestion;
    }

    public void setNextQuestion(String nextQuestion) {
        this.nextQuestion = nextQuestion;
    }

    public int getInc() {
        return inc;
    }

    public void setInc(int inc) {
        this.inc = inc;
    }


    public Map<String, Object> getSurvey() {
        return survey;
    }


    public BeneficiaryObject getBeneficiaryObject() {
        return beneficiaryObject;
    }

    public void setBeneficiaryObject(BeneficiaryObject beneficiaryObject) {
        this.beneficiaryObject = beneficiaryObject;
    }

    public void setAssistance(String assistanceType) {
        this.assistanceType = assistanceType;
    }

    public void setSurvey(Map<String, Object> survey) {
        this.survey = survey;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }
}
