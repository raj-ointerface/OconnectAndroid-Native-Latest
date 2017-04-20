package com.ointerface.oconnect.util;

/**
 * Created by AnthonyDoan on 4/13/17.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ointerface.oconnect.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HTTPPostHandler {

    public String performTwitterImport(Context context, String requestURL,
                                       String userId, String token,
                                       String ocUser) {
        HashMap<String, String> postDataParams = new HashMap<String, String>();

        postDataParams.put("userId", userId);
        postDataParams.put("token", token);
        postDataParams.put("ocUser", ocUser);

        // requestURL = requestURL + "&Username=" + username + "&Password=" + password;

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-Parse-Application-Id", context.getString(R.string.parse_app_id));
            conn.setRequestProperty("X-Parse-REST-API-Key", context.getString(R.string.parse_rest_api_key));
            conn.setRequestProperty("Content-Type", "multipart/form-data");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("oConnect response: ", response);

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}