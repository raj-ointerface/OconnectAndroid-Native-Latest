package com.ointerface.oconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ExpandableListView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.AttendeeDetailViewActivity;
import com.ointerface.oconnect.activities.EventDetailViewActivity;
import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.activities.SpeakerDetailViewActivity;
import com.ointerface.oconnect.adapters.SearchExpandableListViewAdapter;
import com.ointerface.oconnect.data.Attendee;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.Session;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.data.SpeakerEventCache;
import com.ointerface.oconnect.util.AppUtil;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchDialogFragment extends DialogFragment {
    ExpandableListView expSearchView;

    SearchExpandableListViewAdapter searchAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private SearchView search;

    public SearchDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchDialogFragment newInstance(String param1, String param2) {
        SearchDialogFragment fragment = new SearchDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Search Dialog");
        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);

        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        View fragmentView = inflater.inflate(R.layout.fragment_search_dialog, container, false);

        search = (SearchView) fragmentView.findViewById(R.id.search);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.equalsIgnoreCase("")) {
                    search.clearFocus();
                }

                performSearch(newText);

                return false;
            }
        });

        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getDialog().dismiss();
                return false;
            }
        });

        search.setActivated(true);
        search.setIconified(false);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setIconified(false);
            }
        });

        expSearchView = (ExpandableListView) fragmentView.findViewById(R.id.elvSearchItems);

        expSearchView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                RealmObject obj = (RealmObject) searchAdapter.getChild(groupPosition, childPosition);

                if (obj instanceof Event) {
                    Event event = (Event) obj;

                    // Goto Event Detail View
                    EventDetailViewActivity.mItems = new ArrayList<RealmObject>();

                    EventDetailViewActivity.mItems.add(obj);

                    int newPosition = 0;

                    Intent i = new Intent(getActivity(), EventDetailViewActivity.class);
                    i.putExtra("EVENT_NUMBER", newPosition);
                    startActivity(i);
                } else if (obj instanceof Speaker) {
                    Speaker speaker = (Speaker) obj;

                    SpeakerDetailViewActivity.mItems = new ArrayList<RealmObject>();

                    SpeakerDetailViewActivity.mItems.add(speaker);

                    Intent i = new Intent(getActivity(), SpeakerDetailViewActivity.class);

                    i.putExtra("SPEAKER_NUMBER", 0);

                    startActivity(i);

                } else if (obj instanceof Attendee) {
                    Attendee attendee = (Attendee) obj;

                    AttendeeDetailViewActivity.mItems = new ArrayList<RealmObject>();

                    AttendeeDetailViewActivity.mItems.add(attendee);

                    Intent i = new Intent(getActivity(), AttendeeDetailViewActivity.class);

                    i.putExtra("ATTENDEE_NUMBER", 0);

                    startActivity(i);
                }

                return false;
            }
        });

        return fragmentView;
    }

    public void performSearch(String searchText) {
        if (searchText == null || searchText.length() == 0) {
            return;
        }

        expSearchView.setVisibility(View.VISIBLE);

        searchText = searchText.toLowerCase();

        String[] searchArr = searchText.split(" ");

        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<Session> sessionResults;

        sessionResults = realm.where(Session.class).equalTo("conference", AppUtil.getSelectedConferenceID(getActivity())).findAllSorted("startTime", Sort.ASCENDING);

        RealmResults<Event> eventResults;
        RealmResults<RealmObject> personResults;

        ArrayList<RealmObject> eventsList = new ArrayList<RealmObject>();
        ArrayList<RealmObject> personsList = new ArrayList<RealmObject>();

        for (int i = 0; i < sessionResults.size(); ++i) {

            Session currentSession = sessionResults.get(i);

            eventResults = realm.where(Event.class).equalTo("session", currentSession.getObjectId()).findAllSorted("startTime", Sort.ASCENDING);

            for (int j = 0; j < eventResults.size(); ++j) {
                boolean bIncludeInResults = false;

                Event currentEvent = eventResults.get(j);

                RealmResults<SpeakerEventCache> speakerEventCache = realm.where(SpeakerEventCache.class).equalTo("eventID", currentEvent.getObjectId()).findAll();

                for (int l = 0; l < speakerEventCache.size(); ++l) {
                    SpeakerEventCache speakerEvent = speakerEventCache.get(l);

                    RealmResults<Speaker> speakers = realm.where(Speaker.class).equalTo("objectId", speakerEvent.getSpeakerID()).findAll();

                    for (int m = 0; m < speakers.size(); ++m) {
                        Speaker speaker = speakers.get(m);

                        for (String value : searchArr) {
                            if (speaker.getName().toLowerCase().contains(value)) {
                                bIncludeInResults = true;
                                break;
                            }
                        }
                    }
                }

                for (String value : searchArr) {
                    if ((currentEvent.getName() != null && currentEvent.getName().toLowerCase().contains(value)) ||
                            (currentEvent.getLocation() != null && currentEvent.getLocation().toLowerCase().contains(value))) {
                        bIncludeInResults = true;
                    }
                }

                if (bIncludeInResults == true) {
                    eventsList.add(currentEvent);
                }
            }
        }

        RealmResults<Speaker> speakerResults;

        speakerResults = realm.where(Speaker.class).equalTo("conference", AppUtil.getSelectedConferenceID(getActivity())).findAllSorted("name", Sort.ASCENDING);

        for (int i = 0; i < speakerResults.size(); ++i) {
            Speaker speaker = speakerResults.get(i);

            for (String value : searchArr) {
                if (speaker.getName().toLowerCase().contains(value)) {
                    personsList.add(speaker);
                    break;
                }
            }
        }

        RealmResults<Attendee> attendeeResults;

        attendeeResults = realm.where(Attendee.class).equalTo("conference", AppUtil.getSelectedConferenceID(getActivity())).findAllSorted("name", Sort.ASCENDING);

        for (int i = 0; i < attendeeResults.size(); ++i) {
            Attendee attendee = attendeeResults.get(i);

            for (String value : searchArr) {
                if (attendee.getName().toLowerCase().contains(value)) {
                    personsList.add(attendee);
                    break;
                }
            }
        }

        ArrayList<String> headers = new ArrayList<String>();
        headers.add("Event");
        headers.add("Attendee");
        HashMap<String, ArrayList<RealmObject>> map = new HashMap<String, ArrayList<RealmObject>>();

        map.put("Event", eventsList);
        map.put("Attendee", personsList);

        searchAdapter = new SearchExpandableListViewAdapter(getActivity(), headers, map);

        expSearchView.setAdapter(searchAdapter);

        expSearchView.expandGroup(0);
        expSearchView.expandGroup(1);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
        */
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
