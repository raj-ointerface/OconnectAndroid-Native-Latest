package com.ointerface.oconnect.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.easing.linear.Linear;
import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.AddNoteActivity;
import com.ointerface.oconnect.activities.EventDetailViewActivity;
import com.ointerface.oconnect.activities.HelpViewPagerActivity;
import com.ointerface.oconnect.activities.InfoActivity;
import com.ointerface.oconnect.activities.MyNotesActivity;
import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.activities.ScheduleActivity;
import com.ointerface.oconnect.adapters.EventDetailExpandableListViewAdapter;
import com.ointerface.oconnect.data.Attendee;
import com.ointerface.oconnect.data.DBQuestion;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.EventAbstract;
import com.ointerface.oconnect.data.EventFile;
import com.ointerface.oconnect.data.EventJournal;
import com.ointerface.oconnect.data.EventLink;
import com.ointerface.oconnect.data.EventMisc;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.data.Session;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.data.SpeakerAbstract;
import com.ointerface.oconnect.data.SpeakerEventCache;
import com.ointerface.oconnect.data.SpeakerFile;
import com.ointerface.oconnect.data.SpeakerJournal;
import com.ointerface.oconnect.data.SpeakerMisc;
import com.ointerface.oconnect.util.AppUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventDetailViewFragment extends Fragment {

    public int pageNumber = 0;

    static public EventDetailViewActivity activity;

    static private ArrayList<RealmObject> mItems;

    static private int currentEventNumber;

    private ExpandableListView elvEventDetailInfo;
    private EventDetailExpandableListViewAdapter adapter;

    private List<String> _listDataHeader;
    private List<Integer> _listHeaderNumber;
    private List<Boolean> _listGroupIsSpeaker;
    private HashMap<Integer, Integer> _listChildCount;
    private HashMap<Integer, ArrayList<Speaker>> _listChildSpeaker;
    private Event _listEvent;

    public EventDetailViewFragment() {
        // Required empty public constructor
    }

    public static EventDetailViewFragment newInstance(int pageNumberArg, EventDetailViewActivity activityArg,
                                                      ArrayList<RealmObject> mItemsArg,
                                                      int currentEventNumberArg) {
        EventDetailViewFragment fragment = new EventDetailViewFragment();

        activity = activityArg;

        // pageNumber = pageNumberArg;

        Bundle bundle = new Bundle();
        bundle.putInt("PAGE_NUMBER", pageNumberArg);

        fragment.setArguments(bundle);

        mItems = mItemsArg;

        currentEventNumber = currentEventNumberArg;

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_event_detail_layout, container, false);

        final Realm realm = AppUtil.getRealmInstance(App.getInstance());

        Bundle bundle = getArguments();

        pageNumber = bundle.getInt("PAGE_NUMBER");

        final Event event = (Event) mItems.get(pageNumber);

        elvEventDetailInfo = (ExpandableListView) rootView.findViewById(R.id.elvEventInfo);

        getEventDetailListData();

        elvEventDetailInfo.setAdapter(adapter);

        for (int i = 0; i < adapter.getGroupCount(); ++i) {
            elvEventDetailInfo.expandGroup(i);
        }

        return rootView;
    }

    public void getEventDetailListData() {
        _listDataHeader = new ArrayList<String>();
        _listHeaderNumber = new ArrayList<Integer>();
        _listGroupIsSpeaker = new ArrayList<Boolean>();
        _listChildCount = new HashMap<Integer, Integer>();
        _listChildSpeaker = new HashMap<Integer, ArrayList<Speaker>>();

        Event event = (Event) mItems.get(pageNumber);

        _listDataHeader.add("");
        _listHeaderNumber.add(0);
        _listGroupIsSpeaker.add(false);
        _listChildCount.put(0,1);

        if (event.getInfo() != null && !event.getInfo().equalsIgnoreCase("")) {
            _listDataHeader.add("Info");
            _listHeaderNumber.add(1);
            _listGroupIsSpeaker.add(false);
            _listChildCount.put(1,1);
        }

        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<SpeakerEventCache> speakersResult = realm.where(SpeakerEventCache.class).equalTo("eventID", event.getObjectId()).findAll();

        ArrayList<Speaker> speakersList = new ArrayList<Speaker>();

        if (speakersResult != null) {
            for (int i = 0; i < speakersResult.size(); ++i) {
                SpeakerEventCache speakerEventObj = speakersResult.get(i);

                Speaker speaker = realm.where(Speaker.class).equalTo("objectId", speakerEventObj.getSpeakerID()).findFirst();

                if (speaker != null) {
                    speakersList.add(speaker);
                }
            }
        }

        Collections.sort(speakersList, new Comparator<Speaker>(){
            public int compare(Speaker obj1, Speaker obj2) {
                // ## Ascending order
                return obj1.getSpeakerLabel().compareToIgnoreCase(obj2.getSpeakerLabel());
            }
        });

        String speakerLabel = "";
        int headerNumber = _listHeaderNumber.size();

        for (int j = 0; j < speakersList.size(); ++j) {
            Speaker curSpeaker = speakersList.get(j);

            if (!speakerLabel.equalsIgnoreCase(curSpeaker.getSpeakerLabel())) {
                speakerLabel = curSpeaker.getSpeakerLabel();

                _listDataHeader.add(speakerLabel);
                _listHeaderNumber.add(headerNumber);
                _listGroupIsSpeaker.add(true);

                ArrayList<Speaker> speakersArr = new ArrayList<Speaker>();
                for (int k = 0; k < speakersList.size(); ++k) {
                    Speaker mySpeaker = speakersList.get(k);

                    realm.beginTransaction();
                    if (mySpeaker.getSpeakerLabel() == null) {
                        mySpeaker.setSpeakerLabel("Speakers");
                    }
                    realm.commitTransaction();

                    if (mySpeaker.getSpeakerLabel().equalsIgnoreCase(speakerLabel)) {
                        speakersArr.add(mySpeaker);
                    }
                }
                _listChildCount.put(headerNumber, speakersArr.size());
                _listChildSpeaker.put(headerNumber++, speakersArr);
            }
        }

        RealmResults<EventLink> linksResult = realm.where(EventLink.class).equalTo("eventID", event.getObjectId()).findAll();

        if (linksResult.size() > 0) {
            _listDataHeader.add("Links");
            _listHeaderNumber.add(headerNumber);
            _listGroupIsSpeaker.add(false);
            _listChildCount.put(headerNumber++,1);
        }

        RealmResults<DBQuestion> questionsResult = realm.where(DBQuestion.class).equalTo("event", event.getObjectId()).findAll();

        if (questionsResult.size() > 0) {
            _listDataHeader.add("Discussion Board");
            _listHeaderNumber.add(headerNumber);
            _listGroupIsSpeaker.add(false);
            _listChildCount.put(headerNumber++,1);
        }

        if (OConnectBaseActivity.selectedConference.isShowQuestions() == true) {
            _listDataHeader.add("Post Questions & Comments");
            _listHeaderNumber.add(headerNumber);
            _listGroupIsSpeaker.add(false);
            _listChildCount.put(headerNumber++,1);
        }

        boolean bHasFile = false;

        RealmResults<EventAbstract> abstractResult = realm.where(EventAbstract.class).equalTo("eventID", event.getObjectId()).findAll();
        RealmResults<EventMisc> miscResult = realm.where(EventMisc.class).equalTo("eventID", event.getObjectId()).findAll();
        RealmResults<EventJournal> journalResult = realm.where(EventJournal.class).equalTo("eventID", event.getObjectId()).findAll();
        RealmResults<EventFile> fileResult = realm.where(EventFile.class).equalTo("eventID", event.getObjectId()).findAll();

        if (abstractResult != null && abstractResult.size() > 0 ||
                miscResult != null && miscResult.size() > 0 ||
                journalResult != null && journalResult.size() > 0 ||
                fileResult != null && fileResult.size() > 0) {
            bHasFile = true;
        }

        if (bHasFile == true) {
            _listDataHeader.add("Files");
            _listHeaderNumber.add(headerNumber);
            _listGroupIsSpeaker.add(false);
            _listChildCount.put(headerNumber++,1);
        }

        adapter = new EventDetailExpandableListViewAdapter(activity, _listDataHeader, _listHeaderNumber, _listChildCount,
                _listGroupIsSpeaker, _listChildSpeaker, event, activity);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
