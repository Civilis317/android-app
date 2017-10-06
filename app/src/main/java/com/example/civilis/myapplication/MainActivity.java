package com.example.civilis.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

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

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);

        String name = getTextEditValue(R.id.txtName);
        String longitude = getTextEditValue(R.id.txtLong);
        String latitude = getTextEditValue(R.id.txtLat);

        new PostLocationTask().execute(name, longitude, latitude);

        intent.putExtra(Constants.NAME, name);
        intent.putExtra(Constants.LONGITUDE, longitude);
        intent.putExtra(Constants.LATITUDE, latitude);
        startActivity(intent);
    }

    private String getTextEditValue(int txtEditId) {
        return ((EditText) findViewById(txtEditId)).getText().toString();
    }

}

