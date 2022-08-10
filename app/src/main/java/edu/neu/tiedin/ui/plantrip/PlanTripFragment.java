package edu.neu.tiedin.ui.plantrip;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import edu.neu.tiedin.AreasByFilterQuery;
import edu.neu.tiedin.R;
import edu.neu.tiedin.databinding.FragmentPlanTripBinding;
import edu.neu.tiedin.types.ClimbingStyle;

public class PlanTripFragment extends Fragment {

    private static final String TAG = "PlanTripFragment";

    private FirebaseDatabase firebaseDatabase;
    private FragmentPlanTripBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connect with firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PlanTripViewModel planTripViewModel =
                new ViewModelProvider(this).get(PlanTripViewModel.class);

        binding = FragmentPlanTripBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Bind UI to ViewModel
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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void postTripHandler() {
//        // Error handling: must be logged in, have a sticker selected, and have a recipient selected
//        if (currentUser == null) {
//            Toast.makeText(getActivity(),"Must be logged in to send message",Toast.LENGTH_SHORT).show();
//            return;
//        } else if (stickerChipGroup.getCheckedChipId() == View.NO_ID) {
//            Toast.makeText(getActivity(),"Must select one sticker to send",Toast.LENGTH_SHORT).show();
//            return;
//        } else if (recipientChipGroup.getCheckedChipId() == View.NO_ID) {
//            Toast.makeText(getActivity(),"Must select one recipient to send",Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Create message object and push to DB
//        String recipientUsername = ((Chip) findViewById(recipientChipGroup.getCheckedChipId())).getText().toString();
//        Stickers selectedSticker = chipStickersMap.get(findViewById(stickerChipGroup.getCheckedChipId()));
//        Message newMessage = new Message(currentUser.getUsername(), recipientUsername, "", System.currentTimeMillis(), selectedSticker.getStickerId());
//        Task<Void> dbPostMessage = mDatabase.getReference().child("messages").push().setValue(newMessage);
//        dbPostMessage.addOnCompleteListener((OnCompleteListener<Void>) completedPostMessage -> {
//            if(completedPostMessage.isSuccessful()){
//                Toast.makeText(getActivity(),"Posted new message: " + newMessage.getStickerId(),Toast.LENGTH_SHORT).show();
//            }
//            else {
//                Toast.makeText(getActivity(),"Unable to post message to DB",Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}