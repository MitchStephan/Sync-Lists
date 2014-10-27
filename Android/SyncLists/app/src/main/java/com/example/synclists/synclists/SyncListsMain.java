package com.example.synclists.synclists;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SyncListsMain extends Activity {

    private EditText mEmail = null;
    private EditText mPassword = null;
    protected static SharedPreferences mPrefs;
    private final String PREF_FILE_NAME = "SyncListsPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_lists_main);
        mEmail = (EditText)findViewById(R.id.email);
        mPassword = (EditText)findViewById(R.id.password);

        //set up prefs
        mPrefs = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);

        if(mPrefs.getInt(SyncListsApi.USER_CONTEXT, -1) != -1) {
            Intent lists = new Intent(this, ListsActivity.class);
            startActivity(lists);
        }
        //login(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sync_lists_main, menu);
        return true;
    }

    public void login(View view) {
        //SyncListsApi.login(this, "mitch@bitch.com", "password");
        SyncListsApi.login(this, mEmail.getText().toString(), mPassword.getText().toString());
    }

    public void createUser(View view) {
        SyncListsApi.createUser(this, mEmail.getText().toString(), mPassword.getText().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected static SharedPreferences.Editor getPreferencesEditor() {
        return SyncListsMain.mPrefs.edit();
    }
}
