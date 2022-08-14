package edu.neu.tiedin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.neu.tiedin.data.User;
import edu.neu.tiedin.databinding.ActivityLoginBinding;
import edu.neu.tiedin.type.Query;
import edu.neu.tiedin.types.ClimberProfile;
import edu.neu.tiedin.types.ClimbingSkill;
import edu.neu.tiedin.types.ClimbingStyle;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    public String SHARED_PREFS;
    public String USER_KEY;

    SharedPreferences sharedpreferences;
    String userId;

    FirebaseFirestore firebaseFirestore;

    ActivityLoginBinding binding;

    private boolean registration = false;

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

        binding.btnLogin.setOnClickListener(v -> attemptLogin());

        binding.btnRegister.setOnClickListener(v -> toggleRegistration());
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

        if (TextUtils.isEmpty(binding.txtEmail.getText().toString())) {
            binding.txtEmail.setError("Username cannot be blank!");
        } else if (TextUtils.isEmpty(binding.txtPassword.getText().toString())) {
            binding.txtPassword.setError("Password cannot be blank!");
        } else {
            binding.loading.setVisibility(View.VISIBLE);

            String attemptedEmail = binding.txtEmail.getText().toString();
            String attemptedPassword = binding.txtPassword.getText().toString();

            firebaseFirestore.collection("users")
                    .whereEqualTo("email", attemptedEmail)
                    .get()
                    .addOnFailureListener(e -> Log.e(TAG, "bind: " + "Failed query to DB (username: " + attemptedEmail + ")"))
                    .onSuccessTask(snapshot -> Tasks.forResult(validateUserResultExists(snapshot, attemptedEmail)))
                    .onSuccessTask(snapshot -> Tasks.forResult(validateLoginAttempt(snapshot, attemptedEmail, attemptedPassword)))
                    .addOnSuccessListener(snapshot -> executeLogin(snapshot))
                    .addOnFailureListener(snapshot -> Log.e(TAG, "attemptLogin: executeLogin failed?"))
                    .addOnCompleteListener(task -> binding.loading.setVisibility(View.GONE));
        }

    }

    private void attemptRegistration() {
        // Fields cannot be empty
        AtomicBoolean containedEmpty = new AtomicBoolean(false);
        containedEmpty.set(false);
        Stream<EditText> fieldsToValidate = Stream.of(
                binding.txtEmail,
                binding.txtPassword,
                binding.txtPassword2,
                binding.txtName,
                binding.txtLocation,
                binding.txtPhoneNumber);
        fieldsToValidate.forEach(editText -> {
            editText.setError(null);
            if (editText.getText().toString().isEmpty()) {
                editText.setError(editText.getHint().toString() + " cannot be empty!");
                containedEmpty.set(true);
            }
        });
        if (containedEmpty.get()) {
            return;
        } else if (!binding.txtPassword.getText().toString().equals(binding.txtPassword2.getText().toString())) {
            // Passwords must match
            binding.txtPassword2.setError("Passwords must match");
        } else if (!PhoneNumberUtils.isGlobalPhoneNumber(binding.txtPhoneNumber.getText().toString())) {
            // Phone number must be valid
            binding.txtPhoneNumber.setError("Invalid phone number");
        } else {
            // Ensure user doesn't exist, then attempt to register
            String attemptedEmail = binding.txtEmail.getText().toString();
            firebaseFirestore.collection("users")
                    .whereEqualTo("email", attemptedEmail)
                    .get()
                    .addOnFailureListener(e -> Log.e(TAG, "bind: " + "Failed query to DB (username: " + attemptedEmail + ")"))
                    .onSuccessTask(snapshot -> Tasks.forResult(validateUserResultExists(snapshot, attemptedEmail)))
                    .onSuccessTask(snapshot -> Tasks.forResult(validateNoExistingUser(snapshot, attemptedEmail)))
                    .onSuccessTask(snapshot -> {
                                List<ClimbingStyle> climbTypes = binding.chipGroupPreferredStyles.getCheckedChipIds().stream().map(integer -> {
                                    switch (integer) {
                                        case R.id.preferredStyleChipSport: return ClimbingStyle.SPORT;
                                        case R.id.preferredStyleChipTrad: return ClimbingStyle.TRADITIONAL;
                                        case R.id.preferredStyleChipTR: return ClimbingStyle.TOPROPE;
                                        case R.id.preferredStyleChipBoulder: return ClimbingStyle.BOULDER;
                                        default: return ClimbingStyle.UNKNOWN;
                                    }
                                }).collect(Collectors.toList());
                                ClimberProfile profile = new ClimberProfile(
                                        "Icon not implemented",
                                        binding.txtLocation.getText().toString(),
                                        climbTypes,
                                        ClimbingSkill.NOVICE,
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        new ArrayList<>()
                                );

                                User createUser = new User(
                                        binding.txtName.getText().toString(),
                                        binding.txtEmail.getText().toString(),
                                        binding.txtPassword.getText().toString(),
                                        binding.txtPhoneNumber.getText().toString(),
                                        profile);

                                return firebaseFirestore.collection("users")
                                        .document(createUser.get_id())
                                        .set(createUser);
                            }
                    )
                    .addOnCompleteListener(task -> {
                                binding.loading.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Log.e(TAG, "attemptRegistration: successfully registered user");
                                    Toast.makeText(LoginActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                    toggleRegistration();
                                } else {
                                    Log.e(TAG, "attemptRegistration: failed to register user in DB");
                                    Toast.makeText(LoginActivity.this, "Failed to register user in DB", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
        }

    }

    private void toggleRegistration() {
        registration = !registration;
        List<View> registrationFields = Arrays.asList(
                binding.txtPassword2,
                binding.txtProfileLabel,
                binding.txtName,
                binding.txtPhoneNumber,
                binding.txtLocation,
                binding.chipGroupPreferredStyles
        );
        if (registration) {
            // Make registration-specific fields visible
            registrationFields.forEach(view -> {
                view.setVisibility(View.VISIBLE);
            });

            // Re-bind login button
            binding.btnLogin.setOnClickListener(v -> {
                attemptRegistration();
            });

            // Change button texts
            binding.btnLogin.setText("Register");
            binding.btnRegister.setText("Cancel registration");
        } else {
            // Make registration-specific fields gone
            registrationFields.forEach(view -> {
                view.setVisibility(View.GONE);
            });

            // Re-bind login button
            binding.btnLogin.setOnClickListener(v -> {
                attemptLogin();
            });

            // Change button texts
            binding.btnLogin.setText("Sign In");
            binding.btnRegister.setText("Need an account? Register");
        }
    }

    private QuerySnapshot validateNoExistingUser(QuerySnapshot snapshot, String attemptedEmail) throws Exception {
        if (snapshot.getDocuments().size() != 0) {
            Log.e(TAG, "bind: " + "existing user document found in DB (username: " + attemptedEmail + ")");
            binding.txtEmail.setError("Username already in use");
            throw new Exception("existing user found in DB (username: " + attemptedEmail + ")");
        } else {
            return snapshot;
        }
    }

    private QuerySnapshot validateUserResultExists(QuerySnapshot snapshot, String attemptedEmail) throws Exception {
        if (snapshot == null) {
            Log.e(TAG, "bind: " + "null result from DB (username: " + attemptedEmail + ")");
            Toast.makeText(LoginActivity.this, "Null result from database", Toast.LENGTH_SHORT).show();
            throw new Exception("null result from DB (username: " + attemptedEmail + ")");
        } else if (snapshot.getDocuments() == null) {
            Log.e(TAG, "bind: " + "null documents from DB (username: " + attemptedEmail + ")");
            Toast.makeText(LoginActivity.this, "Null documents result from database", Toast.LENGTH_SHORT).show();
            throw new Exception("null result from DB (username: " + attemptedEmail + ")");
        }
        return snapshot;
    }

    private QuerySnapshot validateLoginAttempt(QuerySnapshot snapshot, String attemptedEmail, String attemptedPassword) throws Exception {
        if (snapshot.getDocuments().size() == 0) {
            Log.e(TAG, "bind: " + "no user document found in DB (username: " + attemptedEmail + ")");
            binding.txtEmail.setError("Invalid username");
            throw new Exception("null result from DB (username: " + attemptedEmail + ")");
        } else if (snapshot.getDocuments().size() > 1) {
            Log.e(TAG, "bind: " + "too many user document found in DB (username: " + attemptedEmail + ")");
            binding.txtEmail.setError("Invalid username");
            throw new Exception("null result from DB (username: " + attemptedEmail + ")");
        } else if (snapshot.getDocuments().get(0).toObject(User.class) == null) {
            Log.e(TAG, "bind: " + "null user object found in DB (username: " + attemptedEmail + ")");
            binding.txtEmail.setError("Invalid username");
            throw new Exception("null result from DB (username: " + attemptedEmail + ")");
        } else if (!snapshot.getDocuments().get(0).toObject(User.class).checkPassword(attemptedPassword)) {
            Log.i(TAG, "bind: " + "incorrect password (username: " + attemptedEmail + ")");
            binding.txtPassword.setError("Incorrect password");
            throw new Exception("null result from DB (username: " + attemptedEmail + ")");
        } else {
            Log.i(TAG, "bind: " + "valid login (username: " + attemptedEmail + ")");
            return snapshot;
        }
    }

    private void transitionToMain() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    private void executeLogin(QuerySnapshot snapshot) {
        // Execute the login
        User loggedInUser = snapshot.getDocuments().get(0).toObject(User.class);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(USER_KEY, loggedInUser.get_id());
        editor.apply();

        // Transition to the main activity
        transitionToMain();
    }
}