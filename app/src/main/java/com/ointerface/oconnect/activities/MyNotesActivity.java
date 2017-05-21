package com.ointerface.oconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.adapters.MyNotesListViewAdapter;
import com.ointerface.oconnect.adapters.SponsorsListViewAdapter;
import com.ointerface.oconnect.data.MyNote;
import com.ointerface.oconnect.data.Sponsor;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.w3c.dom.Text;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.view.View.GONE;

public class MyNotesActivity extends OConnectBaseActivity {
    private ListView lvNotes;
    private MyNotesListViewAdapter adapter;
    public ArrayList<MyNote> mData = new ArrayList<MyNote>();
    public ArrayList<Boolean> markForDeleteList = new ArrayList<Boolean>();
    private TextView tvNoNotes;
    private ImageView ivTrashCan;
    private ImageView ivAddNote;
    private TextView tvNoteCount;
    private TextView tvCancel;
    public TextView tvDeleteAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notes);
        super.onCreateDrawer();

        tvToolbarTitle.setText("Notes");

        ivProfileLanyard.setVisibility(GONE);
        ivHelp.setVisibility(View.VISIBLE);
        ivRightToolbarIcon.setVisibility(View.VISIBLE);
        ivSearch.setVisibility(GONE);

        tvCancel = (TextView) findViewById(R.id.tvCancel);
        tvDeleteAll = (TextView) findViewById(R.id.tvDeleteAll);
        tvNoteCount = (TextView) findViewById(R.id.tvNoteCount);

        tvNoNotes = (TextView) findViewById(R.id.tvNoNotes);

        ivAddNote = (ImageView) findViewById(R.id.ivAddPlus);

        ivTrashCan = (ImageView) findViewById(R.id.ivTrashCan);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.isEdit = false;
                adapter.notifyDataSetChanged();
                tvCancel.setVisibility(GONE);
                tvDeleteAll.setVisibility(GONE);
                ivTrashCan.setVisibility(View.VISIBLE);
                ivAddNote.setVisibility(View.VISIBLE);
            }
        });

        tvDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = AppUtil.getRealmInstance(App.getInstance());

                if (tvDeleteAll.getText().toString().equalsIgnoreCase("Delete All")) {
                    for (int i = adapter.mData.size() - 1; i >= 0; --i) {
                        // TODO : Update deleted list in Parse and Realm

                        realm.beginTransaction();

                        MyNote myNote = adapter.mData.get(i);

                        myNote.setDeleted(true);

                        realm.commitTransaction();

                        try {
                            ParseObject noteObj = ParseQuery.getQuery("Note").whereEqualTo("objectId", myNote.getObjectId()).getFirst();
                            noteObj.put("isDeleted", true);
                            noteObj.save();
                        } catch (Exception ex) {
                            Log.d("MyNotes", ex.getMessage());
                        }

                        adapter.mData.remove(i);
                        adapter.markForDeleteList.remove(i);
                    }
                } else {
                    for (int i = adapter.mData.size() - 1; i >= 0; --i) {
                        Boolean shouldRemove = adapter.markForDeleteList.get(i);

                        if (shouldRemove == true) {
                            // TODO : Update deleted list in Parse and Realm
                            realm.beginTransaction();

                            MyNote myNote = adapter.mData.get(i);

                            myNote.setDeleted(true);

                            realm.commitTransaction();

                            try {
                                ParseObject noteObj = ParseQuery.getQuery("Note").whereEqualTo("objectId", myNote.getObjectId()).getFirst();
                                noteObj.put("isDeleted", true);
                                noteObj.save();
                            } catch (Exception ex) {
                                Log.d("MyNotes", ex.getMessage());
                            }

                            adapter.mData.remove(i);
                            adapter.markForDeleteList.remove(i);
                        }
                    }
                }

                updateNoteCount();

                tvDeleteAll.setVisibility(GONE);
                tvCancel.setVisibility(GONE);
                ivAddNote.setVisibility(View.VISIBLE);
                ivTrashCan.setVisibility(View.VISIBLE);

                adapter.isEdit = false;
                adapter.notifyDataSetChanged();
            }
        });

        ivAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyNotesActivity.this, AddNoteActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up );
            }
        });



        ivTrashCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.isEdit = true;
                adapter.notifyDataSetChanged();
                ivTrashCan.setVisibility(GONE);
                ivAddNote.setVisibility(GONE);
                tvCancel.setVisibility(View.VISIBLE);
                tvDeleteAll.setVisibility(View.VISIBLE);
            }
        });

        getListViewData();

        adapter = new MyNotesListViewAdapter(MyNotesActivity.this, mData, markForDeleteList);

        lvNotes = (ListView) findViewById(R.id.lvNotesList);

        lvNotes.setAdapter(adapter);
    }

    public void getListViewData() {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<MyNote> myNotesResults;

        myNotesResults  = realm.where(MyNote.class).equalTo("Conference", AppUtil.getSelectedConferenceID(MyNotesActivity.this)).equalTo("User", currentPerson.getObjectId()).notEqualTo("isDeleted", true).findAllSorted("createdAt", Sort.ASCENDING);

        mData = new ArrayList<MyNote>();
        markForDeleteList = new ArrayList<Boolean>();

        mData.addAll(myNotesResults);

        for (int j = 0; j < myNotesResults.size(); ++j) {
            markForDeleteList.add(false);
        }

        if (mData.size() > 0) {
            tvNoNotes.setVisibility(GONE);
        } else {
            tvNoNotes.setVisibility(View.VISIBLE);
        }

        updateNoteCount();
    }

    public void updateNoteCount() {
        if (mData.size() == 1) {
            tvNoteCount.setText(mData.size() + " Note");
        } else {
            tvNoteCount.setText(mData.size() + " Notes");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getListViewData();

        adapter = new MyNotesListViewAdapter(MyNotesActivity.this, mData, markForDeleteList);

        lvNotes.setAdapter(adapter);
    }
}
