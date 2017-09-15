package com.ointerface.oconnect.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.adapters.MyAgendaListViewAdapter;
import com.ointerface.oconnect.data.DataSyncManager;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.IDataSyncListener;
import com.ointerface.oconnect.util.AppUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.realm.Realm;
import io.realm.RealmList;

import static android.view.View.GONE;

public class MyAgendaActivity extends OConnectBaseActivity implements IDataSyncListener {
    private ListView lvMyAgendaList;
    private MyAgendaListViewAdapter adapter;
    public ArrayList<Event> mData = new ArrayList<Event>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_agenda);
        super.onCreateDrawer();

        tvToolbarTitle.setText("My Agenda");

        ivProfileLanyard.setVisibility(GONE);
        ivConnections.setVisibility(View.VISIBLE);
        ivRightToolbarIcon.setVisibility(View.VISIBLE);
        ivSearch.setVisibility(GONE);

        lvMyAgendaList = (ListView) findViewById(R.id.lvMyAgendaList);

        DataSyncManager.dialog = ProgressDialog.show(MyAgendaActivity.this, null, "Downloading Events ... Please wait.");
        DataSyncManager.shouldSyncAll = false;
        DataSyncManager.initDataSyncManager(getApplicationContext(), MyAgendaActivity.this);
        DataSyncManager.dataSyncSessions();
        DataSyncManager.dataSyncEvents();
        DataSyncManager.dialog.hide();

        getListViewData();

        adapter = new MyAgendaListViewAdapter(MyAgendaActivity.this, mData);

        lvMyAgendaList.setAdapter(adapter);
    }

    public void getListViewData() {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        realm.beginTransaction();

        RealmList<Event> myEventsResults;

        myEventsResults  = currentPerson.getFavoriteEvents();

        Collections.sort(myEventsResults, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return o1.getStartTime().compareTo(o2.getStartTime());
            }
        });

        realm.commitTransaction();

        mData = new ArrayList<Event>();

        mData.addAll(myEventsResults);
    }

    public void onDataSyncFinish() {

    }
}
