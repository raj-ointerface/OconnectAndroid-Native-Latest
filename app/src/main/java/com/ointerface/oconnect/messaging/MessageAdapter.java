package com.ointerface.oconnect.messaging;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.InfoActivity;
import com.ointerface.oconnect.data.SinchMessage;
import com.ointerface.oconnect.util.AppConfig;
import com.ointerface.oconnect.util.AppUtil;
import com.sinch.android.rtc.messaging.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {

    public static final int DIRECTION_INCOMING = 0;

    public static final int DIRECTION_OUTGOING = 1;

    private List<Pair<Object, Integer>> mMessages;

    private SimpleDateFormat mFormatter;

    private LayoutInflater mInflater;

    private Activity activity;

    public MessageAdapter(Activity activityArg) {
        activity = activityArg;
        mInflater = activity.getLayoutInflater();
        mMessages = new ArrayList<Pair<Object, Integer>>();
        mFormatter = new SimpleDateFormat("EEE, HH:mm");
    }

    public void addMessage(Object message, int direction) {
        mMessages.add(new Pair(message, direction));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Object getItem(int i) {
        return mMessages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int i) {
        return mMessages.get(i).second;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        int direction = getItemViewType(i);

        if (convertView == null) {
            int res = 0;
            if (direction == DIRECTION_INCOMING) {
                res = R.layout.message_right;
            } else if (direction == DIRECTION_OUTGOING) {
                res = R.layout.message_left;
            }
            convertView = mInflater.inflate(res, viewGroup, false);
        }

        Object message = mMessages.get(i).first;
        // String name = message.getSenderId();

        if (direction == DIRECTION_INCOMING) {
            RelativeLayout rlMessageBackground = (RelativeLayout) convertView.findViewById(R.id.rlMessageBackground);

            rlMessageBackground.setBackground(AppUtil.tintDrawable(activity.getResources().getDrawable(R.drawable.purple_bubble), ColorStateList.valueOf(AppConfig.messagingBackgroundColor)));
        }

        // TextView txtSender = (TextView) convertView.findViewById(R.id.txtSender);
        TextView txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
        TextView txtDate = (TextView) convertView.findViewById(R.id.txtDate);

        // txtSender.setText(name);
        if (message instanceof Message) {
            txtMessage.setText(((Message) message).getTextBody());
            txtDate.setText(mFormatter.format(((Message) message).getTimestamp()));
        } else if (message instanceof SinchMessage) {
            txtMessage.setText(((SinchMessage) message).getMessageString());
            txtDate.setText(mFormatter.format(((SinchMessage) message).getMessageDateTime()));
        }
        return convertView;
    }
}
