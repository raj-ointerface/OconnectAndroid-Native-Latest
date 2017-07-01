package com.ointerface.oconnect.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.activities.ScheduleActivity;
import com.ointerface.oconnect.containers.MenuItemHolder;
import com.ointerface.oconnect.data.Attendee;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.MasterNotification;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.util.AppUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.view.View.GONE;

/**
 * Created by AnthonyDoan on 7/1/17.
 */

public class SearchExpandableListViewAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, ArrayList<RealmObject>> _listDataChild;


    public SearchExpandableListViewAdapter(Context context, List<String> listDataHeader,
                                        HashMap<String, ArrayList<RealmObject>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final RealmObject childItem = (RealmObject) getChild(groupPosition, childPosition);

        if (childItem instanceof Event) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_view_event_item, null);

            Event eventItem = (Event) childItem;

            TextView tvCircle = (TextView) convertView.findViewById(R.id.tvCircle);
            TextView tvEventTitle = (TextView) convertView.findViewById(R.id.tvEventTitle);
            TextView tvEventTime = (TextView) convertView.findViewById(R.id.tvEventTime);

            DateFormat dfTime = new SimpleDateFormat("h:mm a");
            dfTime.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));

            String startTime = dfTime.format(eventItem.getStartTime());
            String endTime = dfTime.format(eventItem.getEndTime());

            if (ScheduleActivity.isEventType == true) {
                SimpleDateFormat dfEventDate = new SimpleDateFormat("EEEE, MMMM d");

                tvEventTime.setText(dfEventDate.format(eventItem.getStartTime()));
            } else {
                tvEventTime.setText(startTime + " - " + endTime);
            }

            if (eventItem.getName().length() > 2) {
                tvCircle.setText(eventItem.getName().substring(0,2));
            } else {
                tvCircle.setText("");
            }

            tvEventTitle.setText(eventItem.getName());

        } else if (childItem instanceof Speaker || childItem instanceof Attendee) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_view_person_item, null);

            CircleImageView ivProfilePicture = (CircleImageView) convertView.findViewById(R.id.ivPersonPicture);
            TextView tvCircle = (TextView) convertView.findViewById(R.id.tvCircle);
            TextView tvName = (TextView) convertView.findViewById(R.id.tvNameText);

            if (childItem instanceof  Speaker) {
                Speaker speakerItem = (Speaker) childItem;

                if (speakerItem.getImage() != null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(speakerItem.getImage(), 0, speakerItem.getImage().length);
                    Drawable d = new BitmapDrawable(this._context.getResources(), bmp);

                    ivProfilePicture.setImageDrawable(d);

                    // ivProfilePicture.setBackground(d);
                    ivProfilePicture.setVisibility(View.VISIBLE);
                    tvCircle.setVisibility(GONE);
                } else {
                    ivProfilePicture.setVisibility(GONE);
                    tvCircle.setVisibility(View.VISIBLE);
                    String[] nameArr = speakerItem.getName().split(" ");
                    if (nameArr.length == 1) {
                        tvCircle.setText(nameArr[0].charAt(0));
                    } else if (nameArr.length > 1) {
                        tvCircle.setText(nameArr[0].charAt(0) + nameArr[1].charAt(0));
                    }
                }

                tvName.setText(speakerItem.getName());
            } else if (childItem instanceof  Attendee) {
                Attendee attendeeItem = (Attendee) childItem;

                if (attendeeItem.getImage() != null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(attendeeItem.getImage(), 0, attendeeItem.getImage().length);
                    Drawable d = new BitmapDrawable(this._context.getResources(), bmp);

                    ivProfilePicture.setImageDrawable(d);

                    // ivProfilePicture.setBackground(d);

                    ivProfilePicture.setVisibility(View.VISIBLE);
                    tvCircle.setVisibility(GONE);
                } else {
                    ivProfilePicture.setVisibility(GONE);
                    tvCircle.setVisibility(View.VISIBLE);
                    String[] nameArr = attendeeItem.getName().split(" ");
                    if (nameArr.length == 1) {
                        tvCircle.setText(nameArr[0].substring(0,2));
                    } else if (nameArr.length > 1) {
                        tvCircle.setText(nameArr[0].substring(0,1) + nameArr[1].substring(0,1));
                    } else {
                        tvCircle.setText("");
                    }
                }

                tvName.setText(attendeeItem.getName());
            }
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_view_group_header, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        TextView tvArrow = (TextView) convertView.findViewById(R.id.tvArrow);

        tvArrow.setText("Show More ...");

        if (isExpanded == true) {
            tvArrow.setText("Show Less ...");
        } else {
            tvArrow.setText("Show More ...");
        }

        // We hide the first header.
        /*
        if (groupPosition == 0) {
            tvArrow.setVisibility(GONE);
            convertView.setVisibility(GONE);
            section1Header = convertView;
        }
        */

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
