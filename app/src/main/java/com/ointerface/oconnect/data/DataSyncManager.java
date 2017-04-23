package com.ointerface.oconnect.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.ointerface.oconnect.util.AppConfig;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.ointerface.oconnect.App;
import com.parse.ParseRelation;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by AnthonyDoan on 4/11/17.
 */

public class DataSyncManager {
    static private Context context;
    static private IDataSyncListener callback;
    static public ProgressDialog dialog;

    // private static List<ParseObject> allObjects = new ArrayList<ParseObject>();

    static public void beginDataSync(Context contextArg, IDataSyncListener callbackArg) {
        context = contextArg;
        callback = callbackArg;

        dataSyncOrganizations();

    }

    static public void dataSyncOrganizations() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Organization").setLimit(1000);

        Date date = getLastSyncDate();

        if (date != null) {
            query.whereGreaterThanOrEqualTo("updatedAt", date);
        }

        Log.d("DataSyncManager", "Begin Parse Query For Organizations");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    Log.d("DataSyncManager", "Start Processing Org Records: " + objects.size() + " objects");
                    for (ParseObject parseObject : objects){
                        Realm realm = AppUtil.getRealmInstance(App.getInstance());

                        Organization result = realm.where(Organization.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

                        if (result == null) {
                            // Insert into Realm
                            realm.beginTransaction();

                            // Create an object
                            Organization org = realm.createObject(Organization.class);

                            // Set its fields
                            org.setObjectId(parseObject.getObjectId());
                            org.setShowSplash(parseObject.getBoolean("showSplash"));
                            org.setName(parseObject.getString("name"));
                            org.setUpdatedAt(parseObject.getUpdatedAt());
                            org.setDeleted(parseObject.getBoolean("isDeleted"));
                            org.setCreatedAt(parseObject.getDate("createdAt"));
                            org.setWebsite(parseObject.getString("website"));

                            ParseFile parseImage = (ParseFile) parseObject.getParseFile("image");

                            try {
                                if (parseImage != null) {
                                    org.setImage(parseImage.getData());
                                }
                            } catch (Exception ex) {
                                Log.d("DataSyncManager", "Line 90 " + ex.getMessage());
                            }

                            realm.commitTransaction();
                        } else {
                            // Update in Realm
                            realm.beginTransaction();

                            result.setObjectId(parseObject.getObjectId());
                            result.setShowSplash(parseObject.getBoolean("showSplash"));
                            result.setName(parseObject.getString("name"));
                            result.setUpdatedAt(parseObject.getUpdatedAt());
                            result.setDeleted(parseObject.getBoolean("isDeleted"));
                            result.setCreatedAt(parseObject.getDate("createdAt"));
                            result.setWebsite(parseObject.getString("website"));

                            ParseFile parseImage = (ParseFile) parseObject.getParseFile("image");

                            try {
                                if (parseImage != null) {
                                    result.setImage(parseImage.getData());
                                }
                            } catch (Exception ex) {
                                Log.d("DataSyncManager", "Line 113 " + ex.getMessage());
                            }

                            realm.commitTransaction();
                        }
                    }
                } else {
                    Log.d("DataSyncManager", "Line 120 " + e.getMessage());
                }

                dataSyncConferences();
            }
        });
    }

    static public void dataSyncConferences() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Conference").setLimit(1000);

        Date date = getLastSyncDate();

        if (date != null) {
            query.whereGreaterThanOrEqualTo("updatedAt", date).whereNotEqualTo("isDeleted", true);
        }

        Log.d("DataSyncManager", "Begin Query for Parse Conferences");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    Log.d("DataSyncManager", "Start Processing Conferences: " + objects.size() + " objects");
                    for (ParseObject parseObject : objects){
                        Realm realm = AppUtil.getRealmInstance(App.getInstance());

                        Conference result = realm.where(Conference.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

                        if (result == null) {
                            // Insert into Realm
                            realm.beginTransaction();

                            // Create an object
                            Conference conference = realm.createObject(Conference.class);

                            // Set its fields
                            conference.setObjectId(parseObject.getObjectId());
                            conference.setShowQRScanner(parseObject.getBoolean("showQRScanner"));
                            conference.setShowNonTimedEvents(parseObject.getBoolean("showNonTimedEvents"));
                            conference.setZip(parseObject.getString("zip"));
                            conference.setShowExternalLink(parseObject.getBoolean("showExternalLink"));
                            conference.setPasswordProtectInfo(parseObject.getBoolean("passwordProtectInfo"));
                            conference.setColor(parseObject.getString("color"));
                            conference.setShouldShowPin(parseObject.getBoolean("shouldShowPin"));
                            conference.setToolbarLabelDiscussionBoard(parseObject.getString("toolbarLabelDiscussionBoard"));
                            conference.setExternalLink(parseObject.getString("externalLink"));
                            conference.setSummary(parseObject.getString("summary"));
                            conference.setToolbarLabelInfo(parseObject.getString("toolbarLabelInfo"));
                            conference.setPasswordProtectSpeakers(parseObject.getBoolean("passwordProtectSpeakers"));
                            conference.setEndTime(parseObject.getDate("endTime"));
                            conference.setParticipantsLabelSpeakers(parseObject.getString("participantsLabelSpeakers"));
                            conference.setShowPosters(parseObject.getBoolean("showPosters"));
                            conference.setEventbriteToken(parseObject.getString("eventbriteToken"));
                            conference.setPasswordProtectMaps(parseObject.getBoolean("passwordProtectMaps"));
                            conference.setEventbriteId(parseObject.getString("eventBriteId"));
                            conference.setShowInfo(parseObject.getBoolean("showInfo"));
                            conference.setCity(parseObject.getString("city"));
                            conference.setName(parseObject.getString("name"));
                            conference.setAnnouncements(parseObject.getString("announcements"));
                            conference.setHashtag(parseObject.getString("hashtag"));
                            conference.setCode(parseObject.getString("code"));
                            conference.setParkingLocation(parseObject.getString("parkingLocation"));
                            conference.setPasswordProtectSurvey(parseObject.getBoolean("passwordProtectSurvey"));
                            conference.setContactPhone(parseObject.getString("contactPhone"));
                            conference.setUpdatedAt(parseObject.getUpdatedAt());
                            conference.setStartTime(parseObject.getDate("startTime"));
                            conference.setVenue(parseObject.getString("venue"));
                            conference.setDeleted(parseObject.getBoolean("isDeleted"));
                            conference.setToolbarLabelSchedule(parseObject.getString("toolbarLabelSchedule"));
                            conference.setShowCheckin(parseObject.getBoolean("showCheckin"));
                            conference.setToolbarLabelMaps(parseObject.getString("toolbarLabelMaps"));
                            conference.setShowQuestions(parseObject.getBoolean("showQuestions"));
                            conference.setState(parseObject.getString("state"));
                            conference.setToolbarLabelSponsors(parseObject.getString("toolbarLabelSponsors"));
                            conference.setPublic(parseObject.getBoolean("isPublic"));
                            conference.setToolbarLabelSurvey(parseObject.getString("toolbarLabelSurvey"));
                            conference.setShowPaticipants(parseObject.getBoolean("showPaticipants"));

                            ParseObject tempObj = parseObject.getParseObject("organization");

                            if (tempObj != null) {
                                conference.setOrganization(tempObj.getObjectId());
                            }

                            conference.setMapUrl(parseObject.getString("mapUrl"));
                            conference.setShowSchedule(parseObject.getBoolean("showSchedule"));
                            conference.setParticipantsLabelParticipants(parseObject.getString("participantsLabelParticipants"));
                            conference.setImageUrl(parseObject.getString("imageUrl"));
                            conference.setParkingInformation(parseObject.getString("parkingInformation"));
                            conference.setShowMaps(parseObject.getBoolean("showMaps"));
                            conference.setAddress(parseObject.getString("address"));
                            conference.setShowRegistration(parseObject.getBoolean("showRegistration"));
                            conference.setShowSponsors(parseObject.getBoolean("showSponsors"));
                            conference.setCountry(parseObject.getString("country"));
                            conference.setShowDashboard(parseObject.getBoolean("showDashboard"));
                            conference.setToolbarLabelNonTimedEvent(parseObject.getString("toolbarLabelNonTimedEvent"));
                            conference.setShowSurvey(parseObject.getBoolean("showSurvey"));
                            conference.setInappPassword(parseObject.getString("inappPassword"));
                            conference.setToolbarLabel(parseObject.getString("toolbarLabel"));
                            conference.setToolbarLabelParticipants(parseObject.getString("toolbarLabelParticipants"));
                            conference.setShowPinEntry(parseObject.getBoolean("showPinEntry"));
                            conference.setTravel(parseObject.getString("travel"));
                            conference.setType(parseObject.getString("type"));
                            conference.setSurvey(parseObject.getString("survey"));
                            conference.setWebsite(parseObject.getString("website"));
                            conference.setCreatedAt(parseObject.getDate("createdAt"));

                            ParseFile parseImage = (ParseFile) parseObject.getParseFile("image");

                            try {
                                if (parseImage != null) {
                                    conference.setImage(parseImage.getData());
                                }
                            } catch (Exception ex) {
                                Log.d("DataSyncManager", "Line 231 " + ex.getMessage());
                            }

                            conference.setContactEmail(parseObject.getString("contactEmail"));
                            conference.setDescription(parseObject.getString("description"));
                            conference.setShowParticipants(parseObject.getBoolean("showParticipants"));
                            conference.setToolbarLabelExternalLink(parseObject.getString("toolbarLabelExternalLink"));
                            conference.setGroup(parseObject.getString("group"));

                            realm.commitTransaction();
                        } else {
                            // Update in Realm
                            realm.beginTransaction();

                            result.setObjectId(parseObject.getObjectId());
                            result.setShowQRScanner(parseObject.getBoolean("showQRScanner"));
                            result.setShowNonTimedEvents(parseObject.getBoolean("showNonTimedEvents"));
                            result.setZip(parseObject.getString("zip"));
                            result.setShowExternalLink(parseObject.getBoolean("showExternalLink"));
                            result.setPasswordProtectInfo(parseObject.getBoolean("passwordProtectInfo"));
                            result.setColor(parseObject.getString("color"));
                            result.setShouldShowPin(parseObject.getBoolean("shouldShowPin"));
                            result.setToolbarLabelDiscussionBoard(parseObject.getString("toolbarLabelDiscussionBoard"));
                            result.setExternalLink(parseObject.getString("externalLink"));
                            result.setSummary(parseObject.getString("summary"));
                            result.setToolbarLabelInfo(parseObject.getString("toolbarLabelInfo"));
                            result.setPasswordProtectSpeakers(parseObject.getBoolean("passwordProtectSpeakers"));
                            result.setEndTime(parseObject.getDate("endTime"));
                            result.setParticipantsLabelSpeakers(parseObject.getString("participantsLabelSpeakers"));
                            result.setShowPosters(parseObject.getBoolean("showPosters"));
                            result.setEventbriteToken(parseObject.getString("eventbriteToken"));
                            result.setPasswordProtectMaps(parseObject.getBoolean("passwordProtectMaps"));
                            result.setEventbriteId(parseObject.getString("eventBriteId"));
                            result.setShowInfo(parseObject.getBoolean("showInfo"));
                            result.setCity(parseObject.getString("city"));
                            result.setName(parseObject.getString("name"));
                            result.setAnnouncements(parseObject.getString("announcements"));
                            result.setHashtag(parseObject.getString("hashtag"));
                            result.setCode(parseObject.getString("code"));
                            result.setParkingLocation(parseObject.getString("parkingLocation"));
                            result.setPasswordProtectSurvey(parseObject.getBoolean("passwordProtectSurvey"));
                            result.setContactPhone(parseObject.getString("contactPhone"));
                            result.setUpdatedAt(parseObject.getUpdatedAt());
                            result.setStartTime(parseObject.getDate("startTime"));
                            result.setVenue(parseObject.getString("venue"));
                            result.setDeleted(parseObject.getBoolean("isDeleted"));
                            result.setToolbarLabelSchedule(parseObject.getString("toolbarLabelSchedule"));
                            result.setShowCheckin(parseObject.getBoolean("showCheckin"));
                            result.setToolbarLabelMaps(parseObject.getString("toolbarLabelMaps"));
                            result.setShowQuestions(parseObject.getBoolean("showQuestions"));
                            result.setState(parseObject.getString("state"));
                            result.setToolbarLabelSponsors(parseObject.getString("toolbarLabelSponsors"));
                            result.setPublic(parseObject.getBoolean("isPublic"));
                            result.setToolbarLabelSurvey(parseObject.getString("toolbarLabelSurvey"));
                            result.setShowPaticipants(parseObject.getBoolean("showPaticipants"));

                            ParseObject tempObj = parseObject.getParseObject("organization");

                            if (tempObj != null) {
                                result.setOrganization(tempObj.getObjectId());
                            }

                            result.setMapUrl(parseObject.getString("mapUrl"));
                            result.setShowSchedule(parseObject.getBoolean("showSchedule"));
                            result.setParticipantsLabelParticipants(parseObject.getString("participantsLabelParticipants"));
                            result.setImageUrl(parseObject.getString("imageUrl"));
                            result.setParkingInformation(parseObject.getString("parkingInformation"));
                            result.setShowMaps(parseObject.getBoolean("showMaps"));
                            result.setAddress(parseObject.getString("address"));
                            result.setShowRegistration(parseObject.getBoolean("showRegistration"));
                            result.setShowSponsors(parseObject.getBoolean("showSponsors"));
                            result.setCountry(parseObject.getString("country"));
                            result.setShowDashboard(parseObject.getBoolean("showDashboard"));
                            result.setToolbarLabelNonTimedEvent(parseObject.getString("toolbarLabelNonTimedEvent"));
                            result.setShowSurvey(parseObject.getBoolean("showSurvey"));
                            result.setInappPassword(parseObject.getString("inappPassword"));
                            result.setToolbarLabel(parseObject.getString("toolbarLabel"));
                            result.setToolbarLabelParticipants(parseObject.getString("toolbarLabelParticipants"));
                            result.setShowPinEntry(parseObject.getBoolean("showPinEntry"));
                            result.setTravel(parseObject.getString("travel"));
                            result.setType(parseObject.getString("type"));
                            result.setSurvey(parseObject.getString("survey"));
                            result.setWebsite(parseObject.getString("website"));
                            result.setCreatedAt(parseObject.getDate("createdAt"));

                            ParseFile parseImage = (ParseFile) parseObject.getParseFile("image");

                            try {
                                if (parseImage != null) {
                                    result.setImage(parseImage.getData());
                                }
                            } catch (Exception ex) {
                                Log.d("DataSyncManager", "Line 323 " + ex.getMessage());
                            }

                            result.setContactEmail(parseObject.getString("contactEmail"));
                            result.setDescription(parseObject.getString("description"));
                            result.setShowParticipants(parseObject.getBoolean("showParticipants"));
                            result.setToolbarLabelExternalLink(parseObject.getString("toolbarLabelExternalLink"));
                            result.setGroup(parseObject.getString("group"));

                            realm.commitTransaction();
                        }
                    }
                } else {
                    Log.d("DataSyncManager", "Line 336 " + e.getMessage());
                }

                dataSyncSessions();

            }
        });
    }

    static public void dataSyncSessions() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Session").setLimit(1000);

        Date date = getLastSyncDate();

        if (date != null) {
            query.whereGreaterThanOrEqualTo("updatedAt", date).whereNotEqualTo("isDeleted", true);
        }

        Log.d("DataSyncManager", "Start Parse Query for All Sessions");

        List<ParseObject>  allObjects = new ArrayList<ParseObject>();

        int countSkip=0,loopCloseCount=0;
        do{
            query.setLimit(1000);
            query.setSkip(countSkip);
            List<ParseObject> objects=null;
            try {
                objects = query.find();
                loopCloseCount = objects.size();
                countSkip += objects.size();
                if(loopCloseCount > 0) {
                    allObjects.addAll(objects);
                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
                loopCloseCount=0;
            }
        } while (loopCloseCount > 0);

        Log.d("DataSyncManager", "Query for Sessions Completed");
        Log.d("DataSyncManager", "Start Processing Sessions: " + allObjects.size() + " objects");
        for (ParseObject parseObject : allObjects) {
            Realm realm = AppUtil.getRealmInstance(App.getInstance());

            Session result = realm.where(Session.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

            if (result == null) {
                realm.beginTransaction();

                // Create an object
                Session session = realm.createObject(Session.class);

                // Set its fields
                session.setObjectId(parseObject.getObjectId());
                session.setEndTime(parseObject.getDate("endTime"));
                session.setTrack(parseObject.getString("track"));
                session.setStartTime(parseObject.getDate("startTime"));
                session.setLocation(parseObject.getString("location"));
                session.setModerator(parseObject.getString("moderator"));

                ParseObject tempObj = parseObject.getParseObject("conference");

                if (tempObj != null) {
                    session.setConference(tempObj.getObjectId());
                }

                session.setUpdatedAt(parseObject.getUpdatedAt());

                realm.commitTransaction();
            } else {
                realm.beginTransaction();

                result.setObjectId(parseObject.getObjectId());
                result.setEndTime(parseObject.getDate("endTime"));
                result.setTrack(parseObject.getString("track"));
                result.setStartTime(parseObject.getDate("startTime"));
                result.setLocation(parseObject.getString("location"));
                result.setModerator(parseObject.getString("moderator"));

                ParseObject tempObj = parseObject.getParseObject("conference");

                if (tempObj != null) {
                    result.setConference(tempObj.getObjectId());
                }

                result.setUpdatedAt(parseObject.getUpdatedAt());

                realm.commitTransaction();
            }
        }

        dataSyncEvents();

        /*
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    for (ParseObject parseObject : objects) {
                        Realm realm = AppUtil.getRealmInstance(App.getInstance());

                        Session result = realm.where(Session.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

                        if (result == null) {
                            realm.beginTransaction();

                            // Create an object
                            Session session = realm.createObject(Session.class);

                            // Set its fields
                            session.setObjectId(parseObject.getObjectId());
                            session.setEndTime(parseObject.getDate("endTime"));
                            session.setTrack(parseObject.getString("track"));
                            session.setStartTime(parseObject.getDate("startTime"));
                            session.setLocation(parseObject.getString("location"));

                            ParseObject tempObj = parseObject.getParseObject("conference");

                            if (tempObj != null) {
                                session.setConference(tempObj.getObjectId());
                            }

                            session.setUpdatedAt(parseObject.getDate("updatedAt"));

                            realm.commitTransaction();
                        } else {
                            realm.beginTransaction();

                            result.setObjectId(parseObject.getObjectId());
                            result.setEndTime(parseObject.getDate("endTime"));
                            result.setTrack(parseObject.getString("track"));
                            result.setStartTime(parseObject.getDate("startTime"));
                            result.setLocation(parseObject.getString("location"));

                            ParseObject tempObj = parseObject.getParseObject("conference");

                            if (tempObj != null) {
                                result.setConference(tempObj.getObjectId());
                            }

                            result.setUpdatedAt(parseObject.getDate("updatedAt"));

                            realm.commitTransaction();
                        }
                    }
                }

                dataSyncEvents();
            }
        });
        */
    }

    static public void dataSyncEvents() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event").setLimit(1000);

        Date date = getLastSyncDate();

        if (date != null) {
            query.whereGreaterThanOrEqualTo("updatedAt", date).whereNotEqualTo("isDeleted", true);
        }

        Log.d("DataSyncManager", "Start Query For Parse Events");
        List<ParseObject>  allObjects = new ArrayList<ParseObject>();

        int countSkip=0,loopCloseCount=0;
        do{
            query.setLimit(1000);
            query.setSkip(countSkip);
            List<ParseObject> objects=null;
            try {
                objects = query.find();
                loopCloseCount= objects.size();
                countSkip += objects.size();
                if(loopCloseCount > 0) {
                    allObjects.addAll(objects);
                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
                loopCloseCount=0;
            }
        } while (loopCloseCount > 0);

        Log.d("DataSyncManager", "Start Processing Events: " + allObjects.size() + " objects");

        for (ParseObject parseObject : allObjects) {
            Realm realm = AppUtil.getRealmInstance(App.getInstance());

            Event result = realm.where(Event.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

            if (result == null) {
                realm.beginTransaction();

                // Create an object
                Event event = realm.createObject(Event.class);

                event.setObjectId(parseObject.getObjectId());
                event.setNonTimedEvent(parseObject.getBoolean("isNonTimedEvent"));
                event.setEndTime(parseObject.getDate("endTime"));
                event.setName(parseObject.getString("name"));
                event.setStartTime(parseObject.getDate("startTime"));
                event.setLocation(parseObject.getString("location"));

                ParseRelation<ParseObject> speakersRelation = parseObject.getRelation("speakers");

                ParseQuery<ParseObject> speakersQuery = speakersRelation.getQuery();

                try {
                    List<ParseObject> speakersList = speakersQuery.find();

                    RealmList<Speaker> realmSpeakerList = new RealmList<Speaker>();

                    for (ParseObject speakerObj: speakersList) {
                        Speaker speaker = realm.where(Speaker.class).equalTo("objectId", speakerObj.getObjectId()).findFirst();

                        if (speaker == null) {
                            speaker = realm.createObject(Speaker.class);

                        }

                        speaker.setObjectId(speakerObj.getObjectId());
                        speaker.setName(speakerObj.getString("name"));
                        speaker.setAllowCheckIn(speakerObj.getBoolean("allowCheckIn"));
                        speaker.setIOS_code(speakerObj.getString("IOS_code"));
                        speaker.setUpdatedAt(speakerObj.getUpdatedAt());

                        realmSpeakerList.add(speaker);
                    }

                    event.setSpeakers(realmSpeakerList);
                } catch (Exception ex) {
                    Log.d("DataSyncManager", "Line 551: " + ex.getMessage());
                }


                ParseObject sessionObj = parseObject.getParseObject("session");

                if (sessionObj != null) {
                    event.setSession(sessionObj.getObjectId());

                    if (parseObject.getBoolean("isNonTimedEvent") == true) {
                        try {
                            if (sessionObj.fetchIfNeeded().getDate("startTime") != null) {
                                event.setStartTime(sessionObj.getDate("startTime"));
                            }

                            if (sessionObj.fetchIfNeeded().getDate("endTime") != null) {
                                event.setEndTime(sessionObj.getDate("endTime"));
                            }
                        } catch (Exception ex) {
                            Log.d("DataSync", ex.getMessage());
                        }
                    }
                }

                event.setUpdatedAt(parseObject.getUpdatedAt());

                realm.commitTransaction();
            } else {
                realm.beginTransaction();

                result.setObjectId(parseObject.getObjectId());
                result.setNonTimedEvent(parseObject.getBoolean("isNonTimedEvent"));
                result.setEndTime(parseObject.getDate("endTime"));
                result.setName(parseObject.getString("name"));
                result.setStartTime(parseObject.getDate("startTime"));
                result.setLocation(parseObject.getString("location"));

                ParseRelation<ParseObject> speakersRelation = parseObject.getRelation("speakers");

                ParseQuery<ParseObject> speakersQuery = speakersRelation.getQuery();

                try {
                    List<ParseObject> speakersList = speakersQuery.find();

                    RealmList<Speaker> realmSpeakerList = new RealmList<Speaker>();

                    for (ParseObject speakerObj: speakersList) {
                        Speaker speaker = realm.where(Speaker.class).equalTo("objectId", speakerObj.getObjectId()).findFirst();

                        if (speaker == null) {
                            speaker = realm.createObject(Speaker.class);

                        }

                        speaker.setObjectId(speakerObj.getObjectId());
                        speaker.setName(speakerObj.getString("name"));
                        speaker.setAllowCheckIn(speakerObj.getBoolean("allowCheckIn"));
                        speaker.setIOS_code(speakerObj.getString("IOS_code"));
                        speaker.setUpdatedAt(speakerObj.getUpdatedAt());

                        realmSpeakerList.add(speaker);
                    }

                    result.setSpeakers(realmSpeakerList);
                } catch (Exception ex) {
                    Log.d("DataSyncManager", "Line 551: " + ex.getMessage());
                }


                ParseObject sessionObj = parseObject.getParseObject("session");

                if (sessionObj != null) {
                    result.setSession(sessionObj.getObjectId());

                    if (parseObject.getBoolean("isNonTimedEvent") == true) {
                        result.setStartTime(sessionObj.getDate("startTime"));
                        result.setEndTime(sessionObj.getDate("endTime"));
                    }
                }

                result.setUpdatedAt(parseObject.getUpdatedAt());

                realm.commitTransaction();
            }
        }

        dataSyncSpeakers();

        /*
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    for (ParseObject parseObject : objects) {
                        Realm realm = AppUtil.getRealmInstance(App.getInstance());

                        Event result = realm.where(Event.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

                        if (result == null) {
                            realm.beginTransaction();

                            // Create an object
                            Event event = realm.createObject(Event.class);

                            event.setObjectId(parseObject.getObjectId());
                            event.setNonTimedEvent(parseObject.getBoolean("isNonTimedEvent"));
                            event.setEndTime(parseObject.getDate("endTime"));
                            event.setName(parseObject.getString("name"));
                            event.setStartTime(parseObject.getDate("startTime"));

                            ParseObject sessionObj = parseObject.getParseObject("session");

                            if (sessionObj != null) {
                                event.setSession(sessionObj.getObjectId());
                            }

                            event.setUpdatedAt(parseObject.getDate("updatedAt"));

                            realm.commitTransaction();
                        } else {
                            realm.beginTransaction();

                            result.setObjectId(parseObject.getObjectId());
                            result.setNonTimedEvent(parseObject.getBoolean("isNonTimedEvent"));
                            result.setEndTime(parseObject.getDate("endTime"));
                            result.setName(parseObject.getString("name"));
                            result.setStartTime(parseObject.getDate("startTime"));

                            ParseObject sessionObj = parseObject.getParseObject("session");

                            if (sessionObj != null) {
                                result.setSession(sessionObj.getObjectId());
                            }

                            result.setUpdatedAt(parseObject.getDate("updatedAt"));

                            realm.commitTransaction();
                        }
                    }
                }

                callback.onDataSyncFinish();
            }
        });
        */
    }

    static public void dataSyncSpeakers() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Speaker").setLimit(1000);

        Date date = getLastSyncDate();

        if (date != null) {
            query.whereGreaterThanOrEqualTo("updatedAt", date).whereNotEqualTo("isDeleted", true);
        }

        Log.d("DataSyncManager", "Start Parse Query for Speakers");
        List<ParseObject> allObjects = new ArrayList<ParseObject>();

        int countSkip = 0, loopCloseCount = 0;
        do {
            query.setLimit(1000);
            query.setSkip(countSkip);
            List<ParseObject> objects = null;
            try {
                objects = query.find();
                loopCloseCount = objects.size();
                countSkip += objects.size();
                if (loopCloseCount > 0) {
                    allObjects.addAll(objects);
                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
                loopCloseCount = 0;
            }
        } while (loopCloseCount > 0);

        Log.d("DataSyncManager", "Start Processing Speakers: " + allObjects.size() + " objects");

        for (ParseObject parseObject : allObjects) {
            Realm realm = AppUtil.getRealmInstance(App.getInstance());

            Speaker result = realm.where(Speaker.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

            if (result == null) {
                realm.beginTransaction();

                // Create an object
                Speaker speaker = realm.createObject(Speaker.class);

                speaker.setObjectId(parseObject.getObjectId());
                speaker.setName(parseObject.getString("name"));
                speaker.setAllowCheckIn(parseObject.getBoolean("allowCheckIn"));
                speaker.setIOS_code(parseObject.getString("IOS_code"));
                speaker.setUpdatedAt(parseObject.getUpdatedAt());

                ParseRelation<ParseObject> eventsRelation = parseObject.getRelation("event");

                ParseQuery<ParseObject> eventsQuery = eventsRelation.getQuery();

                try {
                    List<ParseObject> eventsList = eventsQuery.find();

                    RealmList<Event> realmEventList = new RealmList<Event>();

                    for (ParseObject eventObj : eventsList) {
                        Event event = realm.where(Event.class).equalTo("objectId", eventObj.getObjectId()).findFirst();
                        if (event != null) {
                            realmEventList.add(event);
                        }
                    }

                    speaker.setEventsList(realmEventList);
                } catch (Exception ex) {
                    Log.d("DataSyncManager", "Line 727: " + ex.getMessage());
                }

                realm.commitTransaction();
            } else {
                realm.beginTransaction();

                result.setObjectId(parseObject.getObjectId());
                result.setName(parseObject.getString("name"));
                result.setAllowCheckIn(parseObject.getBoolean("allowCheckIn"));
                result.setIOS_code(parseObject.getString("IOS_code"));
                result.setUpdatedAt(parseObject.getUpdatedAt());

                ParseRelation<ParseObject> eventsRelation = parseObject.getRelation("event");

                ParseQuery<ParseObject> eventsQuery = eventsRelation.getQuery();

                try {
                    List<ParseObject> eventsList = eventsQuery.find();

                    RealmList<Event> realmEventList = new RealmList<Event>();

                    for (ParseObject eventObj : eventsList) {
                        Event event = realm.where(Event.class).equalTo("objectId", eventObj.getObjectId()).findFirst();
                        if (event != null) {
                            realmEventList.add(event);
                        }
                    }

                    result.setEventsList(realmEventList);
                } catch (Exception ex) {
                    Log.d("DataSyncManager", "Line 754: " + ex.getMessage());
                }

                realm.commitTransaction();
            }
        }

        dataSyncAttendees();
    }

    static public void dataSyncAttendees() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Attendee").setLimit(1000);

        Date date = getLastSyncDate();

        if (date != null) {
            query.whereGreaterThanOrEqualTo("updatedAt", date).whereNotEqualTo("isDeleted", true);
        }

        Log.d("DataSyncManager", "Start Query for Parse Attendees");

        List<ParseObject> allObjects = new ArrayList<ParseObject>();

        int countSkip = 0, loopCloseCount = 0;
        do {
            query.setLimit(1000);
            query.setSkip(countSkip);
            List<ParseObject> objects = null;
            try {
                objects = query.find();
                loopCloseCount = objects.size();
                countSkip += objects.size();
                if (loopCloseCount > 0) {
                    allObjects.addAll(objects);
                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
                loopCloseCount = 0;
            }
        } while (loopCloseCount > 0);

        Log.d("DataSyncManager", "Start Processing Attendees: " + allObjects.size() + " objects");

        for (ParseObject parseObject : allObjects) {
            Realm realm = AppUtil.getRealmInstance(App.getInstance());

            Attendee result = realm.where(Attendee.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

            if (result == null) {
                realm.beginTransaction();

                // Create an object
                Attendee attendee = realm.createObject(Attendee.class);

                attendee.setObjectId(parseObject.getObjectId());
                attendee.setName(parseObject.getString("name"));
                attendee.setAllowCheckIn(parseObject.getBoolean("allowCheckIn"));
                attendee.setIOS_code(parseObject.getString("IOS_code"));
                attendee.setUpdatedAt(parseObject.getUpdatedAt());

                ParseRelation<ParseObject> eventsRelation = parseObject.getRelation("event");

                ParseQuery<ParseObject> eventsQuery = eventsRelation.getQuery();

                try {
                    List<ParseObject> eventsList = eventsQuery.find();

                    RealmList<Event> realmEventList = new RealmList<Event>();

                    for (ParseObject eventObj : eventsList) {
                        Event event = realm.where(Event.class).equalTo("objectId", eventObj.getObjectId()).findFirst();
                        if (event != null) {
                            realmEventList.add(event);
                        }
                    }

                    attendee.setEventsList(realmEventList);

                } catch (Exception ex) {
                    Log.d("DataSyncManager", "Line 727: " + ex.getMessage());
                }

                realm.commitTransaction();
            } else {
                realm.beginTransaction();

                result.setObjectId(parseObject.getObjectId());
                result.setName(parseObject.getString("name"));
                result.setAllowCheckIn(parseObject.getBoolean("allowCheckIn"));
                result.setIOS_code(parseObject.getString("IOS_code"));
                result.setUpdatedAt(parseObject.getUpdatedAt());

                ParseRelation<ParseObject> eventsRelation = parseObject.getRelation("event");

                ParseQuery<ParseObject> eventsQuery = eventsRelation.getQuery();

                try {
                    List<ParseObject> eventsList = eventsQuery.find();

                    RealmList<Event> realmEventList = new RealmList<Event>();

                    for (ParseObject eventObj : eventsList) {
                        Event event = realm.where(Event.class).equalTo("objectId", eventObj.getObjectId()).findFirst();
                        if (event != null) {
                            realmEventList.add(event);
                        }
                    }

                    result.setEventsList(realmEventList);
                } catch (Exception ex) {
                    Log.d("DataSyncManager", "Line 754: " + ex.getMessage());
                }

                realm.commitTransaction();
            }
        }

        dataSyncMasterNotifications();
    }

    static public void dataSyncMasterNotifications() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("MasterNotification").setLimit(1000);

        Date date = getLastSyncDate();

        if (date != null) {
            query.whereGreaterThanOrEqualTo("updatedAt", date).whereNotEqualTo("isDeleted", true);
        }

        Log.d("DataSyncManager", "Start Query for Parse MasterNotifications");

        List<ParseObject> allObjects = new ArrayList<ParseObject>();

        int countSkip = 0, loopCloseCount = 0;
        do {
            query.setLimit(1000);
            query.setSkip(countSkip);
            List<ParseObject> objects = null;
            try {
                objects = query.find();
                loopCloseCount = objects.size();
                countSkip += objects.size();
                if (loopCloseCount > 0) {
                    allObjects.addAll(objects);
                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
                loopCloseCount = 0;
            }
        } while (loopCloseCount > 0);

        Log.d("DataSyncManager", "Start Processing MasterNotifications: " + allObjects.size() + " objects");

        for (ParseObject parseObject : allObjects) {
            Realm realm = AppUtil.getRealmInstance(App.getInstance());

            MasterNotification result = realm.where(MasterNotification.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

            if (result == null) {
                realm.beginTransaction();

                // Create an object
                MasterNotification alert = realm.createObject(MasterNotification.class);

                alert.setObjectId(parseObject.getObjectId());
                alert.setAlert(parseObject.getString("alert"));
                alert.setNew(true);

                ParseObject conferenceObj = parseObject.getParseObject("conference");

                if (conferenceObj != null) {
                    alert.setConference(conferenceObj.getObjectId());
                }

                alert.setCreatedAt(parseObject.getCreatedAt());

                realm.commitTransaction();
            } else {
                realm.beginTransaction();

                result.setObjectId(parseObject.getObjectId());
                result.setAlert(parseObject.getString("alert"));

                ParseObject conferenceObj = parseObject.getParseObject("conference");

                if (conferenceObj != null) {
                    result.setConference(conferenceObj.getObjectId());
                }

                result.setCreatedAt(parseObject.getCreatedAt());

                realm.commitTransaction();
            }
        }

        dataSyncMaps();
    }

    static public void dataSyncMaps(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Maps").setLimit(1000);

        Date date = getLastSyncDate();

        if (date != null) {
            query.whereGreaterThanOrEqualTo("updatedAt", date).whereNotEqualTo("isDeleted", true);
        }

        Log.d("DataSyncManager", "Start Query for Parse Maps");

        List<ParseObject> allObjects = new ArrayList<ParseObject>();

        int countSkip = 0, loopCloseCount = 0;
        do {
            query.setLimit(1000);
            query.setSkip(countSkip);
            List<ParseObject> objects = null;
            try {
                objects = query.find();
                loopCloseCount = objects.size();
                countSkip += objects.size();
                if (loopCloseCount > 0) {
                    allObjects.addAll(objects);
                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
                loopCloseCount = 0;
            }
        } while (loopCloseCount > 0);

        Log.d("DataSyncManager", "Start Processing Maps: " + allObjects.size() + " objects");

        for (ParseObject parseObject : allObjects) {
            Realm realm = AppUtil.getRealmInstance(App.getInstance());

            Maps result = realm.where(Maps.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

            if (result == null) {
                realm.beginTransaction();

                // Create an object
                Maps map = realm.createObject(Maps.class);

                map.setObjectId(parseObject.getObjectId());
                map.setLabel(parseObject.getString("label"));

                ParseObject conferenceObj = parseObject.getParseObject("conference");

                if (conferenceObj != null) {
                    map.setConference(conferenceObj.getObjectId());
                }

                ParseFile parseMapImage = (ParseFile) parseObject.getParseFile("map");

                try {
                    if (parseMapImage != null) {
                        map.setUrl(parseMapImage.getUrl());
                        map.setMap(parseMapImage.getData());
                    }
                } catch (Exception ex) {
                    Log.d("DataSyncManager", "Line 231 " + ex.getMessage());
                }

                realm.commitTransaction();
            } else {
                realm.beginTransaction();

                result.setObjectId(parseObject.getObjectId());
                result.setLabel(parseObject.getString("label"));

                ParseObject conferenceObj = parseObject.getParseObject("conference");

                if (conferenceObj != null) {
                    result.setConference(conferenceObj.getObjectId());
                }

                ParseFile parseMapImage = (ParseFile) parseObject.getParseFile("map");

                try {
                    if (parseMapImage != null) {
                        result.setUrl(parseMapImage.getUrl());
                        result.setMap(parseMapImage.getData());
                    }
                } catch (Exception ex) {
                    Log.d("DataSyncManager", "Line 1087 " + ex.getMessage());
                }

                realm.commitTransaction();
            }
        }

        dataSyncSponsors();
    }

    static public void dataSyncSponsors() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Sponsor").setLimit(1000);

        Date date = getLastSyncDate();

        if (date != null) {
            query.whereGreaterThanOrEqualTo("updatedAt", date).whereNotEqualTo("isDeleted", true);
        }

        Log.d("DataSyncManager", "Start Query for Parse Sponsors");

        List<ParseObject> allObjects = new ArrayList<ParseObject>();

        int countSkip = 0, loopCloseCount = 0;
        do {
            query.setLimit(1000);
            query.setSkip(countSkip);
            List<ParseObject> objects = null;
            try {
                objects = query.find();
                loopCloseCount = objects.size();
                countSkip += objects.size();
                if (loopCloseCount > 0) {
                    allObjects.addAll(objects);
                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
                loopCloseCount = 0;
            }
        } while (loopCloseCount > 0);

        Log.d("DataSyncManager", "Start Processing Sponsors: " + allObjects.size() + " objects");

        for (ParseObject parseObject : allObjects) {
            Realm realm = AppUtil.getRealmInstance(App.getInstance());

            Sponsor result = realm.where(Sponsor.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

            if (result == null) {
                realm.beginTransaction();

                // Create an object
                Sponsor sponsor = realm.createObject(Sponsor.class);

                sponsor.setObjectId(parseObject.getObjectId());
                sponsor.setName(parseObject.getString("name"));
                sponsor.setType(parseObject.getString("type"));
                sponsor.setWebsite(parseObject.getString("website"));

                ParseObject conferenceObj = parseObject.getParseObject("conference");

                if (conferenceObj != null) {
                    sponsor.setConference(conferenceObj.getObjectId());
                }

                ParseFile parseImage = (ParseFile) parseObject.getParseFile("logo");

                try {
                    if (parseImage != null) {
                        sponsor.setLogo(parseImage.getData());
                    }
                } catch (Exception ex) {
                    Log.d("DataSyncManager", "Line 1159 " + ex.getMessage());
                }

                realm.commitTransaction();
            } else {
                realm.beginTransaction();

                result.setObjectId(parseObject.getObjectId());
                result.setName(parseObject.getString("name"));
                result.setType(parseObject.getString("type"));
                result.setWebsite(parseObject.getString("website"));

                ParseObject conferenceObj = parseObject.getParseObject("conference");

                if (conferenceObj != null) {
                    result.setConference(conferenceObj.getObjectId());
                }

                ParseFile parseImage = (ParseFile) parseObject.getParseFile("logo");

                try {
                    if (parseImage != null) {
                        result.setLogo(parseImage.getData());
                    }
                } catch (Exception ex) {
                    Log.d("DataSyncManager", "Line 1159 " + ex.getMessage());
                }

                realm.commitTransaction();
            }
        }

        callback.onDataSyncFinish();
    }

    static public void setLastSyncDate(Date syncDate) {
        DateFormat df = new SimpleDateFormat(AppConfig.defaultDateTimeFormat);

        String newLastSyncDateStr = df.format(syncDate);

        SharedPreferences.Editor editor = context.getSharedPreferences(AppConfig.sharedPrefsName, MODE_PRIVATE).edit();
        editor.putString(AppConfig.lastSyncDateName, newLastSyncDateStr);
        editor.commit();
    }

    static public Date getLastSyncDate() {
        SharedPreferences prefs = context.getSharedPreferences(AppConfig.sharedPrefsName, MODE_PRIVATE);
        String lastSyncDateStr = prefs.getString(AppConfig.lastSyncDateName, "2010-01-01T00:00:00");

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = isoFormat.parse(lastSyncDateStr);
        } catch (Exception ex) {
            Log.d("DataSyncManager", ex.getMessage());
        }

        return date;
    }
}
