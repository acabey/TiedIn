package edu.neu.tiedin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import edu.neu.tiedin.data.User;
import edu.neu.tiedin.databinding.ActivityLoginBinding;
import edu.neu.tiedin.databinding.FragmentHomeBinding;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    public String SHARED_PREFS;
    public String USER_KEY;

    SharedPreferences sharedpreferences;
    String userId;

    FirebaseFirestore firebaseFirestore;

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SHARED_PREFS = getString(R.string.sessionLoginPrefsKey);
        USER_KEY = getString(R.string.sessionUserIdKey);

        // Connect with firebase
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Initializing binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get login preferences (if they exist)
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        userId = sharedpreferences.getString(USER_KEY, null);

        binding.btnLogin.setOnClickListener(v -> {
            attemptLogin();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // If there is an existing session, transition to Main
        if (userId != null) {
            Log.d(TAG, "onStart: existing session found, transitioning to main");
            transitionToMain();
        }
    }

    private void attemptLogin() {
        if (TextUtils.isEmpty(binding.txtEmail.getText().toString()) && TextUtils.isEmpty(binding.txtPassword.getText().toString())) {
            // Email and password cannot be empty
            Toast.makeText(LoginActivity.this, "Please Enter Email and Password", Toast.LENGTH_SHORT).show();
        } else {
            String attemptedEmail = binding.txtEmail.getText().toString();
            String attemptedPassword = binding.txtPassword.getText().toString();

            firebaseFirestore.collection("users")
                    .whereEqualTo("email", attemptedEmail)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "bind: " + "Failed to pull email from DB (email: " + attemptedEmail + ")");
                            Toast.makeText(LoginActivity.this, "Failed to connect to database", Toast.LENGTH_SHORT).show();
                        } else if (task.getResult() == null) {
                            Log.e(TAG, "bind: " + "null result from DB (username: " + attemptedEmail + ")");
                            Toast.makeText(LoginActivity.this, "Null result from database", Toast.LENGTH_SHORT).show();
                        } else if (task.getResult().getDocuments() == null) {
                            Log.e(TAG, "bind: " + "null documents from DB (username: " + attemptedEmail + ")");
                            Toast.makeText(LoginActivity.this, "Null documents result from database", Toast.LENGTH_SHORT).show();
                        } else if (task.getResult().getDocuments().size() == 0) {
                            Log.e(TAG, "bind: " + "no user document found in DB (username: " + attemptedEmail + ")");
                            binding.txtEmail.setError("Invalid username");
                        } else if (task.getResult().getDocuments().size() > 1) {
                            Log.e(TAG, "bind: " + "too many user document found in DB (username: " + attemptedEmail + ")");
                            binding.txtEmail.setError("Invalid username");
                        } else if (task.getResult().getDocuments().get(0).toObject(User.class) == null) {
                            Log.e(TAG, "bind: " + "null user object found in DB (username: " + attemptedEmail + ")");
                            binding.txtEmail.setError("Invalid username");
                        } else if (!task.getResult().getDocuments().get(0).toObject(User.class).checkPassword(attemptedPassword)) {
                            Log.i(TAG, "bind: " + "incorrect password (username: " + attemptedEmail + ")");
                            binding.txtPassword.setError("Incorrect password");
                        } else {
                            Log.i(TAG, "bind: " + "valid login (username: " + attemptedEmail + ")");
                            User loggedInUser = task.getResult().getDocuments().get(0).toObject(User.class);
                            // Mock login, save email as the user key
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(USER_KEY, loggedInUser.get_id());
                            editor.apply();

                            // Transition to the main activity
                            transitionToMain();
                        }
                    });
        }
    }

    private void transitionToMain() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}