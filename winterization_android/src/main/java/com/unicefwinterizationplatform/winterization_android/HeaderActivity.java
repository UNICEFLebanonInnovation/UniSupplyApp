package com.unicefwinterizationplatform.winterization_android;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by Tarek on 9/27/2014.
 */
public class HeaderActivity extends Activity {
    BroadcastReceiver reciever;
    Location location;
  //  TextView accuracyView;
    String role;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        String username = UserPrefs.getUsername(getApplicationContext());
        this.setTitle("UniSupply | "+username);
        role = UserPrefs.getUserPref(this);

        //registerReceiver(reciever, new IntentFilter(LocationService.GPS_ACCURACY));

      //  actionBar.setDisplayOptions(actionBar.getDisplayOptions()
                //| ActionBar.DISPLAY_SHOW_CUSTOM);
      //  accuracyView = new TextView(this);
        //accuracyView.setScaleType(TextView.ScaleType.CENTER);
       //  accuracyView.setText("TEST");
      //  ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
       //         ActionBar.LayoutParams.WRAP_CONTENT,
       //         ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
       //         | Gravity.CENTER_VERTICAL);
       // layoutParams.rightMargin = 40;
      //  accuracyView.setLayoutParams(layoutParams);
        //actionBar.setCustomView(accuracyView);



         reciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                    location = intent.getParcelableExtra("LOCATION");
                    Log.d("ACCURACY VAL", location.getAccuracy() + "");
                     // Toast.makeText(HeaderActivity.this,
                      //        location + "",
                      //        Toast.LENGTH_LONG).show();
          //      actionBar.setSubtitle(location.getAccuracy()+ "");


                    //accuracyView.setText(location.getAccuracy() + "");
            }
        };
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(reciever, new IntentFilter(LocationService.GPS_ACCURACY));

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(reciever);


    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

}
