package com.ointerface.oconnect.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ointerface.oconnect.App;
import com.ointerface.oconnect.R;
import com.ointerface.oconnect.adapters.ParticipantsSwipeListAdapter;
import com.ointerface.oconnect.data.Attendee;
import com.ointerface.oconnect.data.DataSyncManager;
import com.ointerface.oconnect.data.IDataSyncListener;
import com.ointerface.oconnect.data.Person;
import com.ointerface.oconnect.data.Speaker;
import com.ointerface.oconnect.fragments.OverlayDialogFragment;
import com.ointerface.oconnect.util.AppConfig;
import com.ointerface.oconnect.util.AppUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.view.View.GONE;

public class ParticipantsActivity extends OConnectBaseActivity implements IDataSyncListener {
    private ListView lvParticipantsList;
    private ParticipantsSwipeListAdapter adapter;

    private SearchView participantsSearch;

    private Button attendeeButton;

    private Button speakerButton;

    private LinearLayout bottomNavigation;

    public Speaker currentSpeaker = null;

    private boolean bIsSpeakerView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);
        super.onCreateDrawer();

        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);

        if (selectedConference.getToolbarLabelParticipants() != null &&
                !selectedConference.getToolbarLabelParticipants().equalsIgnoreCase("")) {
            tvToolbarTitle.setText(selectedConference.getToolbarLabelParticipants());
        } else {
            tvToolbarTitle.setText("Participants");
        }

        ivSearch.setVisibility(GONE);

        ivProfileLanyard.setVisibility(View.VISIBLE);

        ivProfileLanyard.setBackgroundResource(R.drawable.icon_header_calendar);

        ivProfileLanyard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ParticipantsActivity.this, ScheduleActivity.class);
                startActivity(i);
            }
        });

        initSearchView();

        ivConnections.setVisibility(View.VISIBLE);


        lvParticipantsList = (ListView) findViewById(R.id.lvParticipants);

        onDataSyncFinish();

        lvParticipantsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.showingSpeakers) {
                    SpeakerDetailViewActivity.mItems = new ArrayList<RealmObject>();

                    if (adapter.mSpeakers.size() < 11) {
                        SpeakerDetailViewActivity.mItems.addAll(adapter.mSpeakers);
                    } else {
                        int index = position;
                        for (int i = 0; i < 10 && index < adapter.mSpeakers.size(); ++i) {
                            SpeakerDetailViewActivity.mItems.add(adapter.mSpeakers.get(index++));
                        }
                    }

                    Intent i = new Intent(ParticipantsActivity.this, SpeakerDetailViewActivity.class);

                    if (adapter.mSpeakers.size() < 11) {
                        i.putExtra("SPEAKER_NUMBER", position);
                    } else {
                        i.putExtra("SPEAKER_NUMBER", 0);
                    }

                    // i.putExtra("SPEAKER_LIST", adapter.mSpeakers);
                    startActivity(i);
                } else {
                    AttendeeDetailViewActivity.mItems = new ArrayList<RealmObject>();

                    if (adapter.mUsers.size() < 11) {
                        AttendeeDetailViewActivity.mItems.addAll(adapter.mUsers);
                    } else {
                        int index = position;

                        int i = 0;

                        if (position < adapter.mUsers.size()) {
                            for (i = 0; i < 10 && index < adapter.mUsers.size(); ++i) {
                                AttendeeDetailViewActivity.mItems.add(adapter.mUsers.get(index++));
                            }
                        }
                    }

                    Intent i = new Intent(ParticipantsActivity.this, AttendeeDetailViewActivity.class);

                    if (adapter.mUsers.size() < 11) {
                        i.putExtra("ATTENDEE_NUMBER", position);
                    } else {
                        i.putExtra("ATTENDEE_NUMBER", 0);
                    }

                    // i.putExtra("ATTENDEE_LIST", adapter.mAttendees);
                    startActivity(i);
                }

            }
        });



        //show buttons at bottom
        bottomNavigation = (LinearLayout) findViewById(R.id.bottom_navigation);
        bottomNavigation.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());



        speakerButton = (Button) findViewById(R.id.navigation_speakers_button);
        attendeeButton = (Button) findViewById(R.id.navigation_attendees_button);

        speakerButton.setBackgroundResource(R.drawable.button_border_white);
        speakerButton.setTextColor(AppUtil.getPrimaryThemColorAsInt());
        attendeeButton.setBackgroundResource(R.drawable.button_border);

        speakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakerButton.setBackgroundResource(R.drawable.button_border_white);
                speakerButton.setTextColor(AppUtil.getPrimaryThemColorAsInt());
                attendeeButton.setBackgroundResource(R.drawable.button_border);
                attendeeButton.setTextColor(Color.WHITE);
                bIsSpeakerView = true;
                displaySpeakers();
            }
        });

        attendeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attendeeButton.setBackgroundResource(R.drawable.button_border_white);
                attendeeButton.setTextColor(AppUtil.getPrimaryThemColorAsInt());
                speakerButton.setBackgroundResource(R.drawable.button_border);
                speakerButton.setTextColor(Color.WHITE);
                bIsSpeakerView = false;
                displayAttendees();
            }
        });



        if (AppUtil.getParticipantsTutorialShown(this) == false && AppConfig.bParticipantsTutorialShown == false) {
            FragmentManager fm = getSupportFragmentManager();
            OverlayDialogFragment dialogFragment = OverlayDialogFragment.newInstance(this, OverlayDialogFragment.OverlayType.Partificpants1);
            dialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDialog);
            dialogFragment.show(fm, OverlayDialogFragment.OverlayType.Partificpants1.name());
            AppConfig.bParticipantsTutorialShown = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!DataSyncManager.listeners.contains(this)) {
            DataSyncManager.listeners.add(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DataSyncManager.listeners.contains(this)) {
            DataSyncManager.listeners.remove(this);
        }
    }

    public void onDataSyncFinish() {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        if (currentPerson != null) {
            Speaker result = realm.where(Speaker.class).equalTo("UserLink", currentPerson.getObjectId()).findFirst();

            if (result != null) {
                currentSpeaker = result;
            }
        }

        getListViewData();

        lvParticipantsList.setAdapter(adapter);
    }

    public void initSearchView() {
        participantsSearch = (SearchView) findViewById(R.id.participantSearch);

        participantsSearch.setBackgroundColor(AppUtil.getPrimaryThemColorAsInt());

        participantsSearch.setActivated(true);
        participantsSearch.setQueryHint("Search");

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        participantsSearch.setIconified(false);
        participantsSearch.clearFocus();

        participantsSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                participantsSearch.setIconified(false);
            }
        });

        participantsSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                participantsSearch.setIconified(false);
                return false;
            }
        });

        ImageView closeButton = (ImageView)participantsSearch.findViewById(R.id.search_close_btn);

        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.search_src_text);

                et.setText("");

                participantsSearch.clearFocus();

                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                bIsSpeakerView = adapter.showingSpeakers;

                getListViewData();

                adapter.showingSpeakers = bIsSpeakerView;

                lvParticipantsList.setAdapter(adapter);

            }
        });

        LinearLayout linearLayout1 = (LinearLayout) participantsSearch.getChildAt(0);
        LinearLayout linearLayout2 = (LinearLayout) linearLayout1.getChildAt(2);
        LinearLayout linearLayout3 = (LinearLayout) linearLayout2.getChildAt(1);
        AutoCompleteTextView autoComplete = (AutoCompleteTextView) linearLayout3.getChildAt(0);
        autoComplete.setTextSize(14);
        autoComplete.setBackground(getResources().getDrawable(R.drawable.search_view));

        autoComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                participantsSearch.setIconified(false);
            }
        });

        linearLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                participantsSearch.setIconified(false);
            }
        });

        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                participantsSearch.setIconified(false);
            }
        });

        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                participantsSearch.setIconified(false);
            }
        });

        participantsSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return false;
            }
        });
    }


    public void displaySpeakers() {
        getListViewData();

        adapter.showingSpeakers = true;

        lvParticipantsList.setAdapter(adapter);
    }

    public void displayAttendees() {
        getListViewData();

        adapter.showingSpeakers = false;

        lvParticipantsList.setAdapter(adapter);
    }

    public void getListViewData() {
        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        if (adapter != null) {
            bIsSpeakerView = adapter.showingSpeakers;
        }

        adapter = new ParticipantsSwipeListAdapter(this, this);

        adapter.showingSpeakers = bIsSpeakerView;

        RealmResults<Speaker> speakerResults;

        speakerResults = realm.where(Speaker.class).equalTo("conference", AppUtil.getSelectedConferenceID(ParticipantsActivity.this)).findAllSorted("name", Sort.ASCENDING);

        for (int i = 0; i < speakerResults.size(); ++i) {
            adapter.addSpeaker(speakerResults.get(i));
        }

        ArrayList<RealmObject> sortedUsers = new ArrayList<RealmObject>();

        RealmResults<Attendee> attendeeResults;

        attendeeResults = realm.where(Attendee.class).equalTo("conference", AppUtil.getSelectedConferenceID(ParticipantsActivity.this)).findAllSorted("name", Sort.ASCENDING);

        for (int i = 0; i < attendeeResults.size(); ++i) {
            sortedUsers.add(attendeeResults.get(i));
        }

        RealmList<Person> peopleList = OConnectBaseActivity.selectedConference.getPeople();

        if (peopleList != null) {
            for (int i = 0; i < peopleList.size(); ++i) {
                sortedUsers.add(peopleList.get(i));
            }
        }

        realm.beginTransaction();

        Collections.sort(sortedUsers, new SortUser());

        realm.commitTransaction();

        /*
        Collections.sort(sortedUsers, new Comparator<RealmObject>() {
            @Override
            public int compare(RealmObject o1, RealmObject o2) {
                Attendee attendee1 = null;
                Person person1 = null;
                Attendee attendee2 = null;
                Person person2 = null;

                if (o1 instanceof Attendee) {
                    attendee1 = (Attendee) o1;
                } else if (o1 instanceof Person) {
                    person1 = (Person) o1;
                }

                if (o2 instanceof Attendee) {
                    attendee2 = (Attendee) o2;
                } else if (o2 instanceof Person) {
                    person2 = (Person) o2;
                }

                if (attendee1 != null && attendee2 != null) {
                    if (attendee1.getName() == null || attendee2.getName() == null) {
                        return 0;
                    }

                    return attendee1.getName().compareTo(attendee2.getName());
                } else if (attendee1 != null && person2 != null) {
                    if (attendee1.getName() == null || person2.getFirstName() == null) {
                        return 0;
                    }

                    return attendee1.getName().compareTo(person2.getFirstName());
                } else if (person1 != null && person2 != null) {
                    if (person1.getFirstName() == null || person2.getFirstName() == null) {
                        return 0;
                    }

                    return person1.getFirstName().compareTo(person2.getFirstName());
                } else {
                    if (person1.getFirstName() == null || attendee2.getName() == null) {
                        return 0;
                    }

                    return person1.getFirstName().compareTo(attendee2.getName());
                }

            }
        });
        */

        adapter.mUsers = new ArrayList<RealmObject>();

        adapter.mUsers.addAll(sortedUsers);
    }

    public void performSearch(String searchText) {

        if (searchText == null || searchText.equalsIgnoreCase("")) {
            return;
        }

        searchText = searchText.toLowerCase();

        String[] searchArr = searchText.split(" ");

        Realm realm = AppUtil.getRealmInstance(App.getInstance());

        adapter = new ParticipantsSwipeListAdapter(this, this);

        RealmResults<Speaker> speakerResults;

        speakerResults = realm.where(Speaker.class).equalTo("conference", AppUtil.getSelectedConferenceID(ParticipantsActivity.this)).findAllSorted("name", Sort.ASCENDING);

        for (int i = 0; i < speakerResults.size(); ++i) {
            Speaker speaker = speakerResults.get(i);

            for (String value : searchArr) {
                if (speaker.getName().toLowerCase().contains(value)) {
                    adapter.addSpeaker(speaker);
                    break;
                }
            }
        }

        RealmResults<Attendee> attendeeResults;

        attendeeResults = realm.where(Attendee.class).equalTo("conference", AppUtil.getSelectedConferenceID(ParticipantsActivity.this)).findAllSorted("name", Sort.ASCENDING);

        for (int i = 0; i < attendeeResults.size(); ++i) {
            Attendee attendee = attendeeResults.get(i);

            for (String value : searchArr) {
                if (attendee.getName().toLowerCase().contains(value)) {
                    adapter.addUser(attendee);
                    break;
                }
            }
        }

        RealmList<Person> peopleList = OConnectBaseActivity.selectedConference.getPeople();

        if (peopleList != null) {
            for (int i = 0; i < peopleList.size(); ++i) {
                Person person = peopleList.get(i);

                for (String value : searchArr) {
                    if (person.getFirstName() != null && person.getLastName() != null) {
                        if (person.getFirstName().toLowerCase().contains(value) ||
                                person.getLastName().toLowerCase().contains(value)) {
                            adapter.addUser(person);
                            break;
                        }
                    }
                }
            }
        }

        adapter.showingSpeakers = bIsSpeakerView;

        lvParticipantsList.setAdapter(adapter);
    }

    public class SortUser implements Comparator<RealmObject> {
        @Override
        public int compare(RealmObject o1, RealmObject o2) {
            Attendee attendee1 = null;
            Person person1 = null;
            Attendee attendee2 = null;
            Person person2 = null;

            if (o1 instanceof Attendee) {
                attendee1 = (Attendee) o1;
            } else if (o1 instanceof Person) {
                person1 = (Person) o1;
            }

            if (o2 instanceof Attendee) {
                attendee2 = (Attendee) o2;
            } else if (o2 instanceof Person) {
                person2 = (Person) o2;
            }

            if (attendee1 != null && attendee2 != null) {
                if (attendee1.getName() == null && attendee2.getName() == null) {
                    return 0;
                } else if (attendee1.getName() != null && attendee2.getName() == null) {
                    return -1;
                } else if (attendee1.getImage() == null && attendee2.getName() != null) {
                    return 1;
                }

                // Log.d("APD", "Comparing: " + attendee1.getName() + " : " + attendee2.getName());

                return attendee1.getName().toLowerCase().compareTo(attendee2.getName().toLowerCase());
            } else if (attendee1 != null && person2 != null) {
                if (attendee1.getName() == null && person2.getFirstName() == null) {
                    return 0;
                } else if (attendee1.getName() != null && person2.getFirstName() == null) {
                    return -1;
                } else if (attendee1.getImage() == null && person2.getFirstName() != null) {
                    return 1;
                }

                Log.d("APD", "Comparing: " + attendee1.getName() + " : " + person2.getFirstName());

                return attendee1.getName().toLowerCase().compareTo(person2.getFirstName().toLowerCase());
            } else if (person1 != null && person2 != null) {
                if (person1.getFirstName() == null && person2.getFirstName() == null) {
                    return 0;
                } else if (person1.getFirstName() != null && person2.getFirstName() == null) {
                    return -1;
                } else if (person1.getFirstName() == null && person2.getFirstName() != null) {
                    return 1;
                }

                // Log.d("APD", "Comparing: " + person1.getFirstName() + " : " + person2.getFirstName());

                return person1.getFirstName().toLowerCase().compareTo(person2.getFirstName().toLowerCase());
            } else {
                if (person1.getFirstName() == null && attendee2.getName() == null) {
                    return 0;
                } else if (person1.getFirstName() != null && attendee2.getName() == null) {
                    return -1;
                } else if (person1.getFirstName() == null && attendee2.getName() != null) {
                    return 1;
                }

                Log.d("APD", "Comparing: " + person1.getFirstName() + " : " + attendee2.getName());

                return person1.getFirstName().toLowerCase().compareTo(attendee2.getName().toLowerCase());
            }
        }
    }
}
