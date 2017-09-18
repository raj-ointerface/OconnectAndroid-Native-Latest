package com.ointerface.oconnect.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.util.AppUtil;

/**
 * Created by gagan on 9/17/17.
 * This class is specifically created for the UI pop up that asks the user if they want to make
 * better connections.
 * Uses a custom layout called activity_survey_option_dialog
 */

public class CustomDialog {

    public void showDialog(final Context context){
        final Dialog alertDialog = new Dialog(context);

        alertDialog.setCancelable(false);

        alertDialog.setContentView(R.layout.activity_survey_option_dialog);

        Button btn_start = (Button) alertDialog.findViewById(R.id.btn_start);


        btn_start.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());

        final CheckBox checkBox = (CheckBox) alertDialog.findViewById(R.id.checkBox);

        TextView text = (TextView) alertDialog.findViewById(R.id.text_dialog);

        ImageView close = (ImageView) alertDialog.findViewById(R.id.cross);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                AppUtil.setSurveyShown(context, true);
                Intent i = new Intent(context, AnalyticsSurveyActivity.class);
                context.startActivity(i);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    AppUtil.setSurveyShown(context, true);
                }
                alertDialog.dismiss();
            }
        });

        WindowManager manager = (WindowManager) context.getSystemService(Activity.WINDOW_SERVICE);
        int width = manager.getDefaultDisplay().getWidth();
        Window window = alertDialog.getWindow();
        window.setLayout(width,ViewGroup.LayoutParams.WRAP_CONTENT);

        alertDialog.show();

    }
}
