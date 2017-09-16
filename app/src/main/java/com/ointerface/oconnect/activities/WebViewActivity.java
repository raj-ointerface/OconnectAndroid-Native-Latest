package com.ointerface.oconnect.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ointerface.oconnect.R;
import com.ointerface.oconnect.util.AppUtil;

import static android.view.View.GONE;

public class WebViewActivity extends OConnectBaseActivity {
    private WebView wvContent;
    private ProgressDialog progDailog;

    private float x1;
    private float x2;
    static final int MIN_DISTANCE = 150;

    private boolean isEventBriteRegistration = false;
    private boolean isSurvey = false;

    private String globalUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        super.onCreateDrawer();

        ivProfileLanyard.setVisibility(GONE);
        ivConnections.setVisibility(View.GONE);
        ivRightToolbarIcon.setVisibility(View.GONE);
        ivSearch.setVisibility(GONE);

        isEventBriteRegistration = getIntent().getBooleanExtra("isRegistration", false);
        isSurvey = getIntent().getBooleanExtra("isSurvey", false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (isEventBriteRegistration == true || isSurvey == true) {
            ivConnections.setVisibility(View.VISIBLE);
            ivRightToolbarIcon.setVisibility(View.VISIBLE);

            tvHeaderBack.setVisibility(GONE);
            ivHeaderBack.setVisibility(GONE);
        } else {
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            Drawable dr = getResources().getDrawable(R.drawable.icon_back);
            Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();

            Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, AppUtil.convertDPToPXInt(WebViewActivity.this, 20), AppUtil.convertDPToPXInt(WebViewActivity.this, 20), true));

            // toolbar.setNavigationIcon(d);

            getSupportActionBar().setHomeAsUpIndicator(d);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebViewActivity.this.finish();
                }
            });

            tvHeaderBack.setVisibility(View.VISIBLE);
            ivHeaderBack.setVisibility(View.VISIBLE);

            ivHeaderBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebViewActivity.this.finish();
                }
            });

            tvHeaderBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebViewActivity.this.finish();
                }
            });

        }

        String title = getIntent().getStringExtra("TITLE");
        String backText = getIntent().getStringExtra("BACK_TEXT");
        final String url = getIntent().getStringExtra("URL");
        globalUrl = getIntent().getStringExtra("URL");

        String open = getIntent().getStringExtra("OPEN");

        if (!open.equalsIgnoreCase("")) {
            tvEdit.setVisibility(View.VISIBLE);
            tvEdit.setText(open);

            tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            });
        }

        tvToolbarTitle.setText(title);

        tvHeaderBack.setText(backText);

        tvHeaderBack.bringToFront();

        wvContent = (WebView) findViewById(R.id.wvContent);

        try {
            progDailog = ProgressDialog.show(WebViewActivity.this, "Loading","Please wait...", true);

            wvContent.getSettings().setJavaScriptEnabled(true);
            wvContent.getSettings().setLoadWithOverviewMode(true);
            wvContent.getSettings().setUseWideViewPort(true);
            wvContent.setWebViewClient(new WebViewClient(){

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    progDailog.show();
                    view.loadUrl(url);

                    return true;
                }
                @Override
                public void onPageFinished(WebView view, final String url) {
                    progDailog.dismiss();
                }
            });

            if (ContextCompat.checkSelfPermission(WebViewActivity.this, Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(WebViewActivity.this,
                        new String[]{Manifest.permission.INTERNET}, 12);
                return;
            } else {
                wvContent.loadUrl(url);
            }

        } catch (Exception ex) {
            Log.d("WebViewActivity", ex.getMessage());
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
                        WebViewActivity.this.finish();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 12:
                if(resultCode == RESULT_OK) {
                    wvContent.loadUrl(globalUrl);
                }
                break;
        }
    }
}
