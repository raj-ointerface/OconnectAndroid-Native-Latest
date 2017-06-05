package com.ointerface.oconnect.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by AnthonyDoan on 6/4/17.
 */

public class SurveyQuestionAnswerRelation extends RealmObject {
    @Required
    @PrimaryKey
    private String objectId = "";

    private String surveyQuestion = "";
    private String surveyQuestionAnswer = "";

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getSurveyQuestion() {
        return surveyQuestion;
    }

    public void setSurveyQuestion(String surveyQuestion) {
        this.surveyQuestion = surveyQuestion;
    }

    public String getSurveyQuestionAnswer() {
        return surveyQuestionAnswer;
    }

    public void setSurveyQuestionAnswer(String surveyQuestionAnswer) {
        this.surveyQuestionAnswer = surveyQuestionAnswer;
    }
}
