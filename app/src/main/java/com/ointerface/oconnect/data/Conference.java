package com.ointerface.oconnect.data;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by AnthonyDoan on 4/11/17.
 */

public class Conference extends RealmObject {
    @Required
    @PrimaryKey
    private String objectId = "";
    private boolean showQRScanner = true;
    private boolean showNonTimedEvents = true;
    private String zip = "";
    private boolean showExternalLink = true;
    private boolean passwordProtectInfo = false;
    private String color = "#5595D0";
    private boolean shouldShowPin = false;
    private String toolbarLabelDiscussionBoard = "";
    private String externalLink = "";
    private String summary = "";
    private String toolbarLabelInfo = "";
    private boolean passwordProtectSpeakers = false;
    private Date endTime = null;
    private String participantsLabelSpeakers = "";
    private boolean showPosters = true;
    private String eventbriteToken = "";
    private boolean passwordProtectMaps = false;
    private String eventbriteId = "";
    private boolean showInfo = true;
    private String city = "";
    private String name = "";
    private String announcements = "";
    private String hashtag = "#";
    private String code = "";
    private String parkingLocation = "";
    private boolean passwordProtectSurvey = false;
    private String contactPhone = "";
    private Date updatedAt = null;
    private Date startTime = null;
    private String venue = "";
    private boolean isDeleted = false;
    private String toolbarLabelSchedule = "";
    private boolean showCheckin = false;
    private String toolbarLabelMaps = "";
    private boolean showQuestions = true;
    private String state = "";
    private String toolbarLabelSponsors = "";
    private boolean isPublic = true;
    private String toolbarLabelSurvey = "";
    private boolean showPaticipants = false;
    // This is the organization's objectId
    private String organization = "";
    private String mapUrl = "";
    private boolean showSchedule = true;
    private String participantsLabelParticipants = "";
    private String imageUrl = "";
    private String parkingInformation = "";
    private boolean showMaps = true;
    private String address = "";
    private boolean showRegistration = true;
    private boolean showSponsors = true;
    private String country = "";
    private boolean showDashboard = true;
    private String toolbarLabelNonTimedEvent = "";
    private boolean showSurvey = true;
    private String inappPassword = "";
    private String toolbarLabel = "";
    private String toolbarLabelParticipants = "";
    private boolean showPinEntry = false;
    private String travel = "";
    private String type = "Conference";
    private String survey = "";
    private String website = "";
    private Date createdAt = null;
    private byte[] image = null;
    private String contactEmail = "";
    private String description = "";
    private boolean showParticipants = true;
    private String toolbarLabelExternalLink = "";
    private String group = "";

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public boolean isShowQRScanner() {
        return showQRScanner;
    }

    public void setShowQRScanner(boolean showQRScanner) {
        this.showQRScanner = showQRScanner;
    }

    public boolean isShowNonTimedEvents() {
        return showNonTimedEvents;
    }

    public void setShowNonTimedEvents(boolean showNonTimedEvents) {
        this.showNonTimedEvents = showNonTimedEvents;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public boolean isShowExternalLink() {
        return showExternalLink;
    }

    public void setShowExternalLink(boolean showExternalLink) {
        this.showExternalLink = showExternalLink;
    }

    public boolean isPasswordProtectInfo() {
        return passwordProtectInfo;
    }

    public void setPasswordProtectInfo(boolean passwordProtectInfo) {
        this.passwordProtectInfo = passwordProtectInfo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isShouldShowPin() {
        return shouldShowPin;
    }

    public void setShouldShowPin(boolean shouldShowPin) {
        this.shouldShowPin = shouldShowPin;
    }

    public String getToolbarLabelDiscussionBoard() {
        return toolbarLabelDiscussionBoard;
    }

    public void setToolbarLabelDiscussionBoard(String toolbarLabelDiscussionBoard) {
        this.toolbarLabelDiscussionBoard = toolbarLabelDiscussionBoard;
    }

    public String getExternalLink() {
        return externalLink;
    }

    public void setExternalLink(String externalLink) {
        this.externalLink = externalLink;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getToolbarLabelInfo() {
        return toolbarLabelInfo;
    }

    public void setToolbarLabelInfo(String toolbarLabelInfo) {
        this.toolbarLabelInfo = toolbarLabelInfo;
    }

    public boolean isPasswordProtectSpeakers() {
        return passwordProtectSpeakers;
    }

    public void setPasswordProtectSpeakers(boolean passwordProtectSpeakers) {
        this.passwordProtectSpeakers = passwordProtectSpeakers;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getParticipantsLabelSpeakers() {
        return participantsLabelSpeakers;
    }

    public void setParticipantsLabelSpeakers(String participantsLabelSpeakers) {
        this.participantsLabelSpeakers = participantsLabelSpeakers;
    }

    public boolean isShowPosters() {
        return showPosters;
    }

    public void setShowPosters(boolean showPosters) {
        this.showPosters = showPosters;
    }

    public String getEventbriteToken() {
        return eventbriteToken;
    }

    public void setEventbriteToken(String eventbriteToken) {
        this.eventbriteToken = eventbriteToken;
    }

    public boolean isPasswordProtectMaps() {
        return passwordProtectMaps;
    }

    public void setPasswordProtectMaps(boolean passwordProtectMaps) {
        this.passwordProtectMaps = passwordProtectMaps;
    }

    public String getEventbriteId() {
        return eventbriteId;
    }

    public void setEventbriteId(String eventbriteId) {
        this.eventbriteId = eventbriteId;
    }

    public boolean isShowInfo() {
        return showInfo;
    }

    public void setShowInfo(boolean showInfo) {
        this.showInfo = showInfo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(String announcements) {
        this.announcements = announcements;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParkingLocation() {
        return parkingLocation;
    }

    public void setParkingLocation(String parkingLocation) {
        this.parkingLocation = parkingLocation;
    }

    public boolean isPasswordProtectSurvey() {
        return passwordProtectSurvey;
    }

    public void setPasswordProtectSurvey(boolean passwordProtectSurvey) {
        this.passwordProtectSurvey = passwordProtectSurvey;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getToolbarLabelSchedule() {
        return toolbarLabelSchedule;
    }

    public void setToolbarLabelSchedule(String toolbarLabelSchedule) {
        this.toolbarLabelSchedule = toolbarLabelSchedule;
    }

    public boolean isShowCheckin() {
        return showCheckin;
    }

    public void setShowCheckin(boolean showCheckin) {
        this.showCheckin = showCheckin;
    }

    public String getToolbarLabelMaps() {
        return toolbarLabelMaps;
    }

    public void setToolbarLabelMaps(String toolbarLabelMaps) {
        this.toolbarLabelMaps = toolbarLabelMaps;
    }

    public boolean isShowQuestions() {
        return showQuestions;
    }

    public void setShowQuestions(boolean showQuestions) {
        this.showQuestions = showQuestions;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getToolbarLabelSponsors() {
        return toolbarLabelSponsors;
    }

    public void setToolbarLabelSponsors(String toolbarLabelSponsors) {
        this.toolbarLabelSponsors = toolbarLabelSponsors;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getToolbarLabelSurvey() {
        return toolbarLabelSurvey;
    }

    public void setToolbarLabelSurvey(String toolbarLabelSurvey) {
        this.toolbarLabelSurvey = toolbarLabelSurvey;
    }

    public boolean isShowPaticipants() {
        return showPaticipants;
    }

    public void setShowPaticipants(boolean showPaticipants) {
        this.showPaticipants = showPaticipants;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    public boolean isShowSchedule() {
        return showSchedule;
    }

    public void setShowSchedule(boolean showSchedule) {
        this.showSchedule = showSchedule;
    }

    public String getParticipantsLabelParticipants() {
        return participantsLabelParticipants;
    }

    public void setParticipantsLabelParticipants(String participantsLabelParticipants) {
        this.participantsLabelParticipants = participantsLabelParticipants;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getParkingInformation() {
        return parkingInformation;
    }

    public void setParkingInformation(String parkingInformation) {
        this.parkingInformation = parkingInformation;
    }

    public boolean isShowMaps() {
        return showMaps;
    }

    public void setShowMaps(boolean showMaps) {
        this.showMaps = showMaps;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isShowRegistration() {
        return showRegistration;
    }

    public void setShowRegistration(boolean showRegistration) {
        this.showRegistration = showRegistration;
    }

    public boolean isShowSponsors() {
        return showSponsors;
    }

    public void setShowSponsors(boolean showSponsors) {
        this.showSponsors = showSponsors;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isShowDashboard() {
        return showDashboard;
    }

    public void setShowDashboard(boolean showDashboard) {
        this.showDashboard = showDashboard;
    }

    public String getToolbarLabelNonTimedEvent() {
        return toolbarLabelNonTimedEvent;
    }

    public void setToolbarLabelNonTimedEvent(String toolbarLabelNonTimedEvent) {
        this.toolbarLabelNonTimedEvent = toolbarLabelNonTimedEvent;
    }

    public boolean isShowSurvey() {
        return showSurvey;
    }

    public void setShowSurvey(boolean showSurvey) {
        this.showSurvey = showSurvey;
    }

    public String getInappPassword() {
        return inappPassword;
    }

    public void setInappPassword(String inappPassword) {
        this.inappPassword = inappPassword;
    }

    public String getToolbarLabel() {
        return toolbarLabel;
    }

    public void setToolbarLabel(String toolbarLabel) {
        this.toolbarLabel = toolbarLabel;
    }

    public String getToolbarLabelParticipants() {
        return toolbarLabelParticipants;
    }

    public void setToolbarLabelParticipants(String toolbarLabelParticipants) {
        this.toolbarLabelParticipants = toolbarLabelParticipants;
    }

    public boolean isShowPinEntry() {
        return showPinEntry;
    }

    public void setShowPinEntry(boolean showPinEntry) {
        this.showPinEntry = showPinEntry;
    }

    public String getTravel() {
        return travel;
    }

    public void setTravel(String travel) {
        this.travel = travel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSurvey() {
        return survey;
    }

    public void setSurvey(String survey) {
        this.survey = survey;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isShowParticipants() {
        return showParticipants;
    }

    public void setShowParticipants(boolean showParticipants) {
        this.showParticipants = showParticipants;
    }

    public String getToolbarLabelExternalLink() {
        return toolbarLabelExternalLink;
    }

    public void setToolbarLabelExternalLink(String toolbarLabelExternalLink) {
        this.toolbarLabelExternalLink = toolbarLabelExternalLink;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }




}
