package com.unicefwinterizationplatform.winterization_android;

/**
 * Created by Tarek on 9/17/15.
 */
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

/**
 * This Activity is exactly the same as CaptureActivity, but has a different orientation
 * setting in AndroidManifest.xml.
 */
public class AnyOrientationCaptureActivity extends CaptureActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}
