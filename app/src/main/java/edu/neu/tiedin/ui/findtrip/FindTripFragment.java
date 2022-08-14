package edu.neu.tiedin.ui.findtrip;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
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
import edu.neu.tiedin.ui.home.TripAdapter;

public class FindTripFragment extends Fragment {

    private static final String TAG = "FindTripFragment";
    public String SHARED_PREFS;
    public String USER_KEY;

    private FirebaseFirestore firestoreDatabase;
    private SharedPreferences sharedpreferences;
    private String userId;
    private FragmentFindTripBinding binding;

    private RecyclerView.LayoutManager tripViewLayoutManager;
    private TripAdapter tripAdapter;

    // Location services
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    static final int DEFAULT_UPDATE_INTERVAL = 100;
    static final int FASTEST_UPDATE_INTERVAL = 3000;
    static final int MAX_WAIT_TIME = 100;
    static final int DEFAULT_PRIORITY = Priority.PRIORITY_BALANCED_POWER_ACCURACY;
    private static final byte PERMISSION_COARSE_LOCATION = 2;
    private static final String[] locationPermissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
    private ActivityResultLauncher<String> activityResultLauncher;

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


        // Set location properties
        locationRequest = LocationRequest.create()
                .setInterval(DEFAULT_UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL)
                .setPriority(DEFAULT_PRIORITY)
                .setMaxWaitTime(MAX_WAIT_TIME);

        // Get coarse location permissions
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                locationRequest.setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // TODO
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        });

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FindTripViewModel findTripViewModel =
                new ViewModelProvider(this).get(FindTripViewModel.class);

        binding = FragmentFindTripBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configure trip pull from DB
        tripAdapter = new TripAdapter(findTripViewModel.getTrips().getValue(), getContext(), firestoreDatabase, userId, findTripViewModel.getLocation());

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

        // Start location tracking
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                if (!locationAvailability.isLocationAvailable()) {
                    Log.d(TAG, "onLocationAvailability: location unavailable");

                } else {
                    Log.d(TAG, "onLocationAvailability: location available");
                }
            }

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d(TAG, "onLocationAvailability: location received, updating");
                findTripViewModel.getLocation().setValue(locationResult.getLastLocation());

            }
        };

        // Get coarse location permissions
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: location permissions not granted, requesting permission");
            activityResultLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        } else {
            Log.d(TAG, "onCreate: location permissions already granted");
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
