package com.ointerface.oconnect.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
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
import com.ointerface.oconnect.adapters.ScheduleSwipeListAdapter;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.Session;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.data.SpeakerEventCache;
import com.ointerface.oconnect.fragments.EventDetailViewFragment;
import com.ointerface.oconnect.fragments.OverlayDialogFragment;
import com.ointerface.oconnect.util.AppConfig;
import com.ointerface.oconnect.util.AppUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeSet;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.view.View.GONE;

public class ScheduleActivity extends OConnectBaseActivity {
    private SearchView scheduleSearch;

    private List<String> listDataHeader;
    private List<Session> listSessionHeader;
    private HashMap<String, List<Event>> listDataChild;

    // private ArrayList<Event> eventsArr;
    // private ArrayList<String> sessionNamesArr;

    private ListView lvEvents;
    private ScheduleSwipeListAdapter adapter;

    private Date currentScheduleDate;

    private TextView tvCurrentDay;

    private ImageView ivLeftArrow;
    private ImageView ivRightArrow;

    public ArrayList<RealmObject> mData = new ArrayList<RealmObject>();
    public TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    public ArrayList<Boolean> mIsExpandedArray = new ArrayList<Boolean>();

    static public boolean isEventType = false;

    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        super.onCreateDrawer();

        if (selectedConference != null && selectedConference.getType().equalsIgnoreCase("Event")) {
            isEventType = true;
        }

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

        ivProfileLanyard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtil.getIsSignedIn(ScheduleActivity.this) == false) {
                    AppUtil.displayPleaseSignInDialog(ScheduleActivity.this);
                    return;
                }

                Intent i = new Intent(ScheduleActivity.this, ParticipantsActivity.class);
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

        ImageView closeButton = (ImageView) scheduleSearch.findViewById(R.id.search_close_btn);

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
            RealmResults<Session> results = realm.where(Session.class).equalTo("conference", AppUtil.getSelectedConferenceID(ScheduleActivity.this)).findAllSorted("startTime", Sort.ASCENDING);

            if (results.size() > 0) {
                Session session = results.first();

                currentScheduleDate = session.getStartTime();

            }
        } else {
            currentScheduleDate = now;

            // we set the schedule date to the start of day so that entire day is displayed
            currentScheduleDate = AppUtil.setTime(currentScheduleDate, 0, 0, 0, 0);
        }

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
                            if (adapter.hiddenPositions.contains(i) == false) {
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

                    Intent i = new Intent(ScheduleActivity.this, EventDetailViewActivity.class);
                    i.putExtra("EVENT_NUMBER", newPosition);
                    startActivity(i);
                }
            }
        });

        setScheduleNavigationArrows();

        OverlayDialogFragment.schedule2AnchorView = ivRightToolbarIcon;
        OverlayDialogFragment.schedule3AnchorView = getToolbarNavigationIcon((Toolbar) findViewById(R.id.toolbar));
        OverlayDialogFragment.schedule4AnchorView = ivProfileLanyard;

        if (AppUtil.getScheduleTutorialShown(this) == false && AppConfig.bScheduleTutorialShown == false) {
            FragmentManager fm = getSupportFragmentManager();
            OverlayDialogFragment dialogFragment = OverlayDialogFragment.newInstance(this, OverlayDialogFragment.OverlayType.Schedule1);
            dialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDialog);
            dialogFragment.show(fm, OverlayDialogFragment.OverlayType.Schedule1.name());
            AppConfig.bScheduleTutorialShown = true;
        }

        scheduleSearch = (SearchView) findViewById(R.id.scheduleSearch);

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

    public void getListViewData(Date newSelectedDate) {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<Session> sessionResults;

        if (newSelectedDate != null) {
            sessionResults = realm.where(Session.class).equalTo("conference", AppUtil.getSelectedConferenceID(ScheduleActivity.this)).findAllSorted("startTime", Sort.ASCENDING);
        } else {
            sessionResults = realm.where(Session.class).equalTo("conference", AppUtil.getSelectedConferenceID(ScheduleActivity.this)).findAllSorted("startTime", Sort.ASCENDING);
        }

        adapter = new ScheduleSwipeListAdapter(ScheduleActivity.this);

        newSelectedDate = AppUtil.setTime(newSelectedDate, 12, 0, 0, 0);

        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM d");

        if (isEventType == true) {
            df = new SimpleDateFormat("MMMM");
        }

        tvCurrentDay = (TextView) findViewById(R.id.tvCurrentDay);

        tvCurrentDay.setText(df.format(newSelectedDate));

        RealmList<Event> myAgendaList = new RealmList<Event>();

        if (currentPerson != null) {
            myAgendaList = currentPerson.getFavoriteEvents();
        }

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

            RealmResults<Event> eventResults = realm.where(Event.class).equalTo("session", currentSession.getObjectId()).findAllSorted("startTime", Sort.ASCENDING);

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
        if (searchText == null || searchText.length() == 0) {
            return;

        }
        searchText = searchText.toLowerCase();

        String[] searchArr = searchText.split(" ");

        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<Session> sessionResults;

        if (currentScheduleDate != null) {
            sessionResults = realm.where(Session.class).equalTo("conference", AppUtil.getSelectedConferenceID(ScheduleActivity.this)).findAllSorted("startTime", Sort.ASCENDING);
        } else {
            sessionResults = realm.where(Session.class).equalTo("conference", AppUtil.getSelectedConferenceID(ScheduleActivity.this)).findAllSorted("startTime", Sort.ASCENDING);
        }

        adapter = new ScheduleSwipeListAdapter(ScheduleActivity.this);

        currentScheduleDate = AppUtil.setTime(currentScheduleDate, 12, 0, 0, 0);

        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM d");

        if (isEventType == true) {
            df = new SimpleDateFormat("MMMM");
        }

        tvCurrentDay = (TextView) findViewById(R.id.tvCurrentDay);

        tvCurrentDay.setText(df.format(currentScheduleDate));

        RealmList<Event> myAgendaList = new RealmList<Event>();

        if (currentPerson != null) {
            myAgendaList = currentPerson.getFavoriteEvents();
        }

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

            RealmResults<Event> eventResults = realm.where(Event.class).equalTo("session", currentSession.getObjectId()).findAllSorted("startTime", Sort.ASCENDING);

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

        lvEvents.setAdapter(adapter);
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

    public View getToolbarNavigationIcon(Toolbar toolbar){
        //check if contentDescription previously was set
        boolean hadContentDescription = TextUtils.isEmpty(toolbar.getNavigationContentDescription());
        String contentDescription = !hadContentDescription ? toolbar.getNavigationContentDescription().toString() : "navigationIcon";
        toolbar.setNavigationContentDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews,contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setNavigationContentDescription ensures its existence
        View navIcon = null;
        if(potentialViews.size() > 0){
            navIcon = potentialViews.get(0); //navigation icon is ImageButton
        }
        //Clear content description if not previously present
        if(hadContentDescription)
            toolbar.setNavigationContentDescription(null);
        return navIcon;
    }
}
