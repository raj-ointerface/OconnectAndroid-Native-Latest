package com.ointerface.oconnect;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.data.MyNote;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.data.SinchMessage;
import com.ointerface.oconnect.messaging.SinchService;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
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
                    // realm.getSchema().get("Attendee").setRequired("objectId", true);
                    // realm.getSchema().get("Conference").setRequired("objectId", true);
                    // realm.getSchema().get("Event").setRequired("objectId", true);
                    // realm.getSchema().get("Maps").setRequired("objectId", true);
                    // realm.getSchema().get("MasterNotification").setRequired("objectId", true);
                    // realm.getSchema().get("MyNote").setRequired("objectId", true);
                    // realm.getSchema().get("Organization").setRequired("objectId", true);
                    // realm.getSchema().get("Person").setRequired("objectId", true);
                    // realm.getSchema().get("Person").addRealmListField("favoriteUsers", realm.getSchema().get("Person"));
                    // realm.getSchema().get("Person").addRealmListField("favoriteSpeakers", realm.getSchema().get("Speaker"));
                    // realm.getSchema().get("Person").addRealmListField("favoriteAttendees", realm.getSchema().get("Attendee"));
                    // realm.getSchema().get("Person").addRealmListField("suggestedConnections", realm.getSchema().get("Person"));
                    // realm.getSchema().get("Person").removeField("locaton");
                    // realm.getSchema().get("Person").addField("location", String.class);
                    // realm.getSchema().create("PredAnalyticsMatches");
                    // realm.getSchema().get("PredAnalyticsMatches").addField("objectId", String.class);
                    // realm.getSchema().get("PredAnalyticsMatches").addPrimaryKey("objectId").setRequired("objectId", true);
                    // realm.getSchema().get("PredAnalyticsMatches").addField("scoreConferences", double.class);
                    // realm.getSchema().get("PredAnalyticsMatches").addField("scoreLocation", double.class);
                    // realm.getSchema().get("PredAnalyticsMatches").addField("isRejected", boolean.class);
                    // realm.getSchema().get("PredAnalyticsMatches").addField("scoreSurveyAnswers", double.class);
                    // realm.getSchema().get("PredAnalyticsMatches").addField("isDeleted", boolean.class);
                    // realm.getSchema().get("PredAnalyticsMatches").addField("isAccepted", boolean.class);
                    // realm.getSchema().get("PredAnalyticsMatches").addField("score", double.class);
                    // realm.getSchema().get("PredAnalyticsMatches").addField("scoreBio", double.class);
                    // realm.getSchema().get("PredAnalyticsMatches").addField("id1", String.class);
                    // realm.getSchema().get("PredAnalyticsMatches").addField("id2", String.class);
                    // realm.getSchema().get("PredAnalyticsMatches").addField("scoreInterests", double.class);

                    // realm.getSchema().get("Event").addField("info", String.class);
                    // realm.getSchema().get("Speaker").addField("speakerLabel", String.class);

                    // realm.getSchema().create("EventLink");
                    // realm.getSchema().get("EventLink").addField("objectId", String.class);
                    // realm.getSchema().get("EventLink").addPrimaryKey("objectId").setRequired("objectId", true);
                    // realm.getSchema().get("EventLink").addField("eventID", String.class);
                    // realm.getSchema().get("EventLink").addField("label", String.class);
                    // realm.getSchema().get("EventLink").addField("link", String.class);

                    // realm.getSchema().get("Session").setRequired("objectId", true);
                    // realm.getSchema().get("Speaker").setRequired("objectId", true);
                    // realm.getSchema().get("SpeakerEventCache").setRequired("objectId", true);
                    // realm.getSchema().get("Sponsor").setRequired("objectId", true);

                    realm.getSchema().get("Speaker").removeField("eventsList");
                    realm.getSchema().get("Attendee").removeField("eventsList");
                    realm.getSchema().get("Speaker").removeField("updatedAt");
                    realm.getSchema().get("Attendee").removeField("updatedAt");

                    realm.getSchema().create("TravelBusiness");
                    realm.getSchema().get("TravelBusiness").addField("objectId", String.class);
                    realm.getSchema().get("TravelBusiness").addPrimaryKey("objectId").setRequired("objectId", true);
                    realm.getSchema().get("TravelBusiness").addField("businessName", String.class);
                    realm.getSchema().get("TravelBusiness").addField("otherDetails", String.class);
                    realm.getSchema().get("TravelBusiness").addField("rates", String.class);
                    realm.getSchema().get("TravelBusiness").addField("address", String.class);
                    realm.getSchema().get("TravelBusiness").addField("businessType", String.class);
                    realm.getSchema().get("TravelBusiness").addField("website", String.class);
                    realm.getSchema().get("TravelBusiness").addField("conference", String.class);
                    realm.getSchema().get("TravelBusiness").addField("key", String.class);

                    realm.getSchema().create("SurveyQuestion");
                    realm.getSchema().get("SurveyQuestion").addField("objectId", String.class);
                    realm.getSchema().get("SurveyQuestion").addPrimaryKey("objectId").setRequired("objectId", true);
                    realm.getSchema().get("SurveyQuestion").addField("question", String.class);
                    realm.getSchema().get("SurveyQuestion").addField("order", Integer.class);
                    realm.getSchema().get("SurveyQuestion").addField("conference", String.class);

                    realm.getSchema().create("SurveyQuestionAnswer");
                    realm.getSchema().get("SurveyQuestionAnswer").addField("objectId", String.class);
                    realm.getSchema().get("SurveyQuestionAnswer").addPrimaryKey("objectId").setRequired("objectId", true);
                    realm.getSchema().get("SurveyQuestionAnswer").addField("title", String.class);

                    realm.getSchema().create("SurveyQuestionAnswerRelation");
                    realm.getSchema().get("SurveyQuestionAnswerRelation").addField("objectId", String.class);
                    realm.getSchema().get("SurveyQuestionAnswerRelation").addPrimaryKey("objectId").setRequired("objectId", true);
                    realm.getSchema().get("SurveyQuestionAnswerRelation").addField("surveyQuestion", String.class);
                    realm.getSchema().get("SurveyQuestionAnswerRelation").addField("surveyQuestionAnswer", String.class);

                    realm.getSchema().create("UserSurveyAnswer");
                    realm.getSchema().get("UserSurveyAnswer").addField("objectId", String.class);
                    realm.getSchema().get("UserSurveyAnswer").addPrimaryKey("objectId").setRequired("objectId", true);
                    realm.getSchema().get("UserSurveyAnswer").addField("questionId", String.class);
                    realm.getSchema().get("UserSurveyAnswer").addField("questionAnswerIds", String.class);
                    realm.getSchema().get("UserSurveyAnswer").addField("userId", String.class);

                    realm.getSchema().get("Attendee").addField("isCheckedIn", Boolean.class);
                    realm.getSchema().get("Attendee").addField("email", String.class);

                    realm.getSchema().create("SinchMessage");
                    realm.getSchema().get("SinchMessage").addField("messageString", String.class);
                    realm.getSchema().get("SinchMessage").addField("messageDateTime", Date.class);
                    realm.getSchema().get("SinchMessage").addField("currentUserID", String.class);
                    realm.getSchema().get("SinchMessage").addField("connectedUserID", String.class);
                    realm.getSchema().get("SinchMessage").addField("isIncoming", Boolean.class);

                    realm.getSchema().get("Speaker").addField("email", String.class);

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
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret)))
                .debug(true)
                .build();
        Twitter.initialize(config);

        getApplicationContext().bindService(new Intent(this, SinchService.class), this,
                BIND_AUTO_CREATE);

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
}
