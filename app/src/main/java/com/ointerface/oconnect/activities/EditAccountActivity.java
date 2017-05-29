package com.ointerface.oconnect.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.util.AppConfig;
import com.ointerface.oconnect.util.AppUtil;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import io.realm.Realm;

import static android.view.View.GONE;

public class EditAccountActivity extends OConnectBaseActivity {

    private ImageView ivProfile;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etPassword;
    private EditText etOrganization;
    private EditText etJobTitle;
    private EditText etLocation;
    private EditText etInterests;
    private EditText etContactEmail;
    private Switch switchContactable;

    private EditText etBio;
    private Switch switchConfAttended;
    private Switch switchTwitter;
    private Switch switchFacebook;
    private Switch switchLinkedIn;

    private int SELECT_PICTURE = 10;
    private int TAKE_PICTURE = 11;

    private ParseUser currentUser = null;

    // private Person currentPerson = null;

    private boolean isProfilePictureTaken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        super.onCreateDrawer();

        tvToolbarTitle.setText("Edit Profile");

        ivProfileLanyard.setVisibility(GONE);
        ivHelp.setVisibility(GONE);
        ivRightToolbarIcon.setVisibility(GONE);
        ivSearch.setVisibility(GONE);

        tvHeaderBack.setVisibility(View.VISIBLE);
        ivHeaderBack.setVisibility(GONE);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Drawable dr = getResources().getDrawable(R.drawable.icon_back);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();

        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, AppUtil.convertDPToPXInt(EditAccountActivity.this, 20), AppUtil.convertDPToPXInt(EditAccountActivity.this, 20), true));

        getSupportActionBar().setHomeAsUpIndicator(d);

        tvHeaderBack.setTextSize(22);
        tvHeaderBack.setText("X");
        tvHeaderBack.setTextColor(AppConfig.whiteColor);

        tvHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditAccountActivity.this.finish();
                overridePendingTransition(R.anim.hold, R.anim.disappear_to_bottom);
            }
        });

        tvEdit.setTextSize(18);
        tvEdit.setVisibility(View.VISIBLE);
        tvEdit.setTextColor(AppConfig.whiteColor);
        tvEdit.setText("Save");

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etOrganization = (EditText) findViewById(R.id.etOrganization);
        etJobTitle = (EditText) findViewById(R.id.etJobTitle);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etInterests = (EditText) findViewById(R.id.etInterests);
        etContactEmail = (EditText) findViewById(R.id.etContactEmail);
        etBio = (EditText) findViewById(R.id.etBio);
        switchConfAttended = (Switch) findViewById(R.id.switchConfAttended);
        switchTwitter = (Switch) findViewById(R.id.switchTwitter);
        switchFacebook = (Switch) findViewById(R.id.switchFacebook);
        switchLinkedIn = (Switch) findViewById(R.id.switchLinkedIn);

        if (currentPerson != null) {
            if (currentPerson.getPictureURL() != null && !currentPerson.getPictureURL().equalsIgnoreCase("")) {
                final String pictureURL = currentPerson.getPictureURL();

                new AsyncTask<Void, Void, Void>() {
                    public Bitmap bmp;
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            InputStream in = new URL(pictureURL).openStream();
                            bmp = BitmapFactory.decodeStream(in);
                        } catch (Exception e) {
                            Log.d("APD", e.getMessage());
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        if (bmp != null) {
                            ivProfile.setImageBitmap(bmp);
                        }
                    }

                }.execute();
            }

            etFirstName.setText(currentPerson.getFirstName());
            etLastName.setText(currentPerson.getLastName());
            etPassword.setText(currentPerson.getPassword());
            etOrganization.setText(currentPerson.getOrg());
            etJobTitle.setText(currentPerson.getJob());
            etLocation.setText(currentPerson.getLocation());
            etInterests.setText(currentPerson.getInterests());
            etBio.setText(currentPerson.getBio());
            etContactEmail.setText(currentPerson.getContact_email());

            ImageView ivTwitter = (ImageView) findViewById(R.id.ivTwitter);
            ivTwitter.setBackground(AppUtil.changeDrawableColor(this, R.drawable.twitter_icon, AppUtil.getPrimaryThemColorAsInt()));
            ImageView ivFacebook = (ImageView) findViewById(R.id.ivFacebook);
            ivFacebook.setBackground(AppUtil.changeDrawableColor(this, R.drawable.social_facebook, AppUtil.getPrimaryThemColorAsInt()));
            ImageView ivLinkedIn = (ImageView) findViewById(R.id.ivLinkedIn);
            ivLinkedIn.setBackground(AppUtil.changeDrawableColor(this, R.drawable.social_linkedin, AppUtil.getPrimaryThemColorAsInt()));
        }
    }

    public void saveProfile() {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());
        realm.beginTransaction();

        currentPerson.setFirstName(etFirstName.getText().toString());
        currentPerson.setLastName(etLastName.getText().toString());
        currentPerson.setPassword(etPassword.getText().toString());
        currentPerson.setOrg(etOrganization.getText().toString());
        currentPerson.setJob(etJobTitle.getText().toString());
        currentPerson.setLocation(etLocation.getText().toString());
        currentPerson.setInterests(etInterests.getText().toString());
        currentPerson.setBio(etBio.getText().toString());
        currentPerson.setContact_email(etContactEmail.getText().toString());

        realm.commitTransaction();
        realm.close();

        try {
            currentUser = ParseUser.getQuery().get(currentPerson.getObjectId());

            if (currentUser != null) {
                currentUser.setEmail(etContactEmail.getText().toString());
                currentUser.setUsername(etContactEmail.getText().toString());
                currentUser.setPassword(etPassword.getText().toString());
                currentUser.put("contact_email", etContactEmail.getText().toString());
                currentUser.put("firstName", etFirstName.getText().toString());
                currentUser.put("lastName", etLastName.getText().toString());
                currentUser.put("job", etJobTitle.getText().toString());
                currentUser.put("org", etOrganization.getText().toString());
                currentUser.put("location", etLocation.getText().toString());
                currentUser.put("Interests", etInterests.getText().toString());
                currentUser.put("bio", etBio.getText().toString());
                currentUser.put("password", etPassword.getText().toString());

                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        EditAccountActivity.this.finish();
                        overridePendingTransition(R.anim.hold, R.anim.disappear_to_bottom);
                    }
                });
            }
        } catch (Exception ex) {
            Log.d("EditAccount", ex.getMessage());
        }
    }

    public void editProfilePhotoClicked (View view) {
        selectProfileImage();
    }

    private void selectProfileImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };


        AlertDialog.Builder builder = new AlertDialog.Builder(
                EditAccountActivity.this);

        // builder.setCustomTitle(title);

        // builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    String[] permission = {"android.permission.CAMERA"};

                    if (ContextCompat.checkSelfPermission(EditAccountActivity.this,
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EditAccountActivity.this,
                                permission, 10);
                    }else{
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, 0);
                    }

                } else if (items[item].equals("Choose from Library")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Bitmap thumbnail = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    ivProfile.setImageBitmap(thumbnail);
                    isProfilePictureTaken = true;
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();


                    Bitmap bmp = null;
                    try {
                        bmp = getBitmapFromUri(selectedImage);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    ivProfile.setImageBitmap(bmp);

                    isProfilePictureTaken = true;
                }
                break;
            case 10:
                if(resultCode == RESULT_OK) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                }
                break;
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

}
