package com.ointerface.oconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.InfoActivity;
import com.ointerface.oconnect.activities.WebViewActivity;
import com.ointerface.oconnect.data.TravelBusiness;
import com.ointerface.oconnect.util.AppConfig;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by AnthonyDoan on 5/28/17.
 */

public class TravelBusinessListViewAdapter extends BaseAdapter {
    public Context context;

    private LayoutInflater mInflater;

    public ArrayList<TravelBusiness> mData = new ArrayList<TravelBusiness>();

    public TravelBusinessListViewAdapter(Context context, ArrayList<TravelBusiness> mDataArg) {
        super();
        this.context = context;
        this.mData = mDataArg;

        mInflater = LayoutInflater.from(context);
    }

    public void addItem(final TravelBusiness item) {
        mData.add(item);
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position).getBusinessName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final TravelBusiness travelBusiness = mData.get(position);

        convertView = mInflater.inflate(R.layout.travel_business_list_item, null);

        TextView tvBusinessName = (TextView) convertView.findViewById(R.id.tvBusinessName);
        TextView tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
        TextView tvRates = (TextView) convertView.findViewById(R.id.tvRates);
        TextView tvOtherDetails = (TextView) convertView.findViewById(R.id.tvOtherDetails);
        TextView tvWebsite = (TextView) convertView.findViewById(R.id.tvWebsite);

        if (travelBusiness.getBusinessName() != null && !travelBusiness.getBusinessName().equalsIgnoreCase("")) {
            tvBusinessName.setText(travelBusiness.getBusinessName());
        } else {
            tvBusinessName.setVisibility(GONE);
        }

        if (travelBusiness.getAddress() != null && !travelBusiness.getAddress().equalsIgnoreCase("")) {
            tvAddress.setText(travelBusiness.getAddress());
        } else {
            tvAddress.setVisibility(GONE);
        }

        if (travelBusiness.getRates() != null && !travelBusiness.getRates().equalsIgnoreCase("")) {
            tvRates.setText(travelBusiness.getRates());
        } else {
            tvRates.setVisibility(GONE);
        }

        if (travelBusiness.getOtherDetails() != null && !travelBusiness.getOtherDetails().equalsIgnoreCase("")) {
            tvOtherDetails.setText(travelBusiness.getOtherDetails());
        } else {
            tvOtherDetails.setVisibility(GONE);
        }

        if (travelBusiness.getWebsite() != null && !travelBusiness.getWebsite().equalsIgnoreCase("")) {
            tvWebsite.setText("Website");

            tvWebsite.setTextColor(AppConfig.blueColor);

            tvWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, WebViewActivity.class);
                    i.putExtra("TITLE", "");
                    i.putExtra("URL", travelBusiness.getWebsite());
                    i.putExtra("BACK_TEXT", "Back");
                    i.putExtra("OPEN", "Open In Browser");
                    context.startActivity(i);
                }
            });
        } else {
            tvWebsite.setVisibility(GONE);
        }

        return convertView;
    }
}



