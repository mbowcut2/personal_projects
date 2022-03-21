package com.example.familymapclient.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.familymapclient.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Create the LoginTask, pass to executor.
 * use TextWatcher to grab data from form
 */
public class LoginFragment extends Fragment {

    private View view;
    private Listener listener;
    private EditText serverHost;
    private String serverHostString = "";
    private EditText serverPort;
    private String serverPortString = "";
    private EditText username;
    private String usernameString = "";
    private EditText password;
    private String passwordString = "";
    private EditText firstName;
    private String firstNameString = "";
    private EditText lastName;
    private String lastNameString = "";
    private EditText email;
    private String emailString = "";
    private RadioGroup gender;
    private String genderString = "";

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            checkFieldsForEmptyValues();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            serverHostString = serverHost.getText().toString();
            serverPortString = serverPort.getText().toString();
            usernameString = username.getText().toString();
            passwordString = password.getText().toString();
            firstNameString = firstName.getText().toString();
            lastNameString = lastName.getText().toString();
            emailString = email.getText().toString();
            checkFieldsForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable s) {
            checkFieldsForEmptyValues();
        }
    };

    void checkFieldsForEmptyValues(){
        Button loginButton = (Button) view.findViewById(R.id.loginButton);

        if(usernameString.equals("")|| passwordString.equals("")|| serverHostString.equals("")|| serverPortString.equals("")){
            loginButton.setEnabled(false);
        } else {
            loginButton.setEnabled(true);
        }

        Button registerButton = (Button) view.findViewById(R.id.registerButton);

        if(
                usernameString.equals("") ||
                passwordString.equals("") ||
                serverHostString.equals("") ||
                serverPortString.equals("") ||
                firstNameString.equals("") ||
                lastNameString.equals("") ||
                emailString.equals("") ||
                genderString.equals("")
        ){
            registerButton.setEnabled(false);
        } else {
            registerButton.setEnabled(true);
        }
    }


    public interface Listener {
        void login(String serverHost, String serverPort, String username, String password);
        void register(
                String serverHost,
                String serverPort,
                String username,
                String password,
                String firstName,
                String lastName,
                String email,
                String gender
        );
    }
    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);

        checkFieldsForEmptyValues();

        serverHost = view.findViewById(R.id.serverHostField);
        serverHost.addTextChangedListener(watcher);
        serverPort = view.findViewById(R.id.serverPortField);
        serverPort.addTextChangedListener(watcher);
        username = view.findViewById(R.id.usernameField);
        username.addTextChangedListener(watcher);
        password = view.findViewById(R.id.passwordField);
        password.addTextChangedListener(watcher);
        firstName = view.findViewById(R.id.firstNameField);
        firstName.addTextChangedListener(watcher);
        lastName = view.findViewById(R.id.lastNameField);
        lastName.addTextChangedListener(watcher);
        email = view.findViewById(R.id.emailField);
        email.addTextChangedListener(watcher);
        gender = view.findViewById(R.id.gender);

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case (R.id.male) :
                        genderString = "m";
                        break;
                    case (R.id.female) :
                        genderString = "f";

                }
                checkFieldsForEmptyValues();
            }
        });




        Button loginButton = view.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {

                    System.out.println(
                            serverHostString + "\n" +
                                    serverPortString + "\n" +
                                    usernameString + "\n" +
                                    passwordString
                    );

                    listener.login(serverHostString, serverPortString, usernameString, passwordString);
                }
            }
        });

        Button registerButton = view.findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {

                    System.out.println(serverHostString + "\n" +
                            serverPortString + "\n" +
                            usernameString + "\n" +
                            passwordString + "\n" +
                            firstNameString + "\n" +
                            lastNameString + "\n" +
                            emailString + "\n" +
                            genderString);

                    listener.register(
                            serverHostString,
                            serverPortString,
                            usernameString,
                            passwordString,
                            firstNameString,
                            lastNameString,
                            emailString,
                            genderString
                    );
                }

            }
        });
        return view;
    }

}