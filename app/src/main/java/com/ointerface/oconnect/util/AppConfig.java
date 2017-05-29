package com.ointerface.oconnect.util;

/**
 * Created by AnthonyDoan on 4/11/17.
 */

public class AppConfig {
    // If app is Private Label app, then set this to true to show the splash screen for the organization
    static public boolean isPrivateLabelApp = false;

    static public String primaryOrganizationID = "D8cdOcYLOK";

    // Shared Preferences
    static public String sharedPrefsName = "com.ointerface.oconnect.sharedprefs";
    static public String lastSyncDateName = "com.ointerface.oconnect.sharedprefs.lastSyncDate";
    static public String sharedPrefsConferenceID = "com.ointerface.oconnect.sharedprefs.conferenceID";
    static public String sharedPrefsIsSignedIn = "com.ointerface.oconnect.sharedprefs.isSignedIn";
    static public String sharedPrefsSignedInUserID = "com.ointerface.oconnect.sharedprefs.signedInUserID";
    static public String getSharedPrefsPinPromptConferenceList = "com.ointerface.oconnect.sharedprefs.pinPromptConferenceList";
    static public String getSharedPrefsPinPromptSkippedConferenceList = "com.ointerface.oconnect.sharedprefs.pinPromptSkippedConferenceList";
    static public String getSharedPrefsNavItemPasswordEntered = "com.ointerface.oconnect.sharedprefs.navItemPasswordEntered";
    static public String getSharedPrefsDefaultRealmLoaded = "com.ointerface.oconnect.sharedprefs.defaultRealmLoaded";

    static public String defaultDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss";

    // Default UI values
    static public int defaultThemeColor = 0xFF5595D0;
    static public int whiteColor = 0xFFFFFFFF;
    static public int blueColor = 0xFF0000FF;
    static public int lightGreyColor = 0xFFE2E2E0;
    static public int hiddenGreyBackgroundColor = 0xFF6D7177;
}
