package com.ointerface.oconnect.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.AddNoteActivity;
import com.ointerface.oconnect.activities.DiscussionBoardActivity;
import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.activities.ParticipantsActivity;
import com.ointerface.oconnect.activities.SpeakerDetailViewActivity;
import com.ointerface.oconnect.data.DBQuestion;
import com.ointerface.oconnect.data.DiscussionBoard;
import com.ointerface.oconnect.data.Event;
import com.ointerface.oconnect.data.EventAbstract;
import com.ointerface.oconnect.data.EventFile;
import com.ointerface.oconnect.data.EventJournal;
import com.ointerface.oconnect.data.EventLink;
import com.ointerface.oconnect.data.EventMisc;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.data.Session;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.data.SpeakerAbstract;
import com.ointerface.oconnect.data.SpeakerEventCache;
import com.ointerface.oconnect.data.SpeakerFile;
import com.ointerface.oconnect.data.SpeakerJournal;
import com.ointerface.oconnect.data.SpeakerLink;
import com.ointerface.oconnect.data.SpeakerMisc;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.view.View.GONE;

/**
 * Created by AnthonyDoan on 5/7/17.
 */

public class EventDetailExpandableListViewAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private Activity activity;
    private List<String> _listDataHeader;
    private List<Integer> _listHeaderNumber;
    private List<Boolean> _listGroupIsSpeaker;
    private HashMap<Integer, Integer> _listChildCount;
    private HashMap<Integer, ArrayList<Speaker>> _listChildSpeaker;
    private Event _listEvent;
    private EventDetailExpandableListViewAdapter thisAdapter;

    public View section1Header;

    public EventDetailExpandableListViewAdapter(Context context, List<String> listDataHeader,
                                             List<Integer> listHeaderNumber,
                                             HashMap<Integer, Integer> listChildCount,
                                                List<Boolean> listGroupIsSpeaker,
                                                HashMap<Integer, ArrayList<Speaker>> listChildSpeaker,
                                                Event listEvent, Activity activityArg) {
        this.activity = activityArg;
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listHeaderNumber = listHeaderNumber;
        this._listChildCount = listChildCount;
        this._listGroupIsSpeaker = listGroupIsSpeaker;
        this._listChildSpeaker = listChildSpeaker;
        this._listEvent = listEvent;
        this.thisAdapter = this;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listChildCount.get(this._listHeaderNumber.get(groupPosition));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listChildCount.get(this._listHeaderNumber.get(groupPosition)).intValue();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Realm realm = AppUtil.getRealmInstance(App.getInstance());

        String groupItemStr = (String) getGroup(groupPosition);

            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (groupPosition == 0 && childPosition == 0) {
                convertView = infalInflater.inflate(R.layout.event_detail_top_section_list_view_item, null);

                RelativeLayout mainContainer = (RelativeLayout) convertView.findViewById(R.id.main_container);
                LinearLayout llMain = (LinearLayout) convertView.findViewById(R.id.llMain);
                RelativeLayout rlTopSection = (RelativeLayout) convertView.findViewById(R.id.rlTopSection);
                RelativeLayout rlMiscItems = (RelativeLayout) convertView.findViewById(R.id.rlMiscItems);

                mainContainer.setClipChildren(false);
                llMain.setClipChildren(false);
                rlTopSection.setClipChildren(false);
                rlMiscItems.setClipChildren(false);

                final ImageView ivMyAgenda = (ImageView) convertView.findViewById(R.id.ivMyAgenda);

                ivMyAgenda.setBackground(AppUtil.changeDrawableColor(_context, R.drawable.icon_blue_star_empty, AppUtil.getPrimaryThemColorAsInt()));

                RealmList<Event> myAgendaList = OConnectBaseActivity.currentPerson.getFavoriteEvents();

                if (myAgendaList.contains(_listEvent) == true) {
                    ivMyAgenda.setBackground(AppUtil.changeDrawableColor(_context, R.drawable.icon_blue_star_filled, AppUtil.getPrimaryThemColorAsInt()));
                }

                ImageView ivTweet = (ImageView) convertView.findViewById(R.id.ivTweet);

                ivTweet.setBackground(AppUtil.changeDrawableColor(_context, R.drawable.twitter_icon, AppUtil.getPrimaryThemColorAsInt()));

                ImageView ivAddNote = (ImageView) convertView.findViewById(R.id.ivAddANote);

                ivAddNote.setBackground(AppUtil.changeDrawableColor(_context, R.drawable.icon_add_a_note, AppUtil.getPrimaryThemColorAsInt()));

                TextView tvMyAgenda = (TextView) convertView.findViewById(R.id.tvMyAgenda);

                tvMyAgenda.setTextColor(AppUtil.getPrimaryThemColorAsInt());

                tvMyAgenda.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        realm.beginTransaction();
                        OConnectBaseActivity.currentPerson.getFavoriteEvents().add(_listEvent);
                        realm.commitTransaction();

                        ivMyAgenda.setBackground(AppUtil.changeDrawableColor(_context, R.drawable.icon_blue_star_filled, AppUtil.getPrimaryThemColorAsInt()));
                    }
                });

                ivMyAgenda.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        realm.beginTransaction();
                        OConnectBaseActivity.currentPerson.getFavoriteEvents().add(_listEvent);
                        realm.commitTransaction();

                        ivMyAgenda.setBackground(AppUtil.changeDrawableColor(_context, R.drawable.icon_blue_star_filled, AppUtil.getPrimaryThemColorAsInt()));
                    }
                });

                TextView tvTweet = (TextView) convertView.findViewById(R.id.tvTweet);

                tvTweet.setTextColor(AppUtil.getPrimaryThemColorAsInt());

                ivTweet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
                        tweetIntent.putExtra(Intent.EXTRA_TEXT, "Attending \"" + OConnectBaseActivity.selectedConference.getName() + "\"" + " #oconnectapp");
                        tweetIntent.setType("text/plain");

                        PackageManager packManager = _context.getPackageManager();
                        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent,  PackageManager.MATCH_DEFAULT_ONLY);

                        boolean resolved = false;
                        for(ResolveInfo resolveInfo: resolvedInfoList){
                            if(resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")){
                                tweetIntent.setClassName(
                                        resolveInfo.activityInfo.packageName,
                                        resolveInfo.activityInfo.name );
                                resolved = true;
                                break;
                            }
                        }
                        if(resolved){
                            _context.startActivity(tweetIntent);
                        }else{
                            Toast.makeText(_context, "Twitter app isn't found.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                tvTweet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
                        tweetIntent.putExtra(Intent.EXTRA_TEXT, "Attending \"" + OConnectBaseActivity.selectedConference.getName() + "\"" + " #oconnectapp");
                        tweetIntent.setType("text/plain");

                        PackageManager packManager = _context.getPackageManager();
                        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent,  PackageManager.MATCH_DEFAULT_ONLY);

                        boolean resolved = false;
                        for(ResolveInfo resolveInfo: resolvedInfoList){
                            if(resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")){
                                tweetIntent.setClassName(
                                        resolveInfo.activityInfo.packageName,
                                        resolveInfo.activityInfo.name );
                                resolved = true;
                                break;
                            }
                        }
                        if(resolved){
                            _context.startActivity(tweetIntent);
                        }else{
                            Toast.makeText(_context, "Twitter app isn't found.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                TextView tvAddNote = (TextView) convertView.findViewById(R.id.tvAddNote);

                tvAddNote.setTextColor(AppUtil.getPrimaryThemColorAsInt());

                ivAddNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(_context, AddNoteActivity.class);
                        _context.startActivity(i);
                        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up );
                    }
                });

                tvAddNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(activity, AddNoteActivity.class);
                        activity.startActivity(i);
                        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up );
                    }
                });

                TextView tvQA = (TextView) convertView.findViewById(R.id.tvQA);

                tvQA.setTextColor(AppUtil.getPrimaryThemColorAsInt());

                if (OConnectBaseActivity.selectedConference.isShowQuestions() == true) {
                    tvQA.setVisibility(View.VISIBLE);
                } else {
                    tvQA.setVisibility(GONE);
                }

                ImageView ivOrganizationLogo = (ImageView) convertView.findViewById(R.id.ivOrganizationLogo);

                if (OConnectBaseActivity.selectedConference.getImage() != null) {
                    Bitmap bm = BitmapFactory.decodeByteArray(OConnectBaseActivity.selectedConference.getImage(), 0, OConnectBaseActivity.selectedConference.getImage().length);

                    ivOrganizationLogo.setImageBitmap(bm);
                }

                TextView tvEventTitle = (TextView) convertView.findViewById(R.id.tvEventTitle);
                tvEventTitle.setText(_listEvent.getName());

                TextView tvTimeRange = (TextView) convertView.findViewById(R.id.tvEventDateRange);

                if (!_listEvent.isNonTimedEvent()) {
                    DateFormat dfTime = new SimpleDateFormat("h:mm a");
                    DateFormat dfDate = new SimpleDateFormat("MMM d");

                    dfTime.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
                    dfDate.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));

                    String startTime = dfTime.format(_listEvent.getStartTime());
                    String endTime = dfTime.format(_listEvent.getEndTime());

                    tvTimeRange.setText(startTime + " - " + endTime + " on " + dfDate.format(_listEvent.getStartTime()));

                    tvTimeRange.setVisibility(View.VISIBLE);
                } else {
                    tvTimeRange.setVisibility(GONE);
                }
            } else if (groupItemStr.equalsIgnoreCase("Info")) {
                convertView = infalInflater.inflate(R.layout.event_detail_list_child, null);

                TextView tvInfo = (TextView) convertView.findViewById(R.id.tvInfo);

                tvInfo.setText(_listEvent.getInfo());
            } else if (_listGroupIsSpeaker.get(groupPosition) == true) {
                convertView = infalInflater.inflate(R.layout.event_detail_list_speaker_child, null);

                ListView lvSpeakers = (ListView) convertView.findViewById(R.id.lvSpeakers);

                final ArrayList<Speaker> speakersList = _listChildSpeaker.get(groupPosition);
                ArrayList<String> speakerNamesList = new ArrayList<String>();

                for (int i = 0; i < speakersList.size(); ++i) {
                    speakerNamesList.add(speakersList.get(i).getName());
                }

                ArrayAdapter<String> itemsAdapter =
                        new ArrayAdapter<String>(_context, android.R.layout.simple_list_item_1, speakerNamesList);

                lvSpeakers.setAdapter(itemsAdapter);

                lvSpeakers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // SpeakerDetailViewActivity.mItems = new ArrayList<RealmObject>();

                        // SpeakerDetailViewActivity.mItems.addAll(speakersList);

                        Intent i = new Intent(_context, SpeakerDetailViewActivity.class);
                        i.putExtra("SPEAKER_NUMBER", position);
                        i.putExtra("SPEAKER_LIST", speakersList);
                        _context.startActivity(i);
                    }
                });
            } else if (groupItemStr.equalsIgnoreCase("Links")) {
                RealmResults<EventLink> linksResult = realm.where(EventLink.class).equalTo("eventID", _listEvent.getObjectId()).findAll();

                convertView = infalInflater.inflate(R.layout.speaker_detail_item_list_view, null);

                ArrayList<String> links = new ArrayList<String>();
                ArrayList<String> urls = new ArrayList<String>();

                for (int i = 0; i < linksResult.size(); ++i) {
                    EventLink link = linksResult.get(i);

                    links.add(link.getLabel());
                    urls.add(link.getLink());
                }

                ListView lvItems = (ListView) convertView.findViewById(R.id.lvItems);

                LinksListViewAdapter linksAdapter = new LinksListViewAdapter(_context, links, urls);

                lvItems.setAdapter(linksAdapter);

                final ArrayList<String> finalUrls = urls;

                lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrls.get(position)));
                        _context.startActivity(browserIntent);
                    }
                });
            } else if (groupItemStr.equalsIgnoreCase("Discussion Board")) {

                convertView = infalInflater.inflate(R.layout.event_detail_discussion_board_list_view_item, null);

                RealmResults<DBQuestion> questionsResult = realm.where(DBQuestion.class).equalTo("event", _listEvent.getObjectId()).findAllSorted("votes", Sort.DESCENDING);

                ListView lvItems = (ListView) convertView.findViewById(R.id.lvItems);

                ArrayList<DBQuestion> questionsList = new ArrayList<DBQuestion>();

                for (int i = 0; i < questionsResult.size(); ++i) {
                    questionsList.add(questionsResult.get(i));

                    if (i >= 2) {
                        break;
                    }
                }

                DiscussionBoardQuestionListViewAdapter thisAdapter = new DiscussionBoardQuestionListViewAdapter(_context, questionsList);

                thisAdapter.eventDetailAdapter = this;

                lvItems.setAdapter(thisAdapter);

                TextView tvViewAll = (TextView) convertView.findViewById(R.id.tvViewAll);

                if (questionsResult.size() > 1) {
                    tvViewAll.setVisibility(View.VISIBLE);
                } else {
                    tvViewAll.setVisibility(GONE);
                }

                tvViewAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(_context, DiscussionBoardActivity.class);
                        DiscussionBoardActivity.discussionBoardEvent = _listEvent;
                        _context.startActivity(intent);
                    }
                });
            } else if (groupItemStr.equalsIgnoreCase("Post Questions & Comments")) {
                convertView = infalInflater.inflate(R.layout.discussion_board_list_item_post_questions, null);

                final EditText etSendQuestion = (EditText) convertView.findViewById(R.id.etSendQuestion);

                TextView tvSend = (TextView) convertView.findViewById(R.id.tvSend);

                tvSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        RealmResults<DiscussionBoard> discussionBoardResults = realm.where(DiscussionBoard.class).equalTo("event", _listEvent.getObjectId()).findAll();

                        DiscussionBoard discussionBoard = null;

                        if (discussionBoardResults.size() > 0) {
                            discussionBoard = discussionBoardResults.get(0);
                        }

                        final DiscussionBoard finalDiscussionBoard = discussionBoard;

                        final ParseObject newParseQuestion = new ParseObject("DBQuestion");

                        newParseQuestion.put("question", etSendQuestion.getText().toString());
                        newParseQuestion.put("votes", 0);
                        newParseQuestion.put("user_email", OConnectBaseActivity.currentPerson.getContact_email());
                        newParseQuestion.put("isResolved", false);
                        newParseQuestion.put("event", ParseObject.createWithoutData("Event", _listEvent.getObjectId()));
                        newParseQuestion.put("conference", ParseObject.createWithoutData("Conference", OConnectBaseActivity.selectedConference.getObjectId()));

                        if (discussionBoard != null) {
                            newParseQuestion.put("discussionBoard", ParseObject.createWithoutData("DiscussionBoard", discussionBoard.getObjectId()));
                        }

                        newParseQuestion.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                realm.beginTransaction();

                                DBQuestion newQuestion = realm.createObject(DBQuestion.class, newParseQuestion.getObjectId());

                                newQuestion.setQuestion(etSendQuestion.getText().toString());
                                newQuestion.setVotes(0);
                                newQuestion.setUpdatedAt(newParseQuestion.getUpdatedAt());
                                newQuestion.setUser_email(OConnectBaseActivity.currentPerson.getContact_email());
                                newQuestion.setResolved(false);
                                newQuestion.setConference(OConnectBaseActivity.selectedConference.getObjectId());

                                if (finalDiscussionBoard != null) {
                                    newQuestion.setDiscussionBoard(finalDiscussionBoard.getObjectId());
                                }

                                newQuestion.setEvent(_listEvent.getObjectId());
                                realm.commitTransaction();

                                thisAdapter.notifyDataSetChanged();

                                AppUtil.displayPostingMessageDialog(activity);
                            }
                        });
                    }
                });
            }else if (groupItemStr.equalsIgnoreCase("Files")) {

                ArrayList<RealmObject> filesList = new ArrayList<RealmObject>();

                RealmResults<EventAbstract> abstractResult = realm.where(EventAbstract.class).equalTo("eventID", _listEvent.getObjectId()).findAll();
                RealmResults<EventMisc> miscResult = realm.where(EventMisc.class).equalTo("eventID", _listEvent.getObjectId()).findAll();
                RealmResults<EventJournal> journalResult = realm.where(EventJournal.class).equalTo("eventID", _listEvent.getObjectId()).findAll();
                RealmResults<EventFile> fileResult = realm.where(EventFile.class).equalTo("eventID", _listEvent.getObjectId()).findAll();

                filesList.addAll(abstractResult);
                filesList.addAll(miscResult);
                filesList.addAll(journalResult);
                filesList.addAll(fileResult);

                ArrayList<String> links = new ArrayList<String>();
                ArrayList<String> urls = new ArrayList<String>();

                for (int i = 0; i < filesList.size(); ++i) {
                    RealmObject file = filesList.get(i);

                    if (file instanceof EventFile) {
                        EventFile curFile = (EventFile) file;

                        links.add(curFile.getName());
                        urls.add(curFile.getUrl());
                    } else if (file instanceof EventJournal) {
                        EventJournal curFile = (EventJournal) file;

                        links.add(curFile.getName());
                        urls.add(curFile.getUrl());
                    } else if (file instanceof EventMisc) {
                        EventMisc curFile = (EventMisc) file;

                        links.add(curFile.getName());
                        urls.add(curFile.getUrl());
                    } else if (file instanceof EventAbstract) {
                        EventAbstract curFile = (EventAbstract) file;

                        links.add(curFile.getName());
                        urls.add(curFile.getUrl());
                    }
                }

                convertView = infalInflater.inflate(R.layout.speaker_detail_item_list_view, null);

                ListView lvItems = (ListView) convertView.findViewById(R.id.lvItems);

                LinksListViewAdapter linksAdapter = new LinksListViewAdapter(_context, links, urls);

                lvItems.setAdapter(linksAdapter);

                final ArrayList<String> finalUrls = urls;

                lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    /*
                    Intent i = new Intent(_context, WebViewActivity.class);
                    i.putExtra("TITLE", "File");
                    i.putExtra("URL", finalUrls.get(position));
                    i.putExtra("BACK_TEXT", "Back");
                    i.putExtra("OPEN", "Open In Browser");
                    _context.startActivity(i);
                    */

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrls.get(position)));
                        _context.startActivity(browserIntent);

                    }
                });
            }


        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        // Session session = (Session) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.event_detail_list_header, null);
        }

        TextView tvEventDetailName = (TextView) convertView.findViewById(R.id.tvEventDetailName);

        tvEventDetailName.setText((String) _listDataHeader.get(groupPosition));

        TextView tvArrow = (TextView) convertView.findViewById(R.id.tvArrow);

        tvArrow.setText(">");

        if (isExpanded == true) {
            tvArrow.setRotation(90);
        } else {
            tvArrow.setRotation(0);
        }

        if (groupPosition == 0) {
            tvEventDetailName.setVisibility(GONE);
            tvArrow.setVisibility(GONE);
            convertView.setVisibility(GONE);
            section1Header = convertView;
        } else {
            tvEventDetailName.setVisibility(View.VISIBLE);
            tvArrow.setVisibility(View.VISIBLE);
            convertView.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
