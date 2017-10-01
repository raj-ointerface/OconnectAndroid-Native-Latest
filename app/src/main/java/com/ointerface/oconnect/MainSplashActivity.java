package com.ointerface.oconnect;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.ointerface.oconnect.data.AppConfig;
import com.ointerface.oconnect.data.DataSyncManager;
import com.ointerface.oconnect.data.IDataSyncListener;
import com.ointerface.oconnect.push.MyParsePNReceiver;
import com.ointerface.oconnect.service.BackgroundService;
import com.ointerface.oconnect.util.AppUtil;

import java.util.Calendar;
import java.util.Date;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;

public class MainSplashActivity extends AppCompatActivity implements IDataSyncListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main_splash);

        MyParsePNReceiver.setBadge(MainSplashActivity.this, MyParsePNReceiver.getAlertCount(MainSplashActivity.this));

        // DataSyncManager.dialog = ProgressDialog.show((Context)this, null, "Initializing Data ... Please wait.");

        Realm realm = AppUtil.getRealmInstance(this);

        AppConfig config = realm.where(AppConfig.class).findFirst();

        /*
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                DataSyncManager.shouldSyncAll = true;
                DataSyncManager.beginDataSync(getApplicationContext(), MainSplashActivity.this);

                // startService(new Intent(MainSplashActivity.this, BackgroundService.class));
            }
        });
        */

        startService(new Intent(MainSplashActivity.this, BackgroundService.class));

        /*
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    DataSyncManager.shouldSyncAll = true;
                    DataSyncManager.beginDataSync(getApplicationContext(), MainSplashActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        */

        if (config != null && config.getShowMainSplash() == false) {
            Intent i = new Intent(MainSplashActivity.this, CustomSplashActivity.class);
            startActivity(i);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Goto CustomSplashActivity after 4 seconds
                    Intent i = new Intent(MainSplashActivity.this, CustomSplashActivity.class);
                    startActivity(i);
                }
            }, 4000);
        }

        // startService(new Intent(MainSplashActivity.this, BackgroundService.class));
    }

    public void onDataSyncFinish() {
        if (DataSyncManager.dialog != null && DataSyncManager.dialog.isShowing() == true) {
            DataSyncManager.dialog.hide();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
        Date dateTimeNow = cal.getTime();

        DataSyncManager.setLastSyncDate(dateTimeNow);

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
}
