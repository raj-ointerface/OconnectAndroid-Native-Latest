package com.ointerface.oconnect.push;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.CustomSplashActivity;
import com.ointerface.oconnect.MainSplashActivity;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.AnnouncementsActivity;
import com.ointerface.oconnect.activities.ConnectionsActivity;
import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.data.DataSyncManager;
import com.ointerface.oconnect.data.IDataSyncListener;
import com.ointerface.oconnect.data.MasterNotification;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.service.BackgroundService;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by AnthonyDoan on 7/14/17.
 */

public class MyParsePNReceiver extends ParsePushBroadcastReceiver implements IDataSyncListener {

    public static final String PARSE_DATA_KEY = "com.parse.Data";

    protected void onPushReceive(final Context mContext, Intent intent) {

        int badgeCount = getAlertCount(mContext);

        ++badgeCount;

        setBadge(mContext, badgeCount);

        /*
        for (String key : intent.getExtras().keySet()) {
            String tempStr = intent.getExtras().getString(key);
            tempStr = tempStr;
        }
        */

        String jsonStr = intent.getExtras().getString(PARSE_DATA_KEY);

        boolean isMessageFromCMSForDataUpdate = true;

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);


            if (jsonObj.has("class")) {
                String notificationType = jsonObj.getString("class");

                if (notificationType != null && notificationType.equalsIgnoreCase("Notification")) {
                    isMessageFromCMSForDataUpdate = false;
                } else {
                    isMessageFromCMSForDataUpdate = true;
                }
            }
        } catch (Exception ex) {
            Log.d("APD", ex.getMessage());
        }
        if (isAppRunning(mContext, "com.ointerface.oconnect") == true) {
            try {

                if (isMessageFromCMSForDataUpdate == false) {
                    ParseObject notification = ParseQuery.getQuery("MasterNotification").addDescendingOrder("createdAt").getFirst();

                    String message = notification.getString("alert");

                    JSONObject data = new JSONObject(intent.getExtras().getString(PARSE_DATA_KEY));
                    AlertDialog dialog = new AlertDialog.Builder(App.getInstance().activity)
                            .setMessage(message)
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();

                    dialog.show();
                } else {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            DataSyncManager.shouldSyncAll = true;
                            DataSyncManager.beginDataSync(mContext, MyParsePNReceiver.this);
                        }
                    });
                }
            } catch (Exception ex) {
                Log.d("APD", ex.getMessage());
            }
        } else {
            try {

                ParseObject notification = ParseQuery.getQuery("MasterNotification").addDescendingOrder("createdAt").getFirst();

                String message = notification.getString("alert");

                Intent notificationIntent = new Intent(mContext, MainSplashActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(mContext,
                        0, notificationIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

                NotificationManager notificationManager =
                        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
                builder.setContentIntent(contentIntent);
                builder.setContentTitle("OConnect");
                builder.setContentText(message);
                builder.setSmallIcon(R.drawable.oconnect_logo);
                builder.setAutoCancel(true);

                // notificationManager.notify("MyTag", 0, builder.build());

                Notification systemNotification = builder.build();

                if (systemNotification != null) {
                    notificationManager.notify(null, 10, systemNotification);
                }
            } catch (Exception ex) {
                Log.d("APD", ex.getMessage());
            }
        }
    }

    public static void setBadge(Context context, int count) {
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }

    public static String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getAlertCount(Context context) {
        if (AppUtil.getSignedInUserID(context).equalsIgnoreCase("")) {
            return 0;
        }

        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<MasterNotification> alertResults;

        alertResults  = realm.where(MasterNotification.class).equalTo("conference", AppUtil.getSelectedConferenceID(context)).findAllSorted("createdAt", Sort.DESCENDING);

        RealmList<MasterNotification> tempResults = new RealmList<MasterNotification>();

        tempResults.addAll(alertResults);

        Person person = realm.where(Person.class).equalTo("objectId", AppUtil.getSignedInUserID(context)).findFirst();

        OConnectBaseActivity.currentPerson = person;

        if (OConnectBaseActivity.currentPerson != null) {
            for (int i = tempResults.size() - 1; i >= 0; --i) {
                MasterNotification alert = tempResults.get(i);

                RealmList<MasterNotification> deletedAlerts = OConnectBaseActivity.currentPerson.getDeletedNotificationIds();

                for (int j = deletedAlerts.size() - 1; j >= 0; --j) {
                    MasterNotification thisAlert = deletedAlerts.get(j);
                    if (alert.getObjectId().contentEquals(thisAlert.getObjectId())) {
                        realm.beginTransaction();
                        alertResults.remove(i);
                        realm.commitTransaction();
                    }
                }
            }
        }

        return alertResults.size();
    }

    public void onDataSyncFinish() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
        Date dateTimeNow = cal.getTime();

        DataSyncManager.setLastSyncDate(dateTimeNow);
    }
}
