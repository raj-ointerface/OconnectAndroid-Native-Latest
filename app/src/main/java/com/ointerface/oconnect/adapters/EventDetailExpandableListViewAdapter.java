package com.ointerface.oconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.ParticipantsActivity;
import com.ointerface.oconnect.activities.SpeakerDetailViewActivity;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.EventLink;
import com.ointerface.oconnect.data.Session;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.data.SpeakerLink;
import com.ointerface.oconnect.util.AppUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

import static android.view.View.GONE;

/**
 * Created by AnthonyDoan on 5/7/17.
 */

public class EventDetailExpandableListViewAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader;
    private List<Integer> _listHeaderNumber;
    private List<Boolean> _listGroupIsSpeaker;
    private HashMap<Integer, Integer> _listChildCount;
    private HashMap<Integer, ArrayList<Speaker>> _listChildSpeaker;
    private Event _listEvent;

    public EventDetailExpandableListViewAdapter(Context context, List<String> listDataHeader,
                                             List<Integer> listHeaderNumber,
                                             HashMap<Integer, Integer> listChildCount,
                                                List<Boolean> listGroupIsSpeaker,
                                                HashMap<Integer, ArrayList<Speaker>> listChildSpeaker,
                                                Event listEvent) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listHeaderNumber = listHeaderNumber;
        this._listChildCount = listChildCount;
        this._listGroupIsSpeaker = listGroupIsSpeaker;
        this._listChildSpeaker = listChildSpeaker;
        this._listEvent = listEvent;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listChildCount.get(this._listHeaderNumber.get(groupPosition));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listChildCount.get(this._listHeaderNumber.get(groupPosition)).intValue();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
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

        final Realm realm = AppUtil.getRealmInstance(App.getInstance());

        String groupItemStr = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (groupItemStr.equalsIgnoreCase("Info")) {
                convertView = infalInflater.inflate(R.layout.event_detail_list_child, null);

                TextView tvInfo = (TextView) convertView.findViewById(R.id.tvInfo);

                tvInfo.setText(_listEvent.getInfo());
            } else if (_listGroupIsSpeaker.get(groupPosition) == true) {
                convertView = infalInflater.inflate(R.layout.event_detail_list_speaker_child, null);

                ListView lvSpeakers = (ListView) convertView.findViewById(R.id.lvSpeakers);

                final ArrayList<Speaker> speakersList = _listChildSpeaker.get(groupPosition);
                ArrayList<String> speakerNamesList = new ArrayList<String>();

                for (int i = 0; i < speakersList.size(); ++i) {
                    speakerNamesList.add(speakersList.get(i).getName());
                }

                ArrayAdapter<String> itemsAdapter =
                        new ArrayAdapter<String>(_context, android.R.layout.simple_list_item_1, speakerNamesList);

                lvSpeakers.setAdapter(itemsAdapter);

                lvSpeakers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // SpeakerDetailViewActivity.mItems = new ArrayList<RealmObject>();

                        // SpeakerDetailViewActivity.mItems.addAll(speakersList);

                        Intent i = new Intent(_context, SpeakerDetailViewActivity.class);
                        i.putExtra("SPEAKER_NUMBER", position);
                        i.putExtra("SPEAKER_LIST", speakersList);
                        _context.startActivity(i);
                    }
                });
            }else if (groupItemStr.equalsIgnoreCase("Links")) {
                RealmResults<EventLink> linksResult = realm.where(EventLink.class).equalTo("eventID", _listEvent.getObjectId()).findAll();

                convertView = infalInflater.inflate(R.layout.speaker_detail_item_list_view, null);

                ArrayList<String> links = new ArrayList<String>();
                ArrayList<String> urls = new ArrayList<String>();

                for (int i = 0; i < linksResult.size(); ++i) {
                    EventLink link = linksResult.get(i);

                    links.add(link.getLabel());
                    urls.add(link.getLink());
                }

                ListView lvItems = (ListView) convertView.findViewById(R.id.lvItems);

                LinksListViewAdapter linksAdapter = new LinksListViewAdapter(_context, links, urls);

                lvItems.setAdapter(linksAdapter);

                final ArrayList<String> finalUrls = urls;

                lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrls.get(position)));
                        _context.startActivity(browserIntent);
                    }
                });
            }
        }

        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        // Session session = (Session) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.event_detail_list_header, null);
        }

        TextView tvEventDetailName = (TextView) convertView.findViewById(R.id.tvEventDetailName);

        tvEventDetailName.setText((String) _listDataHeader.get(groupPosition));

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
