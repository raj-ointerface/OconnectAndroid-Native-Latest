package com.ointerface.oconnect.network;

import android.app.DownloadManager;
import android.os.AsyncTask;

import com.twitter.sdk.android.core.internal.oauth.OAuth2Token;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;

/**
 * Created by AnthonyDoan on 4/16/17.
 */

public class TwitterAsyncTask extends AsyncTask<String, Void, Void> {
    protected Void doInBackground(String... params) {
        try {
            // ConfigurationBuilder builder = new twitter4j.conf.ConfigurationBuilder();
            /*
            builder.setUseSSL(true);
            builder.setApplicationOnlyAuthEnabled(true);
            builder.setOAuthConsumerKey(TWIT_CONS_KEY);
            builder.setOAuthConsumerSecret(TWIT_CONS_SEC_KEY);

            OAuth2Token token = new TwitterFactory(builder.build()).getInstance().getOAuth2Token();

            builder = new ConfigurationBuilder();
            builder.setUseSSL(true);
            builder.setApplicationOnlyAuthEnabled(true);
            builder.setOAuthConsumerKey(TWIT_CONS_KEY);
            builder.setOAuthConsumerSecret(TWIT_CONS_SEC_KEY);
            builder.setOAuth2TokenType(token.getTokenType());
            builder.setOAuth2AccessToken(token.getAccessToken());

            Twitter twitter = new TwitterFactory(builder.build()).getInstance();

            Query query = new Query(params[0]);
            // YOu can set the count of maximum records here
            query.setCount(50);
            QueryResult result;
            result = twitter.search(query);
            List<twitter4j.Status> tweets = result.getTweets();
            StringBuilder str = new StringBuilder();
            if (tweets != null) {
                this.tweets = new ArrayList<Tweet>();
                for (twitter4j.Status tweet : tweets) {
                    str.append("@" + tweet.getUser().getScreenName() + " - " + tweet.getText() + "\n");
                    System.out.println(str);
                    this.tweets.add(new Tweet("@" + tweet.getUser().getScreenName(), tweet.getText()));
                }

            }
            */
            int temp = 0;

            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
        //setProgressPercent(progress[0]);
    }

    protected void onPostExecute(Long result) {
        //showDialog("Downloaded " + result + " bytes");
    }
}

