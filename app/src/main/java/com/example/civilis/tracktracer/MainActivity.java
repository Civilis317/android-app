package com.example.civilis.tracktracer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.civilis.tracktracer.Util.Config;
import com.example.civilis.tracktracer.service.MainService;

public class MainActivity extends AppCompatActivity {
    private ToggleButton toggleButton;
    private TextView textView;
    private Intent sendLocationServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkService(View view) {
        TextView txtRunning = (TextView) findViewById(R.id.txtRunning);
        txtRunning.setText(String.valueOf("Running: " + Config.isRunning()));

        TextView txtInterval = (TextView) findViewById(R.id.txtInterval);
        txtInterval.setText(String.valueOf("Interval: " + Config.getInterval()));

        TextView txtTarget = (TextView) findViewById(R.id.txtTarget);
        txtTarget.setText("Target: " + Config.getTarget());
    }

    public void stopIntentService(View view) {
        if (sendLocationServiceIntent == null) {
            sendLocationServiceIntent = new Intent(this, MainService.class);
        }
        stopService(sendLocationServiceIntent);
    }

    public void startIntentService(View view) {
        sendLocationServiceIntent = new Intent(this, MainService.class);
        startService(sendLocationServiceIntent);
    }

}

