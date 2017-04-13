package com.ointerface.oconnect;

import android.app.Application;

import com.parse.Parse;

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

        RealmConfiguration config = new RealmConfiguration.Builder(getApplicationContext()).build();

        Realm.setDefaultConfiguration(config);
    }

    public static App getInstance() {

        return instance;

    }
}
