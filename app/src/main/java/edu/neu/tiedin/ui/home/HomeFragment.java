package edu.neu.tiedin.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import edu.neu.tiedin.R;
import edu.neu.tiedin.data.ClimbingTrip;
import edu.neu.tiedin.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    public String SHARED_PREFS;
    public String USER_KEY;

    private FirebaseFirestore firestoreDatabase;
    private SharedPreferences sharedpreferences;
    private String userId;
    private FragmentHomeBinding binding;

    private RecyclerView.LayoutManager tripViewLayoutManager;
    private RecyclerView.Adapter tripAdapter;

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
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configure trip pull from DB
        tripAdapter = new TripAdapter(homeViewModel.getTrips().getValue(), getContext());

        // Configure Climb recyclerview
        binding.recyclerViewListTrips.setAdapter(tripAdapter);
        binding.recyclerViewListTrips.setLayoutManager(tripViewLayoutManager);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}