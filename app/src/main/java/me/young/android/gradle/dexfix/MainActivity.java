package me.young.android.gradle.dexfix;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.young.android.gradle.dexfix.methodpool1.A;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        A.start(this);
    }
}
