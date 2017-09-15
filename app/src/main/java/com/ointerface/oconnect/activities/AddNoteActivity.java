package com.ointerface.oconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.data.MyNote;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import io.realm.Realm;

import static android.view.View.GONE;

public class AddNoteActivity extends OConnectBaseActivity {
    private EditText etTitle;
    private EditText etNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        super.onCreateDrawer();

        tvToolbarTitle.setText("Add Note");

        ivProfileLanyard.setVisibility(GONE);
        ivSearch.setVisibility(GONE);

        tvEdit.setVisibility(View.VISIBLE);

        ivRightToolbarIcon.setVisibility(View.VISIBLE);
        ivRightToolbarIcon.setBackgroundResource(R.drawable.icon_share);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(AppUtil.convertDPToPXInt(AddNoteActivity.this,25), AppUtil.convertDPToPXInt(AddNoteActivity.this,25));
        lp.setMargins(AppUtil.convertDPToPXInt(AddNoteActivity.this, 210), 0, AppUtil.convertDPToPXInt(AddNoteActivity.this, 0), 0);

        ivRightToolbarIcon.setLayoutParams(lp);

        etTitle = (EditText) findViewById(R.id.etTitle);
        etNote = (EditText) findViewById(R.id.etNote);

        ivRightToolbarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                String shareBody = etTitle.getText().toString() + " " + etNote.getText().toString();
                i.putExtra(Intent.EXTRA_SUBJECT, etTitle.getText().toString());
                i.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(i, "Share via oConnect"));
            }
        });

        ivConnections.setVisibility(GONE);

        tvEdit.setText("Save");

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ParseObject noteObject = new ParseObject("Note");
                noteObject.put("User", ParseObject.createWithoutData("_User", currentPerson.getObjectId()));
                noteObject.put("title", etTitle.getText().toString());
                noteObject.put("content", etNote.getText().toString());
                noteObject.put("Conference", ParseObject.createWithoutData("Conference", selectedConference.getObjectId()));

                noteObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Realm realm = AppUtil.getRealmInstance(App.getInstance());

                            realm.beginTransaction();

                            MyNote myNote = realm.createObject(MyNote.class, noteObject.getObjectId());

                            myNote.setUser(currentPerson.getObjectId());
                            myNote.setConference(selectedConference.getObjectId());
                            myNote.setTitle(etTitle.getText().toString());
                            myNote.setContent(etNote.getText().toString());
                            // myNote.setObjectId(noteObject.getObjectId());
                            myNote.setCreatedAt(noteObject.getCreatedAt());

                            realm.commitTransaction();
                        }

                        AddNoteActivity.this.finish();
                        overridePendingTransition(R.anim.hold, R.anim.disappear_to_bottom);
                    }
                });
            }
        });

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, navigationViewRight);

    }

}
