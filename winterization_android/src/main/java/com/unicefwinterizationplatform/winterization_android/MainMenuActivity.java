package com.unicefwinterizationplatform.winterization_android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tarek on 9/19/2014.
 */
public class MainMenuActivity extends HeaderActivity {


    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            //  CouchBaseManager.getInstance(this).testAdditionOne("TWO");
            //  CouchBaseManager.getInstance(this).printAllRows();


       try {
           PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
           String version = pInfo.versionName;
           if (!version.equals(UserPrefs.getLatestVersionData(getApplicationContext())))
           {

               LayoutInflater li = LayoutInflater.from(context);
               View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                       context);

               // set prompts.xml to alertdialog builder
               alertDialogBuilder.setView(promptsView);

               final TextView barcodeVal = (TextView) promptsView
                       .findViewById(R.id.textView_barcodeNumber);

                   barcodeVal.setText("New version "+UserPrefs.getLatestVersionData(getApplicationContext())+" of UniSupply Available. Please make sure you sync up all your information before installing");
                   // set dialog message
                   alertDialogBuilder
                           .setCancelable(false)
                           .setPositiveButton("Download Now",
                                   new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int id) {
                                           Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://equitrack.uniceflebanon.org/winter/app"));
                                           startActivity(browserIntent);

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
       }
       catch (Exception e){

       }
        LinearLayout parentLayout = (LinearLayout) this.findViewById(R.id.layout_main);

        TextView titleText = (TextView) findViewById(R.id.textView_userPref);
        if (role.equals(Constants.ROLE_KITS_ASSESSOR)) {
            titleText.setText("Kits Assessments");
        } else if (role.equals(Constants.ROLE_KITS_DISTRIBUTOR)) {
            titleText.setText("Kits Distribution");
        } else if (role.equals(Constants.ROLE_VOUCHERS_DISTRIBUTOR)) {
            titleText.setText("Voucher Distribution");
        } else if (role.equals(Constants.ROLE_VOUCHERS_REDEEMER)) {
            titleText.setText("Voucher Redemption");
        }

        Button addButton = (Button) this.findViewById(R.id.add_resident_btn);
        addButton.setOnClickListener(AddListener());
        Button browseButton = (Button) this.findViewById(R.id.browse_residents_btn);
        browseButton.setOnClickListener(BrowseListener());
        Button voucherButton = (Button) this.findViewById(R.id.add_vouchers);
        voucherButton.setOnClickListener(startVoucherActivity());

        Button barcodeButton = (Button) this.findViewById(R.id.scan_barcode_btn);
        barcodeButton.setOnClickListener(BarcodeButtonListener());
        Button redeemButton = (Button) this.findViewById(R.id.redeem_vouchers_btn);
        redeemButton.setOnClickListener(redeemVoucherActivity());

        //   kits-assessor, kits-distributor, vouchers-distributor

        if (role.equals(Constants.ROLE_KITS_DISTRIBUTOR)) {

            parentLayout.removeView(addButton);
            parentLayout.removeView(browseButton);
            parentLayout.removeView(voucherButton);
            parentLayout.removeView(redeemButton);
        } else if (role.equals(Constants.ROLE_KITS_ASSESSOR)) {

            //    if(getIntent().getAction().equals("doAssessments")) {
            parentLayout.removeView(barcodeButton);
            parentLayout.removeView(voucherButton);
            parentLayout.removeView(redeemButton);

            //  }

            //   else if(getIntent().getAction().equals("doDistribution"))
            //   {
            //      parentLayout.removeView(addButton);
            //    parentLayout.removeView(browseButton);
            //    parentLayout.removeView(voucherButton);
            // }

        } else if (role.equals(Constants.ROLE_VOUCHERS_DISTRIBUTOR)) {
            parentLayout.removeView(addButton);
            parentLayout.removeView(barcodeButton);
            parentLayout.removeView(browseButton);
            parentLayout.removeView(redeemButton);


        }else if (role.equals(Constants.ROLE_VOUCHERS_REDEEMER)) {
            parentLayout.removeView(addButton);
            parentLayout.removeView(barcodeButton);
            parentLayout.removeView(browseButton);
           // parentLayout.removeView(voucherButton);


        } else {
            parentLayout.removeView(addButton);
            parentLayout.removeView(barcodeButton);
            parentLayout.removeView(browseButton);
            parentLayout.removeView(voucherButton);
            parentLayout.removeView(redeemButton);

        }

        //  String name = UserPrefs.getUsername(this);
        Log.d("NAME", role);

    }


    private View.OnClickListener AddListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GeolocationActivity.class);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener BrowseListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getApplicationContext(), BrowseBeneficiariesActivity.class);
                startActivity(intent);
            }
        };
    }


    private View.OnClickListener startVoucherActivity() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {


             //   if (role.equals(Constants.ROLE_VOUCHERS_DISTRIBUTOR)) {

                    JSONObject userObj = UserPrefs.getUserData(getApplicationContext());
                    JSONArray roles = null;
                    try {
                        roles = userObj.getJSONArray("roles");
                        //firstRole =  roles.getString(0);
                    }
                    catch (JSONException e)
                    {

                    }

                    if (roles.length()>1) {
                        Intent intent = new Intent(getApplicationContext(), SelectSDCLocationActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(getApplicationContext(), BrowseBeneficiariesActivity.class);
                        startActivity(intent);
                    }
                }
           // }
        };
    }

    private View.OnClickListener redeemVoucherActivity() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReaderActivity.class);
                intent.setAction("redeemVoucher");
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener BarcodeButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (role.equals(Constants.ROLE_KITS_DISTRIBUTOR)) {
                    Intent intent = new Intent(getApplicationContext(), PCodeSelectionActivity.class);
                    intent.setAction("getDistPcode");
                    startActivity(intent);
                }

            }
        };

    }

}


