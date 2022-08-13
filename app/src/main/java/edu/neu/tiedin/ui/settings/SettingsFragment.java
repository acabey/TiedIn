package edu.neu.tiedin.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.neu.tiedin.LoginActivity;
import edu.neu.tiedin.MainActivity;
import edu.neu.tiedin.R;
import edu.neu.tiedin.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";
    public String SHARED_PREFS;
    public String USER_KEY;

    private SharedPreferences sharedpreferences;
    private String userId;

    FragmentSettingsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get login preferences
        SHARED_PREFS = getString(R.string.sessionLoginPrefsKey);
        USER_KEY = getString(R.string.sessionUserIdKey);
        sharedpreferences = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        userId = sharedpreferences.getString(USER_KEY, null);
        assert (userId != null); // MainActivity should force login
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Delete all sharedPreferences and transition back to login
        binding.btnLogout.setOnClickListener(v -> {
            Log.d(TAG, "onCreateView: Clearing SharedPreferences to force logout");
            sharedpreferences.edit().clear().apply();
            Intent i = new Intent(getContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(i);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}