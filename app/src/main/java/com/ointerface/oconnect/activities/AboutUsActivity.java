package com.ointerface.oconnect.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.data.Organization;
import com.ointerface.oconnect.util.AppConfig;
import com.ointerface.oconnect.util.AppUtil;

import io.realm.Realm;

import static android.view.View.GONE;

public class AboutUsActivity extends OConnectBaseActivity {
    private TextView tvConferenceName;
    private TextView tvAppVersion;
    private TextView tvWebsite;
    private ImageView ivConferenceImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        super.onCreateDrawer();

        tvToolbarTitle.setText("About Us");

        ivProfileLanyard.setVisibility(GONE);
        ivSearch.setVisibility(GONE);
        ivHelp.setVisibility(View.VISIBLE);
        ivRightToolbarIcon.setVisibility(View.VISIBLE);

        tvConferenceName = (TextView) findViewById(R.id.tvConferenceTitle);
        tvConferenceName.setText(selectedConference.getName());

        tvAppVersion = (TextView) findViewById(R.id.tvVersionNumber);
        try {
            tvAppVersion.setText("Version " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (Exception ex) {
            Log.d("AboutUs", ex.getMessage());
        }

        tvWebsite = (TextView) findViewById(R.id.tvConferenceWebsite);
        tvWebsite.setText(selectedConference.getWebsite());

        ivConferenceImage = (ImageView) findViewById(R.id.ivConferenceImage);

        if (selectedConference.getImage() != null) {
            Bitmap bm = BitmapFactory.decodeByteArray(selectedConference.getImage(), 0, selectedConference.getImage().length);

            ivConferenceImage.setImageBitmap(bm);
        }
    }

}
