package com.ointerface.oconnect;

import android.app.Application;

import com.ointerface.oconnect.util.AppUtil;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by AnthonyDoan on 4/11/17.
 */

public class App extends Application {
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(getString(R.string.parse_app_id))
                .server(getString(R.string.parse_server_url))
                .clientKey(getString(R.string.parse_client_key))
                .build());

        /*
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        ParseACL.setDefaultACL(defaultACL, true);
        */

        AppUtil.realmConfiguration = new RealmConfiguration.Builder(getApplicationContext()).build();

        Realm.setDefaultConfiguration(AppUtil.realmConfiguration);
    }

    public static App getInstance() {

        return instance;

    }
}
