package com.example.synclists.synclists;

import android.app.Activity;
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
    private Button mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_lists_main);
        mEmail = (EditText)findViewById(R.id.email);
        mPassword = (EditText)findViewById(R.id.password);
        mLogin = (Button)findViewById(R.id.loginButton);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sync_lists_main, menu);
        return true;
    }

    public void login(View view) {
        if(authenticate(mEmail.getText().toString(), mPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), "You did it",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "DUMBMBMBY",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean authenticate(String username, String password) {
        return username.equals("mitch") && password.equals("bitch");
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
}
