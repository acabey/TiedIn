package edu.neu.tiedin.ui.findtrip;

import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import edu.neu.tiedin.data.ClimbingTrip;

public class FindTripViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<ClimbingTrip>> trips;
    private final MutableLiveData<Location> location;

    public FindTripViewModel() {
        trips = new MutableLiveData<>(new ArrayList<>());
        location = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<ClimbingTrip>> getTrips() {
        return trips;
    }

    public MutableLiveData<Location> getLocation() {
        return location;
    }
}