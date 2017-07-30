package com.ointerface.oconnect.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.HelpViewPagerActivity;
import com.ointerface.oconnect.activities.InfoActivity;
import com.ointerface.oconnect.activities.ScheduleActivity;
import com.ointerface.oconnect.util.AppConfig;
import com.ointerface.oconnect.util.AppUtil;

import org.w3c.dom.Text;

/**
 * Created by AnthonyDoan on 6/10/17.
 */

public class OverlayDialogFragment extends DialogFragment {

    public static OverlayType overlayType = OverlayType.Partificpants1;
    public static Activity activity;

    public enum OverlayType { Partificpants1, Schedule1, Schedule2, Schedule3, Schedule4 }

    private CheckBox cbDontShowAgain;
    private TextView tvTapContinue;
    private TextView tvDontShowAgain;

    private ImageView ivAnchorIcon;
    private RelativeLayout rlViewToMove;

    public static View schedule2AnchorView;
    public static View schedule3AnchorView;
    public static View schedule4AnchorView;
    public View currentAnchorView;

    private Context context;

    public OverlayDialogFragment() {

    }

    public static OverlayDialogFragment newInstance(Activity activityArg, OverlayType type) {
        OverlayDialogFragment fragment = new OverlayDialogFragment();

        activity = activityArg;

        overlayType = type;

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = null;

        switch (overlayType)
        {
            case Partificpants1:
                rootView = inflater.inflate(R.layout.participants_overlay, container, false);
                break;
            case Schedule1:
                rootView = inflater.inflate(R.layout.schedule_overlay_1, container, false);
                break;
            case Schedule2:
                rootView = inflater.inflate(R.layout.schedule_overlay_2, container, false);
                break;
            case Schedule3:
                rootView = inflater.inflate(R.layout.schedule_overlay_3, container, false);
                break;
            case Schedule4:
                rootView = inflater.inflate(R.layout.schedule_overlay_4, container, false);
                break;
        }

        Button btnClose = (Button) rootView.findViewById(R.id.btnClose);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        rootView.setBackgroundColor(AppConfig.hiddenGreyBackgroundColor);
        rootView.getBackground().setAlpha(150);

        cbDontShowAgain = (CheckBox) rootView.findViewById(R.id.cbDontShowAgain);

        if (overlayType == OverlayType.Schedule1 ||
                overlayType == OverlayType.Schedule2 ||
                overlayType == OverlayType.Schedule3 ||
                overlayType == OverlayType.Schedule4) {
            cbDontShowAgain.setChecked(AppUtil.getScheduleTutorialShown(activity));
        }

        tvDontShowAgain = (TextView) rootView.findViewById(R.id.tvDontShowAgain);

        tvDontShowAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbDontShowAgain.setChecked(!cbDontShowAgain.isChecked());
            }
        });

        tvTapContinue = (TextView) rootView.findViewById(R.id.tvTapContinue);

        tvTapContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (overlayType) {
                    case Partificpants1:
                        AppUtil.setParticipantsTutorialShown(activity, cbDontShowAgain.isChecked());
                        dismiss();
                        break;
                    case Schedule1:
                        AppUtil.setScheduleTutorialShow(activity, cbDontShowAgain.isChecked());
                        dismiss();

                        if (cbDontShowAgain.isChecked() == false) {
                            FragmentManager fm = ((ScheduleActivity) activity).getSupportFragmentManager();
                            OverlayDialogFragment dialogFragment = newInstance(activity, OverlayType.Schedule2);
                            dialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDialog);
                            dialogFragment.show(fm, OverlayType.Schedule2.name());
                        }
                        break;
                    case Schedule2:
                        AppUtil.setScheduleTutorialShow(activity, cbDontShowAgain.isChecked());
                        dismiss();

                        if (cbDontShowAgain.isChecked() == false) {
                            FragmentManager fm2 = ((ScheduleActivity) activity).getSupportFragmentManager();
                            OverlayDialogFragment dialogFragment2 = newInstance(activity, OverlayType.Schedule3);
                            dialogFragment2.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDialog);
                            dialogFragment2.show(fm2, OverlayType.Schedule3.name());
                        }
                        break;
                    case Schedule3:
                        AppUtil.setScheduleTutorialShow(activity, cbDontShowAgain.isChecked());
                        dismiss();

                        if (cbDontShowAgain.isChecked() == false) {
                            FragmentManager fm3 = ((ScheduleActivity) activity).getSupportFragmentManager();
                            OverlayDialogFragment dialogFragment3 = newInstance(activity, OverlayType.Schedule4);
                            dialogFragment3.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDialog);
                            dialogFragment3.show(fm3, OverlayType.Schedule4.name());
                        }
                        break;
                    case Schedule4:
                        AppUtil.setScheduleTutorialShow(activity, cbDontShowAgain.isChecked());
                        dismiss();
                        break;
                }
            }
        });

        getDialog().setTitle(overlayType.name());

        rlViewToMove = (RelativeLayout) rootView.findViewById(R.id.top_layer);

        ivAnchorIcon = (ImageView) rootView.findViewById(R.id.ivAnchorIcon);

        switch (overlayType)
        {
            case Schedule2:
                currentAnchorView = schedule2AnchorView;
                ivAnchorIcon.setBackground(AppUtil.changeDrawableColor(activity, R.drawable.ic_person, AppConfig.whiteColor));
                positionAnchorView();
                break;
            case Schedule3:
                currentAnchorView = schedule3AnchorView;
                ivAnchorIcon.setBackground(AppUtil.changeDrawableColor(activity, R.drawable.ic_hamburger, AppConfig.whiteColor));
                positionAnchorView();
                break;
            case Schedule4:
                currentAnchorView = schedule4AnchorView;
                ivAnchorIcon.setBackground(AppUtil.changeDrawableColor(activity, R.drawable.icon_profile_lanyard, AppConfig.whiteColor));
                positionAnchorView();
                break;
        }

        return rootView;
    }

    public void positionAnchorView() {
        rlViewToMove.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                rlViewToMove.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int statusBarHeight = 0;
                try {
                    Rect rectangle = new Rect();
                    Window window = ((Activity) context).getWindow();
                    window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
                    statusBarHeight = rectangle.top;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final int[] textViewScreenPosition = new int[2];
                currentAnchorView.getLocationOnScreen(textViewScreenPosition);
                float textX = textViewScreenPosition[0];
                float textY = textViewScreenPosition[1] - AppUtil.convertDPToPXInt(activity, 20);

                rlViewToMove.setX(textX);
                rlViewToMove.setY(textY);
            }
        });
    }
}
