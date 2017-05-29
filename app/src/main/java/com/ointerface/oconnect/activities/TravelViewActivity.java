package com.ointerface.oconnect.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.adapters.TravelBusinessListViewAdapter;
import com.ointerface.oconnect.data.TravelBusiness;
import com.ointerface.oconnect.util.AppUtil;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.view.View.GONE;

public class TravelViewActivity extends OConnectBaseActivity {
    private TextView tvLodging;
    private TextView tvTransportation;
    private ListView lvLodging;
    private ListView lvTransportation;
    private View divider1;
    private View divider2;
    private View divider3;
    private TextView tvNoInfo;

    private float x1;
    private float x2;
    static final int MIN_DISTANCE = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_view);
        super.onCreateDrawer();

        ivProfileLanyard.setVisibility(GONE);
        ivHelp.setVisibility(View.GONE);
        ivRightToolbarIcon.setVisibility(View.GONE);
        ivSearch.setVisibility(GONE);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Drawable dr = getResources().getDrawable(R.drawable.icon_back);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();

        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, AppUtil.convertDPToPXInt(TravelViewActivity.this, 20), AppUtil.convertDPToPXInt(TravelViewActivity.this, 20), true));

        // toolbar.setNavigationIcon(d);

        getSupportActionBar().setHomeAsUpIndicator(d);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TravelViewActivity.this.finish();
            }
        });

        tvToolbarTitle.setText("Travel & Lodging");

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, navigationView);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, navigationViewRight);

        tvHeaderBack.setVisibility(View.VISIBLE);
        ivHeaderBack.setVisibility(View.VISIBLE);

        ivHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TravelViewActivity.this.finish();
            }
        });

        tvHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TravelViewActivity.this.finish();
            }
        });

        tvHeaderBack.setText("Back");

        tvLodging = (TextView) findViewById(R.id.tvLodging);
        tvTransportation = (TextView) findViewById(R.id.tvTransportation);
        lvLodging = (ListView) findViewById(R.id.lvLodging);
        lvTransportation = (ListView) findViewById(R.id.lvTransportation);
        divider1 = (View) findViewById(R.id.divider1);
        divider2 = (View) findViewById(R.id.divider2);
        divider3 = (View) findViewById(R.id.divider3);
        tvNoInfo = (TextView) findViewById(R.id.tvNoInfo);

        boolean missingAllInformation = getListData();

        if (missingAllInformation = true) {
            tvNoInfo.setVisibility(View.VISIBLE);
        } else {
            tvNoInfo.setVisibility(GONE);
        }
    }

    private boolean getListData() {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<TravelBusiness> travelResults = realm.where(TravelBusiness.class).equalTo("conference", selectedConference.getObjectId()).findAll();

        if (travelResults == null || travelResults.size() == 0) {
            return true;
        }

        ArrayList<TravelBusiness> lodgingList = new ArrayList<TravelBusiness>();
        ArrayList<TravelBusiness> transportationList = new ArrayList<TravelBusiness>();

        for (int i = 0; i < travelResults.size(); ++i) {
            TravelBusiness travelObj = travelResults.get(i);

            if (travelObj.getBusinessType().equalsIgnoreCase("hotel")) {
                lodgingList.add(travelObj);
            } else {
                transportationList.add(travelObj);
            }
        }

        TravelBusinessListViewAdapter lodgingAdapter = new TravelBusinessListViewAdapter(this, lodgingList);
        TravelBusinessListViewAdapter transportationAdapter = new TravelBusinessListViewAdapter(this, transportationList);

        lvLodging.setAdapter(lodgingAdapter);

        lvTransportation.setAdapter(transportationAdapter);

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    // Left to Right swipe action
                    if (x2 > x1)
                    {
                        // Toast.makeText(this, "Left to Right swipe [Next]", Toast.LENGTH_SHORT).show ();
                        TravelViewActivity.this.finish();
                    }

                    // Right to left swipe action
                    else
                    {
                        // Toast.makeText(this, "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show ();
                    }

                }
                else
                {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
    }

}
