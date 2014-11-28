package com.example.synclists.synclists;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


public class SyncListsLogin extends Activity {

    private EditText mEmail = null;
    private EditText mPassword = null;
    protected static SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_lists_main);
        mEmail = (EditText)findViewById(R.id.email);
        mPassword = (EditText)findViewById(R.id.password);

        //when user hits enter on password EditText, call login for them
        mPassword.setOnKeyListener(new PasswordOnKeyListener());

        //set up prefs
        mPrefs = getSharedPreferences(Constants.PREF_FILE_NAME, MODE_PRIVATE);

        Log.d(Constants.TAG, " USER CONTEXT " + mPrefs.getInt(Constants.PREF_USER_CONTEXT, -1));
        if(mPrefs.getInt(Constants.USER_CONTEXT_HEADER, -1) != -1) {
            Intent lists = new Intent(this, ListsActivity.class);
            startActivity(lists);
        }
    }

    public void login(View view) {
        SyncListsApi.login(this, mEmail.getText().toString(), mPassword.getText().toString(), this);
    }

    public void createUser(View view) {
        if(validSignUp()) {
            SyncListsApi.createUser(this, mEmail.getText().toString(), mPassword.getText().toString(), this);
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

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private class PasswordOnKeyListener implements View.OnKeyListener {

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if (email != null && !email.isEmpty() && password != null && !password.isEmpty())
                    login(null);

                return true;
            }

            return false;
        }
    }
}
