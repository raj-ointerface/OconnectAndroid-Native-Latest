package com.ointerface.oconnect.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.adapters.DashboardEventListViewAdapter;
import com.ointerface.oconnect.adapters.ScheduleExpandableListViewAdapter;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.Session;
import com.ointerface.oconnect.util.AppUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.view.View.GONE;

public class ScheduleActivity extends OConnectBaseActivity {
    private SearchView scheduleSearch;

    private List<String> listDataHeader;
    private List<Session> listSessionHeader;
    private HashMap<String, List<Event>> listDataChild;

    // private ArrayList<Event> eventsArr;
    // private ArrayList<String> sessionNamesArr;

    private ExpandableListView lvEvents;
    private ScheduleExpandableListViewAdapter adapter;

    private Date currentScheduleDate;

    private TextView tvCurrentDay;

    private ImageView ivLeftArrow;
    private ImageView ivRightArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        super.onCreateDrawer();

        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);

        if (selectedConference.getToolbarLabelSchedule() != null &&
                !selectedConference.getToolbarLabelSchedule().equalsIgnoreCase("")) {
            tvToolbarTitle.setText(selectedConference.getToolbarLabelSchedule());
        } else {
            tvToolbarTitle.setText("Schedule");
        }

        ivSearch.setVisibility(GONE);
        ivProfileLanyard.setVisibility(View.VISIBLE);
        ivHelp.setVisibility(View.VISIBLE);

        scheduleSearch = (SearchView) findViewById(R.id.scheduleSearch);

        scheduleSearch.setActivated(true);
        scheduleSearch.setQueryHint("Search");

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        scheduleSearch.setIconified(false);
        scheduleSearch.clearFocus();

        scheduleSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleSearch.setIconified(false);
            }
        });

        scheduleSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                scheduleSearch.setIconified(false);
                return false;
            }
        });

        ImageView closeButton = (ImageView)scheduleSearch.findViewById(R.id.search_close_btn);

        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.search_src_text);

                et.setText("");

                scheduleSearch.clearFocus();

                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                // search.setIconified(false);
            }
        });

        LinearLayout linearLayout1 = (LinearLayout) scheduleSearch.getChildAt(0);
        LinearLayout linearLayout2 = (LinearLayout) linearLayout1.getChildAt(2);
        LinearLayout linearLayout3 = (LinearLayout) linearLayout2.getChildAt(1);
        AutoCompleteTextView autoComplete = (AutoCompleteTextView) linearLayout3.getChildAt(0);
        autoComplete.setTextSize(14);
        autoComplete.setBackground(getResources().getDrawable(R.drawable.search_view));

        autoComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleSearch.setIconified(false);
            }
        });

        linearLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleSearch.setIconified(false);
            }
        });

        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleSearch.setIconified(false);
            }
        });

        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleSearch.setIconified(false);
            }
        });

        ivLeftArrow = (ImageView) findViewById(R.id.ivLeftArrow);
        ivRightArrow = (ImageView) findViewById(R.id.ivRightArrow);

        // if current day is during conference, then display that day
        Date now = new Date();

        if (now.before(selectedConference.getStartTime()) ||
                now.after(selectedConference.getEndTime())) {
            Realm realm = AppUtil.getRealmInstance(App.getInstance());
            RealmResults<Session> results = realm.where(Session.class).equalTo("conference", AppUtil.getSelectedConferenceID(ScheduleActivity.this)).findAllSorted("startTime", true);

            if (results.size() > 0) {
                Session session = results.first();

                currentScheduleDate = session.getStartTime();

                /*
                Date utcDate;

                try {
                    // utcDate = sdf.parse(session.getStartTime().toString());

                    // currentScheduleDate = session.getStartTime();
                } catch (Exception ex) {
                    Log.d("Schedule", ex.getMessage());
                }
                */
            }
        } else {
            currentScheduleDate = now;
            // we set the schedule date to the start of day so that entire day is displayed
            currentScheduleDate = AppUtil.setTime(currentScheduleDate, 0, 0, 0, 0 );
        }

        /*
        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM d");

        tvCurrentDay = (TextView) findViewById(R.id.tvCurrentDay);

        tvCurrentDay.setText(df.format(currentScheduleDate));
        */

        getListViewData(currentScheduleDate);

        adapter = new ScheduleExpandableListViewAdapter(ScheduleActivity.this, listDataHeader, listSessionHeader, listDataChild);

        lvEvents = (ExpandableListView) findViewById(R.id.elvSchedule);

        lvEvents.setAdapter(adapter);

        for (int i = 0; i < listDataHeader.size(); ++i) {
            lvEvents.expandGroup(i);
        }

        setScheduleNavigationArrows();
    }

    public void getListViewData(Date newSelectedDate) {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<Session> sessionResults;

        if (newSelectedDate != null) {
            sessionResults = realm.where(Session.class).equalTo("conference", AppUtil.getSelectedConferenceID(ScheduleActivity.this)).findAllSorted("startTime", true);
        } else {
            sessionResults = realm.where(Session.class).equalTo("conference", AppUtil.getSelectedConferenceID(ScheduleActivity.this)).findAllSorted("startTime", true);
        }

        listDataHeader = new ArrayList<String>();
        listSessionHeader = new ArrayList<Session>();
        listDataChild = new HashMap<String, List<Event>>();

        newSelectedDate = AppUtil.setTime(newSelectedDate, 12, 0, 0, 0);

        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM d");

        tvCurrentDay = (TextView) findViewById(R.id.tvCurrentDay);

        tvCurrentDay.setText(df.format(newSelectedDate));

        for (int i = 0; i < sessionResults.size(); ++i) {
            List<Event> currentEventsList = new ArrayList<Event>();

            Session currentSession = sessionResults.get(i);

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(newSelectedDate);
            cal2.setTime(currentSession.getStartTime());
            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

            if (sameDay == false) {
                continue;
            }

            // currentScheduleDate = AppUtil.setTime(currentScheduleDate, 12, 0, 0, 0);

            /*
            if (((currentSession.getStartTime().before(currentScheduleDate) || currentSession.getStartTime().compareTo(currentScheduleDate) == 0) &&
                    (currentSession.getEndTime().after(currentScheduleDate) ||
                    currentSession.getEndTime().compareTo(currentScheduleDate) == 0))) {
            */
                listDataHeader.add(currentSession.getTrack());
                listSessionHeader.add(currentSession);

                RealmResults<Event> eventResults = realm.where(Event.class).findAllSorted("startTime", true);

                for (int j = 0; j < eventResults.size(); ++j) {
                    Event currentEvent = eventResults.get(j);

                    if (currentEvent.getSession().equalsIgnoreCase(currentSession.getObjectId())) {
                        currentEventsList.add(currentEvent);
                    }
                }

            // }

            listDataChild.put(currentSession.getTrack(), currentEventsList);
        }
    }

    public void leftArrowClicked(View view) {
        Date conferenceFirstDayNoTime = AppUtil.setTime(selectedConference.getStartTime(), 0, 0, 0, 0);

        Date currentScheduleDateNoTime = AppUtil.setTime(currentScheduleDate, 0, 0, 0, 0);

        if (currentScheduleDateNoTime.compareTo(conferenceFirstDayNoTime) == 0) {
            return;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(currentScheduleDate);
        c.add(Calendar.DATE, -1);  // number of days to add
        // dt = sdf.format(c.getTime());  // dt is now the new date
        currentScheduleDate = c.getTime();

        getListViewData(currentScheduleDate);

        adapter = new ScheduleExpandableListViewAdapter(ScheduleActivity.this, listDataHeader, listSessionHeader, listDataChild);

        lvEvents.setAdapter(adapter);

        for (int i = 0; i < listDataHeader.size(); ++i) {
            lvEvents.expandGroup(i);
        }

        setScheduleNavigationArrows();
    }

    public void rightArrowClicked(View view) {
        Date conferenceLastDayNoTime = AppUtil.setTime(selectedConference.getEndTime(), 0, 0, 0, 0);

        Date currentScheduleDateNoTime = AppUtil.setTime(currentScheduleDate, 0, 0, 0, 0);

        if (currentScheduleDateNoTime.compareTo(conferenceLastDayNoTime) == 0) {
            return;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(currentScheduleDate);
        c.add(Calendar.DATE, 1);  // number of days to add
        // dt = sdf.format(c.getTime());  // dt is now the new date
        currentScheduleDate = c.getTime();

        getListViewData(currentScheduleDate);

        adapter = new ScheduleExpandableListViewAdapter(ScheduleActivity.this, listDataHeader, listSessionHeader, listDataChild);

        lvEvents.setAdapter(adapter);

        for (int i = 0; i < listDataHeader.size(); ++i) {
            lvEvents.expandGroup(i);
        }

        setScheduleNavigationArrows();
    }

    public void setScheduleNavigationArrows () {
        Date conferenceFirstDayNoTime = AppUtil.setTime(selectedConference.getStartTime(), 0, 0, 0, 0);
        Date conferenceLastDayNoTime = AppUtil.setTime(selectedConference.getEndTime(), 0, 0, 0, 0);

        Date currentScheduleDateNoTime = AppUtil.setTime(currentScheduleDate, 0, 0, 0, 0);

        if (currentScheduleDateNoTime.compareTo(conferenceFirstDayNoTime) == 0) {
            ivLeftArrow.setBackgroundResource(R.drawable.icon_left_arrow_inactive);
        } else {
            ivLeftArrow.setBackgroundResource(R.drawable.icon_left_arrow_active);
        }

        if (currentScheduleDateNoTime.compareTo(conferenceLastDayNoTime) == 0) {
            ivRightArrow.setBackgroundResource(R.drawable.icon_right_arrow_inactive);
        } else {
            ivRightArrow.setBackgroundResource(R.drawable.icon_right_arrow_active);
        }
    }
}
