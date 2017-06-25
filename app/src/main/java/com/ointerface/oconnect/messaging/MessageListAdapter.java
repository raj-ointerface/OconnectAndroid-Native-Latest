package com.ointerface.oconnect.messaging;

import android.app.Activity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ointerface.oconnect.R;
import com.ointerface.oconnect.data.SinchMessage;
import com.sinch.android.rtc.messaging.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AnthonyDoan on 6/24/17.
 */

public class MessageListAdapter extends BaseAdapter {

    private List<SinchMessage> mMessages;

    private SimpleDateFormat mFormatter;

    private LayoutInflater mInflater;

    public MessageListAdapter(Activity activity) {
        mInflater = activity.getLayoutInflater();
        mMessages = new ArrayList<SinchMessage>();
        mFormatter = new SimpleDateFormat("HH:mm");
    }

    public void addMessage(SinchMessage message) {
        mMessages.add(message);
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
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        int direction = getItemViewType(i);

        if (convertView == null) {
            int res = 0;
            res = R.layout.message_list_item;
            convertView = mInflater.inflate(res, viewGroup, false);
        }

        SinchMessage message = mMessages.get(i);

        TextView txtSender = (TextView) convertView.findViewById(R.id.txtSender);
        TextView txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
        TextView txtDate = (TextView) convertView.findViewById(R.id.txtDate);

        txtSender.setText(message.getConnectedUserID());
        txtMessage.setText(message.getMessageString());
        txtDate.setText(mFormatter.format(message.getMessageDateTime()));

        return convertView;
    }
}
