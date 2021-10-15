package com.unicefwinterizationplatform.winterization_android;

import com.couchbase.lite.QueryEnumerator;

/**
 * Created by Tarek on 12/9/2014.
 */
public interface AsyncGetHouseholdList {

        void getHouseholdList(QueryEnumerator enumerator);
}
