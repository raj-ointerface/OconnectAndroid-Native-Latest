package com.ointerface.oconnect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.ointerface.oconnect.data.IDataSyncListener;
import com.ointerface.oconnect.data.Organization;
import com.ointerface.oconnect.util.AppConfig;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import io.realm.Realm;

public class CustomSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppConfig.isPrivateLabelApp == false) {
            // Goto Conference List View
            gotoConferenceListView();
            finish();
            return;
        }

        setContentView(R.layout.activity_custom_splash);

        Realm realm = AppUtil.getRealmInstance(App.getInstance());
        Organization result = realm.where(Organization.class).equalTo("objectId", AppConfig.primaryOrganizationID).findFirst();

        if (result != null) {
            if (result.getShowSplash() == true) {
                ImageView ivOrganizationLogo = (ImageView) findViewById(R.id.ivOrganizationLogo);

                Bitmap bm = BitmapFactory.decodeByteArray(result.getImage(), 0, result.getImage().length);

                ivOrganizationLogo.setImageBitmap(bm);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Goto ConferenceListView after 4 seconds
                        gotoConferenceListView();
                    }
                }, 4000);
            } else {
                gotoConferenceListView();
            }
        } else {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Organization");
            query.getInBackground(AppConfig.primaryOrganizationID, new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        if (object.getBoolean("showSplash")) {
                            ParseFile parseImage = (ParseFile) object.getParseFile("image");

                            ImageView ivOrganizationLogo = (ImageView) findViewById(R.id.ivOrganizationLogo);

                            AppUtil.loadImages(parseImage, ivOrganizationLogo);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    // Goto ConferenceListView after 4 seconds
                                    gotoConferenceListView();
                                }
                            }, 4000);
                        } else {
                            gotoConferenceListView();
                        }
                    } else {
                        // something went wrong
                        gotoConferenceListView();
                    }
                }
            });

        }
    }

    public void gotoConferenceListView() {
        Intent i = new Intent(CustomSplashActivity.this, ConferenceListViewActivity.class);
        startActivity(i);
    }

}
