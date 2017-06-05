package com.ointerface.oconnect.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by AnthonyDoan on 6/4/17.
 */

public class UserSurveyAnswer extends RealmObject {
    @Required
    @PrimaryKey
    private String objectId = "";

    private String questionId = "";
    private String questionAnswerIds = "";
    private String userId = "";

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionAnswerIds() {
        return questionAnswerIds;
    }

    public void setQuestionAnswerIds(String questionAnswerIds) {
        this.questionAnswerIds = questionAnswerIds;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
