package com.ointerface.oconnect.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.ointerface.oconnect.ConferenceListViewActivity;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.data.CloudUtil;
import com.ointerface.oconnect.data.Person;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.R.attr.data;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class CreateAccountActivity extends AppCompatActivity {

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

    private int SELECT_PICTURE = 10;
    private int TAKE_PICTURE = 11;

    private ParseUser currentUser = null;

    private boolean isProfilePictureTaken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etOrganization = (EditText) findViewById(R.id.etOrganization);
        etJobTitle = (EditText) findViewById(R.id.etJobTitle);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etInterests = (EditText) findViewById(R.id.etInterests);
        etContactEmail = (EditText) findViewById(R.id.etContactEmail);
        switchContactable = (Switch) findViewById(R.id.switchContactable);

    }

    public void signUpClicked(View view) {
        if (etFirstName.getText().toString().equalsIgnoreCase("") ||
                etLastName.getText().toString().equalsIgnoreCase("") ||
                etContactEmail.getText().toString().equalsIgnoreCase("") ||
                etPassword.getText().toString().equalsIgnoreCase("")) {
            AlertDialog alertDialog = new AlertDialog.Builder(CreateAccountActivity.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("All fields are required.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return;
        }

        final ParseUser newUser = new ParseUser();
        newUser.setUsername(etContactEmail.getText().toString());
        newUser.setEmail(etContactEmail.getText().toString());
        newUser.setPassword(etPassword.getText().toString());

        currentUser = newUser;

        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    CloudUtil.setPersonToUserRole(newUser);
                    saveUserDetails(newUser);
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(CreateAccountActivity.this).create();
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

    public void cancelClicked (View view) {
        CreateAccountActivity.this.finish();
        overridePendingTransition(R.anim.hold, R.anim.disappear_to_bottom);
    }

    public void editProfilePhotoClicked (View view) {
        selectProfileImage();
    }

    public void saveUserDetails(ParseUser user) {
        user.put("firstName", etFirstName.getText().toString());
        user.put("lastName", etLastName.getText().toString());
        user.put("contact_email", etContactEmail.getText().toString());
        user.put("isContactable", switchContactable.isChecked());
        user.put("job", etJobTitle.getText().toString());
        user.put("org", etOrganization.getText().toString());
        user.put("location", etLocation.getText().toString());
        user.put("Interests", etInterests.getText().toString());
        user.put("userType", "app");
        user.put("password", etPassword.getText().toString());

        if (isProfilePictureTaken == true) {
            ivProfile.setDrawingCacheEnabled(true);
            ivProfile.buildDrawingCache();
            Bitmap bm = ivProfile.getDrawingCache();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();


            byte[] data = byteArray;

            ParseFile image1 = new ParseFile(user.getObjectId() + ".png", data);

            try {
                image1.save();
            } catch (Exception ex) {
                Log.d("APD", ex.getMessage());
            }

            user.put("pictureURL", image1.getUrl());
        }

        try {
            user.save();
            OConnectBaseActivity.currentPerson = Person.saveFromParseUser(user, false);
        } catch (Exception ex) {
            Log.d("CreateAccount", ex.getMessage());
        }

        CreateAccountActivity.this.finish();
        overridePendingTransition(R.anim.hold, R.anim.disappear_to_bottom);
    }

    private void selectProfileImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };


        AlertDialog.Builder builder = new AlertDialog.Builder(
                CreateAccountActivity.this);



        // builder.setCustomTitle(title);

        // builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    String[] permission = {"android.permission.CAMERA"};

                    if (ContextCompat.checkSelfPermission(CreateAccountActivity.this,
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CreateAccountActivity.this,
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
