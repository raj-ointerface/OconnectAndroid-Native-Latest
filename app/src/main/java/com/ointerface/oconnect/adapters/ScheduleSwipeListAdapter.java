package com.ointerface.oconnect.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.ointerface.oconnect.App;
import com.ointerface.oconnect.ConferenceListViewActivity;
import com.ointerface.oconnect.ConferenceListViewAdapter;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.activities.ScheduleActivity;
import com.ointerface.oconnect.data.Conference;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.Session;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.util.AppConfig;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

import static android.view.View.GONE;

/**
 * Created by AnthonyDoan on 4/24/17.
 */

public class ScheduleSwipeListAdapter extends BaseSwipeAdapter {
    public Context context;
    public Conference[] conferences;

    private LayoutInflater mInflater;

    // public ArrayList<Event> mData = new ArrayList<Event>();
    // private List<String> _listDataHeader;
    // public TreeSet<Integer> sectionHeader = new TreeSet<Integer>();
    // public HashMap<String, List<Event>> _listDataChild;
    // public List<Session> _listSessionHeader;

    public ArrayList<RealmObject> mData = new ArrayList<RealmObject>();
    public TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    public ArrayList<Boolean> mIsExpandedArray = new ArrayList<Boolean>();
    public TreeSet<Integer> hiddenPositions = new TreeSet<Integer>();

    public TreeSet<Integer> hiddenPositionsByUser = new TreeSet<Integer>();
    public TreeSet<Integer> myEventsPositionsByUser = new TreeSet<Integer>();


    public static final int TYPE_ITEM = 0;
    public static final int TYPE_SEPARATOR = 1;

    public ScheduleSwipeListAdapter(Context context) {
        super();
        this.context = context;

        mData = new ArrayList<RealmObject>();
        sectionHeader = new TreeSet<Integer>();
        mIsExpandedArray = new ArrayList<Boolean>();
        hiddenPositions = new TreeSet<Integer>();
        hiddenPositionsByUser = new TreeSet<Integer>();

        mInflater = LayoutInflater.from(context);
    }

    public void addItem(final Event item) {
        mData.add(item);
    }

    public void addSectionHeaderItem(final Session item) {
        mData.add(item);
        sectionHeader.add(mData.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return "";
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipeMain;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        /*
        if (hiddenPositions.contains(position) && getItemViewType(position) != TYPE_SEPARATOR) {
            return mInflater.inflate(R.layout.null_item, null);
        }
        */
        if (getItemViewType(position) == TYPE_SEPARATOR) {
            return mInflater.inflate(R.layout.schedule_group_list_header, null);
        } else {
            return mInflater.inflate(R.layout.schedule_group_list_child, null);
        }
    }

    @Override
    public void fillValues(final int position, final View convertView) {
        /*
        if (hiddenPositions.contains(position) && getItemViewType(position) != TYPE_SEPARATOR) {
            return;
        }
        */

        if (getItemViewType(position) == TYPE_SEPARATOR) {
            try {
                convertView.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());

                Session session = (Session) mData.get(position);

                if (session.getColor() != null && !session.getColor().equalsIgnoreCase("")) {
                    convertView.setBackgroundColor(Color.parseColor(session.getColor()));
                }

                TextView tvSessionName = (TextView) convertView
                        .findViewById(R.id.tvSessionName);
                tvSessionName.setTypeface(null, Typeface.BOLD);
                tvSessionName.setText(session.getTrack());

                TextView tvModerator = (TextView) convertView.findViewById(R.id.tvModeratorName);

                if (session.getModerator() != null && !session.getModerator().equalsIgnoreCase("")) {
                    tvModerator.setText(session.getModerator());
                    tvModerator.setVisibility(View.VISIBLE);
                } else {
                    tvModerator.setVisibility(GONE);
                }

                TextView tvTimeRange = (TextView) convertView.findViewById(R.id.tvTimeRange);

                DateFormat dfTime = new SimpleDateFormat("h:mm a");
                dfTime.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));

                String startTime = dfTime.format(session.getStartTime());
                String endTime = dfTime.format(session.getEndTime());

                if (ScheduleActivity.isEventType == true) {
                    SimpleDateFormat dfEventDate = new SimpleDateFormat("EEEE, MMMM d");

                    tvTimeRange.setText(dfEventDate.format(session.getStartTime()));
                } else {
                    tvTimeRange.setText(startTime + " - " + endTime);
                }

                TextView tvLocation = (TextView) convertView.findViewById(R.id.tvLocation);

                if (session.getLocation() != null && !session.getLocation().equalsIgnoreCase("")) {
                    tvLocation.setText(session.getLocation());
                    tvLocation.setVisibility(View.VISIBLE);
                } else {
                    tvLocation.setVisibility(GONE);
                }

                TextView tvArrow = (TextView) convertView.findViewById(R.id.tvArrow);

                tvArrow.setText(">");

                if (mIsExpandedArray.get(position) == true) {
                    tvArrow.setRotation(90);
                } else {
                    tvArrow.setRotation(0);
                }
            } catch (Exception ex) {
                Log.d("APD", ex.getMessage());
            }
        } else {
            try {

                final Event event = (Event) mData.get(position);

                TextView tvTimeRange = (TextView) convertView
                        .findViewById(R.id.tvTimeRange);

                // tvTimeRange.setText(childItem.ge);
                SimpleDateFormat dfTime = new SimpleDateFormat("h:mm a");

                dfTime.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));

                String startTime = dfTime.format(event.getStartTime());
                String endTime = dfTime.format(event.getEndTime());

                tvTimeRange.setText(startTime + " - " + endTime);

                TextView eventName = (TextView) convertView.findViewById(R.id.tvEventName);

                eventName.setText(event.getName());

                TextView tvLocation = (TextView) convertView.findViewById(R.id.tvLocation);

                if (event.getLocation() != null && !event.getLocation().equalsIgnoreCase("")) {
                    tvLocation.setText(event.getLocation());
                    tvLocation.setVisibility(View.VISIBLE);
                } else {
                    tvLocation.setVisibility(GONE);
                }

                TextView tvSpeaker = (TextView) convertView.findViewById(R.id.tvSpeakerName);

                RealmList<Speaker> foundSpeakers = event.getSpeakers();

                if (foundSpeakers.size() > 1) {
                    tvSpeaker.setText("Multiple Speakers");
                    tvSpeaker.setVisibility(View.VISIBLE);
                } else if (foundSpeakers.size() == 0) {
                    tvSpeaker.setVisibility(GONE);
                } else {
                    tvSpeaker.setText(foundSpeakers.get(0).getName());
                    tvSpeaker.setVisibility(View.VISIBLE);
                }

                RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.rlSessionEvent);
                final SwipeLayout swipeMain = (SwipeLayout) convertView.findViewById(R.id.swipeMain);
                RelativeLayout rlContent = (RelativeLayout) convertView.findViewById(R.id.rlContent);
                RelativeLayout rlLeft = (RelativeLayout) convertView.findViewById(R.id.rlLeft);
                RelativeLayout rlRight = (RelativeLayout) convertView.findViewById(R.id.rlRight);
                TextView tvRemove = (TextView) convertView.findViewById(R.id.tvRemove);
                TextView tvHide = (TextView) convertView.findViewById(R.id.tvHide);
                ImageView ivStar = (ImageView) convertView.findViewById(R.id.ivStar);

                relativeLayout.setBackgroundColor(AppConfig.customGrayColor);

                if (event.getTrackColor() != null && !event.getTrackColor().equalsIgnoreCase("")) {
                    relativeLayout.setBackgroundColor(Color.parseColor(event.getTrackColor()));
                }

                tvHide.setTextColor(AppUtil.getPrimaryThemColorAsInt());

                tvHide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hiddenPositionsByUser.contains(position)) {
                            hiddenPositionsByUser.remove(position);
                        } else {
                            hiddenPositionsByUser.add(position);
                        }

                        swipeMain.close();
                        notifyDataSetChanged();
                    }
                });

                tvRemove.setTextColor(AppUtil.getPrimaryThemColorAsInt());

                tvRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (AppUtil.getIsSignedIn(context) == false) {
                            AppUtil.displayPleaseSignInDialog(context);
                            return;
                        }

                        Realm realm = AppUtil.getRealmInstance(App.getInstance());

                        if (myEventsPositionsByUser.contains(position)) {
                            myEventsPositionsByUser.remove(position);

                            ScheduleActivity activity = (ScheduleActivity) context;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    swipeMain.close();

                                    notifyDataSetChanged();

                                    Toast.makeText(context, "Event removed from My Agenda ...", Toast.LENGTH_LONG).show();
                                }
                            });

                            if (OConnectBaseActivity.currentPerson != null) {
                                realm.beginTransaction();
                                OConnectBaseActivity.currentPerson.getFavoriteEvents().remove(event);
                                realm.commitTransaction();
                            }

                            /*
                            try {
                                if (OConnectBaseActivity.currentPerson != null) {
                                    ParseUser user = ParseUser.getQuery().get(OConnectBaseActivity.currentPerson.getObjectId());
                                    ParseRelation<ParseObject> eventsRelation = user.getRelation("favoriteEventsRelation");

                                    ParseObject eventObj = ParseQuery.getQuery("Event").get(event.getObjectId());

                                    if (eventObj != null) {
                                        eventsRelation.remove(eventObj);
                                    }

                                    // user.put("favoriteEventsRelation", eventsRelation);

                                    user.save();
                                }
                            } catch (Exception ex) {
                                Log.d("APD", ex.getMessage());
                            }
                            */
                        } else {
                            myEventsPositionsByUser.add(position);

                            ScheduleActivity activity = (ScheduleActivity) context;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    swipeMain.close();

                                    notifyDataSetChanged();

                                    Toast.makeText(context, "Event added to My Agenda ...", Toast.LENGTH_LONG).show();
                                }
                            });


                            if (OConnectBaseActivity.currentPerson != null) {
                                realm.beginTransaction();
                                OConnectBaseActivity.currentPerson.getFavoriteEvents().add(event);
                                realm.commitTransaction();
                            }

                            /*
                            try {
                                if (OConnectBaseActivity.currentPerson != null) {
                                    ParseUser user = ParseUser.getQuery().get(OConnectBaseActivity.currentPerson.getObjectId());
                                    ParseRelation<ParseObject> eventsRelation = user.getRelation("favoriteEventsRelation");

                                    ParseObject eventObj = ParseQuery.getQuery("Event").get(event.getObjectId());

                                    if (eventObj != null) {
                                        eventsRelation.add(eventObj);
                                    }

                                    // user.put("favoriteEventsRelation", eventsRelation);

                                    user.save();
                                }
                            } catch (Exception ex) {
                                Log.d("APD", ex.getMessage());
                            }
                            */
                        }

                        swipeMain.close();

                        notifyDataSetChanged();
                    }
                });

                if (myEventsPositionsByUser.contains(position)) {
                    tvRemove.setText("Remove Event");
                    ivStar.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_star_filled, AppUtil.getPrimaryThemColorAsInt()));
                    ivStar.setVisibility(View.VISIBLE);
                } else {
                    tvRemove.setText("Add Event");
                    ivStar.setVisibility(GONE);
                }

                if (hiddenPositionsByUser.contains(position)) {
                    tvHide.setText("Unhide");
                    rlContent.setBackgroundResource(R.drawable.layout_hidden_background_rounded);
                    rlContent.getBackground().setAlpha(80);
                } else {
                    tvHide.setText("Hide");
                    rlContent.setBackgroundResource(R.drawable.layout_background_rounded);
                }

                if (mIsExpandedArray.get(position) == false) {
                    tvTimeRange.setVisibility(GONE);
                    eventName.setVisibility(GONE);
                    tvLocation.setVisibility(GONE);
                    tvSpeaker.setVisibility(GONE);
                    relativeLayout.setVisibility(GONE);
                    swipeMain.setVisibility(GONE);
                    rlContent.setVisibility(GONE);
                    rlLeft.setVisibility(GONE);
                    rlRight.setVisibility(GONE);
                    tvRemove.setVisibility(GONE);
                    tvHide.setVisibility(GONE);
                    ivStar.setVisibility(GONE);
                } else {
                    tvTimeRange.setVisibility(View.VISIBLE);
                    eventName.setVisibility(View.VISIBLE);

                    // tvLocation.setVisibility(View.VISIBLE);

                    // tvSpeaker.setVisibility(View.VISIBLE);

                    relativeLayout.setVisibility(View.VISIBLE);
                    swipeMain.setVisibility(View.VISIBLE);
                    rlContent.setVisibility(View.VISIBLE);
                    rlLeft.setVisibility(View.VISIBLE);
                    rlRight.setVisibility(View.VISIBLE);
                    tvRemove.setVisibility(View.VISIBLE);
                    tvHide.setVisibility(View.VISIBLE);
                }
            } catch (Exception ex) {
                Log.d("APD", ex.getMessage());
            }
        }
    }
}

