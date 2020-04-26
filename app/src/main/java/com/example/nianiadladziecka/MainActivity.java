package com.example.nianiadladziecka;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.net.*;
import java.util.HashMap;

import helper.SQLiteHandler;
import helper.SessionManager;

public class MainActivity extends AppCompatActivity {
    private TextView txtName, txtTemp, txtHumi;
    private Button btnLogout, btnCamera, btnLullaby;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);


        txtName = (TextView) findViewById(R.id.name);
        txtHumi = (TextView) findViewById(R.id.textViewHumidity);
        txtTemp = (TextView) findViewById(R.id.textViewTemperature);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnLullaby = (Button) findViewById(R.id.btnLullaby);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");

        // Displaying the user details on the screen
        txtName.setText(name);

        // Fetching and displaying temperature and humidity
        HashMap<String,String> sensor = db.getSensorDetails();

        String temp = sensor.get("temperature");
        String humi = sensor.get("humidity");
        txtTemp.setText("Temperature: "+ temp);
        txtHumi.setText("Humidity: "+ humi);

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        CameraActivity.class);
                startActivity(i);
            }
        });
        btnLullaby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LullabyActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
