package com.ointerface.oconnect.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.ointerface.oconnect.R;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.w3c.dom.Text;

import static android.view.View.GONE;

public class MapActivity extends OConnectBaseActivity {

    private WebView wvMap;

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
        ivHelp.setVisibility(View.VISIBLE);
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

        try {
            String mapObjectId = getIntent().getStringExtra("objectId");

            ParseObject object = ParseQuery.getQuery("Maps").whereEqualTo("objectId", mapObjectId).getFirst();

            ParseFile parseImage = (ParseFile) object.getParseFile("map");

            Log.d("Parse", parseImage.getUrl());

            // ImageView ivMap = (ImageView) findViewById(R.id.ivMap);

            // AppUtil.loadImages(parseImage, ivMap);

            String doc = "<html><body style='margin:0;padding:0;'><iframe src='http://docs.google.com/gview?embedded=true&url=" + parseImage.getUrl() + "'" +
            "width='100%' height='100%'" +
            "style='border: none;margin:0;padding:0;'></iframe></body></html>";

            wvMap.getSettings().setJavaScriptEnabled(true);
            wvMap.loadData( doc , "text/html",  "UTF-8");

            //wvMap.loadUrl("http://docs.google.com/gview?embedded=true&url=" + parseImage.getUrl());
        } catch (Exception ex) {
            Log.d("MapActivity", ex.getMessage());
        }
    }

}
