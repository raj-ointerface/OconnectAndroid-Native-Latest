package com.ointerface.oconnect.messaging;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.MapActivity;
import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.data.Organization;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.data.SinchMessage;
import com.ointerface.oconnect.util.AppUtil;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class MessagingListActivity extends BaseActivity implements MessageClientListener {

    private ListView lvMessages;
    private MessageListAdapter adapter;
    private ArrayList<String> existingConversations;

    public ImageView ivHeaderBack;
    public TextView tvHeaderBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (OConnectBaseActivity.selectedConference != null && OConnectBaseActivity.selectedConference.getColor() != null &&
                !OConnectBaseActivity.selectedConference.getColor().equalsIgnoreCase("")
                && !OConnectBaseActivity.selectedConference.getColor().equalsIgnoreCase("#")) {
            int color = Color.parseColor(OConnectBaseActivity.selectedConference.getColor());

            toolbar.getBackground().setAlpha(255);

            Drawable wrappedDrawable = DrawableCompat.wrap(toolbar.getBackground());
            DrawableCompat.setTint(wrappedDrawable, color);

            getSupportActionBar().setBackgroundDrawable(wrappedDrawable);
        }

        lvMessages = (ListView) findViewById(R.id.lstMessages);

        adapter = new MessageListAdapter(this);

        lvMessages.setAdapter(adapter);

        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<SinchMessage> results = realm.where(SinchMessage.class).equalTo("currentUserID", OConnectBaseActivity.currentPerson.getObjectId()).findAllSorted("messageDateTime", Sort.DESCENDING);

        existingConversations = new ArrayList<String>();

        for (int i = 0; i < results.size(); ++i) {
            SinchMessage message = results.get(i);

            if (!existingConversations.contains(message.getConnectedUserID())) {
                existingConversations.add(message.getConnectedUserID());
                adapter.addMessage(message);
            }
        }

        lvMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SinchMessage message = (SinchMessage) adapter.getItem(position);

                Realm realm = AppUtil.getRealmInstance(App.getInstance());

                Person person = realm.where(Person.class).equalTo("objectId", message.getConnectedUserID()).findFirst();

                if (person != null) {
                    MessagingActivity.recipientNameStr = person.getFirstName() + " " + person.getLastName();
                }

                MessagingActivity.recipientIDStr = message.getConnectedUserID();

                Intent intent = new Intent(MessagingListActivity.this, MessagingActivity.class);
                startActivity(intent);
            }
        });

        tvHeaderBack = (TextView) findViewById(R.id.tvHeaderBack);
        ivHeaderBack = (ImageView) findViewById(R.id.ivHeaderBack);

        ivHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessagingListActivity.this.finish();
            }
        });

        tvHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessagingListActivity.this.finish();
            }
        });

    }

    @Override
    public void onDestroy() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().removeMessageClientListener(this);
        }
        super.onDestroy();
    }

    @Override
    public void onServiceConnected() {
        getSinchServiceInterface().addMessageClientListener(this);
    }

    @Override
    public void onServiceDisconnected() {

    }

    @Override
    public void onIncomingMessage(MessageClient client, Message message) {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        realm.beginTransaction();

        SinchMessage sinchMessage = realm.createObject(SinchMessage.class);

        sinchMessage.setMessageString(message.getTextBody());

        sinchMessage.setCurrentUserID(message.getRecipientIds().get(0));

        sinchMessage.setIncoming(true);

        sinchMessage.setConnectedUserID(message.getSenderId());
        sinchMessage.setMessageDateTime(message.getTimestamp());

        realm.commitTransaction();
        realm.close();

        if (!existingConversations.contains(sinchMessage.getConnectedUserID())) {
            existingConversations.add(sinchMessage.getConnectedUserID());
            adapter.addMessage(sinchMessage);
        }
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

        if (!existingConversations.contains(sinchMessage.getConnectedUserID())) {
            adapter.addMessage(sinchMessage);
        }
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
        Log.d("APD", sb.toString());
    }

    @Override
    public void onMessageDelivered(MessageClient client, MessageDeliveryInfo deliveryInfo) {
        Log.d("APD", "onDelivered");
    }
}
