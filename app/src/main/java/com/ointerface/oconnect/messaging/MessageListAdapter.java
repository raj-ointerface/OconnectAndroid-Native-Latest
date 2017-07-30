package com.ointerface.oconnect.messaging;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.data.SinchMessage;
import com.ointerface.oconnect.util.AppUtil;
import com.sinch.android.rtc.messaging.Message;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

/**
 * Created by AnthonyDoan on 6/24/17.
 */

public class MessageListAdapter extends BaseAdapter {

    private List<SinchMessage> mMessages;

    private SimpleDateFormat mFormatter;

    private LayoutInflater mInflater;

    private Activity mActivity;

    public MessageListAdapter(Activity activity) {
        mActivity = activity;
        mInflater = activity.getLayoutInflater();
        mMessages = new ArrayList<SinchMessage>();
        mFormatter = new SimpleDateFormat("EEE, HH:mm");
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

        Realm realm = AppUtil.getRealmInstance(App.getInstance());
        Person recipientPerson = realm.where(Person.class).equalTo("objectId", message.getConnectedUserID()).findFirst();

        if (recipientPerson != null) {
            txtSender.setText(recipientPerson.getFirstName() + " " + recipientPerson.getLastName());

            final CircleImageView ivProfileImage = (CircleImageView) convertView.findViewById(R.id.ivParticipantPicture);

            final String pictureURL = recipientPerson.getPictureURL();

            if (pictureURL != null && !pictureURL.equalsIgnoreCase("")) {
                new AsyncTask<Void, Void, Void>() {
                    public Bitmap bmp;

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            InputStream in = new URL(pictureURL).openStream();
                            bmp = BitmapFactory.decodeStream(in);
                        } catch (Exception e) {
                            Log.d("APD", e.getMessage());
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        if (bmp != null) {
                            ivProfileImage.setImageBitmap(bmp);
                        }
                    }

                }.execute();
            }
        } else {
            txtSender.setText("");
        }

        txtMessage.setText(message.getMessageString());
        txtDate.setText(mFormatter.format(message.getMessageDateTime()));

        return convertView;
    }
}
