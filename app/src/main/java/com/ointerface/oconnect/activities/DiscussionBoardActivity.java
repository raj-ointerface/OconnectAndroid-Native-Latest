package com.ointerface.oconnect.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ListView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.adapters.DiscussionBoardQuestionListViewAdapter;
import com.ointerface.oconnect.data.DBQuestion;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.IDataSyncListener;
import com.ointerface.oconnect.util.AppUtil;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.view.View.GONE;

public class DiscussionBoardActivity extends OConnectBaseActivity implements IDataSyncListener {

    private ListView lvQuestions;
    private DiscussionBoardQuestionListViewAdapter adapter;
    public ArrayList<DBQuestion> mData = new ArrayList<DBQuestion>();

    public static Event discussionBoardEvent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_board);
        super.onCreateDrawer();

        tvToolbarTitle.setText("Discussion Board");

        ivProfileLanyard.setVisibility(GONE);
        ivSearch.setVisibility(GONE);
        ivRightToolbarIcon.setVisibility(GONE);

        ivConnections.setVisibility(GONE);
        tvEdit.setVisibility(GONE);

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, navigationViewRight);

        Drawable dr = getResources().getDrawable(R.drawable.icon_back);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();

        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, AppUtil.convertDPToPXInt(DiscussionBoardActivity.this, 20), AppUtil.convertDPToPXInt(DiscussionBoardActivity.this, 20), true));

        // toolbar.setNavigationIcon(d);

        getSupportActionBar().setHomeAsUpIndicator(d);

        tvHeaderBack.setVisibility(View.VISIBLE);
        ivHeaderBack.setVisibility(View.VISIBLE);

        ivHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiscussionBoardActivity.this.finish();
            }
        });

        tvHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiscussionBoardActivity.this.finish();
            }
        });

        tvHeaderBack.setText("Event");

        lvQuestions = (ListView) findViewById(R.id.lvQuestions);

        getListViewData();

        adapter = new DiscussionBoardQuestionListViewAdapter(DiscussionBoardActivity.this, mData);

        lvQuestions.setAdapter(adapter);

    }

    public void getListViewData() {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        mData = new ArrayList<DBQuestion>();

        if (discussionBoardEvent != null) {
            RealmResults<DBQuestion> questionsResult = realm.where(DBQuestion.class).equalTo("event", discussionBoardEvent.getObjectId()).findAllSorted("votes", Sort.DESCENDING);

            if (questionsResult.size() > 0) {
                mData.addAll(questionsResult);
            }
        }
    }

}
