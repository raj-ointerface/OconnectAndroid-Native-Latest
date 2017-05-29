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

import org.json.JSONArray;
import org.json.JSONObject;

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
    static public boolean shouldSyncAll = true;

    // private static List<ParseObject> allObjects = new ArrayList<ParseObject>();

    static public void beginDataSync(Context contextArg, IDataSyncListener callbackArg) {
        context = contextArg;
        callback = callbackArg;

        dataSyncOrganizations();

    }

    static public void initDataSyncManager(Context contextArg, IDataSyncListener callbackArg) {
        context = contextArg;
        callback = callbackArg;
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

                    Realm realm = AppUtil.getRealmInstance(App.getInstance());

                    realm.beginTransaction();

                    for (ParseObject parseObject : objects) {

                        Organization result = realm.where(Organization.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

                        if (result == null) {
                            // Insert into Realm


                            // Create an object
                            Organization org = realm.createObject(Organization.class, parseObject.getObjectId());

                            // Set its fields
                            // org.setObjectId(parseObject.getObjectId());
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

                        } else {
                            // Update in Realm

                            // result.setObjectId(parseObject.getObjectId());
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


                        }
                    }

                    realm.commitTransaction();
                    realm.close();
                } else {
                    Log.d("DataSyncManager", "Line 120 " + e.getMessage() + " " + e.getStackTrace());
                }

                if (shouldSyncAll == true) {
                    dataSyncConferences();
                } else {
                    callback.onDataSyncFinish();
                }
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

                    Realm realm = AppUtil.getRealmInstance(App.getInstance());
                    realm.beginTransaction();

                    for (ParseObject parseObject : objects){


                        Conference result = realm.where(Conference.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

                        if (result == null) {

                            // Create an object
                            Conference conference = realm.createObject(Conference.class, parseObject.getObjectId());

                            // Set its fields
                            // conference.setObjectId(parseObject.getObjectId());
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

                        } else {
                            // Update in Realm

                            // result.setObjectId(parseObject.getObjectId());
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

                        }
                    }

                    realm.commitTransaction();
                    realm.close();
                } else {
                    Log.d("DataSyncManager", "Line 336 " + e.getMessage() + " " + e.getStackTrace());
                }

                if (shouldSyncAll == true) {
                    dataSyncSessions();
                } else {
                    callback.onDataSyncFinish();
                }

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

        /*
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                */

                    Log.d("DataSyncManager", "Query for Sessions Completed");
                    Log.d("DataSyncManager", "Start Processing Sessions: " + allObjects.size() + " objects");

                    int counter = 1;

                    Realm realm = AppUtil.getRealmInstance(App.getInstance());
                    realm.beginTransaction();

                    for (ParseObject parseObject : allObjects) {


                        if (counter % 100 == 0) {
                            Log.d("DataSyncManager", "Session Number: " + counter);
                        }
                        ++counter;

                        Session result = realm.where(Session.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

                        if (result == null) {

                            // Create an object
                            Session session = realm.createObject(Session.class, parseObject.getObjectId());

                            // Set its fields
                            // session.setObjectId(parseObject.getObjectId());
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


                        } else {


                            // result.setObjectId(parseObject.getObjectId());
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


                        }
                    }

                    realm.commitTransaction();
                    realm.close();

                    /*
                } else {
                    Log.d("DataSync", e.getMessage());
                }
                */

                if (shouldSyncAll == true) {
                    dataSyncEvents();
                } else {
                    callback.onDataSyncFinish();
                }

            // }});
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

        /*
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                */
                    Log.d("DataSyncManager", "Start Processing Events: " + allObjects.size() + " objects");

                    int counter = 1;

                    Realm realm = AppUtil.getRealmInstance(App.getInstance());
                    realm.beginTransaction();

                    for (ParseObject parseObject : allObjects) {


                        if (counter % 100 == 0) {
                            Log.d("DataSyncManager", "Events Number: " + counter);
                        }
                        ++counter;

                        Event result = realm.where(Event.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

                        if (result == null) {

                            // Create an object
                            Event event = realm.createObject(Event.class, parseObject.getObjectId());

                            // event.setObjectId(parseObject.getObjectId());

                            event.setNonTimedEvent(parseObject.getBoolean("isNonTimedEvent"));
                            event.setEndTime(parseObject.getDate("endTime"));
                            event.setName(parseObject.getString("name"));
                            event.setStartTime(parseObject.getDate("startTime"));
                            event.setLocation(parseObject.getString("location"));

                            event.setInfo(parseObject.getString("info"));

                            JSONArray linksJSONArr = parseObject.getJSONArray("links");

                            if (linksJSONArr != null) {
                                for (int m = 0; m < linksJSONArr.length(); ++m) {
                                    try {
                                        JSONObject linksObj = linksJSONArr.getJSONObject(m);

                                        EventLink eventLink = realm.createObject(EventLink.class, parseObject.getObjectId() + m);

                                        eventLink.setEventID(parseObject.getObjectId());
                                        eventLink.setLabel(linksObj.getString("label"));
                                        eventLink.setLink(linksObj.getString("link"));
                                    } catch (Exception ex) {
                                        Log.d("DataSync", ex.getMessage());
                                    }
                                }
                            }

                            JSONArray filesJSONArr = parseObject.getJSONArray("slidesFiles");

                            if (filesJSONArr != null) {
                                for (int m = 0; m < filesJSONArr.length(); ++m) {
                                    try {
                                        JSONObject fileObj = filesJSONArr.getJSONObject(m);

                                        EventFile eventFile = realm.createObject(EventFile.class, parseObject.getObjectId() + m);

                                        eventFile.setEventID(parseObject.getObjectId());
                                        eventFile.set__type(fileObj.getString("__type"));
                                        eventFile.setName(fileObj.getString("name"));
                                        eventFile.setUrl(fileObj.getString("url"));
                                    } catch (Exception ex) {
                                        Log.d("DataSync", ex.getMessage());
                                    }
                                }
                            }

                            JSONArray abstractJSONArr = parseObject.getJSONArray("abstractFiles");

                            if (abstractJSONArr != null) {
                                for (int m = 0; m < abstractJSONArr.length(); ++m) {
                                    try {
                                        JSONObject fileObj = abstractJSONArr.getJSONObject(m);

                                        EventAbstract eventAbstract = realm.createObject(EventAbstract.class, parseObject.getObjectId() + m);

                                        eventAbstract.setEventID(parseObject.getObjectId());
                                        eventAbstract.set__type(fileObj.getString("__type"));
                                        eventAbstract.setName(fileObj.getString("name"));
                                        eventAbstract.setUrl(fileObj.getString("url"));
                                    } catch (Exception ex) {
                                        Log.d("DataSync", ex.getMessage());
                                    }
                                }
                            }

                            JSONArray journalsJSONArr = parseObject.getJSONArray("journalFiles");

                            if (journalsJSONArr != null) {
                                for (int m = 0; m < journalsJSONArr.length(); ++m) {
                                    try {
                                        JSONObject fileObj = journalsJSONArr.getJSONObject(m);

                                        EventJournal eventJournal = realm.createObject(EventJournal.class, parseObject.getObjectId() + m);

                                        eventJournal.setEventID(parseObject.getObjectId());
                                        eventJournal.set__type(fileObj.getString("__type"));
                                        eventJournal.setName(fileObj.getString("name"));
                                        eventJournal.setUrl(fileObj.getString("url"));
                                    } catch (Exception ex) {
                                        Log.d("DataSync", ex.getMessage());
                                    }
                                }
                            }

                            JSONArray miscJSONArr = parseObject.getJSONArray("miscellaneousFiles");

                            if (miscJSONArr != null) {
                                for (int m = 0; m < miscJSONArr.length(); ++m) {
                                    try {
                                        JSONObject fileObj = miscJSONArr.getJSONObject(m);

                                        EventMisc eventMisc = realm.createObject(EventMisc.class, parseObject.getObjectId() + m);

                                        eventMisc.setEventID(parseObject.getObjectId());
                                        eventMisc.set__type(fileObj.getString("__type"));
                                        eventMisc.setName(fileObj.getString("name"));
                                        eventMisc.setUrl(fileObj.getString("url"));
                                    } catch (Exception ex) {
                                        Log.d("DataSync", ex.getMessage());
                                    }
                                }
                            }

                            ParseRelation<ParseObject> speakersRelation = parseObject.getRelation("speakers");

                            ParseQuery<ParseObject> speakersQuery = speakersRelation.getQuery();

                            /*
                            try {
                                List<ParseObject> speakersList = speakersQuery.find();

                                RealmList<Speaker> realmSpeakerList = new RealmList<Speaker>();

                                for (ParseObject speakerObj : speakersList) {
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
                            */


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

                        } else {


                            // result.setObjectId(parseObject.getObjectId());
                            result.setNonTimedEvent(parseObject.getBoolean("isNonTimedEvent"));
                            result.setEndTime(parseObject.getDate("endTime"));
                            result.setName(parseObject.getString("name"));
                            result.setStartTime(parseObject.getDate("startTime"));
                            result.setLocation(parseObject.getString("location"));

                            result.setInfo(parseObject.getString("info"));

                            JSONArray linksJSONArr = parseObject.getJSONArray("links");

                            if (linksJSONArr != null) {
                                for (int m = 0; m < linksJSONArr.length(); ++m) {
                                    try {
                                        JSONObject linksObj = linksJSONArr.getJSONObject(m);

                                        EventLink eventLink = realm.createObject(EventLink.class, parseObject.getObjectId() + m);

                                        eventLink.setEventID(parseObject.getObjectId());
                                        eventLink.setLabel(linksObj.getString("label"));
                                        eventLink.setLink(linksObj.getString("link"));
                                    } catch (Exception ex) {
                                        Log.d("DataSync", ex.getMessage());
                                    }
                                }
                            }

                            JSONArray filesJSONArr = parseObject.getJSONArray("slidesFiles");

                            if (filesJSONArr != null) {
                                for (int m = 0; m < filesJSONArr.length(); ++m) {
                                    try {
                                        JSONObject fileObj = filesJSONArr.getJSONObject(m);

                                        EventFile eventFile = realm.createObject(EventFile.class, parseObject.getObjectId() + m);

                                        eventFile.setEventID(parseObject.getObjectId());
                                        eventFile.set__type(fileObj.getString("__type"));
                                        eventFile.setName(fileObj.getString("name"));
                                        eventFile.setUrl(fileObj.getString("url"));
                                    } catch (Exception ex) {
                                        Log.d("DataSync", ex.getMessage());
                                    }
                                }
                            }

                            JSONArray abstractJSONArr = parseObject.getJSONArray("abstractFiles");

                            if (abstractJSONArr != null) {
                                for (int m = 0; m < abstractJSONArr.length(); ++m) {
                                    try {
                                        JSONObject fileObj = abstractJSONArr.getJSONObject(m);

                                        EventAbstract eventAbstract = realm.createObject(EventAbstract.class, parseObject.getObjectId() + m);

                                        eventAbstract.setEventID(parseObject.getObjectId());
                                        eventAbstract.set__type(fileObj.getString("__type"));
                                        eventAbstract.setName(fileObj.getString("name"));
                                        eventAbstract.setUrl(fileObj.getString("url"));
                                    } catch (Exception ex) {
                                        Log.d("DataSync", ex.getMessage());
                                    }
                                }
                            }

                            ParseRelation<ParseObject> speakersRelation = parseObject.getRelation("speakers");

                            ParseQuery<ParseObject> speakersQuery = speakersRelation.getQuery();

                            /*
                            try {
                                List<ParseObject> speakersList = speakersQuery.find();

                                RealmList<Speaker> realmSpeakerList = new RealmList<Speaker>();

                                for (ParseObject speakerObj : speakersList) {
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
                            */


                            ParseObject sessionObj = parseObject.getParseObject("session");

                            if (sessionObj != null) {
                                result.setSession(sessionObj.getObjectId());

                                if (parseObject.getBoolean("isNonTimedEvent") == true) {
                                    result.setStartTime(sessionObj.getDate("startTime"));
                                    result.setEndTime(sessionObj.getDate("endTime"));
                                }
                            }

                            result.setUpdatedAt(parseObject.getUpdatedAt());

                        }
                    }

                    realm.commitTransaction();
                    realm.close();

                    /*
                } else {
                    Log.d("DataSync", e.getMessage());
                }
                */

                if (shouldSyncAll == true) {
                    dataSyncSpeakers();
                } else {
                    callback.onDataSyncFinish();
                }
            // }});

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


        /*
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                */

                    Log.d("DataSyncManager", "Start Processing Speakers: " + allObjects.size() + " objects");

                    Realm realm = AppUtil.getRealmInstance(App.getInstance());
                    realm.beginTransaction();

                    int counter = 1;
                    for (final ParseObject parseObject : allObjects) {


                        if (counter % 100 == 0) {
                            Log.d("DataSyncManager", "Speaker Number: " + counter);
                        }
                        ++counter;

                        Speaker result = realm.where(Speaker.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

                        if (result == null) {


                            // Create an object
                            Speaker speaker = realm.createObject(Speaker.class, parseObject.getObjectId());

                            // speaker.setObjectId(parseObject.getObjectId());
                            speaker.setName(parseObject.getString("name"));
                            speaker.setAllowCheckIn(parseObject.getBoolean("allowCheckIn"));
                            speaker.setIOS_code(parseObject.getString("IOS_code"));

                            // speaker.setUpdatedAt(parseObject.getUpdatedAt());

                            speaker.setLocation(parseObject.getString("location"));
                            speaker.setOrganization(parseObject.getString("organization"));
                            speaker.setContactable(parseObject.getBoolean("isContactable"));

                            speaker.setJob(parseObject.getString("job"));
                            speaker.setBio(parseObject.getString("bio"));

                            speaker.setSpeakerLabel(parseObject.getString("speakerLabel"));

                            ParseObject conferenceObj = parseObject.getParseObject("conference");

                            if (conferenceObj != null) {
                                speaker.setConference(conferenceObj.getObjectId());
                            }

                            ParseFile parseImage = (ParseFile) parseObject.getParseFile("image");

                            try {
                                if (parseImage != null) {
                                    speaker.setImage(parseImage.getData());
                                }
                            } catch (Exception ex) {
                                Log.d("DataSyncManager", "Line 699 " + ex.getMessage());
                            }

                            // Log.d("APD", "Speaker ObjectId: " + speaker.getObjectId() +
                            // " IOS_code: " + speaker.getIOS_code());

                            JSONArray linksJSONArr = parseObject.getJSONArray("links");

                            if (linksJSONArr != null) {
                                for (int m = 0; m < linksJSONArr.length(); ++m) {
                                    try {
                                        JSONObject linksObj = linksJSONArr.getJSONObject(m);

                                        SpeakerLink speakerLink = realm.createObject(SpeakerLink.class, parseObject.getObjectId() + m);

                                        speakerLink.setSpeakerID(parseObject.getObjectId());
                                        speakerLink.setLabel(linksObj.getString("label"));
                                        speakerLink.setLink(linksObj.getString("link"));
                                    } catch (Exception ex) {
                                        Log.d("DataSync", ex.getMessage());
                                    }
                                }
                            }

                            JSONArray filesJSONArr = parseObject.getJSONArray("slidesFiles");

                            if (filesJSONArr != null) {
                                for (int m = 0; m < filesJSONArr.length(); ++m) {
                                    try {
                                        JSONObject fileObj = filesJSONArr.getJSONObject(m);

                                        SpeakerFile speakerFile = realm.createObject(SpeakerFile.class, parseObject.getObjectId() + m);

                                        speakerFile.setSpeakerID(parseObject.getObjectId());
                                        speakerFile.set__type(fileObj.getString("__type"));
                                        speakerFile.setName(fileObj.getString("name"));
                                        speakerFile.setUrl(fileObj.getString("url"));
                                    } catch (Exception ex) {
                                        Log.d("DataSync", ex.getMessage());
                                    }
                                }
                            }

                            JSONArray journalsJSONArr = parseObject.getJSONArray("journalFiles");

                            if (journalsJSONArr != null) {
                                for (int m = 0; m < journalsJSONArr.length(); ++m) {
                                    try {
                                        JSONObject fileObj = journalsJSONArr.getJSONObject(m);

                                        SpeakerJournal speakerJournal = realm.createObject(SpeakerJournal.class, parseObject.getObjectId() + m);

                                        speakerJournal.setSpeakerID(parseObject.getObjectId());
                                        speakerJournal.set__type(fileObj.getString("__type"));
                                        speakerJournal.setName(fileObj.getString("name"));
                                        speakerJournal.setUrl(fileObj.getString("url"));
                                    } catch (Exception ex) {
                                        Log.d("DataSync", ex.getMessage());
                                    }
                                }
                            }

                            JSONArray miscJSONArr = parseObject.getJSONArray("miscellaneousFiles");

                            if (miscJSONArr != null) {
                                for (int m = 0; m < miscJSONArr.length(); ++m) {
                                    try {
                                        JSONObject fileObj = miscJSONArr.getJSONObject(m);

                                        SpeakerMisc speakerMisc = realm.createObject(SpeakerMisc.class, parseObject.getObjectId() + m);

                                        speakerMisc.setSpeakerID(parseObject.getObjectId());
                                        speakerMisc.set__type(fileObj.getString("__type"));
                                        speakerMisc.setName(fileObj.getString("name"));
                                        speakerMisc.setUrl(fileObj.getString("url"));
                                    } catch (Exception ex) {
                                        Log.d("DataSync", ex.getMessage());
                                    }
                                }
                            }

                            JSONArray abstractJSONArr = parseObject.getJSONArray("abstractFiles");

                            if (abstractJSONArr != null) {
                                for (int m = 0; m < abstractJSONArr.length(); ++m) {
                                    try {
                                        JSONObject fileObj = abstractJSONArr.getJSONObject(m);

                                        SpeakerAbstract speakerAbstract = realm.createObject(SpeakerAbstract.class, parseObject.getObjectId() + m);

                                        speakerAbstract.setSpeakerID(parseObject.getObjectId());
                                        speakerAbstract.set__type(fileObj.getString("__type"));
                                        speakerAbstract.setName(fileObj.getString("name"));
                                        speakerAbstract.setUrl(fileObj.getString("url"));
                                    } catch (Exception ex) {
                                        Log.d("DataSync", ex.getMessage());
                                    }
                                }
                            }

                            ParseRelation<ParseObject> eventsRelation = parseObject.getRelation("event");

                            ParseQuery<ParseObject> eventsQuery = eventsRelation.getQuery();

                            /*
                            try {
                                eventsQuery.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        if (e == null) {
                                            Realm realm = AppUtil.getRealmInstance(App.getInstance());
                                            realm.beginTransaction();
                                            RealmList<Event> realmEventList = new RealmList<Event>();

                                            for (ParseObject eventObj : objects) {

                                                Event event = realm.where(Event.class).equalTo("objectId", eventObj.getObjectId()).findFirst();
                                                if (event != null) {
                                                    realmEventList.add(event);
                                                }
                                            }

                                            speaker.setEventsList(realmEventList);
                                            realm.commitTransaction();
                                            realm.close();
                                        }
                                    }
                                });
                            } catch (Exception ex) {
                                Log.d("DataSyncManager", "Line 727: " + ex.getMessage());
                            }
                            */

                        } else {


                            // result.setObjectId(parseObject.getObjectId());
                            result.setName(parseObject.getString("name"));
                            result.setAllowCheckIn(parseObject.getBoolean("allowCheckIn"));
                            result.setIOS_code(parseObject.getString("IOS_code"));

                            // result.setUpdatedAt(parseObject.getUpdatedAt());

                            result.setLocation(parseObject.getString("location"));
                            result.setOrganization(parseObject.getString("organization"));
                            result.setContactable(parseObject.getBoolean("isContactable"));

                            result.setJob(parseObject.getString("job"));
                            result.setBio(parseObject.getString("bio"));

                            result.setSpeakerLabel(parseObject.getString("speakerLabel"));

                            ParseObject conferenceObj = parseObject.getParseObject("conference");

                            if (conferenceObj != null) {
                                result.setConference(conferenceObj.getObjectId());
                            }

                            ParseFile parseImage = (ParseFile) parseObject.getParseFile("image");

                            try {
                                if (parseImage != null) {
                                    result.setImage(parseImage.getData());
                                }
                            } catch (Exception ex) {
                                Log.d("DataSyncManager", "Line 755 " + ex.getMessage());
                            }

                            JSONArray linksJSONArr = parseObject.getJSONArray("links");

                            if (linksJSONArr != null) {
                                for (int m = 0; m < linksJSONArr.length(); ++m) {
                                    try {
                                        JSONObject linksObj = linksJSONArr.getJSONObject(m);

                                        SpeakerLink speakerLink = realm.createObject(SpeakerLink.class, parseObject.getObjectId() + m);

                                        speakerLink.setSpeakerID(parseObject.getObjectId());
                                        speakerLink.setLabel(linksObj.getString("label"));
                                        speakerLink.setLink(linksObj.getString("link"));
                                    } catch (Exception ex) {
                                        Log.d("DataSync", ex.getMessage());
                                    }
                                }
                            }

                            JSONArray filesJSONArr = parseObject.getJSONArray("slidesFiles");

                            if (filesJSONArr != null) {
                                for (int m = 0; m < filesJSONArr.length(); ++m) {
                                    try {
                                        JSONObject fileObj = filesJSONArr.getJSONObject(m);

                                        SpeakerFile speakerFile = realm.createObject(SpeakerFile.class, parseObject.getObjectId() + m);

                                        speakerFile.setSpeakerID(parseObject.getObjectId());
                                        speakerFile.set__type(fileObj.getString("__type"));
                                        speakerFile.setName(fileObj.getString("name"));
                                        speakerFile.setUrl(fileObj.getString("url"));
                                    } catch (Exception ex) {
                                        Log.d("DataSync", ex.getMessage());
                                    }
                                }
                            }

                            JSONArray journalsJSONArr = parseObject.getJSONArray("journalFiles");

                            if (journalsJSONArr != null) {
                                for (int m = 0; m < journalsJSONArr.length(); ++m) {
                                    try {
                                        JSONObject fileObj = journalsJSONArr.getJSONObject(m);

                                        SpeakerJournal speakerJournal = realm.createObject(SpeakerJournal.class, parseObject.getObjectId() + m);

                                        speakerJournal.setSpeakerID(parseObject.getObjectId());
                                        speakerJournal.set__type(fileObj.getString("__type"));
                                        speakerJournal.setName(fileObj.getString("name"));
                                        speakerJournal.setUrl(fileObj.getString("url"));
                                    } catch (Exception ex) {
                                        Log.d("DataSync", ex.getMessage());
                                    }
                                }
                            }

                            JSONArray miscJSONArr = parseObject.getJSONArray("miscellaneousFiles");

                            if (miscJSONArr != null) {
                                for (int m = 0; m < miscJSONArr.length(); ++m) {
                                    try {
                                        JSONObject fileObj = miscJSONArr.getJSONObject(m);

                                        SpeakerMisc speakerMisc = realm.createObject(SpeakerMisc.class, parseObject.getObjectId() + m);

                                        speakerMisc.setSpeakerID(parseObject.getObjectId());
                                        speakerMisc.set__type(fileObj.getString("__type"));
                                        speakerMisc.setName(fileObj.getString("name"));
                                        speakerMisc.setUrl(fileObj.getString("url"));
                                    } catch (Exception ex) {
                                        Log.d("DataSync", ex.getMessage());
                                    }
                                }
                            }

                            JSONArray abstractJSONArr = parseObject.getJSONArray("abstractFiles");

                            if (abstractJSONArr != null) {
                                for (int m = 0; m < abstractJSONArr.length(); ++m) {
                                    try {
                                        JSONObject fileObj = abstractJSONArr.getJSONObject(m);

                                        SpeakerAbstract speakerAbstract = realm.createObject(SpeakerAbstract.class, parseObject.getObjectId() + m);

                                        speakerAbstract.setSpeakerID(parseObject.getObjectId());
                                        speakerAbstract.set__type(fileObj.getString("__type"));
                                        speakerAbstract.setName(fileObj.getString("name"));
                                        speakerAbstract.setUrl(fileObj.getString("url"));
                                    } catch (Exception ex) {
                                        Log.d("DataSync", ex.getMessage());
                                    }
                                }
                            }

                            /*
                            ParseRelation<ParseObject> eventsRelation = parseObject.getRelation("event");

                            ParseQuery<ParseObject> eventsQuery = eventsRelation.getQuery();

                            try {
                                eventsQuery.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        if (e == null) {
                                            Realm realm = AppUtil.getRealmInstance(App.getInstance());
                                            realm.beginTransaction();
                                            RealmList<Event> realmEventList = new RealmList<Event>();
                                            Speaker result = realm.where(Speaker.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

                                            for (ParseObject eventObj : objects) {

                                                Event event = realm.where(Event.class).equalTo("objectId", eventObj.getObjectId()).findFirst();
                                                if (event != null) {
                                                    realmEventList.add(event);
                                                }
                                            }

                                            result.setEventsList(realmEventList);
                                            realm.commitTransaction();
                                            realm.close();
                                        }
                                    }
                                });
                            } catch (Exception ex) {
                                Log.d("DataSyncManager", "Line 754: " + ex.getMessage());
                            }
                            */

                        }
                    }

                    realm.commitTransaction();
                    realm.close();

                    /*
                } else {
                    Log.d("DataSync", e.getMessage());
                }
                */

                if (shouldSyncAll == true) {
                    dataSyncAttendees();
                } else {
                    callback.onDataSyncFinish();
                }
            // }});


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


        /*
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                */
                    Log.d("DataSyncManager", "Start Processing Attendees: " + allObjects.size() + " objects");

                    int counter = 1;

                    Realm realm = AppUtil.getRealmInstance(App.getInstance());
                    realm.beginTransaction();

                    for (ParseObject parseObject : allObjects) {


                        if (counter % 100 == 0) {
                            Log.d("DataSyncManager", "Attendee Number: " + counter);
                        }
                        ++counter;

                        Attendee result = realm.where(Attendee.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

                        if (result == null) {
                            // realm.beginTransaction();

                            // Create an object
                            Attendee attendee = realm.createObject(Attendee.class, parseObject.getObjectId());

                            // attendee.setObjectId(parseObject.getObjectId());
                            attendee.setName(parseObject.getString("name"));
                            attendee.setAllowCheckIn(parseObject.getBoolean("allowCheckIn"));
                            attendee.setIOS_code(parseObject.getString("IOS_code"));

                            // attendee.setUpdatedAt(parseObject.getUpdatedAt());

                            attendee.setLocation(parseObject.getString("location"));
                            attendee.setOrganization(parseObject.getString("organization"));
                            attendee.setContactable(parseObject.getBoolean("isContactable"));

                            attendee.setJob(parseObject.getString("job"));

                            ParseObject conferenceObj = parseObject.getParseObject("conference");

                            if (conferenceObj != null) {
                                attendee.setConference(conferenceObj.getObjectId());
                            }

                            ParseFile parseImage = (ParseFile) parseObject.getParseFile("image");

                            try {
                                if (parseImage != null) {
                                    attendee.setImage(parseImage.getData());
                                }
                            } catch (Exception ex) {
                                Log.d("DataSyncManager", "Line 859 " + ex.getMessage());
                            }

                            ParseRelation<ParseObject> eventsRelation = parseObject.getRelation("event");

                            ParseQuery<ParseObject> eventsQuery = eventsRelation.getQuery();

                            /*
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
                            */

                        } else {

                            // result.setObjectId(parseObject.getObjectId());
                            result.setName(parseObject.getString("name"));
                            result.setAllowCheckIn(parseObject.getBoolean("allowCheckIn"));
                            result.setIOS_code(parseObject.getString("IOS_code"));

                            // result.setUpdatedAt(parseObject.getUpdatedAt());

                            result.setLocation(parseObject.getString("location"));
                            result.setOrganization(parseObject.getString("organization"));
                            result.setContactable(parseObject.getBoolean("isContactable"));

                            result.setJob(parseObject.getString("job"));

                            ParseObject conferenceObj = parseObject.getParseObject("conference");

                            if (conferenceObj != null) {
                                result.setConference(conferenceObj.getObjectId());
                            }

                            ParseFile parseImage = (ParseFile) parseObject.getParseFile("image");

                            try {
                                if (parseImage != null) {
                                    result.setImage(parseImage.getData());
                                }
                            } catch (Exception ex) {
                                Log.d("DataSyncManager", "Line 755 " + ex.getMessage());
                            }

                            ParseRelation<ParseObject> eventsRelation = parseObject.getRelation("event");

                            ParseQuery<ParseObject> eventsQuery = eventsRelation.getQuery();

                            /*
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
                            */
                        }
                    }

                    realm.commitTransaction();
                    realm.close();

                    /*
                } else {
                    Log.d("DataSync", e.getMessage());
                }
                */

                if (shouldSyncAll == true) {
                    dataSyncMasterNotifications();
                } else {
                    callback.onDataSyncFinish();
                }

            // }});

    }

    static public void dataSyncMasterNotifications() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("MasterNotification").setLimit(1000);

        Date date = getLastSyncDate();

        if (date != null) {
            query.whereGreaterThanOrEqualTo("updatedAt", date).whereNotEqualTo("isDeleted", true);
        }

        Log.d("DataSyncManager", "Start Query for Parse MasterNotifications");

        /*
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
        */

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {


                    Log.d("DataSyncManager", "Start Processing MasterNotifications: " + objects.size() + " objects");

                    Realm realm = AppUtil.getRealmInstance(App.getInstance());
                    realm.beginTransaction();

                    for (ParseObject parseObject : objects) {


                        MasterNotification result = realm.where(MasterNotification.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

                        if (result == null) {


                            // Create an object
                            MasterNotification alert = realm.createObject(MasterNotification.class, parseObject.getObjectId());

                            // alert.setObjectId(parseObject.getObjectId());
                            alert.setAlert(parseObject.getString("alert"));
                            alert.setNew(true);

                            ParseObject conferenceObj = parseObject.getParseObject("conference");

                            if (conferenceObj != null) {
                                alert.setConference(conferenceObj.getObjectId());
                            }

                            alert.setCreatedAt(parseObject.getCreatedAt());

                        } else {

                            // result.setObjectId(parseObject.getObjectId());
                            result.setAlert(parseObject.getString("alert"));

                            ParseObject conferenceObj = parseObject.getParseObject("conference");

                            if (conferenceObj != null) {
                                result.setConference(conferenceObj.getObjectId());
                            }

                            result.setCreatedAt(parseObject.getCreatedAt());

                        }
                    }

                    realm.commitTransaction();
                    realm.close();
                } else {
                    Log.d("DataSync", e.getMessage());
                }

                if (shouldSyncAll == true) {
                    dataSyncMaps();
                } else {
                    callback.onDataSyncFinish();
                }
            }});

    }

    static public void dataSyncMaps(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Maps").setLimit(1000);

        Date date = getLastSyncDate();

        if (date != null) {
            query.whereGreaterThanOrEqualTo("updatedAt", date).whereNotEqualTo("isDeleted", true);
        }

        Log.d("DataSyncManager", "Start Query for Parse Maps");

        /*
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
        */

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    Log.d("DataSyncManager", "Start Processing Maps: " + objects.size() + " objects");

                    Realm realm = AppUtil.getRealmInstance(App.getInstance());
                    realm.beginTransaction();

                    for (ParseObject parseObject : objects) {


                        Maps result = realm.where(Maps.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

                        if (result == null) {

                            // Create an object
                            Maps map = realm.createObject(Maps.class, parseObject.getObjectId());

                            // map.setObjectId(parseObject.getObjectId());
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

                        } else {

                            // result.setObjectId(parseObject.getObjectId());
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


                        }
                    }

                    realm.commitTransaction();
                    realm.close();
                } else {
                    Log.d("DataSync", e.getMessage());
                }

                if (shouldSyncAll == true) {
                    dataSyncSponsors();
                } else {
                    callback.onDataSyncFinish();
                }
            }});
    }

    static public void dataSyncSponsors() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Sponsor").setLimit(1000);

        Date date = getLastSyncDate();

        if (date != null) {
            query.whereGreaterThanOrEqualTo("updatedAt", date).whereNotEqualTo("isDeleted", true);
        }

        Log.d("DataSyncManager", "Start Query for Parse Sponsors");

        /*
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
        */

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    Log.d("DataSyncManager", "Start Processing Sponsors: " + objects.size() + " objects");

                    Realm realm = AppUtil.getRealmInstance(App.getInstance());
                    realm.beginTransaction();

                    for (ParseObject parseObject : objects) {

                        Sponsor result = realm.where(Sponsor.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

                        if (result == null) {

                            // Create an object
                            Sponsor sponsor = realm.createObject(Sponsor.class, parseObject.getObjectId());

                            // sponsor.setObjectId(parseObject.getObjectId());

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

                        } else {


                            // result.setObjectId(parseObject.getObjectId());
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

                        }
                    }
                    realm.commitTransaction();
                    realm.close();
                } else {
                    Log.d("DataSync", e.getMessage());
                }

                if (shouldSyncAll == true) {
                    dataSyncSpeakerEventCache();
                } else {
                    callback.onDataSyncFinish();
                }
            }});

    }

    static public void dataSyncSpeakerEventCache() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("IOS_SPEAKER_EVENT_CACHE").setLimit(1000);

        Date date = getLastSyncDate();

        if (date != null) {
            query.whereGreaterThanOrEqualTo("updatedAt", date).whereNotEqualTo("isDeleted", true);
        }


        Log.d("DataSyncManager", "Start Query for Parse IOS_SPEAKER_EVENT_CACHE ");

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


        /*
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                */

                    Log.d("DataSyncManager", "Start Processing SpeakerEventCache: " + allObjects.size() + " objects");

                    int counter = 1;

                    Realm realm = AppUtil.getRealmInstance(App.getInstance());
                    realm.beginTransaction();

                    for (ParseObject parseObject : allObjects) {


                        if (counter % 100 == 0) {
                            Log.d("DataSyncManager", "SpeakerEventCache Number: " + counter);
                        }
                        ++counter;

                        SpeakerEventCache result = realm.where(SpeakerEventCache.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

                        if (result == null) {

                            // Create an object
                            SpeakerEventCache cache = realm.createObject(SpeakerEventCache.class, parseObject.getObjectId());

                            // cache.setObjectId(parseObject.getObjectId());
                            cache.setEventID(parseObject.getString("eventID"));
                            cache.setSpeakerID(parseObject.getString("speakerID"));

                        } else {


                            // result.setObjectId(parseObject.getObjectId());
                            result.setEventID(parseObject.getString("eventID"));
                            result.setSpeakerID(parseObject.getString("speakerID"));


                        }
                    }

                    realm.commitTransaction();
                    realm.close();
                    /*
                } else {
                    Log.d("DataSync", e.getMessage());
                }
                */

                dataSyncTravelBusiness();
                // callback.onDataSyncFinish();
            // }});
    }

    static public void dataSyncTravelBusiness() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("TravelBusiness").whereNotEqualTo("isDeleted", true).setLimit(1000);

        Date date = getLastSyncDate();

        if (date != null) {
            // query.whereGreaterThanOrEqualTo("updatedAt", date);
        }

        Log.d("DataSyncManager", "Begin Parse Query For TravelBusiness");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    Log.d("DataSyncManager", "Start Processing TravelBusiness Records: " + objects.size() + " objects");

                    Realm realm = AppUtil.getRealmInstance(App.getInstance());
                    realm.beginTransaction();

                    for (ParseObject parseObject:
                         objects) {
                        TravelBusiness result = realm.where(TravelBusiness.class).equalTo("objectId", parseObject.getObjectId()).findFirst();

                        if (result == null) {

                            // Create an object
                            TravelBusiness travelBusiness = realm.createObject(TravelBusiness.class, parseObject.getObjectId());

                            ParseObject confObj = parseObject.getParseObject("conference");

                            if (confObj != null) {
                                travelBusiness.setConference(confObj.getObjectId());
                            }

                            travelBusiness.setAddress(parseObject.getString("address"));
                            travelBusiness.setBusinessName(parseObject.getString("businessName"));
                            travelBusiness.setBusinessType(parseObject.getString("businessType"));
                            travelBusiness.setKey(parseObject.getString("key"));
                            travelBusiness.setOtherDetails(parseObject.getString("otherDetails"));
                            travelBusiness.setRates(parseObject.getString("rates"));
                            travelBusiness.setWebsite(parseObject.getString("website"));

                        } else {


                            ParseObject confObj = parseObject.getParseObject("conference");

                            if (confObj != null) {
                                result.setConference(confObj.getObjectId());
                            }

                            result.setAddress(parseObject.getString("address"));
                            result.setBusinessName(parseObject.getString("businessName"));
                            result.setBusinessType(parseObject.getString("businessType"));
                            result.setKey(parseObject.getString("key"));
                            result.setOtherDetails(parseObject.getString("otherDetails"));
                            result.setRates(parseObject.getString("rates"));
                            result.setWebsite(parseObject.getString("website"));

                        }
                    }

                    realm.commitTransaction();
                    realm.close();

                    callback.onDataSyncFinish();
                }
            }
        });

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
        String lastSyncDateStr = prefs.getString(AppConfig.lastSyncDateName, "2017-05-15T17:00:00");

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
