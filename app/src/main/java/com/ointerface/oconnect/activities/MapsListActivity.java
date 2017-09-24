package com.ointerface.oconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.data.Maps;
import com.ointerface.oconnect.util.AppUtil;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.view.View.GONE;

public class MapsListActivity extends OConnectBaseActivity {
    private ArrayAdapter<String> adapter;
    private ListView lvMapsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_list);
        super.onCreateDrawer();

        if (selectedConference.getToolbarLabelMaps() != null &&
                !selectedConference.getToolbarLabelMaps().equalsIgnoreCase("")) {
            tvToolbarTitle.setText(selectedConference.getToolbarLabelMaps());
        } else {
            tvToolbarTitle.setText("Maps");
        }

        ivProfileLanyard.setVisibility(GONE);
        ivConnections.setVisibility(View.VISIBLE);
        ivRightToolbarIcon.setVisibility(View.VISIBLE);
        ivSearch.setVisibility(GONE);

        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        final RealmResults<Maps> mapResults = realm.where(Maps.class).equalTo("conference", selectedConference.getObjectId()).findAllSorted("label", Sort.ASCENDING);

        ArrayList<String> labelList = new ArrayList<String>();

        for (Maps thisMap : mapResults) {
            labelList.add(thisMap.getLabel());
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, labelList);

        lvMapsList = (ListView) findViewById(R.id.lvMapsList);

        lvMapsList.setAdapter(adapter);

        lvMapsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Maps map = mapResults.get(position);

                Intent i = new Intent(MapsListActivity.this, MapActivity.class);

                i.putExtra("objectId", map.getObjectId());

                startActivity(i);
            }
        });

        if (mapResults.size() <= 1) {
            Maps map = mapResults.get(0);

            Intent i = new Intent(MapsListActivity.this, MapActivity.class);

            i.putExtra("objectId", map.getObjectId());

            startActivity(i);
        }
    }

}
