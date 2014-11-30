package com.example.synclists.synclists;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DialerFilter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by ethan on 10/27/14.
 */
public class SettingsActivity extends Activity{

    private SettingsArrayAdapter mSettingsAdapter;
    private String mEmail;
    private Boolean mSharingEnabled;
    private final String CHANGE_PASSWORD = "Change Password";
    private final String SYNC_EVERY = "Sync Every...";
    private final String INSTRUCTIONS = "Instructions";
    private final String LOGOUT = "Logout";
    private final String SHARING = "Toggle Sharing";
    private final String[] M_SETTINGS = new String[] { INSTRUCTIONS, SHARING, SYNC_EVERY, CHANGE_PASSWORD, LOGOUT };
    private SharedPreferences mPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_list_setting);

        mPrefs = getSharedPreferences(Constants.PREF_FILE_NAME, MODE_PRIVATE);

        boolean sharingOn = mPrefs.getBoolean(Constants.PREF_SHARING, Constants.DEFAULT_SHARING);
        mSettingsAdapter = new SettingsArrayAdapter(this, R.id.settingsListView, Arrays.asList(M_SETTINGS), SHARING, sharingOn);//new ArrayAdapter(this, android.R.layout.simple_list_item_1, M_SETTINGS);

        final ListView settingsListView = (ListView) findViewById(R.id.settingsListView);
        settingsListView.setAdapter(mSettingsAdapter);

        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String settingTitle = M_SETTINGS[position];
                if(settingTitle.equals(CHANGE_PASSWORD)) {
                    onChangePasswordClicked(view);
                }
                else if(settingTitle.equals(SYNC_EVERY)) {
                    onSyncEveryClicked(view);
                }
                else if(settingTitle.equals(INSTRUCTIONS)) {
                    onInstructionsClicked(view);
                }
                else if(settingTitle.equals(LOGOUT)) {
                    onLogoutClicked(view);
                }
                else {
                    //silence is golden
                }
            }
        });
    }

    public void onSharingClicked(final View view) {
        final String email = mPrefs.getString(Constants.PREF_EMAIL, Constants.DEFAULT_EMAIL);

        final boolean on = ((Switch) view).isChecked();
        final String successText;

        if(on)
            successText = "Sharing On";
        else
            successText = "Sharing Off";

        SyncListsApi.updateSharing(new SyncListsRequestAsyncTaskCallback() {
            @Override
            public void onTaskComplete(SyncListsResponse syncListsResponse) {
                if (syncListsResponse == null) {
                    Toast.makeText(SettingsActivity.this, "Error changing Sharing",
                            Toast.LENGTH_SHORT).show();

                    //toggle it back since we could not update
                    ((Switch) view).toggle();
                }
                else {
                    SharedPreferences.Editor editor = SyncListsLogin.getPreferencesEditor();
                    editor.putBoolean(Constants.PREF_SHARING, on);
                    editor.apply();

                    Toast.makeText(SettingsActivity.this,
                            successText, Toast.LENGTH_SHORT).show();
                }
            }
        }, email, on, SettingsActivity.this);
    }

    public void onLogoutClicked(View v) {
        final Intent login = new Intent(this, SyncListsLogin.class);

        Toast.makeText(SettingsActivity.this,
                "You have logged out", Toast.LENGTH_SHORT).show();
        SyncListsApi.logout();

        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(login);
        finish();
    }

    public void onInstructionsClicked(View v) {
        final Dialog instructionsDialog = new Dialog(this);
        instructionsDialog.setContentView(R.layout.instructions_dialog);
        instructionsDialog.setTitle("Instructions");

        Button dialogButton = (Button) instructionsDialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instructionsDialog.dismiss();
            }
        });

        instructionsDialog.show();
    }

    public void onSyncEveryClicked(View v) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final CharSequence options[] = new CharSequence[] {"15 seconds", "30 seconds", "1 minute", "5 minutes"};
        final int optionsInMilliseconds [] = {15000, 30000, 60000, 300000};

        int currentOption = 0;
        int syncEvery = mPrefs.getInt(Constants.PREF_SYNC_EVERY, Constants.DEFAULT_SYNC_EVERY);

        for(int i = 0; i < optionsInMilliseconds.length; i++) {
            if(optionsInMilliseconds[i] == syncEvery) {
                currentOption = i;
                break;
            }
        }

        adb.setSingleChoiceItems(options, currentOption, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int n) {
                dialog.dismiss();

                SharedPreferences.Editor editor = SyncListsLogin.getPreferencesEditor();
                editor.putInt(Constants.PREF_SYNC_EVERY, optionsInMilliseconds[n]);
                editor.apply();

                Toast.makeText(SettingsActivity.this,
                        "Set to sync every " + options[n], Toast.LENGTH_SHORT).show();
            }

        });

        adb.setNegativeButton(getString(R.string.cancel), null);
        adb.setTitle(getString(R.string.sync_every));
        adb.show();
    }

    public void onChangePasswordClicked(View v) {
        final Dialog changePasswordDialog = new Dialog(this);
        changePasswordDialog.setContentView(R.layout.change_password_dialog);
        changePasswordDialog.setTitle("Change Password");

        Button dialogButton = (Button) changePasswordDialog.findViewById(R.id.confirmChange);
        // if button is clicked, close the custom dialog and attempt to update password
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // change password in database
                EditText newPasswordDescription = (EditText)changePasswordDialog.findViewById(R.id.passwordChange);
                String newPassword = newPasswordDescription.getText().toString();
                EditText confirmNewPasswordDescription = (EditText)changePasswordDialog.findViewById(R.id.confirmPasswordChange);
                String confirmNewPassword = confirmNewPasswordDescription.getText().toString();

                if (!newPassword.equals(confirmNewPassword)) {
                    Toast.makeText(SettingsActivity.this,
                            "Your passwords do not match", Toast.LENGTH_SHORT).show();
                }
                else if(newPassword.isEmpty()) {
                    Toast.makeText(SettingsActivity.this,
                            "Password must not be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    changePassword(v, newPassword, confirmNewPassword);
                    changePasswordDialog.dismiss();
                }
            }
        });
        changePasswordDialog.show();
    }

    public void changePassword(View view, String newPassword, String confirmPassword) {
        mEmail = mPrefs.getString(Constants.PREF_EMAIL, Constants.DEFAULT_EMAIL);
        mSharingEnabled = mPrefs.getBoolean(Constants.PREF_SHARING, Constants.DEFAULT_SHARING);
        SyncListsApi.changePassword(this, newPassword, confirmPassword, this, mEmail, mSharingEnabled);
    }
}
