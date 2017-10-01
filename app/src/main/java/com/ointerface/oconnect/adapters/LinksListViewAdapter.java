package com.ointerface.oconnect.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.MyNotesActivity;
import com.ointerface.oconnect.data.MyNote;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by AnthonyDoan on 5/20/17.
 */

public class LinksListViewAdapter extends BaseAdapter {
    public Context context;

    private LayoutInflater mInflater;

    public ArrayList<String> mData = new ArrayList<String>();
    public ArrayList<String> mURL = new ArrayList<String>();

    private int index = 0;

    public LinksListViewAdapter(Context context, ArrayList<String> mDataArg, ArrayList<String> mURLArg) {
        super();
        this.context = context;
        mData = new ArrayList<String>();
        mURL = new ArrayList<String>();

        mData.addAll(mDataArg);
        mURL.addAll(mURLArg);

        index = 0;

        mInflater = LayoutInflater.from(context);
    }

    public void addItem(final String item) {
        mData.add(item);
    }


    @Override
    public int getCount() {
        Log.d("APD", "APD mData size: " + mData.size());
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (index >= mData.size()) {
            index = 0;
        }

        Log.d("APD", "APD SpeakerFile getView at: " + index);
        String link = mData.get(index);

        convertView = mInflater.inflate(R.layout.speaker_detail_list_link_item, null);

        TextView tvLink = (TextView) convertView.findViewById(R.id.tvLink);

        tvLink.setText(link);

        tvLink.setPaintFlags(tvLink.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);

        index += 1;

        return convertView;
    }
}


