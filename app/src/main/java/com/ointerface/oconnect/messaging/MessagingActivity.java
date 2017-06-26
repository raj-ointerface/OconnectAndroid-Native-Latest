package com.ointerface.oconnect.messaging;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.data.SinchMessage;
import com.ointerface.oconnect.util.AppUtil;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.ointerface.oconnect.messaging.MessageAdapter.DIRECTION_INCOMING;
import static com.ointerface.oconnect.messaging.MessageAdapter.DIRECTION_OUTGOING;

public class MessagingActivity extends BaseActivity implements MessageClientListener {

    private static final String TAG = MessagingActivity.class.getSimpleName();

    private MessageAdapter mMessageAdapter;
    private EditText mTxtRecipient;
    private EditText mTxtTextBody;
    private Button mBtnSend;

    public ImageView ivHeaderBack;
    public TextView tvHeaderBack;

    private TextView tvToolbarTitle;

    public static String recipientIDStr = "";
    public static String recipientNameStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging);

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

        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);

        tvToolbarTitle.setText(recipientNameStr);

        mTxtRecipient = (EditText) findViewById(R.id.txtRecipient);
        mTxtTextBody = (EditText) findViewById(R.id.txtTextBody);

        mTxtRecipient.setText(recipientIDStr);

        mMessageAdapter = new MessageAdapter(this);
        ListView messagesList = (ListView) findViewById(R.id.lstMessages);
        messagesList.setAdapter(mMessageAdapter);

        mBtnSend = (Button) findViewById(R.id.btnSend);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<SinchMessage> results = realm.where(SinchMessage.class).equalTo("currentUserID", OConnectBaseActivity.currentPerson.getObjectId()).equalTo("connectedUserID", recipientIDStr).findAllSorted("messageDateTime", Sort.ASCENDING);

        // existingConversations = new ArrayList<String>();

        for (int i = 0; i < results.size(); ++i) {
            SinchMessage message = results.get(i);

            if (message.getIncoming() == true) {
                mMessageAdapter.addMessage(message, DIRECTION_INCOMING);
            } else {
                mMessageAdapter.addMessage(message, DIRECTION_OUTGOING);
            }
        }

        tvHeaderBack = (TextView) findViewById(R.id.tvHeaderBack);
        ivHeaderBack = (ImageView) findViewById(R.id.ivHeaderBack);

        ivHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessagingActivity.this.finish();
            }
        });

        tvHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessagingActivity.this.finish();
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
        setButtonEnabled(true);
    }

    @Override
    public void onServiceDisconnected() {
        setButtonEnabled(false);
    }

    private void sendMessage() {
        String recipient = recipientIDStr;
        String textBody = mTxtTextBody.getText().toString();
        if (recipient.isEmpty()) {
            Toast.makeText(this, "No recipient added", Toast.LENGTH_SHORT).show();
            return;
        }
        if (textBody.isEmpty()) {
            Toast.makeText(this, "No text message", Toast.LENGTH_SHORT).show();
            return;
        }

        getSinchServiceInterface().sendMessage(recipient, textBody);
        mTxtTextBody.setText("");
    }

    private void setButtonEnabled(boolean enabled) {
        mBtnSend.setEnabled(enabled);
    }

    @Override
    public void onIncomingMessage(MessageClient client, Message message) {
        /*
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
        */

        mMessageAdapter.addMessage(message, DIRECTION_INCOMING);
    }

    @Override
    public void onMessageSent(MessageClient client, Message message, String recipientId) {
        /*
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
        */

        mMessageAdapter.addMessage(message, MessageAdapter.DIRECTION_OUTGOING);
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
        Log.d(TAG, sb.toString());
    }

    @Override
    public void onMessageDelivered(MessageClient client, MessageDeliveryInfo deliveryInfo) {
        Log.d(TAG, "onDelivered");
    }
}
