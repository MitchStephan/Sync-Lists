package com.example.synclists.synclists;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ethan on 10/27/14.
 */
public class SettingsActivity extends Activity{
    ImageButton mLogoutButton;
    ImageButton mInstructionsButton;
    ImageButton mChangePasswordButton;
    ImageButton mSyncButton;

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
        mLogoutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(SettingsActivity.this,
                        "Logout is clicked!", Toast.LENGTH_SHORT).show();

            }

        });

    }

    public void addShowInstuctionsListener() {

        mInstructionsButton = (ImageButton) findViewById(R.id.instructionsButton);
        mInstructionsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(SettingsActivity.this,
                        "Show Instructions is clicked!", Toast.LENGTH_SHORT).show();

            }

        });

    }

    public void addSyncWithDatabaseListener() {

        mSyncButton = (ImageButton) findViewById(R.id.snycButton);
        mSyncButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(SettingsActivity.this,
                        "Sync is clicked!", Toast.LENGTH_SHORT).show();

            }

        });

    }

    public void addChangePasswordListner() {

        mChangePasswordButton = (ImageButton) findViewById(R.id.changePasswordButton);
        mChangePasswordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(SettingsActivity.this,
                        "Change Password is clicked!", Toast.LENGTH_SHORT).show();

            }

        });

    }
}
