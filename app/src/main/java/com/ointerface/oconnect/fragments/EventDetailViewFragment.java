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
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.data.Session;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.data.SpeakerEventCache;
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

        RelativeLayout mainContainer = (RelativeLayout) rootView.findViewById(R.id.main_container);
        LinearLayout llMain = (LinearLayout) rootView.findViewById(R.id.llMain);
        RelativeLayout rlTopSection = (RelativeLayout) rootView.findViewById(R.id.rlTopSection);
        RelativeLayout rlMiscItems = (RelativeLayout) rootView.findViewById(R.id.rlMiscItems);

        mainContainer.setClipChildren(false);
        llMain.setClipChildren(false);
        rlTopSection.setClipChildren(false);
        rlMiscItems.setClipChildren(false);

        final Realm realm = AppUtil.getRealmInstance(App.getInstance());

        Bundle bundle = getArguments();

        pageNumber = bundle.getInt("PAGE_NUMBER");

        final Event event = (Event) mItems.get(pageNumber);

        final ImageView ivMyAgenda = (ImageView) rootView.findViewById(R.id.ivMyAgenda);

        ivMyAgenda.setBackground(AppUtil.changeDrawableColor(activity, R.drawable.icon_blue_star_empty, AppUtil.getPrimaryThemColorAsInt()));

        RealmList<Event> myAgendaList = OConnectBaseActivity.currentPerson.getFavoriteEvents();

        if (myAgendaList.contains(event) == true) {
            ivMyAgenda.setBackground(AppUtil.changeDrawableColor(activity, R.drawable.icon_blue_star_filled, AppUtil.getPrimaryThemColorAsInt()));
        }

        ImageView ivTweet = (ImageView) rootView.findViewById(R.id.ivTweet);

        ivTweet.setBackground(AppUtil.changeDrawableColor(activity, R.drawable.twitter_icon, AppUtil.getPrimaryThemColorAsInt()));

        ImageView ivAddNote = (ImageView) rootView.findViewById(R.id.ivAddANote);

        ivAddNote.setBackground(AppUtil.changeDrawableColor(activity, R.drawable.icon_add_a_note, AppUtil.getPrimaryThemColorAsInt()));

        TextView tvMyAgenda = (TextView) rootView.findViewById(R.id.tvMyAgenda);

        tvMyAgenda.setTextColor(AppUtil.getPrimaryThemColorAsInt());

        tvMyAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();
                OConnectBaseActivity.currentPerson.getFavoriteEvents().add(event);
                realm.commitTransaction();

                ivMyAgenda.setBackground(AppUtil.changeDrawableColor(activity, R.drawable.icon_blue_star_filled, AppUtil.getPrimaryThemColorAsInt()));
            }
        });

        ivMyAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();
                OConnectBaseActivity.currentPerson.getFavoriteEvents().add(event);
                realm.commitTransaction();

                ivMyAgenda.setBackground(AppUtil.changeDrawableColor(activity, R.drawable.icon_blue_star_filled, AppUtil.getPrimaryThemColorAsInt()));
            }
        });

        TextView tvTweet = (TextView) rootView.findViewById(R.id.tvTweet);

        tvTweet.setTextColor(AppUtil.getPrimaryThemColorAsInt());

        ivTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tweetIntent = new Intent(Intent.ACTION_SEND);
                tweetIntent.putExtra(Intent.EXTRA_TEXT, "Attending \"" + OConnectBaseActivity.selectedConference.getName() + "\"" + " #oconnectapp");
                tweetIntent.setType("text/plain");

                PackageManager packManager = activity.getPackageManager();
                List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent,  PackageManager.MATCH_DEFAULT_ONLY);

                boolean resolved = false;
                for(ResolveInfo resolveInfo: resolvedInfoList){
                    if(resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")){
                        tweetIntent.setClassName(
                                resolveInfo.activityInfo.packageName,
                                resolveInfo.activityInfo.name );
                        resolved = true;
                        break;
                    }
                }
                if(resolved){
                    startActivity(tweetIntent);
                }else{
                    Toast.makeText(activity, "Twitter app isn't found.", Toast.LENGTH_LONG).show();
                }
            }
        });

        tvTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tweetIntent = new Intent(Intent.ACTION_SEND);
                tweetIntent.putExtra(Intent.EXTRA_TEXT, "Attending \"" + OConnectBaseActivity.selectedConference.getName() + "\"" + " #oconnectapp");
                tweetIntent.setType("text/plain");

                PackageManager packManager = activity.getPackageManager();
                List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent,  PackageManager.MATCH_DEFAULT_ONLY);

                boolean resolved = false;
                for(ResolveInfo resolveInfo: resolvedInfoList){
                    if(resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")){
                        tweetIntent.setClassName(
                                resolveInfo.activityInfo.packageName,
                                resolveInfo.activityInfo.name );
                        resolved = true;
                        break;
                    }
                }
                if(resolved){
                    startActivity(tweetIntent);
                }else{
                    Toast.makeText(activity, "Twitter app isn't found.", Toast.LENGTH_LONG).show();
                }
            }
        });

        TextView tvAddNote = (TextView) rootView.findViewById(R.id.tvAddNote);

        tvAddNote.setTextColor(AppUtil.getPrimaryThemColorAsInt());

        ivAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, AddNoteActivity.class);
                startActivity(i);
                activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up );
            }
        });

        tvAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, AddNoteActivity.class);
                startActivity(i);
                activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up );
            }
        });

        TextView tvQA = (TextView) rootView.findViewById(R.id.tvQA);

        tvQA.setTextColor(AppUtil.getPrimaryThemColorAsInt());

        if (OConnectBaseActivity.selectedConference.isShowQuestions() == true) {
            tvQA.setVisibility(View.VISIBLE);
        } else {
            tvQA.setVisibility(GONE);
        }

        ImageView ivOrganizationLogo = (ImageView) rootView.findViewById(R.id.ivOrganizationLogo);

        if (OConnectBaseActivity.selectedConference.getImage() != null) {
            Bitmap bm = BitmapFactory.decodeByteArray(OConnectBaseActivity.selectedConference.getImage(), 0, OConnectBaseActivity.selectedConference.getImage().length);

            ivOrganizationLogo.setImageBitmap(bm);
        }

        TextView tvEventTitle = (TextView) rootView.findViewById(R.id.tvEventTitle);
        tvEventTitle.setText(event.getName());

        TextView tvTimeRange = (TextView) rootView.findViewById(R.id.tvEventDateRange);

        if (!event.isNonTimedEvent()) {
            DateFormat dfTime = new SimpleDateFormat("h:mm a");
            DateFormat dfDate = new SimpleDateFormat("MMM d");

            dfTime.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
            dfDate.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));

            String startTime = dfTime.format(event.getStartTime());
            String endTime = dfTime.format(event.getEndTime());

            tvTimeRange.setText(startTime + " - " + endTime + " on " + dfDate.format(event.getStartTime()));

            tvTimeRange.setVisibility(View.VISIBLE);
        } else {
            tvTimeRange.setVisibility(GONE);
        }

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

        if (event.getInfo() != null && !event.getInfo().equalsIgnoreCase("")) {
            _listDataHeader.add("Info");
            _listHeaderNumber.add(0);
            _listGroupIsSpeaker.add(false);
            _listChildCount.put(0,1);
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

        adapter = new EventDetailExpandableListViewAdapter(activity, _listDataHeader, _listHeaderNumber, _listChildCount,
                _listGroupIsSpeaker, _listChildSpeaker, event);
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
