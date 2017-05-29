package com.ointerface.oconnect.data;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by AnthonyDoan on 4/16/17.
 */

public class Event extends RealmObject implements Serializable {
    @Required
    @PrimaryKey
    private String objectId = "";
    private boolean isNonTimedEvent = false;
    private Date endTime = null;
    private String name = "";
    private Date startTime = null;
    // contains objectId of Session
    private String session = "";
    private Date updatedAt = null;
    private String location = "";
    private RealmList<Speaker> speakers = new RealmList<Speaker>();
    private String info = "";

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public RealmList<Speaker> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(RealmList<Speaker> speakers) {
        this.speakers = speakers;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public boolean isNonTimedEvent() {
        return isNonTimedEvent;
    }

    public void setNonTimedEvent(boolean nonTimedEvent) {
        isNonTimedEvent = nonTimedEvent;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
