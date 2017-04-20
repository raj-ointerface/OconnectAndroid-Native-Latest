package com.ointerface.oconnect.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by AnthonyDoan on 4/19/17.
 */

public class Attendee extends RealmObject {
    @PrimaryKey
    private String objectId = "";
    private String name = "";
    private String UserLink = "";
    private boolean allowCheckIn = false;
    private String IOS_code = "";
    private RealmList<Event> eventsList = new RealmList<Event>();
    private Date updatedAt = null;

    public RealmList<Event> getEventsList() {
        return eventsList;
    }

    public void setEventsList(RealmList<Event> eventsList) {
        this.eventsList = eventsList;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserLink() {
        return UserLink;
    }

    public void setUserLink(String userLink) {
        UserLink = userLink;
    }

    public boolean isAllowCheckIn() {
        return allowCheckIn;
    }

    public void setAllowCheckIn(boolean allowCheckIn) {
        this.allowCheckIn = allowCheckIn;
    }

    public String getIOS_code() {
        return IOS_code;
    }

    public void setIOS_code(String IOS_code) {
        this.IOS_code = IOS_code;
    }
}
