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
import com.ointerface.oconnect.activities.AttendeeDetailViewActivity;
import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.activities.SpeakerDetailViewActivity;
import com.ointerface.oconnect.adapters.SpeakerDetailExpandableListViewAdapter;
import com.ointerface.oconnect.data.Attendee;
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
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendeeDetailViewFragment extends Fragment {

    static public int pageNumber = 0;

    static public AttendeeDetailViewActivity activity;

    static private ArrayList<RealmObject> mItems;

    static private int currentAttendeeNumber;

    private ExpandableListView elvAttendeeDetailInfo;
    private SpeakerDetailExpandableListViewAdapter adapter;

    private List<String> _listDataHeader;
    private List<Integer> _listHeaderNumber;
    private List<Boolean> _listGroupHasListView;
    private HashMap<Integer, Integer> _listChildCount;
    private HashMap<Integer, ArrayList<String>> _listChildItems;
    private Attendee _listAttendee;

    private Bitmap bmp;

    public AttendeeDetailViewFragment() {
        // Required empty public constructor
    }

    public static AttendeeDetailViewFragment newInstance(int pageNumberArg, AttendeeDetailViewActivity activityArg,
                                                      ArrayList<RealmObject> mItemsArg,
                                                      int currentSpeakerNumberArg) {
        AttendeeDetailViewFragment fragment = new AttendeeDetailViewFragment();

        activity = activityArg;

        pageNumber = pageNumberArg;

        mItems = mItemsArg;

        currentAttendeeNumber = currentSpeakerNumberArg;

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_attendee_detail_view, container, false);

        RelativeLayout mainContainer = (RelativeLayout) rootView.findViewById(R.id.main_container);
        LinearLayout llMain = (LinearLayout) rootView.findViewById(R.id.llMain);
        RelativeLayout rlTopSection = (RelativeLayout) rootView.findViewById(R.id.rlTopSection);
        RelativeLayout rlMiscItems = (RelativeLayout) rootView.findViewById(R.id.rlMiscItems);

        mainContainer.setClipChildren(false);
        llMain.setClipChildren(false);
        rlTopSection.setClipChildren(false);
        rlMiscItems.setClipChildren(false);

        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        Attendee attendee = (Attendee) mItems.get(pageNumber);

        Person person = realm.where(Person.class).equalTo("objectId", attendee.getUserLink()).findFirst();

        ImageView ivConnect = (ImageView) rootView.findViewById(R.id.ivConnect);

        ivConnect.setBackground(AppUtil.changeDrawableColor(activity, R.drawable.icon_blue_star_empty, AppUtil.getPrimaryThemColorAsInt()));

        ImageView ivMessage = (ImageView) rootView.findViewById(R.id.ivMessage);

        ivMessage.setBackground(AppUtil.changeDrawableColor(activity, R.drawable.icon_envelop, AppUtil.getPrimaryThemColorAsInt()));

        TextView tvConnect = (TextView) rootView.findViewById(R.id.tvConnect);

        tvConnect.setTextColor(AppUtil.getPrimaryThemColorAsInt());

        TextView tvMessage = (TextView) rootView.findViewById(R.id.tvMessage);

        tvMessage.setTextColor(AppUtil.getPrimaryThemColorAsInt());

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
        } else if (attendee.getImage() != null) {
            Bitmap bm2 = BitmapFactory.decodeByteArray(attendee.getImage(), 0, attendee.getImage().length);

            ivProfile.setImageBitmap(bm2);
        }

        TextView tvAttendeeName = (TextView) rootView.findViewById(R.id.tvAttendeeName);
        tvAttendeeName.setText(attendee.getName());

        elvAttendeeDetailInfo = (ExpandableListView) rootView.findViewById(R.id.elvAttendeeInfo);

        getAttendeeDetailListData();

        elvAttendeeDetailInfo.setAdapter(adapter);

        for (int i = 0; i < adapter.getGroupCount(); ++i) {
            elvAttendeeDetailInfo.expandGroup(i);
        }

        return rootView;
    }

    public void getAttendeeDetailListData() {
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
