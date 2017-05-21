package com.ointerface.oconnect.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by AnthonyDoan on 5/6/17.
 */

public class PredAnalyticsMatches extends RealmObject {
    @Required
    @PrimaryKey
    private String objectId = "";
    private double scoreConferences = 0.0;
    private double scoreLocation = 0.0;
    private boolean isRejected = false;
    private double scoreSurveyAnswers = 0.0;
    private boolean isDeleted = false;
    private boolean isAccepted = false;
    private double score = 0.0;
    private double scoreBio = 0.0;
    private String id1 = "";
    private String id2 = "";
    private double scoreInterests = 0.0;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public double getScoreConferences() {
        return scoreConferences;
    }

    public void setScoreConferences(double scoreConferences) {
        this.scoreConferences = scoreConferences;
    }

    public double getScoreLocation() {
        return scoreLocation;
    }

    public void setScoreLocation(double scoreLocation) {
        this.scoreLocation = scoreLocation;
    }

    public boolean isRejected() {
        return isRejected;
    }

    public void setRejected(boolean rejected) {
        isRejected = rejected;
    }

    public double getScoreSurveyAnswers() {
        return scoreSurveyAnswers;
    }

    public void setScoreSurveyAnswers(double scoreSurveyAnswers) {
        this.scoreSurveyAnswers = scoreSurveyAnswers;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getScoreBio() {
        return scoreBio;
    }

    public void setScoreBio(double scoreBio) {
        this.scoreBio = scoreBio;
    }

    public String getId1() {
        return id1;
    }

    public void setId1(String id1) {
        this.id1 = id1;
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public double getScoreInterests() {
        return scoreInterests;
    }

    public void setScoreInterests(double scoreInterests) {
        this.scoreInterests = scoreInterests;
    }
}
