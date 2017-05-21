package com.ointerface.oconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ointerface.oconnect.R;
import com.ointerface.oconnect.data.MasterNotification;
import com.ointerface.oconnect.util.AppUtil;

import java.util.ArrayList;
import java.util.TreeSet;

import static android.view.View.GONE;

/**
 * Created by AnthonyDoan on 4/19/17.
 */

public class AnnouncementsListViewAdapter extends BaseAdapter {
    public Context context;

    private LayoutInflater mInflater;

    public ArrayList<MasterNotification> mData = new ArrayList<MasterNotification>();

    public ArrayList<Boolean> markForDeleteList = new ArrayList<Boolean>();

    public boolean isEdit = false;

    public AnnouncementsListViewAdapter(Context context, ArrayList<MasterNotification> announcementsArg,
                                        ArrayList<Boolean> markForDeleteList) {
        super();
        this.context = context;
        this.mData = announcementsArg;
        this.markForDeleteList = markForDeleteList;

        mInflater = LayoutInflater.from(context);
    }

    public void addItem(final MasterNotification item) {
        mData.add(item);
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position).getAlert();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MasterNotification announcement = mData.get(position);

        convertView = mInflater.inflate(R.layout.announcements_list_view_item, null);

        TextView tvAnnouncement = (TextView) convertView.findViewById(R.id.tvAnnouncement);

        tvAnnouncement.setText(announcement.getAlert());

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
                }
            });
        }

        TextView tvNew = (TextView) convertView.findViewById(R.id.tvNew);

        if (announcement.isNew() == true) {
            tvNew.setVisibility(View.VISIBLE);
            tvNew.setTextColor(AppUtil.getPrimaryThemColorAsInt());
        } else {
            tvNew.setVisibility(GONE);
        }

        return convertView;
    }
}

