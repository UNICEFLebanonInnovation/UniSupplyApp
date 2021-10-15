package com.unicefwinterizationplatform.winterization_android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.style.UpdateLayout;
import android.util.Log;
import android.database.sqlite.*;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


/**
 * Created by Tarek on 9/30/2014.
 */
public class UserPrefs {


    public static void removeSettingsFromList(String prefName, Context ctx){

        SQLiteDatabase db = ctx.openOrCreateDatabase("settingsdb",ctx.MODE_PRIVATE,null);

        db.execSQL(" DELETE FROM settings_table WHERE pref_name='"+prefName+"';");
        db.close();

    }

    public static ArrayList<PrefObject> readSettingsList(Context ctx){


        SQLiteDatabase db = ctx.openOrCreateDatabase("settingsdb",ctx.MODE_PRIVATE,null);

        db.execSQL("CREATE TABLE IF NOT EXISTS settings_table(pref_name VARCHAR PRIMARY KEY," +
                                                            "base_url VARCHAR," +
                                                            "port VARCHAR," +
                                                            "cb_name VARCHAR," +
                                                            "username VARCHAR," +
                                                            "password VARCHAR," +
                                                            "enabled INTEGER);");

        Cursor cursor = db.rawQuery("SELECT * FROM settings_table", null);

        if (cursor.getCount() == 0)
        {
            return new ArrayList<PrefObject>();
        }
        else
        {
            ArrayList<PrefObject> prefs = new ArrayList<PrefObject>();
            while (cursor.moveToNext())
            {
                PrefObject prefObject = new PrefObject();
                prefObject.setPrefName(cursor.getString(0));
                prefObject.setBaseUrl(cursor.getString(1));
                prefObject.setPort(cursor.getString(2));
                prefObject.setCbName(cursor.getString(3));
                prefObject.setUsername(cursor.getString(4));
                prefObject.setPassword(cursor.getString(5));
                prefObject.setEnabled(cursor.getInt(6) != 0);
                prefs.add(prefObject);
            }
            db.close();
            return prefs;
        }
    }


    public static PrefObject getEnabledSettings(Context ctx){


        SQLiteDatabase db = ctx.openOrCreateDatabase("settingsdb",ctx.MODE_PRIVATE,null);

        db.execSQL("CREATE TABLE IF NOT EXISTS settings_table(pref_name VARCHAR PRIMARY KEY," +
                "base_url VARCHAR," +
                "port VARCHAR," +
                "cb_name VARCHAR," +
                "username VARCHAR," +
                "password VARCHAR," +
                "enabled INTEGER);");

        Cursor cursor = db.rawQuery("SELECT * FROM settings_table where enabled=1", null);

        PrefObject prefObject = new PrefObject();


        while (cursor.moveToNext())
        {
            prefObject.setPrefName(cursor.getString(0));
            prefObject.setPrefName(cursor.getString(0));
            prefObject.setBaseUrl(cursor.getString(1));
            prefObject.setPort(cursor.getString(2));
            prefObject.setCbName(cursor.getString(3));
            prefObject.setUsername(cursor.getString(4));
            prefObject.setPassword(cursor.getString(5));
            prefObject.setEnabled(cursor.getInt(6) != 0);            }

        db.close();

        return prefObject;


    }

    public static PrefObject getSettings(Context ctx, String prefName){


        SQLiteDatabase db = ctx.openOrCreateDatabase("settingsdb",ctx.MODE_PRIVATE,null);

        db.execSQL("CREATE TABLE IF NOT EXISTS settings_table(pref_name VARCHAR PRIMARY KEY," +
                "base_url VARCHAR," +
                "port VARCHAR," +
                "cb_name VARCHAR," +
                "username VARCHAR," +
                "password VARCHAR," +
                "enabled INTEGER);");

        Cursor cursor = db.rawQuery("SELECT * FROM settings_table where pref_name='"+prefName+"'", null);

        PrefObject prefObject = new PrefObject();


        while (cursor.moveToNext())
            {
                prefObject.setPrefName(cursor.getString(0));
                prefObject.setPrefName(cursor.getString(0));
                prefObject.setBaseUrl(cursor.getString(1));
                prefObject.setPort(cursor.getString(2));
                prefObject.setCbName(cursor.getString(3));
                prefObject.setUsername(cursor.getString(4));
                prefObject.setPassword(cursor.getString(5));
                prefObject.setEnabled(cursor.getInt(6) != 0);            }

            db.close();

            return prefObject;


    }

    public  static void updatedSettings(Context ctx, PrefObject prefObject)
    {
        SQLiteDatabase db = ctx.openOrCreateDatabase("settingsdb",ctx.MODE_PRIVATE,null);

        db.execSQL("CREATE TABLE IF NOT EXISTS settings_table(pref_name VARCHAR PRIMARY KEY," +
                "base_url VARCHAR," +
                "port VARCHAR," +
                "cb_name VARCHAR," +
                "username VARCHAR," +
                "password VARCHAR," +
                "enabled INTEGER);");

        int en = 0;
        if (prefObject.isEnabled())
            en = 1;
        else
            en = 0;


        db.execSQL("UPDATE settings_table "+
                "SET base_url='"+prefObject.getBaseUrl()+"',"+
                "port='"+prefObject.getPort()+"',"+
                "cb_name='"+prefObject.getCbName()+"',"+
                "username='"+prefObject.getUsername()+"',"+
                "password='"+prefObject.getPassword()+"',"+
                "enabled="+en+" "+
                "WHERE pref_name='"+prefObject.getPrefName()+"';");


        db.close();
    }


    public  static void disableSettings(Context ctx, String prefName)
    {
        SQLiteDatabase db = ctx.openOrCreateDatabase("settingsdb", ctx.MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS settings_table(pref_name VARCHAR PRIMARY KEY," +
                "base_url VARCHAR," +
                "port VARCHAR," +
                "cb_name VARCHAR," +
                "username VARCHAR," +
                "password VARCHAR," +
                "enabled INTEGER);");


        db.execSQL("UPDATE settings_table "+
                "SET enabled=0 "+
                "WHERE pref_name='"+prefName+"';");

        db.close();
    }

    public  static void enableSettings(Context ctx, String prefName)
    {
        SQLiteDatabase db = ctx.openOrCreateDatabase("settingsdb", ctx.MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS settings_table(pref_name VARCHAR PRIMARY KEY," +
                "base_url VARCHAR," +
                "port VARCHAR," +
                "cb_name VARCHAR," +
                "username VARCHAR," +
                "password VARCHAR," +
                "enabled INTEGER);");


        Cursor cursor = db.rawQuery("SELECT * FROM settings_table where enabled=1", null);

        while(cursor.moveToNext())
        {
            db.execSQL("UPDATE settings_table "+
                    "SET enabled=0 "+
                    "WHERE pref_name='"+cursor.getString(0)+"';");
        }

        db.execSQL("UPDATE settings_table "+
                "SET enabled=1 "+
                "WHERE pref_name='"+prefName+"';");

        db.close();
    }

    public static void addSettingstoList(Context ctx, PrefObject prefObject) {

        SQLiteDatabase db = ctx.openOrCreateDatabase("settingsdb", ctx.MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS settings_table(pref_name VARCHAR PRIMARY KEY," +
                "base_url VARCHAR," +
                "port VARCHAR," +
                "cb_name VARCHAR," +
                "username VARCHAR," +
                "password VARCHAR," +
                "enabled INTEGER);");


        db.execSQL("INSERT INTO settings_table VALUES('" + prefObject.getPrefName() + "','" +
                    prefObject.getBaseUrl() + "','" +
                    prefObject.getPort() + "','" +
                    prefObject.getCbName() + "','" +
                    prefObject.getUsername() + "','" +
                    prefObject.getPassword() + "'," +
                    "0);");


        db.close();
    }


    public static ArrayList<PrefObject> readSQLList(Context ctx){


        SQLiteDatabase db = ctx.openOrCreateDatabase("settingsdb",ctx.MODE_PRIVATE,null);

        db.execSQL("CREATE TABLE IF NOT EXISTS settings_table(pref_name VARCHAR,enabled INTEGER);");

        Cursor cursor = db.rawQuery("SELECT * FROM settings_table", null);

        if (cursor.getCount() == 0)
        {
            db.close();

            return new ArrayList<PrefObject>();
        }
        else
        {
            ArrayList<PrefObject> prefs = new ArrayList<PrefObject>();
            while (cursor.moveToNext())
            {
                PrefObject prefObject = new PrefObject();
                prefObject.setPrefName(cursor.getString(0));
                prefObject.setEnabled(cursor.getInt(1) != 0);
                prefs.add(prefObject);
            }

            db.close();

            return prefs;

        }
    }

    public static void writeSQList(Context ctx,ArrayList<PrefObject> list){

        SQLiteDatabase db = ctx.openOrCreateDatabase("settingsdb",ctx.MODE_PRIVATE,null);

        db.execSQL("CREATE TABLE IF NOT EXISTS settings_table(pref_name VARCHAR,enabled INTEGER);");

        db.execSQL("delete from settings_table");

        for (PrefObject prefObject : list)
        {
            String prefName = prefObject.getPrefName();
            int en = 0;
            if (prefObject.isEnabled())
                en = 1;
            else
                en = 0;

            db.execSQL("INSERT INTO settings_table VALUES('"+prefName+"',"+
                    en+");");
        }

        db.close();
    }


    public static ArrayList<PrefObject> readList(Context c){//
        try{
            FileInputStream fis = c.openFileInput("NAME");
            ObjectInputStream is = new ObjectInputStream(fis);
            ArrayList<PrefObject> list = (ArrayList<PrefObject>)is.readObject();
            //fis.close();
            //is.close();
            return list;

        }catch(EOFException e){
            Log.e("PRINT", e.getMessage());
            return new ArrayList<PrefObject>();
        } catch(IOException e){
            Log.e("PRINT", e.getMessage());
            return new ArrayList<PrefObject>();
        }catch(Exception ex){
            ex.printStackTrace();
            Log.e("PRINT", ex.getMessage());
            return new ArrayList<PrefObject>();
        }
    }

    public static void writeList(Context c, ArrayList<PrefObject> list){
        try{
            FileOutputStream fos = c.openFileOutput("NAME", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(list);
            os.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void firstSyncDone( Context context)
    {
        final SharedPreferences prefs = context.getSharedPreferences("Session",0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstSync", true);
        editor.commit();
    }

    public static boolean isFirstSyncDone(Context context)
    {
        final SharedPreferences prefs = context.getSharedPreferences("Session",0);
        return prefs.getBoolean("firstSync", false);
    }


    public static void storeLoginData(String pref, Context context)
    {


        final SharedPreferences prefs = context.getSharedPreferences("Session",0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("pref", pref);
        editor.commit();
    }

    public static void storeAssistanceType(ArrayList<String> assistanceType, Context context)
    {
        final SharedPreferences prefs = context.getSharedPreferences("Session",0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("assistance_type1", new HashSet<String>(assistanceType));

        editor.commit();
    }

    public static ArrayList<String> getAssistanceType( Context context)
    {
        final SharedPreferences prefs = context.getSharedPreferences("Session",0);

        ArrayList<String> assistanceType = new ArrayList<String>(prefs.getStringSet("assistance_type1",null));
        return assistanceType;
    }

    public static void storeLocations(ArrayList <String> locations,String locationType, Context context)
    {
        final SharedPreferences prefs = context.getSharedPreferences("Session",0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet ("locations", new HashSet<String>(locations));
        editor.putString("location_type", locationType);

        editor.commit();
    }

    public static Map<String,Object> getLocations( Context context)
    {
        final SharedPreferences prefs = context.getSharedPreferences("Session",0);
        Map<String,Object> usermap = new HashMap<String, Object>();
        usermap.put("locations",new ArrayList<String>(prefs.getStringSet("locations",null)));
        usermap.put("location_type",prefs.getString("location_type", ""));

        return usermap;
    }

    public static void storeUserProfile(String username,String password, Context context)
    {


        final SharedPreferences prefs = context.getSharedPreferences("Session",0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        //editor.putString("assistance_type", password);

        editor.commit();
    }

    public static Map<String,String> getUserProfile(Context context)
    {
        final SharedPreferences prefs = context.getSharedPreferences("Session",0);
        Map<String,String> usermap = new HashMap<String, String>();

        usermap.put("user",prefs.getString("username", "")) ;
        usermap.put("pass",prefs.getString("password", ""));
        usermap.put("assistance_type",prefs.getString("assistance_type", ""));

        return usermap;

    }


    public static void setViewIncrement(Context context)
    {
        final SharedPreferences prefs = context.getSharedPreferences("Inc",0);
        //  editor.putString("username", username);
        String inc =  prefs.getString("INCREMENT", "0");
        int increment = Integer.parseInt(inc) + 1;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("INCREMENT", String.valueOf(increment));
        editor.commit();


    }

    public static String getViewIncrement( Context context)
    {
        final SharedPreferences prefs = context.getSharedPreferences("Inc",0);
        return prefs.getString("INCREMENT", "0");
    }


    public static void storeLatestVersionData(String version, Context context)
    {
        final SharedPreferences prefs = context.getSharedPreferences("Version",0);
        SharedPreferences.Editor editor = prefs.edit();
        //  editor.putString("username", username);
        editor.putString("VERSION", version.toString());
        editor.commit();
    }

    public static String getLatestVersionData( Context context)
    {
        final SharedPreferences prefs = context.getSharedPreferences("Version",0);
        //  editor.putString("username", username);
        return prefs.getString("VERSION", "1.4");


    }

    public static void storeUserData(JSONObject pref, Context context)
    {


        final SharedPreferences prefs = context.getSharedPreferences("Session",0);
        SharedPreferences.Editor editor = prefs.edit();
      //  editor.putString("username", username);

        editor.putString("USERDATA", pref.toString());
        editor.commit();
    }

    public static void incrementDeviceAssessment(Context context,String siteName)
    {
        final SharedPreferences prefs = context.getSharedPreferences("SITE_ASSESSMENT",0);
        int deviceIncrement = prefs.getInt("DEVICE_INCREMENT_" + siteName, 0);
        SharedPreferences.Editor editor = prefs.edit();
        deviceIncrement++;
        editor.putInt("DEVICE_INCREMENT_" + siteName, deviceIncrement);

        editor.commit();
    }

    public static int getDeviceIncrement(Context context, String siteName)
    {

        final SharedPreferences prefs = context.getSharedPreferences("SITE_ASSESSMENT",0);

        return prefs.getInt("DEVICE_INCREMENT_"+ siteName, 0);
    }


    public static String getUsername(Context context)
    {

        final SharedPreferences prefs = context.getSharedPreferences("Session",0);

        return prefs.getString("username", "not available");
    }

    public static String getUserPref(Context context)
    {
        final SharedPreferences prefs = context.getSharedPreferences("Session",0);
        return prefs.getString("pref", "");
    }

    public static JSONObject getUserData(Context context)
    {
        final SharedPreferences prefs = context.getSharedPreferences("Session",0);
        try {


            return  new JSONObject(prefs.getString("USERDATA", ""));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getOrganisation(Context context)
    {
        final SharedPreferences prefs = context.getSharedPreferences("Session",0);
        try {


            JSONObject o =  new JSONObject(prefs.getString("USERDATA", ""));

          return  o.get("organisation").toString();


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
