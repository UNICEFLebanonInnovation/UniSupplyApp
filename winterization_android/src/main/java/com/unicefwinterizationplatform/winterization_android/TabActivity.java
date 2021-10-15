package com.unicefwinterizationplatform.winterization_android;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.internal.widget.ScrollingTabContainerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

import com.couchbase.lite.CouchbaseLiteException;

import java.util.ArrayList;
import java.util.Iterator;

public class TabActivity extends FragmentActivity {
    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mainlayout);
        UserPrefs.firstSyncDone(getApplicationContext());
       String username = UserPrefs.getUsername(getApplicationContext());
       ArrayList<String> assistanceType = UserPrefs.getAssistanceType(getApplicationContext());
       // ActionBar actionBar = getActionBar();
       // actionBar.show();

        ArrayList<PrefObject> prefList = UserPrefs.readSettingsList(getApplicationContext());
        String prefName = "";
        for (Iterator<PrefObject> it = prefList.iterator(); it.hasNext(); ) {

            PrefObject pref = it.next();


            if (pref.isEnabled())
            {
                prefName = pref.getPrefName();
                break;

            }
        }

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        //KEEPING THIS FOR NOW
        boolean assessments = true;//sharedPrefs.getBoolean(prefName+"assessment",false);

        this.setTitle("UniSupply | "+username);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        Bundle bundle = new Bundle();
        bundle.putBoolean("ASSESSMENTS",assessments);

        if (assessments == false) {
            mTabHost.addTab(
                    mTabHost.newTabSpec("pending").setIndicator("Started", null),
                    PendingTab.class, bundle);
            mTabHost.addTab(
                    mTabHost.newTabSpec("completed").setIndicator("Finished", null),
                    CompletedTab.class, null);
            mTabHost.addTab(
                    mTabHost.newTabSpec("data").setIndicator("Reports", null),
                    DataTab.class,bundle);
        }

        else{

            mTabHost.addTab(
                    mTabHost.newTabSpec("pending").setIndicator("Cash", null),
                    PendingTab.class,bundle);

            if (assistanceType.size() == 2) {
                mTabHost.addTab(
                        mTabHost.newTabSpec("kits").setIndicator("Kits", null),
                        KitsTab.class, bundle);
            }


            mTabHost.addTab(
                    mTabHost.newTabSpec("data").setIndicator("Reports", null),
                    DataTab.class,bundle);

        }



        mTabHost.addTab(
                mTabHost.newTabSpec("main").setIndicator("Sync", null),
                MainTab.class,null);
       /*- mTabHost.addTab(
                mTabHost.newTabSpec("stock").setIndicator("Stock Level",null),
                PendingTab.class, null);*/


        if (getIntent().getAction() != null && getIntent().getAction().equals("kits"))
            mTabHost.setCurrentTab(1);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_btn, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                this.finish();
                return true;


            case R.id.action_logs:
                Intent backIntent3 = new Intent(getApplicationContext(), DailyLogsActivity.class);
                startActivity(backIntent3);
                return true;

            case R.id.action_signout:
                Intent backIntent = new Intent(getApplicationContext(), LoginActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
               String username = UserPrefs.getUsername(getApplicationContext());

                UserPrefs.storeUserProfile(username,"",getApplicationContext());
                startActivity(backIntent);
                return true;

            case R.id.action_settings:
                Intent backIntent2 = new Intent(getApplicationContext(), SettingsListActivity.class);
                startActivity(backIntent2);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

}


