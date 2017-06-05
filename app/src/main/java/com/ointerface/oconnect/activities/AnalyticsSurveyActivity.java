package com.ointerface.oconnect.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.adapters.SurveyAnswerListViewAdapter;
import com.ointerface.oconnect.data.SurveyQuestion;
import com.ointerface.oconnect.data.SurveyQuestionAnswer;
import com.ointerface.oconnect.data.SurveyQuestionAnswerRelation;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.view.View.GONE;

public class AnalyticsSurveyActivity extends OConnectBaseActivity {
    private ArrayList<SurveyQuestion> questionsList = new ArrayList<SurveyQuestion>();
    private HashMap<String, ArrayList<SurveyQuestionAnswer>> mapAnswers = new HashMap<String, ArrayList<SurveyQuestionAnswer>>();
    private int currentQuestionNumber = 1;

    private TextView tvSurveyQuestion;
    private ListView lvSurvey;
    private SurveyAnswerListViewAdapter adapter;

    private TextView tvNext;

    private int selectedAnswerNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_survey);
        super.onCreateDrawer();

        tvToolbarTitle.setText("Survey");

        ivProfileLanyard.setVisibility(GONE);
        ivHelp.setVisibility(GONE);
        ivRightToolbarIcon.setVisibility(GONE);
        ivSearch.setVisibility(GONE);

        tvEdit.setVisibility(View.VISIBLE);
        tvEdit.setText("Skip");

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsSurveyActivity.this.finish();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Drawable dr = getResources().getDrawable(R.drawable.icon_back);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();

        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, AppUtil.convertDPToPXInt(AnalyticsSurveyActivity.this, 20), AppUtil.convertDPToPXInt(AnalyticsSurveyActivity.this, 20), true));

        // toolbar.setNavigationIcon(d);

        getSupportActionBar().setHomeAsUpIndicator(d);

        ivHeaderBack.setVisibility(View.VISIBLE);
        tvHeaderBack.setText("Back");
        tvHeaderBack.setVisibility(View.VISIBLE);


        ivHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsSurveyActivity.this.finish();
            }
        });

        tvHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsSurveyActivity.this.finish();
            }
        });

        tvSurveyQuestion = (TextView) findViewById(R.id.tvSurveyQuestion);
        lvSurvey = (ListView) findViewById(R.id.lvSurvey);

        lvSurvey.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < lvSurvey.getChildCount(); ++i) {
                    View childView = lvSurvey.getChildAt(i);

                    TextView answer = (TextView) childView.findViewById(R.id.tvAnswer);

                    if (answer != null) {
                        answer.setBackgroundResource(R.drawable.survey_answer_unselected);

                        GradientDrawable drawable = (GradientDrawable)answer.getBackground();
                        drawable.setStroke(AppUtil.convertDPToPXInt(AnalyticsSurveyActivity.this, 4), AppUtil.getPrimaryThemColorAsInt());
                        answer.setBackground(drawable);
                    }
                }

                TextView answer = (TextView) view.findViewById(R.id.tvAnswer);
                answer.setBackgroundResource(R.drawable.survey_answer_selected);

                GradientDrawable drawable = (GradientDrawable)answer.getBackground();
                drawable.setStroke(AppUtil.convertDPToPXInt(AnalyticsSurveyActivity.this, 4), AppUtil.getPrimaryThemColorAsInt());
                answer.setBackground(drawable);

                selectedAnswerNumber = position;
            }
        });

        tvNext = (TextView) findViewById(R.id.tvNext);
        tvNext.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());

        getSurveyData();

        displayCurrentQuestionAndAnswers();

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAnswerToParse();

                if (tvNext.getText().toString().equalsIgnoreCase("Finish")) {
                    AnalyticsSurveyActivity.this.finish();
                } else {
                    ++currentQuestionNumber;
                    displayCurrentQuestionAndAnswers();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getSurveyData() {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<SurveyQuestion> results = realm.where(SurveyQuestion.class).equalTo("conference", selectedConference.getObjectId()).findAllSorted("order", Sort.ASCENDING);

        if (results == null || results.size() == 0) {
            results = realm.where(SurveyQuestion.class).isNull("conference").or().equalTo("conference", "").findAllSorted("order", Sort.ASCENDING);
        }

        questionsList.addAll(results);

        ArrayList<SurveyQuestion> questionsWithAnswers = new ArrayList<SurveyQuestion>();

        for (int i = 0; i < questionsList.size(); ++i) {
            SurveyQuestion surveyQuestion = questionsList.get(i);
            RealmResults<SurveyQuestionAnswerRelation> relations = realm.where(SurveyQuestionAnswerRelation.class).equalTo("surveyQuestion", surveyQuestion.getObjectId()).findAll();

            if (relations != null) {
                ArrayList<SurveyQuestionAnswer> answersList = new ArrayList<SurveyQuestionAnswer>();
                for (int j = 0; j < relations.size(); ++j) {
                    SurveyQuestionAnswerRelation rel = relations.get(j);

                    SurveyQuestionAnswer answer = realm.where(SurveyQuestionAnswer.class).equalTo("objectId", rel.getSurveyQuestionAnswer()).findFirst();

                    if (answer != null) {
                        answersList.add(answer);
                    }
                }

                if (answersList.size() > 0) {
                    mapAnswers.put(surveyQuestion.getObjectId(), answersList);
                    questionsWithAnswers.add(surveyQuestion);
                }
            }
        }

        questionsList.clear();
        questionsList.addAll(questionsWithAnswers);
    }

    public void displayCurrentQuestionAndAnswers() {

        if (currentQuestionNumber < questionsList.size()) {
            tvNext.setText("Next");
        } else {
            tvNext.setText("Finish");
        }

        SurveyQuestion question = questionsList.get(currentQuestionNumber - 1);
        ArrayList<SurveyQuestionAnswer> answers = mapAnswers.get(question.getObjectId());

        tvSurveyQuestion.setText(question.getQuestion());

        adapter = new SurveyAnswerListViewAdapter(this, answers);

        lvSurvey.setAdapter(adapter);
    }

    public void saveAnswerToParse() {
        SurveyQuestion question = questionsList.get(currentQuestionNumber - 1);
        ArrayList<SurveyQuestionAnswer> answers = mapAnswers.get(question.getObjectId());
        SurveyQuestionAnswer answer = answers.get(selectedAnswerNumber);

        ParseObject parseObject = new ParseObject("UserSurveyAnswer");

        parseObject.put("questionId", question.getObjectId());

        parseObject.put("questionAnswerIds", answer.getObjectId());

        parseObject.put("userId", currentPerson.getObjectId());

        try {
            parseObject.save();
        } catch (Exception ex) {
            Log.d("Survey", ex.getMessage());
        }

        selectedAnswerNumber = 0;
    }
}
