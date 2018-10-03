package com.hl3hl3.siaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


/**
 * Created by harjot on 26/9/18.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    public void callAR(View view) {
        Intent intent=new Intent(MainActivity.this,ArMeasureActivity.class);
        startActivity(intent);
    }

    public void callRecom(View view) {
        Intent intent = new Intent(MainActivity.this, RecomActivity.class);
        startActivity(intent);
    }

    public void callAI(View view) {
        Intent intent=new Intent(MainActivity.this,AIActivity.class);
        startActivity(intent);
    }

    public void callTools(View view){
        Intent intent=new Intent(MainActivity.this,ToolsActivity.class);
        startActivity(intent);
    }

}
