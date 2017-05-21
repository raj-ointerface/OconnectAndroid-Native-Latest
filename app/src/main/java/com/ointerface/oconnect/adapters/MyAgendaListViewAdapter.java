package com.ointerface.oconnect.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.MyNotesActivity;
import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.MyNote;
import com.ointerface.oconnect.data.Session;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.util.AppUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeSet;

import io.realm.Realm;
import io.realm.RealmList;

import static android.view.View.GONE;

import com.daimajia.swipe.SwipeLayout;
import com.nineoldandroids.view.ViewHelper;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * Created by AnthonyDoan on 4/22/17.
 */

public class MyAgendaListViewAdapter extends BaseSwipeAdapter {
    public Context context;

    private LayoutInflater mInflater;

    public ArrayList<Event> mData = new ArrayList<Event>();

    public TreeSet<Integer> hiddenPositionsByUser = new TreeSet<Integer>();

    public MyAgendaListViewAdapter(Context context, ArrayList<Event> eventsArg) {
        super();
        this.context = context;
        this.mData = eventsArg;

        mInflater = LayoutInflater.from(context);
    }

    public void addItem(final Event item) {
        mData.add(item);
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position).getName();
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
        View v = LayoutInflater.from(context).inflate(R.layout.my_agenda_list_view_item, null);
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

        swipeLayout.findViewById(R.id.ivStar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Star", Toast.LENGTH_SHORT).show();
            }
        });

        swipeLayout.findViewById(R.id.tvHide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Hide", Toast.LENGTH_SHORT).show();
            }
        });

        /*
        swipeLayout.findViewById(R.id.tvRemove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Remove", Toast.LENGTH_SHORT).show();
            }
        });
        */

        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        Event event = mData.get(position);

        TextView tvTimeRange = (TextView) convertView.findViewById(R.id.tvTimeRange);

        SimpleDateFormat dfTime1 = new SimpleDateFormat("EEE, h:mm a");
        dfTime1.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
        SimpleDateFormat dfTime2 = new SimpleDateFormat("h:mm a");
        dfTime2.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));

        if (event.getEndTime() == null) {
            tvTimeRange.setText(dfTime1.format(event.getStartTime()));
        } else {
            tvTimeRange.setText(dfTime1.format(event.getStartTime()) +
                    " - " + dfTime2.format(event.getEndTime()));
        }

        TextView tvEventName = (TextView) convertView.findViewById(R.id.tvEventName);

        if (!event.getName().equalsIgnoreCase("")) {
            tvEventName.setText(event.getName());
            tvEventName.setVisibility(View.VISIBLE);
        } else {
            tvEventName.setVisibility(GONE);
        }

        TextView tvEventLocation = (TextView) convertView.findViewById(R.id.tvEventLocation);

        // Realm realm = AppUtil.getRealmInstance(App.getInstance());

        // Session session = realm.where(Session.class).equalTo("objectId", event.getSession()).findFirst();

        if (event.getLocation() != null && !event.getLocation().equalsIgnoreCase("")) {
            tvEventLocation.setText(event.getLocation());
            tvEventLocation.setVisibility(View.VISIBLE);
        } else {
            tvEventLocation.setVisibility(GONE);
        }

        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        TextView tvSpeaker = (TextView) convertView.findViewById(R.id.tvSpeakerNames);

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

        final int finalPosition = position;
        RelativeLayout rlContent = (RelativeLayout) convertView.findViewById(R.id.rlContent);
        final SwipeLayout swipeMain = (SwipeLayout) convertView.findViewById(R.id.swipeMain);
        TextView tvRemove = (TextView) convertView.findViewById(R.id.tvRemove);
        TextView tvHide = (TextView) convertView.findViewById(R.id.tvHide);
        final Event finalEvent = event;

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

        tvRemove.setTextColor(AppUtil.getPrimaryThemColorAsInt());

        tvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = AppUtil.getRealmInstance(App.getInstance());

                if (OConnectBaseActivity.currentPerson != null) {
                    realm.beginTransaction();
                    OConnectBaseActivity.currentPerson.getFavoriteEvents().remove(finalEvent);
                    realm.commitTransaction();
                }

                /*
                RealmList<Event> newAgendaList = new RealmList<Event>();

                newAgendaList.addAll(OConnectBaseActivity.currentPerson.getFavoriteEvents());

                newAgendaList.remove(finalEvent);

                OConnectBaseActivity.currentPerson.setFavoriteEvents(newAgendaList);

                realm.commitTransaction();
                */

                try {
                    if (OConnectBaseActivity.currentPerson != null) {
                        ParseUser user = ParseUser.getQuery().get(OConnectBaseActivity.currentPerson.getObjectId());
                        ParseRelation<ParseObject> eventsRelation = user.getRelation("favoriteEventsRelation");

                        ParseObject eventObj = ParseQuery.getQuery("Event").get(finalEvent.getObjectId());

                        if (eventObj != null) {
                            eventsRelation.remove(eventObj);
                        }

                        // user.put("favoriteEventsRelation", eventsRelation);

                        user.save();
                    }
                } catch (Exception ex) {
                    Log.d("APD", ex.getMessage());
                }

                mData.remove(finalEvent);

                Toast.makeText(context, "Event removed from My Agenda ...", Toast.LENGTH_LONG).show();

                notifyDataSetChanged();
            }
        });

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


