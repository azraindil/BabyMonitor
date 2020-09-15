package com.example.nianiadladziecka;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import helper.SQLiteHandler;
import helper.SessionManager;

public class MainActivity extends AppCompatActivity {
    private TextView txtName, txtTemp, txtHumi;
    private Button btnLogout, btnCamera, btnLullaby;
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private SQLiteHandler db;
    private SessionManager session;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        checkSensor("1");
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
        HashMap<String, String> sensor = db.getSensorDetails();

        String temp = sensor.get("temperature");
        String humi = sensor.get("humidity");

        txtTemp.setText("Temperature: " + temp + (char) 0x00B0 + "C");
        txtHumi.setText("Humidity: " + humi + "%");

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
                checkSensor("1");
            }
        });
        btnLullaby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LullabyActivity.class);
                startActivity(i);
                checkSensor("1");
            }
        });
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();

                        // Log and toast
                        Log.d(TAG, token);

                        new AsyncTask<Integer, Void, Void>() {
                            @Override
                            protected Void doInBackground(Integer... params) {
                                try {
                                    SShCommandSend.executeRemoteCommand("pi", "raspberry", AppConfig.IP_RPI, 22, "echo " + token + "> /home/pi/token.txt");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        }.execute(1);
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
        db.deleteSensor();
        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkSensor(final String iduser) {
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SENSOR, new Response.Listener<String>() {


            public void onResponse(String response) {
                new AsyncTask<Integer, Void, Void>() {
                    @Override
                    protected Void doInBackground(Integer... params) {
                        try {
                            db.deleteSensor();
                            SShCommandSend.executeRemoteCommand("pi", "raspberry", AppConfig.IP_RPI, 22, "python Adafruit_Python_DHT/examples/temperature.py 11 4");
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        return null;
                    }
                }.execute(1);
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        JSONObject sensor = jObj.getJSONObject("sensor");
                        String temperature = sensor.getString("temperature");
                        String humidity = sensor.getString("humidity");

                        // Inserting row in sensors table
                        db.addSensor("1", temperature, humidity);

                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), "error: " +
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to sensor url
                Map<String, String> params = new HashMap<String, String>();
                params.put("iduser", iduser);

                return params;
            }
        };
        // Adding request to request queue
        MySingleton.getInstance(this).addToRequestQueue(strReq);
    }
}