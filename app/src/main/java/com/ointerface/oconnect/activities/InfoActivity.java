package com.ointerface.oconnect.activities;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ointerface.oconnect.R;
import com.ointerface.oconnect.util.AppUtil;

import java.text.SimpleDateFormat;

import static android.view.View.GONE;

public class InfoActivity extends OConnectBaseActivity {

    private TextView tvVenueInfo;
    private TextView tvAddressInfo;
    private TextView tvAnnouncementInfo;
    private TextView tvDescriptionInfo;

    private TextView tvConferenceTitle;
    private TextView tvConferenceDateRange;

    private ImageView ivWebsite;
    private ImageView ivParking;
    private ImageView ivTravel;
    private ImageView ivContactUs;

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

        ivWebsite = (ImageView) findViewById(R.id.ivWebsite);
        ivParking = (ImageView) findViewById(R.id.ivParking);
        ivTravel = (ImageView) findViewById(R.id.ivTravel);
        ivContactUs = (ImageView) findViewById(R.id.ivContactUs);

        ivWebsite.setBackground(AppUtil.changeDrawableColor(InfoActivity.this, R.drawable.icon_globe, AppUtil.getPrimaryThemColorAsInt()));
        ivParking.setBackground(AppUtil.changeDrawableColor(InfoActivity.this, R.drawable.icon_parking, AppUtil.getPrimaryThemColorAsInt()));
        ivTravel.setBackground(AppUtil.changeDrawableColor(InfoActivity.this, R.drawable.icon_travel, AppUtil.getPrimaryThemColorAsInt()));
        ivContactUs.setBackground(AppUtil.changeDrawableColor(InfoActivity.this, R.drawable.icon_envelop, AppUtil.getPrimaryThemColorAsInt()));

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

    public void websiteClicked (View view) {
        Intent i = new Intent(InfoActivity.this, WebViewActivity.class);
        i.putExtra("TITLE", "");
        i.putExtra("URL", selectedConference.getWebsite());
        i.putExtra("BACK_TEXT", "Back");
        i.putExtra("OPEN", "Open In Browser");
        startActivity(i);
    }

    public void parkingClicked (View view) {
        Intent i = new Intent(InfoActivity.this, ParkingActivity.class);
        startActivity(i);
    }

    public void travelClicked (View view) {
        AppUtil.displayNotImplementedDialog(InfoActivity.this);
    }

    public void contactUsClicked (View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { selectedConference.getContactEmail() });
        intent.putExtra(Intent.EXTRA_SUBJECT, "oConnect");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(Intent.createChooser(intent, ""));
    }
}
