package com.unicefwinterizationplatform.winterization_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

import com.couchbase.lite.Document;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.support.CouchbaseLiteHttpClientFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Map;


/**
 * Created by Tarek on 7/23/15.
 */
public class DetailedDistActivity extends HeaderActivity {

   final int EDIT_ITEM_REQUEST = 1;
    DistributionObject distributionObject;
    ItemAdapter itemAdapter;
    ListView itemList;
    int currentDistValue =0;
    ScrollView scrollView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemslistview);

        distributionObject = getIntent().getParcelableExtra("DISTRIBUTION");

        TextView instituion_type = (TextView) findViewById(R.id.textView_institutionType);
        TextView institutionName = (TextView) findViewById(R.id.name);
        TextView interventionName = (TextView) findViewById(R.id.textView_intervention);
        scrollView = (ScrollView) findViewById(R.id.scrollview);
        //TextView phoneNumber = (TextView) findViewById(R.id.textView_PhoneNumber);
        //TextView organizer = (TextView) findViewById(R.id.textView_organizer);
        TextView interventionTitle = (TextView) findViewById(R.id.textView_intervention);
        TextView completed = (TextView) findViewById(R.id.textView_completed);
        final Button doneBtn = (Button) findViewById(R.id.button_done);
        final Button forceBtn = (Button) findViewById(R.id.button_forceComplete);
        final Button logsBtn = (Button) findViewById(R.id.button_logs);


        int del = distributionObject.getItemList().get(0).getDelivered();
        currentDistValue = del;
        int quant = distributionObject.getItemList().get(0).getQuantity();

        if (del == quant) {
            forceBtn.setText("Force Completion");
        } else {
            if (distributionObject.isComplete() == true) {
                forceBtn.setText("Reverse Force\nCompletion");
            } else if (distributionObject.isComplete() == false) {
                forceBtn.setText("Force Completion");
            }

        }

        //TextView siteName = (TextView) findViewById(R.id.textView_siteName);
        //  TextView pcodeNum = (TextView) findViewById(R.id.textView_PCode);

        interventionTitle.setText(distributionObject.getInterventionTitle());


        PCodeObject pCodeObject = distributionObject.getPcode();
 
        institutionName.setText(pCodeObject.getPcodeName());
        instituion_type.setText(pCodeObject.getLocationType());
        interventionName.setText(distributionObject.getInterventionTitle());
        //phoneNumber.setText(distributionObject.getPhoneNumber());
        //organizer.setText(distributionObject.getFirstName() + " " + distributionObject.getFamilyName());
        if (distributionObject.isComplete()) {
            completed.setText("The Distribution has been Completed");
            completed.setTextColor(getResources().getColor(R.color.Qi));
        } else {
            completed.setText("The Distribution has Not Yet Been Completed.");
            completed.setTextColor(getResources().getColor(R.color.red));
        }

        //     siteName.setText(beneficiaryObject.getPcode().getPcodeName());
        //     pcodeNum.setText(beneficiaryObject.getPcode().getPcodeID());


        ArrayList<ItemObject> arr; //= new ArrayList<>();

        arr = distributionObject.getItemList();

        itemAdapter = new ItemAdapter(this);
        itemAdapter.addList(arr);// new ListAdapter(this, android.R.layout.simple_list_item_1, arr);
        itemList = (ListView) this.findViewById(R.id.item_list);
        itemList.setAdapter(itemAdapter);

        itemList.setOnItemClickListener(onItemClickListener());


        forceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int del = distributionObject.getItemList().get(0).getDelivered();
                int quant = distributionObject.getItemList().get(0).getQuantity();

                if (del == quant) {
                    Toast.makeText(getApplicationContext(), "ALL ITEMS DELIVERED", Toast.LENGTH_SHORT).show();
                } else {

                    if (distributionObject.isComplete() == false) {

                        LayoutInflater li = LayoutInflater.from(getApplicationContext());
                        View promptsView = li.inflate(R.layout.alert_generic, null);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DetailedDistActivity.this);

                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(promptsView);

                        final LinearLayout linearLayout = (LinearLayout) promptsView
                                .findViewById(R.id.layout_gen);

                        TextView alertText = new TextView(getApplicationContext());
                        linearLayout.addView(alertText);
                        alertText.setTextSize(20);
                        alertText.setTextColor(getResources().getColor(R.color.black));
                        alertText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        alertText.setGravity(Gravity.CENTER);

                        alertText.setText("Are you sure you want to\nforce complete?");


                        // set dialog message
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                distributionObject.setComplete(true);
                                                CouchBaseManager.getInstance(getApplicationContext()).editDist(distributionObject, CouchBaseManager.DistStat.FORCE);
                                                forceBtn.setText("Reverse Force\nCompletion");

                                                dialog.cancel();

                                            }
                                        })
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                    } else if (distributionObject.isComplete() == true) {
                        distributionObject.setComplete(false);
                        CouchBaseManager.getInstance(getApplicationContext()).editDist(distributionObject, CouchBaseManager.DistStat.NO_FORCE);
                        forceBtn.setText("Force Completion");
                    }

                }

            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // LogObject logObject = new LogObject();
                // logObject.setAmount();


                finish();

            }
        });

        logsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LinearLayout logsLayout = (LinearLayout) findViewById(R.id.layout_logs);
                CardView logsView = (CardView) findViewById(R.id.cv_logs);
                ArrayList<Map<String, Object>> history_list = new ArrayList<Map<String, Object>>();

                if (logsLayout.getVisibility() == View.GONE) {

                    logsBtn.setText("Hide Logs");

                    Document doc = CouchBaseManager.getInstance(getApplicationContext()).getDocumentFromID(distributionObject.getDocID());
                    if (doc.getCurrentRevision().getProperty("history") != null) {
                        history_list = (ArrayList<Map<String, Object>>) doc.getCurrentRevision().getProperty("history");
                    }

                    LinearLayout lin = new LinearLayout(getApplicationContext());
                    lin.setOrientation(LinearLayout.VERTICAL);
                    Collections.reverse(history_list);
                    for (Map<String, Object> history : history_list) {
                        TextView histText = new TextView(getApplicationContext());
                        histText.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
                        histText.setTextSize(15);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.
                                WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(5, 5, 5, 5);
                        histText.setLayoutParams(params);
                        String text="";

                        if (history.get("action").toString().toLowerCase().equals("delivered"))
                         text= "- <b><font color=\"#1399de\">[" + history.get("datetime") + "]</font>: </b> " + history.get("delivered") + " of " + history.get("quantity") + " " + history.get("item_type") + " delivered";
                        else if (history.get("action").toString().toLowerCase().equals("force_completed"))
                         text= "- <b><font color=\"#d62323\">[" + history.get("datetime") + "]</font>: </b> Force Completed by "+history.get("user")+"";
                        else if (history.get("action").toString().toLowerCase().equals("no_force"))
                            text= "- <b><font color=\"#d62323\">[" + history.get("datetime") + "]</font>: </b> Force Completed Reverted by "+history.get("user")+"";
                        else if (history.get("action").toString().toLowerCase().equals("comment_added"))
                            text= "- <b><font color=\"#FE9128\">[" + history.get("datetime") + "]</font>: </b> Comment added for item "+history.get("item_type")+" by "+history.get("user")+"";
                        else if (history.get("action").toString().toLowerCase().equals("comment_edited"))
                            text= "- <b><font color=\"#FE9128\">[" + history.get("datetime") + "]</font>: </b> Comment edited for item "+history.get("item_type")+" by "+history.get("user")+"";


                        histText.setText(Html.fromHtml(text));

                        lin.addView(histText);
                    /*
                    historyObj.put("user", username);
                    historyObj.put("action","Delivered");
                    historyObj.put("organisation",username);*/

                    }
                    logsView.addView(lin);
                    logsLayout.setVisibility(View.VISIBLE);


                    scrollView.postDelayed(new Runnable() {
                        public void run() {
                            scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    }, 100L);

                }
                else if (logsLayout.getVisibility() == View.VISIBLE)
                {
                    logsBtn.setText("Show Logs");
                    logsView.removeAllViews();
                    logsLayout.setVisibility(View.GONE);

                    //scrollView.fullScroll(View.FOCUS_UP);
                }

            }

        });
    }


    private AdapterView.OnItemClickListener onItemClickListener() {

        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.d("POSITION", i + "");
                Intent intent = new Intent(getApplicationContext(), EditItemView.class);
                ItemObject itemObject = itemAdapter.getList().get(i);
                intent.putExtra("ITEM", itemObject);
                intent.putExtra("DIST_ID", distributionObject.getDocID());
                intent.putExtra("position", i);
                startActivityForResult(intent, EDIT_ITEM_REQUEST);
            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == 1) {
                ItemObject itemObject = (ItemObject)data.getSerializableExtra("ITEM");
                int pos = data.getIntExtra("position", 0);
                this.itemAdapter.editItemToList(itemObject, pos);
                if (itemObject.getDelivered() != currentDistValue) {
                    currentDistValue = itemObject.getDelivered();
                    CouchBaseManager.getInstance(getApplicationContext()).editDist(distributionObject, CouchBaseManager.DistStat.NONE);
                    LogObject logObject = (LogObject) data.getSerializableExtra("LOG");
                    logObject.setPlace(distributionObject.getPcode().getPcodeName());
                    saveToLogs(logObject);
                }

            }
        }

    }

    public void saveToLogs(LogObject logObject)
    {
        String username = UserPrefs.getUsername(getApplicationContext());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = GregorianCalendar.getInstance();
        String currentTimeString = dateFormatter.format(calendar.getTime());
        String i = "LOG2:"+username+":"+currentTimeString;


        boolean checkID =  CouchBaseManager.getInstance(getApplicationContext()).checkDocumentFromCode("LOG2:" + username + ":" + currentTimeString);

        if (!checkID)
        {
            CouchBaseManager.getInstance(getApplicationContext()).addLog(logObject);
        }
        else
        {
            CouchBaseManager.getInstance(getApplicationContext()).editLog(logObject);
        }

    }



}
