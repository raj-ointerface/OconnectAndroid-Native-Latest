package com.ointerface.oconnect.messaging;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.sinch.android.rtc.NotificationResult;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchHelpers;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;

public class GcmIntentService extends IntentService implements ServiceConnection {

    private Intent mIntent;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (SinchHelpers.isSinchPushIntent(intent)) {
            mIntent = intent;
            connectToService();
        } else {
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void connectToService() {
        getApplicationContext().bindService(new Intent(this, SinchService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (mIntent == null) {
            return;
        }

        if (SinchHelpers.isSinchPushIntent(mIntent)) {
            SinchService.SinchServiceInterface sinchService = (SinchService.SinchServiceInterface) iBinder;

            Log.d("APD", "APD - Push Notification Received.");

            if (sinchService != null && SinchService.staticSinchClient != null) {
                NotificationResult result = SinchService.staticSinchClient.relayRemotePushNotificationPayload(mIntent);
                // handle result, e.g. show a notification or similar

                try {
                    Log.d("APD", "APD Start Display of PN");
                    NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                    PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                            new Intent(this, MessagingListActivity.class), 0);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.oconnect_logo)
                                    .setContentTitle("OConnect")
                                    .setContentText("You have a new message from the OConnect App")
                                    .setShowWhen(true)
                                    .setPriority(PRIORITY_MAX)
                                    .setContentIntent(contentIntent);

                    mNotificationManager.notify(0, mBuilder.build());
                } catch (Exception ex) {
                    Log.d("APD", "APD PN Exception: " + ex.getMessage());
                }
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(mIntent);
        mIntent = null;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }

}