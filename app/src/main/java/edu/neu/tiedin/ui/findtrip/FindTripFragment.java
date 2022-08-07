package edu.neu.tiedin.ui.findtrip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.neu.tiedin.databinding.FragmentFindTripBinding;

public class FindTripFragment extends Fragment {


    private FragmentFindTripBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FindTripViewModel findTripViewModel =
                new ViewModelProvider(this).get(FindTripViewModel.class);

        binding = FragmentFindTripBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}