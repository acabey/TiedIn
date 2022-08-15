package edu.neu.tiedin.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Tasks;
import com.google.android.material.chip.Chip;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import edu.neu.tiedin.LoginActivity;
import edu.neu.tiedin.R;
import edu.neu.tiedin.data.User;
import edu.neu.tiedin.databinding.FragmentSettingsBinding;
import edu.neu.tiedin.types.ClimbingEquipment;
import edu.neu.tiedin.types.ClimbingSkill;
import edu.neu.tiedin.types.ClimbingStyle;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";
    public String SHARED_PREFS;
    public String USER_KEY;

    private FirebaseFirestore firestoreDatabase;
    private SharedPreferences sharedpreferences;
    private String userId;

    private SettingsViewModel settingsViewModel;
    FragmentSettingsBinding binding;

    BiMap<Integer, ClimbingSkill> idToSkillMap = new ImmutableBiMap.Builder<Integer, ClimbingSkill>()
            .put(R.id.settingsSkillBeginner, ClimbingSkill.BEGINNER)
            .put(R.id.settingsSkillNovice, ClimbingSkill.NOVICE)
            .put(R.id.settingsSkillAdvanced, ClimbingSkill.ADVANCED)
            .put(R.id.settingsSkillExpert, ClimbingSkill.EXPERT)
            .build();


    BiMap<Integer, ClimbingStyle> idToStyleMap = new ImmutableBiMap.Builder<Integer, ClimbingStyle>()
            .put(R.id.settingsStyleChipSport, ClimbingStyle.SPORT)
        .put(R.id.settingsStyleChipTrad, ClimbingStyle.TRADITIONAL)
        .put(R.id.settingsStyleChipTR, ClimbingStyle.TOPROPE)
        .put(R.id.settingsStyleChipBoulder, ClimbingStyle.BOULDER)
    .build();

    BiMap<Integer, ClimbingEquipment> idToEquipmentMap = new ImmutableBiMap.Builder<Integer, ClimbingEquipment>()
            .put(R.id.settingsEquipmentBelayDevice, ClimbingEquipment.BELAY_DEVICE)
            .put(R.id.settingsEquipmentCrashPad, ClimbingEquipment.CRASH_PAD)
            .put(R.id.settingsEquipmentHarness, ClimbingEquipment.HARNESS)
            .put(R.id.settingsEquipmentRope, ClimbingEquipment.ROPE)
            .build();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get login preferences
        SHARED_PREFS = getString(R.string.sessionLoginPrefsKey);
        USER_KEY = getString(R.string.sessionUserIdKey);
        sharedpreferences = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        userId = sharedpreferences.getString(USER_KEY, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setSettingsViewModel(settingsViewModel);
        View root = binding.getRoot();

        // Connect with firebase
        firestoreDatabase = FirebaseFirestore.getInstance();

        // Skill chipgroup bindings
        binding.chipGroupSkillLevel.setOnCheckedStateChangeListener((group, checkedIds) -> {
            Optional<ClimbingSkill> selected = checkedIds.
                    stream()
                    .map(id -> idToSkillMap.getOrDefault(id, ClimbingSkill.BEGINNER))
                    .findFirst();
            if (selected.isPresent()) {
                settingsViewModel.getSkillLevel().setValue(selected.get());
            }
        });

        // Styles bindings
        binding.chipGroupPreferredStyles.setOnCheckedStateChangeListener((group, checkedIds) -> {
            settingsViewModel.getStyles().setValue(checkedIds
                    .stream()
                    .map(id -> idToStyleMap.getOrDefault(id, ClimbingStyle.UNKNOWN))
                    .collect(Collectors.toList()));
        });

        // Equipment bindings
        binding.chipGroupEquipment.setOnCheckedStateChangeListener((group, checkedIds) -> {
            settingsViewModel.getEquipment().setValue(checkedIds
                    .stream()
                    .map(id -> idToEquipmentMap.getOrDefault(id, ClimbingEquipment.ROPE))
                    .collect(Collectors.toList()));
        });

        // Async populate with current settings
        // Pull down the current user
        firestoreDatabase.collection("users").document(userId).get()
                .onSuccessTask(snapshot -> Tasks.forResult(validateTUserResultExists(snapshot, userId, User.class)))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "onCreateView: pulled down user");
                        User pulledUser = task.getResult().toObject(User.class);
                        // Already bound two-way
                        settingsViewModel.getName().setValue(pulledUser.getName());
                        settingsViewModel.getPhoneNumber().setValue(pulledUser.getPhoneNumber());
                        settingsViewModel.getLocation().setValue(pulledUser.getProfile().getLocation());
                        // Requires manually setting chips
                        settingsViewModel.getStyles().setValue(pulledUser.getProfile().getStyles());
                        settingsViewModel.getSkillLevel().setValue(pulledUser.getProfile().getSkillLevel());
                        settingsViewModel.getEquipment().setValue(pulledUser.getProfile().getEquipment());

                        // Check chips
                        binding.chipGroupPreferredStyles.clearCheck();
                        settingsViewModel.getStyles().getValue().forEach(climbingStyle -> {
                            ((Chip) root.findViewById(idToStyleMap.inverse().get(climbingStyle))).setChecked(true);
                        });

                        binding.chipGroupEquipment.clearCheck();
                        settingsViewModel.getEquipment().getValue().forEach(equipment-> {
                            ((Chip) root.findViewById(idToEquipmentMap.inverse().get(equipment))).setChecked(true);
                        });

                        binding.chipGroupSkillLevel.clearCheck();
                        ((Chip) root.findViewById(idToSkillMap.inverse().get(settingsViewModel.getSkillLevel().getValue()))).setChecked(true);
                        Log.i(TAG, "onCreateView: synced settings with DB");
                    } else {
                        Log.e(TAG, "onCreateView: failed to pull down user");
                        Toast.makeText(getContext(), "Failed to pull down user information", Toast.LENGTH_SHORT);
                    }
                });

        // Delete all sharedPreferences and transition back to login
        binding.btnLogout.setOnClickListener(v -> {
            Log.d(TAG, "onCreateView: Clearing SharedPreferences to force logout");
            sharedpreferences.edit().clear().apply();
            Intent i = new Intent(getContext(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            getActivity().finish();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private DocumentSnapshot validateTUserResultExists(DocumentSnapshot snapshot, String attemptedKey, Class<?> t) throws Exception {
        if (snapshot == null) {
            Log.e(TAG, "bind: " + "null result from DB (key: " + attemptedKey + ")");
            Toast.makeText(getContext(), "Null result from database", Toast.LENGTH_SHORT).show();
            throw new Exception("null result from DB (key: " + attemptedKey + ")");
        } else if (snapshot.toObject(t) == null) {
            Log.e(TAG, "bind: " + "null user from DB (key: " + attemptedKey + ")");
            Toast.makeText(getContext(), "Null object result from database", Toast.LENGTH_SHORT).show();
            throw new Exception("null object from DB (key: " + attemptedKey + ")");
        }
        return snapshot;
    }
}