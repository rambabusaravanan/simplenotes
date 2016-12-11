package com.rambabusaravanan.simplenotes.application;

import android.app.Application;

import com.backendless.Backendless;
import com.rambabusaravanan.simplenotes.R;

/**
 * Created by androbabu on 10/12/16.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Backendless.initApp(getApplicationContext(), getString(R.string.backendless_appId), getString(R.string.backendless_androidKey), getString(R.string.backendless_appVersion));
    }
}
