package edu.neu.tiedin.ui.plantrip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.neu.tiedin.databinding.FragmentPlanTripBinding;

public class PlanTripFragment extends Fragment {

    private FragmentPlanTripBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PlanTripViewModel planTripViewModel =
                new ViewModelProvider(this).get(PlanTripViewModel.class);

        binding = FragmentPlanTripBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}