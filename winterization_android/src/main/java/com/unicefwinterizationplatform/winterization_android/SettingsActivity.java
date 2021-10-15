package com.unicefwinterizationplatform.winterization_android;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Tarek on 8/29/15.
 */
public class SettingsActivity extends PreferenceActivity   {

    PrefsFragment prefsFragment;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        final ActionBar actionBar = getActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        super.onCreate(savedInstanceState);

       PrefObject prefMap = (PrefObject)getIntent().getSerializableExtra("PREF_MAP");
       //ArrayList<PrefObject> prefList = (ArrayList<PrefObject>)getIntent().getSerializableExtra("LIST");

       prefsFragment = new PrefsFragment();

        Bundle bd = new Bundle();
        bd.putSerializable("PREF_MAP",prefMap);
        //bd.putSerializable("PREF_LIST", prefList);
        //bd.putInt("POS",getIntent().getIntExtra("POS",0));
        prefsFragment.setArguments(bd);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                prefsFragment).commit();

    }

       @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
    }


    public void backPressed(){

        PrefObject pref = prefsFragment.getPrefMap();
        Intent intent = new Intent();
        intent.putExtra("PREF", pref);
       // int pos = getIntent().getIntExtra("POS",0);
        //intent.putExtra("POS",pos);
        //Bundle listBdl = new Bundle();

      //  listBdl.putSerializable("LIST", prefsFragment.getPrefList());
      //  intent.putExtras(listBdl);

        setResult(0, intent);
        // Navigate "up" the demo structure to the launchpad activity.
        // See http://developer.android.com/design/patterns/navigation.html for more.
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:

          backPressed();
                return true;


        }

      return true;
    }

    @Override
    public void onBackPressed() {

        backPressed();

        super.onBackPressed();


    }
}
