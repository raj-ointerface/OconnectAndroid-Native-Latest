package com.ointerface.oconnect.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
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
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.ConferenceListViewActivity;
import com.ointerface.oconnect.CustomSplashActivity;
import com.ointerface.oconnect.MainSplashActivity;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.adapters.NavExpandableListViewAdapter;
import com.ointerface.oconnect.containers.MenuItemHolder;
import com.ointerface.oconnect.data.Conference;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.fragments.SearchDialogFragment;
import com.ointerface.oconnect.util.AppUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;

import static android.view.View.GONE;


public class OConnectBaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

    public NavigationView navigationView;
    public NavigationView navigationViewRight;
    public DrawerLayout drawer;

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

        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);

        ivProfileLanyard = (ImageView) toolbar.findViewById(R.id.ivProfileLanyard);

        ivProfileLanyard.setVisibility(GONE);

        ivHelp = (ImageView) toolbar.findViewById(R.id.ivQuestion);

        ivHelp.setVisibility(GONE);

        ivRightToolbarIcon = (ImageView) toolbar.findViewById(R.id.ivRightToolbarPerson);

        ivRightToolbarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        /*  Use following code to change icon to different colors
        int color = Color.parseColor("#3CB371");

        Drawable wrappedDrawable = DrawableCompat.wrap(ivSearch.getBackground());
        DrawableCompat.setTint(wrappedDrawable, color);

        ivSearch.setBackground(wrappedDrawable);
        */

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationViewRight = (NavigationView) findViewById(R.id.nav_view_right);
        navigationViewRight.setNavigationItemSelectedListener(this);

        // get the listview
        // View headerView = navigationView.getHeaderView(0);
        expListView = (ExpandableListView) navigationView.findViewById(R.id.elvLeftNavItems);

        navSearch = (SearchView) navigationView.findViewById(R.id.search);

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
                        i = new Intent(OConnectBaseActivity.this, ConferenceListViewActivity.class);
                        startActivity(i);
                        break;
                    case R.drawable.icon_dashboard:
                        i = new Intent(OConnectBaseActivity.this, DashboardActivity.class);
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

        if (selectedConference.isShowDashboard()) {
            section2.add(new MenuItemHolder(R.drawable.icon_dashboard, "Dashboard"));
        }

        if (selectedConference.isShowRegistration()) {
            section2.add(new MenuItemHolder(R.drawable.icon_registration, "Registration"));
        }

        if (selectedConference.isShowInfo()) {
            String infoStr = "Info";

            if (selectedConference.getToolbarLabelInfo() != null &&
                    !selectedConference.getToolbarLabelInfo().equalsIgnoreCase("")) {
                infoStr = selectedConference.getToolbarLabelInfo();
            }

            section2.add(new MenuItemHolder(R.drawable.icon_info, infoStr));
        }

        if (selectedConference.isShowSchedule()) {
            String itemStr = "Schedule";

            if (selectedConference.getToolbarLabelSchedule() != null &&
                    !selectedConference.getToolbarLabelSchedule().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelSchedule();
            }

            section2.add(new MenuItemHolder(R.drawable.icon_schedule, itemStr));
        }

        if (selectedConference.isShowNonTimedEvents()) {
            String itemStr = "Non-Timed Event";

            if (selectedConference.getToolbarLabelNonTimedEvent() != null &&
                    !selectedConference.getToolbarLabelNonTimedEvent().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelNonTimedEvent();
            }

            section2.add(new MenuItemHolder(R.drawable.icon_non_timed_event, itemStr));
        }

        if (selectedConference.isShowParticipants()) {
            String itemStr = "Participants";

            if (selectedConference.getToolbarLabelParticipants() != null &&
                    !selectedConference.getToolbarLabelParticipants().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelParticipants();
            }

            section2.add(new MenuItemHolder(R.drawable.ic_person, itemStr));
        }

        if (selectedConference.isShowMaps()) {
            String itemStr = "Maps";

            if (selectedConference.getToolbarLabelMaps() != null &&
                    !selectedConference.getToolbarLabelMaps().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelMaps();
            }

            section2.add(new MenuItemHolder(R.drawable.icon_maps, itemStr));
        }

        section2.add(new MenuItemHolder(R.drawable.icon_my_agenda, "My Agenda"));
        section2.add(new MenuItemHolder(R.drawable.icon_my_notes, "My Notes"));

        if (selectedConference.isShowSponsors()) {
            String itemStr = "Sponsors";

            if (selectedConference.getToolbarLabelSponsors() != null &&
                    !selectedConference.getToolbarLabelSponsors().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelSponsors();
            }

            section2.add(new MenuItemHolder(R.drawable.icon_sponsors, itemStr));
        }

        section2.add(new MenuItemHolder(R.drawable.icon_share, "Share"));

        List<MenuItemHolder> section3 = new ArrayList<MenuItemHolder>();

        if (selectedConference.isShowQRScanner()) {
            String itemStr = "QR Scanner";

            section3.add(new MenuItemHolder(R.drawable.icon_qr_scanner, itemStr));
        }

        if (selectedConference.isShowSurvey()) {
            String itemStr = "Survey";

            if (selectedConference.getToolbarLabelSurvey() != null &&
                    !selectedConference.getToolbarLabelSurvey().equalsIgnoreCase("")) {
                itemStr = selectedConference.getToolbarLabelSurvey();
            }

            section3.add(new MenuItemHolder(R.drawable.icon_survey, itemStr));
        }

        if (selectedConference.isShowExternalLink()) {
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
        AppUtil.setIsSignedIn(OConnectBaseActivity.this, false);
        drawer.closeDrawer(navigationViewRight);
    }
}
