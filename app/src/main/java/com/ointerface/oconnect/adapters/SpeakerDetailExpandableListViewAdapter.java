package com.ointerface.oconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.EventDetailViewActivity;
import com.ointerface.oconnect.activities.ScheduleActivity;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.EventAbstract;
import com.ointerface.oconnect.data.EventFile;
import com.ointerface.oconnect.data.EventJournal;
import com.ointerface.oconnect.data.EventMisc;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.data.Session;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.data.SpeakerAbstract;
import com.ointerface.oconnect.data.SpeakerEventCache;
import com.ointerface.oconnect.data.SpeakerFile;
import com.ointerface.oconnect.data.SpeakerJournal;
import com.ointerface.oconnect.data.SpeakerLink;
import com.ointerface.oconnect.data.SpeakerMisc;
import com.ointerface.oconnect.util.AppUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

import static android.view.View.GONE;

/**
 * Created by AnthonyDoan on 5/20/17.
 */

public class SpeakerDetailExpandableListViewAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader;
    private List<Integer> _listHeaderNumber;
    private List<Boolean> _listGroupHasListView;
    private HashMap<Integer, Integer> _listChildCount;
    private HashMap<Integer, ArrayList<String>> _listChildItems;
    private Speaker _listSpeaker;

    public SpeakerDetailExpandableListViewAdapter(Context context, List<String> listDataHeader,
                                                List<Integer> listHeaderNumber,
                                                HashMap<Integer, Integer> listChildCount,
                                                List<Boolean> listGroupHasListView,
                                                HashMap<Integer, ArrayList<String>> listChildItems,
                                                Speaker listSpeaker) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listHeaderNumber = listHeaderNumber;
        this._listChildCount = listChildCount;
        this._listGroupHasListView = listGroupHasListView;
        this._listChildItems = listChildItems;
        this._listSpeaker = listSpeaker;
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
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        Person person = realm.where(Person.class).equalTo("objectId", _listSpeaker.getUserLink()).findFirst();

        String groupItemStr = (String) getGroup(groupPosition);

        LayoutInflater infalInflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (groupItemStr.equalsIgnoreCase("About")) {
            convertView = infalInflater.inflate(R.layout.speaker_detail_about_list_view_item, null);

            ImageView ivInfo = (ImageView) convertView.findViewById(R.id.ivParticipantJobTitle);
            ImageView ivSuitcase = (ImageView) convertView.findViewById(R.id.ivParticipantOrg);
            ImageView ivLightBuld = (ImageView) convertView.findViewById(R.id.ivParticipantInterests);
            ImageView ivHouse = (ImageView) convertView.findViewById(R.id.ivParticipantLocation);
            // ImageView ivPicture = (ImageView) convertView.findViewById(R.id.ivParticipantPicture);

            // ivPicture.setVisibility(View.INVISIBLE);

            RelativeLayout rlContainer = (RelativeLayout) convertView.findViewById(R.id.rlContainer);
            RelativeLayout rlContent = (RelativeLayout) convertView.findViewById(R.id.rlContent);

            rlContainer.setClipChildren(false);
            rlContent.setClipChildren(false);

            ivInfo.setBackground(AppUtil.changeDrawableColor(_context, R.drawable.icon_participants_info, AppUtil.getPrimaryThemColorAsInt()));
            ivSuitcase.setBackground(AppUtil.changeDrawableColor(_context, R.drawable.icon_paricipants_suitcase, AppUtil.getPrimaryThemColorAsInt()));
            ivLightBuld.setBackground(AppUtil.changeDrawableColor(_context, R.drawable.icon_participants_light_bulb, AppUtil.getPrimaryThemColorAsInt()));
            ivHouse.setBackground(AppUtil.changeDrawableColor(_context, R.drawable.icon_participants_house, AppUtil.getPrimaryThemColorAsInt()));


            TextView tvName = (TextView) convertView.findViewById(R.id.tvParticipantName);
            TextView tvJobTitle = (TextView) convertView.findViewById(R.id.tvParticipantJobTitle);
            TextView tvOrg = (TextView) convertView.findViewById(R.id.tvParticipantOrg);
            TextView tvInterests = (TextView) convertView.findViewById(R.id.tvParticipantInterests);
            TextView tvLocation = (TextView) convertView.findViewById(R.id.tvParticipantLocation);

            tvName.setText(_listSpeaker.getName());

            if (_listSpeaker.getJob() != null && !_listSpeaker.getJob().equalsIgnoreCase("")) {
                tvJobTitle.setText(_listSpeaker.getJob());
                tvJobTitle.setVisibility(View.VISIBLE);
                ivInfo.setVisibility(View.VISIBLE);
            } else {
                tvJobTitle.setVisibility(GONE);
                ivInfo.setVisibility(GONE);
            }

            if (_listSpeaker.getOrganization() != null && !_listSpeaker.getOrganization().equalsIgnoreCase("")) {
                tvOrg.setText(_listSpeaker.getOrganization());
                tvOrg.setVisibility(View.VISIBLE);
                ivSuitcase.setVisibility(View.VISIBLE);
            } else {
                tvOrg.setVisibility(GONE);
                ivSuitcase.setVisibility(GONE);
            }

            // TODO SET INTERESTS
            tvInterests.setVisibility(GONE);
            ivLightBuld.setVisibility(GONE);

            if (_listSpeaker.getLocation() != null && !_listSpeaker.getLocation().equalsIgnoreCase("")) {
                tvLocation.setText(_listSpeaker.getLocation());
                tvLocation.setVisibility(View.VISIBLE);
                ivHouse.setVisibility(View.VISIBLE);
            } else {
                tvLocation.setVisibility(GONE);
                ivHouse.setVisibility(GONE);
            }
        } else if (groupItemStr.equalsIgnoreCase("Links")) {
            RealmResults<SpeakerLink> linksResult = realm.where(SpeakerLink.class).equalTo("speakerID", _listSpeaker.getObjectId()).findAll();

            convertView = infalInflater.inflate(R.layout.speaker_detail_item_list_view, null);

            ArrayList<String> links = new ArrayList<String>();
            ArrayList<String> urls = new ArrayList<String>();

            for (int i = 0; i < linksResult.size(); ++i) {
                SpeakerLink link = linksResult.get(i);

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
        } else if (groupItemStr.equalsIgnoreCase("Talks")) {
            RealmResults<SpeakerEventCache> eventsResult = realm.where(SpeakerEventCache.class).equalTo("speakerID", _listSpeaker.getObjectId()).findAll();

            ArrayList<Event> eventsList = new ArrayList<Event>();

            for (int i = 0; i < eventsResult.size(); ++i) {
                SpeakerEventCache speakerEvent = eventsResult.get(i);

                Event event = realm.where(Event.class).equalTo("objectId", speakerEvent.getEventID()).findFirst();

                if (event != null) {
                    eventsList.add(event);
                }
            }

            Collections.sort(eventsList, new Comparator<Event>() {
                @Override
                public int compare(Event o1, Event o2) {
                    return o1.getStartTime().compareTo(o2.getStartTime());
                }
            });

            convertView = infalInflater.inflate(R.layout.speaker_detail_item_list_view, null);

            ListView lvItems = (ListView) convertView.findViewById(R.id.lvItems);

            TalksListViewAdapter adapter = new TalksListViewAdapter(_context, eventsList);

            lvItems.setAdapter(adapter);

            final ArrayList<Event> finalEventsList = eventsList;

            lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    EventDetailViewActivity.mItems = new ArrayList<RealmObject>();

                    EventDetailViewActivity.mItems.addAll(finalEventsList);

                    Intent i = new Intent(_context, EventDetailViewActivity.class);
                    i.putExtra("EVENT_NUMBER", position);
                    _context.startActivity(i);
                }
            });
        } else if (groupItemStr.equalsIgnoreCase("Bio")) {
            convertView = infalInflater.inflate(R.layout.speaker_detail_list_child, null);

            TextView tvBio = (TextView) convertView.findViewById(R.id.tvSpeakerInfo);

            if (person != null && person.getBio() != null && !person.getBio().equalsIgnoreCase("")) {
                tvBio.setText(person.getBio());
            } else if (_listSpeaker.getBio() != null && !_listSpeaker.getBio().equalsIgnoreCase("")) {
                tvBio.setText(_listSpeaker.getBio());
            }
        } else if (groupItemStr.equalsIgnoreCase("Send Question")) {
            convertView = infalInflater.inflate(R.layout.speaker_detail_list_send_question, null);
        } else if (groupItemStr.equalsIgnoreCase("Files")) {
            RealmResults<SpeakerEventCache> eventsResult = realm.where(SpeakerEventCache.class).equalTo("speakerID", _listSpeaker.getObjectId()).findAll();

            ArrayList<RealmObject> filesList = new ArrayList<RealmObject>();

            for (int i = 0; i < eventsResult.size(); ++i) {
                SpeakerEventCache speakerEvent = eventsResult.get(i);
                RealmResults<EventAbstract> abstractResult = realm.where(EventAbstract.class).equalTo("eventID", speakerEvent.getEventID()).findAll();
                RealmResults<EventMisc> miscResult = realm.where(EventMisc.class).equalTo("eventID", speakerEvent.getEventID()).findAll();
                RealmResults<EventJournal> journalResult = realm.where(EventJournal.class).equalTo("eventID", speakerEvent.getEventID()).findAll();
                RealmResults<EventFile> fileResult = realm.where(EventFile.class).equalTo("eventID", speakerEvent.getEventID()).findAll();

                filesList.addAll(abstractResult);
                filesList.addAll(miscResult);
                filesList.addAll(journalResult);
                filesList.addAll(fileResult);
            }

            RealmResults<SpeakerAbstract> abstractResult = realm.where(SpeakerAbstract.class).equalTo("speakerID", _listSpeaker.getObjectId()).findAll();
            RealmResults<SpeakerMisc> miscResult = realm.where(SpeakerMisc.class).equalTo("speakerID", _listSpeaker.getObjectId()).findAll();
            RealmResults<SpeakerJournal> journalResult = realm.where(SpeakerJournal.class).equalTo("speakerID", _listSpeaker.getObjectId()).findAll();
            RealmResults<SpeakerFile> fileResult = realm.where(SpeakerFile.class).equalTo("speakerID", _listSpeaker.getObjectId()).findAll();

            filesList.addAll(abstractResult);
            filesList.addAll(miscResult);
            filesList.addAll(journalResult);
            filesList.addAll(fileResult);

            ArrayList<String> links = new ArrayList<String>();
            ArrayList<String> urls = new ArrayList<String>();

            for (int i = 0; i < filesList.size(); ++i) {
                RealmObject file = filesList.get(i);

                if (file instanceof EventFile) {
                    EventFile curFile = (EventFile) file;

                    links.add(curFile.getName());
                    urls.add(curFile.getUrl());
                } else if (file instanceof EventJournal) {
                    EventJournal curFile = (EventJournal) file;

                    links.add(curFile.getName());
                    urls.add(curFile.getUrl());
                } else if (file instanceof EventMisc) {
                    EventMisc curFile = (EventMisc) file;

                    links.add(curFile.getName());
                    urls.add(curFile.getUrl());
                } else if (file instanceof EventAbstract) {
                    EventAbstract curFile = (EventAbstract) file;

                    links.add(curFile.getName());
                    urls.add(curFile.getUrl());
                } else if (file instanceof SpeakerFile) {
                    SpeakerFile curFile = (SpeakerFile) file;

                    links.add(curFile.getName());
                    urls.add(curFile.getUrl());
                } else if (file instanceof SpeakerJournal) {
                    SpeakerJournal curFile = (SpeakerJournal) file;

                    links.add(curFile.getName());
                    urls.add(curFile.getUrl());
                } else if (file instanceof SpeakerMisc) {
                    SpeakerMisc curFile = (SpeakerMisc) file;

                    links.add(curFile.getName());
                    urls.add(curFile.getUrl());
                } else if (file instanceof SpeakerAbstract) {
                    SpeakerAbstract curFile = (SpeakerAbstract) file;

                    links.add(curFile.getName());
                    urls.add(curFile.getUrl());
                }
            }

            convertView = infalInflater.inflate(R.layout.speaker_detail_item_list_view, null);

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
            /*
            if (groupItemStr.equalsIgnoreCase("Info")) {
                convertView = infalInflater.inflate(R.layout.event_detail_list_child, null);

                TextView tvInfo = (TextView) convertView.findViewById(R.id.tvInfo);

                tvInfo.setText(_listEvent.getInfo());
            } else if (_listGroupIsSpeaker.get(groupPosition) == true) {
                convertView = infalInflater.inflate(R.layout.event_detail_list_speaker_child, null);

                ListView lvSpeakers = (ListView) convertView.findViewById(R.id.lvSpeakers);

                ArrayList<Speaker> speakersList = _listChildSpeaker.get(groupPosition);
                ArrayList<String> speakerNamesList = new ArrayList<String>();

                for (int i = 0; i < speakersList.size(); ++i) {
                    speakerNamesList.add(speakersList.get(i).getName());
                }

                ArrayAdapter<String> itemsAdapter =
                        new ArrayAdapter<String>(_context, android.R.layout.simple_list_item_1, speakerNamesList);

                lvSpeakers.setAdapter(itemsAdapter);
            }
            */


        realm.close();

        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        // Session session = (Session) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.speaker_detail_list_header, null);
        }

        TextView tvSpeakerDetailName = (TextView) convertView.findViewById(R.id.tvSpeakerDetailName);

        tvSpeakerDetailName.setText((String) _listDataHeader.get(groupPosition));

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
