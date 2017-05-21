package com.ointerface.oconnect.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.adapters.AnnouncementsListViewAdapter;
import com.ointerface.oconnect.adapters.SponsorsListViewAdapter;
import com.ointerface.oconnect.data.MasterNotification;
import com.ointerface.oconnect.data.Sponsor;
import com.ointerface.oconnect.util.AppUtil;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.view.View.GONE;

public class SponsorsListActivity extends OConnectBaseActivity {
    private ListView lvSponsors;
    private SponsorsListViewAdapter adapter;
    public ArrayList<Sponsor> mData = new ArrayList<Sponsor>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponsors_list);
        super.onCreateDrawer();

        if (selectedConference.getToolbarLabelSponsors() != null
                && !selectedConference.getToolbarLabelSponsors().equalsIgnoreCase("")) {
            tvToolbarTitle.setText(selectedConference.getToolbarLabelSponsors());
        } else {
            tvToolbarTitle.setText("Sponsors");
        }

        ivProfileLanyard.setVisibility(GONE);
        ivSearch.setVisibility(GONE);
        ivRightToolbarIcon.setVisibility(View.VISIBLE);

        ivHelp.setVisibility(View.VISIBLE);
        tvEdit.setVisibility(GONE);

        getListViewData();

        adapter = new SponsorsListViewAdapter(SponsorsListActivity.this, mData);

        lvSponsors = (ListView) findViewById(R.id.lvSponsors);

        lvSponsors.setAdapter(adapter);

        lvSponsors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Sponsor sponsor = adapter.mData.get(position);

                Intent i = new Intent(SponsorsListActivity.this, WebViewActivity.class);
                i.putExtra("TITLE", "");
                i.putExtra("URL", sponsor.getWebsite());
                i.putExtra("BACK_TEXT", "Sponsors");
                i.putExtra("OPEN", "Open In Browser");
                startActivity(i);
            }
        });
    }

    public void getListViewData() {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<Sponsor> sponsorResults;

        sponsorResults  = realm.where(Sponsor.class).equalTo("conference", AppUtil.getSelectedConferenceID(SponsorsListActivity.this)).findAllSorted("name", Sort.ASCENDING);

        mData = new ArrayList<Sponsor>();

        mData.addAll(sponsorResults);
    }
}
