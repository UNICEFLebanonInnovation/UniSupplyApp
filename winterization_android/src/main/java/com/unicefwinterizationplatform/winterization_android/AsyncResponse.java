package com.unicefwinterizationplatform.winterization_android;

import com.couchbase.lite.QueryRow;

/**
 * Created by Tarek on 12/7/2014.
 */
public interface AsyncResponse {
        void processFinish(QueryRow output, String barcodeVal);
        void barcodeRangeCheck(boolean isBarcodeInRange, String barcodeVal);
        void checkIfBeneficiaryExists(BeneficiaryObject beneficiaryObject, String barcodeVal);
}
