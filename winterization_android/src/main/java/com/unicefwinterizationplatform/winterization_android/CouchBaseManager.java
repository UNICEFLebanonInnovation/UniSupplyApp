package com.unicefwinterizationplatform.winterization_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.couchbase.lite.*;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.auth.BasicAuthenticator;
import com.couchbase.lite.internal.InterfaceAudience;
import com.couchbase.lite.internal.RevisionInternal;
import com.couchbase.lite.replicator.PullerInternal;
import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.support.CouchbaseLiteHttpClientFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

//import org.codehaus.jackson.map.MapperConfig;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jackson.map.PropertyNamingStrategy;
//import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 *
 * Created by Tarek on 10/18/2014.
 */

public class CouchBaseManager implements Replication.ChangeListener {


    Replication pullReplication;
    Replication pushReplication;

    public static  String DATABASE_NAME;
    public static  String designDocName;
    public static  String pendingViewName;
    public static  String logsViewName;

    public static  String assessmentViewName;
    public static  String kitsViewName;

    public static  String aggViewName;
    public static  String completedViewName;

    public static String TAG;
    public static  String SYNC_URL;
    public static  String USER_NAME;
    public static  String PASSWORD;


    private static CouchBaseManager sInstance;
    ArrayList<PCodeObject> pcodeList;
    ArrayList<PCodeObject> sdcList;

    Query pcodeEnum;
    Query sdcEnum;

    //couch internals
    protected static Manager manager;
    private Database database;
    private LiveQuery liveQuery;

    private int totalPullDocs = 0;
    private int pullDocs = 0;

    private int totalPushDocs = 0;
    private int pushDocs = 0;

    String netStatus = "Offline";

    public ArrayList<PCodeObject> getPcodeList()
    {
        return pcodeList;
    }

    public ArrayList<PCodeObject> getSDCList()
    {
        return sdcList;
    }

    public Query getPcodeEnumerator()
    {
        return pcodeEnum;
    }

    public Query getSDCEnumerator()
    {
        return sdcEnum;
    }

    public Replication getPullReplication() {
        return pullReplication;
    }

    public void setPullReplication(Replication pullReplication) {
        this.pullReplication = pullReplication;
    }

    public Replication getPushReplication() {
        return pushReplication;
    }

    public void setPushReplication(Replication pushReplication) {
        this.pushReplication = pushReplication;
    }

    public void PutCurrentSettings(PrefObject prefObject) throws CouchbaseLiteException
    {

       SYNC_URL ="http://"+prefObject.getBaseUrl()+":"+prefObject.getPort()+"/"+prefObject.getCbName(); //sharedPrefs.getString(pref+"ServerURL","");
//       if (!SYNC_URL.startsWith("http://"))
//       {
//           SYNC_URL = "http://"+SYNC_URL;
//       }
       DATABASE_NAME = prefObject.getPrefName().toLowerCase()+"db";
       USER_NAME = prefObject.getUsername(); //sharedPrefs.getString(pref+"ServerName","");
       PASSWORD =  prefObject.getPassword(); ////sharedPrefs.getString(pref+"ServerPassword","");
       database = manager.getDatabase(DATABASE_NAME);

    }

    public Document getDocumentFromID(String docID)
    {
        return database.getDocument(docID);
    }


    public int getTotalDocs()
    {
       return database.getDocumentCount();
    }


    public boolean checkDocumentFromID(String docID,String username)
    {

        if (database.getExistingDocument(docID+":"+username) == null)
            return false;
        else
            return true;
    }

    public boolean checkDocumentFromCode(String docID)
    {

        if (database.getExistingDocument(docID) == null)
            return false;
        else
            return true;
    }

    public boolean checkDocumentFromID(String docID)
    {

        String username = UserPrefs.getUsername(mContext);
        if (database.getExistingDocument(docID+":"+username) == null)
            return false;
        else
            return true;
    }

    public void setPcodeEnumerator()
    {

        if(pcodeEnum == null ){
            pcodeEnum = getRowsWithEnumerator("byLoction3", "location", "IS");

            try {
                QueryEnumerator q = pcodeEnum.run();
                Log.v("COUNT", q.getCount()+"");
            } catch (CouchbaseLiteException e) {
                Log.v("TEST", e.getMessage());
            }
        }
    }
    public void setSDCEnumerator()
    {
        if(sdcEnum == null) {
            sdcEnum = getRowsWithEnumerator("byLoction4", "location", "SDC");
            try {
                QueryEnumerator q = sdcEnum.run();
                Log.v("COUNT", q.getCount()+"");
            } catch (CouchbaseLiteException e) {
                Log.v("TEST", "TE");
            }
        }

    }

    public static CouchBaseManager getInstance(Context context) {
        if (sInstance == null) {
            //Always pass in the Application Context

            Properties prop = new Properties();
            Resources resources = context.getResources();
            AssetManager assetManager = resources.getAssets();
            try {
                InputStream inputStream = assetManager.open("config.properties");
                prop.load(inputStream);

               //DATABASE_NAME = prop.getProperty("database_name");
                designDocName = prop.getProperty("design_doc");
                pendingViewName = prop.getProperty("pending_view");
                logsViewName = prop.getProperty("logs_view");
                assessmentViewName = prop.getProperty("assessment_view");
                kitsViewName = prop.getProperty("kits_view");
                aggViewName = prop.getProperty("agg_view");
                completedViewName = prop.getProperty("completed_view");
                TAG = prop.getProperty("tag");
              //  SYNC_URL = prop.getProperty("sync_url");
            }
            catch (IOException ex)
            {}

            sInstance = new CouchBaseManager(context.getApplicationContext());



        }

        return sInstance;
    }

    private Context mContext;

    private CouchBaseManager(Context context) {
        mContext = context;
    }


    public void setPullDocs(int pullDocs) {
        this.pullDocs = pullDocs;
    }

    public void setPushDocs(int pushDocs) {
        this.pushDocs = pushDocs;
    }

    public int getPullDocs() {
        return pullDocs;
    }

    public int getPushDocs() {
        return pushDocs;
    }

    public void setTotalPullDocs(int totalPullDocs) {
        this.totalPullDocs = totalPullDocs;
    }

    public void setTotalPushDocs(int totalPushDocs) {
        this.totalPushDocs = totalPushDocs;
    }

    public int getTotalPullDocs() {
        return totalPullDocs;
    }

    public int getTotalPushDocs() {
        return totalPushDocs;
    }


    protected void startCBLite(String username) throws Exception {

     //   manager = new Manager(new AndroidContext(mContext), Manager.DEFAULT_OPTIONS);

        //install a view definition needed by the application
      //  database = manager.getDatabase(DATABASE_NAME);

        startSync(username);

    }

    public void startCBSetUp() throws Exception {

        manager = new Manager(new AndroidContext(mContext), Manager.DEFAULT_OPTIONS);

        //install a view definition needed by the application

        database = manager.getDatabase(DATABASE_NAME);


        /***
         SSL CODE FOR LATER USE
          ***/

        /*
        try {
            Resources resources = mContext.getResources();
            AssetManager am = resources.getAssets();
            InputStream is = am.open("cert.pem");
            // Load CAs from an InputStream
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(is);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
            } finally {
                caInput.close();
            }
            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            SSLSocketFactory sf = new MySSLSocketFactory(keyStore);
            CouchbaseLiteHttpClientFactory f =
                    new CouchbaseLiteHttpClientFactory(database.getPersistentCookieStore());
            f.setSSLSocketFactory(sf);
            manager.setDefaultHttpClientFactory(f);
        }
        catch (Exception ex){

            com.couchbase.lite.util.Log.e("SSLWEL", ex.getMessage());
        }
        */



    }


    public void startSync(String username) {
        totalPullDocs = 0;
        pullDocs = 0;

        totalPushDocs = 0;
        pushDocs = 0;



        URL syncUrl;
        try {
            syncUrl = new URL(SYNC_URL);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }


      //  BasicAuthenticator authenticator = new BasicAuthenticator("unisupply-gateway","W!nT3er!zAtioN");
        BasicAuthenticator authenticator = new BasicAuthenticator(USER_NAME,PASSWORD);

        pullReplication = database.createPullReplication(syncUrl);
        pullReplication.setContinuous(true);
        List<String> channels = new ArrayList<String>();
        channels.add(username);
        channels.add("users");
        channels.add("locations");
        channels.add("surveys");


        pullReplication.setChannels(channels);

        pullReplication.setAuthenticator(authenticator);

        pushReplication = database.createPushReplication(syncUrl);
        pushReplication.setContinuous(true);
        pushReplication.setAuthenticator(authenticator);


        pullReplication.start();
        pushReplication.start();

        pullReplication.addChangeListener(pullChangeListener());
        pushReplication.addChangeListener(pushChangeListener());

    }

    public void restartSync() {
        totalPullDocs = 0;
        pullDocs = 0;

        totalPushDocs = 0;
        pushDocs = 0;
        URL syncUrl;
        try {
            syncUrl = new URL(SYNC_URL);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

//        BasicAuthenticator authenticator = new BasicAuthenticator("user","pass");

        pullReplication = database.createPullReplication(syncUrl);
        pullReplication.setContinuous(true);
        // pullReplication.setAuthenticator(authenticator);

        pushReplication = database.createPushReplication(syncUrl);
        pushReplication.setContinuous(true);
        //  pushReplication.setAuthenticator(authenticator);
        pullReplication.start();
        pushReplication.start();

    }


    public void startPullReplication()
    {
        URL syncUrl;



        try {
            syncUrl = new URL(SYNC_URL);



        BasicAuthenticator authenticator = new BasicAuthenticator(USER_NAME,PASSWORD);


        pullReplication = database.createPullReplication(syncUrl);
        pullReplication.setAuthenticator(authenticator);
        pullReplication.setContinuous(true);
        List<String> channels = new ArrayList<String>();
        channels.add("users");
        channels.add("locations");
        channels.add("surveys");
        pullReplication.setChannels(channels);
        pullReplication.start();
        } catch (MalformedURLException e) {
            Log.e("ERROR", e.getMessage());
        }
    }

    public void stopPullReplication()
    {
        pullReplication.stop();
    }

    public void stopReplication()
    {
        pullReplication.stop();
        pushReplication.stop();

    }


    public boolean isLatestVersion(String version)
    {
        com.couchbase.lite.View viewItem = getVersioningView();
        Query query = viewItem.createQuery();
        try {

            QueryEnumerator  rowList = query.run();
            QueryRow queryRow = rowList.getRow(0);

            Document doc = queryRow.getDocument();


            if( doc.getCurrentRevision().getProperty("version").toString().equals(version))
            {
                return true;
            }
            else
            {
                return false;
            }


        }
        catch (CouchbaseLiteException e)
        {
            return  false;
        }
    }
public boolean isBarcodeInRange(String barcode)
{
    Query query = getBarcodeRange("byBarcodeAsRange", "barcodes");
    try {

        QueryEnumerator  rowList = query.run();
        QueryRow queryRow = rowList.getRow(0);

            Document doc = queryRow.getDocument();


            if( doc.getCurrentRevision().getProperty(barcode) != null)
            {
                return true;
            }
        else
            {
                return false;
            }


    }
    catch (CouchbaseLiteException e)
    {
        return  false;
    }
}

public QueryRow doesBarcodeExist(String barcode)
{
/*    ArrayList<QueryRow> rowList = getRowsWithView("byBarcode","assessment");
    QueryRow queryRow = null;
    for(QueryRow row : rowList)
    {
        Document doc = row.getDocument();
        String docBarcode = doc.getCurrentRevision().getProperty("barcode_num").toString();

        if(docBarcode.equals(barcode))
        {
            queryRow = row;
            break;
        }

    }
    return queryRow;

    */

    Query query = getBarcodeVal("byBarcodeAsKey", "assessment");
    try {
        query.setStartKey(barcode);
        query.setLimit(1);
        QueryEnumerator  rowList = query.run();
        QueryRow queryRow = null;

    for(int i = 0; i<rowList.getCount(); i++)
    {
        QueryRow row = rowList.getRow(i);
        Document doc = row.getDocument();
        String docBarcode = doc.getCurrentRevision().getProperty("barcode_num").toString();

        if(docBarcode.equals(barcode))
        {
            queryRow = row;
            break;
        }
    }
    return queryRow;
    }
    catch (CouchbaseLiteException e)
    {
     return  null;
    }
}

    public QueryRow doesVoucherExist(String barcode)
    {


        Query query = getVoucherVal("byVoucherAsKey", "assessment-voucher");
        try {
            query.setStartKey(barcode);
            query.setLimit(1);
            QueryEnumerator  rowList = query.run();
            QueryRow queryRow = null;

            for(int i = 0; i<rowList.getCount(); i++)
            {
                QueryRow row = rowList.getRow(i);
                Document doc = row.getDocument();
                ArrayList<Map<String,Object>> children = (ArrayList<Map<String,Object>>) doc.getCurrentRevision().getProperty("child_list");
                boolean shouldBreak = false;
                for(Map<String,Object> child : children)
                {
                    String voucherCode = (String)child.get("voucher_code");

                    if(voucherCode.equals(barcode))
                    {
                        queryRow = row;
                        shouldBreak = true;
                        break;
                    }
                }
                if (shouldBreak)
                {
                    break;
                }
            }
            return queryRow;
        }
        catch (CouchbaseLiteException e)
        {
            Log.e("CB-ERROR",e.getMessage());
            return  null;
        }
    }


    public QueryEnumerator getAssessments(String viewName, final String fieldName) {
        try {

            JSONObject userObj = UserPrefs.getUserData(mContext);
            String userName ="";
            try{
                userName = userObj.getString("username");
            }
            catch (JSONException e){}


            com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, viewName +"_"+ userName));

            viewItems.setMap(new Mapper() {

                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    JSONObject userObj = UserPrefs.getUserData(mContext);
                    String userName ="";
                    try{
                        userName = userObj.getString("username");
                    }
                    catch (JSONException e){}

                    if(userName.equals("unicef-leb")){
                        Object createdType2 = document.get("type");
                        if (createdType2 != null  && createdType2.equals(fieldName)) {
                            emitter.emit(createdType2.toString(), null);
                        }
                    }
                    else {
                        Object createdType = document.get("type");

                        ArrayList<Map<String, Object>> historyList = (ArrayList<Map<String, Object>>) document.get("history");
                        if (historyList != null) {
                            Map<String, Object> firstEntry = historyList.get(0);
                            Object createdBy = firstEntry.get("user");
                            // Log.v("DOC", String.valueOf(document.getProperties()));

                            if (createdType != null && createdBy != null && createdType.equals(fieldName) && createdBy.equals(userName)) {
                                emitter.emit(createdType.toString(), null);
                            }
                        }
                    }
                    }

            }, "2.0");

            //ArrayList<QueryRow> rows = new ArrayList<QueryRow>();
            Query query = viewItems.createQuery();
            QueryEnumerator result = query.run();


           /* for (Iterator<QueryRow> it = result; it.hasNext(); )  {
                QueryRow row = it.next();

                Document document = row.getDocument();
                rows.add(row);
            }*/
            return result;//rows;
        }

        catch (CouchbaseLiteException  e  )
        {
            return null;//new ArrayList<QueryRow>();
        }
    }


public com.couchbase.lite.View createPcodeView(String role)
{
    if (role.equals(Constants.ROLE_KITS_DISTRIBUTOR)) {
        com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, "byPcodeNameAsKey"));

        viewItems.setMap(new Mapper() {

            @Override
            public void map(Map<String, Object> document, Emitter emitter) {

                Object createdType = document.get("type");
                //   ArrayList<Map<String, Object>> historyList = (ArrayList<Map<String, Object>>)document.get("history");
                //  Map<String, Object> firstEntry =  historyList.get(0);
                //  Object createdBy = firstEntry.get("user");
                if (createdType != null && createdType.equals("assessment")) {

                    Map<String, Object> pcodeObj = (Map<String, Object>)  document.get("location");

                    emitter.emit(pcodeObj.get("p_code_name"), pcodeObj.get("p_code_name"));
                }
            }
        }, "3.0");

        return viewItems;
    }
    else
    {
        JSONObject userObj = UserPrefs.getUserData(mContext);
        String userName ="";
        try{
            userName = userObj.getString("username");
        }
        catch (JSONException e){}


        com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, "byPcodeNameAsKey" +"_"+ userName));

        viewItems.setMap(new Mapper() {

            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                JSONObject userObj = UserPrefs.getUserData(mContext);
                String userName ="";
                try{
                    userName = userObj.getString("username");
                }
                catch (JSONException e){}

                if(userName.equals("unicef-leb")){
                    Object createdType2 = document.get("type");
                    if (createdType2 != null  && createdType2.equals("assessment")) {
                        emitter.emit(createdType2.toString(), null);
                    }
                }
                else {
                    Object createdType = document.get("type");

                    ArrayList<Map<String, Object>> historyList = (ArrayList<Map<String, Object>>) document.get("history");
                    if (historyList != null) {
                        Map<String, Object> firstEntry = historyList.get(0);
                        Object createdBy = firstEntry.get("user");
                        // Log.v("DOC", String.valueOf(document.getProperties()));

                        if (createdType != null && createdBy != null && createdType.equals("assessment") && createdBy.equals(userName)) {

                            Map<String, Object> pcodeObj = (Map<String, Object>)  document.get("location");

                            emitter.emit(pcodeObj.get("p_code_name"),pcodeObj.get("p_code_name"));
                        }
                    }
                }
            }

        }, "2.0");
        return viewItems;
    }
}

public com.couchbase.lite.View createBarcodeView(String role)
{

if (role.equals(Constants.ROLE_KITS_DISTRIBUTOR)) {
    com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, "byBarcodeAsKey"));

    viewItems.setMap(new Mapper() {

        @Override
        public void map(Map<String, Object> document, Emitter emitter) {

            Object createdType = document.get("type");
            //   ArrayList<Map<String, Object>> historyList = (ArrayList<Map<String, Object>>)document.get("history");
            //  Map<String, Object> firstEntry =  historyList.get(0);
            //  Object createdBy = firstEntry.get("user");
            if (createdType != null && createdType.equals("assessment")) {
                emitter.emit(document.get("barcode_num"), document.get("barcode_num"));
            }
        }
    }, "3.0");

    return viewItems;
}
    else
{
    JSONObject userObj = UserPrefs.getUserData(mContext);
    String userName ="";
    try{
        userName = userObj.getString("username");
    }
    catch (JSONException e){}


    com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, "byBarcodeAsKey" +"_"+ userName));

    viewItems.setMap(new Mapper() {

        @Override
        public void map(Map<String, Object> document, Emitter emitter) {
            JSONObject userObj = UserPrefs.getUserData(mContext);
            String userName ="";
            try{
                userName = userObj.getString("username");
            }
            catch (JSONException e){}

            if(userName.equals("unicef-leb")){
                Object createdType2 = document.get("type");
                if (createdType2 != null  && createdType2.equals("assessment")) {
                    emitter.emit(createdType2.toString(), null);
                }
            }
            else {
                Object createdType = document.get("type");

                ArrayList<Map<String, Object>> historyList = (ArrayList<Map<String, Object>>) document.get("history");
                if (historyList != null) {
                    Map<String, Object> firstEntry = historyList.get(0);
                    Object createdBy = firstEntry.get("user");
                    // Log.v("DOC", String.valueOf(document.getProperties()));

                    if (createdType != null && createdBy != null && createdType.equals("assessment") && createdBy.equals(userName)) {
                        emitter.emit(document.get("barcode_num"),document.get("barcode_num"));
                    }
                }
            }
        }

    }, "2.0");
    return viewItems;
}
}




    public com.couchbase.lite.View createPhoneNumberView(String role,final String temp) {

        if (role.equals(Constants.ROLE_VOUCHERS_DISTRIBUTOR)) {

            JSONObject userObj = UserPrefs.getUserData(mContext);
            String userName ="";
            try{
                userName = userObj.getString("username");
           }
            catch (JSONException e){}

            com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, "byPhoneNumAsKey"+"_"+userName));

            viewItems.setMap(new Mapper() {

                @Override
                public void map(Map<String, Object> document, Emitter emitter) {

                    JSONObject userObj = UserPrefs.getUserData(mContext);
                    String org = "";
                    try{
                        org = userObj.getString("organisation");
                    }
                    catch (JSONException e){}

                    Object createdType = document.get("type");
                    //   ArrayList<Map<String, Object>> historyList = (ArrayList<Map<String, Object>>)document.get("history");
                    //  Map<String, Object> firstEntry =  historyList.get(0);
                    //  Object createdBy = firstEntry.get("user");
                    if (createdType != null && createdType.equals("assessment-voucher") ) {
                    Map<String, Object> locationDoc =  (Map<String,Object>)document.get("location");
                        Object pcodeLoc = locationDoc.get("p_code");
                        if (pcodeLoc != null && pcodeLoc.equals(org)) {
                            emitter.emit(document.get("phone_number"), document.get("phone_number"));
                        }
                    }
                }
            }, "3.1");

            return viewItems;
        }
        if (role.equals(Constants.ROLE_VOUCHERS_REDEEMER)) {

            JSONObject userObj = UserPrefs.getUserData(mContext);
            String userName ="";
            try{
                userName = userObj.getString("username");
            }
            catch (JSONException e){}

            com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, "byPhoneNumAsKey"+"_"+userName+" "+temp));

            viewItems.setMap(new Mapper() {

                @Override
                public void map(Map<String, Object> document, Emitter emitter) {



                    Object createdType = document.get("type");
                    //   ArrayList<Map<String, Object>> historyList = (ArrayList<Map<String, Object>>)document.get("history");
                    //  Map<String, Object> firstEntry =  historyList.get(0);
                    //  Object createdBy = firstEntry.get("user");
                    if (createdType != null && createdType.equals("assessment-voucher") ) {
                        Map<String, Object> locationDoc =  (Map<String,Object>)document.get("location");
                        Object pcodeLoc = locationDoc.get("p_code");
                        if (pcodeLoc != null && pcodeLoc.equals(temp)) {
                            emitter.emit(document.get("phone_number"), document.get("phone_number"));
                        }
                    }
                   // }
                }
            }, "3.1");

            return viewItems;
        }

        else
            return null;
    }

    public com.couchbase.lite.View createIDView(String role,final String temp) {

        if (role.equals(Constants.ROLE_KITS_DISTRIBUTOR)) {
            com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, "byIDAsKey"));

            viewItems.setMap(new Mapper() {

                @Override
                public void map(Map<String, Object> document, Emitter emitter) {

                    Object createdType = document.get("type");
                    //   ArrayList<Map<String, Object>> historyList = (ArrayList<Map<String, Object>>)document.get("history");
                    //  Map<String, Object> firstEntry =  historyList.get(0);
                    //  Object createdBy = firstEntry.get("user");
                    if (createdType != null && createdType.equals("assessment")) {
                        emitter.emit(document.get("official_id"), document.get("official_id"));
                    }
                }
            }, "3.0");

            return viewItems;
        }
        else if(role.equals(Constants.ROLE_VOUCHERS_DISTRIBUTOR)){


            JSONObject userObj = UserPrefs.getUserData(mContext);
            String userName ="";
            try{
                userName = userObj.getString("username");
            }
            catch (JSONException e){}

            com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, "byIDAsKey2" +"_"+ userName));

            viewItems.setMap(new Mapper() {

                @Override
                public void map(Map<String, Object> document, Emitter emitter) {

                    JSONObject userObj = UserPrefs.getUserData(mContext);
                    String org = "";
                    try{
                        org = userObj.getString("organisation");
                    }
                    catch (JSONException e){}

                    Object createdType = document.get("type");
                    //   ArrayList<Map<String, Object>> historyList = (ArrayList<Map<String, Object>>)document.get("history");
                    //  Map<String, Object> firstEntry =  historyList.get(0);
                    //  Object createdBy = firstEntry.get("user");
                    if (createdType != null && createdType.equals("assessment-voucher") ) {
                        Map<String, Object> locationDoc = (Map<String, Object>) document.get("location");
                        Object pcodeLoc = locationDoc.get("p_code");
                        if (pcodeLoc != null && pcodeLoc.equals(org)) {
                            emitter.emit(document.get("official_id"), document.get("official_id"));
                        }
                    }
                }
            }, "3.1");

            return viewItems;

        }
        else if(role.equals(Constants.ROLE_VOUCHERS_REDEEMER)){


            JSONObject userObj = UserPrefs.getUserData(mContext);
            String userName ="";
            try{
                userName = userObj.getString("username");
            }
            catch (JSONException e){}

            com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, "byIDAsKey2" +"_"+ userName+" "+temp));

            viewItems.setMap(new Mapper() {

                @Override
                public void map(Map<String, Object> document, Emitter emitter) {

                    Object createdType = document.get("type");
                    //   ArrayList<Map<String, Object>> historyList = (ArrayList<Map<String, Object>>)document.get("history");
                    //  Map<String, Object> firstEntry =  historyList.get(0);
                    //  Object createdBy = firstEntry.get("user");
                    if (createdType != null && createdType.equals("assessment-voucher") ) {
                        Map<String, Object> locationDoc = (Map<String, Object>) document.get("location");
                        Object pcodeLoc = locationDoc.get("p_code");
                        if (pcodeLoc != null && pcodeLoc.equals(temp)) {
                            emitter.emit(document.get("official_id"), document.get("official_id"));
                        }
                    }
                   // }
                }
            }, "3.1");

            return viewItems;

        }

       else{

            JSONObject userObj = UserPrefs.getUserData(mContext);
            String userName ="";
            try{
                userName = userObj.getString("username");
            }
            catch (JSONException e){}


            com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, "byIDAsKey" +"_"+ userName));

            viewItems.setMap(new Mapper() {

                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    JSONObject userObj = UserPrefs.getUserData(mContext);
                    String userName ="";
                    try{
                        userName = userObj.getString("username");
                    }
                    catch (JSONException e){}

                    if(userName.equals("unicef-leb")){
                        Object createdType2 = document.get("type");
                        if (createdType2 != null  && createdType2.equals("assessment")) {
                            emitter.emit(createdType2.toString(), null);
                        }
                    }
                    else {
                        Object createdType = document.get("type");

                        ArrayList<Map<String, Object>> historyList = (ArrayList<Map<String, Object>>) document.get("history");
                        if (historyList != null) {
                            Map<String, Object> firstEntry = historyList.get(0);
                            Object createdBy = firstEntry.get("user");
                            // Log.v("DOC", String.valueOf(document.getProperties()));

                            if (createdType != null && createdBy != null && createdType.equals("assessment") && createdBy.equals(userName)) {
                                emitter.emit(document.get("official_id"),document.get("official_id"));
                            }
                        }
                    }
                }

            }, "2.0");
            return viewItems;


        }

    }

    public Query getVoucherVal(String viewName, final String fieldName) {


        com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, viewName));

        viewItems.setMap(new Mapper() {

            @Override
            public void map(Map<String, Object> document, Emitter emitter) {

                Object createdType = document.get("type");
                //   ArrayList<Map<String, Object>> historyList = (ArrayList<Map<String, Object>>)document.get("history");
                //  Map<String, Object> firstEntry =  historyList.get(0);
                Log.v("TEST",createdType.toString());

                //  Object createdBy = firstEntry.get("user");
                if (createdType != null &&  createdType.equals(fieldName) ) {
                    ArrayList<Map<String, Object>> children = (ArrayList<Map<String, Object>>)document.get("child_list");
                    for (Map<String,Object> child : children) {
                       if (child.get("voucher_code") != null && !child.get("voucher_code").equals(""))
                        emitter.emit(child.get("voucher_code"),child.get("voucher_code"));
                    }
                }
            }
        }, "3.1");

        Query query = viewItems.createQuery();

        //QueryEnumerator result = query.run();

        return query;

    }

    public Query getBarcodeVal(String viewName, final String fieldName) {


            com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, viewName));

            viewItems.setMap(new Mapper() {

                @Override
                public void map(Map<String, Object> document, Emitter emitter) {

                    Object createdType = document.get("type");
                    //  ArrayList<Map<String, Object>> historyList = (ArrayList<Map<String, Object>>)document.get("history");
                    // Map<String, Object> firstEntry =  historyList.get(0);
                    //  Object createdBy = firstEntry.get("user");
                    if (createdType != null && createdType.equals(fieldName)) {
                        emitter.emit(document.get("barcode_num"), document.get("barcode_num"));
                    }
                }
            }, "3.0");

            Query query = viewItems.createQuery();

            //QueryEnumerator result = query.run();

            return query;



    }

    public com.couchbase.lite.View getVersioningView() {


        com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, "byVersionName"));

        viewItems.setMap(new Mapper() {

            @Override
            public void map(Map<String, Object> document, Emitter emitter) {

                Object createdType = document.get("type");
                //   ArrayList<Map<String, Object>> historyList = (ArrayList<Map<String, Object>>)document.get("history");
                //  Map<String, Object> firstEntry =  historyList.get(0);
                //  Object createdBy = firstEntry.get("user");
                if (createdType != null &&  createdType.equals("versioning") ) {
                    emitter.emit(document.get("version"), document.get("version"));
                }
            }
        }, "3.0");



        //QueryEnumerator result = query.run();

        return viewItems;



    }

    public String getLatestVersion() {


        com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, "byVersionName"));

        viewItems.setMap(new Mapper() {

            @Override
            public void map(Map<String, Object> document, Emitter emitter) {

                Object createdType = document.get("type");
                //   ArrayList<Map<String, Object>> historyList = (ArrayList<Map<String, Object>>)document.get("history");
                //  Map<String, Object> firstEntry =  historyList.get(0);
                //  Object createdBy = firstEntry.get("user");
                if (createdType != null &&  createdType.equals("versioning") ) {
                    emitter.emit(document.get("version"), document.get("version"));
                }
            }
        }, "3.0");




        Query query = viewItems.createQuery();
        try {

            QueryEnumerator rowList = query.run();
            QueryRow queryRow = rowList.getRow(0);

            Document doc = queryRow.getDocument();

            //QueryEnumerator result = query.run();
            return doc.getCurrentRevision().getProperty("version").toString();
        }
        catch (CouchbaseLiteException e)
        {
            return "1.0";
        }
        catch (Exception e)
        {
            return "1.0";
        }
    }

    public Query getBarcodeRange(String viewName, final String fieldName) {


        com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, viewName));

        viewItems.setMap(new Mapper() {

            @Override
            public void map(Map<String, Object> document, Emitter emitter) {

                Object createdType = document.get("type");
                //   ArrayList<Map<String, Object>> historyList = (ArrayList<Map<String, Object>>)document.get("history");
                //  Map<String, Object> firstEntry =  historyList.get(0);
                //  Object createdBy = firstEntry.get("user");
                if (createdType != null &&  createdType.equals(fieldName)) {
                    emitter.emit(document.get("type"), null);
                }
            }
        }, "3.0");

        Query query = viewItems.createQuery();

        //QueryEnumerator result = query.run();

        return query;



    }
    public QueryEnumerator getAllRows(String viewName, final String fieldName) {
        try {

            com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, viewName));

            viewItems.setMap(new Mapper() {

                @Override
                public void map(Map<String, Object> document, Emitter emitter) {

                    Object createdType = document.get("type");
                    //   ArrayList<Map<String, Object>> historyList = (ArrayList<Map<String, Object>>)document.get("history");
                    //  Map<String, Object> firstEntry =  historyList.get(0);
                    //  Object createdBy = firstEntry.get("user");
                    if (createdType != null &&  createdType.equals(fieldName) ) {
                        emitter.emit(createdType.toString(), null);
                    }
                }
            }, "1.0");

            //ArrayList<QueryRow> rows = new ArrayList<QueryRow>();
            Query query = viewItems.createQuery();
            QueryEnumerator result = query.run();


           // for (Iterator<QueryRow> it = result; it.hasNext(); )  {
            //    QueryRow row = it.next();
                // Document document = row.getDocument();
                // Log.v("DOC", String.valueOf(document.getProperties()));
           //     rows.add(row);
         //   }
            return result;//rows;
        }

        catch (CouchbaseLiteException  e )
        {
            return null;
        }
    }

    public ArrayList<QueryRow> getRowsWithView(String viewName, final String fieldName) {
        try {

        Log.e("TEST",designDocName+" "+viewName);
        com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, viewName));

        viewItems.setMap(new Mapper() {

            @Override
            public void map(Map<String, Object> document, Emitter emitter) {

                Object createdType = document.get("type");
              //  ArrayList<Map<String, Object>> historyList = (ArrayList<Map<String, Object>>)document.get("history");
              //  Map<String, Object> firstEntry =  historyList.get(0);
              //  Object createdBy = firstEntry.get("user");
                if (createdType != null &&  createdType.equals(fieldName) ) {
                    emitter.emit(createdType.toString(), null);
                }
            }
        }, "1.2");

        ArrayList<QueryRow> rows = new ArrayList<QueryRow>();
            Query query = viewItems.createQuery();
            QueryEnumerator result = query.run();


        for (Iterator<QueryRow> it = result; it.hasNext(); )  {
            QueryRow row = it.next();
           // Document document = row.getDocument();
           // Log.v("DOC", String.valueOf(document.getProperties()));
            rows.add(row);
        }
        return rows;
        }

        catch (CouchbaseLiteException  e  )
        {
            return new ArrayList<QueryRow>();
        }
    }


    public ArrayList<PCodeObject> getRowsWithView(String viewName, final String fieldName, final String subFieldName) {
        try {

            JSONObject userObj = UserPrefs.getUserData(mContext);
            JSONArray sdc_Location = new JSONArray();
            try {
                if ( userObj.getJSONArray("sdc_locations") != null) {
                    sdc_Location = userObj.getJSONArray("sdc_locations");
                }
            }

            catch (JSONException e)
            {}


            com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, viewName));

            viewItems.setMap(new Mapper() {

                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    Object mainType = document.get("type");
                    Object subType = document.get("site-type");

                    if (subFieldName.equals("IS")) {
                        if (mainType != null && mainType.equals(fieldName) && subType.equals(subFieldName)) {
                            emitter.emit(mainType.toString(), null);
                        }
                    }
                    else if (subFieldName.equals("SDC")){

                    if (mainType != null && mainType.equals(fieldName) && subType.equals(subFieldName)) {
                        JSONObject userObj = UserPrefs.getUserData(mContext);
                        JSONArray sdc_Location = new JSONArray();
                        try {
                            sdc_Location = userObj.getJSONArray("sdc_locations");
                            for (int i = 0; i < sdc_Location.length(); i++) {
                                Object pcode = document.get("p_code");
                                if (pcode.equals(sdc_Location.get(i))) {
                                    emitter.emit(mainType.toString(), null);
                                }
                            }

                        } catch (JSONException e) {
                        }
                    }
                    }

                }
            }, "1.2.6");

            ArrayList<PCodeObject> rows = new ArrayList<PCodeObject>();
            Query query = viewItems.createQuery();
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); )  {
                QueryRow row = it.next();
                Document document = row.getDocument();
                PCodeObject pCodeObject = new PCodeObject();
               // Log.v("DOO", String.valueOf(document.getProperties()));
                pCodeObject.setPcodeID(document.getCurrentRevision().getProperty("p_code").toString());
                pCodeObject.setPcodeName(document.getCurrentRevision().getProperty("p_code_name").toString());
                pCodeObject.setPcodeLat(document.getCurrentRevision().getProperty("latitude").toString());
                pCodeObject.setPcodeLong(document.getCurrentRevision().getProperty("longitude").toString());

                rows.add(pCodeObject);
            }
            return rows;
        }

        catch (CouchbaseLiteException  e  )
        {
            return new ArrayList<PCodeObject>();
        }
    }


    public Query getRowsWithEnumerator(String viewName, final String fieldName, final String subFieldName) {


            JSONObject userObj = UserPrefs.getUserData(mContext);
            JSONArray sdc_Location = new JSONArray();
            try {
                if ( userObj.getJSONArray("sdc_locations") != null) {
                    sdc_Location = userObj.getJSONArray("sdc_locations");
                }
            }

            catch (JSONException e)
            {}


            com.couchbase.lite.View viewItems = database.getView(String.format("%s/%s", designDocName, viewName));

            viewItems.setMap(new Mapper() {

                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    Object mainType = document.get("type");
                    Object subType = document.get("site-type");

                    if (subFieldName.equals("IS")) {
                        if (mainType != null && mainType.equals(fieldName) && subType.equals(subFieldName)) {
                            Object pcode = document.get("p_code");
                            Object pcodeName = document.get("p_code_name");
                            emitter.emit(pcode.toString(), pcodeName.toString());
                        }
                    }
                    else if (subFieldName.equals("SDC")){

                        if (mainType != null && mainType.equals(fieldName) && subType.equals(subFieldName)) {
                            JSONObject userObj = UserPrefs.getUserData(mContext);
                            JSONArray sdc_Location = new JSONArray();
                            try {
                                sdc_Location = userObj.getJSONArray("sdc_locations");
                                for (int i = 0; i < sdc_Location.length(); i++) {
                                    Object pcode = document.get("p_code");
                                    Object pcodeName = document.get("p_code_name");
                                    if (pcode.equals(sdc_Location.get(i))) {
                                        emitter.emit(pcode.toString(), pcodeName.toString());
                                    }
                                }

                            } catch (JSONException e) {
                            }
                        }
                    }

                }
            }, "1.4");

            Query query = viewItems.createQuery();
          //  QueryEnumerator result = query.run();
            return query;

           // for (Iterator<QueryRow> it = result; it.hasNext(); )  {
           //     QueryRow row = it.next();
         //       Document document = row.getDocument();
           //     PCodeObject pCodeObject = new PCodeObject();
                // Log.v("DOO", String.valueOf(document.getProperties()));
                //pCodeObject.setPcodeID(document.getCurrentRevision().getProperty("p_code").toString());
              //  pCodeObject.setPcodeName(document.getCurrentRevision().getProperty("p_code_name").toString());
                //pCodeObject.setPcodeLat(document.getCurrentRevision().getProperty("latitude").toString());
                //pCodeObject.setPcodeLong(document.getCurrentRevision().getProperty("longitude").toString());

               // rows.add(pCodeObject);
         ///   }
         // //  return rows;



    }


    public void removeBeneficiary(BeneficiaryObject beneficiaryObject)
    {
        Document retrievedDocument = database.getDocument(beneficiaryObject.getMainID());
    //    RevisionInternal internal = database.getLocalDocument(beneficiaryObject.officialID,retrievedDocument.getCurrentRevisionId());
        // display the retrieved document
        try {
            retrievedDocument.delete();
   //         internal.setDeleted(true);
        }
        catch (CouchbaseLiteException e)
        {
            Log.e("DAMN", e.getMessage());
         }



    }


 /*   public void printAllRows()
    {
        // retrieve the document from the database
        try {


            com.couchbase.lite.View viewItemsByDate = database.getView(String.format("%s/%s", designDocName, byDateViewName));
            viewItemsByDate.setMap(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    Object createdAt = document.get("child_list");
                    if (createdAt != null) {
                        emitter.emit(createdAt.toString(), null);
                    }
                }
            }, "1.0");
            Query query = viewItemsByDate.createQuery();
            query.isDescending();
            query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();




                Log.v("DOCUMENT",  String.valueOf(document.getCurrentRevision().getProperties())+"");
             BeneficiaryObject benny =  extractBeneficiary( row);


            }
        }

        catch (CouchbaseLiteException  e  )
        {
            Log.v("DOCUMENT",e.getMessage());

        }
    }*/


    public DistributionObject extractDist(QueryRow row) {
        Document doc = row.getDocument();
        DistributionObject distributionObject = new DistributionObject();

        distributionObject.setDocID(doc.getId());

        distributionObject.setName(doc.getCurrentRevision().getProperty("name").toString());
        distributionObject.setPartnerName(doc.getCurrentRevision().getProperty("partner_name").toString());
        distributionObject.setAssessmentType(doc.getCurrentRevision().getProperty("icon").toString());

        if (doc.getCurrentRevision().getProperty("family_name") != null)
            distributionObject.setFamilyName(doc.getCurrentRevision().getProperty("family_name").toString());
        else
            distributionObject.setFamilyName("");

        if (doc.getCurrentRevision().getProperty("first_name") != null)
            distributionObject.setFirstName(doc.getCurrentRevision().getProperty("first_name").toString());
        else
            distributionObject.setFirstName("");

        if (doc.getCurrentRevision().getProperty("middle_name") != null)
            distributionObject.setMiddleName(doc.getCurrentRevision().getProperty("middle_name").toString());
        else
            distributionObject.setMiddleName("");

        if (doc.getCurrentRevision().getProperty("relationship_type") != null)
            distributionObject.setRelationshipType(doc.getCurrentRevision().getProperty("relationship_type").toString());
        else
            distributionObject.setRelationshipType("");

        if (doc.getCurrentRevision().getProperty("barcode_num") != null)
            distributionObject.setBarcodeNum(doc.getCurrentRevision().getProperty("barcode_num").toString());

        if (doc.getCurrentRevision().getProperty("official_id") != null)
            distributionObject.setOfficialID(doc.getCurrentRevision().getProperty("official_id").toString());

        if (doc.getCurrentRevision().getProperty("id_exists") != null)
            distributionObject.setIdDoesExist(((Boolean) doc.getCurrentRevision().getProperty("id_exists")).booleanValue());

        if (doc.getCurrentRevision().getProperty("intervention") != null){
           String d =  doc.getCurrentRevision().getProperty("intervention").toString();
        distributionObject.setInterventionTitle(doc.getCurrentRevision().getProperty("intervention").toString());
    }
        if (doc.getCurrentRevision().getProperty("id_notes") != null)
        distributionObject.setIdNotes(doc.getCurrentRevision().getProperty("id_notes").toString());


        if (doc.getCurrentRevision().getProperty("id_type") != null)
            distributionObject.setIdType(doc.getCurrentRevision().getProperty("id_type").toString());

        if(doc.getCurrentRevision().getProperty("phone_number") !=null)
            distributionObject.setPhoneNumber(doc.getCurrentRevision().getProperty("phone_number").toString());

        if (doc.getCurrentRevision().getProperty("criticality") != null)
            distributionObject.setCriticality(doc.getCurrentRevision().getProperty("criticality").toString());

        if (doc.getCurrentRevision().getProperty("creation_date") != null)
            distributionObject.setCreationDate(doc.getCurrentRevision().getProperty("creation_date").toString());

        if (doc.getCurrentRevision().getProperty("distribution_date") != null)
            distributionObject.setDistributionDate(doc.getCurrentRevision().getProperty("distribution_date").toString());


        if (doc.getCurrentRevision().getProperty("completed") != null)
            distributionObject.setComplete(((Boolean)doc.getCurrentRevision().getProperty("completed")).booleanValue());

        PCodeObject pcode = new PCodeObject();
        Map<String,Object> pcodeData  = (Map<String,Object>)doc.getCurrentRevision().getProperty("location");
        pcode.setPcodeName((String)pcodeData.get("p_code_name"));
        pcode.setPcodeID((String) pcodeData.get("p_code"));
        pcode.setPcodeLat((String) pcodeData.get("latitude"));
        pcode.setPcodeLong((String) pcodeData.get("longitude"));
        pcode.setLocationType((String) pcodeData.get("location_type"));
        distributionObject.setPcode(pcode);

        if (doc.getCurrentRevision().getProperty("child_list") !=null) {
            ArrayList<Map<String, Object>> childList = (ArrayList<Map<String, Object>>) doc.getCurrentRevision().getProperty("child_list");

            ArrayList<ChildObject> children = new ArrayList<ChildObject>();

            for (Map<String, Object> child : childList) {
                ChildObject childObject = new ChildObject();
                childObject.setAge((String) child.get("age"));
                childObject.setGender((String) child.get("gender"));
                childObject.setStatus((String) child.get("status"));
                childObject.setKit((String) child.get("kit"));
                childObject.setName((String) child.get("name"));
                childObject.setReasonForEdit((String) child.get("reason"));
                childObject.setVoucherCode((String) child.get("voucher_code"));
                children.add(childObject);
            }

            distributionObject.setChildrenList(children);
        }

        ArrayList<Map<String,Object>> itemList =  (ArrayList<Map<String,Object>>)doc.getCurrentRevision().getProperty("item_list");

        ArrayList<ItemObject> items = new ArrayList<ItemObject>();

        for(Map<String,Object> item : itemList)
        {
            ItemObject itemObject = new ItemObject();
            itemObject.setDeliveryStatus((String) item.get("delivery_status"));
            itemObject.setItemID((String) item.get("item_id"));
            itemObject.setItemType((String) item.get("item_type"));
            if ( item.get("comments") != null) {
                itemObject.setCommentArray((ArrayList<Map<String, Object>>) item.get("comments"));
            }
            else
            {
                itemObject.setCommentArray(new ArrayList<Map<String, Object>>());
            }
                if ( item.get("quantity") != null)
                    itemObject.setQuantity(/*(Integer.valueOf((String)*/ (Integer)item.get("quantity"));
                else
                    itemObject.setQuantity(0);
            if ( item.get("delivered") != null)
                itemObject.setDelivered((Integer) item.get("delivered"));
            else
                itemObject.setDelivered(0);
                //itemObject.setDestroyed((Integer.valueOf((String)item.get("destroyed"))));

            items.add(itemObject);
        }

        distributionObject.setItemList(items);


        return  distributionObject;

    }

    public void forceCompleteDist(DistributionObject distributionObject)
    {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = GregorianCalendar.getInstance();
        String currentTimeString = dateFormatter.format(calendar.getTime());

        Document retrievedDocument = database.getDocument(distributionObject.docID);



        // display the retrieved document


        // update the document
        Map<String, Object> distContent = new HashMap<String, Object>();
        distContent.putAll(retrievedDocument.getProperties());

            distContent.put("completed", true);

        try {

            retrievedDocument.putProperties(distContent);
            Log.d(TAG, "Document written to database named  with ID = " + retrievedDocument.getId());
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Cannot write document to database", e);
        }
        catch (Exception e) {
        }

    }


    public void addForceCompleteToLog(String docId,boolean isCompleted)
    {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = GregorianCalendar.getInstance();
        String currentTimeString = dateFormatter.format(calendar.getTime());

        Document retrievedDocument = database.getDocument(docId);

        Map<String, Object> distContent = new HashMap<String, Object>();
        distContent.putAll(retrievedDocument.getProperties());


        if (isCompleted){

            ArrayList<Map<String, Object>> historyList;
            if (retrievedDocument.getProperty("history")== null) {
                historyList = new ArrayList<Map<String, Object>>();
            }
            else
            {
                historyList = (ArrayList<Map<String, Object>>)distContent.get("history");
            }
            String username = UserPrefs.getUsername(mContext);

            Map<String, Object> historyObj = new HashMap<String, Object>();

            historyObj.put("user", username);
            historyObj.put("action","FORCE_COMPLETED");
            historyObj.put("datetime",currentTimeString);
            historyObj.put("organisation",username);
            historyList.add(historyObj);

        }


    }

    public void addCommentInLog(String docId,String itemType,DistStat distStat)
    {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = GregorianCalendar.getInstance();
        String currentTimeString = dateFormatter.format(calendar.getTime());


        Document retrievedDocument = database.getDocument(docId);

        Map<String, Object> distContent = new HashMap<String, Object>();
        distContent.putAll(retrievedDocument.getProperties());

        ArrayList<Map<String, Object>> historyList;

        if (distContent.get("history")== null) {
            historyList = new ArrayList<Map<String, Object>>();
        }
        else
        {
            historyList = (ArrayList<Map<String, Object>>) distContent.get("history");
        }

        String username = UserPrefs.getUsername(mContext);

        Map<String, Object> historyObj = new HashMap<String, Object>();

        if (distStat == DistStat.COMMENT_ADDED) {


            historyObj.put("user", username);
            historyObj.put("action", "COMMENT_ADDED");
            historyObj.put("datetime", currentTimeString);
            historyObj.put("item_type", itemType);
            historyObj.put("organisation", username);
            historyList.add(historyObj);
        }
        else if(distStat == DistStat.COMMENT_EDITED)
        {
            historyObj.put("user", username);
            historyObj.put("action", "COMMENT_EDITED");
            historyObj.put("datetime", currentTimeString);
            historyObj.put("item_type", itemType);
            historyObj.put("organisation", username);
            historyList.add(historyObj);
        }

        distContent.put("history",historyList);

        try {

            retrievedDocument.putProperties(distContent);
            Log.d(TAG, "Document written to database named  with ID = " + retrievedDocument.getId());
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Cannot write document to database", e);
        }
        catch (Exception e) {
        }

    }

    public void editDist(DistributionObject distributionObject, DistStat distStat)
    {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = GregorianCalendar.getInstance();
        String currentTimeString = dateFormatter.format(calendar.getTime());

        Document retrievedDocument = database.getDocument(distributionObject.docID);


        // display the retrieved document


        // update the document
        Map<String, Object> distContent = new HashMap<String, Object>();
        distContent.putAll(retrievedDocument.getProperties());

        //bennyContent.put("creation_date",beneficiaryObject.creationDate);


        ArrayList<Map<String,Object>> items = new ArrayList<Map<String, Object>>();


        int totalDelivered = 0;
        int totalQuantity = 0;
        for (ItemObject itemObject: distributionObject.getItemList()) {

            totalDelivered += itemObject.getDelivered();
            totalQuantity += itemObject.getQuantity();
        }


        boolean comp = distributionObject.isComplete();

        if (!comp) {
            Log.e("TEST",totalDelivered+" "+ totalQuantity);
            if (totalDelivered == totalQuantity) {
                distContent.put("completed", true);
            } else {
                distContent.put("completed", false);
            }
        }
        else
        {
            distContent.put("completed", true);
        }

        if (distStat == DistStat.NO_FORCE)
        {
            distContent.put("completed", false);
        }

        String username = UserPrefs.getUsername(mContext);
        ArrayList<Map<String, Object>> historyList;
        if (retrievedDocument.getProperty("history")== null) {
             historyList = new ArrayList<Map<String, Object>>();
        }
        else
        {
             historyList = (ArrayList<Map<String, Object>>)retrievedDocument.getProperty("history");
        }

        if (distStat == DistStat.NONE) {
        for(ItemObject itemObject : distributionObject.getItemList())
        {
            Map<String,Object> item = new HashMap<String, Object>();

            item.put("delivered",itemObject.getDelivered());
            item.put("delivery_status",itemObject.getDeliveryStatus());
            item.put("item_id",itemObject.getItemID());
            item.put("item_type",itemObject.getItemType());
            item.put("quantity",itemObject.getQuantity());
            //item.put("destroyed",itemObject.getDestroyed().toString());
            item.put("comments",itemObject.getCommentArray());

            totalDelivered += itemObject.getDelivered();
            totalQuantity += itemObject.getQuantity();

            items.add(item);


                Map<String, Object> historyObj = new HashMap<String, Object>();

                historyObj.put("user", username);
                historyObj.put("action", "DELIVERED");
                historyObj.put("datetime", currentTimeString);
                historyObj.put("organisation", username);
                historyObj.put("delivered", itemObject.getDelivered().toString());
                historyObj.put("quantity", itemObject.getQuantity().toString());
                historyObj.put("item_type", itemObject.getItemType().toString());
                historyList.add(historyObj);
            }

            distContent.put("item_list", items);
        }

        else if (distStat == DistStat.FORCE) {
            Map<String, Object> historyObj = new HashMap<String, Object>();
            historyObj.put("user", username);
            historyObj.put("action", "FORCE_COMPLETED");
            historyObj.put("datetime", currentTimeString);
            historyObj.put("organisation", username);
            historyList.add(historyObj);
        }
        else if (distStat == DistStat.NO_FORCE)
        {
            Map<String, Object> historyObj = new HashMap<String, Object>();
            historyObj.put("user", username);
            historyObj.put("action", "NO_FORCE");
            historyObj.put("datetime", currentTimeString);
            historyObj.put("organisation", username);
            historyList.add(historyObj);
        }



        distContent.put("history",historyList);


        try {

            retrievedDocument.putProperties(distContent);
            Log.d(TAG, "Document written to database named  with ID = " + retrievedDocument.getId());
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Cannot write document to database", e);
        }
        catch (Exception e) {
        }

    }


    public BeneficiaryObject extractBeneficiary(Document doc)
    {


        BeneficiaryObject beneficiaryObject = new BeneficiaryObject();


        beneficiaryObject.setMainID(doc.getId());

        beneficiaryObject.setFamilyName(doc.getCurrentRevision().getProperty("family_name").toString());
        beneficiaryObject.setFirstName(doc.getCurrentRevision().getProperty("first_name").toString());
        beneficiaryObject.setMiddleName(doc.getCurrentRevision().getProperty("middle_name").toString());
        beneficiaryObject.setRelationshipType(doc.getCurrentRevision().getProperty("relationship_type").toString());

        beneficiaryObject.setOfficialID(doc.getCurrentRevision().getProperty("official_id").toString());


//        if (doc.getCurrentRevision().getProperty("answer1")!= null)
//            beneficiaryObject.setAnswer1(doc.getCurrentRevision().getProperty("answer1").toString());
//        if (doc.getCurrentRevision().getProperty("answer2")!= null)
//            beneficiaryObject.setAnswer2(doc.getCurrentRevision().getProperty("answer2").toString());
//        if (doc.getCurrentRevision().getProperty("answer3")!= null)
//            beneficiaryObject.setAnswer3(doc.getCurrentRevision().getProperty("answer3").toString());
        if(doc.getCurrentRevision().getProperty("barcode_num") != null)
            beneficiaryObject.setBarcodeNum(doc.getCurrentRevision().getProperty("barcode_num").toString());

        if (doc.getCurrentRevision().getProperty("moving_location")!= null)
            beneficiaryObject.setMovingLoc(doc.getCurrentRevision().getProperty("moving_location").toString());
        if (doc.getCurrentRevision().getProperty("new_district")!= null)
            beneficiaryObject.setNewDistrict(doc.getCurrentRevision().getProperty("new_district").toString());
        if (doc.getCurrentRevision().getProperty("new_cadastral")!= null)
            beneficiaryObject.setNewCadasteral(doc.getCurrentRevision().getProperty("new_cadastral").toString());
//        beneficiaryObject.setIdDoesExist(((Boolean)doc.getCurrentRevision().getProperty("id_exists")).booleanValue());
       // beneficiaryObject.setIdNotes(doc.getCurrentRevision().getProperty("id_notes").toString());
        beneficiaryObject.setIdType(doc.getCurrentRevision().getProperty("id_type").toString());
        beneficiaryObject.setPhoneNumber(doc.getCurrentRevision().getProperty("phone_number").toString());
        if (doc.getCurrentRevision().getProperty("dob")!= null)
             beneficiaryObject.setDateOfBirth(doc.getCurrentRevision().getProperty("dob").toString());
        if (doc.getCurrentRevision().getProperty("gender")!= null)
            beneficiaryObject.setGender(doc.getCurrentRevision().getProperty("gender").toString());
        if (doc.getCurrentRevision().getProperty("partner_name")!= null)
             beneficiaryObject.setPartnerName(doc.getCurrentRevision().getProperty("partner_name").toString());
        if (doc.getCurrentRevision().getProperty("marital_status")!= null)
            beneficiaryObject.setMaritalStatus(doc.getCurrentRevision().getProperty("marital_status").toString());
        if (doc.getCurrentRevision().getProperty("disabilities")!= null)
            beneficiaryObject.setDisabilities(doc.getCurrentRevision().getProperty("disabilities").toString());
        if (doc.getCurrentRevision().getProperty("principal_applicant") != null)
            beneficiaryObject.setPrincipleApplicant(doc.getCurrentRevision().getProperty("principal_applicant").toString());
        if (doc.getCurrentRevision().getProperty("phone_owner") != null)
            beneficiaryObject.setPhoneOwner(doc.getCurrentRevision().getProperty("phone_owner").toString());
        if (doc.getCurrentRevision().getProperty("assistance_type") != null)
            beneficiaryObject.setAssistanceType(doc.getCurrentRevision().getProperty("assistance_type").toString());
        if (doc.getCurrentRevision().getProperty("over18") != null)
            beneficiaryObject.setOver18(doc.getCurrentRevision().getProperty("over18").toString());
        if (doc.getCurrentRevision().getProperty("mothers_name") != null)
            beneficiaryObject.setMothersName(doc.getCurrentRevision().getProperty("mothers_name").toString());
        if (doc.getCurrentRevision().getProperty("family_count") != null)
            beneficiaryObject.setFamilyCount(doc.getCurrentRevision().getProperty("family_count").toString());

        // beneficiaryObject.setShawish(((Boolean)doc.getCurrentRevision().getProperty("shawish")).booleanValue());
        beneficiaryObject.setCriticality(doc.getCurrentRevision().getProperty("criticality").toString());
        beneficiaryObject.setCreationDate(doc.getCurrentRevision().getProperty("creation_date").toString());
        if (doc.getCurrentRevision().getProperty("completion_date")!=null)
        beneficiaryObject.setCompletionDate(doc.getCurrentRevision().getProperty("completion_date").toString());

        beneficiaryObject.setComplete(((Boolean)doc.getCurrentRevision().getProperty("completed")).booleanValue());

        PCodeObject pcode = new PCodeObject();
        Map<String,Object> pcodeData  = (Map<String,Object>)doc.getCurrentRevision().getProperty("location");
        pcode.setPcodeName((String)pcodeData.get("p_code_name"));
        pcode.setPcodeID((String) pcodeData.get("p_code"));
        pcode.setPcodeLat((String) pcodeData.get("latitude"));
        pcode.setPcodeLong((String) pcodeData.get("longitude"));
        pcode.setLocationType((String) pcodeData.get("location_type"));
        pcode.setDistrict((String) pcodeData.get("district"));
        pcode.setCadastral((String) pcodeData.get("cadastral"));

        beneficiaryObject.setPcode(pcode);

        PCodeObject pcodeDist = new PCodeObject();
        if (doc.getCurrentRevision().getProperty("location_distribution") != null) {
            Map<String, Object> pcodeDataDist = (Map<String, Object>) doc.getCurrentRevision().getProperty("location_distribution");
            pcodeDist.setPcodeName((String) pcodeDataDist.get("p_code_name"));
            pcodeDist.setPcodeID((String) pcodeDataDist.get("p_code"));
            pcodeDist.setPcodeLat((String) pcodeDataDist.get("latitude"));
            pcodeDist.setPcodeLong((String) pcodeDataDist.get("longitude"));
            pcodeDist.setLocationType((String) pcodeData.get("location_type"));
            pcodeDist.setDistrict((String) pcodeData.get("district"));
            pcodeDist.setCadastral((String) pcodeData.get("cadastral"));
            beneficiaryObject.setPcodeDist(pcodeDist);
        }
        else{
            pcodeDist.setPcodeName("");
            pcodeDist.setPcodeID("");
            pcodeDist.setPcodeLat("");
            pcodeDist.setPcodeLong("");
            beneficiaryObject.setPcodeDist(pcodeDist);
        }




        if (doc.getCurrentRevision().getProperty("reason_for_edit") != null)
        beneficiaryObject.setReasonForEdit(doc.getCurrentRevision().getProperty("reason_for_edit").toString());

        ArrayList<Map<String,Object>> childList =  (ArrayList<Map<String,Object>>)doc.getCurrentRevision().getProperty("child_list");

        ArrayList<ChildObject> children = new ArrayList<ChildObject>();

        for(Map<String,Object> child : childList)
        {
            ChildObject childObject = new ChildObject();
            childObject.setAge((String)child.get("age"));
            childObject.setGender((String)child.get("gender"));
            childObject.setStatus((String)child.get("status"));
            childObject.setKit((String)child.get("kit"));
            childObject.setName((String)child.get("name"));
            childObject.setReasonForEdit((String)child.get("reason"));
            childObject.setVoucherCode((String)child.get("voucher_code"));
            children.add(childObject);
        }

        beneficiaryObject.setChildrenList(children);

        if (doc.getCurrentRevision().getProperty("family_list") != null) {
            ArrayList<Map<String, Object>> familyList = (ArrayList<Map<String, Object>>) doc.getCurrentRevision().getProperty("family_list");

            ArrayList<FamilyObject> family = new ArrayList<FamilyObject>();

            for (Map<String, Object> famObj : familyList) {
                FamilyObject familyObject = new FamilyObject();
                familyObject.setIdType((String) famObj.get("id_type"));
                familyObject.setID((String) famObj.get("id_number"));

                family.add(familyObject);
            }

            beneficiaryObject.setFamilyList(family);
        }

        return beneficiaryObject;
    }

    public BeneficiaryObject extractBeneficiary(QueryRow row)
    {
        Document doc = row.getDocument();

        BeneficiaryObject beneficiaryObject = new BeneficiaryObject();

        beneficiaryObject.setMainID(doc.getId());
        beneficiaryObject.setFamilyName(doc.getCurrentRevision().getProperty("family_name").toString());
        beneficiaryObject.setFirstName(doc.getCurrentRevision().getProperty("first_name").toString());
        beneficiaryObject.setMiddleName(doc.getCurrentRevision().getProperty("middle_name").toString());
        beneficiaryObject.setRelationshipType(doc.getCurrentRevision().getProperty("relationship_type").toString());
        beneficiaryObject.setBarcodeNum(doc.getCurrentRevision().getProperty("barcode_num").toString());
        beneficiaryObject.setOfficialID(doc.getCurrentRevision().getProperty("official_id").toString());
        beneficiaryObject.setIdDoesExist(((Boolean) doc.getCurrentRevision().getProperty("id_exists")).booleanValue());
        beneficiaryObject.setIdNotes(doc.getCurrentRevision().getProperty("id_notes").toString());
        beneficiaryObject.setIdType(doc.getCurrentRevision().getProperty("id_type").toString());
        beneficiaryObject.setPhoneNumber(doc.getCurrentRevision().getProperty("phone_number").toString());
        beneficiaryObject.setShawish(((Boolean) doc.getCurrentRevision().getProperty("shawish")).booleanValue());
        beneficiaryObject.setCriticality(doc.getCurrentRevision().getProperty("criticality").toString());
        beneficiaryObject.setCreationDate(doc.getCurrentRevision().getProperty("creation_date").toString());
        beneficiaryObject.setCompletionDate(doc.getCurrentRevision().getProperty("completion_date").toString());
      //  beneficiaryObject.setAnswer1(doc.getCurrentRevision().getProperty("answer1").toString());
      //  beneficiaryObject.setAnswer2(doc.getCurrentRevision().getProperty("answer2").toString());
      //  beneficiaryObject.setAnswer3(doc.getCurrentRevision().getProperty("answer3").toString());
        if (doc.getCurrentRevision().getProperty("moving_location")!= null)
            beneficiaryObject.setMovingLoc(doc.getCurrentRevision().getProperty("moving_location").toString());
        if (doc.getCurrentRevision().getProperty("new_district")!= null)
            beneficiaryObject.setNewDistrict(doc.getCurrentRevision().getProperty("new_district").toString());
        if (doc.getCurrentRevision().getProperty("new_cadastral")!= null)
            beneficiaryObject.setNewCadasteral(doc.getCurrentRevision().getProperty("new_cadastral").toString());

        if (doc.getCurrentRevision().getProperty("disabilities")!= null)
            beneficiaryObject.setDisabilities(doc.getCurrentRevision().getProperty("disabilities").toString());
        if (doc.getCurrentRevision().getProperty("principal_applicant")!= null)
            beneficiaryObject.setDisabilities(doc.getCurrentRevision().getProperty("principal_applicant").toString());
        if (doc.getCurrentRevision().getProperty("phone_owner")!= null)
            beneficiaryObject.setDisabilities(doc.getCurrentRevision().getProperty("phone_owner").toString());
        if (doc.getCurrentRevision().getProperty("assistance_type")!= null)
            beneficiaryObject.setDisabilities(doc.getCurrentRevision().getProperty("assistance_type").toString());
        if (doc.getCurrentRevision().getProperty("over18")!= null)
            beneficiaryObject.setDisabilities(doc.getCurrentRevision().getProperty("over18").toString());


        beneficiaryObject.setComplete(((Boolean)doc.getCurrentRevision().getProperty("completed")).booleanValue());

        PCodeObject pcode = new PCodeObject();
        Map<String,Object> pcodeData  = (Map<String,Object>)doc.getCurrentRevision().getProperty("location");
        pcode.setPcodeName((String) pcodeData.get("p_code_name"));
        pcode.setPcodeID((String) pcodeData.get("p_code"));
        pcode.setPcodeLat((String) pcodeData.get("latitude"));
        pcode.setPcodeLong((String) pcodeData.get("longitude"));
        pcode.setLocationType((String) pcodeData.get("location_type"));
        pcode.setDistrict((String) pcodeData.get("district"));
        pcode.setCadastral((String) pcodeData.get("cadastral"));
        beneficiaryObject.setPcode(pcode);

        PCodeObject pcodeDist = new PCodeObject();
        if (doc.getCurrentRevision().getProperty("location_distribution") != null) {
            Map<String, Object> pcodeDataDist = (Map<String, Object>) doc.getCurrentRevision().getProperty("location_distribution");
            pcodeDist.setPcodeName((String) pcodeDataDist.get("p_code_name"));
            pcodeDist.setPcodeID((String) pcodeDataDist.get("p_code"));
            pcodeDist.setPcodeLat((String) pcodeDataDist.get("latitude"));
            pcodeDist.setPcodeLong((String) pcodeDataDist.get("longitude"));
            pcodeDist.setLocationType((String) pcodeData.get("location_type"));
            pcodeDist.setDistrict((String) pcodeData.get("district"));
            pcodeDist.setCadastral((String) pcodeData.get("cadastral"));
            beneficiaryObject.setPcodeDist(pcodeDist);
        }
        else{
            pcodeDist.setPcodeName("");
            pcodeDist.setPcodeID("");
            pcodeDist.setPcodeLat("");
            pcodeDist.setPcodeLong("");
            beneficiaryObject.setPcodeDist(pcodeDist);
        }



        beneficiaryObject.setReasonForEdit(doc.getCurrentRevision().getProperty("reason_for_edit").toString());

         ArrayList<Map<String,Object>> childList =  (ArrayList<Map<String,Object>>)doc.getCurrentRevision().getProperty("child_list");

        ArrayList<ChildObject> children = new ArrayList<ChildObject>();

        for(Map<String,Object> child : childList)
        {
            ChildObject childObject = new ChildObject();
            childObject.setAge((String)child.get("age"));
            childObject.setGender((String)child.get("gender"));
            childObject.setStatus((String)child.get("status"));
            childObject.setKit((String)child.get("kit"));
            childObject.setName((String)child.get("name"));
            childObject.setReasonForEdit((String)child.get("reason"));
            childObject.setVoucherCode((String)child.get("voucher_code"));
            children.add(childObject);
        }

beneficiaryObject.setChildrenList(children);


        return beneficiaryObject;
    }

    public void addBeneficiary(BeneficiaryObject beneficiaryObject)
    {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Calendar calendar = GregorianCalendar.getInstance();
        String currentTimeString = dateFormatter.format(calendar.getTime());


        beneficiaryObject.setCriticality("0");


        Map<String, Object> bennyContent = new HashMap<String, Object>();

        bennyContent.put("type","assessment");
        bennyContent.put("creation_date",currentTimeString);
        bennyContent.put("completion_date","");//
        bennyContent.put("completed",beneficiaryObject.isComplete());//
        Map<String,Object> pcodeData = new HashMap<String, Object>();
        pcodeData.put("p_code", beneficiaryObject.getPcode().getPcodeID());

        pcodeData.put("p_code_name", beneficiaryObject.getPcode().getPcodeName());
        pcodeData.put("latitude", beneficiaryObject.getPcode().getPcodeLat());
        pcodeData.put("longitude", beneficiaryObject.getPcode().getPcodeLong());
        pcodeData.put("district", beneficiaryObject.getPcode().getDistrict());
        pcodeData.put("cadastral", beneficiaryObject.getPcode().getCadastral());
        pcodeData.put("location_type",beneficiaryObject.getPcode().getLocationType());
        bennyContent.put("location",pcodeData);


        Map<String,Object> pcodeDataDist = new HashMap<String, Object>();
        pcodeDataDist.put("p_code", "");
        pcodeDataDist.put("p_code_name", "");
        pcodeDataDist.put("latitude", "");
        pcodeDataDist.put("longitude", "");
        pcodeDataDist.put("district", "");
        pcodeDataDist.put("cadastral", "");
        pcodeDataDist.put("location_type","");


        bennyContent.put("location_distribution",pcodeDataDist);

//        bennyContent.put("lat",beneficiaryObject.latitude);
//        bennyContent.put("long",beneficiaryObject.longitude);
//        bennyContent.put("alt",beneficiaryObject.altitude);



        bennyContent.put("official_id",beneficiaryObject.getOfficialID());
        //bennyContent.put("id_exists",beneficiaryObject.isIdDoesExist());//
       // bennyContent.put("id_notes",beneficiaryObject.getIdNotes());//
        bennyContent.put("id_type",beneficiaryObject.getIdType());
        bennyContent.put("barcode_num", beneficiaryObject.getBarcodeNum());
        bennyContent.put("phone_number",beneficiaryObject.getPhoneNumber());
        //bennyContent.put("shawish", beneficiaryObject.isShawish());
        bennyContent.put("family_name",beneficiaryObject.getFamilyName());
        bennyContent.put("first_name",beneficiaryObject.getFirstName());
        bennyContent.put("middle_name",beneficiaryObject.getMiddleName());
        bennyContent.put("relationship_type",beneficiaryObject.getRelationshipType());
        bennyContent.put("gender",beneficiaryObject.getGender());
        bennyContent.put("marital_status",beneficiaryObject.getMaritalStatus());
        bennyContent.put("dob",beneficiaryObject.getDateOfBirth());
        bennyContent.put("partner_name",beneficiaryObject.getPartnerName());
        //bennyContent.put("answer1",beneficiaryObject.getAnswer1());
        //bennyContent.put("answer2",beneficiaryObject.getAnswer2());
        //bennyContent.put("answer3",beneficiaryObject.getAnswer3());
        bennyContent.put("moving_location",beneficiaryObject.getMovingLoc());
        bennyContent.put("new_district",beneficiaryObject.getNewDistrict());
        bennyContent.put("new_cadastral",beneficiaryObject.getNewCadasteral());

        bennyContent.put("disabilities",beneficiaryObject.getDisabilities());
        bennyContent.put("phone_owner",beneficiaryObject.getPhoneOwner());
        bennyContent.put("assistance_type",beneficiaryObject.getAssistanceType());
        bennyContent.put("over18",beneficiaryObject.getOver18());
        bennyContent.put("principal_applicant", beneficiaryObject.getPrincipleApplicant());

        bennyContent.put("mothers_name",beneficiaryObject.getMothersName());
        bennyContent.put("family_count",beneficiaryObject.getFamilyCount());


        ArrayList<String> channels = new ArrayList<String>();
        channels.add(beneficiaryObject.partnerName);
        bennyContent.put("channels",channels);



        bennyContent.put("criticality",beneficiaryObject.getCriticality());
        //bennyContent.put("reason_for_edit","");
      //  Map<String,ArrayList<Map<String, Object>>> childData = new HashMap<String, ArrayList<Map<String, Object>>>();
        ArrayList<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();

        for (ChildObject childObject : beneficiaryObject.childrenList)
        {
            Map<String, Object> child = new HashMap<String, Object>();
            child.put("age", childObject.getAge());
            child.put("gender", childObject.getGender());
            child.put("status", childObject.getStatus());
            child.put("kit", childObject.getKit());
            child.put("name",childObject.getName());
            child.put("voucher_code",childObject.getVoucherCode());
            child.put("reason",childObject.getReasonForEdit());
            childList.add(child);
        }


      //  childData.put("children",childList);
        bennyContent.put("child_list",childList);


        ArrayList<Map<String, Object>> familyList = new ArrayList<Map<String, Object>>();

        for (FamilyObject familyObject : beneficiaryObject.getFamilyList())
        {
            Map<String, Object> family = new HashMap<String, Object>();
            family.put("id_type", familyObject.getIdType());
            family.put("id_number", familyObject.getID());

            familyList.add(family);
        }


        //  childData.put("children",childList);
        bennyContent.put("family_list",familyList);


        /*{
            "user": x,
                "action": something,
                "datetime":
        }*/

        JSONObject userObj = UserPrefs.getUserData(mContext);

  ArrayList<Map<String, Object>> historyList = new ArrayList<Map<String, Object>>();
        Map<String, Object> historyObj = new HashMap<String, Object>();

 try {
     historyObj.put("user", userObj.getString("username"));
     historyObj.put("action","HouseHold Added");
     historyObj.put("datetime",currentTimeString);
     historyObj.put("organisation",userObj.getString("organisation"));
     historyList.add(historyObj);
     bennyContent.put("history",historyList);
 }
 catch (JSONException e)
 {

 }

        Document document = database.getDocument(beneficiaryObject.mainID);
        Log.d(TAG, "Document written to database named  with ID = " + document.getId());

        try {
            document.putProperties(bennyContent);

            //com.couchbase.lite.util.Log.d (TAG, "Document written to database named  with ID = " + document.getId());

            Log.e("ERROR", document.getId());

            //return document.getId() +"";
        } catch (CouchbaseLiteException e) {
            com.couchbase.lite.util.Log.e(TAG, "Cannot write document to database", e);
            Log.e("ERROR",e.getMessage());
            //return null;
        }
        catch (Exception e) {
            com.couchbase.lite.util.Log.e(TAG, "Cannot write document to database", e);
            Log.e("ERROR",e.getMessage());
         //   return null;
        }

    }



    public void addComment(String comment, BeneficiaryObject beneficiaryObject)
    {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = GregorianCalendar.getInstance();
        String currentTimeString = dateFormatter.format(calendar.getTime());

        Document retrievedDocument = database.getDocument(beneficiaryObject.getMainID());

        Map<String, Object> bennyContent = new HashMap<String, Object>();
        bennyContent.putAll(retrievedDocument.getProperties());

        if (bennyContent.get("comments") == null){

            Map<String,Object> commentMap = new HashMap<String, Object>();
            commentMap.put("comment",comment);
            commentMap.put("date-time", currentTimeString);
            ArrayList<Map<String,Object>> comments = new ArrayList<Map<String, Object>>();

            comments.add(commentMap);

            bennyContent.put("comments",comments);
        }
        else
        {
            Map<String,Object> commentMap = new HashMap<String, Object>();
            commentMap.put("comment",comment);
            commentMap.put("date-time", currentTimeString);
            ArrayList<Map<String,Object>> comments = (ArrayList<Map<String,Object>> )bennyContent.get("comments");

            comments.add(commentMap);

            bennyContent.put("comments",comments);
        }


        try {
            retrievedDocument.putProperties(bennyContent);
            Log.d(TAG, "Document written to database named  with ID = " + retrievedDocument.getId());
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Cannot write document to database", e);
        }
        catch (Exception e) {
        }

    }

    public void editBeneficiary(BeneficiaryObject beneficiaryObject, String action)
    {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = GregorianCalendar.getInstance();
        String currentTimeString = dateFormatter.format(calendar.getTime());


        Document retrievedDocument = database.getDocument(beneficiaryObject.getMainID());

        // display the retrieved document


        // update the document
        Map<String, Object> bennyContent = new HashMap<String, Object>();
        bennyContent.putAll(retrievedDocument.getProperties());

        //bennyContent.put("creation_date",beneficiaryObject.creationDate);

        Map<String,Object> pcodeData = new HashMap<String, Object>();

        pcodeData.put("p_code", beneficiaryObject.getPcode().getPcodeID());

        pcodeData.put("p_code_name", beneficiaryObject.getPcode().getPcodeName());
        pcodeData.put("latitude", beneficiaryObject.getPcode().getPcodeLat());
        pcodeData.put("longitude", beneficiaryObject.getPcode().getPcodeLong());
        pcodeData.put("district", beneficiaryObject.getPcode().getDistrict());
        pcodeData.put("cadastral", beneficiaryObject.getPcode().getCadastral());
        pcodeData.put("location_type",beneficiaryObject.getPcode().getLocationType());

        bennyContent.put("location",pcodeData);

        Map<String,Object> pcodeDataDist = new HashMap<String, Object>();

            pcodeDataDist.put("p_code", beneficiaryObject.getPcodeDist().getPcodeID());

        pcodeDataDist.put("p_code_name", beneficiaryObject.getPcodeDist().getPcodeName());
        pcodeDataDist.put("latitude", beneficiaryObject.getPcodeDist().getPcodeLat());
        pcodeDataDist.put("longitude", beneficiaryObject.getPcodeDist().getPcodeLong());
        pcodeDataDist.put("district", beneficiaryObject.getPcodeDist().getDistrict());
        pcodeDataDist.put("cadastral", beneficiaryObject.getPcodeDist().getCadastral());
        pcodeDataDist.put("location_type",beneficiaryObject.getPcodeDist().getLocationType());

        bennyContent.put("location_distribution",pcodeDataDist);





//        bennyContent.put("lat",beneficiaryObject.latitude);
//        bennyContent.put("long",beneficiaryObject.longitude);
//        bennyContent.put("alt",beneficiaryObject.altitude);

        bennyContent.put("completion_date",beneficiaryObject.getCompletionDate());
        bennyContent.put("completed",beneficiaryObject.isComplete());


        bennyContent.put("official_id",beneficiaryObject.getOfficialID());
      //  bennyContent.put("id_exists",beneficiaryObject.isIdDoesExist());
      //  bennyContent.put("id_notes",beneficiaryObject.getIdNotes());

        bennyContent.put("id_type",beneficiaryObject.getIdType());
        bennyContent.put("barcode_num", beneficiaryObject.getBarcodeNum());
        bennyContent.put("phone_number",beneficiaryObject.getPhoneNumber());
        //bennyContent.put("shawish",beneficiaryObject.isShawish());

        bennyContent.put("family_name",beneficiaryObject.getFamilyName());
        bennyContent.put("first_name",beneficiaryObject.getFirstName());
        bennyContent.put("middle_name",beneficiaryObject.getMiddleName());
        bennyContent.put("criticality",beneficiaryObject.getCriticality());
        bennyContent.put("reason_for_edit",beneficiaryObject.getReasonForEdit());

        bennyContent.put("gender",beneficiaryObject.getGender());
        bennyContent.put("marital_status",beneficiaryObject.getMaritalStatus());
        bennyContent.put("dob",beneficiaryObject.getDateOfBirth());
        bennyContent.put("partner_name",beneficiaryObject.getPartnerName());
        //bennyContent.put("answer1",beneficiaryObject.getAnswer1());
      //  bennyContent.put("answer2",beneficiaryObject.getAnswer2());
        //bennyContent.put("answer3",beneficiaryObject.getAnswer3());
        bennyContent.put("moving_location",beneficiaryObject.getMovingLoc());

        bennyContent.put("new_district",beneficiaryObject.getNewDistrict());
        bennyContent.put("new_cadastral",beneficiaryObject.getNewCadasteral());

        bennyContent.put("relationship_type",beneficiaryObject.getRelationshipType());

        bennyContent.put("disabilities",beneficiaryObject.getDisabilities());
        bennyContent.put("phone_owner",beneficiaryObject.getPhoneOwner());
        bennyContent.put("assistance_type",beneficiaryObject.getAssistanceType());
        bennyContent.put("over18",beneficiaryObject.getOver18());
        bennyContent.put("principal_applicant",beneficiaryObject.getPrincipleApplicant());
        bennyContent.put("mothers_name",beneficiaryObject.getMothersName());
        bennyContent.put("family_count",beneficiaryObject.getFamilyCount());



        ArrayList<String> channels = new ArrayList<String>();
        channels.add(beneficiaryObject.partnerName);
        bennyContent.put("channels",channels);


        //  Map<String,ArrayList<Map<String, Object>>> childData = new HashMap<String, ArrayList<Map<String, Object>>>();
        ArrayList<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();

        for (ChildObject childObject : beneficiaryObject.childrenList)
        {
            Map<String, Object> child = new HashMap<String, Object>();
            child.put("age", childObject.getAge());
            child.put("gender", childObject.getGender());
            child.put("status", childObject.getStatus());
            child.put("kit", childObject.getKit());
            child.put("name",childObject.getName());
            child.put("voucher_code",childObject.getVoucherCode());
            child.put("reason",childObject.getReasonForEdit());
            childList.add(child);
        }

        //  childData.put("children",childList);
        bennyContent.put("child_list",childList);

        ArrayList<Map<String, Object>> familylist = new ArrayList<Map<String, Object>>();

        for (FamilyObject familyObject : beneficiaryObject.familyList)
        {
            Map<String, Object> family = new HashMap<String, Object>();
            family.put("id_type", familyObject.getIdType());
            family.put("id_number", familyObject.getID());

            familylist.add(family);
        }

        //  childData.put("children",childList);
        bennyContent.put("family_list",familylist);


        JSONObject userObj = UserPrefs.getUserData(mContext);

        ArrayList<Map<String, Object>> historyList = (ArrayList<Map<String, Object>>)retrievedDocument.getCurrentRevision().getProperty("history");//new ArrayList<Map<String, Object>>();


        Map<String, Object> historyObj = new HashMap<String, Object>();

        try {
            historyObj.put("user", userObj.getString("username"));
            historyObj.put("action",action);
            historyObj.put("datetime",currentTimeString);
            historyObj.put("organisation",userObj.getString("organisation"));
            historyList.add(historyObj);
            bennyContent.put("history",historyList);
        }
        catch (JSONException e)
        {

        }

       // Document document = database.createDocument();

        try {
            retrievedDocument.putProperties(bennyContent);
            Log.d(TAG, "Document written to database named  with ID = " + retrievedDocument.getId());
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Cannot write document to database", e);
        }
        catch (Exception e) {
        }

    }

    public void addSurvey(Map<String,Object> survey, String docID)
    {
        Document retrievedDocument = database.getDocument(docID);
        Map<String, Object> allContent = new HashMap<String, Object>();
        allContent.putAll(retrievedDocument.getProperties());

        if (retrievedDocument.getCurrentRevision().getProperty("surveys")==null)
        {
            ArrayList<Map<String,Object>> surveys = new ArrayList<Map<String, Object>>();
            surveys.add(survey);
            //Map<String,Object> sur = new HashMap<String, Object>();
            allContent.put("surveys", surveys);

            try {
                retrievedDocument.putProperties(allContent);
                Log.d(TAG, "Document written to database named  with ID = " + retrievedDocument.getId());
            } catch (CouchbaseLiteException e) {
                Log.e(TAG, "Cannot write document to database", e);
            }
            catch (Exception e) {
            }
        }
        else
        {
            String name = (String)survey.keySet().toArray()[0];
            ArrayList<Map<String,Object>> objs =  (ArrayList<Map<String,Object>>)allContent.get("surveys");
            boolean t = false;
            int total = objs.size();
            for(int i =0;i<total;i++)
            {
                Map<String,Object> obj = objs.get(i);
                String sName = (String)obj.keySet().toArray()[0];
                if (sName.equals(name))
                {
                   ((ArrayList<Map<String,Object>>) allContent.get("surveys")).remove(i);
                    break;
                }
            }

            ((ArrayList<Map<String,Object>>)allContent.get("surveys")).add(survey);


            try {
                retrievedDocument.putProperties(allContent);
                Log.d(TAG, "Document written to database named  with ID = " + retrievedDocument.getId());
            } catch (CouchbaseLiteException e) {
                Log.e(TAG, "Cannot write document to database", e);
            }
            catch (Exception e) {
            }
        }
    }


    public com.couchbase.lite.View startCompletedItemsView(final Context context, String increment)
    {
        com.couchbase.lite.View viewItemsByCompletion = database.getView(String.format("%s/%s", designDocName, completedViewName));
        viewItemsByCompletion.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object isCompleted = document.get("completed");
                Object partnerName = document.get("partner_name");

                //Object textVal = document.get("text");
                //  Map<String,String> usermap = UserPrefs.getUserProfile(context);
                // String user = usermap.get("user").toLowerCase();
                String org = UserPrefs.getOrganisation(context);

                if (isCompleted != null && partnerName != null && org.toLowerCase().equals(partnerName.toString().toLowerCase())) {

                    Boolean iscomp = ((Boolean) isCompleted).booleanValue();
                    if (iscomp != null && iscomp == true) {

                        Object byCreation = document.get("creation_date");
                        emitter.emit(byCreation.toString(), null);

                    }
                }
            }
        }, increment);

        return viewItemsByCompletion;
    }


    public com.couchbase.lite.View startAggrigatedItemsView(final Context context,String increment)
    {
        com.couchbase.lite.View viewItemsByAgg = database.getView(String.format("%s/%s", designDocName, aggViewName));

        viewItemsByAgg.setMapReduce(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {

                Object partnerName = document.get("partner_name");
                // Map<String,String> usermap = UserPrefs.getUserProfile(context);
                // String user = usermap.get("user").toLowerCase();
                String org = UserPrefs.getOrganisation(context);


                if (partnerName != null && org.toLowerCase().equals(partnerName.toString().toLowerCase())) {

                    Map<String, Object> byLocation = (Map<String, Object>) document.get("location");
                    Map<String, Object> byLocationParent = (Map<String, Object>) byLocation.get("parent");
                    Object vdc = byLocation.get("p_code_name");//byLocationParent.get("p_code_name");

                    ArrayList<Map<String, Object>> itemObjects = (ArrayList<Map<String, Object>>) document.get("item_list");

                    for (Map<String, Object> itemObject : itemObjects)
                        emitter.emit(vdc, itemObject);
                }
            }
        }, new Reducer() {
            @Override
            public Object reduce(List<Object> keys, List<Object> values, boolean rereduce) {


                Map<String, Object> placeMap = new TreeMap<String, Object>();
                for (int i = 0; i < keys.size(); i++) {

                    String key = (String) keys.get(i);
                    Map<String, Object> it = (Map<String, Object>) values.get(i);

                    if (!placeMap.containsKey(key)) {

                        int del = 0;
                        if (it.get("delivered") != null)
                            del = (Integer) it.get("delivered");
                        else
                            del = 0;
                        int quant = (Integer) it.get("quantity"); //Integer.parseInt((String)it.get("quantity"));
                        String item_type = (String) it.get("item_type");
                        Map<String, Object> quantMap = new HashMap<String, Object>();


                        quantMap.put("total_del", del);
                        quantMap.put("total_quant", quant);
                        Map<String, Object> itemMap = new HashMap<String, Object>();

                        itemMap.put("del", del);
                        itemMap.put("quant", quant);

                        quantMap.put(item_type, itemMap);


                        placeMap.put(key, quantMap);

                    } else {

                        Map<String, Object> quantMap = (Map<String, Object>) placeMap.get(key);
                        int del = 0;
                        if (it.get("delivered") != null)
                            del = (Integer) it.get("delivered");
                        else
                            del = 0;
                        int quant = (Integer) it.get("quantity");
                        String item_type = (String) it.get("item_type");


                        quantMap.put("total_del", (Integer) quantMap.get("total_del") + del);
                        quantMap.put("total_quant", (Integer) quantMap.get("total_quant") + quant);

                        if (!quantMap.containsKey(item_type)) {
                            Map<String, Object> itemMap = new HashMap<String, Object>();

                            itemMap.put("del", del);
                            itemMap.put("quant", quant);

                            quantMap.put(item_type, itemMap);
                        } else {
                            Map<String, Object> itemMap = (Map<String, Object>) quantMap.get(item_type);
                            itemMap.put("del", (Integer) itemMap.get("del") + del);
                            itemMap.put("quant", (Integer) itemMap.get("quant") + quant);
                            quantMap.put(item_type, itemMap);
                        }


                        placeMap.put(key, quantMap);
                    }
                }

                return placeMap;
            }
        }, increment + "122");

        return viewItemsByAgg;
    }



    public com.couchbase.lite.View startAggrigatedItemsViewforAssessments(final Context context,String increment)
    {
        com.couchbase.lite.View viewItemsByAgg = database.getView(String.format("%s/%s", designDocName, aggViewName));

        viewItemsByAgg.setMapReduce(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {

                Object partnerName = document.get("partner_name");
                // Map<String,String> usermap = UserPrefs.getUserProfile(context);
                // String user = usermap.get("user").toLowerCase();
                String org = UserPrefs.getOrganisation(context);


                if (partnerName != null && org.toLowerCase().equals(partnerName.toString().toLowerCase())) {

                    Map<String, Object> byLocation = (Map<String, Object>) document.get("location");
                    //Map<String,Object>  byLocationParent  = (Map<String,Object>)byLocation.get("parent");
                    Object site_name = byLocation.get("p_code_name");//byLocationParent.get("p_code_name");
                    Object site = byLocation.get("p_code");
                    Object officialID = document.get("official_id");
                    //  Map<String,Object> childObj = new HashMap<String, Object>();
                    // childObj.put("site_name",site_name);
                    // childObj.put("site",site);

                    ArrayList<Map<String, Object>> childObjects = (ArrayList<Map<String, Object>>) document.get("child_list");

                    for (Map<String, Object> itemObject : childObjects) {
                        itemObject.put("official_id", officialID);
                        emitter.emit(site_name, itemObject);
                    }
                }
            }
        }, new Reducer() {
            @Override
            public Object reduce(List<Object> keys, List<Object> values, boolean rereduce) {


                Map<Object, Object> placeMap = new TreeMap<Object, Object>();
                for (int i = 0; i < keys.size(); i++) {

                    String key = (String) keys.get(i);

                    Map<String, Object> it = (Map<String, Object>) values.get(i);

                    if (!placeMap.containsKey(key)) {


                        String gender = (String) it.get("gender");

                        String age = (String) it.get("age");

                        String officialId = (String) it.get("official_id");

                        //Integer.parseInt((String)it.get("quantity"));
                        // String item_type = (String)it.get("item_type");
                        Map<String, Object> ageMap = new HashMap<String, Object>();
                        Map<String, Object> genderMap = new HashMap<String, Object>();
                        Map<String, Object> offIdMap = new HashMap<String, Object>();

                        ageMap.put(age, 1);
                        genderMap.put(gender, 1);
                        offIdMap.put(officialId, 1);


                        Map<String, Object> childMap = new HashMap<String, Object>();

                        childMap.put("age", ageMap);
                        childMap.put("gender", genderMap);
                        childMap.put("official_id", offIdMap);

                        placeMap.put(key, childMap);

                    } else {

                        Map<String, Object> childMap = (Map<String, Object>) placeMap.get(key);

                        String gender = (String) it.get("gender");

                        String age = (String) it.get("age");

                        String officialId = (String) it.get("official_id");


                        Map<String, Object> ageMap = (Map<String, Object>) childMap.get("age");
                        Map<String, Object> genderMap = (Map<String, Object>) childMap.get("gender");
                        Map<String, Object> offIdMap = (Map<String, Object>) childMap.get("official_id");


                        if (age != null) {
                            if (ageMap.containsKey(age)) {
                                ageMap.put(age, (Integer) ageMap.get(age) + 1);
                            } else
                                ageMap.put(age, 1);
                        } else {
                            Log.e("W", "W");
                        }


                        if (gender != null) {
                            if (genderMap.containsKey(gender))
                                genderMap.put(gender, (Integer) genderMap.get(gender) + 1);
                            else
                                genderMap.put(gender, 1);
                        }

                        if (officialId != null) {
                            offIdMap.put(officialId, 1);
                        }

                        childMap.put("age", ageMap);
                        childMap.put("gender", genderMap);
                        childMap.put("official_id", offIdMap);


                        placeMap.put(key, childMap);
                    }
                }

                return placeMap;
            }
        }, increment + "13");

        return viewItemsByAgg;
    }


    public com.couchbase.lite.View startPcodeItemsView(final Context context,String increment)
    {
        com.couchbase.lite.View viewItemsByCompletion = database.getView(String.format("%s/%s", designDocName, "pcodeview"));
        viewItemsByCompletion.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object p_code = document.get("p_code");
                Object district = document.get("district");
                Object cadastral = document.get("cadastral");

                Map<String,Object> locationMap = ( Map<String,Object>) UserPrefs.getLocations(context);

                ArrayList<String> locations = (ArrayList<String>)locationMap.get("locations");

                String locationType =  (String) locationMap.get("location_type");

                if (p_code != null ) {

                    boolean locationExists = false;
                    for (String location : locations)
                    {

                        if (locationType.equals("district"))
                        {
                            if (location.equals(district.toString()))
                            {
                                locationExists = true;
                                break;
                            }
                        }
                        else if (locationType.equals("cadastral"))
                        {
                            if (location.equals(cadastral.toString()))
                            {
                                locationExists = true;
                                break;
                            }
                        }

                    }


                    if (locationExists) {
                        Object byName = document.get("p_code_name");

                        emitter.emit(byName.toString(), null);
                    }

                }
            }
        }, increment+"2");

        return viewItemsByCompletion;
    }


    public com.couchbase.lite.View startAssessmentsView(final Context context,String increment)
    {
        com.couchbase.lite.View viewItemsByCompletion = database.getView(String.format("%s/%s", designDocName, assessmentViewName));
        viewItemsByCompletion.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {

                Object docType = document.get("type");
                Object isCompleted = document.get("completed");
                Object partnerName = document.get("partner_name");
                Object assistanceType = document.get("assistance_type");
                //String userAssistanceType = UserPrefs.getAssistanceType(context);

                //Object textVal = document.get("text");
                //Map<String,String> usermap = UserPrefs.getUserProfile(context);
                // String user = usermap.get("user").toLowerCase();
                String org = UserPrefs.getOrganisation(context);

                if (docType != null && docType.equals("assessment"))
                {


                    if (partnerName != null && org.toLowerCase().equals(partnerName.toString().toLowerCase()) && assistanceType != null && assistanceType.equals("cash"))
                    {
                        if (isCompleted != null) {
                            boolean iscomp = ((Boolean) isCompleted).booleanValue();
                            Object byCreation = document.get("creation_date");
                            emitter.emit(byCreation.toString(), null);
                        }
                    }
                    // ADDED FOR SAWA DUE TO MISTAKE
//                    else if (org.toLowerCase().equals("sawa") || org.toLowerCase().equals("unicef-test"))
//                    {
//                        if (partnerName != null && "user5".toLowerCase().equals(partnerName.toString().toLowerCase()) )
//                        {
//                            if (isCompleted != null) {
//
//                                boolean iscomp = ((Boolean) isCompleted).booleanValue();
//
//                                Object byCreation = document.get("creation_date");
//
//                                emitter.emit(byCreation.toString(), null);
//
//                            }
//                        }
//                    }
                }
            }
        }, "3"+increment);

        return viewItemsByCompletion;
    }

    public com.couchbase.lite.View startKitsView(final Context context,String increment)
    {
        com.couchbase.lite.View viewItemsByCompletion = database.getView(String.format("%s/%s", designDocName, kitsViewName));
        viewItemsByCompletion.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {

                Object docType = document.get("type");
                Object isCompleted = document.get("completed");
                Object partnerName = document.get("partner_name");
                Object assistanceType = document.get("assistance_type");
                //ArrayList<String> userAssistanceType = UserPrefs.getAssistanceType(context);

                //Object textVal = document.get("text");
                //Map<String,String> usermap = UserPrefs.getUserProfile(context);
                // String user = usermap.get("user").toLowerCase();
                String org = UserPrefs.getOrganisation(context);

                if (docType != null && docType.equals("assessment"))
                {


                     if (partnerName != null && org.toLowerCase().equals(partnerName.toString().toLowerCase()) && assistanceType != null && assistanceType.equals("kits"))
                    {
                        if (isCompleted != null) {

                            boolean iscomp = ((Boolean) isCompleted).booleanValue();

                            Object byCreation = document.get("creation_date");

                            emitter.emit(byCreation.toString(), null);

                        }
                    }
//                    // ADDED FOR SAWA DUE TO MISTAKE
//                    else if (org.toLowerCase().equals("sawa") || org.toLowerCase().equals("unicef-test"))
//                    {
//                        if (partnerName != null && "user5".toLowerCase().equals(partnerName.toString().toLowerCase()))
//                        {
//                            if (isCompleted != null) {
//
//                                boolean iscomp = ((Boolean) isCompleted).booleanValue();
//
//                                Object byCreation = document.get("creation_date");
//
//                                emitter.emit(byCreation.toString(), null);
//
//                            }
//                        }
//                    }
                }
            }
        }, "3"+increment);

        return viewItemsByCompletion;
    }


    public com.couchbase.lite.View startPendingItemsView(final Context context,String increment)
    {
        com.couchbase.lite.View viewItemsByCompletion = database.getView(String.format("%s/%s", designDocName, pendingViewName));
        viewItemsByCompletion.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {

                Object docType = document.get("type");
                Object isCompleted = document.get("completed");
                Object partnerName = document.get("partner_name");

                //Object textVal = document.get("text");
                //Map<String,String> usermap = UserPrefs.getUserProfile(context);
               // String user = usermap.get("user").toLowerCase();
                String org = UserPrefs.getOrganisation(context);

                if (docType!=null && docType.equals("distribution")) {
                    if (isCompleted != null && partnerName != null && org.toLowerCase().equals(partnerName.toString().toLowerCase())) {

                        boolean iscomp = ((Boolean) isCompleted).booleanValue();
                        if (iscomp == false) {

                            Object byCreation = document.get("creation_date");

                            emitter.emit(byCreation.toString(), null);

                        }
                    }
                }
            }
        }, "0"+increment+"4");

        return viewItemsByCompletion;
    }


    public com.couchbase.lite.View startLogItemsView(final Context context,String increment)
    {
        com.couchbase.lite.View viewItemsByCompletion = database.getView(String.format("%s/%s", designDocName, logsViewName));
        viewItemsByCompletion.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {

                Object docType = document.get("type");
                //Object isCompleted = document.get("completed");
                Object partnerName = document.get("partner_name");

                //Object textVal = document.get("text");
                //Map<String,String> usermap = UserPrefs.getUserProfile(context);
                // String user = usermap.get("user").toLowerCase();
                String org = UserPrefs.getOrganisation(context);

                if (docType!=null && docType.equals("logs")) {

                            Object byCreation = document.get("date");
                            emitter.emit(byCreation.toString(), null);

                }
            }
        }, increment);

        return viewItemsByCompletion;
    }



    public void editLog(LogObject logObject)
    {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = GregorianCalendar.getInstance();
        String currentTimeString = dateFormatter.format(calendar.getTime());
        String getUser = UserPrefs.getUsername(mContext);
        Document retrievedDocument = database.getDocument("LOG2:" + getUser + ":" + currentTimeString);


        Map<String,Object> logMap = retrievedDocument.getProperties();

        ArrayList<Map<String,Object>> logs = (ArrayList<Map<String,Object>>)logMap.get("logs");
        Map<String,Object> log = new HashMap<String, Object>();
        log.put("location",logObject.getPlace());
        log.put("time",logObject.getTime());
        log.put("item_type",logObject.getItemName());
        log.put("amount", logObject.getAmount());

        logs.add(log);

        //logMap.put("logs",logs);

        try {
            retrievedDocument.putProperties(logMap);

            //return document.getId();
        } catch (CouchbaseLiteException e) {
            com.couchbase.lite.util.Log.e(TAG, "Cannot write document to database", e);
            //return null;
        }
        catch (Exception e) {
            //return null;
        }

    }

    public void addLog(LogObject logObject)
    {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = GregorianCalendar.getInstance();
        String currentTimeString = dateFormatter.format(calendar.getTime());
        Map<String,Object> logMap = new HashMap<String, Object>();

        logMap.put("date",currentTimeString);
        logMap.put("type","logs");
        ArrayList<Map<String,Object>> logs = new ArrayList<Map<String, Object>>();
        Map<String,Object> log = new HashMap<String, Object>();
        log.put("location",logObject.getPlace());
        log.put("time",logObject.getTime());
        log.put("item_type", logObject.getItemName());
        log.put("amount", logObject.getAmount());

        logs.add(log);

        logMap.put("logs",logs);

        String getUser = UserPrefs.getUsername(mContext);

        ArrayList<String> channels = new ArrayList<String>();
        channels.add(getUser);
        //channels.add("users");
        //channels.add("locations");
        //channels.add("surveys");
        logMap.put("channels",channels);

        Document document = database.getDocument("LOG2:"+getUser+":"+currentTimeString);
        Log.d(TAG, "Document written to database named  with ID = " + document.getId());

        try {
            document.putProperties(logMap);

            //return document.getId();
        } catch (CouchbaseLiteException e) {
            com.couchbase.lite.util.Log.e(TAG, "Cannot write document to database", e);
            //return null;
        }
        catch (Exception e) {
            //return null;
        }

    }

    public void testAdditionOne(String msg)
    {
        // get the current date and time
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = GregorianCalendar.getInstance();
        String currentTimeString = dateFormatter.format(calendar.getTime());

        // create an object that contains data for a document
        Map<String, Object> docContent = new HashMap<String, Object>();
        docContent.put("message", msg);
        docContent.put("creationDate", currentTimeString);

        // display the data for the new document
        com.couchbase.lite.util.Log.d(TAG, "docContent=" + String.valueOf(docContent));

        // create an empty document
        Document document = database.createDocument();

        // add content to document and write the document to the database
        try {
            document.putProperties(docContent);
            com.couchbase.lite.util.Log.d(TAG, "Document written to database named  with ID = " + document.getId());
        } catch (CouchbaseLiteException e) {
            com.couchbase.lite.util.Log.e(TAG, "Cannot write document to database", e);
        }
        catch (Exception e) {
        }

    }

    @Override
    public void changed(Replication.ChangeEvent event) {


        Log.e("REP4","REP4");

        Replication replication = event.getSource();
        Log.d(TAG, "Replication : " + replication + " changed.");
        if (!replication.isRunning()) {
            String msg = String.format("Replicator %s not running", replication);
            Log.d(TAG, msg);
        }
        else {
            int processed = replication.getCompletedChangesCount();
            int total = replication.getChangesCount();
            String msg = String.format("Replicator processed %d / %d", processed, total);
            Log.d(TAG, msg);
        }

    }

    private Replication.ChangeListener pullChangeListener()
    {
        return new Replication.ChangeListener() {
            @Override
            public void changed(final Replication.ChangeEvent event) {


                Log.e("REP2","REP2");

                Replication replication = event.getSource();


                totalPullDocs = replication.getChangesCount();
                pullDocs = replication.getCompletedChangesCount();

                switch (replication.getStatus()) {
                    case REPLICATION_ACTIVE:
                        netStatus = "Syncing Data...";
                        break;
                    case REPLICATION_IDLE:
                        netStatus ="Data Updated";
                        break;
                    case REPLICATION_OFFLINE:
                        netStatus ="Offline";
                        break;
                    case REPLICATION_STOPPED:
                        netStatus ="Stopped";
                        break;
                }
            }

        };

    }



    private Replication.ChangeListener pushChangeListener()
    {
        return new Replication.ChangeListener() {
            @Override
            public void changed(final Replication.ChangeEvent event) {
                Log.e("REP3","REP3");

                Replication replication = event.getSource();
                totalPushDocs = replication.getChangesCount();
                pushDocs = replication.getCompletedChangesCount();

            }
        };
    }


    ////////////////////////
   //////SSL CLASS/////////
    ////////////////////

    class  MySSLSocketFactory  extends SSLSocketFactory {
        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{tm}, null);
        }
    }


    public enum DistStat {
        NONE,FORCE,NO_FORCE,COMMENT_ADDED,COMMENT_EDITED;
    }

}
