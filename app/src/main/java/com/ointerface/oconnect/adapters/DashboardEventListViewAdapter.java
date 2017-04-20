package com.ointerface.oconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ointerface.oconnect.ConferenceListViewAdapter;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.containers.MenuItemHolder;
import com.ointerface.oconnect.data.Conference;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.Session;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by AnthonyDoan on 4/17/17.
 */

public class DashboardEventListViewAdapter extends BaseAdapter {
    public Context context;
    public ArrayList<Event> eventsList;
    public ArrayList<String> sessionNamesList;

    private LayoutInflater mInflater;

    public DashboardEventListViewAdapter(Context context, ArrayList<Event> eventsArg , ArrayList<String> sessionsArg) {
        super();
        this.context = context;
        this.eventsList = eventsArg;
        this.sessionNamesList = sessionsArg;

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return eventsList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public String getItem(int position) {
        return eventsList.get(position).getName();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Event event = eventsList.get(position);
        String sessionName = sessionNamesList.get(position);

        convertView = mInflater.inflate(R.layout.dashboard_event_list_view_item, null);

        DateFormat dfDay = new SimpleDateFormat("EEE");
        DateFormat dfTime = new SimpleDateFormat("h:mm a");
        dfTime.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));

        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDateDay);

        tvDate.setText(dfDay.format(event.getStartTime()) + ",");

        TextView tvTimeStart = (TextView) convertView.findViewById(R.id.tvStartTime);

        tvTimeStart.setText(dfTime.format(event.getStartTime()) + " - ");

        TextView tvTimeEnd = (TextView) convertView.findViewById(R.id.tvEndTime);

        tvTimeEnd.setText(dfTime.format(event.getEndTime()));

        TextView tvEventName = (TextView) convertView.findViewById(R.id.tvEventName);

        tvEventName.setText(event.getName());

        TextView tvSessionName = (TextView) convertView.findViewById(R.id.tvSessionName);

        tvSessionName.setText(sessionName);

        return convertView;
    }
}
