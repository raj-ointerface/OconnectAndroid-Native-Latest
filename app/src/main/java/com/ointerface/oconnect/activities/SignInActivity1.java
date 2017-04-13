package com.ointerface.oconnect.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ointerface.oconnect.R;

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
                overridePendingTransition(R.anim.hold, R.anim.disappear_to_bottom);
            }
        });
    }
}
