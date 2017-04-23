package com.ointerface.oconnect.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.MyNotesActivity;
import com.ointerface.oconnect.data.MyNote;
import com.ointerface.oconnect.data.Sponsor;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by AnthonyDoan on 4/21/17.
 */

public class MyNotesListViewAdapter extends BaseAdapter {
    public Context context;

    private LayoutInflater mInflater;

    public ArrayList<MyNote> mData = new ArrayList<MyNote>();
    public ArrayList<Boolean> markForDeleteList = new ArrayList<Boolean>();

    public boolean isEdit = false;

    public MyNotesListViewAdapter(Context context, ArrayList<MyNote> notesArg, ArrayList<Boolean> deleteList) {
        super();
        this.context = context;
        this.mData = notesArg;
        this.markForDeleteList = deleteList;

        mInflater = LayoutInflater.from(context);
    }

    public void addItem(final MyNote item) {
        mData.add(item);
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position).getTitle();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyNote myNote = mData.get(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.my_notes_list_view_item, null);
        }

        TextView tvNoteTitle = (TextView) convertView.findViewById(R.id.tvNoteTitle);

        tvNoteTitle.setText(myNote.getTitle());

        TextView tvNoteText = (TextView) convertView.findViewById(R.id.tvNoteText);

        tvNoteText.setText(myNote.getContent());

        final CheckBox cbSelect = (CheckBox) convertView.findViewById(R.id.cbSelect);

        if (isEdit == false) {
            cbSelect.setVisibility(GONE);
        } else {
            cbSelect.setVisibility(View.VISIBLE);

            cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    cbSelect.setChecked(isChecked);

                    markForDeleteList.set(position, isChecked);

                    int count = 0;

                    for (boolean value : markForDeleteList) {
                        if (value == true) {
                            ++count;
                        }
                    }

                    MyNotesActivity activity = (MyNotesActivity) context;

                    if (count > 0) {
                        activity.tvDeleteAll.setText("Delete");
                    } else {
                        activity.tvDeleteAll.setText("Delete All");
                    }
                }
            });
        }

        return convertView;
    }
}

