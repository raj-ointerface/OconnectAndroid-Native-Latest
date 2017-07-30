package com.ointerface.oconnect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ointerface.oconnect.data.Conference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeSet;

import static android.view.View.GONE;

/**
 * Created by AnthonyDoan on 4/11/17.
 */

public class ConferenceListViewAdapter extends BaseAdapter {
    public Context context;
    public Conference[] conferences;

    private LayoutInflater mInflater;

    public ArrayList<Conference> mData = new ArrayList<Conference>();
    public TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    public ConferenceListViewAdapter(Context context, Conference[] conferencesArg) {
        super();
        this.context = context;
        this.conferences = conferencesArg;

        mInflater = LayoutInflater.from(context);
    }

    public void addItem(final Conference item) {
        mData.add(item);
        // notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final Conference item) {
        mData.add(item);
        sectionHeader.add(mData.size() - 1);
        //notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position).getGroup();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderLocal holder = null;
        int rowType = getItemViewType(position);

        Conference conference = mData.get(position);

        // if (convertView == null) {
            holder = new ViewHolderLocal();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.conference_list_view_item, null);
                    holder.tvConferenceTitle = (TextView) convertView.findViewById(R.id.conference_title);
                    holder.tvConferenceDate = (TextView) convertView.findViewById(R.id.conference_date);
                    holder.tvConferenceAddress1 = (TextView) convertView.findViewById(R.id.conference_address_1);
                    holder.tvConferenceAddress2 = (TextView) convertView.findViewById(R.id.conference_address_2);
                    holder.ivConferenceImage = (ImageView) convertView.findViewById(R.id.conference_image);
                    holder.llPrivateText = (LinearLayout) convertView.findViewById(R.id.llPrivateText);

                    holder.tvConferenceTitle.setText(conference.getName());

                    SimpleDateFormat sdFormat1 = new SimpleDateFormat("MMMM d");
                    SimpleDateFormat sdFormat2 = new SimpleDateFormat("MMMM d yyyy");
                    sdFormat1.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
                    sdFormat2.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));

                    if (conference.getEndTime() == null) {
                        holder.tvConferenceDate.setText(sdFormat2.format(conference.getStartTime()));
                    } else {
                        holder.tvConferenceDate.setText(sdFormat1.format(conference.getStartTime()) +
                        " - " + sdFormat2.format(conference.getEndTime()));
                    }

                    if (conference.isPublic() == true) {
                        holder.llPrivateText.setVisibility(GONE);
                    }

                    holder.tvConferenceAddress1.setText(conference.getAddress());
                    holder.tvConferenceAddress2.setText(conference.getCity() + ", " + conference.getState() + " " +
                            conference.getZip() + ", " + conference.getCountry());
                    if (conference.getImage() != null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(conference.getImage(), 0, conference.getImage().length);
                        holder.ivConferenceImage.setImageBitmap(bmp);
                    }
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.conference_list_header, null);
                    convertView.setBackgroundColor(ConferenceListViewActivity.customColor);
                    holder.tvHeader = (TextView) convertView.findViewById(R.id.textSeparator);
                    holder.tvHeader.setText(conference.getGroup());
                    if (conference.getGroup() == null || conference.getGroup().equalsIgnoreCase("")) {
                        convertView.setVisibility(GONE);
                        holder.tvHeader.setVisibility(GONE);
                    }
                    break;
            }
            convertView.setTag(holder);
        // }
        /*
        else {
            holder = (ViewHolderLocal) convertView.getTag();
        }
        */

        return convertView;

    }

    public class ViewHolderLocal {
        public TextView tvConferenceTitle;
        public TextView tvConferenceDate;
        public TextView tvConferenceAddress1;
        public TextView tvConferenceAddress2;
        public TextView tvHeader;
        public ImageView ivConferenceImage;
        public LinearLayout llPrivateText;
    }
}
