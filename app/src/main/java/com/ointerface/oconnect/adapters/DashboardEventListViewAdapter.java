package com.ointerface.oconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.ConferenceListViewAdapter;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.DashboardActivity;
import com.ointerface.oconnect.activities.EventDetailViewActivity;
import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.activities.ScheduleActivity;
import com.ointerface.oconnect.containers.MenuItemHolder;
import com.ointerface.oconnect.data.Conference;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.Session;
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

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by AnthonyDoan on 4/17/17.
 */

public class DashboardEventListViewAdapter extends BaseAdapter {
    public Context context;
    public ArrayList<Event> eventsList;
    public ArrayList<String> sessionNamesList;

    private LayoutInflater mInflater;

    public DashboardEventListViewAdapter(Context context, ArrayList<Event> eventsArg , ArrayList<String> sessionsArg) {
        super();
        this.context = context;
        this.eventsList = eventsArg;
        this.sessionNamesList = sessionsArg;

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return eventsList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public String getItem(int position) {
        return eventsList.get(position).getName();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Event event = eventsList.get(position);
        String sessionName = sessionNamesList.get(position);

        convertView = mInflater.inflate(R.layout.dashboard_event_list_view_item, null);

        DateFormat dfDay = new SimpleDateFormat("EEE");
        DateFormat dfTime = new SimpleDateFormat("h:mm a");
        dfTime.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));

        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDateDay);

        TextView tvTimeStart = (TextView) convertView.findViewById(R.id.tvStartTime);

        if (event.getStartTime() != null) {
            tvDate.setText(dfDay.format(event.getStartTime()) + ",");
            tvTimeStart.setText(dfTime.format(event.getStartTime()) + " - ");
        }

        TextView tvTimeEnd = (TextView) convertView.findViewById(R.id.tvEndTime);

        if (event.getEndTime() != null) {
            tvTimeEnd.setText(dfTime.format(event.getEndTime()));
        }

        TextView tvEventName = (TextView) convertView.findViewById(R.id.tvEventName);

        tvEventName.setText(event.getName());

        TextView tvSessionName = (TextView) convertView.findViewById(R.id.tvSessionName);

        tvSessionName.setText(sessionName);

        tvEventName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoEventDetail(event);
            }
        });

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoEventDetail(event);
            }
        });

        tvTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoEventDetail(event);
            }
        });

        tvTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoEventDetail(event);
            }
        });

        tvSessionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoEventDetail(event);
            }
        });

        final ImageView ivStar = (ImageView) convertView.findViewById(R.id.ivStar);

        RealmList<Event> tempList = new RealmList<Event>();

        if (OConnectBaseActivity.currentPerson != null) {
            tempList = OConnectBaseActivity.currentPerson.getFavoriteEvents();
        }

        final RealmList<Event> myAgendaList = tempList;

        if (myAgendaList.contains(event) == true) {
            ivStar.setBackgroundResource(R.drawable.icon_star_filled);
        } else {
            ivStar.setBackgroundResource(R.drawable.icon_star_empty);
        }

        final Event finalEvent = event;

        ivStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtil.getIsSignedIn(context) == false) {
                    AppUtil.displayPleaseSignInDialog(context);
                    return;
                }

                boolean shouldAddToMyAgenda = false;
                Realm realm = AppUtil.getRealmInstance(App.getInstance());

                if (myAgendaList.contains(finalEvent) == true) {
                    shouldAddToMyAgenda = false;
                    ivStar.setBackgroundResource(R.drawable.icon_star_empty);
                    realm.beginTransaction();
                    OConnectBaseActivity.currentPerson.getFavoriteEvents().remove(event);
                    realm.commitTransaction();
                    notifyDataSetChanged();
                    Toast.makeText(context, "Event removed from My Agenda ...", Toast.LENGTH_LONG).show();
                } else {
                    shouldAddToMyAgenda = true;
                    ivStar.setBackgroundResource(R.drawable.icon_star_filled);
                    realm.beginTransaction();
                    OConnectBaseActivity.currentPerson.getFavoriteEvents().add(event);
                    realm.commitTransaction();
                    notifyDataSetChanged();
                    Toast.makeText(context, "Event added to My Agenda ...", Toast.LENGTH_LONG).show();
                }

                boolean eventInAgenda = false;

                for (Event currentEvent : myAgendaList) {
                    if (currentEvent.getObjectId().equalsIgnoreCase(finalEvent.getObjectId())) {
                        eventInAgenda = true;

                        if (shouldAddToMyAgenda == false) {
                            /*
                            realm.beginTransaction();
                            RealmList<Event> newAgendaList = new RealmList<Event>();

                            newAgendaList.addAll(myAgendaList);

                            newAgendaList.remove(currentEvent);

                            OConnectBaseActivity.currentPerson.setFavoriteEvents(newAgendaList);

                            realm.commitTransaction();
                            */

                            try {
                                ParseUser user = ParseUser.getQuery().get(OConnectBaseActivity.currentPerson.getObjectId());
                                ParseRelation<ParseObject> eventsRelation = user.getRelation("favoriteEventsRelation");

                                ParseObject eventObj = ParseQuery.getQuery("Event").get(currentEvent.getObjectId());

                                if (eventObj != null) {
                                    eventsRelation.remove(eventObj);
                                }

                                // user.put("favoriteEventsRelation", eventsRelation);

                                user.save();
                            } catch (Exception ex) {
                                Log.d("APD", ex.getMessage());
                            }

                            // Toast.makeText(context, "Event removed from My Agenda ...", Toast.LENGTH_LONG).show();
                        }
                        break;
                    }
                }

                if (eventInAgenda == false && shouldAddToMyAgenda == true) {
                    /*
                    realm.beginTransaction();
                    RealmList<Event> newAgendaList = new RealmList<Event>();

                    newAgendaList.addAll(myAgendaList);

                    newAgendaList.add(event);

                    OConnectBaseActivity.currentPerson.setFavoriteEvents(newAgendaList);

                    realm.commitTransaction();
                    */

                    try {
                        ParseUser user = ParseUser.getQuery().get(OConnectBaseActivity.currentPerson.getObjectId());
                        ParseRelation<ParseObject> eventsRelation = user.getRelation("favoriteEventsRelation");

                        ParseObject eventObj = ParseQuery.getQuery("Event").get(event.getObjectId());

                        if (eventObj != null) {
                            eventsRelation.add(eventObj);
                        }

                        // user.put("favoriteEventsRelation", eventsRelation);

                        user.save();
                    } catch (Exception ex) {
                        Log.d("APD", ex.getMessage());
                    }

                    // Toast.makeText(context, "Event added to My Agenda ...", Toast.LENGTH_LONG).show();
                }
            }
        });

        return convertView;
    }

    public void gotoEventDetail(Event event) {
        // Goto Event Detail View
        EventDetailViewActivity.mItems = new ArrayList<RealmObject>();

        int newPosition = 0;

        EventDetailViewActivity.mItems.add(event);

        Intent i = new Intent(context, EventDetailViewActivity.class);
        i.putExtra("EVENT_NUMBER", newPosition);
        context.startActivity(i);
    }
}
