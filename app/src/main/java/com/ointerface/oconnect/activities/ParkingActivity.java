package com.ointerface.oconnect.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.ointerface.oconnect.R;
import com.ointerface.oconnect.util.AppUtil;

import static android.view.View.GONE;

public class ParkingActivity extends OConnectBaseActivity {
    private TextView tvImportantInformationText;
    private TextView tvParkingDirectionsText;
    private TextView tvImportantInformation;
    private TextView tvParkingDirections;
    private View divider1;
    private View divider2;
    private View divider3;
    private TextView tvNoInfo;

    private float x1;
    private float x2;
    static final int MIN_DISTANCE = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);
        super.onCreateDrawer();

        ivProfileLanyard.setVisibility(GONE);
        ivHelp.setVisibility(View.GONE);
        ivRightToolbarIcon.setVisibility(View.GONE);
        ivSearch.setVisibility(GONE);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Drawable dr = getResources().getDrawable(R.drawable.icon_back);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();

        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, AppUtil.convertDPToPXInt(ParkingActivity.this, 20), AppUtil.convertDPToPXInt(ParkingActivity.this, 20), true));

        // toolbar.setNavigationIcon(d);

        getSupportActionBar().setHomeAsUpIndicator(d);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParkingActivity.this.finish();
            }
        });

        tvToolbarTitle.setText("Parking");

        tvHeaderBack.setVisibility(View.VISIBLE);
        ivHeaderBack.setVisibility(View.VISIBLE);

        ivHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParkingActivity.this.finish();
            }
        });

        tvHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParkingActivity.this.finish();
            }
        });

        tvHeaderBack.setText("Back");

        tvImportantInformationText = (TextView) findViewById(R.id.tvImportantInformationText);
        tvParkingDirectionsText = (TextView) findViewById(R.id.tvParkingDirectionsText);
        tvImportantInformation = (TextView) findViewById(R.id.tvImportantInformation);
        tvParkingDirections = (TextView) findViewById(R.id.tvParkingDirections);
        divider1 = (View) findViewById(R.id.divider1);
        divider2 = (View) findViewById(R.id.divider2);
        divider3 = (View) findViewById(R.id.divider3);
        tvNoInfo = (TextView) findViewById(R.id.tvNoInfo);

        boolean missingAllInformation = true;

        if (selectedConference.getParkingInformation() != null &&
                !selectedConference.getParkingInformation().equalsIgnoreCase("")) {
            tvImportantInformationText.setText(selectedConference.getParkingInformation());
            missingAllInformation = false;
        } else {
            tvImportantInformationText.setVisibility(GONE);
            tvImportantInformation.setVisibility(GONE);
            divider1.setVisibility(GONE);
            divider2.setVisibility(GONE);
        }

        if (selectedConference.getParkingLocation() != null &&
                !selectedConference.getParkingLocation().equalsIgnoreCase("")) {
            tvParkingDirectionsText.setText(selectedConference.getParkingLocation());
            missingAllInformation = false;
        } else {
            tvParkingDirections.setVisibility(GONE);
            tvParkingDirectionsText.setVisibility(GONE);
            divider3.setVisibility(GONE);
        }

        if (missingAllInformation = true) {
            tvNoInfo.setVisibility(View.VISIBLE);
        } else {
            tvNoInfo.setVisibility(GONE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    // Left to Right swipe action
                    if (x2 > x1)
                    {
                        // Toast.makeText(this, "Left to Right swipe [Next]", Toast.LENGTH_SHORT).show ();
                        ParkingActivity.this.finish();
                    }

                    // Right to left swipe action
                    else
                    {
                        // Toast.makeText(this, "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show ();
                    }

                }
                else
                {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
    }

}
