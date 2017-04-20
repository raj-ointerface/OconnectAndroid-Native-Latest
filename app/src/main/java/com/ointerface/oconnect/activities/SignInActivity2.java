package com.ointerface.oconnect.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.ointerface.oconnect.ConferenceListViewActivity;
import com.ointerface.oconnect.CustomSplashActivity;
import com.ointerface.oconnect.MainSplashActivity;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.data.DataSyncManager;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.util.AppConfig;
import com.ointerface.oconnect.util.AppUtil;
import com.ointerface.oconnect.util.HTTPPostHandler;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONException;

import java.util.List;

public class SignInActivity2 extends AppCompatActivity {
    enum SignInType { Normal,Twitter,Facebook, LinkedIn}

    public SignInType currentSignInType = SignInType.Normal;

    private EditText username;
    private EditText password;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FacebookSdk.sdkInitialize(this);

        setContentView(R.layout.activity_sign_in_2);

        Button btnClose = (Button) findViewById(R.id.btnClose);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInActivity2.this.finish();
                Intent i = new Intent(SignInActivity2.this, DashboardActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up );
            }
        });

        username = (EditText) findViewById(R.id.etEmail);
        password = (EditText) findViewById(R.id.etPassword);
    }

    public void createAccountClicked(View sender) {
        Intent i = new Intent(SignInActivity2.this, CreateAccountActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up );
    }

    public void forgotPasswordClicked(View sender) {
        ParseUser.requestPasswordResetInBackground(username.getText().toString(), new RequestPasswordResetCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    AlertDialog alertDialog = new AlertDialog.Builder(SignInActivity2.this).create();
                    alertDialog.setTitle("");
                    alertDialog.setMessage("An email with instructions on how to reset your password has been sent to your email.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(SignInActivity2.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage(e.getLocalizedMessage());
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });
    }

    public void twitterLoginClicked(View sender) {
        TwitterAuthClient twitterAuthClient = new TwitterAuthClient();
        twitterAuthClient.authorize(SignInActivity2.this, new Callback<TwitterSession>() {
            @Override
            public void success(final Result<TwitterSession> result) {
                final TwitterSession sessionData = result.data;
                // Do something with the returned TwitterSession (contains the user token and secret)
                currentSignInType = SignInType.Twitter;

                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    new ImportTask().execute("");
                } else {
                    new android.app.AlertDialog.Builder(SignInActivity2.this)
                            .setTitle(getString(R.string.no_internet))
                            .setMessage(getString(R.string.internet_message))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .show();
                }
            }

            @Override
            public void failure(final TwitterException e) {
                AlertDialog alertDialog = new AlertDialog.Builder(SignInActivity2.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Twitter login failed.  Please try again.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    public void facebookLoginClicked(View sender) {

    }

    public void linkedInLoginClicked(View sender) {

    }

    public void signInClicked(View sender) {
        final String usernametxt = username.getText().toString();
        String passwordtxt = password.getText().toString();

        dialog = ProgressDialog.show((Context)this, null, "Refreshing Data ...");

        // Send data to Parse.com for verification
        ParseUser.logInInBackground(usernametxt, passwordtxt,
                new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            OConnectBaseActivity.currentPerson = Person.saveFromParseUser(user);
                            AppUtil.setIsSignedIn(SignInActivity2.this, true);
                            AppUtil.setSignedInUserID(SignInActivity2.this, user.getObjectId());

                            dialog.dismiss();

                            try {
                                List<ParseObject> speakerList = ParseQuery.getQuery("Speaker").whereEqualTo("email", usernametxt).find();
                                List<ParseObject> attendeeList = ParseQuery.getQuery("Attendee").whereEqualTo("email", usernametxt).find();

                                final ParseUser finalUser = user;

                                if (speakerList.size() == 0 &&
                                        attendeeList.size() == 0 &&
                                        AppUtil.hasPinPromptEnteredForConference(SignInActivity2.this, AppUtil.getSelectedConferenceID(SignInActivity2.this)) == false
                                        && OConnectBaseActivity.selectedConference.isShouldShowPin() == true &&
                                        AppUtil.hasPinPromptSkippedForConference(SignInActivity2.this, AppUtil.getSelectedConferenceID(SignInActivity2.this)) == false) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity2.this);
                                    builder.setTitle("Please Enter Your PIN To Verify Your Identity");
                                    builder.setMessage("If you have received an email from the organizer with a PIN number, please enter it here to verify your identity.  If you did not receive an email, please Skip this step.");

                                    final EditText input = new EditText(SignInActivity2.this);

                                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                    builder.setView(input);

                                    builder.setPositiveButton("Enter PIN", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            try {
                                                List<ParseObject> speakerList = ParseQuery.getQuery("Speaker").whereEqualTo("IOS_code", input.getText().toString()).find();
                                                List<ParseObject> attendeeList = ParseQuery.getQuery("Attendee").whereEqualTo("IOS_code", input.getText().toString()).find();

                                                boolean bUserLinked = false;

                                                if (speakerList.size() > 0) {
                                                    ParseObject speakerObj = speakerList.get(0);
                                                    speakerObj.put("UserLink", finalUser.getObjectId());
                                                    speakerObj.save();
                                                    bUserLinked = true;
                                                } else if (attendeeList.size() > 0) {
                                                    ParseObject attendeeObj = attendeeList.get(0);
                                                    attendeeObj.put("UserLink", finalUser.getObjectId());
                                                    attendeeObj.save();
                                                    bUserLinked = true;
                                                }

                                                if (bUserLinked == true) {
                                                    AppUtil.addConferenceForPinPromptEntered(SignInActivity2.this, OConnectBaseActivity.selectedConference.getObjectId());
                                                    SignInActivity2.this.finish();
                                                    Intent i = new Intent(SignInActivity2.this, DashboardActivity.class);
                                                    startActivity(i);
                                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up );
                                                } else {
                                                    AlertDialog alertDialog = new AlertDialog.Builder(SignInActivity2.this).create();
                                                    alertDialog.setTitle("Error");
                                                    alertDialog.setMessage("Incorrect PIN.  Please try again when logging in.");
                                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    SignInActivity2.this.finish();
                                                                    Intent i = new Intent(SignInActivity2.this, DashboardActivity.class);
                                                                    startActivity(i);
                                                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up );
                                                                }
                                                            });
                                                    alertDialog.show();
                                                }
                                            } catch (Exception ex) {
                                                Log.d("SignIn2", ex.getMessage());
                                            }
                                        }
                                    });
                                    builder.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity2.this);
                                            builder.setTitle("Are You Sure?");
                                            builder.setMessage("If you received an email with a PIN, we strongly recommend you enter it so that we can verify your identity properly.");

                                            final EditText input = new EditText(SignInActivity2.this);

                                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                            builder.setView(input);

                                            builder.setPositiveButton("Enter PIN", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    try {
                                                        List<ParseObject> speakerList = ParseQuery.getQuery("Speaker").whereEqualTo("IOS_code", input.getText().toString()).find();
                                                        List<ParseObject> attendeeList = ParseQuery.getQuery("Attendee").whereEqualTo("IOS_code", input.getText().toString()).find();

                                                        boolean bUserLinked = false;

                                                        if (speakerList.size() > 0) {
                                                            ParseObject speakerObj = speakerList.get(0);
                                                            speakerObj.put("UserLink", finalUser.getObjectId());
                                                            speakerObj.save();
                                                            bUserLinked = true;
                                                        } else if (attendeeList.size() > 0) {
                                                            ParseObject attendeeObj = attendeeList.get(0);
                                                            attendeeObj.put("UserLink", finalUser.getObjectId());
                                                            attendeeObj.save();
                                                            bUserLinked = true;
                                                        }

                                                        if (bUserLinked == true) {
                                                            AppUtil.addConferenceForPinPromptEntered(SignInActivity2.this, OConnectBaseActivity.selectedConference.getObjectId());
                                                            SignInActivity2.this.finish();
                                                            Intent i = new Intent(SignInActivity2.this, DashboardActivity.class);
                                                            startActivity(i);
                                                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up );
                                                        } else {
                                                            AlertDialog alertDialog = new AlertDialog.Builder(SignInActivity2.this).create();
                                                            alertDialog.setTitle("Error");
                                                            alertDialog.setMessage("Incorrect PIN.  Please try again when logging in.");
                                                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                                    new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            SignInActivity2.this.finish();
                                                                            Intent i = new Intent(SignInActivity2.this, DashboardActivity.class);
                                                                            startActivity(i);
                                                                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up );
                                                                        }
                                                                    });
                                                            alertDialog.show();
                                                        }
                                                    } catch (Exception ex) {
                                                        Log.d("SignIn2", ex.getMessage());
                                                    }
                                                }
                                            });
                                            builder.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    AppUtil.addConferenceForPinPromptSkipped(SignInActivity2.this, AppUtil.getSelectedConferenceID(SignInActivity2.this));
                                                    Intent i = new Intent(SignInActivity2.this, DashboardActivity.class);
                                                    startActivity(i);
                                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up );
                                                }
                                            });

                                            builder.create().show();

                                        }
                                    });

                                    AlertDialog dialog = builder.create();

                                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                                    dialog.show();
                                } else {
                                    Intent i = new Intent(SignInActivity2.this, DashboardActivity.class);
                                    startActivity(i);
                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up );
                                }
                            } catch (Exception ex) {
                                Log.d("SignIn2", ex.getMessage());
                            }

                            // SignInActivity2.this.finish();
                            // overridePendingTransition(R.anim.hold, R.anim.disappear_to_bottom);
                        } else {
                            dialog.dismiss();

                            AlertDialog alertDialog = new AlertDialog.Builder(SignInActivity2.this).create();
                            alertDialog.setTitle("Error");
                            alertDialog.setMessage("Incorrect username or password.  Please try again.");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    }
                });
    }

    private class ImportTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {

            // params comes from the execute() call: params[0] is the url.
            try {
                HTTPPostHandler httpPostHandler = new HTTPPostHandler();

                return httpPostHandler.performTwitterImport(SignInActivity2.this,"","","","");
            } catch (Exception e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("IMPORT RESULT", result);

            /*
            try {

            } catch (JSONException e) {
                e.printStackTrace();
            }
            */
        }
    }
}
