package com.example.familymapclient.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.example.familymapclient.R;
import com.example.familymapclient.tasks.GetEventsTask;
import com.example.familymapclient.tasks.GetPeopleTask;
import com.example.familymapclient.tasks.LoginTask;
import com.example.familymapclient.tasks.RegisterTask;
import com.example.familymapclient.utils.DataCache;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        setContentView(R.layout.activity_main);



        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);


        if(fragment == null) {
            fragment = createLoginFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        } else {
            if(fragment instanceof LoginFragment) {
                ((LoginFragment) fragment).registerListener(this);
            }
        }
    }

    private Fragment createLoginFragment() {
        LoginFragment fragment = new LoginFragment();
        fragment.registerListener(this);
        return fragment;
    }

    @Override
    public void login(String serverHost, String serverPort, String username, String password) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = new MapFragment();
        final boolean[] success = new boolean[1];


        @SuppressLint("HandlerLeak") Handler uiThreadMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Bundle bundle = message.getData();
                String authtoken = bundle.getString("AUTHTOKEN", null);
                success[0] = bundle.getBoolean("SUCCESS", false);
                String personID = bundle.getString("PERSONID", null);

                if (success[0]) {
                    //create a background task that gets the first/last name, gotta have its own handler --> call toast.
                    //get the result, put it in the DataCache, look up the id in the people, get first/last name.
                    Handler downloadThreadMessageHandler = new Handler() {
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            Bundle bdl = msg.getData();
                            success[0] = bdl.getBoolean("SUCCESS", false);
                            if (success[0]) {
                                String firstName = bdl.getString("FIRST_NAME", "ERROR FIRST_NAME NOT FOUND");
                                String lastName = bdl.getString("LAST_NAME", "ERROR: LAST_NAME NOT FOUND");

                                postToastMessage("Welcome " + firstName + " " + lastName);

                                //drawMap(fragment);

                                MapFragment fragment = new MapFragment();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.fragment_container, fragment)
                                        .commit();

                                GetEventsTask task = new GetEventsTask(serverHost, serverPort, DataCache.getInstance().getAuthtoken());
                                ExecutorService executor = Executors.newSingleThreadExecutor();
                                executor.submit(task);

                            }
                            else {
                                postToastMessage("Ya done messed up A-aron");
                            }
                        }
                    };
                    GetPeopleTask task = new GetPeopleTask(downloadThreadMessageHandler, serverHost, serverPort, DataCache.getInstance().getAuthtoken(), personID);
                    ExecutorService executor2 = Executors.newSingleThreadExecutor();
                    executor2.submit(task);
                }

                else {
                    postToastMessage("Houston, we have a problem.");
                }
            }
        };

        LoginTask task = new LoginTask(uiThreadMessageHandler, serverHost, serverPort, username, password);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);
    }

    @Override
    public void register(
            String serverHost,
            String serverPort,
            String username,
            String password,
            String firstName,
            String lastName,
            String email,
            String gender) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        final boolean[] success = new boolean[1];


        @SuppressLint("HandlerLeak") Handler uiThreadMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Bundle bundle = message.getData();
                success[0] = bundle.getBoolean("SUCCESS", false);
                String personID = bundle.getString("PERSONID", null);

                if (success[0]) {
                    Handler personThreadMessageHandler = new Handler() {
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            Bundle bdl = msg.getData();
                            success[0] = bdl.getBoolean("SUCCESS", false);
                            if (success[0]) {
                                String firstName = bdl.getString("FIRST_NAME", "ERROR FIRST_NAME NOT FOUND");
                                String lastName = bdl.getString("LAST_NAME", "ERROR: LAST_NAME NOT FOUND");

                                postToastMessage("Welcome " + firstName + " " + lastName);

                                MapFragment fragment = new MapFragment();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.fragment_container, fragment)
                                        .commit();

                                GetEventsTask task = new GetEventsTask(serverHost, serverPort, DataCache.getInstance().getAuthtoken());
                                ExecutorService executor = Executors.newSingleThreadExecutor();
                                executor.submit(task);

                            }
                            else {
                                postToastMessage("Ya done messed up A-aron");
                            }
                        }
                    };
                    GetPeopleTask task = new GetPeopleTask(personThreadMessageHandler, serverHost, serverPort, DataCache.getInstance().getAuthtoken(), personID);
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(task);
                }

                else {
                    postToastMessage("Registration failed");
                }
            }
        };

        RegisterTask task = new RegisterTask(uiThreadMessageHandler, serverHost, serverPort, username, password, firstName, lastName, email, gender);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);
    }



    public void postToastMessage(final String message) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}