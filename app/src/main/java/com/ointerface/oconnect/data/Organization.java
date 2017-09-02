package com.ointerface.oconnect.data;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by AnthonyDoan on 4/11/17.
 */

public class Organization extends RealmObject {
    @Required
    @PrimaryKey
    private String objectId = "";
    private boolean showSplash = false;
    private String name = "";
    private Date updatedAt;
    private boolean isDeleted = false;
    private Date createdAt;
    private byte[] image;
    private String website = "";
    private Boolean showCustomSplash = false;

    public String getObjectId() { return this.objectId; }
    public boolean getShowSplash() {return this.showSplash; }
    public String getName() { return this.name; }
    public Date getUpdatedAt() { return  this.updatedAt; }
    public boolean getIsDeleted() { return this.isDeleted; }
    public Date getCreatedAt() { return this.createdAt; }
    public byte[] getImage() { return this.image; }
    public String getWebsite() { return this.website; }
    public Boolean showCustomSplash() {return this.showCustomSplash; }

    public void setObjectId(String id) { this.objectId = id; }
    public void setShowSplash(boolean shouldShow) { this.showSplash = shouldShow; }
    public void setName(String name) { this.name = name; }
    public void setUpdatedAt(Date updated) { this.updatedAt = updated; }
    public void setDeleted(boolean deleted) { this.isDeleted = deleted; }
    public void setCreatedAt(Date created) { this.createdAt = created; }
    public void setImage(byte[] imageArg) { this.image = imageArg; }
    public void setWebsite(String website) { this.website = website; }
    public void setShowCustomSplash(boolean showCustomSplash) {this.showCustomSplash = showCustomSplash; }
}
