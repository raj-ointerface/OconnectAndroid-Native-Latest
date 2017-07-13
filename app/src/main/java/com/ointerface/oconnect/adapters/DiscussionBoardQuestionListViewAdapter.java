package com.ointerface.oconnect.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.client.result.ParsedResult;
import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.activities.OConnectBaseActivity;
import com.ointerface.oconnect.data.DBQuestion;
import com.ointerface.oconnect.data.Votes;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by AnthonyDoan on 7/9/17.
 */

public class DiscussionBoardQuestionListViewAdapter extends BaseAdapter {
    public Context context;

    public boolean finalVote = false;

    private LayoutInflater mInflater;

    public ArrayList<DBQuestion> mData = new ArrayList<DBQuestion>();

    public EventDetailExpandableListViewAdapter eventDetailAdapter;

    public DiscussionBoardQuestionListViewAdapter(Context context, ArrayList<DBQuestion> mDataArg) {
        super();
        this.context = context;
        this.mData = mDataArg;

        mInflater = LayoutInflater.from(context);
    }

    public void addItem(final DBQuestion item) {
        mData.add(item);
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position).getQuestion();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            final DBQuestion question = mData.get(position);

            convertView = mInflater.inflate(R.layout.discussion_board_question_list_item, null);

            TextView tvQuestion = (TextView) convertView.findViewById(R.id.tvQuestion);

            tvQuestion.setText(question.getQuestion());

            final TextView tvVoteCount = (TextView) convertView.findViewById(R.id.tvVoteCount);

            tvVoteCount.setText(String.valueOf(question.getVotes()));

            final Realm realm = AppUtil.getRealmInstance(App.getInstance());

            RealmResults<Votes> votes = realm.where(Votes.class).equalTo("question", question.getObjectId()).equalTo("user", OConnectBaseActivity.currentPerson.getObjectId()).findAllSorted("updatedAt", Sort.DESCENDING);

            // ParseObject parseQuestion = ParseQuery.getQuery("DBQuestion").whereEqualTo("objectId", question.getObjectId()).getFirst();

            // List<ParseObject> votesList = ParseQuery.getQuery("Votes").whereEqualTo("question", parseQuestion).find();

            final ImageView ivLike = (ImageView) convertView.findViewById(R.id.ivLike);

            boolean isUpVote = false;

            realm.beginTransaction();
            if (votes.size() > 0) {
                Votes lastVote = votes.get(0);

                if (lastVote.getType().equalsIgnoreCase("up")) {
                    isUpVote = true;
                    ivLike.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_like, AppUtil.getPrimaryThemColorAsInt()));
                } else {
                    isUpVote = false;
                    ivLike.setBackgroundResource(R.drawable.icon_like);
                }
                lastVote.setLastVoteIsUp(isUpVote);
            } else {
                isUpVote = false;
                ivLike.setBackgroundResource(R.drawable.icon_like);
            }
            realm.commitTransaction();

            finalVote = isUpVote;

            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RealmResults<Votes> votes = realm.where(Votes.class).equalTo("question", question.getObjectId()).equalTo("user", OConnectBaseActivity.currentPerson.getObjectId()).findAllSorted("updatedAt", Sort.DESCENDING);

                    if (votes.size() == 0 || votes.get(0).getLastVoteIsUp() == false) {
                        try {
                            realm.beginTransaction();
                            question.setVotes(question.getVotes() + 1);
                            realm.commitTransaction();

                            ivLike.setBackground(AppUtil.changeDrawableColor(context, R.drawable.icon_like, AppUtil.getPrimaryThemColorAsInt()));
                            finalVote = true;

                            final ParseObject newParseVote = new ParseObject("Votes");

                            newParseVote.put("question", ParseObject.createWithoutData("DBQuestion", question.getObjectId()));
                            newParseVote.put("user", ParseUser.createWithoutData("_User", OConnectBaseActivity.currentPerson.getObjectId()));
                            newParseVote.put("type", "up");

                            newParseVote.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        realm.beginTransaction();
                                        Votes newVote = realm.createObject(Votes.class, newParseVote.getObjectId());

                                        newVote.setType("up");
                                        newVote.setUpdatedAt(newParseVote.getUpdatedAt());
                                        newVote.setUser(OConnectBaseActivity.currentPerson.getObjectId());
                                        newVote.setQuestion(question.getObjectId());
                                        newVote.setLastVoteIsUp(true);
                                        realm.commitTransaction();
                                    } else {
                                        Log.d("APD", e.getMessage());
                                    }
                                }
                            });
                        } catch (Exception ex) {
                            Log.d("APD", ex.getMessage());
                        }
                    } else {
                        try {
                            realm.beginTransaction();
                            question.setVotes(question.getVotes() - 1);
                            realm.commitTransaction();

                            ivLike.setBackgroundResource(R.drawable.icon_like);
                            finalVote = false;

                            final ParseObject newParseVote = new ParseObject("Votes");

                            newParseVote.put("question", ParseObject.createWithoutData("DBQuestion", question.getObjectId()));
                            newParseVote.put("user", ParseUser.createWithoutData("_User", OConnectBaseActivity.currentPerson.getObjectId()));
                            newParseVote.put("type", "down");

                            newParseVote.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        realm.beginTransaction();
                                        Votes newVote = realm.createObject(Votes.class, newParseVote.getObjectId());

                                        newVote.setType("down");
                                        newVote.setUpdatedAt(newParseVote.getUpdatedAt());
                                        newVote.setUser(OConnectBaseActivity.currentPerson.getObjectId());
                                        newVote.setQuestion(question.getObjectId());
                                        newVote.setLastVoteIsUp(false);
                                        realm.commitTransaction();
                                    } else {
                                        Log.d("APD", e.getMessage());
                                    }
                                }
                            });
                        } catch (Exception ex) {
                            Log.d("APD", ex.getMessage());
                        }
                    }

                    try {
                        ParseObject parseQuestion = ParseQuery.getQuery("DBQuestion").get(question.getObjectId());

                        if (parseQuestion != null) {
                            parseQuestion.put("votes", question.getVotes());
                            parseQuestion.saveInBackground();
                        }
                    } catch (Exception ex) {
                        Log.d("APD", ex.getMessage());
                    }

                    tvVoteCount.setText(String.valueOf(question.getVotes()));
                }
            });

        } catch (Exception ex) {
            Log.d("APD", ex.getMessage());
        }
        return convertView;
    }
}


