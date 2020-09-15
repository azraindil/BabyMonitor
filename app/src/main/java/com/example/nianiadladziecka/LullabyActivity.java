package com.example.nianiadladziecka;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class LullabyActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button btnStop,btnStart;
    Spinner spiLullaby;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_lullaby);

        btnStop = findViewById(R.id.btnStop);
        btnStart = findViewById(R.id.btnStart);
        spiLullaby = findViewById(R.id.spiLullaby);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.lullaby, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiLullaby.setAdapter(adapter);
        spiLullaby.setOnItemSelectedListener(this);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Integer, Void, Void>() {
                    @Override
                    protected Void doInBackground(Integer... params) {
                        try {
                            SShCommandSend.executeRemoteCommand("pi", "raspberry", AppConfig.IP_RPI, 22, "omxplayer --no-keys -o local "+ spiLullaby.getSelectedItem().toString()+".mp3&");
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
                            SShCommandSend.executeRemoteCommand("pi", "raspberry", AppConfig.IP_RPI, 22, "killall omxplayer.bin");
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        return null;
                    }
                }.execute(1);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
