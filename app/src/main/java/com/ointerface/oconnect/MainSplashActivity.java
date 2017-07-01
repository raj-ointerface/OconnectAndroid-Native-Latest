package com.ointerface.oconnect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ointerface.oconnect.data.DataSyncManager;
import com.ointerface.oconnect.data.IDataSyncListener;
import com.ointerface.oconnect.data.SpeakerEventCache;
import com.ointerface.oconnect.service.BackgroundService;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainSplashActivity extends AppCompatActivity implements IDataSyncListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_splash);


        DataSyncManager.dialog = ProgressDialog.show((Context)this, null, "Initializing Data ... Please wait.");

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DataSyncManager.shouldSyncAll = true;
                DataSyncManager.beginDataSync(getApplicationContext(), MainSplashActivity.this);
            }
        });


        // startService(new Intent(this, BackgroundService.class));

        /*
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Goto CustomSplashActivity after 4 seconds
                Intent i = new Intent(MainSplashActivity.this, CustomSplashActivity.class);
                startActivity(i);
            }
        }, 4000);
        */
    }

    public void onDataSyncFinish() {
        if (DataSyncManager.dialog.isShowing() == true) {
            DataSyncManager.dialog.hide();
        }

        Date dateTimeNow = Calendar.getInstance().getTime();
        DataSyncManager.setLastSyncDate(dateTimeNow);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Goto CustomSplashActivity after 4 seconds
                Intent i = new Intent(MainSplashActivity.this, CustomSplashActivity.class);
                startActivity(i);
            }
        }, 4000);
    }
}
