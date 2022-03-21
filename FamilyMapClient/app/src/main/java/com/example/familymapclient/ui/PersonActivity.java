package com.example.familymapclient.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familymapclient.R;
import com.example.familymapclient.utils.DataCache;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import model.Event;
import model.Person;

public class PersonActivity extends AppCompatActivity {

    /**
     * You get here by tapping the bottom panel (bottom linear layout)
     */

    private Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        person = DataCache.getInstance().getSelectedPerson();

        TextView firstNameField = findViewById(R.id.firstNameField);
        firstNameField.setText(person.getFirstName());

        TextView lastNameField = findViewById(R.id.lastNameField);
        lastNameField.setText(person.getLastName());

        TextView gender = findViewById(R.id.genderField);
        if (person.getGender().equals("m")) {
            gender.setText("Male");
        } else {
            gender.setText("Female");
        }

        //TODO: wrong selectedPerson after personActivity

        LinkedList<Person> people = new LinkedList<>();
        LinkedList<Event> events = new LinkedList<>();

        //TODO: modify this to reflect settings

        people.addAll(DataCache.getInstance().getImmediateFamily());
        DataCache.getInstance().setPersonEvents();
        events.addAll(DataCache.getInstance().getEventsByID(person.getPersonID()));

        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                if(o1.getYear() < o2.getYear()) {
                    return -1;
                }
                if(o1.getYear() > o2.getYear()) {
                    return 1;
                }
                else { return 0; }
            }
        });


        expandableListView.setAdapter(new ExpandableListAdapter(people, events));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.person_activity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        return true;
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int EVENTS_GROUP_POSITION = 0;
        private static final int PEOPLE_GROUP_POSITION = 1;

        private final List<Person> people;
        private final List<Event> events;

        private DataCache dataCache = DataCache.getInstance();

        ExpandableListAdapter(List<Person> people, List<Event> events) {
            this.people = people;
            this.events = events;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case EVENTS_GROUP_POSITION:
                    return events.size();
                case PEOPLE_GROUP_POSITION:
                    return people.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case EVENTS_GROUP_POSITION:
                    return getString(R.string.eventsGroupTitle);
                case PEOPLE_GROUP_POSITION:
                    return getString(R.string.peopleGroupTitle);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case EVENTS_GROUP_POSITION:
                    return events.get(childPosition);
                case PEOPLE_GROUP_POSITION:
                    return people.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case EVENTS_GROUP_POSITION:
                    titleView.setText(R.string.eventsGroupTitle);
                    break;
                case PEOPLE_GROUP_POSITION:
                    titleView.setText(R.string.peopleGroupTitle);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch (groupPosition) {
                case EVENTS_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;
                case PEOPLE_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                    initializePersonView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }


        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }


        private void initializeEventView(View listItemView, final int childPosition) {
            Event event = events.get(childPosition);
            String details = event.getEventType() + ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";
            TextView eventDetails = listItemView.findViewById(R.id.topField);
            ImageView imageView = listItemView.findViewById(R.id.itemImage);
            eventDetails.setText(details);

            TextView eventPerson = listItemView.findViewById(R.id.bottomField);
            eventPerson.setText(
                    dataCache.getSelectedPerson().getFirstName() +
                    " " +
                    dataCache.getSelectedPerson().getLastName());

            imageView.setImageDrawable(new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.black));

            listItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PersonActivity.this, getString(R.string.eventItemToastText, events.get(childPosition).getEventType()), Toast.LENGTH_SHORT).show();
                    dataCache.setSelectedEvent(event);
                    Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                    startActivity(intent);
                }
            });
        }


        private void initializePersonView(View listItemView, final int childPosition) {
            Person person = people.get(childPosition);
            String details = person.getFirstName() + " " + person.getLastName();
            TextView eventDetails = listItemView.findViewById(R.id.topField);
            eventDetails.setText(details);

            TextView relationship = listItemView.findViewById(R.id.bottomField);

            if(person.getPersonID().equals(dataCache.getSelectedPerson().getSpouseID())) {
                relationship.setText("Spouse");
            }
            else if(person.getPersonID().equals(dataCache.getSelectedPerson().getFatherID())) {
                relationship.setText("Father");
            }
            else if(person.getPersonID().equals(dataCache.getSelectedPerson().getMotherID())) {
                relationship.setText("Mother");
            }
            else {
                relationship.setText("Child");
            }

            ImageView itemImage = listItemView.findViewById(R.id.itemImage);

            BitmapDrawable myDrawable;

            if(person.getGender().equals("f")) {
                Bitmap girl = BitmapFactory.decodeResource(getResources(), R.drawable.girl);
                myDrawable = new BitmapDrawable(getResources(),girl);
            }
            else {
                Bitmap boy = BitmapFactory.decodeResource(getResources(), R.drawable.boy);
                myDrawable = new BitmapDrawable(getResources(), boy);
            }

            //textView.setCompoundDrawables(myDrawable, null, null, null);
            itemImage.setImageDrawable(myDrawable);

            listItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PersonActivity.this, getString(R.string.personItemToastText, details), Toast.LENGTH_SHORT).show();
                    dataCache.setSelectedPerson(person);
                    Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                    startActivity(intent);
                }
            });
        }

    }


}
