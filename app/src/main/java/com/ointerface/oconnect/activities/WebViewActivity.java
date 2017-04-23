package com.ointerface.oconnect.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ointerface.oconnect.R;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import static android.view.View.GONE;

public class WebViewActivity extends OConnectBaseActivity {
    private WebView wvContent;
    private ProgressDialog progDailog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        super.onCreateDrawer();

        ivProfileLanyard.setVisibility(GONE);
        ivHelp.setVisibility(View.GONE);
        ivRightToolbarIcon.setVisibility(View.GONE);
        ivSearch.setVisibility(GONE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

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

        String title = getIntent().getStringExtra("TITLE");
        String backText = getIntent().getStringExtra("BACK_TEXT");
        final String url = getIntent().getStringExtra("URL");
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

            wvContent.loadUrl(url);

        } catch (Exception ex) {
            Log.d("WebViewActivity", ex.getMessage());
        }
    }

}
