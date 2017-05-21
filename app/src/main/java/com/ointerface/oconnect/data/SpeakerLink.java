package com.ointerface.oconnect.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by AnthonyDoan on 5/14/17.
 */

public class SpeakerLink extends RealmObject {
    @Required
    @PrimaryKey
    private String objectId = ""; // SpeakerID + Int
    private String speakerID = "";
    private String label = "";
    private String link = "";

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getSpeakerID() {
        return speakerID;
    }

    public void setSpeakerID(String speakerID) {
        this.speakerID = speakerID;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
