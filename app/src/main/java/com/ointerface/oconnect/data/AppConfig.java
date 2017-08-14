package com.ointerface.oconnect.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by AnthonyDoan on 8/13/17.
 */

public class AppConfig extends RealmObject {
    @PrimaryKey
    @Required
    private String objectId = "";
    private String defaultConference = "";
    private Boolean showMainSplash = true;
    private Boolean showConfList = true;
    private String organizationId = "";
    private String appName = "";

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getDefaultConference() {
        return defaultConference;
    }

    public void setDefaultConference(String defaultConference) {
        this.defaultConference = defaultConference;
    }

    public Boolean getShowMainSplash() {
        return showMainSplash;
    }

    public void setShowMainSplash(Boolean showMainSplash) {
        this.showMainSplash = showMainSplash;
    }

    public Boolean getShowConfList() {
        return showConfList;
    }

    public void setShowConfList(Boolean showConfList) {
        this.showConfList = showConfList;
    }
}
