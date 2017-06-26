package com.ointerface.oconnect.data;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by AnthonyDoan on 6/24/17.
 */

public class SinchMessage  extends RealmObject {

    private String messageString = "";
    private Date messageDateTime = null;
    private String currentUserID = "";
    private String connectedUserID = "";
    private Boolean isIncoming = true;

    public Boolean getIncoming() {
        return isIncoming;
    }

    public void setIncoming(Boolean incoming) {
        isIncoming = incoming;
    }

    public String getMessageString() {
        return messageString;
    }

    public void setMessageString(String messageString) {
        this.messageString = messageString;
    }

    public Date getMessageDateTime() {
        return messageDateTime;
    }

    public void setMessageDateTime(Date messageDateTime) {
        this.messageDateTime = messageDateTime;
    }

    public String getCurrentUserID() {
        return currentUserID;
    }

    public void setCurrentUserID(String currentUserID) {
        this.currentUserID = currentUserID;
    }

    public String getConnectedUserID() {
        return connectedUserID;
    }

    public void setConnectedUserID(String connectedUserID) {
        this.connectedUserID = connectedUserID;
    }
}
