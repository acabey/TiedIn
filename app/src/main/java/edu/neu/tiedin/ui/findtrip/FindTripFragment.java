package edu.neu.tiedin.ui.findtrip;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.function.Function;
import java.util.stream.Collectors;

import edu.neu.tiedin.R;
import edu.neu.tiedin.data.ClimbingTrip;
import edu.neu.tiedin.databinding.FragmentFindTripBinding;
import edu.neu.tiedin.databinding.FragmentHomeBinding;
import edu.neu.tiedin.ui.home.HomeViewModel;
import edu.neu.tiedin.ui.home.TripAdapter;

public class FindTripFragment extends Fragment {

    private static final String TAG = "FindTripFragment";
    public String SHARED_PREFS;
    public String USER_KEY;

    private FirebaseFirestore firestoreDatabase;
    private SharedPreferences sharedpreferences;
    private String userId;
    private FragmentHomeBinding binding;

    private RecyclerView.LayoutManager tripViewLayoutManager;
    private TripAdapter tripAdapter;

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
        tripAdapter = new TripAdapter(homeViewModel.getTrips().getValue(), getContext(), firestoreDatabase, userId);

        // Configure Climb recyclerview
        tripViewLayoutManager = new LinearLayoutManager(getContext());
        binding.recyclerViewListTrips.setAdapter(tripAdapter);
        binding.recyclerViewListTrips.setLayoutManager(tripViewLayoutManager);

        // Get current trips
        final CollectionReference colRef = firestoreDatabase.collection("trips");
        colRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().getDocuments() != null) {
                Log.i(TAG, "FindTripFragment: pulled down trips " + task.getResult().getDocuments().size());
                tripAdapter.addTrips(
                        task.getResult().getDocuments()
                                .stream()
                                .map(
                                        (Function<DocumentSnapshot, ClimbingTrip>) documentSnapshot ->
                                                documentSnapshot.toObject(ClimbingTrip.class))
                                .collect(Collectors.toList()));
            } else {
                Log.e(TAG, "FindTripFragment: failed to pull down Trips");
            }
        });

        // Listen for ongoing changes in trips
        ListenerRegistration listenerRegistration = colRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot != null && !snapshot.getDocumentChanges().isEmpty()) {
                for (DocumentChange change : snapshot.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Log.d(TAG, "Added data: " + change.getDocument().getData());
                        tripAdapter.addTrip(change.getDocument().toObject(ClimbingTrip.class));
                    } else if (change.getType() == DocumentChange.Type.REMOVED) {
                        Log.d(TAG, "Removed data: " + change.getDocument().getData());
                        tripAdapter.removeTrip(change.getDocument().toObject(ClimbingTrip.class));
                    } else if (change.getType() == DocumentChange.Type.MODIFIED) {
                        Log.d(TAG, "Modified data: " + change.getDocument().getData());
                        tripAdapter.modifyTrip(change
                                .getDocument()
                                .toObject(ClimbingTrip.class));
                    } else {
                        Log.d(TAG, "Unknown data change: " + change.getDocument().getData());
                    }
                }
            } else {
                Log.d(TAG, "Current data: null");
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
