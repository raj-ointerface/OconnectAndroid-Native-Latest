package com.ointerface.oconnect;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ointerface.oconnect.activities.*;

import com.ointerface.oconnect.data.Attendee;
import com.ointerface.oconnect.data.Conference;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.util.AppConfig;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class ConferenceListViewActivity extends AppCompatActivity {

    private ConferenceListViewAdapter adapter;

    private ListView lvConferences;

    private SearchView search;

    static public boolean showCancel = false;
    static public boolean launchedFromLeftNav = false;
    static public boolean hasMovedToNextActivity = false;

    private TextView tvCancel;
    private TextView tvTitle;

    static public int customColor = AppUtil.getPrimaryThemColorAsInt();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            int marginPx = AppUtil.convertDPToPXInt(ConferenceListViewActivity.this, 3);

            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

            navigation.setBackgroundColor(AppConfig.whiteColor);

            View view1 = navigation.findViewById(R.id.navigation_coming_soon);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            // RelativeLayout.LayoutParams lp = (ViewGroup.LayoutParams) view1.getLayoutParams();

            lp.setMargins(marginPx, marginPx, marginPx, marginPx);

            view1.setLayoutParams(lp);

            view1.requestLayout();

            View view2 = navigation.findViewById(R.id.navigation_all_upcoming);

            // LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) view2.getLayoutParams();
            // lp2.setMargins(marginPx, marginPx, marginPx, marginPx);

            view2.setLayoutParams(lp);

            view2.requestLayout();

            View view3 = navigation.findViewById(R.id.navigation_past);

            // LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) view3.getLayoutParams();
            // lp3.setMargins(marginPx, marginPx, marginPx, marginPx);

            view3.setLayoutParams(lp);

            view3.requestLayout();

            switch (item.getItemId()) {
                case R.id.navigation_coming_soon:
                    view1.setBackgroundColor(AppConfig.lightGreyColor);

                    view2.setBackgroundColor(customColor);

                    view3.setBackgroundColor(customColor);

                    displayComingSoon();
                    return true;
                case R.id.navigation_all_upcoming:
                    view1.setBackgroundColor(customColor);

                    view2.setBackgroundColor(AppConfig.lightGreyColor);

                    view3.setBackgroundColor(customColor);

                    displayAllUpcoming();
                    return true;
                case R.id.navigation_past:
                    view1.setBackgroundColor(customColor);

                    view2.setBackgroundColor(customColor);

                    view3.setBackgroundColor(AppConfig.lightGreyColor);

                    displayPast();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference_list_view);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setBackgroundColor(AppConfig.defaultThemeColor);
        setSupportActionBar(myToolbar);

        tvCancel = (TextView) findViewById(R.id.tvHeaderCancel);
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        tvTitle.setText("Conferences");

        if (showCancel == true) {
            tvCancel.setVisibility(View.VISIBLE);

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConferenceListViewActivity.this.finish();
                    overridePendingTransition(R.anim.hold, R.anim.disappear_to_bottom);
                }
            });
        }

        if (OConnectBaseActivity.selectedConference != null && OConnectBaseActivity.selectedConference.getColor() != null &&
                !OConnectBaseActivity.selectedConference.getColor().equalsIgnoreCase("")
                && !OConnectBaseActivity.selectedConference.getColor().equalsIgnoreCase("#")) {
            customColor = Color.parseColor(OConnectBaseActivity.selectedConference.getColor());

            myToolbar.getBackground().setAlpha(255);

            Drawable wrappedDrawable = DrawableCompat.wrap(myToolbar.getBackground());
            DrawableCompat.setTint(wrappedDrawable, customColor);

            getSupportActionBar().setBackgroundDrawable(wrappedDrawable);
        }

        lvConferences = (ListView) findViewById(R.id.lvConferences);

        lvConferences.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.sectionHeader.contains(position)) {
                    return;
                }

                final Conference conference = adapter.mData.get(position);

                OConnectBaseActivity.selectedConference = adapter.mData.get(position);

                if (conference.isPublic() == false &&
                        (AppUtil.getIsSignedIn(ConferenceListViewActivity.this) == false ||
                                (AppUtil.getIsSignedIn(ConferenceListViewActivity.this) == true
                                        && !AppUtil.getSelectedConferenceID(ConferenceListViewActivity.this).equalsIgnoreCase(conference.getObjectId())))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ConferenceListViewActivity.this);
                    builder.setTitle("Enter Password");

                    final EditText input = new EditText(ConferenceListViewActivity.this);

                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);

                    builder.setPositiveButton("Enter Conference", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                                if (input.getText().toString().contentEquals(conference.getCode())) {
                                    AppUtil.setSelectedConferenceID(ConferenceListViewActivity.this, conference.getObjectId());

                                    if (AppUtil.getIsSignedIn(ConferenceListViewActivity.this) == false) {
                                        Intent i = new Intent(ConferenceListViewActivity.this, SignInActivity1.class);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                                    } else {
                                        if (AppUtil.hasPinPromptEnteredForConference(ConferenceListViewActivity.this, conference.getObjectId()) == false &&
                                                conference.isShouldShowPin() == true &&
                                                AppUtil.hasPinPromptSkippedForConference(ConferenceListViewActivity.this, conference.getObjectId()) == false) {
                                            callPINPromptWorkflow(conference);
                                        } else {
                                            Intent i = new Intent(ConferenceListViewActivity.this, DashboardActivity.class);
                                            startActivity(i);
                                        }
                                    }
                            } else {
                                AlertDialog alertDialog = new AlertDialog.Builder(ConferenceListViewActivity.this).create();
                                alertDialog.setTitle("");
                                alertDialog.setMessage("Incorrect password.  Please try again.");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog dialog = builder.create();

                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                    dialog.show();
                } else {
                    AppUtil.setSelectedConferenceID(ConferenceListViewActivity.this, conference.getObjectId());
                    Intent i = new Intent(ConferenceListViewActivity.this, DashboardActivity.class);
                    startActivity(i);
                }


            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // navigation.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] { android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed},  // pressed
                new int[] { android.R.attr.state_selected}  // selected
        };

        int[] colors = new int[] {
                Color.WHITE,
                Color.WHITE,
                Color.WHITE,
                Color.WHITE,
                Color.WHITE
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);

        navigation.setItemTextColor(colorStateList);

        View view1 = navigation.findViewById(R.id.navigation_coming_soon);

        view1.setPadding(0,0,0,30);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params1.setMargins(0, 0, AppUtil.convertDPToPXInt(this, 3), 0);

        view1.setLayoutParams(params1);

        view1.requestLayout();

        View view2 = navigation.findViewById(R.id.navigation_all_upcoming);

        view2.setPadding(0,0,0,30);

        View view3 = navigation.findViewById(R.id.navigation_past);

        view3.setPadding(0,0,0,30);

        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params3.setMargins(AppUtil.convertDPToPXInt(this, 3), 0, 0, 0);

        view3.setLayoutParams(params3);

        view3.requestLayout();

        search = (SearchView) findViewById(R.id.search);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                displaySearchResults(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                displaySearchResults(newText);

                if (newText.equalsIgnoreCase("")) {
                    search.clearFocus();
                }

                return false;
            }
        });

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // search.setBackgroundColor(AppConfig.defaultThemeColor);
        search.setActivated(true);
        search.setQueryHint("Search by Name, Location, or Date");
        // search.onActionViewExpanded();
        // search.setIconified(true);
        search.setIconified(false);
        search.clearFocus();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setIconified(false);
            }
        });

        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                search.setIconified(false);
                return false;
            }
        });

        ImageView closeButton = (ImageView)search.findViewById(R.id.search_close_btn);

        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.search_src_text);

                et.setText("");

                search.clearFocus();

                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                // search.setIconified(false);
            }
        });

        LinearLayout linearLayout1 = (LinearLayout) search.getChildAt(0);
        LinearLayout linearLayout2 = (LinearLayout) linearLayout1.getChildAt(2);
        LinearLayout linearLayout3 = (LinearLayout) linearLayout2.getChildAt(1);
        AutoCompleteTextView autoComplete = (AutoCompleteTextView) linearLayout3.getChildAt(0);
        autoComplete.setTextSize(16);
        // autoComplete.setBackground(getResources().getDrawable(R.drawable.search_view));

        if (AppConfig.isPrivateLabelApp == true && launchedFromLeftNav == false) {
            if (getConferenceCount() <= 1) {
                if (AppUtil.getIsSignedIn(ConferenceListViewActivity.this) == true) {
                    Realm realm = AppUtil.getRealmInstance(App.getInstance());
                    OConnectBaseActivity.currentPerson = realm.where(Person.class).equalTo("objectId", AppUtil.getSignedInUserID(ConferenceListViewActivity.this)).findFirst();
                }

                Log.d("APD", "First start activity to Dashboard");

                ConferenceListViewActivity.this.finish();
                Intent i = new Intent(ConferenceListViewActivity.this, DashboardActivity.class);
                startActivity(i);
            }
        }

        if (launchedFromLeftNav == false && !AppUtil.getSelectedConferenceID(ConferenceListViewActivity.this).equalsIgnoreCase("")) {
            if (AppUtil.getIsSignedIn(ConferenceListViewActivity.this) == true) {
                Realm realm = AppUtil.getRealmInstance(App.getInstance());
                OConnectBaseActivity.currentPerson = realm.where(Person.class).equalTo("objectId", AppUtil.getSignedInUserID(ConferenceListViewActivity.this)).findFirst();
            }

            Log.d("APD", "Second start activity to Dashboard");

            ConferenceListViewActivity.this.finish();
            Intent i = new Intent(ConferenceListViewActivity.this, DashboardActivity.class);
            startActivity(i);
        } else if (launchedFromLeftNav == false) {
            Realm realm = AppUtil.getRealmInstance(this);

            com.ointerface.oconnect.data.AppConfig config = realm.where(com.ointerface.oconnect.data.AppConfig.class).findFirst();

            if (config != null && config.getShowConfList() == false) {
                ConferenceListViewActivity.this.finish();
                Intent i = new Intent(ConferenceListViewActivity.this, DashboardActivity.class);
                startActivity(i);
            }
        }

        navigation.setSelectedItemId(R.id.navigation_coming_soon);
        displayComingSoon();
    }

    public int getConferenceCount() {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<Conference> results = realm.where(Conference.class).equalTo("organization", AppConfig.primaryOrganizationID).findAll();

        return results.size();
    }

    public void callPINPromptWorkflow(Conference conference) {
        if (AppUtil.getIsSignedIn(ConferenceListViewActivity.this) == true) {
            Realm realm = AppUtil.getRealmInstance(App.getInstance());
            OConnectBaseActivity.currentPerson = realm.where(Person.class).equalTo("objectId", AppUtil.getSignedInUserID(ConferenceListViewActivity.this)).findFirst();
        }

        String usernametxt = OConnectBaseActivity.currentPerson.getContact_email();
        try {
            final Realm realm = AppUtil.getRealmInstance(App.getInstance());

            List<ParseObject> speakerList = ParseQuery.getQuery("Speaker").whereEqualTo("email", usernametxt).find();
            List<ParseObject> attendeeList = ParseQuery.getQuery("Attendee").whereEqualTo("email", usernametxt).find();

            if (speakerList.size() > 0) {
                ParseObject speakerObj = speakerList.get(0);
                speakerObj.put("UserLink", OConnectBaseActivity.currentPerson.getObjectId());
                speakerObj.save();

                realm.beginTransaction();
                Speaker speaker = realm.where(Speaker.class).equalTo("objectId", speakerObj.getObjectId()).findFirst();
                if (speaker != null) {
                    speaker.setUserLink(OConnectBaseActivity.currentPerson.getObjectId());
                }
                realm.commitTransaction();
            } else if (attendeeList.size() > 0) {
                ParseObject attendeeObj = attendeeList.get(0);
                attendeeObj.put("UserLink", OConnectBaseActivity.currentPerson.getObjectId());
                attendeeObj.save();

                realm.beginTransaction();
                Attendee attendee = realm.where(Attendee.class).equalTo("objectId", attendeeObj.getObjectId()).findFirst();
                if (attendee != null) {
                    attendee.setUserLink(OConnectBaseActivity.currentPerson.getObjectId());
                }
                realm.commitTransaction();
            }

            if (speakerList.size() == 0 &&
                    attendeeList.size() == 0 &&
                    AppUtil.hasPinPromptEnteredForConference(ConferenceListViewActivity.this, OConnectBaseActivity.selectedConference.getObjectId()) == false
                    && OConnectBaseActivity.selectedConference.isShouldShowPin() == true) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConferenceListViewActivity.this);
                builder.setTitle("Please Enter Your PIN To Verify Your Identity");
                builder.setMessage("If you have received an email from the organizer with a PIN number, please enter it here to verify your identity.  If you did not receive an email, please Skip this step.");

                final EditText input = new EditText(ConferenceListViewActivity.this);

                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

                builder.setPositiveButton("Enter PIN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            List<ParseObject> speakerList = ParseQuery.getQuery("Speaker").whereEqualTo("IOS_code", input.getText().toString()).find();
                            List<ParseObject> attendeeList = ParseQuery.getQuery("Attendee").whereEqualTo("IOS_code", input.getText().toString()).find();

                            boolean bUserLinked = false;

                            if (speakerList.size() > 0) {
                                ParseObject speakerObj = speakerList.get(0);
                                speakerObj.put("UserLink", OConnectBaseActivity.currentPerson.getObjectId());
                                speakerObj.save();

                                realm.beginTransaction();
                                Speaker speaker = realm.where(Speaker.class).equalTo("objectId", speakerObj.getObjectId()).findFirst();
                                if (speaker != null) {
                                    speaker.setUserLink(OConnectBaseActivity.currentPerson.getObjectId());
                                }
                                realm.commitTransaction();

                                bUserLinked = true;
                            } else if (attendeeList.size() > 0) {
                                ParseObject attendeeObj = attendeeList.get(0);
                                attendeeObj.put("UserLink", OConnectBaseActivity.currentPerson.getObjectId());
                                attendeeObj.save();

                                realm.beginTransaction();
                                Attendee attendee = realm.where(Attendee.class).equalTo("objectId", attendeeObj.getObjectId()).findFirst();
                                if (attendee != null) {
                                    attendee.setUserLink(OConnectBaseActivity.currentPerson.getObjectId());
                                }
                                realm.commitTransaction();

                                bUserLinked = true;
                            }

                            if (bUserLinked == true) {
                                AppUtil.addConferenceForPinPromptEntered(ConferenceListViewActivity.this, OConnectBaseActivity.selectedConference.getObjectId());
                                ConferenceListViewActivity.this.finish();
                                Intent i = new Intent(ConferenceListViewActivity.this, DashboardActivity.class);
                                startActivity(i);
                                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                            } else {
                                AlertDialog alertDialog = new AlertDialog.Builder(ConferenceListViewActivity.this).create();
                                alertDialog.setTitle("Error");
                                alertDialog.setMessage("Incorrect PIN.  Please try again when logging in.");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                ConferenceListViewActivity.this.finish();
                                                Intent i = new Intent(ConferenceListViewActivity.this, DashboardActivity.class);
                                                startActivity(i);
                                                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                                            }
                                        });
                                alertDialog.show();
                            }
                        } catch (Exception ex) {
                            Log.d("SignIn2", ex.getMessage());
                        }
                    }
                });

                builder.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ConferenceListViewActivity.this);
                        builder.setTitle("Are You Sure?");
                        builder.setMessage("If you received an email with a PIN, we strongly recommend you enter it so that we can verify your identity properly.");

                        final EditText input = new EditText(ConferenceListViewActivity.this);

                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        builder.setView(input);

                        builder.setPositiveButton("Enter PIN", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    List<ParseObject> speakerList = ParseQuery.getQuery("Speaker").whereEqualTo("IOS_code", input.getText().toString()).find();
                                    List<ParseObject> attendeeList = ParseQuery.getQuery("Attendee").whereEqualTo("IOS_code", input.getText().toString()).find();

                                    boolean bUserLinked = false;

                                    if (speakerList.size() > 0) {
                                        ParseObject speakerObj = speakerList.get(0);
                                        speakerObj.put("UserLink", OConnectBaseActivity.currentPerson.getObjectId());
                                        speakerObj.save();

                                        realm.beginTransaction();
                                        Speaker speaker = realm.where(Speaker.class).equalTo("objectId", speakerObj.getObjectId()).findFirst();
                                        if (speaker != null) {
                                            speaker.setUserLink(OConnectBaseActivity.currentPerson.getObjectId());
                                        }
                                        realm.commitTransaction();

                                        bUserLinked = true;
                                    } else if (attendeeList.size() > 0) {
                                        ParseObject attendeeObj = attendeeList.get(0);
                                        attendeeObj.put("UserLink", OConnectBaseActivity.currentPerson.getObjectId());
                                        attendeeObj.save();

                                        realm.beginTransaction();
                                        Attendee attendee = realm.where(Attendee.class).equalTo("objectId", attendeeObj.getObjectId()).findFirst();
                                        if (attendee != null) {
                                            attendee.setUserLink(OConnectBaseActivity.currentPerson.getObjectId());
                                        }
                                        realm.commitTransaction();

                                        bUserLinked = true;
                                    }

                                    if (bUserLinked == true) {
                                        AppUtil.addConferenceForPinPromptEntered(ConferenceListViewActivity.this, OConnectBaseActivity.selectedConference.getObjectId());
                                        ConferenceListViewActivity.this.finish();
                                        Intent i = new Intent(ConferenceListViewActivity.this, DashboardActivity.class);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                                    } else {
                                        AlertDialog alertDialog = new AlertDialog.Builder(ConferenceListViewActivity.this).create();
                                        alertDialog.setTitle("Error");
                                        alertDialog.setMessage("Incorrect PIN.  Please try again when logging in.");
                                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        ConferenceListViewActivity.this.finish();
                                                        Intent i = new Intent(ConferenceListViewActivity.this, DashboardActivity.class);
                                                        startActivity(i);
                                                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                                                    }
                                                });
                                        alertDialog.show();
                                    }
                                } catch (Exception ex) {
                                    Log.d("SignIn2", ex.getMessage());
                                }
                            }
                        });

                        builder.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppUtil.addConferenceForPinPromptSkipped(ConferenceListViewActivity.this, AppUtil.getSelectedConferenceID(ConferenceListViewActivity.this));
                                Intent i = new Intent(ConferenceListViewActivity.this, DashboardActivity.class);
                                startActivity(i);
                                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up );
                            }
                        });

                        builder.create().show();
                    }
                });

                builder.create().show();

                // AlertDialog dialog = builder.create();

                // dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                //dialog.show();
            } else {
                Intent i = new Intent(ConferenceListViewActivity.this, DashboardActivity.class);
                startActivity(i);
            }
        } catch (Exception ex) {
            Log.d("SignIn2", ex.getMessage());
        }
    }

    public void populateListView() {
        lvConferences.setAdapter(adapter);
    }

    public void displaySearchResults(String searchText) {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        Realm realm = AppUtil.getRealmInstance(App.getInstance());
        RealmResults<Conference> results = null;

        Calendar cNow = new GregorianCalendar();
        cNow.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));

        Date dNow = cNow.getTime();

        String[] sortBy = {"group","startTime", "endTime"};
        Sort[] sortAscending = {Sort.ASCENDING, Sort.DESCENDING, Sort.ASCENDING};

        switch (navigation.getSelectedItemId()) {
            case R.id.navigation_coming_soon:
                Calendar c = new GregorianCalendar();
                c.add(Calendar.DATE, 30);
                c.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));

                Date d30 =c.getTime();

                results = realm.where(Conference.class).lessThan("startTime", d30).greaterThanOrEqualTo("startTime", dNow).findAllSorted(sortBy,sortAscending);

                break;
            case R.id.navigation_all_upcoming:
                results = realm.where(Conference.class).greaterThanOrEqualTo("startTime", dNow).or().greaterThanOrEqualTo("endTime", dNow).findAllSorted(sortBy,sortAscending);

                break;
            case R.id.navigation_past:
                results = realm.where(Conference.class).lessThanOrEqualTo("endTime", dNow).findAllSorted(sortBy,sortAscending);
                break;
        }

        Conference[] conferences = new Conference[results.size()];

        for (int i = 0; i < results.size(); ++i) {
            conferences[i] = results.get(i);
        }

        adapter = new ConferenceListViewAdapter(ConferenceListViewActivity.this, conferences);

        String group = "";

        ArrayList<Conference> otherConferences = new ArrayList<Conference>();

        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);

        searchText = searchText.toLowerCase();

        for (int j = 0; j < conferences.length; ++j) {
            // filter out by search text
            if (!conferences[j].getName().toLowerCase().contains(searchText)
                    && !conferences[j].getAddress().toLowerCase().contains(searchText)
                    && !conferences[j].getCity().toLowerCase().contains(searchText)
                    && !conferences[j].getState().toLowerCase().contains(searchText)
                    && !conferences[j].getZip().contains(searchText)
                    && !conferences[j].getCountry().toLowerCase().contains(searchText)
                    && !df.format(conferences[j].getStartTime()).toLowerCase().contains(searchText)
                    && !df.format(conferences[j].getEndTime()).toLowerCase().contains(searchText)) {
                continue;
            }
            if (conferences[j].getGroup() == null || conferences[j].getGroup().equalsIgnoreCase("") ||
                    conferences[j].getGroup().equalsIgnoreCase("Other")) {
                realm.beginTransaction();
                conferences[j].setGroup("Other");
                realm.commitTransaction();
                otherConferences.add(conferences[j]);
                continue;
            }
            if (group.equalsIgnoreCase("") || !group.equalsIgnoreCase(conferences[j].getGroup())) {
                if (conferences[j].getGroup() != null) {
                    group = conferences[j].getGroup();
                }
                adapter.addSectionHeaderItem(conferences[j]);
                adapter.addItem(conferences[j]);
                continue;
            }
            adapter.addItem(conferences[j]);
        }

        for (int k = 0; k < otherConferences.size(); ++k) {
            if (k == 0) {
                adapter.addSectionHeaderItem(otherConferences.get(k));
                adapter.addItem(otherConferences.get(k));
                continue;
            }
            adapter.addItem(otherConferences.get(k));
        }

        populateListView();
    }

    public void displayComingSoon() {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        Calendar c=new GregorianCalendar();
        c.add(Calendar.DATE, 30);
        Date d30 =c.getTime();

        Date dNow = new Date();

        String[] sortBy = {"group","startTime", "endTime"};
        Sort[] sortAscending = {Sort.ASCENDING, Sort.DESCENDING, Sort.ASCENDING};

        RealmResults<Conference> results = realm.where(Conference.class).lessThan("startTime", d30).greaterThanOrEqualTo("startTime", dNow).findAllSorted(sortBy,sortAscending);

        Conference[] conferences = new Conference[results.size()];

        for (int i = 0; i < results.size(); ++i) {
            conferences[i] = results.get(i);
        }

        adapter = new ConferenceListViewAdapter(ConferenceListViewActivity.this, conferences);

        String group = "";

        ArrayList<Conference> otherConferences = new ArrayList<Conference>();

        for (int j = 0; j < conferences.length; ++j) {
            if (conferences[j].getGroup() == null || conferences[j].getGroup().equalsIgnoreCase("") ||
                    conferences[j].getGroup().equalsIgnoreCase("Other")) {
                realm.beginTransaction();
                conferences[j].setGroup("Other");
                realm.commitTransaction();
                otherConferences.add(conferences[j]);
                continue;
            }
            if (group.equalsIgnoreCase("") || !group.equalsIgnoreCase(conferences[j].getGroup())) {
                if (conferences[j].getGroup() != null) {
                    group = conferences[j].getGroup();
                }
                adapter.addSectionHeaderItem(conferences[j]);
                adapter.addItem(conferences[j]);
                continue;
            }
            adapter.addItem(conferences[j]);
        }

        for (int k = 0; k < otherConferences.size(); ++k) {
            if (k == 0) {
                adapter.addSectionHeaderItem(otherConferences.get(k));
                adapter.addItem(otherConferences.get(k));
                continue;
            }
            adapter.addItem(otherConferences.get(k));
        }

        populateListView();
    }

    public void displayAllUpcoming() {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        Date dNow = new Date();

        String[] sortBy = {"group","startTime", "endTime"};
        Sort[] sortAscending = {Sort.ASCENDING, Sort.DESCENDING, Sort.ASCENDING};

        RealmResults<Conference> results = realm.where(Conference.class).greaterThanOrEqualTo("startTime", dNow).or().greaterThanOrEqualTo("endTime", dNow).findAllSorted(sortBy,sortAscending);

        Conference[] conferences = new Conference[results.size()];

        for (int i = 0; i < results.size(); ++i) {
            conferences[i] = results.get(i);
        }

        adapter = new ConferenceListViewAdapter(ConferenceListViewActivity.this, conferences);

        String group = "";

        ArrayList<Conference> otherConferences = new ArrayList<Conference>();

        for (int j = 0; j < conferences.length; ++j) {
            if (conferences[j].getGroup() == null || conferences[j].getGroup().equalsIgnoreCase("") ||
                    conferences[j].getGroup().equalsIgnoreCase("Other")) {
                realm.beginTransaction();
                conferences[j].setGroup("Other");
                realm.commitTransaction();
                otherConferences.add(conferences[j]);
                continue;
            }
            if (group.equalsIgnoreCase("") || !group.equalsIgnoreCase(conferences[j].getGroup())) {
                if (conferences[j].getGroup() != null) {
                    group = conferences[j].getGroup();
                }
                adapter.addSectionHeaderItem(conferences[j]);
                adapter.addItem(conferences[j]);
                continue;
            }
            adapter.addItem(conferences[j]);
        }

        for (int k = 0; k < otherConferences.size(); ++k) {
            if (k == 0) {
                adapter.addSectionHeaderItem(otherConferences.get(k));
                adapter.addItem(otherConferences.get(k));
                continue;
            }
            adapter.addItem(otherConferences.get(k));
        }

        populateListView();
    }

    public void displayPast() {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        Date dNow = new Date();

        String[] sortBy = {"group","startTime", "endTime"};
        Sort[] sortAscending = {Sort.ASCENDING, Sort.DESCENDING, Sort.ASCENDING};

        RealmResults<Conference> results = realm.where(Conference.class).lessThanOrEqualTo("endTime", dNow).findAllSorted(sortBy,sortAscending);

        Conference[] conferences = new Conference[results.size()];

        for (int i = 0; i < results.size(); ++i) {
            conferences[i] = results.get(i);
        }

        adapter = new ConferenceListViewAdapter(ConferenceListViewActivity.this, conferences);

        String group = "";

        ArrayList<Conference> otherConferences = new ArrayList<Conference>();

        for (int j = 0; j < conferences.length; ++j) {
            if (conferences[j].getGroup() == null || conferences[j].getGroup().equalsIgnoreCase("") ||
                    conferences[j].getGroup().equalsIgnoreCase("Other")) {
                realm.beginTransaction();
                conferences[j].setGroup("Other");
                realm.commitTransaction();
                otherConferences.add(conferences[j]);
                continue;
            }
            if (group.equalsIgnoreCase("") || !group.equalsIgnoreCase(conferences[j].getGroup())) {
                if (conferences[j].getGroup() != null) {
                    group = conferences[j].getGroup();
                }
                adapter.addSectionHeaderItem(conferences[j]);
                adapter.addItem(conferences[j]);
                continue;
            }
            adapter.addItem(conferences[j]);
        }

        for (int k = 0; k < otherConferences.size(); ++k) {
            if (k == 0) {
                adapter.addSectionHeaderItem(otherConferences.get(k));
                adapter.addItem(otherConferences.get(k));
                continue;
            }
            adapter.addItem(otherConferences.get(k));
        }

        populateListView();
    }

}
