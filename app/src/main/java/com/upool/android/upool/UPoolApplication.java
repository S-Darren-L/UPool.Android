package com.upool.android.upool;

import android.app.Activity;
import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Darren on 7/8/2017.
 */

public class UPoolApplication extends Application {

    public static UPoolApplication get(Activity activity) {
        return (UPoolApplication) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
