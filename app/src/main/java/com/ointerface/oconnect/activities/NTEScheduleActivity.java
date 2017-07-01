package com.ointerface.oconnect.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.adapters.NTEScheduleSwipeListAdapter;
import com.ointerface.oconnect.adapters.ScheduleSwipeListAdapter;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.Session;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.data.SpeakerEventCache;
import com.ointerface.oconnect.util.AppConfig;
import com.ointerface.oconnect.util.AppUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.view.View.GONE;

public class NTEScheduleActivity extends OConnectBaseActivity {
    private SearchView scheduleSearch;

    private List<String> listDataHeader;
    private List<Session> listSessionHeader;
    private HashMap<String, List<Event>> listDataChild;

    // private ArrayList<Event> eventsArr;
    // private ArrayList<String> sessionNamesArr;

    private ListView lvEvents;
    private NTEScheduleSwipeListAdapter adapter;

    private boolean bDisplayByName = true;

    private Date currentScheduleDate;

    private TextView tvCurrentDay;

    private ImageView ivLeftArrow;
    private ImageView ivRightArrow;

    public ArrayList<RealmObject> mData = new ArrayList<RealmObject>();
    public TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    public ArrayList<Boolean> mIsExpandedArray = new ArrayList<Boolean>();

    static public boolean isEventType = false;

    public ProgressDialog dialog;

    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nteschedule);
        super.onCreateDrawer();

        /*
        if (selectedConference.getType().equalsIgnoreCase("Event")) {
            isEventType = true;
        }
        */

        isEventType = true;

        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);

        if (selectedConference.getToolbarLabelNonTimedEvent() != null &&
                !selectedConference.getToolbarLabelNonTimedEvent().equalsIgnoreCase("")) {
            tvToolbarTitle.setText(selectedConference.getToolbarLabelNonTimedEvent());
        } else {
            tvToolbarTitle.setText("Schedule");
        }

        ivSearch.setVisibility(GONE);
        ivProfileLanyard.setVisibility(View.VISIBLE);
        ivHelp.setVisibility(View.VISIBLE);

        ivProfileLanyard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtil.getIsSignedIn(NTEScheduleActivity.this) == false) {
                    AppUtil.displayPleaseSignInDialog(NTEScheduleActivity.this);
                    return;
                }

                Intent i = new Intent(NTEScheduleActivity.this, ParticipantsActivity.class);
                startActivity(i);
            }
        });

        scheduleSearch = (SearchView) findViewById(R.id.scheduleSearch);

        scheduleSearch.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());

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
            RealmResults<Session> results = realm.where(Session.class).equalTo("conference", AppUtil.getSelectedConferenceID(NTEScheduleActivity.this)).findAllSorted("startTime", Sort.ASCENDING);

            if (results.size() > 0) {
                Session session = results.first();

                currentScheduleDate = session.getStartTime();

            }
        } else {
            currentScheduleDate = now;

            // we set the schedule date to the start of day so that entire day is displayed
            currentScheduleDate = AppUtil.setTime(currentScheduleDate, 0, 0, 0, 0 );
        }

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] { android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed},  // pressed
                new int[] { android.R.attr.state_selected}  // selected
        };

        int[] colors = new int[] {
                Color.BLACK,
                Color.BLACK,
                Color.WHITE,
                Color.WHITE,
                Color.WHITE
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);

        navigation.setItemTextColor(colorStateList);

        View view1 = navigation.findViewById(R.id.navigation_by_name);

        view1.setPadding(0,0,0,30);

        View view2 = navigation.findViewById(R.id.navigation_by_location);

        view2.setPadding(0,0,0,30);

        Menu menu = navigation.getMenu();

        getListViewData(currentScheduleDate);

        lvEvents = (ListView) findViewById(R.id.elvSchedule);

        lvEvents.setAdapter(adapter);

        lvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItemViewType(position) == ScheduleSwipeListAdapter.TYPE_SEPARATOR) {
                    if (adapter.mIsExpandedArray.get(position) == true) {
                        adapter.mIsExpandedArray.set(position, false);

                        for (int i = position + 1; i < adapter.mData.size() &&
                                adapter.getItemViewType(i) != ScheduleSwipeListAdapter.TYPE_SEPARATOR;
                             ++i) {
                            if (adapter.hiddenPositions.contains(i) ==  false) {
                                adapter.hiddenPositions.add(i);
                                adapter.mIsExpandedArray.set(i, false);
                            }
                        }
                    } else {
                        adapter.mIsExpandedArray.set(position, true);

                        ArrayList<Integer> itemsToAddBack = new ArrayList<Integer>();

                        for (int i = position + 1; i < adapter.mData.size() &&
                                adapter.getItemViewType(i) != ScheduleSwipeListAdapter.TYPE_SEPARATOR;
                             ++i) {
                            itemsToAddBack.add(i);
                        }

                        for (int d = itemsToAddBack.size() - 1; d >= 0; --d) {
                            if (adapter.hiddenPositions.contains(itemsToAddBack.get(d))) {
                                adapter.hiddenPositions.remove(itemsToAddBack.get(d));
                                adapter.mIsExpandedArray.set(itemsToAddBack.get(d), true);
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();

                } else {
                    RealmObject curObject = adapter.mData.get(position);
                    if (curObject instanceof Session) {
                        return;
                    }

                    // Goto Event Detail View
                    EventDetailViewActivity.mItems = new ArrayList<RealmObject>();

                    Event event = (Event) adapter.mData.get(position);

                    int newPosition = 0;

                    int j = 0;

                    for (int i = 0; i < adapter.mData.size(); ++i) {
                        RealmObject curObj = adapter.mData.get(i);
                        if (!(curObj instanceof Session)) {
                            EventDetailViewActivity.mItems.add(curObj);

                            Event currentEvent = (Event) curObj;

                            if (event.getObjectId().equalsIgnoreCase(currentEvent.getObjectId())) {
                                newPosition = j;
                            }

                            j += 1;
                        }
                    }

                    Intent i = new Intent(NTEScheduleActivity.this, EventDetailViewActivity.class);
                    i.putExtra("EVENT_NUMBER", newPosition);
                    startActivity(i);
                }
            }
        });

        setScheduleNavigationArrows();

        navigation.setSelectedItemId(R.id.navigation_by_name);

        displayByName();

        scheduleSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return false;
            }
        });
    }

    public void displayByName() {
        bDisplayByName = true;

        getListViewData(currentScheduleDate);

        lvEvents.setAdapter(adapter);
    }

    public void displayByLocation() {
        bDisplayByName = false;

        getListViewData(currentScheduleDate);

        lvEvents.setAdapter(adapter);
    }

    public void getListViewData(Date newSelectedDate) {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<Session> sessionResults;

        if (newSelectedDate != null) {
            sessionResults = realm.where(Session.class).equalTo("conference", AppUtil.getSelectedConferenceID(NTEScheduleActivity.this)).findAllSorted("startTime", Sort.ASCENDING);
        } else {
            sessionResults = realm.where(Session.class).equalTo("conference", AppUtil.getSelectedConferenceID(NTEScheduleActivity.this)).findAllSorted("startTime", Sort.ASCENDING);
        }

        adapter = new NTEScheduleSwipeListAdapter(NTEScheduleActivity.this);

        newSelectedDate = AppUtil.setTime(newSelectedDate, 12, 0, 0, 0);

        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM d");

        if (isEventType == true) {
            df = new SimpleDateFormat("MMMM");
        }

        tvCurrentDay = (TextView) findViewById(R.id.tvCurrentDay);

        tvCurrentDay.setText(df.format(newSelectedDate));

        RealmList<Event> myAgendaList = currentPerson.getFavoriteEvents();

        for (int i = 0; i < sessionResults.size(); ++i) {
            List<Event> currentEventsList = new ArrayList<Event>();

            Session currentSession = sessionResults.get(i);

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(newSelectedDate);
            cal2.setTime(currentSession.getStartTime());
            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

            boolean sameMonth = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);

            if (isEventType == true) {
                if (sameMonth == false) {
                    continue;
                }
            } else {
                if (sameDay == false) {
                    continue;
                }
            }

            adapter.addSectionHeaderItem(currentSession);

            RealmResults<Event> eventResults;

            if (bDisplayByName) {
                eventResults = realm.where(Event.class).equalTo("session", currentSession.getObjectId()).findAllSorted("name", Sort.ASCENDING);
            } else {
                eventResults = realm.where(Event.class).equalTo("session", currentSession.getObjectId()).findAllSorted("location", Sort.ASCENDING);
            }

            for (int j = 0; j < eventResults.size(); ++j) {
                Event currentEvent = eventResults.get(j);

                adapter.addItem(currentEvent);

                /*
                if (currentEvent.getSession().equalsIgnoreCase(currentSession.getObjectId())) {
                    adapter.addItem(currentEvent);
                }
                */

                for (int c = 0; c < myAgendaList.size(); ++c) {
                    Event agendaEvent = myAgendaList.get(c);

                    if (agendaEvent.getObjectId().equalsIgnoreCase(currentEvent.getObjectId())) {
                        adapter.myEventsPositionsByUser.add(adapter.mData.size() - 1);
                    }
                }
            }

            for (int k = 0; k < adapter.mData.size(); ++k) {
                adapter.mIsExpandedArray.add(true);
            }

        }
    }

    public void performSearch(String searchText) {
        searchText = searchText.toLowerCase();

        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<Session> sessionResults;

        if (currentScheduleDate != null) {
            sessionResults = realm.where(Session.class).equalTo("conference", AppUtil.getSelectedConferenceID(NTEScheduleActivity.this)).findAllSorted("startTime", Sort.ASCENDING);
        } else {
            sessionResults = realm.where(Session.class).equalTo("conference", AppUtil.getSelectedConferenceID(NTEScheduleActivity.this)).findAllSorted("startTime", Sort.ASCENDING);
        }

        adapter = new NTEScheduleSwipeListAdapter(NTEScheduleActivity.this);

        currentScheduleDate = AppUtil.setTime(currentScheduleDate, 12, 0, 0, 0);

        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM d");

        if (isEventType == true) {
            df = new SimpleDateFormat("MMMM");
        }

        tvCurrentDay = (TextView) findViewById(R.id.tvCurrentDay);

        tvCurrentDay.setText(df.format(currentScheduleDate));

        RealmList<Event> myAgendaList = currentPerson.getFavoriteEvents();

        for (int i = 0; i < sessionResults.size(); ++i) {
            List<Event> currentEventsList = new ArrayList<Event>();

            Session currentSession = sessionResults.get(i);

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(currentScheduleDate);
            cal2.setTime(currentSession.getStartTime());
            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

            boolean sameMonth = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);

            if (isEventType == true) {
                if (sameMonth == false) {
                    continue;
                }
            } else {
                if (sameDay == false) {
                    continue;
                }
            }

            adapter.addSectionHeaderItem(currentSession);

            RealmResults<Event> eventResults;

            if (bDisplayByName) {
                eventResults = realm.where(Event.class).equalTo("session", currentSession.getObjectId()).findAllSorted("name", Sort.ASCENDING);
            } else {
                eventResults = realm.where(Event.class).equalTo("session", currentSession.getObjectId()).findAllSorted("location", Sort.ASCENDING);
            }

            for (int j = 0; j < eventResults.size(); ++j) {
                boolean bIncludeInResults = false;

                Event currentEvent = eventResults.get(j);

                RealmResults<SpeakerEventCache> speakerEventCache = realm.where(SpeakerEventCache.class).equalTo("eventID", currentEvent.getObjectId()).findAll();

                for (int l = 0; l < speakerEventCache.size(); ++l) {
                    SpeakerEventCache speakerEvent = speakerEventCache.get(l);

                    RealmResults<Speaker> speakers = realm.where(Speaker.class).equalTo("objectId", speakerEvent.getSpeakerID()).findAll();

                    for (int m = 0; m < speakers.size(); ++m) {
                        Speaker speaker = speakers.get(m);
                        if (speaker.getName().toLowerCase().contains(searchText)) {
                            bIncludeInResults = true;
                            break;
                        }
                    }
                }

                if (currentEvent.getName().toLowerCase().contains(searchText) ||
                        currentEvent.getLocation().toLowerCase().contains(searchText)) {
                    bIncludeInResults = true;
                }

                if (bIncludeInResults == true) {
                    adapter.addItem(currentEvent);
                }


                for (int c = 0; c < myAgendaList.size(); ++c) {
                    Event agendaEvent = myAgendaList.get(c);

                    if (agendaEvent.getObjectId().equalsIgnoreCase(currentEvent.getObjectId())) {
                        adapter.myEventsPositionsByUser.add(adapter.mData.size() - 1);
                    }
                }
            }

            for (int k = 0; k < adapter.mData.size(); ++k) {
                adapter.mIsExpandedArray.add(true);
            }

        }
    }

    public void leftArrowClicked(View view) {

        Date conferenceFirstDayNoTime = AppUtil.setTime(selectedConference.getStartTime(), 0, 0, 0, 0);

        Date currentScheduleDateNoTime = AppUtil.setTime(currentScheduleDate, 0, 0, 0, 0);

        if (currentScheduleDateNoTime.compareTo(conferenceFirstDayNoTime) == 0) {
            return;
        }

        if (isEventType == true) {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(selectedConference.getStartTime());
            cal2.setTime(currentScheduleDate);
            boolean sameMonth = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);

            if (sameMonth) {
                return;
            }
        }

        //dialog = ProgressDialog.show(ScheduleActivity.this, "",
        //        "Please wait", true);

        Calendar c = Calendar.getInstance();
        c.setTime(currentScheduleDate);

        if (isEventType == true) {
            c.add(Calendar.MONTH, -1);
        } else {
            c.add(Calendar.DATE, -1);  // number of days to add
        }

        currentScheduleDate = c.getTime();

        getListViewData(currentScheduleDate);

        lvEvents.setAdapter(adapter);

        setScheduleNavigationArrows();

        // dialog.hide();
    }

    public void rightArrowClicked(View view) {

        Date conferenceLastDayNoTime = AppUtil.setTime(selectedConference.getEndTime(), 0, 0, 0, 0);

        Date currentScheduleDateNoTime = AppUtil.setTime(currentScheduleDate, 0, 0, 0, 0);

        if (currentScheduleDateNoTime.compareTo(conferenceLastDayNoTime) == 0) {
            return;
        }

        if (isEventType == true) {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(selectedConference.getEndTime());
            cal2.setTime(currentScheduleDate);
            boolean sameMonth = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);

            if (sameMonth) {
                return;
            }
        }

        //dialog = ProgressDialog.show(ScheduleActivity.this, "",
        //        "Please wait", true);

        Calendar c = Calendar.getInstance();
        c.setTime(currentScheduleDate);

        if (isEventType == true) {
            c.add(Calendar.MONTH, 1);
        } else {
            c.add(Calendar.DATE, 1);  // number of days to add
        }

        currentScheduleDate = c.getTime();

        getListViewData(currentScheduleDate);

        lvEvents.setAdapter(adapter);

        setScheduleNavigationArrows();

        // dialog.hide();
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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Menu menu = navigation.getMenu();

            View byNameView = navigation.findViewById(R.id.navigation_by_name);

            if (byNameView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) byNameView.getLayoutParams();
                p.setMargins(0, 0, 0, 0);
                byNameView.requestLayout();
            }

            View byLocationView = navigation.findViewById(R.id.navigation_by_location);

            if (byLocationView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) byLocationView.getLayoutParams();
                p.setMargins(0, 0, 0, 0);
                byLocationView.requestLayout();
            }

            switch (item.getItemId()) {
                case R.id.navigation_by_name:
                    byNameView.setBackgroundColor(AppConfig.lightGreyColor);

                    byLocationView.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());

                    displayByName();
                    return true;
                case R.id.navigation_by_location:
                    byNameView.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());

                    byLocationView.setBackgroundColor(AppConfig.lightGreyColor);

                    displayByLocation();
                    return true;
            }
            return false;
        }

    };
}
