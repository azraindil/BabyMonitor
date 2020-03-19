package com.example.nianiadladziecka;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class LullabyActivity extends AppCompatActivity {
    Button btnStop,btnStart;
    Spinner spiLullaby;
SpinnerAdapter adapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_lullaby);

        btnStop = findViewById(R.id.btnStop);
        btnStart = findViewById(R.id.btnStart);
        spiLullaby = findViewById(R.id.spiLullaby);
        spiLullaby.setAdapter(adapter);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Integer, Void, Void>() {
                    @Override
                    protected Void doInBackground(Integer... params) {
                        try {
                            SShCommandSend.executeRemoteCommand("pi", "makova94", AppConfig.IP_RPI, 22, "omxplayer --no-keys -o local "+ spiLullaby.getSelectedItem());
                        }
                        catch (Exception e){
                            Toast.makeText(getApplicationContext(),"Error: "+ e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                        return null;
                    }
                }.execute(1);
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Integer, Void, Void>() {
                    @Override
                    protected Void doInBackground(Integer... params) {
                        try {
                            SShCommandSend.executeRemoteCommand("pi", "makova94", AppConfig.IP_RPI, 22, "killall omxplayer.bin");
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        return null;
                    }
                }.execute(1);
            }
        });
    }
}
