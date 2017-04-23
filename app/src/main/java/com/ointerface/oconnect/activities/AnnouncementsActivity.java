package com.ointerface.oconnect.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.adapters.AnnouncementsListViewAdapter;
import com.ointerface.oconnect.adapters.ScheduleExpandableListViewAdapter;
import com.ointerface.oconnect.data.MasterNotification;
import com.ointerface.oconnect.data.Session;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static android.view.View.GONE;

public class AnnouncementsActivity extends OConnectBaseActivity {

    private ListView lvAnnouncements;
    private AnnouncementsListViewAdapter adapter;
    public ArrayList<MasterNotification> mData = new ArrayList<MasterNotification>();
    public ArrayList<Boolean> markForDeleteList = new ArrayList<Boolean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);
        super.onCreateDrawer();

        tvToolbarTitle.setText("Announcements");

        ivProfileLanyard.setVisibility(GONE);
        ivSearch.setVisibility(GONE);
        ivRightToolbarIcon.setVisibility(GONE);

        ivHelp.setVisibility(View.VISIBLE);
        tvEdit.setVisibility(View.VISIBLE);

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, navigationViewRight);

        getListViewData();

        adapter = new AnnouncementsListViewAdapter(AnnouncementsActivity.this, mData, markForDeleteList);

        lvAnnouncements = (ListView) findViewById(R.id.lvAnnouncements);

        lvAnnouncements.setAdapter(adapter);

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvEdit.getText().toString().equalsIgnoreCase("Edit")) {
                    tvEdit.setText("Delete");
                    adapter.isEdit = true;
                    adapter.notifyDataSetChanged();
                } else {
                    try {
                        Realm realm = AppUtil.getRealmInstance(App.getInstance());

                        ParseUser user = ParseUser.getQuery().get(currentPerson.getObjectId()).fetchIfNeeded();

                        ArrayList<String> deletedList = (ArrayList<String>)user.get("deletedNotificationIds");

                        for (int i = adapter.mData.size() - 1; i >= 0; --i) {
                            Boolean shouldRemove = adapter.markForDeleteList.get(i);


                            if (shouldRemove == true) {
                                // TODO : Add MasterNotification to deleted list in Parse and Realm
                                deletedList.add(adapter.mData.get(i).getObjectId());

                                realm.beginTransaction();
                                MasterNotification alert = adapter.mData.get(i);
                                currentPerson.getDeletedNotificationIds().add(alert);
                                realm.commitTransaction();

                                adapter.mData.remove(i);
                                adapter.markForDeleteList.remove(i);
                            }
                        }

                        user.put("deletedNotificationIds", deletedList);
                    } catch (Exception ex) {
                        Log.d("Announcements", ex.getMessage());
                    }

                    tvEdit.setText("Edit");
                    adapter.isEdit = false;
                    adapter.notifyDataSetChanged();
                }
            }
        });

        lvAnnouncements.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Realm realm = AppUtil.getRealmInstance(App.getInstance());

                realm.beginTransaction();
                MasterNotification alert = adapter.mData.get(position);
                alert.setNew(false);
                realm.commitTransaction();

                adapter.notifyDataSetChanged();

                AlertDialog alertDialog = new AlertDialog.Builder(AnnouncementsActivity.this).create();
                alertDialog.setTitle("Announcement");
                alertDialog.setMessage(alert.getAlert() + "\n\nAnnouncement posted at: " + alert.getCreatedAt().toString());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    public void getListViewData() {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<MasterNotification> alertResults;

        alertResults  = realm.where(MasterNotification.class).equalTo("conference", AppUtil.getSelectedConferenceID(AnnouncementsActivity.this)).findAllSorted("createdAt", false);

        RealmList<MasterNotification> tempResults = new RealmList<MasterNotification>();

        tempResults.addAll(alertResults);

        if (OConnectBaseActivity.currentPerson != null) {
            for (int i = 0; i < tempResults.size(); ++i) {
                MasterNotification alert = tempResults.get(i);

                RealmList<MasterNotification> deletedAlerts = OConnectBaseActivity.currentPerson.getDeletedNotificationIds();

                for (MasterNotification thisAlert : deletedAlerts) {
                    if (alert.getObjectId().contentEquals(thisAlert.getObjectId())) {
                        alertResults.remove(i);
                    }
                }
            }
        }

        mData = new ArrayList<MasterNotification>();

        mData.addAll(alertResults);

        for (int j = 0; j < alertResults.size(); ++j) {
            markForDeleteList.add(false);
        }
    }

}
