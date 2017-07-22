package com.ointerface.oconnect.push;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.MainSplashActivity;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.AnnouncementsActivity;
import com.ointerface.oconnect.activities.ConnectionsActivity;
import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.data.MasterNotification;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by AnthonyDoan on 7/14/17.
 */

public class MyParsePNReceiver extends ParsePushBroadcastReceiver {

    public static final String PARSE_DATA_KEY = "com.parse.Data";

    protected void onPushReceive(Context mContext, Intent intent) {

        int badgeCount = getAlertCount(mContext);

        ++badgeCount;

        setBadge(mContext, badgeCount);

        if (isAppRunning(mContext, "com.ointerface.oconnect") == true) {
            try {
                JSONObject data = new JSONObject(intent.getExtras().getString(PARSE_DATA_KEY));
                AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setMessage(data.toString())
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();

                dialog.show();
            } catch (Exception ex) {
                Log.d("APD", ex.getMessage());
            }
        } else {
            try {
                JSONObject data = new JSONObject(intent.getExtras().getString(PARSE_DATA_KEY));

                Intent notificationIntent = new Intent(mContext, MainSplashActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(mContext,
                        0, notificationIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

                NotificationManager notificationManager =
                        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
                builder.setContentIntent(contentIntent);
                builder.setContentTitle("OConnect");
                builder.setContentText(data.toString());
                builder.setSmallIcon(R.drawable.oconnect_logo);
                builder.setAutoCancel(true);

                notificationManager.notify("MyTag", 0, builder.build());
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
}
