package com.unicefwinterizationplatform.winterization_android;

import android.app.ActionBar;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.*;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.couchbase.lite.*;
import com.couchbase.lite.android.AndroidContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;

import java.util.*;


public class LoginActivity extends ActionBarActivity {

    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Map<String,String> usermap = UserPrefs.getUserProfile(getApplicationContext());



        Button loginButton = (Button)this.findViewById(R.id.button_login);
        Button settingsButton = (Button)this.findViewById(R.id.button_settings);
        loginButton.setOnClickListener(loginListener());
        EditText usernameText = (EditText) findViewById(R.id.editText_username);
        usernameText.setText(usermap.get("user"));
        EditText passwordText = (EditText) findViewById(R.id.editText_password);
        passwordText.setText(usermap.get("pass"));
        TextView versionText = (TextView)findViewById(R.id.textView);


        settingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SettingsListActivity.class);
                startActivity(intent);


            }
        });

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            versionText.setText("version " +pInfo.versionName);
        }
        catch(Exception e){}

        try {
          //  CouchBaseManager.getInstance(this).startCBLite();
        }
        catch (Exception e)
        {

        }
    }

  private View.OnClickListener loginListener()
    {
        return  new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String name= "";
                ArrayList<PrefObject> prefList = UserPrefs.readSettingsList(getApplicationContext());
                boolean completed = false;
                for (Iterator<PrefObject> it = prefList.iterator(); it.hasNext(); ) {

                    PrefObject pref = it.next();
                    String prefName = pref.getPrefName();

                    if (pref.isEnabled()) {
                        name = prefName;
                        completed = true;
                        break;

                    }
                }

                if(completed == true) {

                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                    //String url = sharedPrefs.getString(name + "ServerURL", "");


                    EditText usernameText = (EditText) findViewById(R.id.editText_username);
                    String username = usernameText.getText().toString();
                    EditText passwordText = (EditText) findViewById(R.id.editText_password);
                    String password = passwordText.getText().toString();

                    Map<String, String> usermap = UserPrefs.getUserProfile(context);


                    if (!usermap.get("user").toLowerCase().equals(username.toLowerCase())) {

                        UserPrefs.setViewIncrement(context);

                    }
                    UserPrefs.storeUserProfile(username, password, getApplicationContext());

                        ArrayList<QueryRow> userArray = CouchBaseManager.getInstance(context).getRowsWithView("byUsername", "user");
                    QueryRow selectedRow = null;
                    for (QueryRow row : userArray) {
                        Document doc = row.getDocument();

                        String user = doc.getCurrentRevision().getProperty("username").toString();
                        String pass = doc.getCurrentRevision().getProperty("password").toString();
                        ArrayList<String> assistanceType = (ArrayList<String>)doc.getCurrentRevision().getProperty("assistance_type");
                        ArrayList<String> locations = (ArrayList<String>)doc.getCurrentRevision().getProperty("locations");
                        String locationType = doc.getCurrentRevision().getProperty("location_type").toString();



                        if (username.toLowerCase().equals(user.toLowerCase()) && password.equals(pass)) {
                            selectedRow = row;
                            UserPrefs.storeAssistanceType(assistanceType,context);
                            UserPrefs.storeLocations(locations,locationType,context);

                            try {
                                String org = UserPrefs.getUsername(getApplicationContext());

                                CouchBaseManager.getInstance(getApplicationContext()).startCBLite(org);
                            } catch (Exception e) {
                                Log.e("", e.getMessage());
                            }
                            break;
                        }

                    }

                    if (selectedRow != null) {
                        //Set User prefs
                        Document doc = selectedRow.getDocument();
                        JSONObject userObj = new JSONObject(doc.getCurrentRevision().getProperties());
                        UserPrefs.storeUserData(userObj, getApplicationContext());
                      //  ArrayList<String> roles = (ArrayList<String>) doc.getCurrentRevision().getProperty("roles");
                       // UserPrefs.storeLoginData(roles.get(0), getApplicationContext());
                        Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        //   if (roles.get(0).equals(Constants.ROLE_KITS_ASSESSOR))
                        //    {
                        //new AsyncCaller().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong username or password.",
                                Toast.LENGTH_LONG).show();
                    }


            } else{

                Toast.makeText(getApplicationContext(), "Please create or enable an environment.",
                            Toast.LENGTH_LONG).show();

                }

            }
        };
    }


    private class AsyncCaller extends android.os.AsyncTask<Void, Void, Void>
    {

        ProgressDialog pdLoading = new ProgressDialog(LoginActivity.this);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
              pdLoading.setMessage("\tLoading Site Data");
              pdLoading.show();
              pdLoading.setCancelable(false);
              pdLoading.setCanceledOnTouchOutside(false);
        }
        @Override
        protected Void doInBackground(Void... params) {

            //CouchBaseManager.getInstance(getApplicationContext()).createBarcodeView();
            // CouchBaseManager.getInstance(getApplicationContext()).createIDView();
            //CouchBaseManager.getInstance(getApplicationContext()).getVersioningView();

         //   UserPrefs.storeLatestVersionData(  CouchBaseManager.getInstance(getApplicationContext()).getLatestVersion(),getApplicationContext());
         //   CouchBaseManager.getInstance(getApplicationContext()).getBarcodeRange("byBarcodeAsRange","barcodes");
           String role = UserPrefs.getUserPref(getApplicationContext());
            if (role.equals(Constants.ROLE_KITS_ASSESSOR)) {
                CouchBaseManager.getInstance(getApplicationContext()).setPcodeEnumerator();
                CouchBaseManager.getInstance(getApplicationContext()).setSDCEnumerator();
               // CouchBaseManager.getInstance(getApplicationContext()).setSDCList();

            }
            else
            {
                //CouchBaseManager.getInstance(getApplicationContext()).setSDCList();
            }


            CouchBaseManager.getInstance(getApplicationContext()).createIDView(Constants.ROLE_KITS_ASSESSOR,"");
            CouchBaseManager.getInstance(getApplicationContext()).createPcodeView(Constants.ROLE_KITS_ASSESSOR);
            CouchBaseManager.getInstance(getApplicationContext()).createPhoneNumberView(Constants.ROLE_VOUCHERS_DISTRIBUTOR,"");
            CouchBaseManager.getInstance(getApplicationContext()).createBarcodeView(Constants.ROLE_KITS_ASSESSOR);




            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread
            //Intent intent = new Intent(getApplicationContext(), TaskSelectionActivity.class);
            Intent intent = new Intent(getApplicationContext(), TabActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
             pdLoading.dismiss();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {

        // super.onBackPressed(); // Comment this super call to avoid calling finish()
    }

}
