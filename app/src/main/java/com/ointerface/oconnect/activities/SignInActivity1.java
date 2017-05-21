package com.ointerface.oconnect.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.CustomSplashActivity;
import com.ointerface.oconnect.MainSplashActivity;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.data.Organization;
import com.ointerface.oconnect.util.AppUtil;

import io.realm.Realm;

public class SignInActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_1);

        Button btnClose = (Button) findViewById(R.id.btnClose);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInActivity1.this.finish();
                Intent i = new Intent(SignInActivity1.this, DashboardActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up );
            }
        });

        Button btnSignIn = (Button) findViewById(R.id.btnSignIn);

        btnSignIn.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInActivity1.this.finish();
                Intent i = new Intent(SignInActivity1.this, SignInActivity2.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up );
            }
        });

        ImageView ivOrgLogo = (ImageView) findViewById(R.id.ivSignInLogo);

        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        Organization result = realm.where(Organization.class).equalTo("objectId", OConnectBaseActivity.selectedConference.getOrganization()).findFirst();

        if (result != null) {
            if (result.getImage() != null) {
                Bitmap bm = BitmapFactory.decodeByteArray(result.getImage(), 0, result.getImage().length);

                ivOrgLogo.setImageBitmap(bm);
            }
        }
    }
}
