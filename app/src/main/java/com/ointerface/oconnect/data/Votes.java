package com.ointerface.oconnect.data;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by AnthonyDoan on 7/9/17.
 */

public class Votes extends RealmObject {
    @Required
    @PrimaryKey
    private String objectId = "";
    private Date updatedAt = null;
    private String type = "";
    // objectIds
    private String question = "";
    private String user = "";
    private Boolean lastVoteIsUp = false;

    public Boolean getLastVoteIsUp() {
        return lastVoteIsUp;
    }

    public void setLastVoteIsUp(Boolean lastVoteIsUp) {
        this.lastVoteIsUp = lastVoteIsUp;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
