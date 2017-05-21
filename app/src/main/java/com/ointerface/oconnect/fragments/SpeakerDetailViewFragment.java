package com.ointerface.oconnect.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.EventDetailViewActivity;
import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.activities.SpeakerDetailViewActivity;
import com.ointerface.oconnect.adapters.EventDetailExpandableListViewAdapter;
import com.ointerface.oconnect.adapters.SpeakerDetailExpandableListViewAdapter;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.EventAbstract;
import com.ointerface.oconnect.data.EventFile;
import com.ointerface.oconnect.data.EventJournal;
import com.ointerface.oconnect.data.EventMisc;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.data.SpeakerAbstract;
import com.ointerface.oconnect.data.SpeakerEventCache;
import com.ointerface.oconnect.data.SpeakerFile;
import com.ointerface.oconnect.data.SpeakerJournal;
import com.ointerface.oconnect.data.SpeakerLink;
import com.ointerface.oconnect.data.SpeakerMisc;
import com.ointerface.oconnect.util.AppUtil;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpeakerDetailViewFragment extends Fragment {

    static public int pageNumber = 0;

    static public SpeakerDetailViewActivity activity;

    static private ArrayList<RealmObject> mItems;

    static private int currentSpeakerNumber;

    private ExpandableListView elvSpeakerDetailInfo;
    private SpeakerDetailExpandableListViewAdapter adapter;

    private List<String> _listDataHeader;
    private List<Integer> _listHeaderNumber;
    private List<Boolean> _listGroupHasListView;
    private HashMap<Integer, Integer> _listChildCount;
    private HashMap<Integer, ArrayList<String>> _listChildItems;
    private Speaker _listSpeaker;

    private Bitmap bmp;

    public SpeakerDetailViewFragment() {
        // Required empty public constructor
    }

    public static SpeakerDetailViewFragment newInstance(int pageNumberArg, SpeakerDetailViewActivity activityArg,
                                                      ArrayList<RealmObject> mItemsArg,
                                                      int currentSpeakerNumberArg) {
        SpeakerDetailViewFragment fragment = new SpeakerDetailViewFragment();

        activity = activityArg;

        pageNumber = pageNumberArg;

        mItems = mItemsArg;

        currentSpeakerNumber = currentSpeakerNumberArg;

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_speaker_detail_view, container, false);

        RelativeLayout mainContainer = (RelativeLayout) rootView.findViewById(R.id.main_container);
        LinearLayout llMain = (LinearLayout) rootView.findViewById(R.id.llMain);
        RelativeLayout rlTopSection = (RelativeLayout) rootView.findViewById(R.id.rlTopSection);
        RelativeLayout rlMiscItems = (RelativeLayout) rootView.findViewById(R.id.rlMiscItems);

        mainContainer.setClipChildren(false);
        llMain.setClipChildren(false);
        rlTopSection.setClipChildren(false);
        rlMiscItems.setClipChildren(false);

        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        Speaker speaker = (Speaker) mItems.get(pageNumber);

        Person person = realm.where(Person.class).equalTo("objectId", speaker.getUserLink()).findFirst();

        ImageView ivConnect = (ImageView) rootView.findViewById(R.id.ivConnect);

        ivConnect.setBackground(AppUtil.changeDrawableColor(activity, R.drawable.icon_blue_star_empty, AppUtil.getPrimaryThemColorAsInt()));

        ImageView ivMessage = (ImageView) rootView.findViewById(R.id.ivMessage);

        ivMessage.setBackground(AppUtil.changeDrawableColor(activity, R.drawable.icon_envelop, AppUtil.getPrimaryThemColorAsInt()));

        ImageView ivAddNote = (ImageView) rootView.findViewById(R.id.ivAddANote);

        ivAddNote.setBackground(AppUtil.changeDrawableColor(activity, R.drawable.icon_add_a_note, AppUtil.getPrimaryThemColorAsInt()));

        TextView tvConnect = (TextView) rootView.findViewById(R.id.tvConnect);

        tvConnect.setTextColor(AppUtil.getPrimaryThemColorAsInt());

        TextView tvMessage = (TextView) rootView.findViewById(R.id.tvMessage);

        tvMessage.setTextColor(AppUtil.getPrimaryThemColorAsInt());

        TextView tvAddNote = (TextView) rootView.findViewById(R.id.tvAddNote);

        tvAddNote.setTextColor(AppUtil.getPrimaryThemColorAsInt());

        TextView tvQA = (TextView) rootView.findViewById(R.id.tvQA);

        tvQA.setTextColor(AppUtil.getPrimaryThemColorAsInt());

        final ImageView ivProfile = (ImageView) rootView.findViewById(R.id.ivProfile);

        if (person != null && person.getPictureURL() != null && !person.getPictureURL().equalsIgnoreCase("")) {
            final String pictureURL = person.getPictureURL();

            new AsyncTask<Void, Void, Void>() {
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
                        ivProfile.setImageBitmap(bmp);
                    }
                }

            }.execute();
        } else if (speaker.getImage() != null) {
            Bitmap bm2 = BitmapFactory.decodeByteArray(speaker.getImage(), 0, speaker.getImage().length);

            ivProfile.setImageBitmap(bm2);
        }

        TextView tvSpeakerName = (TextView) rootView.findViewById(R.id.tvSpeakerName);
        tvSpeakerName.setText(speaker.getName());

        elvSpeakerDetailInfo = (ExpandableListView) rootView.findViewById(R.id.elvSpeakerInfo);

        getSpeakerDetailListData();

        elvSpeakerDetailInfo.setAdapter(adapter);

        for (int i = 0; i < adapter.getGroupCount(); ++i) {
            elvSpeakerDetailInfo.expandGroup(i);
        }

        return rootView;
    }

    public void getSpeakerDetailListData() {
        _listDataHeader = new ArrayList<String>();
        _listHeaderNumber = new ArrayList<Integer>();
        _listGroupHasListView = new ArrayList<Boolean>();
        _listChildCount = new HashMap<Integer, Integer>();
        _listChildItems = new HashMap<Integer, ArrayList<String>>();

        Speaker speaker = (Speaker) mItems.get(pageNumber);

        int groupNum = 0;

        if (speaker.getJob() != null && !speaker.getJob().equalsIgnoreCase("") ||
                speaker.getOrganization() != null && !speaker.getOrganization().equalsIgnoreCase("") ||
                speaker.getLocation() != null && !speaker.getLocation().equalsIgnoreCase("")) {
            _listDataHeader.add("About");
            _listHeaderNumber.add(groupNum);
            _listGroupHasListView.add(false);
            _listChildCount.put(groupNum,1);
            ++groupNum;
        }

        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<SpeakerLink> linksResult = realm.where(SpeakerLink.class).equalTo("speakerID", speaker.getObjectId()).findAll();

        if (linksResult != null && linksResult.size() > 0) {
            _listDataHeader.add("Links");
            _listHeaderNumber.add(groupNum);
            _listGroupHasListView.add(true);
            _listChildCount.put(groupNum,1);
            ++groupNum;
        }

        RealmResults<SpeakerEventCache> eventsResult = realm.where(SpeakerEventCache.class).equalTo("speakerID", speaker.getObjectId()).findAll();

        if (eventsResult != null && eventsResult.size() > 0) {
            _listDataHeader.add("Talks");
            _listHeaderNumber.add(groupNum);
            _listGroupHasListView.add(true);
            _listChildCount.put(groupNum,1);
            ++groupNum;
        }

        Person person = realm.where(Person.class).equalTo("objectId", speaker.getUserLink()).findFirst();

        if (person != null && person.getBio() != null && !person.getBio().equalsIgnoreCase("")) {
            _listDataHeader.add("Bio");
            _listHeaderNumber.add(groupNum);
            _listGroupHasListView.add(true);
            _listChildCount.put(groupNum,1);
            ++groupNum;
        } else if (speaker.getBio() != null && !speaker.getBio().equalsIgnoreCase("")) {
            _listDataHeader.add("Bio");
            _listHeaderNumber.add(groupNum);
            _listGroupHasListView.add(true);
            _listChildCount.put(groupNum,1);
            ++groupNum;
        }

        if (OConnectBaseActivity.selectedConference.isShowQuestions()) {
            _listDataHeader.add("Send Question");
            _listHeaderNumber.add(groupNum);
            _listGroupHasListView.add(true);
            _listChildCount.put(groupNum,1);
            ++groupNum;
        }

        boolean bHasFile = false;

        for (int i = 0; i < eventsResult.size(); ++i) {
            SpeakerEventCache speakerEvent = eventsResult.get(i);
            RealmResults<EventAbstract> abstractResult = realm.where(EventAbstract.class).equalTo("eventID", speakerEvent.getEventID()).findAll();
            RealmResults<EventMisc> miscResult = realm.where(EventMisc.class).equalTo("eventID", speakerEvent.getEventID()).findAll();
            RealmResults<EventJournal> journalResult = realm.where(EventJournal.class).equalTo("eventID", speakerEvent.getEventID()).findAll();
            RealmResults<EventFile> fileResult = realm.where(EventFile.class).equalTo("eventID", speakerEvent.getEventID()).findAll();

            if (abstractResult != null && abstractResult.size() > 0 ||
                    miscResult != null && miscResult.size() > 0 ||
                    journalResult != null && journalResult.size() > 0 ||
                    fileResult != null && fileResult.size() > 0) {
                bHasFile = true;
                break;
            }
        }

        RealmResults<SpeakerAbstract> abstractResult = realm.where(SpeakerAbstract.class).equalTo("speakerID", speaker.getObjectId()).findAll();
        RealmResults<SpeakerMisc> miscResult = realm.where(SpeakerMisc.class).equalTo("speakerID", speaker.getObjectId()).findAll();
        RealmResults<SpeakerJournal> journalResult = realm.where(SpeakerJournal.class).equalTo("speakerID", speaker.getObjectId()).findAll();
        RealmResults<SpeakerFile> fileResult = realm.where(SpeakerFile.class).equalTo("speakerID", speaker.getObjectId()).findAll();

        if (abstractResult != null && abstractResult.size() > 0 ||
                miscResult != null && miscResult.size() > 0 ||
                journalResult != null && journalResult.size() > 0 ||
                fileResult != null && fileResult.size() > 0) {
            bHasFile = true;
        }

        if (bHasFile == true) {
            _listDataHeader.add("Files");
            _listHeaderNumber.add(groupNum);
            _listGroupHasListView.add(true);
            _listChildCount.put(groupNum,1);
            ++groupNum;
        }

        adapter = new SpeakerDetailExpandableListViewAdapter(activity, _listDataHeader, _listHeaderNumber, _listChildCount,
                _listGroupHasListView, _listChildItems, speaker);
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
