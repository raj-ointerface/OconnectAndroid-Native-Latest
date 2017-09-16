package com.ointerface.oconnect.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.activities.ParticipantsActivity;
import com.ointerface.oconnect.data.Attendee;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeSet;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import retrofit2.http.Url;

import static android.view.View.GONE;

/**
 * Created by AnthonyDoan on 4/25/17.
 */

public class ParticipantsSwipeListAdapter extends BaseSwipeAdapter {
    public Context context;

    public ParticipantsActivity activity;

    private LayoutInflater mInflater;

    public boolean showingSpeakers = true;

    public ArrayList<Speaker> mSpeakers = new ArrayList<Speaker>();

    // public ArrayList<Attendee> mAttendees = new ArrayList<Attendee>();

    // public ArrayList<Person> mPeople = new ArrayList<Person>();

    public ArrayList<RealmObject> mUsers = new ArrayList<RealmObject>();

    public TreeSet<Integer> hiddenPositionsByUser = new TreeSet<Integer>();
    public TreeSet<Integer> connectedPositionsByUser = new TreeSet<Integer>();
    public TreeSet<Integer> connectedPositionsAttendeeByUser = new TreeSet<Integer>();

    public ParticipantsSwipeListAdapter(Context context, ParticipantsActivity activity) {
        super();
        this.context = context;
        this.activity = activity;
        this.mSpeakers = new ArrayList<Speaker>();

        // this.mAttendees = new ArrayList<Attendee>();
        // this.mPeople = new ArrayList<Person>();

        this.mUsers = new ArrayList<RealmObject>();

        hiddenPositionsByUser = new TreeSet<Integer>();

        mInflater = LayoutInflater.from(context);
    }

    public void addSpeaker(final Speaker item) {
        mSpeakers.add(item);
    }

    /*
    public void addAttendee(final Attendee item) {
        mAttendees.add(item);
    }

    public void addPerson(final Person item) {
        mPeople.add(item);
    }
    */

    public void addUser(final RealmObject item) {
        mUsers.add(item);
    }

    @Override
    public int getCount() {
        if (showingSpeakers == true) {
            return mSpeakers.size();
        }

        // return mPeople.size() + mAttendees.size();

        return mUsers.size();
    }

    @Override
    public String getItem(int position) {
        if (showingSpeakers == true) {
            return mSpeakers.get(position).getName();
        }

        /*
        if (position >= mPeople.size()) {
            return mAttendees.get(position - mPeople.size()).getName();
        }
        */

        RealmObject user = mUsers.get(position);

        Attendee attendee = null;
        Person person = null;

        if (user instanceof Attendee) {
            attendee = (Attendee) user;
        } else if (user instanceof  Person) {
            person = (Person) user;
        }

        if (attendee != null) {
            return attendee.getName();
        } else if (person != null) {
            return person.getFirstName() + person.getLastName();
        }

        return "";
    }

    @Override
    public long getItemId(int position) {
        if (showingSpeakers == false) {
            return position;
        }
        return position;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipeMain;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.participant_list_view_item, null);
        SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));

        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, swipeLayout.findViewById(R.id.rlLeft));
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.rlRight));

        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                // YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });

        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(context, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        if (showingSpeakers == true) {
            final Speaker speaker = mSpeakers.get(position);

            ImageView ivInfo = (ImageView) convertView.findViewById(R.id.ivParticipantJobTitle);
            ImageView ivSuitcase = (ImageView) convertView.findViewById(R.id.ivParticipantOrg);
            ImageView ivLightBuld = (ImageView) convertView.findViewById(R.id.ivParticipantInterests);
            ImageView ivHouse = (ImageView) convertView.findViewById(R.id.ivParticipantLocation);
            ImageView ivPicture = (ImageView) convertView.findViewById(R.id.ivParticipantPicture);

            RelativeLayout rlContainer = (RelativeLayout) convertView.findViewById(R.id.rlContainer);
            RelativeLayout rlContent = (RelativeLayout) convertView.findViewById(R.id.rlContent);

            rlContainer.setClipChildren(false);
            rlContent.setClipChildren(false);

            ivInfo.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_participants_info, AppUtil.getPrimaryThemColorAsInt()));
            ivSuitcase.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_paricipants_suitcase, AppUtil.getPrimaryThemColorAsInt()));
            ivLightBuld.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_participants_light_bulb, AppUtil.getPrimaryThemColorAsInt()));
            ivHouse.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_participants_house, AppUtil.getPrimaryThemColorAsInt()));

            ivPicture.setImageResource(R.drawable.icon_silhouette);

            if (speaker.getImage() != null) {
                // Bitmap bmp = BitmapFactory.decodeByteArray(speaker.getImage(), 0, speaker.getImage().length);

                Bitmap bitmap = decodeSampledBitmapFromByteArray(speaker.getImage(), AppUtil.convertDPToPXInt(context, 80), AppUtil.convertDPToPXInt(context, 80));
                // Drawable d = new BitmapDrawable(context.getResources(), bmp);

                ivPicture.setImageBitmap(bitmap);

                // ivPicture.setBackground(d);
            } else {
                ivPicture.setImageResource(R.drawable.icon_silhouette);
            }

            TextView tvName = (TextView) convertView.findViewById(R.id.tvParticipantName);
            TextView tvJobTitle = (TextView) convertView.findViewById(R.id.tvParticipantJobTitle);
            TextView tvOrg = (TextView) convertView.findViewById(R.id.tvParticipantOrg);
            TextView tvInterests = (TextView) convertView.findViewById(R.id.tvParticipantInterests);
            TextView tvLocation = (TextView) convertView.findViewById(R.id.tvParticipantLocation);

            final TextView tvCheckIn = (TextView) convertView.findViewById(R.id.tvCheckIn);
            tvCheckIn.setVisibility(GONE);

            tvName.setText(speaker.getName());

            if (speaker.getJob() != null && !speaker.getJob().equalsIgnoreCase("")) {
                tvJobTitle.setText(speaker.getJob());
                tvJobTitle.setVisibility(View.VISIBLE);
                ivInfo.setVisibility(View.VISIBLE);
            } else {
                tvJobTitle.setVisibility(GONE);
                ivInfo.setVisibility(GONE);
            }

            if (speaker.getOrganization() != null && !speaker.getOrganization().equalsIgnoreCase("")) {
                tvOrg.setText(speaker.getOrganization());
                tvOrg.setVisibility(View.VISIBLE);
                ivSuitcase.setVisibility(View.VISIBLE);
            } else {
                tvOrg.setVisibility(GONE);
                ivSuitcase.setVisibility(GONE);
            }

            // TODO SET INTERESTS
            tvInterests.setVisibility(GONE);
            ivLightBuld.setVisibility(GONE);

            if (speaker.getLocation() != null && !speaker.getLocation().equalsIgnoreCase("")) {
                tvLocation.setText(speaker.getLocation());
                tvLocation.setVisibility(View.VISIBLE);
                ivHouse.setVisibility(View.VISIBLE);
            } else {
                tvLocation.setVisibility(GONE);
                ivHouse.setVisibility(GONE);
            }

            final int finalPosition = position;
            final SwipeLayout swipeMain = (SwipeLayout) convertView.findViewById(R.id.swipeMain);
            final TextView tvConnect = (TextView) convertView.findViewById(R.id.tvConnect);
            TextView tvHide = (TextView) convertView.findViewById(R.id.tvHide);

            tvHide.setTextColor(AppUtil.getPrimaryThemColorAsInt());

            tvHide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hiddenPositionsByUser.contains(finalPosition)) {
                        hiddenPositionsByUser.remove(finalPosition);
                    } else {
                        hiddenPositionsByUser.add(finalPosition);
                    }

                    swipeMain.close();
                    notifyDataSetChanged();
                }
            });

            tvConnect.setTextColor(AppUtil.getPrimaryThemColorAsInt());

            tvConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Realm realm = AppUtil.getRealmInstance(App.getInstance());
                    realm.beginTransaction();
                    if (connectedPositionsByUser.contains(finalPosition)) {
                        connectedPositionsByUser.remove(finalPosition);
                        OConnectBaseActivity.currentPerson.getFavoriteSpeakers().remove(speaker);

                        try {
                            if (OConnectBaseActivity.currentPerson != null) {
                                ParseUser user = ParseUser.getQuery().get(OConnectBaseActivity.currentPerson.getObjectId());
                                ParseRelation<ParseObject> speakersRelation = user.getRelation("favoriteSpeakerRelation");

                                ParseObject speakerObj = ParseQuery.getQuery("Speaker").get(speaker.getObjectId());

                                if (speakerObj != null) {
                                    speakersRelation.remove(speakerObj);
                                }

                                user.save();
                            }
                        } catch (Exception ex) {
                            Log.d("APD", ex.getMessage());
                        }

                        Toast.makeText(context, "Speaker removed from My Connections ...", Toast.LENGTH_LONG).show();
                    } else {
                        connectedPositionsByUser.add(finalPosition);
                        OConnectBaseActivity.currentPerson.getFavoriteSpeakers().add(speaker);

                        try {
                            if (OConnectBaseActivity.currentPerson != null) {
                                ParseUser user = ParseUser.getQuery().get(OConnectBaseActivity.currentPerson.getObjectId());
                                ParseRelation<ParseObject> speakersRelation = user.getRelation("favoriteSpeakerRelation");

                                ParseObject speakerObj = ParseQuery.getQuery("Speaker").get(speaker.getObjectId());

                                if (speakerObj != null) {
                                    speakersRelation.add(speakerObj);
                                }

                                user.save();
                            }
                        } catch (Exception ex) {
                            Log.d("APD", ex.getMessage());
                        }

                        Toast.makeText(context, "Speaker added to My Connections ...", Toast.LENGTH_LONG).show();
                    }
                    realm.commitTransaction();
                    realm.close();

                    swipeMain.close();
                    notifyDataSetChanged();
                }
            });

            if (connectedPositionsByUser.contains(position)) {
                tvConnect.setText("Unconnect");
            } else {
                tvConnect.setText("Connect");
            }

            if (hiddenPositionsByUser.contains(position)) {
                tvHide.setText("Unhide");
                rlContent.setBackgroundResource(R.drawable.layout_hidden_background_rounded);
                rlContent.getBackground().setAlpha(80);
            } else {
                tvHide.setText("Hide");
                rlContent.setBackgroundResource(R.drawable.layout_background_rounded);
            }
        } else {

            boolean bIsAttendee = false;

            RealmObject user = mUsers.get(position);

            Attendee thisAttendee = null;
            Person thisPerson = null;

            if (user instanceof Attendee) {
                thisAttendee = (Attendee) user;
            } else if (user instanceof  Person) {
                thisPerson = (Person) user;
            }


            if (thisAttendee != null) {
                bIsAttendee = true;
            }

            if (bIsAttendee == true) {
                final Attendee attendee = (Attendee) mUsers.get(position);

                ImageView ivInfo = (ImageView) convertView.findViewById(R.id.ivParticipantJobTitle);
                ImageView ivSuitcase = (ImageView) convertView.findViewById(R.id.ivParticipantOrg);
                ImageView ivLightBuld = (ImageView) convertView.findViewById(R.id.ivParticipantInterests);
                ImageView ivHouse = (ImageView) convertView.findViewById(R.id.ivParticipantLocation);
                ImageView ivPicture = (ImageView) convertView.findViewById(R.id.ivParticipantPicture);

                RelativeLayout rlContainer = (RelativeLayout) convertView.findViewById(R.id.rlContainer);
                RelativeLayout rlContent = (RelativeLayout) convertView.findViewById(R.id.rlContent);

                rlContainer.setClipChildren(false);
                rlContent.setClipChildren(false);

                ivInfo.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_participants_info, AppUtil.getPrimaryThemColorAsInt()));
                ivSuitcase.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_paricipants_suitcase, AppUtil.getPrimaryThemColorAsInt()));
                ivLightBuld.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_participants_light_bulb, AppUtil.getPrimaryThemColorAsInt()));
                ivHouse.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_participants_house, AppUtil.getPrimaryThemColorAsInt()));

                ivPicture.setImageBitmap(null);
                ivPicture.setBackgroundResource(R.drawable.icon_silhouette);

                if (attendee.getImage() != null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(attendee.getImage(), 0, attendee.getImage().length);
                    Drawable d = new BitmapDrawable(context.getResources(), bmp);
                    ivPicture.setBackground(d);
                }  else {
                    ivPicture.setBackgroundResource(R.drawable.icon_silhouette);
                }

                TextView tvName = (TextView) convertView.findViewById(R.id.tvParticipantName);
                TextView tvJobTitle = (TextView) convertView.findViewById(R.id.tvParticipantJobTitle);
                TextView tvOrg = (TextView) convertView.findViewById(R.id.tvParticipantOrg);
                TextView tvInterests = (TextView) convertView.findViewById(R.id.tvParticipantInterests);
                TextView tvLocation = (TextView) convertView.findViewById(R.id.tvParticipantLocation);

                tvName.setText(attendee.getName());

                if (attendee.getJob() != null && !attendee.getJob().equalsIgnoreCase("")) {
                    tvJobTitle.setText(attendee.getJob());
                    tvJobTitle.setVisibility(View.VISIBLE);
                } else {
                    tvJobTitle.setVisibility(GONE);
                    ivInfo.setVisibility(GONE);
                }

                if (attendee.getOrganization() != null && !attendee.getOrganization().equalsIgnoreCase("")) {
                    tvOrg.setText(attendee.getOrganization());
                    tvOrg.setVisibility(View.VISIBLE);
                } else {
                    tvOrg.setVisibility(GONE);
                    ivSuitcase.setVisibility(GONE);
                }

                // TODO SET INTERESTS
                tvInterests.setVisibility(GONE);
                ivLightBuld.setVisibility(GONE);

                if (attendee.getLocation() != null && !attendee.getLocation().equalsIgnoreCase("")) {
                    tvLocation.setText(attendee.getLocation());
                    tvLocation.setVisibility(View.VISIBLE);
                } else {
                    tvLocation.setVisibility(GONE);
                    ivHouse.setVisibility(GONE);
                }

                final int finalPosition = position;
                final SwipeLayout swipeMain = (SwipeLayout) convertView.findViewById(R.id.swipeMain);
                TextView tvConnect = (TextView) convertView.findViewById(R.id.tvConnect);
                TextView tvHide = (TextView) convertView.findViewById(R.id.tvHide);
                final TextView tvCheckIn = (TextView) convertView.findViewById(R.id.tvCheckIn);
                tvCheckIn.setVisibility(GONE);

                if (attendee.getCheckedIn() == true) {
                    tvCheckIn.setVisibility(View.VISIBLE);
                    tvCheckIn.setText("Checked In");
                }

                if (activity.currentSpeaker != null &&
                        activity.currentSpeaker.isAllowCheckIn() == true) {
                    tvCheckIn.setVisibility(View.VISIBLE);

                    tvCheckIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Realm realm = AppUtil.getRealmInstance(App.getInstance());
                            realm.beginTransaction();
                            attendee.setCheckedIn(true);
                            realm.commitTransaction();

                            try {
                                ParseObject parseAttendee = ParseQuery.getQuery("Attendee").get(attendee.getObjectId());
                                if (parseAttendee != null) {
                                    parseAttendee.put("isCheckedIn", true);
                                    parseAttendee.save();
                                }
                            } catch (Exception ex) {
                                Log.d("Participants", ex.getMessage());
                            }

                            tvCheckIn.setText("Checked In");
                        }
                    });
                }

                tvHide.setTextColor(AppUtil.getPrimaryThemColorAsInt());

                tvHide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hiddenPositionsByUser.contains(finalPosition)) {
                            hiddenPositionsByUser.remove(finalPosition);
                        } else {
                            hiddenPositionsByUser.add(finalPosition);
                        }

                        swipeMain.close();
                        notifyDataSetChanged();
                    }
                });

                tvConnect.setTextColor(AppUtil.getPrimaryThemColorAsInt());

                tvConnect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Realm realm = AppUtil.getRealmInstance(App.getInstance());
                        realm.beginTransaction();
                        if (connectedPositionsAttendeeByUser.contains(finalPosition)) {
                            connectedPositionsAttendeeByUser.remove(finalPosition);
                            OConnectBaseActivity.currentPerson.getFavoriteAttendees().remove(attendee);

                            try {
                                if (OConnectBaseActivity.currentPerson != null) {
                                    ParseUser user = ParseUser.getQuery().get(OConnectBaseActivity.currentPerson.getObjectId());
                                    ParseRelation<ParseObject> attendeesRelation = user.getRelation("favoriteAttendeesRelation");

                                    ParseObject attendeeObj = ParseQuery.getQuery("Attendee").get(attendee.getObjectId());

                                    if (attendeeObj != null) {
                                        attendeesRelation.remove(attendeeObj);
                                    }

                                    user.save();
                                }
                            } catch (Exception ex) {
                                Log.d("APD", ex.getMessage());
                            }

                            Toast.makeText(context, "Attendee removed from My Connections ...", Toast.LENGTH_LONG).show();
                        } else {
                            connectedPositionsAttendeeByUser.add(finalPosition);
                            OConnectBaseActivity.currentPerson.getFavoriteAttendees().add(attendee);

                            try {
                                if (OConnectBaseActivity.currentPerson != null) {
                                    ParseUser user = ParseUser.getQuery().get(OConnectBaseActivity.currentPerson.getObjectId());
                                    ParseRelation<ParseObject> attendeesRelation = user.getRelation("favoriteAttendeesRelation");

                                    ParseObject attendeeObj = ParseQuery.getQuery("Attendee").get(attendee.getObjectId());

                                    if (attendeeObj != null) {
                                        attendeesRelation.add(attendeeObj);
                                    }

                                    user.save();
                                }
                            } catch (Exception ex) {
                                Log.d("APD", ex.getMessage());
                            }

                            Toast.makeText(context, "Attendee added to My Connections ...", Toast.LENGTH_LONG).show();
                        }
                        realm.commitTransaction();
                        realm.close();

                        swipeMain.close();
                        notifyDataSetChanged();
                    }
                });

                if (connectedPositionsAttendeeByUser.contains(position)) {
                    tvConnect.setText("Unconnect");
                } else {
                    tvConnect.setText("Connect");
                }

                if (hiddenPositionsByUser.contains(position)) {
                    tvHide.setText("Unhide");
                    rlContent.setBackgroundResource(R.drawable.layout_hidden_background_rounded);
                    rlContent.getBackground().setAlpha(80);
                } else {
                    tvHide.setText("Hide");
                    rlContent.setBackgroundResource(R.drawable.layout_background_rounded);
                }
            } else {
                final Person person = (Person) mUsers.get(position);

                ImageView ivInfo = (ImageView) convertView.findViewById(R.id.ivParticipantJobTitle);
                ImageView ivSuitcase = (ImageView) convertView.findViewById(R.id.ivParticipantOrg);
                ImageView ivLightBuld = (ImageView) convertView.findViewById(R.id.ivParticipantInterests);
                ImageView ivHouse = (ImageView) convertView.findViewById(R.id.ivParticipantLocation);
                final ImageView ivPicture = (ImageView) convertView.findViewById(R.id.ivParticipantPicture);

                RelativeLayout rlContainer = (RelativeLayout) convertView.findViewById(R.id.rlContainer);
                RelativeLayout rlContent = (RelativeLayout) convertView.findViewById(R.id.rlContent);

                rlContainer.setClipChildren(false);
                rlContent.setClipChildren(false);

                ivInfo.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_participants_info, AppUtil.getPrimaryThemColorAsInt()));
                ivSuitcase.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_paricipants_suitcase, AppUtil.getPrimaryThemColorAsInt()));
                ivLightBuld.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_participants_light_bulb, AppUtil.getPrimaryThemColorAsInt()));
                ivHouse.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_participants_house, AppUtil.getPrimaryThemColorAsInt()));

                ivPicture.setImageBitmap(null);
                ivPicture.setBackgroundResource(R.drawable.icon_silhouette);

                if (person.getPictureURL() != null) {

                    final String pictureURL = person.getPictureURL();

                    Log.d("APD", "currentPerson.getPictureURL(): " + person.getPictureURL());

                    Picasso.with(context).load(pictureURL).into(ivPicture);

                    // ivPicture.setTag(pictureURL);

                    // new DownloadTask().execute(pictureURL, ivPicture, context);
                }

                TextView tvName = (TextView) convertView.findViewById(R.id.tvParticipantName);
                TextView tvJobTitle = (TextView) convertView.findViewById(R.id.tvParticipantJobTitle);
                TextView tvOrg = (TextView) convertView.findViewById(R.id.tvParticipantOrg);
                TextView tvInterests = (TextView) convertView.findViewById(R.id.tvParticipantInterests);
                TextView tvLocation = (TextView) convertView.findViewById(R.id.tvParticipantLocation);

                tvName.setText(person.getFirstName() + " " + person.getLastName());

                if (person.getJob() != null && !person.getJob().equalsIgnoreCase("")) {
                    tvJobTitle.setText(person.getJob());
                    tvJobTitle.setVisibility(View.VISIBLE);
                } else {
                    tvJobTitle.setVisibility(GONE);
                    ivInfo.setVisibility(GONE);
                }

                if (person.getOrg() != null && !person.getOrg().equalsIgnoreCase("")) {
                    tvOrg.setText(person.getOrg());
                    tvOrg.setVisibility(View.VISIBLE);
                } else {
                    tvOrg.setVisibility(GONE);
                    ivSuitcase.setVisibility(GONE);
                }

                // TODO SET INTERESTS
                tvInterests.setVisibility(GONE);
                ivLightBuld.setVisibility(GONE);

                if (person.getLocation() != null && !person.getLocation().equalsIgnoreCase("")) {
                    tvLocation.setText(person.getLocation());
                    tvLocation.setVisibility(View.VISIBLE);
                } else {
                    tvLocation.setVisibility(GONE);
                    ivHouse.setVisibility(GONE);
                }

                final int finalPosition = position;
                final SwipeLayout swipeMain = (SwipeLayout) convertView.findViewById(R.id.swipeMain);
                TextView tvConnect = (TextView) convertView.findViewById(R.id.tvConnect);
                TextView tvHide = (TextView) convertView.findViewById(R.id.tvHide);
                final TextView tvCheckIn = (TextView) convertView.findViewById(R.id.tvCheckIn);
                tvCheckIn.setVisibility(GONE);

                tvHide.setTextColor(AppUtil.getPrimaryThemColorAsInt());

                tvHide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hiddenPositionsByUser.contains(finalPosition)) {
                            hiddenPositionsByUser.remove(finalPosition);
                        } else {
                            hiddenPositionsByUser.add(finalPosition);
                        }

                        swipeMain.close();
                        notifyDataSetChanged();
                    }
                });

                tvConnect.setTextColor(AppUtil.getPrimaryThemColorAsInt());

                tvConnect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Realm realm = AppUtil.getRealmInstance(App.getInstance());
                        realm.beginTransaction();
                        if (connectedPositionsAttendeeByUser.contains(finalPosition)) {
                            connectedPositionsAttendeeByUser.remove(finalPosition);
                            OConnectBaseActivity.currentPerson.getFavoriteUsers().remove(person);

                            try {
                                if (OConnectBaseActivity.currentPerson != null) {
                                    ParseUser user = ParseUser.getQuery().get(OConnectBaseActivity.currentPerson.getObjectId());
                                    ParseRelation<ParseObject> usersRelation = user.getRelation("favoriteUsersRelation");

                                    ParseObject personObj = ParseQuery.getQuery("User").get(person.getObjectId());

                                    if (personObj != null) {
                                        usersRelation.remove(personObj);
                                    }

                                    user.save();
                                }
                            } catch (Exception ex) {
                                Log.d("APD", ex.getMessage());
                            }

                            Toast.makeText(context, "Attendee removed from My Connections ...", Toast.LENGTH_LONG).show();
                        } else {
                            connectedPositionsAttendeeByUser.add(finalPosition);
                            OConnectBaseActivity.currentPerson.getFavoriteUsers().add(person);

                            try {
                                if (OConnectBaseActivity.currentPerson != null) {
                                    ParseUser user = ParseUser.getQuery().get(OConnectBaseActivity.currentPerson.getObjectId());
                                    ParseRelation<ParseObject> usersRelation = user.getRelation("favoriteUsersRelation");

                                    ParseObject userObj = ParseQuery.getQuery("User").get(person.getObjectId());

                                    if (userObj != null) {
                                        usersRelation.add(userObj);
                                    }

                                    user.save();
                                }
                            } catch (Exception ex) {
                                Log.d("APD", ex.getMessage());
                            }

                            Toast.makeText(context, "Attendee added to My Connections ...", Toast.LENGTH_LONG).show();
                        }
                        realm.commitTransaction();
                        realm.close();

                        swipeMain.close();
                        notifyDataSetChanged();
                    }
                });

                if (connectedPositionsAttendeeByUser.contains(position)) {
                    tvConnect.setText("Unconnect");
                } else {
                    tvConnect.setText("Connect");
                }

                if (hiddenPositionsByUser.contains(position)) {
                    tvHide.setText("Unhide");
                    rlContent.setBackgroundResource(R.drawable.layout_hidden_background_rounded);
                    rlContent.getBackground().setAlpha(80);
                } else {
                    tvHide.setText("Hide");
                    rlContent.setBackgroundResource(R.drawable.layout_background_rounded);
                }
            }
        }

    }

    public static Bitmap decodeSampledBitmapFromByteArray(byte[] data,
                                                          int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;

            }

            return inSampleSize;
        }
        return inSampleSize;
    }


    public class DownloadTask extends AsyncTask<Object, Void, Void> {
        public Bitmap bmp;
        public ImageView ivPicture;
        public String pictureUrl;
        public Context context;

        @Override
        protected Void doInBackground(Object... params) {
            try {
                pictureUrl = (String) params[0];
                ivPicture = (ImageView) params[1];
                context = (Context) params[2];

                InputStream in = new URL(pictureUrl).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.d("APD", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("APD", "pictureURL BITMAP: " + bmp);

            if (bmp != null && ivPicture.getTag().toString().equalsIgnoreCase(pictureUrl)) {
                Drawable d = new BitmapDrawable(context.getResources(), bmp);

                ivPicture.setImageDrawable(d);

            }
        }
    }
}