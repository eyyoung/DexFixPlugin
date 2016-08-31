package me.young.android.gradle.dexfix;

import android.app.Application;
import android.content.Context;

/**
 * Created by young on 2016/8/31.
 */
public class TestApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Test.test();
    }
}
