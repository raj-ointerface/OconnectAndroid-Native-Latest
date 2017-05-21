package com.ointerface.oconnect.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.util.AppUtil;

import java.io.IOException;
import java.util.List;

import static android.view.View.GONE;

public class QRCodeScannerActivity extends OConnectBaseActivity {
    private CompoundBarcodeView barcodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);
        super.onCreateDrawer();

        tvToolbarTitle.setText("QR Code Reader");

        ivProfileLanyard.setVisibility(GONE);
        ivHelp.setVisibility(GONE);
        ivRightToolbarIcon.setVisibility(GONE);
        ivSearch.setVisibility(GONE);

        tvEdit.setVisibility(View.VISIBLE);
        tvEdit.setText("Done");

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCodeScannerActivity.this.finish();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Drawable dr = getResources().getDrawable(R.drawable.icon_back);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();

        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, AppUtil.convertDPToPXInt(QRCodeScannerActivity.this, 20), AppUtil.convertDPToPXInt(QRCodeScannerActivity.this, 20), true));

        // toolbar.setNavigationIcon(d);

        getSupportActionBar().setHomeAsUpIndicator(d);

        /*
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
        */

        /*
        try {

            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

            startActivityForResult(intent, 0);

        } catch (Exception e) {

            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);

        }
        */

        barcodeView = (CompoundBarcodeView) findViewById(R.id.zxing_barcode_scanner);

        String[] permission = {"android.permission.CAMERA"};

        if (ContextCompat.checkSelfPermission(QRCodeScannerActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(QRCodeScannerActivity.this,
                    permission, 10);
        }else{
            barcodeView.decodeSingle(callback);
        }


        // barcodeView.decodeContinuous(callback);
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                barcodeView.setStatusText(result.getText());

                Intent i = new Intent(QRCodeScannerActivity.this, WebViewActivity.class);
                i.putExtra("TITLE", "");
                i.putExtra("URL", result.getText());
                i.putExtra("BACK_TEXT", "QR Code Reader");
                i.putExtra("OPEN", "Open In Browser");
                startActivity(i);
            }

            //Do something with code result
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    @Override
    public void onResume() {
        barcodeView.resume();
        barcodeView.decodeSingle(callback);
        super.onResume();
    }

    @Override
    public void onPause() {
        barcodeView.pause();
        super.onPause();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 10:
                if(resultCode == RESULT_OK) {
                    barcodeView.decodeSingle(callback);
                }
                break;
        }
    }
}
