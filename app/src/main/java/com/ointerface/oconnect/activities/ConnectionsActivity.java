package com.ointerface.oconnect.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.azoft.carousellayoutmanager.CenterScrollListener;
import com.azoft.carousellayoutmanager.DefaultChildSelectionListener;
import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.data.Attendee;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.data.PredAnalyticsMatches;
import com.ointerface.oconnect.databinding.RolodexItemViewBinding;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.messaging.MessagingActivity;
import com.ointerface.oconnect.util.AppConfig;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

import static android.view.View.GONE;

public class ConnectionsActivity extends OConnectBaseActivity {

    private BottomNavigationView navigation;
    private RolodexAdapter adapter;
    private RecyclerView recyclerView;

    public Bitmap bmp;

    private TextView tvEditProfile;
    private TextView tvInterests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);
        super.onCreateDrawer();

        // final ActivityConnectionsCarouselPreviewBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_connections);

        getCarouselViewData();

        boolean circularRotation = true;

        if (adapter.mData.size() < 5) {
            circularRotation = false;
        }

        CarouselLayoutManager manager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, circularRotation);

        manager.setPostLayoutListener(new CarouselZoomPostLayoutListener());

        // initRecyclerView((RecyclerView) findViewById(R.id.carousel_horizontal), manager, adapter);

        recyclerView = (RecyclerView) findViewById(R.id.carousel_horizontal);

        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new CenterScrollListener());
        recyclerView.setClipChildren(false);

        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);

        tvToolbarTitle.setText("Connections");

        ivSearch.setVisibility(GONE);
        ivRightToolbarIcon.setVisibility(View.VISIBLE);
        ivConnections.setVisibility(View.VISIBLE);
        ivProfileLanyard.setVisibility(GONE);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] { android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed},  // pressed
                new int[] { android.R.attr.state_selected}  // selected
        };

        int[] colors = new int[] {
                Color.BLACK,
                Color.BLACK,
                Color.WHITE,
                Color.WHITE,
                Color.WHITE
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);

        navigation.setItemTextColor(colorStateList);

        View view1 = navigation.findViewById(R.id.navigation_my_connections);

        view1.setPadding(0,0,0,30);

        View view2 = navigation.findViewById(R.id.navigation_suggestions);

        view2.setPadding(0,0,0,30);

        navigation.setSelectedItemId(R.id.navigation_my_connections);

        // displayConnections();

        tvEditProfile = (TextView) findViewById(R.id.tvEditProfile);
        tvInterests = (TextView) findViewById(R.id.tvInterests);

        tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ConnectionsActivity.this, EditAccountActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up );
            }
        });

        tvInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ConnectionsActivity.this, AnalyticsSurveyActivity.class);
                startActivity(i);
            }
        });

        if (AppUtil.getAnalyticsSurveyFinished(this) == false) {
            tvInterests.setVisibility(View.VISIBLE);
        } else {
            tvInterests.setVisibility(GONE);
        }

        if (!AppUtil.getSurveyShown(this)) {
            CustomDialog dialog = new CustomDialog();
            dialog.showDialog(this);
        }
    }

    private void initRecyclerView(final RecyclerView recyclerView, final CarouselLayoutManager layoutManager, final RolodexAdapter adapter) {
        // enable zoom effect. this line can be customized
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
        layoutManager.setMaxVisibleItems(2);

        recyclerView.setLayoutManager(layoutManager);
        // we expect only fixed sized item for now
        recyclerView.setHasFixedSize(true);
        // sample adapter with random data
        recyclerView.setAdapter(adapter);
        // enable center post scrolling
        recyclerView.addOnScrollListener(new CenterScrollListener());
        // enable center post touching on item and item click listener
        DefaultChildSelectionListener.initCenterItemListener(new DefaultChildSelectionListener.OnCenterItemClickListener() {
            @Override
            public void onCenterItemClicked(@NonNull final RecyclerView recyclerView, @NonNull final CarouselLayoutManager carouselLayoutManager, @NonNull final View v) {
                /*
                final int position = recyclerView.getChildLayoutPosition(v);
                final String msg = String.format(Locale.US, "Item %1$d was clicked", position);
                Toast.makeText(CarouselPreviewActivity.this, msg, Toast.LENGTH_SHORT).show();
                */
            }
        }, recyclerView, layoutManager);

        layoutManager.addOnItemSelectionListener(new CarouselLayoutManager.OnCenterItemSelectionListener() {

            @Override
            public void onCenterItemChanged(final int adapterPosition) {
                if (CarouselLayoutManager.INVALID_POSITION != adapterPosition) {
                    // final int value = adapter.mPosition[adapterPosition];
/*
                    adapter.mPosition[adapterPosition] = (value % 10) + (value / 10 + 1) * 10;
                    adapter.notifyItemChanged(adapterPosition);
*/
                }
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Menu menu = navigation.getMenu();

            View connectionsView = navigation.findViewById(R.id.navigation_my_connections);

            if (connectionsView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) connectionsView.getLayoutParams();
                p.setMargins(0, 0, 0, 0);
                connectionsView.requestLayout();
            }

            View suggestionsView = navigation.findViewById(R.id.navigation_suggestions);

            if (suggestionsView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) suggestionsView.getLayoutParams();
                p.setMargins(0, 0, 0, 0);
                suggestionsView.requestLayout();
            }

            switch (item.getItemId()) {
                case R.id.navigation_my_connections:
                    connectionsView.setBackgroundColor(AppConfig.lightGreyColor);

                    suggestionsView.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());

                    displayConnections();
                    return true;
                case R.id.navigation_suggestions:
                    if (adapter.mDataSuggestedConnections.size() == 0) {
                        AppUtil.displayNoContactsAvailableDialog(ConnectionsActivity.this);
                    }else {
                        connectionsView.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());

                        suggestionsView.setBackgroundColor(AppConfig.lightGreyColor);

                        displaySuggestions();
                        return true;
                    }
            }
            return false;
        }

    };

    public void displayConnections() {
        adapter.showingMyConnections = true;
        adapter.notifyDataSetChanged();
    }

    public void displaySuggestions() {
        adapter.showingMyConnections = false;
        adapter.notifyDataSetChanged();
    }

    public void getCarouselViewData() {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        adapter = new RolodexAdapter(this);

        // RealmResults<Speaker> speakerResults;

        // speakerResults = realm.where(Speaker.class).equalTo("conference", AppUtil.getSelectedConferenceID(ConnectionsActivity.this)).findAllSorted("name", Sort.ASCENDING);

        /*
        for (int i = 0; i < speakerResults.size(); ++i) {
            adapter.addConnection(speakerResults.get(i));
        }
        */

        RealmList<Person> favoriteUsersList = OConnectBaseActivity.currentPerson.getFavoriteUsers();
        for (int i = 0; i < favoriteUsersList.size(); ++i) {
            adapter.addConnection(favoriteUsersList.get(i));
        }

        RealmList<Speaker> favoriteSpeakersList = OConnectBaseActivity.currentPerson.getFavoriteSpeakers();
        for (int i = 0; i < favoriteSpeakersList.size(); ++i) {
            adapter.addConnection(favoriteSpeakersList.get(i));
        }

        RealmList<Attendee> favoriteAttendeesList = OConnectBaseActivity.currentPerson.getFavoriteAttendees();
        for (int i = 0; i < favoriteAttendeesList.size(); ++i) {
            adapter.addConnection(favoriteAttendeesList.get(i));
        }


        RealmList<Person> suggestedConnections = OConnectBaseActivity.currentPerson.getSuggestedConnections();
        for (int i = 0; i < suggestedConnections.size(); ++i) {
            adapter.addSuggestedConnection(suggestedConnections.get(i));
        }
    }

    private static final class RolodexAdapter extends RecyclerView.Adapter<RolodexViewHolder> {
        public boolean showingMyConnections = true;
        public ArrayList<RealmObject> mData = null;
        public ArrayList<RealmObject> mDataSuggestedConnections = null;

        public Context context;
        RolodexAdapter adapter;

        RolodexAdapter(Context context) {
            adapter = this;
            this.context = context;
            mData = new ArrayList<RealmObject>();
            mDataSuggestedConnections = new ArrayList<RealmObject>();
        }

        public void addConnection(final RealmObject item) {
            mData.add(item);
        }

        public void addSuggestedConnection(final RealmObject item) {
            mDataSuggestedConnections.add(item);
        }

        @Override
        public RolodexViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            return new RolodexViewHolder(RolodexItemViewBinding.inflate(LayoutInflater.from(context), parent, false));
        }

        @Override
        public void onBindViewHolder(final RolodexViewHolder holder, final int position) {
            RealmObject currentObj = null;

            if (showingMyConnections == false) {
                currentObj = mDataSuggestedConnections.get(position);
            } else {
                currentObj = mData.get(position);
            }

            holder.mItemViewBinding.ivMessage.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_say_hi, AppUtil.getPrimaryThemColorAsInt()));

            holder.mItemViewBinding.tvDiscardContact.setVisibility(GONE);
            holder.mItemViewBinding.tvConnectionStrength.setVisibility(GONE);

            if (holder.mItemViewBinding.rlContainer.getParent() != null) {
                ViewParent view = holder.mItemViewBinding.rlContainer.getParent();

                ViewGroup viewGroup = (ViewGroup) view;

                viewGroup.setClipChildren(false);
            }

            holder.mItemViewBinding.rlContainer.setClipChildren(false);
            holder.mItemViewBinding.rlContent.setClipChildren(false);

            // holder.mItemViewBinding.rlProfilePicture.setClipChildren(false);

            holder.mItemViewBinding.rlProfilePicture.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());

            holder.mItemViewBinding.ivParticipantJobTitle.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_participants_info, AppUtil.getPrimaryThemColorAsInt()));
            holder.mItemViewBinding.ivParticipantOrg.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_paricipants_suitcase, AppUtil.getPrimaryThemColorAsInt()));
            holder.mItemViewBinding.ivParticipantInterests.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_participants_light_bulb, AppUtil.getPrimaryThemColorAsInt()));
            holder.mItemViewBinding.ivParticipantLocation.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_participants_house, AppUtil.getPrimaryThemColorAsInt()));

            holder.mItemViewBinding.ivMessage.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_say_hi, AppUtil.getPrimaryThemColorAsInt()));

            holder.mItemViewBinding.tvMessage.setTextColor(AppUtil.getPrimaryThemColorAsInt());

            if (currentObj instanceof Speaker) {
                Speaker currentSpeaker = (Speaker) currentObj;

                if (currentSpeaker.getImage() != null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(currentSpeaker.getImage(), 0, currentSpeaker.getImage().length);

                    // Drawable d = new BitmapDrawable(context.getResources(), bmp);

                    holder.mItemViewBinding.ivParticipantPicture.setImageBitmap(bmp);

                    holder.mItemViewBinding.ivParticipantPicture.bringToFront();
                    // ivPicture.setBackground(d);
                }

                holder.mItemViewBinding.tvParticipantName.setText(currentSpeaker.getName());

                holder.mItemViewBinding.tvMessage.setVisibility(GONE);
                holder.mItemViewBinding.ivMessage.setVisibility(GONE);

                if (currentSpeaker.getJob() != null && !currentSpeaker.getJob().equalsIgnoreCase("")) {
                    holder.mItemViewBinding.tvParticipantJobTitle.setText(currentSpeaker.getJob());
                    holder.mItemViewBinding.tvParticipantJobTitle.setVisibility(View.VISIBLE);
                    holder.mItemViewBinding.ivParticipantJobTitle.setVisibility(View.VISIBLE);
                } else {
                    holder.mItemViewBinding.tvParticipantJobTitle.setVisibility(GONE);
                    holder.mItemViewBinding.ivParticipantJobTitle.setVisibility(GONE);
                }

                if (currentSpeaker.getOrganization() != null && !currentSpeaker.getOrganization().equalsIgnoreCase("")) {
                    holder.mItemViewBinding.tvParticipantOrg.setText(currentSpeaker.getOrganization());
                    holder.mItemViewBinding.tvParticipantOrg.setVisibility(View.VISIBLE);
                    holder.mItemViewBinding.ivParticipantOrg.setVisibility(View.VISIBLE);
                } else {
                    holder.mItemViewBinding.tvParticipantOrg.setVisibility(GONE);
                    holder.mItemViewBinding.ivParticipantOrg.setVisibility(GONE);
                }

                // TODO SET INTERESTS
                holder.mItemViewBinding.tvParticipantInterests.setVisibility(GONE);
                holder.mItemViewBinding.ivParticipantInterests.setVisibility(GONE);

                if (currentSpeaker.getLocation() != null && !currentSpeaker.getLocation().equalsIgnoreCase("")) {
                    holder.mItemViewBinding.tvParticipantLocation.setText(currentSpeaker.getLocation());
                    holder.mItemViewBinding.tvParticipantLocation.setVisibility(View.VISIBLE);
                    holder.mItemViewBinding.ivParticipantLocation.setVisibility(View.VISIBLE);
                } else {
                    holder.mItemViewBinding.tvParticipantLocation.setVisibility(GONE);
                    holder.mItemViewBinding.ivParticipantLocation.setVisibility(GONE);
                }

            } else if (currentObj instanceof Attendee) {
                Attendee currentAttendee = (Attendee) currentObj;

                if (currentAttendee.getImage() != null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(currentAttendee.getImage(), 0, currentAttendee.getImage().length);

                    // Drawable d = new BitmapDrawable(context.getResources(), bmp);

                    holder.mItemViewBinding.ivParticipantPicture.setImageBitmap(bmp);

                    holder.mItemViewBinding.ivParticipantPicture.bringToFront();
                    // ivPicture.setBackground(d);
                }

                holder.mItemViewBinding.tvParticipantName.setText(currentAttendee.getName());

                holder.mItemViewBinding.tvMessage.setVisibility(GONE);
                holder.mItemViewBinding.ivMessage.setVisibility(GONE);

                if (currentAttendee.getJob() != null && !currentAttendee.getJob().equalsIgnoreCase("")) {
                    holder.mItemViewBinding.tvParticipantJobTitle.setText(currentAttendee.getJob());
                    holder.mItemViewBinding.tvParticipantJobTitle.setVisibility(View.VISIBLE);
                    holder.mItemViewBinding.ivParticipantJobTitle.setVisibility(View.VISIBLE);
                } else {
                    holder.mItemViewBinding.tvParticipantJobTitle.setVisibility(GONE);
                    holder.mItemViewBinding.ivParticipantJobTitle.setVisibility(GONE);
                }

                if (currentAttendee.getOrganization() != null && !currentAttendee.getOrganization().equalsIgnoreCase("")) {
                    holder.mItemViewBinding.tvParticipantOrg.setText(currentAttendee.getOrganization());
                    holder.mItemViewBinding.tvParticipantOrg.setVisibility(View.VISIBLE);
                    holder.mItemViewBinding.ivParticipantOrg.setVisibility(View.VISIBLE);
                } else {
                    holder.mItemViewBinding.tvParticipantOrg.setVisibility(GONE);
                    holder.mItemViewBinding.ivParticipantOrg.setVisibility(GONE);
                }

                // TODO SET INTERESTS
                holder.mItemViewBinding.tvParticipantInterests.setVisibility(GONE);
                holder.mItemViewBinding.ivParticipantInterests.setVisibility(GONE);

                if (currentAttendee.getLocation() != null && !currentAttendee.getLocation().equalsIgnoreCase("")) {
                    holder.mItemViewBinding.tvParticipantLocation.setText(currentAttendee.getLocation());
                    holder.mItemViewBinding.tvParticipantLocation.setVisibility(View.VISIBLE);
                    holder.mItemViewBinding.ivParticipantLocation.setVisibility(View.VISIBLE);
                } else {
                    holder.mItemViewBinding.tvParticipantLocation.setVisibility(GONE);
                    holder.mItemViewBinding.ivParticipantLocation.setVisibility(GONE);
                }
            } else if (currentObj instanceof Person) {
                final Person currentPerson = (Person) currentObj;

                try {
                    if (currentPerson.getPictureURL() != null
                            && !currentPerson.getPictureURL().equalsIgnoreCase("")) {

                        final String pictureURL = currentPerson.getPictureURL();

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
                                if (bmp != null) {
                                    holder.mItemViewBinding.ivParticipantPicture.setImageBitmap(bmp);
                                }
                            }

                        }.execute();
                    }
                } catch (Exception ex) {
                    if (ex.getMessage() != null) {
                        Log.d("APD", ex.getMessage());
                    }
                }

                holder.mItemViewBinding.tvParticipantName.setText(currentPerson.getFirstName() + " " + currentPerson.getLastName());

                holder.mItemViewBinding.tvMessage.setVisibility(View.VISIBLE);
                holder.mItemViewBinding.ivMessage.setVisibility(View.VISIBLE);

                if (showingMyConnections == true) {

                    holder.mItemViewBinding.btnClose.setVisibility(GONE);

                    holder.mItemViewBinding.tvMessage.setText("Message");

                    holder.mItemViewBinding.tvMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, MessagingActivity.class);

                            MessagingActivity.recipientIDStr = currentPerson.getObjectId();

                            context.startActivity(intent);
                        }
                    });

                    holder.mItemViewBinding.ivMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, MessagingActivity.class);

                            MessagingActivity.recipientIDStr = currentPerson.getObjectId();

                            context.startActivity(intent);
                        }
                    });
                } else {

                    holder.mItemViewBinding.btnClose.setVisibility(View.VISIBLE);

                    final Realm realm = AppUtil.getRealmInstance(context);
                    final PredAnalyticsMatches matchObj = realm.where(PredAnalyticsMatches.class).equalTo("id1", OConnectBaseActivity.currentPerson.getObjectId()).equalTo("id2", currentPerson.getObjectId()).findFirst();

                    holder.mItemViewBinding.ivMessage.setBackground(AppUtil.changeDrawableColor(context, R.drawable.ic_add_profile, AppUtil.getPrimaryThemColorAsInt()));

                    holder.mItemViewBinding.tvMessage.setText("Add Contact");
                    holder.mItemViewBinding.tvDiscardContact.setVisibility(View.GONE);
                    holder.mItemViewBinding.tvDiscardContact.setText("Discard Contact");
                    holder.mItemViewBinding.tvConnectionStrength.setVisibility(View.VISIBLE);

                    if (matchObj != null) {
                        String connectionStrength = "Your Connection Strength: " + getConnectionStrengthString(matchObj.getScore());

                        holder.mItemViewBinding.tvConnectionStrength.setText(connectionStrength);
                    }


                    holder.mItemViewBinding.tvMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (matchObj == null) {
                                return;
                            }
                            OConnectBaseActivity.currentPerson.getFavoriteUsers().add(currentPerson);

                            realm.beginTransaction();
                            matchObj.setAccepted(true);
                            realm.commitTransaction();

                            mDataSuggestedConnections.remove(currentPerson);

                            try {
                                ParseObject parseMatchObj = ParseQuery.getQuery("PredAnalyticsMatches").whereEqualTo("objectId", matchObj.getObjectId()).getFirst();

                                parseMatchObj.put("isAccepted", true);

                                parseMatchObj.save();

                                adapter.notifyDataSetChanged();
                            } catch (Exception ex) {
                                Log.d("APD", ex.getMessage());
                            }
                        }
                    });

                    holder.mItemViewBinding.btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (matchObj == null) {
                                return;
                            }

                            if (OConnectBaseActivity.currentPerson.getFavoriteUsers().contains(currentPerson)) {
                                OConnectBaseActivity.currentPerson.getFavoriteUsers().remove(currentPerson);
                            }

                            realm.beginTransaction();
                            matchObj.setRejected(true);
                            realm.commitTransaction();

                            mDataSuggestedConnections.remove(currentPerson);

                            try {
                                ParseObject parseMatchObj = ParseQuery.getQuery("PredAnalyticsMatches").whereEqualTo("objectId", matchObj.getObjectId()).getFirst();

                                parseMatchObj.put("isRejected", true);

                                parseMatchObj.save();

                                adapter.notifyDataSetChanged();
                            } catch (Exception ex) {
                                Log.d("APD", ex.getMessage());
                            }
                        }
                    });

                    holder.mItemViewBinding.tvDiscardContact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (matchObj == null) {
                                return;
                            }

                            if (OConnectBaseActivity.currentPerson.getFavoriteUsers().contains(currentPerson)) {
                                OConnectBaseActivity.currentPerson.getFavoriteUsers().remove(currentPerson);
                            }

                            realm.beginTransaction();
                            matchObj.setRejected(true);
                            realm.commitTransaction();

                            mDataSuggestedConnections.remove(currentPerson);

                            try {
                                ParseObject parseMatchObj = ParseQuery.getQuery("PredAnalyticsMatches").whereEqualTo("objectId", matchObj.getObjectId()).getFirst();

                                parseMatchObj.put("isRejected", true);

                                parseMatchObj.save();

                                adapter.notifyDataSetChanged();
                            } catch (Exception ex) {
                                Log.d("APD", ex.getMessage());
                            }
                        }
                    });

                }

                if (currentPerson.getJob() != null && !currentPerson.getJob().equalsIgnoreCase("")) {
                    holder.mItemViewBinding.tvParticipantJobTitle.setText(currentPerson.getJob());
                    holder.mItemViewBinding.tvParticipantJobTitle.setVisibility(View.VISIBLE);
                    holder.mItemViewBinding.ivParticipantJobTitle.setVisibility(View.VISIBLE);
                } else {
                    holder.mItemViewBinding.tvParticipantJobTitle.setVisibility(GONE);
                    holder.mItemViewBinding.ivParticipantJobTitle.setVisibility(GONE);
                }

                if (currentPerson.getOrg() != null && !currentPerson.getOrg().equalsIgnoreCase("")) {
                    holder.mItemViewBinding.tvParticipantOrg.setText(currentPerson.getOrg());
                    holder.mItemViewBinding.tvParticipantOrg.setVisibility(View.VISIBLE);
                    holder.mItemViewBinding.ivParticipantOrg.setVisibility(View.VISIBLE);
                } else {
                    holder.mItemViewBinding.tvParticipantOrg.setVisibility(GONE);
                    holder.mItemViewBinding.ivParticipantOrg.setVisibility(GONE);
                }

                // TODO SET INTERESTS
                holder.mItemViewBinding.tvParticipantInterests.setVisibility(GONE);
                holder.mItemViewBinding.ivParticipantInterests.setVisibility(GONE);

                if (currentPerson.getLocation() != null && !currentPerson.getLocation().equalsIgnoreCase("")) {
                    holder.mItemViewBinding.tvParticipantLocation.setText(currentPerson.getLocation());
                    holder.mItemViewBinding.tvParticipantLocation.setVisibility(View.VISIBLE);
                    holder.mItemViewBinding.ivParticipantLocation.setVisibility(View.VISIBLE);
                } else {
                    holder.mItemViewBinding.tvParticipantLocation.setVisibility(GONE);
                    holder.mItemViewBinding.ivParticipantLocation.setVisibility(GONE);
                }
            }

        }

        @Override
        public int getItemCount() {
            if (showingMyConnections == false) {
                return mDataSuggestedConnections.size();
            }
            return mData.size();
        }
    }

    private static class RolodexViewHolder extends RecyclerView.ViewHolder {

        private final RolodexItemViewBinding mItemViewBinding;

        RolodexViewHolder(final RolodexItemViewBinding itemViewBinding) {
            super(itemViewBinding.getRoot());

            mItemViewBinding = itemViewBinding;
        }
    }

    public static String getConnectionStrengthString(double score) {
        if (score >= 1 && score < 2) {
            return "50%";
        } else if (score >= 2 && score < 3) {
            return "60%";
        } else if (score >= 3 && score < 4) {
            return "70%";
        } else if (score >= 4 && score < 5) {
            return "80%";
        } else if (score >= 5) {
            return "90%";
        }

        return "50%";
    }
}
