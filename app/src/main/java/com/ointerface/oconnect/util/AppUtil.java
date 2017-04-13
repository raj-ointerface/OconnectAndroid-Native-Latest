package com.ointerface.oconnect.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by AnthonyDoan on 4/11/17.
 */

public class AppUtil {
    static public void loadImages(ParseFile thumbnail, final ImageView img) {
        if (thumbnail != null) {
            thumbnail.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        img.setImageBitmap(bmp);
                    } else {
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
}
