package com.ointerface.oconnect.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeSet;

import io.realm.Realm;
import io.realm.RealmList;

import static android.view.View.GONE;

/**
 * Created by AnthonyDoan on 5/20/17.
 */

public class TalksListViewAdapter extends BaseAdapter {
    public Context context;

    private LayoutInflater mInflater;

    public ArrayList<Event> mData = new ArrayList<Event>();

    public TalksListViewAdapter(Context context, ArrayList<Event> eventsArg) {
        super();
        this.context = context;
        this.mData = eventsArg;

        mInflater = LayoutInflater.from(context);
    }

    public void addItem(final Event item) {
        mData.add(item);
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Event event = mData.get(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.speaker_detail_list_talks_item, null);
        }

        TextView tvEventName = (TextView) convertView.findViewById(R.id.tvEventName);

        TextView tvEventTime = (TextView) convertView.findViewById(R.id.tvEventTime);

        DateFormat dfTime = new SimpleDateFormat("h:mm a");
        DateFormat dfDate = new SimpleDateFormat("MMM d");

        dfTime.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
        dfDate.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));

        String startTime = dfTime.format(event.getStartTime());
        String endTime = dfTime.format(event.getEndTime());

        tvEventTime.setText(startTime + " - " + endTime + " on " + dfDate.format(event.getStartTime()));

        tvEventName.setText(event.getName());

        return convertView;
    }
}



