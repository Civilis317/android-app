package com.example.civilis.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.civilis.myapplication.Util.Constants;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String name = intent.getStringExtra(Constants.NAME);
        String longitude = intent.getStringExtra(Constants.LONGITUDE);
        String latitude = intent.getStringExtra(Constants.LATITUDE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(name + ", " + longitude + ", " + latitude);

    }
}
