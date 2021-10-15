package com.unicefwinterizationplatform.winterization_android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tarek on 9/7/15.
 */
public class SettingsListActivity extends Activity {

    final Context context = this;
    ArrayList<PrefObject> prefList;
    PrefsAdapter adapter;
    ListView prefView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final ActionBar actionBar = getActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        prefList = UserPrefs.readSettingsList(context);
        adapter = new PrefsAdapter(this);
        adapter.addList(prefList);// new ListAdapter(this, android.R.layout.simple_list_item_1, arr);
        prefView = (ListView) this.findViewById(R.id.pref_list);
        prefView.setAdapter(adapter);


        TextView noItemsText = (TextView) this.findViewById(R.id.no_items);

        if (prefView.getCount() >0)
            noItemsText.setVisibility(View.GONE);

        prefView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(context, SettingsActivity.class);
               PrefObject pref =  adapter.getItem(i);
                String prefName = pref.getPrefName();
                intent.putExtra("PREF_MAP",pref);
                //intent.putExtra("PREF_NAME",prefName);
                //intent.putExtra("PREF_MAP",pref);
                //intent.putExtra("POS",i);
                //Bundle listBdl = new Bundle();
                //listBdl.putSerializable("LIST", adapter.getList());
                //intent.putExtras(listBdl);
                startActivityForResult(intent, 0);


            }
        });

        prefView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.alert_generic, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final LinearLayout linearLayout = (LinearLayout) promptsView.findViewById(R.id.layout_gen);

                final TextView textTitle = new TextView(context);
                textTitle.setText("Are you sure you want to delete '"+ prefList.get(position).getPrefName()+"'?");
                textTitle.setTextSize(20);
                textTitle.setTextColor(getResources().getColor(R.color.black));
                textTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textTitle.setGravity(Gravity.CENTER);

                linearLayout.addView(textTitle);
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)

                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        adapter.deleteItemFromList(position);

                                        dialog.cancel();

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();


                return true;
            }
        });


        final ImageButton addBtn = (ImageButton) findViewById(R.id.addButton);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.alert_editcomment, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);


                final TextView textTitle = (TextView) promptsView
                        .findViewById(R.id.text_edit);
                textTitle.setText("Type name of preference to proceed");



                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(20);
                userInput.setFilters(filterArray);


                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)

                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        if (!userInput.getText().toString().isEmpty()) {

                                            boolean foundDup = false;

                                            //JAVA
                                            String prefText = userInput.getText().toString().trim();
                                            Log.e("TESI", prefText);
                                            prefText = prefText.replaceAll("\\s", "_");
                                            prefText = prefText.replaceAll("\\W","");
                                            Log.e("TEST1", prefText);

                                            for (PrefObject prefObject : prefList){
                                                if (prefObject.getPrefName().toLowerCase().equals(userInput.getText().toString().toLowerCase())){
                                                    foundDup = true;
                                                    break;
                                                }
                                            }

                                            if (!foundDup) {

                                                //PrefObject pref = new PrefObject(userInput.getText().toString(), false);
                                                PrefObject pref = new PrefObject();

                                                pref.setPrefName(prefText);
                                                pref.setBaseUrl("");
                                                pref.setPort("");
                                                pref.setCbName("");
                                                pref.setUsername("");
                                                pref.setPassword("");
                                                pref.setEnabled(false);
                                                UserPrefs.addSettingstoList(context,pref);

                                                adapter.getList().add(pref);
                                                adapter.notifyDataSetChanged();
                                                //adapter.addList(prefList);

                                                Intent intent = new Intent(context, SettingsActivity.class);

                                                intent.putExtra("PREF_NAME", prefText);
                                                intent.putExtra("PREF_MAP", pref);
                                                //Bundle listBdl = new Bundle();
                                                //listBdl.putSerializable("LIST", adapter.getList());
                                                //intent.putExtras(listBdl);
                                                //intent.putExtra("POS", adapter.getCount() - 1);
                                                startActivityForResult(intent, 0);

                                                dialog.cancel();
                                            }
                                            else
                                            {
                                                Toast.makeText(getApplicationContext(),"Preference already exists. Please create a new preference.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(),"Please enter a name for your preference in order to proceed", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }



        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == 0) {
                PrefObject prefObject = (PrefObject)data.getSerializableExtra("PREF");
                //int pos = data.getIntExtra("POS",0);
               // ArrayList<PrefObject> prefs = (ArrayList<PrefObject>) data.getSerializableExtra("LIST");

               // this.adapter.editItemToList(prefObject, pos);
               //   this.adapter.addList(prefs);
                prefList = UserPrefs.readSettingsList(context);
                this.adapter.addList(prefList);
                this.adapter.notifyDataSetChanged();
                TextView noItemsText = (TextView) this.findViewById(R.id.no_items);

                if (prefView.getCount() >0)
                    noItemsText.setVisibility(View.GONE);
                //UserPrefs.writeSQList(context,this.adapter.getList());

            }
        }

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                 finish();
                return true;


        }

        return true;
    }

}


