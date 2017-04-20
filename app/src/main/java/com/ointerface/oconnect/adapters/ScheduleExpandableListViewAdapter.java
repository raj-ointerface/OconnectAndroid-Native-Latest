package com.ointerface.oconnect.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.icu.util.TimeZone;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.DashboardActivity;
import com.ointerface.oconnect.containers.MenuItemHolder;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.Session;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.util.AppUtil;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static android.view.View.GONE;

/**
 * Created by AnthonyDoan on 4/18/17.
 */

public class ScheduleExpandableListViewAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader; // header titles
    private List<Session> _listSessionHeader;
    // child data in format of header title, child title
    private HashMap<String, List<Event>> _listDataChild;


    public ScheduleExpandableListViewAdapter(Context context, List<String> listDataHeader,
                                             List<Session> listSessionHeader,
                                        HashMap<String, List<Event>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listSessionHeader = listSessionHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listSessionHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Event childItem = (Event) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.schedule_group_list_child, null);
        }

        TextView tvTimeRange = (TextView) convertView
                .findViewById(R.id.tvTimeRange);

        // tvTimeRange.setText(childItem.ge);
        SimpleDateFormat dfTime = new SimpleDateFormat("h:mm a");
        dfTime.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));

        String startTime = dfTime.format(childItem.getStartTime());
        String endTime = dfTime.format(childItem.getEndTime());

        tvTimeRange.setText(startTime + " - " + endTime);

        TextView eventName = (TextView) convertView.findViewById(R.id.tvEventName);

        eventName.setText(childItem.getName());

        TextView tvLocation = (TextView) convertView.findViewById(R.id.tvLocation);

        if (childItem.getLocation() != null && !childItem.getLocation().equalsIgnoreCase("")) {
            tvLocation.setText(childItem.getLocation());
            tvLocation.setVisibility(View.VISIBLE);
        } else {
            tvLocation.setVisibility(GONE);
        }

        TextView tvSpeaker = (TextView) convertView.findViewById(R.id.tvSpeakerName);

        RealmList<Speaker> foundSpeakers = childItem.getSpeakers();

        if (foundSpeakers.size() > 1) {
            tvSpeaker.setText("Multiple Speakers");
            tvSpeaker.setVisibility(View.VISIBLE);
        } else if (foundSpeakers.size() == 0) {
            tvSpeaker.setVisibility(GONE);
        } else {
            tvSpeaker.setText(foundSpeakers.get(0).getName());
            tvSpeaker.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Session session = (Session) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.schedule_group_list_header, null);
        }

        TextView tvSessionName = (TextView) convertView
                .findViewById(R.id.tvSessionName);
        tvSessionName.setTypeface(null, Typeface.BOLD);
        tvSessionName.setText(session.getTrack());

        TextView tvModerator = (TextView) convertView.findViewById(R.id.tvModeratorName);

        if (session.getModerator() != null && !session.getModerator().equalsIgnoreCase("")) {
            tvModerator.setText(session.getModerator());
            tvModerator.setVisibility(View.VISIBLE);
        } else {
            tvModerator.setVisibility(GONE);
        }

        TextView tvTimeRange = (TextView) convertView.findViewById(R.id.tvTimeRange);

        DateFormat dfTime = new SimpleDateFormat("h:mm a");
        dfTime.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));

        String startTime = dfTime.format(session.getStartTime());
        String endTime = dfTime.format(session.getEndTime());

        tvTimeRange.setText(startTime + " - " + endTime);

        TextView tvLocation = (TextView) convertView.findViewById(R.id.tvLocation);

        if (session.getLocation() != null && !session.getLocation().equalsIgnoreCase("")) {
            tvLocation.setText(session.getLocation());
            tvLocation.setVisibility(View.VISIBLE);
        } else {
            tvLocation.setVisibility(GONE);
        }

        TextView tvArrow = (TextView) convertView.findViewById(R.id.tvArrow);

        tvArrow.setText(">");

        if (isExpanded == true) {
            tvArrow.setRotation(90);
        } else {
            tvArrow.setRotation(0);
        }

        return convertView;
    }
}
