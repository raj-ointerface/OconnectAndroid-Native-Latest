package com.ointerface.oconnect.activities;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkedin.platform.AccessToken;
import com.ointerface.oconnect.App;
import com.ointerface.oconnect.ConferenceListViewActivity;
import com.ointerface.oconnect.ConferenceListViewAdapter;
import com.ointerface.oconnect.CustomSplashActivity;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.adapters.DashboardEventListViewAdapter;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.Organization;
import com.ointerface.oconnect.data.Session;
import com.ointerface.oconnect.util.AppConfig;
import com.ointerface.oconnect.util.AppUtil;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.http.Query;

import static android.view.View.GONE;

public class DashboardActivity extends OConnectBaseActivity {

    private TextView tvTwitterText;
    private ImageView ivTwitter;
    private TextView tvConferenceTitle;

    private ArrayList<Event> eventsArr;
    private ArrayList<String> sessionNamesArr;

    private ListView lvEvents;
    private DashboardEventListViewAdapter adapter;

    private LinearLayout llMessageSpeakers;
    private LinearLayout llAlerts;
    private LinearLayout llAddANote;
    private LinearLayout llMyAgenda;
    private LinearLayout llMakeConnections;
    private LinearLayout llInfo;
    private RelativeLayout rlSchedulePreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        isTransparentToolbar = true;

        super.onCreateDrawer();

        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);

        tvToolbarTitle.setText("");

        if (selectedConference != null && selectedConference.isShowDashboard() == false) {
            Intent i = new Intent(DashboardActivity.this, ScheduleActivity.class);
            startActivity(i);
            finish();
        }

        tvConferenceTitle = (TextView) findViewById(R.id.tvConferenceTitle);

        ImageView ivConferenceLogo = (ImageView) findViewById(R.id.ivConferenceLogo);

        if (selectedConference != null && selectedConference.getDashboardImage() != null) {
            Bitmap bm = BitmapFactory.decodeByteArray(selectedConference.getDashboardImage(), 0, selectedConference.getDashboardImage().length);

            ivConferenceLogo.setImageBitmap(bm);
            ivConferenceLogo.setVisibility(View.VISIBLE);
            tvConferenceTitle.setVisibility(GONE);
        } else if (selectedConference != null) {
            tvConferenceTitle.setText(selectedConference.getName());
            ivConferenceLogo.setVisibility(GONE);
            tvConferenceTitle.setVisibility(View.VISIBLE);
        }

        tvTwitterText = (TextView) findViewById(R.id.tvTwitterText);

        tvTwitterText.getBackground().setAlpha(60);

        ivTwitter = (ImageView) findViewById(R.id.ivTwitter);

        if (selectedConference != null && selectedConference.getHashtag() != null && !selectedConference.getHashtag().equalsIgnoreCase("#")) {
            getLatestTweetForHashTag(selectedConference.getHashtag());
        } else {
            tvTwitterText.setVisibility(GONE);
            ivTwitter.setVisibility(GONE);
        }

        llMessageSpeakers = (LinearLayout) findViewById(R.id.llMessageSpeakers);

        llMessageSpeakers.getBackground().setAlpha(60);

        llAlerts = (LinearLayout) findViewById(R.id.llAlerts);

        llAlerts.getBackground().setAlpha(60);

        llAddANote = (LinearLayout) findViewById(R.id.llAddANote);

        llAddANote.getBackground().setAlpha(60);

        llMyAgenda = (LinearLayout) findViewById(R.id.llMyAgenda);

        llMyAgenda.getBackground().setAlpha(60);

        llMakeConnections = (LinearLayout) findViewById(R.id.llMakeConnections);

        llMakeConnections.getBackground().setAlpha(60);

        llInfo = (LinearLayout) findViewById(R.id.llInfo);

        llInfo.getBackground().setAlpha(60);

        rlSchedulePreview = (RelativeLayout) findViewById(R.id.rlSchedulePreview);

        rlSchedulePreview.getBackground().setAlpha(60);

        getListViewData();

        adapter = new DashboardEventListViewAdapter(DashboardActivity.this, eventsArr, sessionNamesArr);

        lvEvents = (ListView) findViewById(R.id.lvEvents);

        lvEvents.setAdapter(adapter);

        Calendar cal = Calendar.getInstance();

        Date dateTimeNow = cal.getTime();

        for (int i = 0; i < adapter.eventsList.size(); ++i) {
            Event currentEvent = adapter.eventsList.get(i);

            if (currentEvent.getStartTime().before(dateTimeNow) && currentEvent.getEndTime().after(dateTimeNow)) {
                lvEvents.setSelection(i);
                break;
            }
        }

        if (adapter.eventsList.size() > 0) {
            Event currentEvent = adapter.eventsList.get(0);
            if (currentEvent.getStartTime().after(dateTimeNow)) {
                lvEvents.setSelection(0);
            } else {
                Event lastEvent = adapter.eventsList.get(adapter.eventsList.size() - 1);
                if (lastEvent.getEndTime().before(dateTimeNow)) {
                    lvEvents.setSelection(adapter.eventsList.size() - 1);
                }
            }
        }

        if (!AppUtil.getSurveyShown(this) && AppUtil.getIsSignedIn(this)) {
            CustomDialog dialog = new CustomDialog();
            dialog.showDialog(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        removeFirstHeaderInNav();
        super.onCreateDrawer();
    }

    public void getListViewData() {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<Session> sessionResults = realm.where(Session.class).equalTo("conference", AppUtil.getSelectedConferenceID(DashboardActivity.this)).findAllSorted("startTime", Sort.ASCENDING);

        eventsArr = new ArrayList<Event>();
        sessionNamesArr = new ArrayList<String>();

        for (int i = 0; i < sessionResults.size(); ++i) {
            Session currentSession = sessionResults.get(i);

            RealmResults<Event> eventResults = realm.where(Event.class).findAllSorted("startTime", Sort.ASCENDING);

            for (int j = 0; j < eventResults.size(); ++j) {
                Event currentEvent = eventResults.get(j);

                if (currentEvent.getSession().equalsIgnoreCase(currentSession.getObjectId())) {
                    eventsArr.add(currentEvent);
                    sessionNamesArr.add(currentSession.getTrack());
                }
            }

        }
    }

    public void messageSpeakersClicked(View view) {
        Intent i = new Intent(DashboardActivity.this, ParticipantsActivity.class);
        startActivity(i);
    }

    public void alertsClicked (View view) {
        Intent i = new Intent(DashboardActivity.this, AnnouncementsActivity.class);
        startActivity(i);
    }

    public void addANoteClicked (View view) {
        if (AppUtil.getIsSignedIn(DashboardActivity.this) == false) {
            AppUtil.displayPleaseSignInDialog(DashboardActivity.this);
            return;
        }
        Intent i = new Intent(DashboardActivity.this, MyNotesActivity.class);
        startActivity(i);
    }

    public void myAgendaClicked (View view) {
        if (AppUtil.getIsSignedIn(DashboardActivity.this) == false) {
            AppUtil.displayPleaseSignInDialog(DashboardActivity.this);
            return;
        }
        Intent i = new Intent(DashboardActivity.this, MyAgendaActivity.class);
        startActivity(i);
    }

    public void makeConnectionsClicked (View view) {
        if (AppUtil.getIsSignedIn(this) == false) {
            AppUtil.displayPleaseSignInForConnectionsDialog(this);
        }

        Intent i = new Intent(DashboardActivity.this, ConnectionsActivity.class);
        startActivity(i);
    }

    public void infoClicked (View view) {
        Intent i = new Intent(DashboardActivity.this, InfoActivity.class);
        startActivity(i);
    }

    public void threeDotsClicked(View view) {
        Intent i = new Intent(DashboardActivity.this, ScheduleActivity.class);
        startActivity(i);
    }

    public void getLatestTweetForHashTag(String hashTag) {
        final SearchTimeline searchTimeline = new SearchTimeline.Builder()
                .query(hashTag)
                .build();

        searchTimeline.next(null, new Callback<TimelineResult<Tweet>>() {
            @Override
            public void success(Result<TimelineResult<Tweet>> result) {
                if (result.data.items.size() > 0) {
                    tvTwitterText.setText(result.data.items.get(0).text);
                    tvTwitterText.setVisibility(View.VISIBLE);
                    ivTwitter.setVisibility(View.VISIBLE);
                } else {
                    tvTwitterText.setVisibility(GONE);
                    ivTwitter.setVisibility(GONE);
                }
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("APD", exception.getMessage());
                tvTwitterText.setVisibility(GONE);
                ivTwitter.setVisibility(GONE);
            }
        });

    }
}