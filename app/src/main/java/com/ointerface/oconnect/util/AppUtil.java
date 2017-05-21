package com.ointerface.oconnect.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.icu.text.IDNA;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.ConferenceListViewActivity;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.DashboardActivity;
import com.ointerface.oconnect.activities.InfoActivity;
import com.ointerface.oconnect.activities.MapsListActivity;
import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.activities.ParticipantsActivity;
import com.ointerface.oconnect.activities.SignInActivity1;
import com.ointerface.oconnect.activities.SurveyActivity;
import com.ointerface.oconnect.data.Conference;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by AnthonyDoan on 4/11/17.
 */

public class AppUtil {
    static public RealmConfiguration realmConfiguration;

    static public void loadImages(ParseFile thumbnail, final ImageView img) {
        if (thumbnail != null) {
            thumbnail.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        img.setImageBitmap(bmp);
                    }
                }
            });
        }
    }

    static public String getSelectedConferenceID (Context context) {
        SharedPreferences prefs = context.getSharedPreferences(AppConfig.sharedPrefsName, MODE_PRIVATE);
        String conferenceID = prefs.getString(AppConfig.sharedPrefsConferenceID, "");
        return conferenceID;
    }

    static public void setSelectedConferenceID (Context context, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppConfig.sharedPrefsName, MODE_PRIVATE).edit();
        editor.putString(AppConfig.sharedPrefsConferenceID, value);
        editor.commit();
    }

    static public boolean getIsSignedIn (Context context) {
        SharedPreferences prefs = context.getSharedPreferences(AppConfig.sharedPrefsName, MODE_PRIVATE);
        boolean isSignedIn = prefs.getBoolean(AppConfig.sharedPrefsIsSignedIn, false);
        return isSignedIn;
    }

    static public void setIsSignedIn (Context context, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppConfig.sharedPrefsName, MODE_PRIVATE).edit();
        editor.putBoolean(AppConfig.sharedPrefsIsSignedIn, value);
        editor.commit();
    }

    static public boolean getIsLeftNavUnlocked (Context context) {
        SharedPreferences prefs = context.getSharedPreferences(AppConfig.sharedPrefsName, MODE_PRIVATE);
        boolean isUnlocked = prefs.getBoolean(AppConfig.getSharedPrefsNavItemPasswordEntered, false);
        return isUnlocked;
    }

    static public void setIsLeftNavUnlocked (Context context, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppConfig.getSharedPrefsNavItemPasswordEntered, MODE_PRIVATE).edit();
        editor.putBoolean(AppConfig.getSharedPrefsNavItemPasswordEntered, value);
        editor.commit();
    }

    static public String getSignedInUserID (Context context) {
        SharedPreferences prefs = context.getSharedPreferences(AppConfig.sharedPrefsName, MODE_PRIVATE);
        String userID = prefs.getString(AppConfig.sharedPrefsSignedInUserID, "");
        return userID;
    }

    static public void setSignedInUserID (Context context, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppConfig.sharedPrefsName, MODE_PRIVATE).edit();
        editor.putString(AppConfig.sharedPrefsSignedInUserID, value);
        editor.commit();
    }

    static public String getServerBaseUrlString () {
        // Configure to Production or Staging
        // return "http://www.oconnectapp.com/api/parse";
        return "http://stage.oconnectapp.com/api/parse";
    }

    static public int convertDPToPXInt(Context context, int currentDPValue) {
        Double doubleObj = new Double(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, currentDPValue, context.getResources().getDisplayMetrics()));
        return doubleObj.intValue();
    }

    static public Realm getRealmInstance(Context context) {
        // RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(context).build();

        try {
            return Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException e){
            try {
                Realm.deleteRealm(realmConfiguration);
                //Realm file has been deleted.
                return Realm.getDefaultInstance();
            } catch (Exception ex){
                throw ex;
                //No Realm file to remove.
            }
        }
    }

    static public Date getLocalDateTimeFromUTC (Date dateUTC) {
        SimpleDateFormat simpleDateFormatUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date localDateTime = null;
        try {
            localDateTime = simpleDateFormat.parse(simpleDateFormatUTC.format(dateUTC));
        } catch (Exception ex) {
            Log.d("AppUtil", ex.getMessage());
        }
        return localDateTime;
    }

    public static Date setTime( final Date date, final int hourOfDay, final int minute, final int second, final int ms )
    {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime( date );
        gc.set( Calendar.HOUR_OF_DAY, hourOfDay );
        gc.set( Calendar.MINUTE, minute );
        gc.set( Calendar.SECOND, second );
        gc.set( Calendar.MILLISECOND, ms );
        return gc.getTime();
    }

    public static boolean hasPinPromptEnteredForConference (Context context, String conferenceID) {
        SharedPreferences prefs = context.getSharedPreferences(AppConfig.sharedPrefsName, MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(AppConfig.getSharedPrefsPinPromptConferenceList, new HashSet<String>());

        return set.contains(conferenceID);
    }

    public static void addConferenceForPinPromptEntered (Context context, String conferenceID) {
        SharedPreferences prefs = context.getSharedPreferences(AppConfig.sharedPrefsName, MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(AppConfig.getSharedPrefsPinPromptConferenceList, new HashSet<String>());

        if (!set.contains(conferenceID)) {
            set.add(conferenceID);
        }

        SharedPreferences.Editor editor = context.getSharedPreferences(AppConfig.sharedPrefsName, MODE_PRIVATE).edit();
        editor.putStringSet(AppConfig.getSharedPrefsPinPromptConferenceList, set);
        editor.commit();
    }

    public static boolean hasPinPromptSkippedForConference (Context context, String conferenceID) {
        SharedPreferences prefs = context.getSharedPreferences(AppConfig.sharedPrefsName, MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(AppConfig.getSharedPrefsPinPromptSkippedConferenceList, new HashSet<String>());

        return set.contains(conferenceID);
    }

    public static void addConferenceForPinPromptSkipped (Context context, String conferenceID) {
        SharedPreferences prefs = context.getSharedPreferences(AppConfig.sharedPrefsName, MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(AppConfig.getSharedPrefsPinPromptSkippedConferenceList, new HashSet<String>());

        if (!set.contains(conferenceID)) {
            set.add(conferenceID);
        }

        SharedPreferences.Editor editor = context.getSharedPreferences(AppConfig.sharedPrefsName, MODE_PRIVATE).edit();
        editor.putStringSet(AppConfig.getSharedPrefsPinPromptSkippedConferenceList, set);
        editor.commit();
    }

    public static void displayPleaseSignInDialog(Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Message");
        alertDialog.setMessage("Please sign in to use this feature");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static int getPrimaryThemColorAsInt() {
        if (OConnectBaseActivity.selectedConference != null &&
                OConnectBaseActivity.selectedConference.getColor() != null &&
                !OConnectBaseActivity.selectedConference.getColor().equalsIgnoreCase("")
                && !OConnectBaseActivity.selectedConference.getColor().equalsIgnoreCase("#")) {
            return Color.parseColor(OConnectBaseActivity.selectedConference.getColor());
        } else {
            return AppConfig.defaultThemeColor;
        }
    }

    public static void displayNavPassword(final Context context, final int menuItemResID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Password");

        final EditText input = new EditText(context);

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (input.getText().toString().contentEquals(OConnectBaseActivity.selectedConference.getInappPassword())) {
                    AppUtil.setIsLeftNavUnlocked(context, true);

                    if (menuItemResID == R.drawable.icon_maps) {
                        Intent i = new Intent(context, MapsListActivity.class);
                        context.startActivity(i);
                    } else if (menuItemResID == R.drawable.ic_person) {
                        Intent i = new Intent(context, ParticipantsActivity.class);
                        context.startActivity(i);
                    } else if (menuItemResID == R.drawable.icon_survey) {
                        Intent i = new Intent(context, SurveyActivity.class);
                        context.startActivity(i);
                    } else if (menuItemResID == R.drawable.icon_info) {
                        Intent i = new Intent(context, InfoActivity.class);
                        context.startActivity(i);
                    }
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Password is incorrect.  Please try again.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();
    }

    public static Drawable changeDrawableColor(Context context,int icon, int newColor) {
        Drawable mDrawable = ContextCompat.getDrawable(context, icon).mutate();
        mDrawable.setColorFilter(new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN));
        return mDrawable;
    }

    public static void displayNotImplementedDialog(Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Message");
        alertDialog.setMessage("This feature is not implemented yet.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    static public boolean getDefaultRealmLoaded (Context context) {
        SharedPreferences prefs = context.getSharedPreferences(AppConfig.sharedPrefsName, MODE_PRIVATE);
        boolean value = prefs.getBoolean(AppConfig.getSharedPrefsDefaultRealmLoaded, false);
        return value;
    }

    static public void setDefaultRealmLoaded (Context context, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppConfig.sharedPrefsName, MODE_PRIVATE).edit();
        editor.putBoolean(AppConfig.getSharedPrefsDefaultRealmLoaded, value);
        editor.commit();
    }
}
