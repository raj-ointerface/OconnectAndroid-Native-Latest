package com.ointerface.oconnect.data;

/**
 * Created by AnthonyDoan on 4/14/17.
 */

import android.util.Log;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;

public class CloudUtil {
    static public void setPersonToUserRole(ParseUser user) {
        ParseQuery<ParseRole> roleQuery = ParseRole.getQuery().whereEqualTo("name", "User");
        try {
            ParseRole userRole = roleQuery.getFirst();

            userRole.getUsers().add(user);

            userRole.save();
        } catch (Exception ex) {
            Log.d("CloudUtil", ex.getMessage());
        }
    }
}
