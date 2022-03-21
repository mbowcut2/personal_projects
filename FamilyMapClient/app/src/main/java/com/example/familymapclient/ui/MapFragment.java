package com.example.familymapclient.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.familymapclient.R;
import com.example.familymapclient.utils.DataCache;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.HashMap;
import java.util.LinkedList;

import model.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    private HashMap<String, Float> colorMap = new HashMap<>();
    private int colorIndex = 0;
    private TextView textView;
    private ImageView imageView;
    private LinearLayout personInfo;
    private DataCache dataCache = DataCache.getInstance();
    //private LinkedList<Polyline> lines= new LinkedList<>();

    //LINE COLOR CONSTANTS

    private final int FAMILY_TREE_LINE_COLOR = Color.parseColor("#000000"); //BLACK
    private final int SPOUSE_LINE_COLOR = Color.parseColor("#fb7373"); //RED
    private final int LIFE_STORY_LINE_COLOR = Color.parseColor("#40de83"); //GREEN
    private float WIDTH = 10f;

    //TODO: OnResume() redraw map for settings


    @Override
    public void onResume() {
        super.onResume();

        Person per = dataCache.getMapPerson();
        Event ev = dataCache.getMapEvent();

        if (map!=null) {

            for(Polyline line: dataCache.getLines()) {
                line.remove();
            }
            dataCache.clearLines();
            map.clear();
            if(per!=null) {
                textView.setText(
                        per.getFirstName() + " " + per.getLastName() + "\n" +  //TODO: need to grab the first / last name
                                ev.getEventType() + ": " + ev.getCity() + ", " +
                                ev.getCountry() + " (" + ((Integer) ev.getYear()).toString() + ")"
                );


                BitmapDrawable myDrawable;

                if (per.getGender().equals("f")) {
                    Bitmap girl = BitmapFactory.decodeResource(getResources(), R.drawable.girl);
                    myDrawable = new BitmapDrawable(getResources(), girl);
                } else {
                    Bitmap boy = BitmapFactory.decodeResource(getResources(), R.drawable.boy);
                    myDrawable = new BitmapDrawable(getResources(), boy);
                }

                //textView.setCompoundDrawables(myDrawable, null, null, null);
                imageView.setImageDrawable(myDrawable);

                //TODO: move camera to center on marker
                //TODO: draw lines
                personInfo.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        /**
                         * This should launch the person activity for the selected marker.
                         */

                        dataCache.setSelectedPerson(per);
                        Intent intent = new Intent(getActivity(), PersonActivity.class);
                        startActivity(intent);
                    }
                });

                drawRelationshipLines(ev, per);
            }



            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(Marker marker) {

                    for(Polyline line : dataCache.getLines()) {
                        line.remove();
                    }
                    dataCache.clearLines();

                    Event event = (Event) marker.getTag();
                    Person person = DataCache.getInstance().getPersonById(event.getPersonID());
                    dataCache.setSelectedPerson(person);
                    dataCache.setMapPerson(person);

                    dataCache.setSelectedEvent(event);
                    dataCache.setMapEvent(event);

                    dataCache.setPersonEvents();

                    LatLng latLng = new LatLng(event.getLatitude(), event.getLongitude());

                    map.moveCamera(CameraUpdateFactory.newLatLng(latLng));



                    textView.setText(
                            person.getFirstName() + " " + person.getLastName() + "\n" +  //TODO: need to grab the first / last name
                                    event.getEventType() + ": " + event.getCity() + ", " +
                                    event.getCountry() + " (" +  ((Integer) event.getYear()).toString() + ")"
                    );


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
                    imageView.setImageDrawable(myDrawable);

                    //TODO: move camera to center on marker
                    //TODO: draw lines
                    personInfo.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            /**
                             * This should launch the person activity for the selected marker.
                             */
                            Intent intent = new Intent(getActivity(), PersonActivity.class);
                            startActivity(intent);
                        }
                    });

                    drawRelationshipLines(event, person);
                    return true;
                }
            });

            LinkedList<Event> events = new LinkedList<Event>(DataCache.getInstance().getFilteredEvents());
            for (Event event : events) {
                addMarker(event);
            }

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_map, container, false);
        textView = (TextView) view.findViewById(R.id.mapTextView);
        imageView = view.findViewById(R.id.imageView);
        personInfo = view.findViewById(R.id.linearLayout);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);

        if(getActivity().getClass().equals(MainActivity.class)) {

            inflater.inflate(R.menu.main_map_menu, menu);

            MenuItem searchMenuItem = menu.findItem(R.id.searchMenuItem);
            searchMenuItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_search).colorRes(R.color.white).actionBarSize());

            searchMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    startActivity(intent);
                    return true;
                }
            });

            MenuItem settingsMenuItem = menu.findItem(R.id.settingsMenuItem);
            settingsMenuItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_gear).colorRes(R.color.white).actionBarSize());

            settingsMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(intent);
                    return true;
                }
            });
        }

        else {
            inflater.inflate(R.menu.person_activity_menu, menu);
        }

    }

    private void drawLine(Event startEvent, Event endEvent, int color, float width) {



        //Create start and end points for the line
        LatLng startPoint = new LatLng(startEvent.getLatitude(), startEvent.getLongitude());
        LatLng endPoint = new LatLng(endEvent.getLatitude(), endEvent.getLongitude());


        //Add line to map by specifying its endpoints, color and width

        PolylineOptions options = new PolylineOptions()
                .add(startPoint)
                .add(endPoint)
                .color(color)
                .width(width);

        Polyline line = map.addPolyline(options);

        dataCache.addLine(line);

    }

    public Event getBirthEvent(String parentID) {
        LinkedList<Event> events = dataCache.getEventsByID(parentID);


        int minYear = 3000;
        Event minEvent = null;

        for (Event event: events) {
            if(event.getYear() < minYear) {
                minYear = event.getYear();
                minEvent = event;
            }
            if(event.getEventType().equalsIgnoreCase("birth")) {
                return event;
            }
        }
        return minEvent;
    }

    public void drawFamilyTree(Person person, float width, boolean first) {


        if(person.getFatherID() == null && person.getMotherID() == null) {
            return;
        }

        if(first) {

            if(person.getFatherID() != null) {
                if(getBirthEvent(person.getFatherID())!=null) {
                    drawLine(dataCache.getSelectedEvent(), getBirthEvent(person.getFatherID()), FAMILY_TREE_LINE_COLOR, width);
                    drawFamilyTree(dataCache.getPersonById(person.getFatherID()), .25f * width, false);
                }
            }

            if(person.getMotherID() != null) {
                if(getBirthEvent(person.getMotherID())!=null) {
                    drawLine(dataCache.getSelectedEvent(), getBirthEvent(person.getMotherID()), FAMILY_TREE_LINE_COLOR, width);
                    drawFamilyTree(dataCache.getPersonById(person.getMotherID()), .25f * width, false);
                }
            }

        }

        else {

            if (person.getFatherID() != null) {
                if(getBirthEvent(person.getFatherID())!=null) {
                    drawLine(getBirthEvent(person.getPersonID()), getBirthEvent(person.getFatherID()), FAMILY_TREE_LINE_COLOR, width);
                    drawFamilyTree(dataCache.getPersonById(person.getFatherID()), .25f * width, false);
                }
            }

            if (person.getMotherID() != null) {
                if(getBirthEvent(person.getMotherID())!=null) {
                    drawLine(getBirthEvent(person.getPersonID()), getBirthEvent(person.getMotherID()), FAMILY_TREE_LINE_COLOR, width);
                    drawFamilyTree(dataCache.getPersonById(person.getMotherID()), .25f * width, false);
                }
            }

        }

    }

    public void drawSpouseLine(Person person, Event event) {
        if(getBirthEvent(person.getSpouseID())!=null) {
            drawLine(event, getBirthEvent(person.getSpouseID()), SPOUSE_LINE_COLOR, WIDTH);
        }
    }

    public void drawLifeStory(Person person) {

        LinkedList<Event> events = dataCache.getLifeStory();
        for (int i = 0; i < events.size() - 1; i++) {

            drawLine(events.get(i), events.get(i + 1), LIFE_STORY_LINE_COLOR, WIDTH);
        }

    }

    private void drawRelationshipLines(Event event, Person person) {

        dataCache.setFilters();
        dataCache.setPersonEvents();

        if(dataCache.isFamilyTree()) {
            drawFamilyTree(person, WIDTH, true);
        }

        if(dataCache.isSpouse()) {
            drawSpouseLine(person, event);
        }

        if(dataCache.isLifeStory()) {
            drawLifeStory(person);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {

                for(Polyline line : dataCache.getLines()) {
                    line.remove();
                }
                dataCache.clearLines();

                Event event = (Event) marker.getTag();
                Person person = DataCache.getInstance().getPersonById(event.getPersonID());
                dataCache.setSelectedPerson(person);
                dataCache.setMapPerson(person);

                dataCache.setSelectedEvent(event);
                dataCache.setMapEvent(event);

                dataCache.setPersonEvents();

                LatLng latLng = new LatLng(event.getLatitude(), event.getLongitude());

                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));



                textView.setText(
                        person.getFirstName() + " " + person.getLastName() + "\n" +  //TODO: need to grab the first / last name
                        event.getEventType() + ": " + event.getCity() + ", " +
                        event.getCountry() + " (" +  ((Integer) event.getYear()).toString() + ")"
                );


                BitmapDrawable myDrawable;

                if(person.getGender().equals("f")) {
                    Bitmap girl = BitmapFactory.decodeResource(getResources(), R.drawable.girl);
                    myDrawable = new BitmapDrawable(getResources(),girl);
                }
                else {
                    Bitmap boy = BitmapFactory.decodeResource(getResources(), R.drawable.boy);
                    myDrawable = new BitmapDrawable(getResources(), boy);
                }

                imageView.setImageDrawable(myDrawable);

                //TODO: move camera to center on marker
                //TODO: draw lines
                personInfo.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        /**
                         * This should launch the person activity for the selected marker.
                         */
                        Intent intent = new Intent(getActivity(), PersonActivity.class);
                        startActivity(intent);
                    }
                });

                drawRelationshipLines(event, person);
                return true;
            }
        });

        LinkedList<Event> events = new LinkedList<Event>(DataCache.getInstance().getFilteredEvents());
        for (Event event : events) {
            addMarker(event);
        }

        if(dataCache.getSelectedEvent() != null) {

            for(Polyline line : dataCache.getLines()) {
                line.remove();
            }
            dataCache.clearLines();

            Event event = dataCache.getSelectedEvent();
            Person person = DataCache.getInstance().getPersonById(event.getPersonID());
            dataCache.setSelectedPerson(person);
            dataCache.setPersonEvents();

            LatLng latLng = new LatLng(event.getLatitude(), event.getLongitude());

            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            drawRelationshipLines(event, person);

            textView.setText(
                    person.getFirstName() + " " + person.getLastName() + "\n" +
                            event.getEventType() + ": " + event.getCity() + ", " +
                            event.getCountry() + " (" +  ((Integer) event.getYear()).toString() + ")"
            );


            BitmapDrawable myDrawable;

            if(person.getGender().equals("f")) {
                Bitmap girl = BitmapFactory.decodeResource(getResources(), R.drawable.girl);
                myDrawable = new BitmapDrawable(getResources(),girl);
            }
            else {
                Bitmap boy = BitmapFactory.decodeResource(getResources(), R.drawable.boy);
                myDrawable = new BitmapDrawable(getResources(), boy);
            }



            imageView.setImageDrawable(myDrawable);

            //TODO: move camera to center on marker
            //TODO: draw lines


            personInfo.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    /**
                     * This should launch the person activity for the selected marker.
                     */
                    Intent intent = new Intent(getActivity(), PersonActivity.class);
                    startActivity(intent);


                }
            });


        }

    }

    @Override
    public void onMapLoaded() {
        // You probably don't need this callback. It occurs after onMapReady and I have seen
        // cases where you get an error when adding markers or otherwise interacting with the map in
        // onMapReady(...) because the map isn't really all the way ready. If you see that, just
        // move all code where you interact with the map (everything after
        // map.setOnMapLoadedCallback(...) above) to here.

    }

    private float getColor(Event event) {
        /**
         * This function will keep track of marker colors in colorMap, and return the color to be used for a given event.
         */

        LinkedList<Float> colorList = new LinkedList<>();
        colorList.add(BitmapDescriptorFactory.HUE_RED);
        colorList.add(BitmapDescriptorFactory.HUE_AZURE);
        colorList.add(BitmapDescriptorFactory.HUE_BLUE);
        colorList.add(BitmapDescriptorFactory.HUE_ORANGE);
        colorList.add(BitmapDescriptorFactory.HUE_ROSE);
        colorList.add(BitmapDescriptorFactory.HUE_CYAN);
        colorList.add(BitmapDescriptorFactory.HUE_GREEN);
        colorList.add(BitmapDescriptorFactory.HUE_MAGENTA);
        colorList.add(BitmapDescriptorFactory.HUE_VIOLET);
        colorList.add(BitmapDescriptorFactory.HUE_YELLOW);

        if (colorMap.containsKey(event.getEventType().toUpperCase())) {
            return colorMap.get(event.getEventType().toUpperCase());
        }

        else {
            colorMap.put(event.getEventType().toUpperCase(), colorList.get(colorIndex));
            DataCache.getInstance().setColorMap(colorMap);
            colorIndex++;
            if (colorIndex >= colorList.size()) {
                colorIndex = 0;
            }
            return colorMap.get(event.getEventType().toUpperCase());
        }

    }

    private void addMarker( Event event) {

        float googleColor = getColor(event);

        Marker marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(event.getLatitude(), event.getLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(googleColor)));

        marker.setTag(event);
    }

}