package com.ointerface.oconnect.data;

import android.util.Log;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by AnthonyDoan on 4/16/17.
 */

public class Person extends RealmObject {
    @PrimaryKey
    private String objectId = "";
    private String org = "";
    private String userType = "app";
    private String Interests = "";
    private boolean isContactable = true;
    private String locaton = "";
    private String username = "";
    private String phoneNumber = "";
    private String firstName = "";
    private String contact_email = "";
    private String job = "";
    private String middleName = "";
    private String lastName = "";
    private Date updatedAt = null;
    private String pictureURL = "";
    private String password = "";
    private RealmList<MasterNotification> deletedNotificationIds = new RealmList<MasterNotification>();
    private RealmList<Event> favoriteEvents = new RealmList<Event>();

    public RealmList<Event> getFavoriteEvents() {
        return favoriteEvents;
    }

    public void setFavoriteEvents(RealmList<Event> favoriteEvents) {
        this.favoriteEvents = favoriteEvents;
    }

    public RealmList<MasterNotification> getDeletedNotificationIds() {
        return deletedNotificationIds;
    }

    public void setDeletedNotificationIds(RealmList<MasterNotification> deletedNotificationIds) {
        this.deletedNotificationIds = deletedNotificationIds;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getInterests() {
        return Interests;
    }

    public void setInterests(String interests) {
        Interests = interests;
    }

    public boolean isContactable() {
        return isContactable;
    }

    public void setContactable(boolean contactable) {
        isContactable = contactable;
    }

    public String getLocaton() {
        return locaton;
    }

    public void setLocaton(String locaton) {
        this.locaton = locaton;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getContact_email() {
        return contact_email;
    }

    public void setContact_email(String contact_email) {
        this.contact_email = contact_email;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    static public Person saveFromParseUser(ParseObject parseObject) {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        realm.beginTransaction();

        Person person = realm.where(Person.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

        if (person == null) {
            person = realm.createObject(Person.class);
        }

        person.setObjectId(parseObject.getObjectId());
        person.setUpdatedAt(parseObject.getDate("updatedAt"));
        person.setContact_email(parseObject.getString("contact_email"));
        person.setContactable(parseObject.getBoolean("isContactable"));
        person.setFirstName(parseObject.getString("firstName"));
        person.setLastName(parseObject.getString("lastName"));
        person.setJob(parseObject.getString("job"));
        person.setOrg(parseObject.getString("org"));
        person.setLocaton(parseObject.getString("location"));
        person.setInterests(parseObject.getString("Interests"));
        person.setUserType(parseObject.getString("userType"));
        person.setPictureURL(parseObject.getString("pictureURL"));
        person.setPassword(parseObject.getString("password"));

        ArrayList<String> deletedAlertsList = (ArrayList<String>) parseObject.get("deletedNotificationIds");

        RealmList<MasterNotification> realmDeletedAlerts = new RealmList<MasterNotification>();

        if (deletedAlertsList != null) {
            for (String alertId : deletedAlertsList) {
                MasterNotification alert = realm.where(MasterNotification.class).equalTo("objectId", alertId).findFirst();

                realmDeletedAlerts.add(alert);
            }
        }

        person.setDeletedNotificationIds(realmDeletedAlerts);

        RealmList<Event> realmFavoriteEvents = new RealmList<Event>();

        ParseRelation<ParseObject> eventsRelation = parseObject.getRelation("favoriteEventsRelation");

        ParseQuery<ParseObject> eventsQuery = eventsRelation.getQuery();

        try {
            List<ParseObject> eventsList = eventsQuery.find();

            for (ParseObject eventObj : eventsList) {
                Event event = realm.where(Event.class).equalTo("objectId", eventObj.getObjectId()).findFirst();
                if (event != null) {
                    realmFavoriteEvents.add(event);
                }
            }

            person.setFavoriteEvents(realmFavoriteEvents);
        } catch (Exception ex) {
            Log.d("DataSyncManager", "Line 727: " + ex.getMessage());
        }

        realm.commitTransaction();

        return person;
    }
}
