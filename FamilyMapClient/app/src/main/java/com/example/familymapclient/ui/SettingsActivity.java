package com.example.familymapclient.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familymapclient.R;
import com.example.familymapclient.utils.DataCache;

public class SettingsActivity extends AppCompatActivity {

    private DataCache dataCache = DataCache.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch lifeStorySwitch = findViewById(R.id.lifeStorySwitch);
        Switch familyTreeSwitch = findViewById(R.id.familyTreeSwitch);
        Switch spouseLinesSwitch = findViewById(R.id.spouseLinesSwitch);
        Switch fatherSideSwitch = findViewById(R.id.fatherSideSwitch);
        Switch motherSideSwitch = findViewById(R.id.motherSideSwitch);
        Switch maleSwitch = findViewById(R.id.maleEventsSwitch);
        Switch femaleSwitch = findViewById(R.id.femaleEventsSwitch);
        TextView logOut = findViewById(R.id.logOut);

        lifeStorySwitch.setChecked(dataCache.isLifeStory());
        familyTreeSwitch.setChecked(dataCache.isFamilyTree());
        spouseLinesSwitch.setChecked(dataCache.isSpouse());
        fatherSideSwitch.setChecked(dataCache.isFatherSide());
        motherSideSwitch.setChecked(dataCache.isMotherSide());
        maleSwitch.setChecked(dataCache.isMale());
        femaleSwitch.setChecked(dataCache.isFemale());

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this,"Logged Out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });



        lifeStorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                dataCache.setLifeStory(isChecked);

            }
        });


        familyTreeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                dataCache.setFamilyTree(isChecked);

            }
        });


        spouseLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                dataCache.setSpouse(isChecked);

            }
        });


        fatherSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                dataCache.setFatherSide(isChecked);
                dataCache.setFilters();

            }
        });


        motherSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                dataCache.setMotherSide(isChecked);
                dataCache.setFilters();

            }
        });


        maleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                dataCache.setMale(isChecked);
                dataCache.setFilters();

            }
        });


        femaleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                dataCache.setFemale(isChecked);
                dataCache.setFilters();

            }
        });
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
}