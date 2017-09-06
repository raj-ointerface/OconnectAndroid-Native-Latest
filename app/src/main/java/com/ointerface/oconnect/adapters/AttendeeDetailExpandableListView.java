package com.ointerface.oconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.data.Attendee;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.util.AppUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;

import static android.view.View.GONE;

/**
 * Created by AnthonyDoan on 5/20/17.
 */

public class AttendeeDetailExpandableListView  extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader;
    private List<Integer> _listHeaderNumber;
    private List<Boolean> _listGroupHasListView;
    private HashMap<Integer, Integer> _listChildCount;
    private HashMap<Integer, ArrayList<String>> _listChildItems;
    private Attendee _listAttendee;
    private Person _listPerson;

    public AttendeeDetailExpandableListView(Context context, List<String> listDataHeader,
                                                  List<Integer> listHeaderNumber,
                                                  HashMap<Integer, Integer> listChildCount,
                                                  List<Boolean> listGroupHasListView,
                                                  HashMap<Integer, ArrayList<String>> listChildItems,
                                                  Attendee listAttendee,
                                                  Person listPerson) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listHeaderNumber = listHeaderNumber;
        this._listChildCount = listChildCount;
        this._listGroupHasListView = listGroupHasListView;
        this._listChildItems = listChildItems;
        this._listAttendee = listAttendee;
        this._listPerson = listPerson;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listChildCount.get(this._listHeaderNumber.get(groupPosition));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listChildCount.get(this._listHeaderNumber.get(groupPosition)).intValue();
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
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        Person person = realm.where(Person.class).equalTo("objectId", _listAttendee.getUserLink()).findFirst();

        String groupItemStr = (String) getGroup(groupPosition);

        LayoutInflater infalInflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (groupItemStr.equalsIgnoreCase("About")) {

            convertView = infalInflater.inflate(R.layout.attendee_detail_about_list_view_item, null);

            ImageView ivInfo = (ImageView) convertView.findViewById(R.id.ivParticipantJobTitle);
            ImageView ivSuitcase = (ImageView) convertView.findViewById(R.id.ivParticipantOrg);
            ImageView ivLightBuld = (ImageView) convertView.findViewById(R.id.ivParticipantInterests);
            ImageView ivHouse = (ImageView) convertView.findViewById(R.id.ivParticipantLocation);
            // ImageView ivPicture = (ImageView) convertView.findViewById(R.id.ivParticipantPicture);

            // ivPicture.setVisibility(View.INVISIBLE);

            RelativeLayout rlContainer = (RelativeLayout) convertView.findViewById(R.id.rlContainer);
            RelativeLayout rlContent = (RelativeLayout) convertView.findViewById(R.id.rlContent);

            rlContainer.setClipChildren(false);
            rlContent.setClipChildren(false);

            ivInfo.setBackground(AppUtil.changeDrawableColor(_context, R.drawable.icon_participants_info, AppUtil.getPrimaryThemColorAsInt()));
            ivSuitcase.setBackground(AppUtil.changeDrawableColor(_context, R.drawable.icon_paricipants_suitcase, AppUtil.getPrimaryThemColorAsInt()));
            ivLightBuld.setBackground(AppUtil.changeDrawableColor(_context, R.drawable.icon_participants_light_bulb, AppUtil.getPrimaryThemColorAsInt()));
            ivHouse.setBackground(AppUtil.changeDrawableColor(_context, R.drawable.icon_participants_house, AppUtil.getPrimaryThemColorAsInt()));


            TextView tvName = (TextView) convertView.findViewById(R.id.tvParticipantName);
            TextView tvJobTitle = (TextView) convertView.findViewById(R.id.tvParticipantJobTitle);
            TextView tvOrg = (TextView) convertView.findViewById(R.id.tvParticipantOrg);
            TextView tvInterests = (TextView) convertView.findViewById(R.id.tvParticipantInterests);
            TextView tvLocation = (TextView) convertView.findViewById(R.id.tvParticipantLocation);

            if (_listAttendee != null) {
                tvName.setText(_listAttendee.getName());

                if (_listAttendee.getJob() != null && !_listAttendee.getJob().equalsIgnoreCase("")) {
                    tvJobTitle.setText(_listAttendee.getJob());
                    tvJobTitle.setVisibility(View.VISIBLE);
                    ivInfo.setVisibility(View.VISIBLE);
                } else {
                    tvJobTitle.setVisibility(GONE);
                    ivInfo.setVisibility(GONE);
                }

                if (_listAttendee.getOrganization() != null && !_listAttendee.getOrganization().equalsIgnoreCase("")) {
                    tvOrg.setText(_listAttendee.getOrganization());
                    tvOrg.setVisibility(View.VISIBLE);
                    ivSuitcase.setVisibility(View.VISIBLE);
                } else {
                    tvOrg.setVisibility(GONE);
                    ivSuitcase.setVisibility(GONE);
                }

                // TODO SET INTERESTS
                tvInterests.setVisibility(GONE);
                ivLightBuld.setVisibility(GONE);

                if (_listAttendee.getLocation() != null && !_listAttendee.getLocation().equalsIgnoreCase("")) {
                    tvLocation.setText(_listAttendee.getLocation());
                    tvLocation.setVisibility(View.VISIBLE);
                    ivHouse.setVisibility(View.VISIBLE);
                } else {
                    tvLocation.setVisibility(GONE);
                    ivHouse.setVisibility(GONE);
                }
            } else if (_listPerson != null) {
                tvName.setText(_listPerson.getFirstName() + " " + _listPerson.getLastName());

                if (_listPerson.getJob() != null && !_listPerson.getJob().equalsIgnoreCase("")) {
                    tvJobTitle.setText(_listPerson.getJob());
                    tvJobTitle.setVisibility(View.VISIBLE);
                    ivInfo.setVisibility(View.VISIBLE);
                } else {
                    tvJobTitle.setVisibility(GONE);
                    ivInfo.setVisibility(GONE);
                }

                if (_listPerson.getOrg() != null && !_listPerson.getOrg().equalsIgnoreCase("")) {
                    tvOrg.setText(_listPerson.getOrg());
                    tvOrg.setVisibility(View.VISIBLE);
                    ivSuitcase.setVisibility(View.VISIBLE);
                } else {
                    tvOrg.setVisibility(GONE);
                    ivSuitcase.setVisibility(GONE);
                }

                // TODO SET INTERESTS
                tvInterests.setVisibility(GONE);
                ivLightBuld.setVisibility(GONE);

                if (_listPerson.getLocation() != null && !_listPerson.getLocation().equalsIgnoreCase("")) {
                    tvLocation.setText(_listPerson.getLocation());
                    tvLocation.setVisibility(View.VISIBLE);
                    ivHouse.setVisibility(View.VISIBLE);
                } else {
                    tvLocation.setVisibility(GONE);
                    ivHouse.setVisibility(GONE);
                }
            }
        }


        realm.close();

        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        // Session session = (Session) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.attendee_detail_list_header, null);
        }

        TextView tvAttendeeDetailName = (TextView) convertView.findViewById(R.id.tvAttendeeDetailName);

        tvAttendeeDetailName.setText((String) _listDataHeader.get(groupPosition));

        TextView tvArrow = (TextView) convertView.findViewById(R.id.tvArrow);

        tvArrow.setText(">");

        if (isExpanded == true) {
            tvArrow.setRotation(90);
        } else {
            tvArrow.setRotation(0);
        }

        return convertView;
    }
}
