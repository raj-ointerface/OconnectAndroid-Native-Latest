package com.ointerface.oconnect.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ointerface.oconnect.R;
import com.ointerface.oconnect.data.SurveyQuestionAnswer;
import com.ointerface.oconnect.util.AppUtil;

import java.util.ArrayList;

/**
 * Created by AnthonyDoan on 6/4/17.
 */

public class SurveyAnswerListViewAdapter extends BaseAdapter {
    public Context context;

    private LayoutInflater mInflater;

    public ArrayList<SurveyQuestionAnswer> mData = new ArrayList<SurveyQuestionAnswer>();

    public SurveyAnswerListViewAdapter(Context context, ArrayList<SurveyQuestionAnswer> mDataArg) {
        super();
        this.context = context;
        this.mData = mDataArg;

        mInflater = LayoutInflater.from(context);
    }

    public void addItem(final SurveyQuestionAnswer item) {
        mData.add(item);
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position).getTitle();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String answer = mData.get(position).getTitle();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.survey_list_view_item, null);
        }

        TextView tvAnswer = (TextView) convertView.findViewById(R.id.tvAnswer);

        tvAnswer.setText(answer);

        GradientDrawable drawable = (GradientDrawable) tvAnswer.getBackground();
        drawable.setStroke(AppUtil.convertDPToPXInt(context, 4), AppUtil.getPrimaryThemColorAsInt());

        tvAnswer.setBackground(drawable);

        return convertView;
    }
}



