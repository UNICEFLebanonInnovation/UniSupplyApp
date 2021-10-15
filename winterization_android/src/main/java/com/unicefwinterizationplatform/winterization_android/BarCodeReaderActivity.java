package com.unicefwinterizationplatform.winterization_android;

/**
 * Created by Tarek on 9/25/2014.
 */
//import net.sourceforge.zbar.android.CameraTest.CameraPreview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.*;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;

import android.graphics.ImageFormat;

/* Import ZBar Class files */

import com.couchbase.lite.CouchbaseLiteException;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import net.sourceforge.zbar.Config;

public class BarCodeReaderActivity extends HeaderActivity
{
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

   // TextView scanText;
    Button scanButton;
    Button manualButton;
    final Context context = this;
    ImageScanner scanner;
    Activity thisActivity = this;
    private boolean barcodeScanned = false;
    private boolean previewing = true;

    private String ActionStatment;

    static {
        System.loadLibrary("iconv");
    }



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_barcode);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


      if(role.equals(Constants.ROLE_KITS_DISTRIBUTOR))
        {
            Button browseButton = (Button)findViewById(R.id.Button_browse);
            browseButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, BrowseBeneficiariesActivity.class);
                    thisActivity.finish();
                    startActivity(intent);
                }
            });
        }

        else if(role.equals(Constants.ROLE_KITS_ASSESSOR))
      {
          Button browseButton = (Button)findViewById(R.id.Button_browse);
          LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.layout_barcodeButtons);
          buttonLayout.removeView(browseButton);

      }

       manualButton = (Button)findViewById(R.id.ManualButton);

        manualButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.alert_manualbarcode, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        getManualCheckAlert(userInput.getText().toString());

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

    public void onStart(){
        super.onStart();

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
    }

    public void onResume(){
        super.onResume();

      //  mCamera = getCameraInstance();

    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Size size = parameters.getPreviewSize();
            String barcodeValue ="";
            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    // scanText.setText("barcode result " + sym.getData());
                    barcodeValue = sym.getData();
                    barcodeScanned = true;
                }
                //   kits-assessor, kits-distributor, vouchers-distributor
                if (getIntent().getAction().equals("searchBarcodes")) {
                    if(role.equals(Constants.ROLE_KITS_ASSESSOR)) {
                        if (getIntent().getStringExtra("barcodeType").equals("id")) {
                            Intent intent = new Intent(context, DetailedBeneficiaryActivity.class);
                            thisActivity.finish();
                            startActivity(intent);
                        } else if (getIntent().getStringExtra("barcodeType").equals("voucher")) {
                            Intent intent = new Intent(context, VoucherActivity.class);
                            thisActivity.finish();
                            startActivity(intent);
                        }
                    }
                    else  if (role.equals(Constants.ROLE_KITS_DISTRIBUTOR))
                    {
                        Intent intent = new Intent(context, DistributorBeneficiaryActivity.class);
                        thisActivity.finish();
                        startActivity(intent);
                    }

                }
               else if (getIntent().getAction().equals("addBarcode")) {
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final TextView barcodeVal = (TextView) promptsView
                            .findViewById(R.id.textView_barcodeNumber);

                    barcodeVal.setText("Barcode Value is: " + barcodeValue);
                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Accept",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // get user input and set it to result
                                            // edit text
                                            //result.setText(userInput.getText());
                                            Intent intent = new Intent(context, AddSuccessActivity.class);
                                            PCodeObject pcode = getIntent().getParcelableExtra("PCODE");
                                            intent.putExtra("PCODE",pcode);
                                            thisActivity.finish();
                                            startActivity(intent);

                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            mCamera.setPreviewCallback(previewCb);
                                            mCamera.startPreview();
                                            previewing = true;
                                            mCamera.autoFocus(autoFocusCB);
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };


  public void getManualCheckAlert(String barcodeValue)
  {
      LayoutInflater li = LayoutInflater.from(context);
      View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

      AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
              context);

      // set prompts.xml to alertdialog builder
      alertDialogBuilder.setView(promptsView);

      final TextView barcodeVal = (TextView) promptsView
              .findViewById(R.id.textView_barcodeNumber);

      barcodeVal.setText("Barcode Value is: " + barcodeValue);
      // set dialog message
      alertDialogBuilder
              .setCancelable(false)
              .setPositiveButton("OK",
                      new DialogInterface.OnClickListener() {
                          public void onClick(DialogInterface dialog, int id) {

                              if (getIntent().getAction().equals("searchBarcodes")) {




                                  if (role.equals(Constants.ROLE_KITS_ASSESSOR)) {
                                      if (getIntent().getStringExtra("barcodeType").equals("id")) {
                                          Intent intent = new Intent(context, DetailedBeneficiaryActivity.class);
                                          thisActivity.finish();
                                          startActivity(intent);
                                      } else if (getIntent().getStringExtra("barcodeType").equals("voucher")) {
                                          Intent intent = new Intent(context, VoucherActivity.class);
                                          thisActivity.finish();
                                          startActivity(intent);
                                      }
                                  } else if (role.equals(Constants.ROLE_KITS_DISTRIBUTOR)) {
                                      Intent intent = new Intent(context, DistributorBeneficiaryActivity.class);
                                      thisActivity.finish();
                                      startActivity(intent);
                                  }
                              } else if (getIntent().getAction().equals("addBarcode")) {
                                  Intent intent = new Intent(context, AddSuccessActivity.class);
                                  PCodeObject pcode = getIntent().getParcelableExtra("PCODE");
                                  intent.putExtra("PCODE", pcode);
                                  thisActivity.finish();
                                  startActivity(intent);
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


}
