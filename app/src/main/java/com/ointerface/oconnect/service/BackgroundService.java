package com.ointerface.oconnect.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.ointerface.oconnect.MainSplashActivity;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.data.DataSyncManager;
import com.ointerface.oconnect.data.IDataSyncListener;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BackgroundService extends Service {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    static public boolean isDataSyncing = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        handler = new Handler();
        context = this;
        new DataSyncTask().execute();
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
        // Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        // Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
    }

    public class DataSyncTask extends AsyncTask<Void, Void, Integer>
    {
        @Override
        protected Integer doInBackground(Void... params)
        {
            try {
                runnable = new Runnable() {
                    public void run() {
                        if (BackgroundService.isDataSyncing == false &&
                                AppUtil.isNetworkAvailable(context) == true) {

                            BackgroundService.isDataSyncing = true;
                            DataSyncManager.shouldSyncAll = true;
                            DataSyncManager.beginDataSync(getApplicationContext(), new IDataSyncListener() {
                                @Override
                                public void onDataSyncFinish() {
                                    Log.d("DataSync", "Data Sync Finished");
                                    Date dateTimeNow = Calendar.getInstance().getTime();
                                    DataSyncManager.setLastSyncDate(dateTimeNow);
                                    BackgroundService.isDataSyncing = false;
                                }
                            });
                            Log.d("DataSync", "Service is still running");
                        }

                        // handler.postDelayed(runnable, 600000);

                        // final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
                        // executor.schedule(runnable, 8, TimeUnit.MINUTES);
                    }
                };

                final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
                executor.schedule(runnable, 2, TimeUnit.SECONDS);

                // executor.schedule(() -> captureCDRProcess(), 1, TimeUnit.MINUTES);

                // handler.postDelayed(runnable, 1000);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }
}