package com.ointerface.oconnect.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.ointerface.oconnect.App;
import com.ointerface.oconnect.ConferenceListViewActivity;
import com.ointerface.oconnect.CustomSplashActivity;
import com.ointerface.oconnect.MainSplashActivity;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.adapters.NavExpandableListViewAdapter;
import com.ointerface.oconnect.adapters.ScheduleSwipeListAdapter;
import com.ointerface.oconnect.adapters.SearchExpandableListViewAdapter;
import com.ointerface.oconnect.containers.MenuItemHolder;
import com.ointerface.oconnect.data.Attendee;
import com.ointerface.oconnect.data.Conference;
import com.ointerface.oconnect.data.DataSyncManager;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.IDataSyncListener;
import com.ointerface.oconnect.data.MasterNotification;
import com.ointerface.oconnect.data.Organization;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.data.Session;
import com.ointerface.oconnect.data.SinchMessage;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.data.SpeakerEventCache;
import com.ointerface.oconnect.fragments.SearchDialogFragment;
import com.ointerface.oconnect.messaging.MessageAdapter;
import com.ointerface.oconnect.messaging.MessagingActivity;
import com.ointerface.oconnect.messaging.MessagingListActivity;
import com.ointerface.oconnect.messaging.SinchService;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.view.View.GONE;


public class OConnectBaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IDataSyncListener, ServiceConnection,
        MessageClientListener {

    private SinchService.SinchServiceInterface mSinchServiceInterface;

    NavExpandableListViewAdapter listAdapter;
    SearchExpandableListViewAdapter searchAdapter;
    ExpandableListView expListView;
    ExpandableListView expSearchView;

    List<String> listDataHeader;
    HashMap<String, List<MenuItemHolder>> listDataChild;

    SearchView navSearch;

    static public Conference selectedConference;
    static public Person currentPerson;

    public ImageView ivProfileLanyard;
    public ImageView ivSearch;
    public ImageView ivHelp;
    public CircleImageView ivRightToolbarIcon;
    public ImageView ivRightToolbarIconOverlay;
    public TextView tvToolbarTitle;
    public TextView tvEdit;
    public ImageView ivHeaderBack;
    public TextView tvHeaderBack;

    public View viewRedDot;

    public NavigationView navigationView;
    public NavigationView navigationViewRight;
    public DrawerLayout drawer;

    public boolean isTransparentToolbar = false;
    public boolean bJustSignedOut = false;

    public TextView tvName;
    public CircleImageView ivProfilePicture;
    public Switch switchContactable;
    public TextView tvRightNavHeader;

    // public Bitmap bmp;
    public Button btnConnections;
    public Button btnMessaging;
    public TextView tvSignOut;
    public ImageView ivSignOut;
    public ImageView ivAccountEdit;

    public static boolean bIsSinchStarted = false;

    private Switch switchTwitter;
    private Switch switchFacebook;
    private Switch switchLinkedIn;

    protected void onCreateDrawer() {
        // super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_oconnect_base);

        App.getInstance().activity = this;

        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        selectedConference = realm.where(Conference.class).equalTo("objectId", AppUtil.getSelectedConferenceID(OConnectBaseActivity.this)).findFirst();

        if (AppUtil.getIsSignedIn(OConnectBaseActivity.this) == true) {
            currentPerson = realm.where(Person.class).equalTo("objectId", AppUtil.getSignedInUserID(OConnectBaseActivity.this)).findFirst();
        }

        /*
        if (bIsSinchStarted == false) {
            getApplicationContext().bindService(new Intent(this, SinchService.class), this,
                    BIND_AUTO_CREATE);

            bIsSinchStarted = true;
        }
        */

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.getBackground().setAlpha(0);

        getSupportActionBar().setElevation(0);

        toolbar.setTitle("");


        if (selectedConference != null && selectedConference.getColor() != null &&
                !selectedConference.getColor().equalsIgnoreCase("")
                && !selectedConference.getColor().equalsIgnoreCase("#") &&
                isTransparentToolbar == false) {
            int color = Color.parseColor(selectedConference.getColor());

            toolbar.getBackground().setAlpha(255);

            Drawable wrappedDrawable = DrawableCompat.wrap(toolbar.getBackground());
            DrawableCompat.setTint(wrappedDrawable, color);

            getSupportActionBar().setBackgroundDrawable(wrappedDrawable);
        }

        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);

        tvHeaderBack = (TextView) findViewById(R.id.tvHeaderBack);
        ivHeaderBack = (ImageView) findViewById(R.id.ivHeaderBack);

        ivProfileLanyard = (ImageView) toolbar.findViewById(R.id.ivProfileLanyard);

        ivProfileLanyard.setVisibility(GONE);

        ivHelp = (ImageView) toolbar.findViewById(R.id.ivQuestion);

        ivHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OConnectBaseActivity.this, HelpViewPagerActivity.class);
                startActivity(i);
            }
        });

        ivHelp.setVisibility(GONE);

        ivRightToolbarIcon = (CircleImageView) toolbar.findViewById(R.id.ivRightToolbarPerson);

        ivRightToolbarIconOverlay = (ImageView) toolbar.findViewById(R.id.ivRightToolbarPersonOverlay);

        ivRightToolbarIconOverlay.setImageDrawable(AppUtil.changeDrawableColor(OConnectBaseActivity.this, R.drawable.icon_envelop, Color.RED));

        ivRightToolbarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtil.getIsSignedIn(OConnectBaseActivity.this) == false) {
                    Intent i = new Intent(OConnectBaseActivity.this, SignInActivity1.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    return;
                }

                NavigationView navigationViewRight = (NavigationView) findViewById(R.id.nav_view_right);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

                drawer.openDrawer(navigationViewRight, true);
            }
        });

        ivSearch = (ImageView) toolbar.findViewById(R.id.ivSearch);

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDialogFragment dialogFragment = new SearchDialogFragment ();
                dialogFragment.show(getSupportFragmentManager(), "Search Fragment");
            }
        });

        tvEdit = (TextView) toolbar.findViewById(R.id.tvHeaderEdit);
        tvEdit.setVisibility(GONE);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationViewRight = (NavigationView) findViewById(R.id.nav_view_right);
        navigationViewRight.setNavigationItemSelectedListener(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (bJustSignedOut == false && drawerView.equals(navigationViewRight) && AppUtil.getIsSignedIn(OConnectBaseActivity.this) == false) {
                    Intent i = new Intent(OConnectBaseActivity.this, SignInActivity1.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if (AppUtil.getIsSignedIn(OConnectBaseActivity.this) == false) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, navigationViewRight);
        } else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, navigationViewRight);
        }

        tvName = (TextView) navigationViewRight.findViewById(R.id.tvName);
        ivProfilePicture = (CircleImageView) navigationViewRight.findViewById(R.id.ivAccountProfilePicture);
        switchContactable = (Switch) navigationViewRight.findViewById(R.id.switchAccountContactable);
        tvRightNavHeader = (TextView) navigationViewRight.findViewById(R.id.tvMyAccount);
        btnConnections = (Button) navigationViewRight.findViewById(R.id.btnConnections);
        btnMessaging = (Button) navigationViewRight.findViewById(R.id.btnMessaging);
        ivSignOut = (ImageView) navigationViewRight.findViewById(R.id.ivSignOut);
        tvSignOut = (TextView) navigationViewRight.findViewById(R.id.tvSignOut);
        ivAccountEdit = (ImageView) navigationViewRight.findViewById(R.id.ivAccountEdit);
        viewRedDot = (View) navigationViewRight.findViewById(R.id.viewRedDot);

        viewRedDot.bringToFront();
        viewRedDot.setZ(15);

        if (currentPerson == null) {
            ivRightToolbarIconOverlay.setVisibility(GONE);
            viewRedDot.setVisibility(GONE);
        } else {
            int messageCount = getUnreadMessagesCount(currentPerson.getObjectId());

            if (messageCount > 0) {
                ivRightToolbarIconOverlay.setVisibility(View.VISIBLE);
                viewRedDot.setVisibility(View.VISIBLE);
            } else {
                ivRightToolbarIconOverlay.setVisibility(GONE);
                viewRedDot.setVisibility(GONE);
            }
        }

        tvRightNavHeader.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());
        btnConnections.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());
        btnMessaging.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());
        ivSignOut.setBackground(AppUtil.changeDrawableColor(OConnectBaseActivity.this,R.drawable.icon_sign_out, AppUtil.getPrimaryThemColorAsInt()));
        tvSignOut.setTextColor(AppUtil.getPrimaryThemColorAsInt());

        btnConnections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OConnectBaseActivity.this, ConnectionsActivity.class);
                startActivity(i);
            }
        });

        btnMessaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                Intent intent = new Intent(OConnectBaseActivity.this, MessagingActivity.class);

                MessagingActivity.recipientIDStr = "Hk17CJJSDc";

                startActivity(intent);
                */

                Intent i = new Intent(OConnectBaseActivity.this, MessagingListActivity.class);
                startActivity(i);
            }
        });

        ivAccountEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OConnectBaseActivity.this, EditAccountActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up );
            }
        });

        if (AppUtil.getIsSignedIn(OConnectBaseActivity.this) == true && currentPerson != null) {
            String firstName = "";
            String lastName = "";

            if (currentPerson.getFirstName() != null) {
                firstName = currentPerson.getFirstName();
            }

            if (currentPerson.getLastName() != null) {
                lastName = currentPerson.getLastName();
            }


            tvName.setText(firstName + " " + lastName);

            try {
                if (currentPerson.getPictureURL() != null
                        && !currentPerson.getPictureURL().equalsIgnoreCase("")) {

                    final String pictureURL = currentPerson.getPictureURL();

                    Log.d("APD", "currentPerson.getPictureURL(): " + currentPerson.getPictureURL());

                    new AsyncTask<Void, Void, Void>() {
                        public Bitmap bmp;
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                InputStream in = new URL(pictureURL).openStream();
                                bmp = BitmapFactory.decodeStream(in);
                            } catch (Exception e) {
                                Log.d("APD", e.getMessage());
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            Log.d("APD", "pictureURL BITMAP: " + bmp);

                            if (bmp != null) {
                                Drawable d = new BitmapDrawable(OConnectBaseActivity.this.getResources(), bmp);

                                ivProfilePicture.setImageDrawable(d);
                                ivRightToolbarIcon.setImageDrawable(d);
                            }
                        }

                    }.execute();
                }

                if (!getSinchServiceInterface().isStarted()) {
                    getSinchServiceInterface().startClient(currentPerson.getObjectId());
                }
            } catch (Exception ex) {
                if (ex.getMessage() != null) {
                    Log.d("APD", ex.getMessage());
                }
            }
        }

        /*
        navigationViewRight.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (AppUtil.getIsSignedIn(OConnectBaseActivity.this) == false) {
                    Intent i = new Intent(OConnectBaseActivity.this, SignInActivity1.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
                return false;
            }
        });
        */

        switchContactable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Realm realm = AppUtil.getRealmInstance(App.getInstance());
                realm.beginTransaction();

                currentPerson.setContactable(isChecked);

                realm.commitTransaction();

                try {
                    ParseUser user = ParseUser.getQuery().get(currentPerson.getObjectId());
                    user.put("isContactable", isChecked);
                    user.save();
                } catch (Exception ex) {
                    Log.d("APD", ex.getMessage());
                }

            }
        });

        // get the listview
        // View headerView = navigationView.getHeaderView(0);
        expListView = (ExpandableListView) navigationView.findViewById(R.id.elvLeftNavItems);

        expSearchView = (ExpandableListView) navigationView.findViewById(R.id.elvSearchItems);

        expSearchView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                RealmObject obj = (RealmObject) searchAdapter.getChild(groupPosition, childPosition);

                if (obj instanceof Event) {
                    Event event = (Event) obj;

                    // Goto Event Detail View
                    EventDetailViewActivity.mItems = new ArrayList<RealmObject>();

                    EventDetailViewActivity.mItems.add(obj);

                    int newPosition = 0;

                    Intent i = new Intent(OConnectBaseActivity.this, EventDetailViewActivity.class);
                    i.putExtra("EVENT_NUMBER", newPosition);
                    startActivity(i);
                } else if (obj instanceof Speaker) {
                    Speaker speaker = (Speaker) obj;

                    SpeakerDetailViewActivity.mItems = new ArrayList<RealmObject>();

                    SpeakerDetailViewActivity.mItems.add(speaker);

                    Intent i = new Intent(OConnectBaseActivity.this, SpeakerDetailViewActivity.class);

                    i.putExtra("SPEAKER_NUMBER", 0);

                    startActivity(i);

                } else if (obj instanceof Attendee) {
                    Attendee attendee = (Attendee) obj;

                    AttendeeDetailViewActivity.mItems = new ArrayList<RealmObject>();

                    AttendeeDetailViewActivity.mItems.add(attendee);

                    Intent i = new Intent(OConnectBaseActivity.this, AttendeeDetailViewActivity.class);

                    i.putExtra("ATTENDEE_NUMBER", 0);

                    startActivity(i);
                }

                return false;
            }
        });

        navSearch = (SearchView) navigationView.findViewById(R.id.search);

        navSearch.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());

        navSearch.setActivated(true);
        navSearch.setQueryHint("Search");

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        navSearch.setIconified(false);
        navSearch.clearFocus();

        navSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navSearch.setIconified(false);
            }
        });

        navSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                navSearch.setIconified(false);
                return false;
            }
        });

        navSearch.bringToFront();

        LinearLayout linearLayout1 = (LinearLayout) navSearch.getChildAt(0);
        LinearLayout linearLayout2 = (LinearLayout) linearLayout1.getChildAt(2);
        LinearLayout linearLayout3 = (LinearLayout) linearLayout2.getChildAt(1);
        AutoCompleteTextView autoComplete = (AutoCompleteTextView) linearLayout3.getChildAt(0);
        autoComplete.setTextSize(14);
        autoComplete.setBackground(getResources().getDrawable(R.drawable.search_view));

        autoComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navSearch.setIconified(false);
            }
        });

        linearLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navSearch.setIconified(false);
            }
        });

        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navSearch.setIconified(false);
            }
        });

        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navSearch.setIconified(false);
            }
        });

        // preparing list data
        prepareListData();

        listAdapter = new NavExpandableListViewAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return groupPosition == 0; // This way the expander cannot be collapsed
            }
        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                final MenuItemHolder childItem = (MenuItemHolder) listAdapter.getChild(groupPosition, childPosition);

                Intent i;

                switch (childItem.menuIconResID) {
                    case R.drawable.icon_conferences:
                        ConferenceListViewActivity.showCancel = true;
                        ConferenceListViewActivity.launchedFromLeftNav = true;
                        i = new Intent(OConnectBaseActivity.this, ConferenceListViewActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                        break;
                    case R.drawable.icon_registration:
                        i = new Intent(OConnectBaseActivity.this, WebViewActivity.class);
                        i.putExtra("TITLE", "Registration");
                        i.putExtra("URL", "https://www.eventbrite.com/e/" + selectedConference.getEventbriteId());
                        i.putExtra("BACK_TEXT", "");
                        i.putExtra("OPEN", "");
                        i.putExtra("isRegistration", true);
                        startActivity(i);
                        break;
                    case R.drawable.icon_dashboard:
                        i = new Intent(OConnectBaseActivity.this, DashboardActivity.class);
                        startActivity(i);
                        break;
                    case R.drawable.icon_schedule:
                        i = new Intent(OConnectBaseActivity.this, ScheduleActivity.class);
                        startActivity(i);
                        break;
                    case R.drawable.icon_non_timed_event_2:
                        i = new Intent(OConnectBaseActivity.this, NTEScheduleActivity.class);
                        startActivity(i);
                        break;
                    case R.drawable.icon_announcements:
                        if (AppUtil.getIsSignedIn(OConnectBaseActivity.this) == false) {
                            AppUtil.displayPleaseSignInDialog(OConnectBaseActivity.this);
                        } else {
                            i = new Intent(OConnectBaseActivity.this, AnnouncementsActivity.class);
                            startActivity(i);
                        }
                        break;
                    case R.drawable.icon_info:
                        if (AppUtil.getIsLeftNavUnlocked(OConnectBaseActivity.this) == false
                                && selectedConference.isPasswordProtectInfo() == true) {
                            AppUtil.displayNavPassword(OConnectBaseActivity.this, R.drawable.icon_info);
                            return false;
                        }
                        i = new Intent(OConnectBaseActivity.this, InfoActivity.class);
                        startActivity(i);
                        break;
                    case R.drawable.icon_maps:
                        if (AppUtil.getIsLeftNavUnlocked(OConnectBaseActivity.this) == false
                                && selectedConference.isPasswordProtectMaps() == true) {
                            AppUtil.displayNavPassword(OConnectBaseActivity.this, R.drawable.icon_maps);
                            return false;
                        }
                        i = new Intent(OConnectBaseActivity.this, MapsListActivity.class);
                        startActivity(i);
                        break;
                    case R.drawable.icon_refresh:
                        DataSyncManager.dialog = ProgressDialog.show((Context)OConnectBaseActivity.this, null, "Refreshing Data ... Please wait.");

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                DataSyncManager.shouldSyncAll = true;
                                DataSyncManager.beginDataSync(getApplicationContext(), OConnectBaseActivity.this);
                            }
                        });
                        break;
                    case R.drawable.icon_share:
                        i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        String shareBody = selectedConference.getWebsite();
                        i.putExtra(Intent.EXTRA_SUBJECT, "oConnect Website");
                        i.putExtra(Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(i, "Share via oConnect"));
                        break;
                    case R.drawable.icon_about_us:
                        i = new Intent(OConnectBaseActivity.this, AboutUsActivity.class);
                        startActivity(i);
                        break;
                    case R.drawable.icon_qr_scanner:
                        i = new Intent(OConnectBaseActivity.this, QRCodeScannerActivity.class);
                        startActivity(i);
                        break;
                    case R.drawable.icon_sponsors:
                        i = new Intent(OConnectBaseActivity.this, SponsorsListActivity.class);
                        startActivity(i);
                        break;
                    case R.drawable.icon_my_notes:
                        if (AppUtil.getIsSignedIn(OConnectBaseActivity.this) == false) {
                            AppUtil.displayPleaseSignInDialog(OConnectBaseActivity.this);
                            return false;
                        }
                        i = new Intent(OConnectBaseActivity.this, MyNotesActivity.class);
                        startActivity(i);
                        break;
                    case R.drawable.icon_my_agenda:
                        if (AppUtil.getIsSignedIn(OConnectBaseActivity.this) == false) {
                            AppUtil.displayPleaseSignInDialog(OConnectBaseActivity.this);
                            return false;
                        }
                        i = new Intent(OConnectBaseActivity.this, MyAgendaActivity.class);
                        startActivity(i);
                        break;
                    case R.drawable.ic_person:
                        if (AppUtil.getIsSignedIn(OConnectBaseActivity.this) == false) {
                            AppUtil.displayPleaseSignInDialog(OConnectBaseActivity.this);
                            return false;
                        }

                        if (AppUtil.getIsLeftNavUnlocked(OConnectBaseActivity.this) == false
                                && selectedConference.isPasswordProtectSpeakers() == true) {
                            AppUtil.displayNavPassword(OConnectBaseActivity.this, R.drawable.ic_person);
                            return false;
                        }

                        i = new Intent(OConnectBaseActivity.this, ParticipantsActivity.class);
                        startActivity(i);
                        break;
                    case R.drawable.icon_survey:
                        i = new Intent(OConnectBaseActivity.this, WebViewActivity.class);

                        if (selectedConference.getToolbarLabelSurvey() != null &&
                                !selectedConference.getToolbarLabelSurvey().equalsIgnoreCase("")) {
                            i.putExtra("TITLE", selectedConference.getToolbarLabelSurvey());
                        } else {
                            i.putExtra("TITLE", "Survey");
                        }
                        i.putExtra("URL", selectedConference.getSurvey());
                        i.putExtra("BACK_TEXT", "");
                        i.putExtra("OPEN", "");
                        i.putExtra("isSurvey", true);
                        startActivity(i);


                        return false;
                        /*
                        if (AppUtil.getIsLeftNavUnlocked(OConnectBaseActivity.this) == false
                                && selectedConference.isPasswordProtectSurvey() == true) {
                            AppUtil.displayNavPassword(OConnectBaseActivity.this, R.drawable.icon_survey);
                            return false;
                        }

                        i = new Intent(OConnectBaseActivity.this, SurveyActivity.class);
                        startActivity(i);


                        break;
                        */
                    case R.drawable.icon_external_link:
                        // Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedConference.getExternalLink()));
                        // startActivity(browserIntent);

                        i = new Intent(OConnectBaseActivity.this, WebViewActivity.class);

                        i.putExtra("TITLE", "");

                        i.putExtra("URL", selectedConference.getExternalLink());
                        i.putExtra("BACK_TEXT", "");
                        i.putExtra("OPEN", "");
                        i.putExtra("isSurvey", false);
                        startActivity(i);
                        break;
                    default:
                        break;
                }

                return false;
            }
        });

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        expListView.setIndicatorBounds(width - AppUtil.convertDPToPXInt(this,50), width - AppUtil.convertDPToPXInt(this, 10));

        expListView.expandGroup(0);
        expListView.expandGroup(1);
        expListView.expandGroup(2);

        expListView.setDividerHeight(0);

        navSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                navSearch.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null || newText.equalsIgnoreCase("")) {
                    navSearch.clearFocus();
                }
                performSearch(newText);
                return false;
            }
        });

        switchTwitter = (Switch) findViewById(R.id.switchTwitter);
        switchFacebook = (Switch) findViewById(R.id.switchFacebook);
        switchLinkedIn = (Switch) findViewById(R.id.switchLinkedIn);

        switchTwitter.setChecked(AppUtil.getTwitterLoggedIn(OConnectBaseActivity.this));
        switchFacebook.setChecked(AppUtil.getFacebookLoggedIn(OConnectBaseActivity.this));
        switchLinkedIn.setChecked(AppUtil.getLinkedInLoggedIn(OConnectBaseActivity.this));

        switchTwitter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppUtil.setTwitterLoggedIn(OConnectBaseActivity.this, isChecked);

                if (isChecked == true) {
                    ParseTwitterUtils.logIn(OConnectBaseActivity.this, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException err) {
                            if (user == null) {
                                Toast.makeText(OConnectBaseActivity.this,"Error during Twitter Login!",Toast.LENGTH_LONG).show();
                            } else {
                                if (user.isNew()) {
                                    user.put("userType", "app");
                                    user.saveInBackground();
                                }

                                AppUtil.setTwitterLoggedIn(OConnectBaseActivity.this, true);

                                OConnectBaseActivity.currentPerson = Person.saveFromParseUser(user, false);
                                callTwitterImportAPI(user);

                            }
                        }
                    });
                }
            }
        });

        switchFacebook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppUtil.setFacebookLoggedIn(OConnectBaseActivity.this, isChecked);

                if (isChecked == true) {
                    ParseFacebookUtils.logInWithReadPermissionsInBackground(OConnectBaseActivity.this, Arrays.asList("email", "user_photos", "public_profile", "user_friends")
                            , new LogInCallback() {
                                @Override
                                public void done(ParseUser user, ParseException err) {
                                    if (user == null) {
                                        Toast.makeText(OConnectBaseActivity.this,"Error during Facebook Login!",Toast.LENGTH_LONG).show();
                                    } else {
                                        if (user.isNew()) {
                                            user.put("userType", "app");
                                            user.saveInBackground();
                                        }

                                        AppUtil.setFacebookLoggedIn(OConnectBaseActivity.this, true);

                                        OConnectBaseActivity.currentPerson = Person.saveFromParseUser(user, false);
                                        callFacebookImportAPI(user);

                                    }
                                }

                            });
                }
            }
        });

        switchLinkedIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppUtil.setLinkedInLoggedIn(OConnectBaseActivity.this, isChecked);

                if (isChecked == true) {
                    final Activity thisActivity = OConnectBaseActivity.this;

                    LISessionManager.getInstance(getApplicationContext()).init(thisActivity, buildScope(), new AuthListener() {
                        @Override
                        public void onAuthSuccess() {
                            // Authentication was successful.  You can now do
                            // other calls with the SDK.
                            String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,email-address)";

                            APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                            apiHelper.getRequest(OConnectBaseActivity.this, url, new ApiListener() {
                                @Override
                                public void onApiSuccess(ApiResponse apiResponse) {
                                    AppUtil.setLinkedInLoggedIn(OConnectBaseActivity.this, true);

                                    // Success!
                                    Log.d("APD", "linkedin response for data: " + apiResponse.getResponseDataAsJson().toString());

                                    final JSONObject obj = apiResponse.getResponseDataAsJson();

                                    String firstName = "";
                                    String lastName = "";
                                    String emailAddress = "";

                                    try {
                                        if (obj.has("firstName")) {
                                            firstName = obj.getString("firstName");
                                        }

                                        if (obj.has("lastName")) {
                                            lastName = obj.getString("lastName");
                                        }

                                        if (obj.has("emailAddress")) {
                                            emailAddress = obj.getString("emailAddress");
                                        }
                                    } catch (Exception ex) {
                                        Log.d("APD", ex.getMessage());
                                    }

                                    final String finalEmailAddress = emailAddress;
                                    final String finalFirstName = firstName;
                                    final String finalLastName = lastName;

                                    final String token = LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().toString();

                                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                                    query.whereEqualTo("username", emailAddress);
                                    query.findInBackground(new FindCallback<ParseUser>() {
                                        public void done(List<ParseUser> objects, ParseException e) {
                                            if (e == null) {
                                                ParseUser parseUser = null;
                                                if (objects.size() > 0) {
                                                    final ParseUser user = objects.get(0);

                                                    user.setUsername(finalEmailAddress);
                                                    user.setEmail(finalEmailAddress);
                                                    user.put("firstName", finalFirstName);
                                                    user.put("lastName", finalLastName);

                                                    user.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            SignInActivity2 tempAct = new SignInActivity2();
                                                            SignInActivity2.LinkedInImportTask task = tempAct.new LinkedInImportTask();
                                                            task.execute(user.getObjectId(), token);
                                                        }
                                                    });

                                                    parseUser = user;
                                                } else {
                                                    final ParseUser user = new ParseUser();

                                                    user.setUsername(finalEmailAddress);
                                                    user.setEmail(finalEmailAddress);
                                                    user.put("firstName", finalFirstName);
                                                    user.put("lastName", finalLastName);

                                                    user.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            SignInActivity2 tempAct = new SignInActivity2();
                                                            SignInActivity2.LinkedInImportTask task = tempAct.new LinkedInImportTask();
                                                            task.execute(user.getObjectId(), token);
                                                        }
                                                    });

                                                    parseUser = user;
                                                }

                                                OConnectBaseActivity.currentPerson = Person.saveFromParseUser(parseUser, false);

                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onApiError(LIApiError liApiError) {
                                    // Error making GET request!
                                }
                            });
                        }

                        @Override
                        public void onAuthError(LIAuthError error) {
                            // Handle authentication errors
                        }
                    }, true);
                }
            }
        });

        ImageView ivTwitter = (ImageView) navigationViewRight.findViewById(R.id.ivTwitter);
        ivTwitter.setBackground(AppUtil.changeDrawableColor(this, R.drawable.twitter_icon, AppUtil.getPrimaryThemColorAsInt()));
        ImageView ivFacebook = (ImageView) navigationViewRight.findViewById(R.id.ivFacebook);
        ivFacebook.setBackground(AppUtil.changeDrawableColor(this, R.drawable.social_facebook, AppUtil.getPrimaryThemColorAsInt()));
        ImageView ivLinkedIn = (ImageView) navigationViewRight.findViewById(R.id.ivLinkedIn);
        ivLinkedIn.setBackground(AppUtil.changeDrawableColor(this, R.drawable.social_linkedin, AppUtil.getPrimaryThemColorAsInt()));
    }

    // Build the list of member permissions our LinkedIn session requires
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE);
    }

    public void performSearch(String searchText) {
        if (searchText == null || searchText.equalsIgnoreCase("")) {
            expListView.setVisibility(View.VISIBLE);
            expSearchView.setVisibility(GONE);
            return;
        }

        expListView.setVisibility(GONE);
        expSearchView.setVisibility(View.VISIBLE);

        searchText = searchText.toLowerCase();

        String[] searchArr = searchText.split(" ");

        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        RealmResults<Session> sessionResults;

        sessionResults = realm.where(Session.class).equalTo("conference", AppUtil.getSelectedConferenceID(OConnectBaseActivity.this)).findAllSorted("startTime", Sort.ASCENDING);

        RealmResults<Event> eventResults;
        RealmResults<RealmObject> personResults;

        ArrayList<RealmObject> eventsList = new ArrayList<RealmObject>();
        ArrayList<RealmObject> personsList = new ArrayList<RealmObject>();

        for (int i = 0; i < sessionResults.size(); ++i) {

            Session currentSession = sessionResults.get(i);

            eventResults = realm.where(Event.class).equalTo("session", currentSession.getObjectId()).findAllSorted("startTime", Sort.ASCENDING);

            for (int j = 0; j < eventResults.size(); ++j) {
                boolean bIncludeInResults = false;

                Event currentEvent = eventResults.get(j);

                RealmResults<SpeakerEventCache> speakerEventCache = realm.where(SpeakerEventCache.class).equalTo("eventID", currentEvent.getObjectId()).findAll();

                for (int l = 0; l < speakerEventCache.size(); ++l) {
                    SpeakerEventCache speakerEvent = speakerEventCache.get(l);

                    RealmResults<Speaker> speakers = realm.where(Speaker.class).equalTo("objectId", speakerEvent.getSpeakerID()).findAll();

                    for (int m = 0; m < speakers.size(); ++m) {
                        Speaker speaker = speakers.get(m);

                        for (String value : searchArr) {
                            if (speaker.getName().toLowerCase().contains(value)) {
                                bIncludeInResults = true;
                                break;
                            }
                        }
                    }
                }

                for (String value : searchArr) {
                    if ((currentEvent.getName() != null && currentEvent.getName().toLowerCase().contains(value)) ||
                            (currentEvent.getLocation() != null && currentEvent.getLocation().toLowerCase().contains(value))) {
                        bIncludeInResults = true;
                    }
                }

                if (bIncludeInResults == true) {
                    eventsList.add(currentEvent);
                }
            }
        }

        RealmResults<Speaker> speakerResults;

        speakerResults = realm.where(Speaker.class).equalTo("conference", AppUtil.getSelectedConferenceID(OConnectBaseActivity.this)).findAllSorted("name", Sort.ASCENDING);

        for (int i = 0; i < speakerResults.size(); ++i) {
            Speaker speaker = speakerResults.get(i);

            for (String value : searchArr) {
                if (speaker.getName().toLowerCase().contains(value)) {
                    personsList.add(speaker);
                    break;
                }
            }
        }

        RealmResults<Attendee> attendeeResults;

        attendeeResults = realm.where(Attendee.class).equalTo("conference", AppUtil.getSelectedConferenceID(OConnectBaseActivity.this)).findAllSorted("name", Sort.ASCENDING);

        for (int i = 0; i < attendeeResults.size(); ++i) {
            Attendee attendee = attendeeResults.get(i);

            for (String value : searchArr) {
                if (attendee.getName().toLowerCase().contains(value)) {
                    personsList.add(attendee);
                    break;
                }
            }
        }

        ArrayList<String> headers = new ArrayList<String>();
        headers.add("Event");
        headers.add("Attendee");
        HashMap<String, ArrayList<RealmObject>> map = new HashMap<String, ArrayList<RealmObject>>();

        map.put("Event", eventsList);
        map.put("Attendee", personsList);

        searchAdapter = new SearchExpandableListViewAdapter(OConnectBaseActivity.this, headers, map);

        expSearchView.setAdapter(searchAdapter);

        expSearchView.expandGroup(0);
        expSearchView.expandGroup(1);
    }

    public void removeFirstHeaderInNav() {
        if (listAdapter.section1Header != null) {
            ViewGroup.LayoutParams params = listAdapter.section1Header.getLayoutParams();
            params.height = 0;

            listAdapter.section1Header.setLayoutParams(params);

            listAdapter.section1Header.requestLayout();

            // expListView.removeHeaderView(listAdapter.section1Header);
            // expListView.deferNotifyDataSetChanged();
        }
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<MenuItemHolder>>();

        // Adding Header
        listDataHeader.add("");
        listDataHeader.add("Event");
        listDataHeader.add("Miscellaneous");

        // Adding child data
        List<MenuItemHolder> section1 = new ArrayList<MenuItemHolder>();

        if (selectedConference != null && selectedConference.isShowDashboard()) {
            section1.add(new MenuItemHolder(R.drawable.icon_dashboard, "Dashboard"));
        }

        section1.add(new MenuItemHolder(R.drawable.icon_conferences, "Conferences"));
        section1.add(new MenuItemHolder(R.drawable.icon_announcements, "Announcements"));


        List<MenuItemHolder> section2 = new ArrayList<MenuItemHolder>();


        if (selectedConference != null && selectedConference.isShowInfo()) {
            String infoStr = "Info";

            if (selectedConference.getToolbarLabelInfo() != null &&
                    !selectedConference.getToolbarLabelInfo().equalsIgnoreCase("")) {
                infoStr = selectedConference.getToolbarLabelInfo();
            }

            section2.add(new MenuItemHolder(R.drawable.icon_info, infoStr));
        }

        section2.add(new MenuItemHolder(R.drawable.icon_my_agenda, "My Agenda"));

        if (selectedConference != null && selectedConference.isShowSchedule()) {
            String itemStr = "Full Schedule";

            if (selectedConference.getToolbarLabelSchedule() != null &&
                    !selectedConference.getToolbarLabelSchedule().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelSchedule();
            }

            section2.add(new MenuItemHolder(R.drawable.icon_schedule, itemStr));
        }

        if (selectedConference != null && selectedConference.isShowParticipants()) {
            String itemStr = "Participants";

            if (selectedConference.getToolbarLabelParticipants() != null &&
                    !selectedConference.getToolbarLabelParticipants().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelParticipants();
            }

            section2.add(new MenuItemHolder(R.drawable.ic_person, itemStr));
        }

        if (selectedConference != null && selectedConference.isShowNonTimedEvents()) {
            String itemStr = "Non-Timed Event";

            if (selectedConference.getToolbarLabelNonTimedEvent() != null &&
                    !selectedConference.getToolbarLabelNonTimedEvent().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelNonTimedEvent();
            }

            section2.add(new MenuItemHolder(R.drawable.icon_non_timed_event_2, itemStr));
        }

        if (selectedConference != null && selectedConference.isShowSponsors()) {
            String itemStr = "Sponsors";

            if (selectedConference.getToolbarLabelSponsors() != null &&
                    !selectedConference.getToolbarLabelSponsors().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelSponsors();
            }

            section2.add(new MenuItemHolder(R.drawable.icon_sponsors, itemStr));
        }

        List<MenuItemHolder> section3 = new ArrayList<MenuItemHolder>();

        if (selectedConference != null && selectedConference.isShowRegistration()) {
            section3.add(new MenuItemHolder(R.drawable.icon_registration, "Eventbrite Register"));
        }

        section3.add(new MenuItemHolder(R.drawable.icon_my_notes, "My Notes"));

        if (selectedConference != null && selectedConference.isShowExternalLink()) {
            String itemStr = "External Link";

            if (selectedConference.getToolbarLabelExternalLink() != null &&
                    !selectedConference.getToolbarLabelExternalLink().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelExternalLink();
            }

            section3.add(new MenuItemHolder(R.drawable.icon_external_link, itemStr));
        }

        if (selectedConference != null && selectedConference.isShowMaps()) {
            String itemStr = "Maps";

            if (selectedConference.getToolbarLabelMaps() != null &&
                    !selectedConference.getToolbarLabelMaps().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelMaps();
            }

            section3.add(new MenuItemHolder(R.drawable.icon_maps, itemStr));
        }

        if (selectedConference != null && selectedConference.isShowSurvey()) {
            String itemStr = "Survey";

            if (selectedConference.getToolbarLabelSurvey() != null &&
                    !selectedConference.getToolbarLabelSurvey().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelSurvey();
            }

            section3.add(new MenuItemHolder(R.drawable.icon_survey, itemStr));
        }

        section2.add(new MenuItemHolder(R.drawable.icon_share, "Share"));

        if (selectedConference != null && selectedConference.isShowQRScanner()) {
            String itemStr = "QR Scanner";

            section3.add(new MenuItemHolder(R.drawable.icon_qr_scanner, itemStr));
        }

        section3.add(new MenuItemHolder(R.drawable.icon_about_us, "About Us"));
        section3.add(new MenuItemHolder(R.drawable.icon_refresh, "Refresh"));

        listDataChild.put(listDataHeader.get(0), section1); // Header, Child data
        listDataChild.put(listDataHeader.get(1), section2);
        listDataChild.put(listDataHeader.get(2), section3);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*
        getMenuInflater().inflate(R.menu.oconnect_base, menu);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_search); //Converting drawable into bitmap

        Bitmap bm = Bitmap.createScaledBitmap(
                icon, AppUtil.convertDPToPXInt(OConnectBaseActivity.this, 23), AppUtil.convertDPToPXInt(OConnectBaseActivity.this, 23), true);

        Drawable d = new BitmapDrawable(getResources(),bm);

        menu.getItem(0).setIcon(d);
        */

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_search) {
            SearchDialogFragment dialogFragment = new SearchDialogFragment ();
            dialogFragment.show(getSupportFragmentManager(), "Search Fragment");
        } else if (id == R.id.action_profile) {
            NavigationView navigationViewRight = (NavigationView) findViewById(R.id.nav_view_right);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            drawer.openDrawer(navigationViewRight, true);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void signOutClicked (View view) {
        bJustSignedOut = true;
        AppUtil.setIsSignedIn(OConnectBaseActivity.this, false);
        drawer.closeDrawer(navigationViewRight);

        if (AppUtil.getIsSignedIn(OConnectBaseActivity.this) == false) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, navigationViewRight);
        } else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, navigationViewRight);
        }
    }

    public void onDataSyncFinish() {
        if (DataSyncManager.dialog.isShowing() == true) {
            DataSyncManager.dialog.hide();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
        Date dateTimeNow = cal.getTime();

        DataSyncManager.setLastSyncDate(dateTimeNow);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            mSinchServiceInterface.addMessageClientListener(this);
            onServiceConnected();

        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }

    @Override
    public void onIncomingMessage(MessageClient client, Message message) {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        realm.beginTransaction();

        SinchMessage sinchMessage = realm.createObject(SinchMessage.class);

        sinchMessage.setMessageString(message.getTextBody());
        sinchMessage.setCurrentUserID(currentPerson.getUsername());
        sinchMessage.setConnectedUserID(message.getSenderId());
        sinchMessage.setMessageDateTime(message.getTimestamp());

        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void onMessageSent(MessageClient client, Message message, String recipientId) {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        realm.beginTransaction();

        SinchMessage sinchMessage = realm.createObject(SinchMessage.class);

        sinchMessage.setMessageString(message.getTextBody());
        sinchMessage.setCurrentUserID(message.getSenderId());
        sinchMessage.setConnectedUserID(recipientId);
        sinchMessage.setMessageDateTime(message.getTimestamp());

        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void onShouldSendPushData(MessageClient client, Message message, List<PushPair> pushPairs) {
        // Left blank intentionally
    }

    @Override
    public void onMessageFailed(MessageClient client, Message message,
                                MessageFailureInfo failureInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Sending failed: ")
                .append(failureInfo.getSinchError().getMessage());

        Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
        Log.d("OConnectBase", sb.toString());
    }

    @Override
    public void onMessageDelivered(MessageClient client, MessageDeliveryInfo deliveryInfo) {
        Log.d("OConnectBase", "onDelivered");
    }

    public void callTwitterImportAPI(ParseUser user) {
        SignInActivity2 tempAct = new SignInActivity2();
        SignInActivity2.TwitterImportTask task = tempAct.new TwitterImportTask();
        task.execute(user);
    }

    public void callFacebookImportAPI(ParseUser user) {
        SignInActivity2 tempAct = new SignInActivity2();
        SignInActivity2.FacebookImportTask task = tempAct.new FacebookImportTask();
        task.execute(user);
    }

    public int getUnreadMessagesCount(String userID) {
        Realm realm = AppUtil.getRealmInstance(this);

        RealmResults<SinchMessage> messages = realm.where(SinchMessage.class).equalTo("currentUserID", userID).equalTo("isRead", false).findAll();

        if (messages != null) {
            return messages.size();
        }

        return 0;
    }
}
