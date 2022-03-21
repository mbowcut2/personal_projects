package com.example.familymapclient.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.familymapclient.R;
import com.example.familymapclient.utils.DataCache;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.List;

import model.Event;
import model.Person;

public class SearchActivity extends AppCompatActivity {

    private static final int PERSON_ITEM_VIEW_TYPE = 0;
    private static final int EVENT_ITEM_VIEW_TYPE = 1;
    private DataCache dataCache = DataCache.getInstance();
    private String searchString = null;
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editText = findViewById(R.id.searchString);
        editText.addTextChangedListener(watcher);

    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            searchString = editText.getText().toString();

            RecyclerView recyclerView = findViewById(R.id.RecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

            List<Person> people = dataCache.searchPersons(searchString);
            List<Event> events = dataCache.searchEvents(searchString);

            SearchAdapter adapter = new SearchAdapter(people, events);
            recyclerView.setAdapter(adapter);


        }

        @Override
        public void afterTextChanged(Editable s) {



        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        return true;
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final List<Person> people;
        private final List<Event> events;

        SearchAdapter(List<Person> people, List<Event> events) {
            this.people = people;
            this.events = events;
        }

        @Override
        public int getItemViewType(int position) {
            return position < people.size() ? PERSON_ITEM_VIEW_TYPE : EVENT_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if(viewType == PERSON_ITEM_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            } else {
                view = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            }

            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if(position < people.size()) {
                holder.bind(people.get(position));
            } else {
                holder.bind(events.get(position - people.size()));
            }
        }

        @Override
        public int getItemCount() {
            return people.size() + events.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView topField;
        private final TextView bottomField;
        private final ImageView icon;

        private final int viewType;
        private Person person;
        private Event event;

        SearchViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);


            if(viewType == PERSON_ITEM_VIEW_TYPE
            ) {
                topField = itemView.findViewById(R.id.topField);
                bottomField = itemView.findViewById(R.id.bottomField);
                icon = itemView.findViewById(R.id.itemImage);
            } else {
                topField = itemView.findViewById(R.id.topField);
                bottomField = itemView.findViewById(R.id.bottomField);
                icon = itemView.findViewById(R.id.itemImage);
            }
        }

        private void bind(Person person) {
            this.person = person;
            topField.setText(person.getFirstName() + " " + person.getLastName());
            bottomField.setText(null);
            BitmapDrawable myDrawable;

            if(person.getGender().equals("f")) {
                Bitmap girl = BitmapFactory.decodeResource(getResources(), R.drawable.girl);
                myDrawable = new BitmapDrawable(getResources(), girl);
            }
            else {
                Bitmap boy = BitmapFactory.decodeResource(getResources(), R.drawable.boy);
                myDrawable = new BitmapDrawable(getResources(), boy);
            }

            icon.setImageDrawable(myDrawable);

            //TODO: set image to male or female
        }

        private void bind(Event event) {
            this.event = event;
            Person pers = DataCache.getInstance().getPersonById(event.getPersonID());
            topField.setText(event.getEventType() + ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")");
            bottomField.setText(pers.getFirstName() + " " + pers.getLastName());
            icon.setImageDrawable(new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.black));

        }

        @Override
        public void onClick(View view) {
            if(viewType == PERSON_ITEM_VIEW_TYPE) {

                dataCache.setSelectedPerson(person);
                Intent intent = new Intent(SearchActivity.this, PersonActivity.class);
                startActivity(intent);

            } else {

                dataCache.setSelectedEvent(event);
                Intent intent = new Intent(SearchActivity.this, EventActivity.class);
                startActivity(intent);

            }
        }
    }
}