package me.young.android.gradle.dexfix;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import junit.framework.Test;

import me.young.android.gradle.dexfix.methodpool1.A;
import me.young.android.gradle.dexfix.methodpool1.B;
import me.young.android.gradle.dexfix.methodpool1.C;
import me.young.android.gradle.dexfix.methodpool1.D;
import me.young.android.gradle.dexfix.methodpool1.E;
import me.young.android.gradle.dexfix.methodpool1.F;

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
        MultiDex.install(base);
    }
}
