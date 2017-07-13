package com.ointerface.oconnect.data;

import android.util.Log;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by AnthonyDoan on 4/16/17.
 */

public class Person extends RealmObject {
    @Required
    @PrimaryKey
    private String objectId = "";
    private String org = "";
    private String userType = "app";
    private String Interests = "";
    private boolean isContactable = true;
    private String location = "";
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
    private RealmList<MasterNotification> deletedNotificationIds;
    private RealmList<Event> favoriteEvents = new RealmList<Event>();
    private RealmList<Person> favoriteUsers = new RealmList<Person>();
    private RealmList<Speaker> favoriteSpeakers = new RealmList<Speaker>();
    private RealmList<Attendee> favoriteAttendees = new RealmList<Attendee>();

    private RealmList<Person> suggestedConnections = new RealmList<Person>();

    private String bio = "";

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public RealmList<Person> getSuggestedConnections() {
        return suggestedConnections;
    }

    public void setSuggestedConnections(RealmList<Person> suggestedConnections) {
        this.suggestedConnections = suggestedConnections;
    }

    public RealmList<Speaker> getFavoriteSpeakers() {
        return favoriteSpeakers;
    }

    public void setFavoriteSpeakers(RealmList<Speaker> favoriteSpeakers) {
        this.favoriteSpeakers = favoriteSpeakers;
    }

    public RealmList<Attendee> getFavoriteAttendees() {
        return favoriteAttendees;
    }

    public void setFavoriteAttendees(RealmList<Attendee> favoriteAttendees) {
        this.favoriteAttendees = favoriteAttendees;
    }

    public RealmList<Person> getFavoriteUsers() {
        return favoriteUsers;
    }

    public void setFavoriteUsers(RealmList<Person> favoriteUsers) {
        this.favoriteUsers = favoriteUsers;
    }

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    static public Person saveFromParseUser(ParseObject parseObject, boolean skipPredAnalyticsSync) {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        realm.beginTransaction();

        Person person = realm.where(Person.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

        if (person == null) {
            person = realm.createObject(Person.class, parseObject.getObjectId());
        }

        // person.setObjectId(parseObject.getObjectId());
        person.setUpdatedAt(parseObject.getDate("updatedAt"));
        person.setContact_email(parseObject.getString("contact_email"));
        person.setContactable(parseObject.getBoolean("isContactable"));
        person.setFirstName(parseObject.getString("firstName"));
        person.setLastName(parseObject.getString("lastName"));
        person.setJob(parseObject.getString("job"));
        person.setOrg(parseObject.getString("org"));
        person.setLocation(parseObject.getString("location"));
        person.setInterests(parseObject.getString("Interests"));
        person.setUserType(parseObject.getString("userType"));
        person.setPictureURL(parseObject.getString("pictureURL"));
        person.setPassword(parseObject.getString("password"));
        person.setBio(parseObject.getString("bio"));

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

        RealmList<Person> realmFavoriteUsers = new RealmList<Person>();

        ParseRelation<ParseObject> usersRelation = parseObject.getRelation("favoriteUsersRelation");

        ParseQuery<ParseObject> usersQuery = usersRelation.getQuery();

        try {
            List<ParseObject> usersList = usersQuery.find();

            for (ParseObject userObj : usersList) {
                Person user = realm.where(Person.class).equalTo("objectId", userObj.getObjectId()).findFirst();
                if (user != null) {
                    realmFavoriteUsers.add(user);
                } else {
                    realm.commitTransaction();
                    user = saveFromParseUser(userObj, true);
                    realm.beginTransaction();
                    realmFavoriteUsers.add(user);
                }
            }

            person.setFavoriteUsers(realmFavoriteUsers);
        } catch (Exception ex) {
            Log.d("DataSyncManager", "Line 300: " + ex.getMessage());
        }

        RealmList<Speaker> realmFavoriteSpeakers = new RealmList<Speaker>();

        ParseRelation<ParseObject> speakersRelation = parseObject.getRelation("favoriteSpeakerRelation");

        ParseQuery<ParseObject> speakersQuery = speakersRelation.getQuery();

        try {
            List<ParseObject> speakersList = speakersQuery.find();

            for (ParseObject speakerObj : speakersList) {
                Speaker speaker = realm.where(Speaker.class).equalTo("objectId", speakerObj.getObjectId()).findFirst();
                if (speaker != null) {
                    realmFavoriteSpeakers.add(speaker);
                }
            }

            person.setFavoriteSpeakers(realmFavoriteSpeakers);
        } catch (Exception ex) {
            Log.d("DataSyncManager", "Line 321: " + ex.getMessage());
        }

        RealmList<Attendee> realmFavoriteAttendees = new RealmList<Attendee>();

        ParseRelation<ParseObject> attendeesRelation = parseObject.getRelation("favoriteAttendeesRelation");

        ParseQuery<ParseObject> attendeesQuery = attendeesRelation.getQuery();

        try {
            List<ParseObject> attendeesList = attendeesQuery.find();

            for (ParseObject attendeeObj : attendeesList) {
                Attendee attendee = realm.where(Attendee.class).equalTo("objectId", attendeeObj.getObjectId()).findFirst();
                if (attendee != null) {
                    realmFavoriteAttendees.add(attendee);
                }
            }

            person.setFavoriteAttendees(realmFavoriteAttendees);
        } catch (Exception ex) {
            Log.d("DataSyncManager", "Line 342: " + ex.getMessage());
        }

        realm.commitTransaction();

        if (!skipPredAnalyticsSync) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("PredAnalyticsMatches").setLimit(1000);

            query.whereEqualTo("id1", parseObject.getObjectId()).addDescendingOrder("score");

            try {
                List<ParseObject> suggestedConnectionsList = query.find();

                if (suggestedConnectionsList != null) {
                    for (int k = 0; k < suggestedConnectionsList.size(); ++k) {
                        realm.beginTransaction();

                        ParseObject curObj = suggestedConnectionsList.get(k);

                        PredAnalyticsMatches result = realm.where(PredAnalyticsMatches.class).equalTo("objectId", curObj.getObjectId()).findFirst();

                        if (result == null) {

                            // Create an object
                            PredAnalyticsMatches matchObj = realm.createObject(PredAnalyticsMatches.class, curObj.getObjectId());

                            matchObj.setAccepted(curObj.getBoolean("isAccepted"));
                            matchObj.setDeleted(curObj.getBoolean("isDeleted"));
                            matchObj.setId1(curObj.getString("id1"));
                            matchObj.setId2(curObj.getString("id2"));
                            matchObj.setScore(curObj.getDouble("score"));
                            matchObj.setScoreBio(curObj.getDouble("scoreBio"));
                            matchObj.setScoreInterests(curObj.getDouble("scoreInterests"));
                            matchObj.setScoreConferences(curObj.getDouble("scoreConferences"));
                            matchObj.setScoreLocation(curObj.getDouble("scoreLocation"));
                            matchObj.setScoreSurveyAnswers(curObj.getDouble("scoreSurveyAnswers"));

                        } else {

                            result.setAccepted(curObj.getBoolean("isAccepted"));
                            result.setDeleted(curObj.getBoolean("isDeleted"));
                            result.setId1(curObj.getString("id1"));
                            result.setId2(curObj.getString("id2"));
                            result.setScore(curObj.getDouble("score"));
                            result.setScoreBio(curObj.getDouble("scoreBio"));
                            result.setScoreInterests(curObj.getDouble("scoreInterests"));
                            result.setScoreConferences(curObj.getDouble("scoreConferences"));
                            result.setScoreLocation(curObj.getDouble("scoreLocation"));
                            result.setScoreSurveyAnswers(curObj.getDouble("scoreSurveyAnswers"));

                        }

                        realm.commitTransaction();

                        ParseQuery<ParseObject> queryUser = ParseQuery.getQuery("_User");
                        queryUser.whereEqualTo("objectId",curObj.getString("id2"));

                        List<ParseObject> userResults = queryUser.find();

                        if (userResults.size() > 0) {

                            ParseObject parseUser = userResults.get(0);

                            Person newMatch = saveFromParseUser(parseUser, true);

                            realm.beginTransaction();
                            if (!person.getSuggestedConnections().contains(newMatch)) {
                                person.getSuggestedConnections().add(newMatch);
                            }
                            realm.commitTransaction();
                        }
                    }
                }
            } catch (Exception ex) {
                Log.d("Person", ex.getMessage());
            }
        }

        return person;
    }
}
