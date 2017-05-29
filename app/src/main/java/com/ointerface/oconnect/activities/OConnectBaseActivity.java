package com.ointerface.oconnect.activities;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.ConferenceListViewActivity;
import com.ointerface.oconnect.CustomSplashActivity;
import com.ointerface.oconnect.MainSplashActivity;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.adapters.NavExpandableListViewAdapter;
import com.ointerface.oconnect.containers.MenuItemHolder;
import com.ointerface.oconnect.data.Conference;
import com.ointerface.oconnect.data.DataSyncManager;
import com.ointerface.oconnect.data.IDataSyncListener;
import com.ointerface.oconnect.data.MasterNotification;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.fragments.SearchDialogFragment;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParseUser;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static android.view.View.GONE;


public class OConnectBaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IDataSyncListener {

    NavExpandableListViewAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<MenuItemHolder>> listDataChild;

    SearchView navSearch;

    static public Conference selectedConference;
    static public Person currentPerson;

    public ImageView ivProfileLanyard;
    public ImageView ivSearch;
    public ImageView ivHelp;
    public ImageView ivRightToolbarIcon;
    public TextView tvToolbarTitle;
    public TextView tvEdit;
    public ImageView ivHeaderBack;
    public TextView tvHeaderBack;

    public NavigationView navigationView;
    public NavigationView navigationViewRight;
    public DrawerLayout drawer;

    public boolean isTransparentToolbar = false;
    public boolean bJustSignedOut = false;

    public TextView tvName;
    public CircleImageView ivProfilePicture;
    public Switch switchContactable;
    public TextView tvRightNavHeader;

    public Bitmap bmp;
    public Button btnConnections;
    public TextView tvSignOut;
    public ImageView ivSignOut;
    public ImageView ivAccountEdit;

    protected void onCreateDrawer() {
        // super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_oconnect_base);
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        selectedConference = realm.where(Conference.class).equalTo("objectId", AppUtil.getSelectedConferenceID(OConnectBaseActivity.this)).findFirst();

        if (AppUtil.getIsSignedIn(OConnectBaseActivity.this) == true) {
            currentPerson = realm.where(Person.class).equalTo("objectId", AppUtil.getSignedInUserID(OConnectBaseActivity.this)).findFirst();
        }

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

        ivRightToolbarIcon = (ImageView) toolbar.findViewById(R.id.ivRightToolbarPerson);

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
        ivSignOut = (ImageView) navigationViewRight.findViewById(R.id.ivSignOut);
        tvSignOut = (TextView) navigationViewRight.findViewById(R.id.tvSignOut);
        ivAccountEdit = (ImageView) navigationViewRight.findViewById(R.id.ivAccountEdit);

        tvRightNavHeader.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());
        btnConnections.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());
        ivSignOut.setBackground(AppUtil.changeDrawableColor(OConnectBaseActivity.this,R.drawable.icon_sign_out, AppUtil.getPrimaryThemColorAsInt()));
        tvSignOut.setTextColor(AppUtil.getPrimaryThemColorAsInt());

        btnConnections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AppUtil.displayNotImplementedDialog(OConnectBaseActivity.this);

                Intent i = new Intent(OConnectBaseActivity.this, ConnectionsActivity.class);
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
            tvName.setText(currentPerson.getFirstName() + " " + currentPerson.getLastName());

            try {
                if (currentPerson.getPictureURL() != null
                        && !currentPerson.getPictureURL().equalsIgnoreCase("")) {

                    final String pictureURL = currentPerson.getPictureURL();

                    new AsyncTask<Void, Void, Void>() {
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
                            if (bmp != null) {
                                ivProfilePicture.setImageBitmap(bmp);
                            }
                        }

                    }.execute();
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
                        AppUtil.displayNotImplementedDialog(OConnectBaseActivity.this);

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
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedConference.getExternalLink()));
                        startActivity(browserIntent);
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
        section1.add(new MenuItemHolder(R.drawable.icon_announcements, "Announcements"));
        section1.add(new MenuItemHolder(R.drawable.icon_conferences, "Conferences"));

        List<MenuItemHolder> section2 = new ArrayList<MenuItemHolder>();

        if (selectedConference != null && selectedConference.isShowDashboard()) {
            section2.add(new MenuItemHolder(R.drawable.icon_dashboard, "Dashboard"));
        }

        if (selectedConference != null && selectedConference.isShowRegistration()) {
            section2.add(new MenuItemHolder(R.drawable.icon_registration, "Registration"));
        }

        if (selectedConference != null && selectedConference.isShowInfo()) {
            String infoStr = "Info";

            if (selectedConference.getToolbarLabelInfo() != null &&
                    !selectedConference.getToolbarLabelInfo().equalsIgnoreCase("")) {
                infoStr = selectedConference.getToolbarLabelInfo();
            }

            section2.add(new MenuItemHolder(R.drawable.icon_info, infoStr));
        }

        if (selectedConference != null && selectedConference.isShowSchedule()) {
            String itemStr = "Schedule";

            if (selectedConference.getToolbarLabelSchedule() != null &&
                    !selectedConference.getToolbarLabelSchedule().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelSchedule();
            }

            section2.add(new MenuItemHolder(R.drawable.icon_schedule, itemStr));
        }

        if (selectedConference != null && selectedConference.isShowNonTimedEvents()) {
            String itemStr = "Non-Timed Event";

            if (selectedConference.getToolbarLabelNonTimedEvent() != null &&
                    !selectedConference.getToolbarLabelNonTimedEvent().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelNonTimedEvent();
            }

            section2.add(new MenuItemHolder(R.drawable.icon_non_timed_event_2, itemStr));
        }

        if (selectedConference != null && selectedConference.isShowParticipants()) {
            String itemStr = "Participants";

            if (selectedConference.getToolbarLabelParticipants() != null &&
                    !selectedConference.getToolbarLabelParticipants().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelParticipants();
            }

            section2.add(new MenuItemHolder(R.drawable.ic_person, itemStr));
        }

        if (selectedConference != null && selectedConference.isShowMaps()) {
            String itemStr = "Maps";

            if (selectedConference.getToolbarLabelMaps() != null &&
                    !selectedConference.getToolbarLabelMaps().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelMaps();
            }

            section2.add(new MenuItemHolder(R.drawable.icon_maps, itemStr));
        }

        section2.add(new MenuItemHolder(R.drawable.icon_my_agenda, "My Agenda"));
        section2.add(new MenuItemHolder(R.drawable.icon_my_notes, "My Notes"));

        if (selectedConference != null && selectedConference.isShowSponsors()) {
            String itemStr = "Sponsors";

            if (selectedConference.getToolbarLabelSponsors() != null &&
                    !selectedConference.getToolbarLabelSponsors().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelSponsors();
            }

            section2.add(new MenuItemHolder(R.drawable.icon_sponsors, itemStr));
        }

        section2.add(new MenuItemHolder(R.drawable.icon_share, "Share"));

        List<MenuItemHolder> section3 = new ArrayList<MenuItemHolder>();

        if (selectedConference != null && selectedConference.isShowQRScanner()) {
            String itemStr = "QR Scanner";

            section3.add(new MenuItemHolder(R.drawable.icon_qr_scanner, itemStr));
        }

        if (selectedConference != null && selectedConference.isShowSurvey()) {
            String itemStr = "Survey";

            if (selectedConference.getToolbarLabelSurvey() != null &&
                    !selectedConference.getToolbarLabelSurvey().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelSurvey();
            }

            section3.add(new MenuItemHolder(R.drawable.icon_survey, itemStr));
        }

        if (selectedConference != null && selectedConference.isShowExternalLink()) {
            String itemStr = "External Link";

            if (selectedConference.getToolbarLabelExternalLink() != null &&
                    !selectedConference.getToolbarLabelExternalLink().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelExternalLink();
            }

            section3.add(new MenuItemHolder(R.drawable.icon_external_link, itemStr));
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

        Date dateTimeNow = Calendar.getInstance().getTime();
        DataSyncManager.setLastSyncDate(dateTimeNow);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
