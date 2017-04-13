package com.ointerface.oconnect.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.ointerface.oconnect.util.AppConfig;
import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.ointerface.oconnect.App;

import io.realm.Realm;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by AnthonyDoan on 4/11/17.
 */

public class DataSyncManager {
    static private Context context;
    static private IDataSyncListener callback;
    static public ProgressDialog dialog;

    static public void beginDataSync(Context contextArg, IDataSyncListener callbackArg) {
        context = contextArg;
        callback = callbackArg;

        dialog = ProgressDialog.show((Context)callback, null, "Initializing Data ... Please wait.");

        dataSyncOrganizations();

    }

    static public void dataSyncOrganizations() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Organization");

        Date date = getLastSyncDate();

        if (date != null) {
            query.whereGreaterThanOrEqualTo("updatedAt", date);
        }

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    for (ParseObject parseObject : objects){
                        Realm realm = Realm.getInstance(App.getInstance());

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
                            org.setUpdatedAt(parseObject.getDate("updatedAt"));
                            org.setDeleted(parseObject.getBoolean("isDeleted"));
                            org.setCreatedAt(parseObject.getDate("createdAt"));
                            org.setWebsite(parseObject.getString("website"));

                            ParseFile parseImage = (ParseFile) parseObject.getParseFile("image");

                            try {
                                if (parseImage != null) {
                                    org.setImage(parseImage.getData());
                                }
                            } catch (Exception ex) {
                                Log.d("DataSyncManager", ex.getMessage());
                            }

                            realm.commitTransaction();
                        } else {
                            // Update in Realm
                            realm.beginTransaction();

                            result.setObjectId(parseObject.getObjectId());
                            result.setShowSplash(parseObject.getBoolean("showSplash"));
                            result.setName(parseObject.getString("name"));
                            result.setUpdatedAt(parseObject.getDate("updatedAt"));
                            result.setDeleted(parseObject.getBoolean("isDeleted"));
                            result.setCreatedAt(parseObject.getDate("createdAt"));
                            result.setWebsite(parseObject.getString("website"));

                            ParseFile parseImage = (ParseFile) parseObject.getParseFile("image");

                            try {
                                if (parseImage != null) {
                                    result.setImage(parseImage.getData());
                                }
                            } catch (Exception ex) {
                                Log.d("DataSyncManager", ex.getMessage());
                            }

                            realm.commitTransaction();
                        }
                    }
                } else {
                    Log.d("DataSyncManager", e.getMessage());
                }

                dataSyncConferences();
            }
        });
    }

    static public void dataSyncConferences() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Conference");

        Date date = getLastSyncDate();

        if (date != null) {
            query.whereGreaterThanOrEqualTo("updatedAt", date).whereNotEqualTo("isDeleted", false);
        }

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    for (ParseObject parseObject : objects){
                        Realm realm = Realm.getInstance(App.getInstance());

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
                            conference.setUpdatedAt(parseObject.getDate("updatedAt"));
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
                            result.setUpdatedAt(parseObject.getDate("updatedAt"));
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

                callback.onDataSyncFinish();
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
