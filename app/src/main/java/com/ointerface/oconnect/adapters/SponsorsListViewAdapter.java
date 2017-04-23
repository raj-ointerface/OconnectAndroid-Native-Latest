package com.ointerface.oconnect.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ointerface.oconnect.R;
import com.ointerface.oconnect.data.MasterNotification;
import com.ointerface.oconnect.data.Sponsor;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by AnthonyDoan on 4/21/17.
 */

public class SponsorsListViewAdapter extends BaseAdapter {
    public Context context;

    private LayoutInflater mInflater;

    public ArrayList<Sponsor> mData = new ArrayList<Sponsor>();

    public SponsorsListViewAdapter(Context context, ArrayList<Sponsor> sponsorsArg) {
        super();
        this.context = context;
        this.mData = sponsorsArg;

        mInflater = LayoutInflater.from(context);
    }

    public void addItem(final Sponsor item) {
        mData.add(item);
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Sponsor sponsor = mData.get(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.sponsor_list_view_item, null);
        }

        TextView tvSponsorName = (TextView) convertView.findViewById(R.id.tvSponsorName);

        tvSponsorName.setText(sponsor.getName());

        TextView tvSponsorType = (TextView) convertView.findViewById(R.id.tvSponsorType);

        tvSponsorType.setText(sponsor.getType());

        ImageView ivLogo = (ImageView) convertView.findViewById(R.id.ivSponsorLogo);

        if (sponsor.getLogo() != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(sponsor.getLogo(), 0, sponsor.getLogo().length);
            ivLogo.setImageBitmap(bmp);
        }

        return convertView;
    }
}

