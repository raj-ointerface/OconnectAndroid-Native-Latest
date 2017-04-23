package com.ointerface.oconnect.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.ointerface.oconnect.R;

import java.text.SimpleDateFormat;

import static android.view.View.GONE;

public class InfoActivity extends OConnectBaseActivity {

    private TextView tvVenueInfo;
    private TextView tvAddressInfo;
    private TextView tvAnnouncementInfo;
    private TextView tvDescriptionInfo;

    private TextView tvConferenceTitle;
    private TextView tvConferenceDateRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        super.onCreateDrawer();

        if (selectedConference.getToolbarLabelInfo() != null &&
                !selectedConference.getToolbarLabelInfo().equalsIgnoreCase("")) {
            tvToolbarTitle.setText(selectedConference.getToolbarLabelInfo());
        } else {
            tvToolbarTitle.setText("Info");
        }

        ivSearch.setVisibility(GONE);
        ivHelp.setVisibility(View.VISIBLE);
        ivRightToolbarIcon.setVisibility(View.VISIBLE);
        ivProfileLanyard.setVisibility(GONE);

        tvVenueInfo = (TextView) findViewById(R.id.tvVenueInfo);
        tvAddressInfo = (TextView) findViewById(R.id.tvAddressInfo);
        tvAnnouncementInfo = (TextView) findViewById(R.id.tvAnnouncementInfo);
        tvDescriptionInfo = (TextView) findViewById(R.id.tvDescriptionInfo);

        tvVenueInfo.setText(selectedConference.getVenue());

        tvAddressInfo.setText(selectedConference.getAddress() + ", " +
                selectedConference.getCity() + ", " + selectedConference.getState() +
        " " + selectedConference.getZip());

        tvAnnouncementInfo.setText(selectedConference.getAnnouncements());

        tvDescriptionInfo.setText(selectedConference.getSummary());

        tvConferenceTitle = (TextView) findViewById(R.id.tvConferenceTitle);

        tvConferenceTitle.setText(selectedConference.getName());

        tvConferenceDateRange = (TextView) findViewById(R.id.tvConferenceDateRange);

        SimpleDateFormat sdFormat1 = new SimpleDateFormat("MMM d");
        SimpleDateFormat sdFormat2 = new SimpleDateFormat("MMM d yyyy");

        if (selectedConference.getEndTime() == null) {
            tvConferenceDateRange.setText(sdFormat2.format(selectedConference.getStartTime()));
        } else {
            tvConferenceDateRange.setText(sdFormat1.format(selectedConference.getStartTime()) +
                    " - " + sdFormat2.format(selectedConference.getEndTime()));
        }
    }

}
