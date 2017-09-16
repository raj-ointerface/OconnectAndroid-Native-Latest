package com.ointerface.oconnect.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.ointerface.oconnect.R;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import static android.view.View.GONE;

public class MapActivity extends OConnectBaseActivity {

    private WebView wvMap;

    private String doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        super.onCreateDrawer();

        if (selectedConference.getToolbarLabelMaps() != null &&
                !selectedConference.getToolbarLabelMaps().equalsIgnoreCase("")) {
            tvToolbarTitle.setText(selectedConference.getToolbarLabelMaps());
        } else {
            tvToolbarTitle.setText("Map");
        }

        ivProfileLanyard.setVisibility(GONE);
        ivConnections.setVisibility(View.VISIBLE);
        ivRightToolbarIcon.setVisibility(View.VISIBLE);
        ivSearch.setVisibility(GONE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Drawable dr = getResources().getDrawable(R.drawable.icon_back);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();

        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, AppUtil.convertDPToPXInt(MapActivity.this, 20), AppUtil.convertDPToPXInt(MapActivity.this, 20), true));

        // toolbar.setNavigationIcon(d);

        getSupportActionBar().setHomeAsUpIndicator(d);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapActivity.this.finish();
            }
        });

        tvHeaderBack.setVisibility(View.VISIBLE);
        ivHeaderBack.setVisibility(View.VISIBLE);

        ivHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapActivity.this.finish();
            }
        });

        tvHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapActivity.this.finish();
            }
        });

        tvHeaderBack.setText(tvToolbarTitle.getText());

        tvHeaderBack.bringToFront();

        wvMap = (WebView) findViewById(R.id.wvMap);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            String mapObjectId = getIntent().getStringExtra("objectId");

            ParseObject object = ParseQuery.getQuery("Maps").whereEqualTo("objectId", mapObjectId).getFirst();

            ParseFile parseImage = (ParseFile) object.getParseFile("map");

            Log.d("Parse", parseImage.getUrl());

            String urlEncodedString = parseImage.getUrl().replace(" ", "%20");

            Log.d("APD", urlEncodedString);

            doc = "<html><body style='margin:0;padding:0;'><iframe src='http://docs.google.com/gview?key=AIzaSyBq63wjlYlN0rINBaNZhDtNXJY6Ezv9_oE&embedded=true&url=" + urlEncodedString + "'" +
                    " width='100%' height='100%' " +
                    " style='border: none;margin:0;padding:0;'></iframe></body></html>";

            Log.d("APD", doc);

            if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapActivity.this,
                        new String[]{Manifest.permission.INTERNET}, 11);
                return;
            } else {
                wvMap.getSettings().setJavaScriptEnabled(true);
                wvMap.loadData( doc , "text/html",  "UTF-8");
            }

            //wvMap.loadUrl("http://docs.google.com/gview?embedded=true&url=" + parseImage.getUrl());
        } catch (Exception ex) {
            Log.d("MapActivity", ex.getMessage());
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 11:
                if(resultCode == RESULT_OK) {
                    wvMap.getSettings().setJavaScriptEnabled(true);
                    wvMap.loadData( doc , "text/html",  "UTF-8");
                }
                break;
        }
    }
}
