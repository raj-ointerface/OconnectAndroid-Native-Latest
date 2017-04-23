package com.ointerface.oconnect.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.adapters.MyAgendaListViewAdapter;
import com.ointerface.oconnect.adapters.MyNotesListViewAdapter;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.MyNote;
import com.ointerface.oconnect.util.AppUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static android.view.View.GONE;

public class MyAgendaActivity extends OConnectBaseActivity {
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
        ivHelp.setVisibility(View.VISIBLE);
        ivRightToolbarIcon.setVisibility(View.VISIBLE);
        ivSearch.setVisibility(GONE);

        getListViewData();

        adapter = new MyAgendaListViewAdapter(MyAgendaActivity.this, mData);

        lvMyAgendaList = (ListView) findViewById(R.id.lvMyAgendaList);

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
}
