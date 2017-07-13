package com.ointerface.oconnect.data;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by AnthonyDoan on 7/9/17.
 */

public class DiscussionBoard extends RealmObject {
    @Required
    @PrimaryKey
    private String objectId = "";
    private Date updatedAt = null;
    private String moderatorName = "";
    private Boolean hasQuestions = false;
    // objectId
    private String event = "";
    // objectId
    private String conference = "";

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

    public String getModeratorName() {
        return moderatorName;
    }

    public void setModeratorName(String moderatorName) {
        this.moderatorName = moderatorName;
    }

    public Boolean getHasQuestions() {
        return hasQuestions;
    }

    public void setHasQuestions(Boolean hasQuestions) {
        this.hasQuestions = hasQuestions;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getConference() {
        return conference;
    }

    public void setConference(String conference) {
        this.conference = conference;
    }
}
