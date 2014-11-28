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
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Map;

/**
 * Created by ethan on 10/27/14.
 */
public class SettingsActivity extends Activity{

    private ArrayAdapter mSettingsAdapter;
    private String mEmail;
    private Boolean mSharingEnabled;
    private final String CHANGE_PASSWORD = "Change Password";
    private final String SYNC_EVERY = "Sync Every...";
    private final String INSTRUCTIONS = "Instructions";
    private final String LOGOUT = "Logout";
    private final String[] M_SETTINGS = new String[] { CHANGE_PASSWORD, SYNC_EVERY, INSTRUCTIONS, LOGOUT };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_list_setting);

        mSettingsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, M_SETTINGS);

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
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_FILE_NAME, MODE_PRIVATE);
        int syncEvery = prefs.getInt(Constants.PREF_SYNC_EVERY, Constants.DEFAULT_SYNC_EVERY);

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
                changePassword(v, newPassword, confirmNewPassword);

                if (newPassword.equals(confirmNewPassword)) {
                    changePasswordDialog.dismiss();
                }
                else {
                    Toast.makeText(SettingsActivity.this,
                            "Your passwords did not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
        changePasswordDialog.show();
    }

    public void changePassword(View view, String newPassword, String confirmPassword) {
        SharedPreferences prefs = this.getSharedPreferences("SyncListsPrefs", Context.MODE_PRIVATE);
        mEmail = prefs.getString("Email", null);
        mSharingEnabled = prefs.getBoolean("sharingEnabled", true);
        SyncListsApi.changePassword(this, newPassword, confirmPassword, this, mEmail, mSharingEnabled);
    }
}
