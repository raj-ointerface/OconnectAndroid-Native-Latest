package com.ointerface.oconnect;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.IBinder;
import android.support.multidex.MultiDexApplication;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.activities.SignInActivity2;
import com.ointerface.oconnect.data.MyNote;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.data.SinchMessage;
import com.ointerface.oconnect.messaging.SinchService;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmMigration;
import io.realm.RealmSchema;
import io.realm.internal.Table;

/**
 * Created by AnthonyDoan on 4/11/17.
 */

public class App extends MultiDexApplication implements ServiceConnection, MessageClientListener {
    private static App instance;

    public SinchService.SinchServiceInterface mSinchServiceInterface;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(getString(R.string.parse_app_id))
                .server(getString(R.string.parse_server_url))
                .clientKey(getString(R.string.parse_client_key))
                .build());

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);

        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

        if (AppUtil.getDefaultRealmLoaded(getApplicationContext()) == false) {
            Uri path = Uri.parse("file:///android_asset/default.realm");

            Realm.init(getApplicationContext());

            RealmMigration migration = new RealmMigration() {
                @Override
                public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

                    /*
                    realm.getSchema().create("SurveyQuestion");
                    realm.getSchema().get("SurveyQuestion").addField("objectId", String.class);
                    realm.getSchema().get("SurveyQuestion").addPrimaryKey("objectId").setRequired("objectId", true);
                    realm.getSchema().get("SurveyQuestion").addField("question", String.class);
                    realm.getSchema().get("SurveyQuestion").addField("order", Integer.class);
                    realm.getSchema().get("SurveyQuestion").addField("conference", String.class);
                    */


                    realm.getSchema().get("SinchMessage").addField("isRead", Boolean.class);
                }
            };

            RealmConfiguration config = new RealmConfiguration.Builder()
                    .name(Realm.DEFAULT_REALM_NAME)
                    .migration(migration)
                    .assetFile("default.realm")
                    .schemaVersion(0)
                    .build();

            AppUtil.realmConfiguration = config;

            Realm.setDefaultConfiguration(config);

            AppUtil.setDefaultRealmLoaded(getApplicationContext(), true);
        } else {
            Realm.init(getApplicationContext());

            AppUtil.realmConfiguration = new RealmConfiguration.Builder().build();

            Realm.setDefaultConfiguration(AppUtil.realmConfiguration);
        }



        /*
        Realm.init(getApplicationContext());

        AppUtil.realmConfiguration = new RealmConfiguration.Builder().build();

        Realm.setDefaultConfiguration(AppUtil.realmConfiguration);
        */

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                //.twitterAuthConfig(new TwitterAuthConfig(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret)))
                .twitterAuthConfig(new TwitterAuthConfig("0ZV9x1zOTs5nXcaFRS3eIengI", "zrsg7kCincIM0fqig4CJk0laliX5tUpsrSmgMyQdjqBcHhZtY4"))
                .debug(true)
                .build();
        Twitter.initialize(config);

        getApplicationContext().bindService(new Intent(this, SinchService.class), this,
                BIND_AUTO_CREATE);

        ParseFacebookUtils.initialize(this);

        ParseTwitterUtils.initialize("0ZV9x1zOTs5nXcaFRS3eIengI", "zrsg7kCincIM0fqig4CJk0laliX5tUpsrSmgMyQdjqBcHhZtY4");

        // generateHashkey();
    }

    public static void initSinchClient(String userID) {
        SinchClient sinchClient = Sinch.getSinchClientBuilder().context(instance.getApplicationContext())
                .applicationKey("57168fac-8abd-4317-820c-3e67ca5e225b")
                .applicationSecret("HLPZ7cYRuk2OfcV+yYar9g==")
                .environmentHost("sandbox.sinch.com")
                .userId(userID)
                .build();
    }

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;

            if (AppUtil.getIsSignedIn(getApplicationContext()) == true) {
                Realm realm = AppUtil.getRealmInstance(App.getInstance());
                OConnectBaseActivity.currentPerson = realm.where(Person.class).equalTo("objectId", AppUtil.getSignedInUserID(getApplicationContext())).findFirst();
            }

            if (OConnectBaseActivity.currentPerson != null) {
                if (!mSinchServiceInterface.isStarted()) {
                    mSinchServiceInterface.startClient(OConnectBaseActivity.currentPerson.getObjectId());
                }
            }

            mSinchServiceInterface.addMessageClientListener(this);
            onServiceConnected();

        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }

    @Override
    public void onIncomingMessage(MessageClient client, Message message) {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        realm.beginTransaction();

        SinchMessage sinchMessage = realm.createObject(SinchMessage.class);

        sinchMessage.setMessageString(message.getTextBody());

        sinchMessage.setIncoming(true);
        sinchMessage.setCurrentUserID(message.getRecipientIds().get(0));

        sinchMessage.setConnectedUserID(message.getSenderId());
        sinchMessage.setMessageDateTime(message.getTimestamp());

        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void onMessageSent(MessageClient client, Message message, String recipientId) {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        realm.beginTransaction();

        SinchMessage sinchMessage = realm.createObject(SinchMessage.class);

        sinchMessage.setMessageString(message.getTextBody());
        sinchMessage.setCurrentUserID(message.getSenderId());
        sinchMessage.setConnectedUserID(recipientId);
        sinchMessage.setIncoming(false);
        sinchMessage.setMessageDateTime(message.getTimestamp());

        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void onShouldSendPushData(MessageClient client, Message message, List<PushPair> pushPairs) {
        // Left blank intentionally
    }

    @Override
    public void onMessageFailed(MessageClient client, Message message,
                                MessageFailureInfo failureInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Sending failed: ")
                .append(failureInfo.getSinchError().getMessage());

        Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
        Log.d("OConnectBase", sb.toString());
    }

    @Override
    public void onMessageDelivered(MessageClient client, MessageDeliveryInfo deliveryInfo) {
        Log.d("OConnectBase", "onDelivered");
    }

    public void generateHashkey(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.ointerface.oconnect",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                Log.d("KeyHash:", "APD " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("APD", e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.d("APD", e.getMessage(), e);
        }
    }
}
