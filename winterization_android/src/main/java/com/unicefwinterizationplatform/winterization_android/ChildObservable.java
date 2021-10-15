package com.unicefwinterizationplatform.winterization_android;

import java.util.Observable;

/**
 * Created by Tarek on 9/23/2014.
 */
public class ChildObservable extends Observable {

    public void notifyObserversTest() {
        setChanged();
        notifyObservers();
    }

}
