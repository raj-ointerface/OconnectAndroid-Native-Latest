package com.ointerface.oconnect.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.ConferenceListViewActivity;
import com.ointerface.oconnect.ConferenceListViewAdapter;
import com.ointerface.oconnect.CustomSplashActivity;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.adapters.DashboardEventListViewAdapter;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.Organization;
import com.ointerface.oconnect.data.Session;
import com.ointerface.oconnect.util.AppUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class DashboardActivity extends OConnectBaseActivity {

    private TextView tvTwitterText;
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
        super.onCreateDrawer();

        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);

        tvToolbarTitle.setText("");

        if (selectedConference.isShowDashboard() == false) {
            Intent i = new Intent(DashboardActivity.this, ScheduleActivity.class);
            startActivity(i);
            finish();
        }

        /*
        if (AppUtil.getIsSignedIn(DashboardActivity.this) == false) {
            Intent i = new Intent(DashboardActivity.this, SignInActivity1.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        }
        */

        tvConferenceTitle = (TextView) findViewById(R.id.tvConferenceTitle);

        tvConferenceTitle.setText(selectedConference.getName());

        tvTwitterText = (TextView) findViewById(R.id.tvTwitterText);

        tvTwitterText.getBackground().setAlpha(60);

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
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        removeFirstHeaderInNav();
    }

    public void getListViewData() {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<Session> sessionResults = realm.where(Session.class).equalTo("conference", AppUtil.getSelectedConferenceID(DashboardActivity.this)).findAllSorted("startTime", true);

        eventsArr = new ArrayList<Event>();
        sessionNamesArr = new ArrayList<String>();

        for (int i = 0; i < sessionResults.size(); ++i) {
            Session currentSession = sessionResults.get(i);

            RealmResults<Event> eventResults = realm.where(Event.class).findAllSorted("startTime", true);

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
        AlertDialog alertDialog = new AlertDialog.Builder(DashboardActivity.this).create();
        alertDialog.setTitle("Not Available");
        alertDialog.setMessage("This feature is unavailable.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void threeDotsClicked(View view) {
        Intent i = new Intent(DashboardActivity.this, ScheduleActivity.class);
        startActivity(i);
    }
}