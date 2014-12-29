
package com.demo.simon;

import android.app.Application;

public class SimonApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ExpressManager.getInstance().initialize(getApplicationContext());
        StorageManager.initialize(getApplicationContext());
    }
}
