package com.example.synclists.synclists;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class SyncListsLogin extends Activity {

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
    }

    public void login(View view) {
        SyncListsApi.login(this, mEmail.getText().toString(), mPassword.getText().toString());
    }

    public void createUser(View view) {
        if(validSignUp()) {
            SyncListsApi.createUser(this, mEmail.getText().toString(), mPassword.getText().toString());
        }
        else {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validSignUp() {
        String email = mEmail.getText().toString();

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    protected static SharedPreferences.Editor getPreferencesEditor() {
        return SyncListsLogin.mPrefs.edit();
    }
}
