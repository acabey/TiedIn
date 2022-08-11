package edu.neu.tiedin.ui.plantrip;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import edu.neu.tiedin.AreaByUUIDQuery;
import edu.neu.tiedin.AreasByFilterQuery;
import edu.neu.tiedin.R;
import edu.neu.tiedin.data.ClimbingTrip;
import edu.neu.tiedin.databinding.FragmentPlanTripBinding;
import edu.neu.tiedin.types.ClimbingStyle;
import edu.neu.tiedin.types.openbeta.composedschema.ComposedArea;

public class PlanTripFragment extends Fragment {

    private static final java.lang.String TAG = "PlanTripFragment";

    public String SHARED_PREFS;
    public String USER_KEY;

    private FirebaseFirestore firestoreDatabase;
    private SharedPreferences sharedpreferences;
    private String userId;

    private FragmentPlanTripBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SHARED_PREFS = getString(R.string.sessionLoginPrefsKey);
        USER_KEY = getString(R.string.sessionUserIdKey);

        // Connect with firebase
        firestoreDatabase = FirebaseFirestore.getInstance();

        // Get login preferences
        sharedpreferences = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        userId = sharedpreferences.getString(USER_KEY, null);
        assert (userId != null); // MainActivity should force login
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PlanTripViewModel planTripViewModel =
                new ViewModelProvider(this).get(PlanTripViewModel.class);

        binding = FragmentPlanTripBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Bind UI to ViewModel

        // Datepicker minimum date is today
        binding.dpWhenPicker.setMinDate(Calendar.getInstance().getTimeInMillis());

        // Date TextView gets date value
        planTripViewModel.getPlanDate().observe(getViewLifecycleOwner(), localDate -> {
            // "<Day of week>, <Month of Year> <day of month" e.g. Tuesday, August 9
            binding.editTextPlanDate.setText(localDate.format(DateTimeFormatter.ofPattern("E, MMM d")));
        });

        //Date TextView toggles DatePicker visibility
        binding.editTextPlanDate.setOnClickListener(v -> {
            int toggledVisibility = binding.dpWhenPicker.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
            binding.dpWhenPicker.setVisibility(toggledVisibility);
        });

        // DatePicker sets date value
        binding.dpWhenPicker.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> {
            LocalDate date = LocalDate.of(year, monthOfYear, dayOfMonth);
            planTripViewModel.setPlanDate(date);
        });

        // Autocomplete Climbing Area search
        ClimbingAreaSuggestFilter areaSuggestFilter = new ClimbingAreaSuggestFilter(getActivity(), android.R.layout.simple_dropdown_item_1line);
        binding.editTextPlanArea.setAdapter(areaSuggestFilter);

        // Clicking an area from the autocomplete adds to the list of areas
        binding.editTextPlanArea.setOnItemClickListener((parent, view, position, id) -> {
            // Clear error in textbox (if any)
            binding.editTextPlanArea.setError(null);

            AreasByFilterQuery.Area selected = (AreasByFilterQuery.Area) parent.getItemAtPosition(position);
            if (selected != null) {
                // Add data to ViewModel
                planTripViewModel.addPlannedArea(selected);

                // Add chip to UI
                Chip areaChip = new Chip(getContext(), null, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Entry);
                ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(getContext(), null, 0, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Entry);
                areaChip.setChipDrawable(chipDrawable);
                areaChip.setText(selected.areaName);
                areaChip.setOnCloseIconClickListener(v -> {
                    planTripViewModel.removePlannedArea(selected);
                    binding.chipGroupPlanAreas.removeView(areaChip);
                });
                binding.chipGroupPlanAreas.addView(areaChip);

                Log.d(TAG, "onItemClick: " + selected.areaName);
            }
        });

        // Selecting / de-selecting climb syle chips
        binding.chipGroupPlanStyles.setOnCheckedStateChangeListener((group, checkedIds) -> {
            List<ClimbingStyle> climbTypes = checkedIds.stream().map(integer -> {
                switch (integer) {
                    case R.id.planStyleChipSport: return ClimbingStyle.SPORT;
                    case R.id.planStyleChipTrad: return ClimbingStyle.TRADITIONAL;
                    case R.id.planStyleChipTR: return ClimbingStyle.TOPROPE;
                    case R.id.planStyleChipBoulder: return ClimbingStyle.BOULDER;
                    default: return ClimbingStyle.UNKNOWN;
                }
            }).collect(Collectors.toList());
            planTripViewModel.setPlanClimbStyles(climbTypes);
        });

        // Autocomplete Objective search
        ObjectiveSuggestFilter objectiveSuggestFilter = new ObjectiveSuggestFilter(getActivity(),
                android.R.layout.simple_dropdown_item_1line,
                areaSuggestFilter.getAreasData());
        binding.planObjectivesValue.setAdapter(objectiveSuggestFilter);

        // Clicking an area from the autocomplete adds to the list of areas
        binding.planObjectivesValue.setOnItemClickListener((parent, view, position, id) -> {
            AreaByUUIDQuery.Climb selected = (AreaByUUIDQuery.Climb) parent.getItemAtPosition(position);
            if (selected != null) {
                // Add data to ViewModel
                planTripViewModel.addPlannedObjective(selected);

                // Add chip to UI
                Chip areaChip = new Chip(getContext(), null, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Entry);
                ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(getContext(), null, 0, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Entry);
                areaChip.setChipDrawable(chipDrawable);
                areaChip.setText(selected.id);
                areaChip.setOnCloseIconClickListener(v -> {
                    planTripViewModel.removePlannedObjective(selected);
                    binding.chipGroupPlanAreas.removeView(areaChip);
                });
                binding.chipGroupPlanAreas.addView(areaChip);

                Log.d(TAG, "onItemClick: " + selected.id);
            }
        });

        // Post button binding
        binding.btnPostPlannedTrip.setOnClickListener(v -> {

            // Create Trip Plan Object from fields
            ClimbingTrip trip = new ClimbingTrip(
                    userId,
                    Collections.emptyList(),
                    planTripViewModel.getPlanDate().getValue().toEpochDay(),
                    planTripViewModel.getPlanAreas().getValue().stream().map(ComposedArea::fromAreaFilterQuery).collect(Collectors.toList()),
                    planTripViewModel.getPlanClimbStyles().getValue(),
                    planTripViewModel.getPlanDetails().getValue()
            );

            // Validate trip settings
            if (trip.getEpochDate() == null) {
                Toast.makeText(getContext(),"Must select date for trip",Toast.LENGTH_SHORT).show();
                return;
            } else if(trip.getAreas() == null || trip.getAreas().isEmpty()) {
                Toast.makeText(getContext(),"Must select at least one area",Toast.LENGTH_SHORT).show();
                binding.editTextPlanArea.setError("Add a climbing area");
                return;
            } else if(trip.getStyles() == null || trip.getStyles().isEmpty()) {
                Toast.makeText(getContext(),"Must select at least one climbing style",Toast.LENGTH_SHORT).show();
                return;
            }

            Task<DocumentReference> dbPostMessage = firestoreDatabase.collection("trips").add(trip);
            dbPostMessage.addOnCompleteListener((OnCompleteListener<DocumentReference>) completedPostTrip -> {
                if(completedPostTrip.isSuccessful()){
                    Toast.makeText(getContext(),"Posted new trip: ",Toast.LENGTH_SHORT).show();
                    switchFragments();
                }
                else {
                    Toast.makeText(getContext(),"Unable to post message to DB",Toast.LENGTH_SHORT).show();
                }
            });

        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void switchFragments() {
        NavHostFragment.findNavController(this).navigate(R.id.nav_home);
    }
}