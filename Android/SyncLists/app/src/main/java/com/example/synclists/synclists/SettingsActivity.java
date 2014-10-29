package com.example.synclists.synclists;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by ethan on 10/27/14.
 */
public class SettingsActivity extends Activity{
    protected static SharedPreferences mPrefs;
    TextView mLogoutButton;
    TextView mInstructionsButton;
    TextView mChangePasswordButton;
    TextView mSyncButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_view);
        addLogoutListner();
        addShowInstuctionsListener();
        addSyncWithDatabaseListener();
        addChangePasswordListner();
    }

    public void addLogoutListner() {

        mLogoutButton = (ImageButton) findViewById(R.id.logoutButton);
        final Intent login = new Intent(this, SyncListsLogin.class);

        mLogoutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(SettingsActivity.this,
                        "You have logged out", Toast.LENGTH_SHORT).show();
                SyncListsApi.logout();

                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(login);
                finish();
            }
        });
    }

    public void addShowInstuctionsListener() {

        mInstructionsButton = (TextView) findViewById(R.id.instructionsButton);
        mInstructionsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this,
                        "Show Instructions is clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addSyncWithDatabaseListener() {

        mSyncButton = (TextView) findViewById(R.id.snycButton);
        mSyncButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(SettingsActivity.this,
                        "Sync is clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addChangePasswordListner() {

        mChangePasswordButton = (TextView) findViewById(R.id.changePasswordButton);
        mChangePasswordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(SettingsActivity.this,
                        "Change Password is clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
