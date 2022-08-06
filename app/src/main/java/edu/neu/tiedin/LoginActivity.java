package edu.neu.tiedin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    public static final String SHARED_PREFS = "LOGIN_PREFS";

    public static final String USER_KEY = "USER_SESSION_KEY";

    SharedPreferences sharedpreferences;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initializing EditTexts and our Button
        EditText emailEdt = findViewById(R.id.username);
        EditText passwordEdt = findViewById(R.id.password);
        Button loginBtn = findViewById(R.id.login);

        // Get login preferences (if they exist)
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        email = sharedpreferences.getString(USER_KEY, null);

        loginBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(emailEdt.getText().toString()) && TextUtils.isEmpty(passwordEdt.getText().toString())) {
                // Email and password cannot be empty
                Toast.makeText(LoginActivity.this, "Please Enter Email and Password", Toast.LENGTH_SHORT).show();
            } else {
                // Mock login, save email as the user key
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(USER_KEY, emailEdt.getText().toString());
                editor.apply();

                // Transition to the main activity
                Intent i = new Intent(LoginActivity.this, FindTripActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // If there is an existing session, transition to Main
        if (email != null) {
            Log.d(TAG, "onStart: existing session found, transitioning to main");
            Intent i = new Intent(LoginActivity.this, FindTripActivity.class);
            startActivity(i);
            finish();
        }
    }
}