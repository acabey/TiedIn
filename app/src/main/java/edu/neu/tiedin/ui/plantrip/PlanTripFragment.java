package edu.neu.tiedin.ui.plantrip;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.neu.tiedin.databinding.FragmentPlanTripBinding;

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

//        ArrayList<String> crags = new ArrayList<>(Arrays.asList("Rumney", "Gunks", "Echo"));
//        ArrayAdapter<String> testAdapter = new ArrayAdapter<>
//                (getActivity(), android.R.layout.select_dialog_item, crags);

        ClimbingAreaSuggestFilter areaSuggestFilter = new ClimbingAreaSuggestFilter(getActivity(), android.R.layout.simple_dropdown_item_1line);
        binding.editTextPlanArea.setAdapter(areaSuggestFilter);
        binding.editTextPlanArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String selected = (String) parent.getItemAtPosition(position);
//                int pos = Arrays.asList().indexOf(selected);
//                Log.d(TAG, "onItemClick: " + selected);
            }
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