package edu.neu.tiedin.ui.findtrip;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import edu.neu.tiedin.data.ClimbingTrip;

public class FindTripViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<ClimbingTrip>> trips;

    public FindTripViewModel() {
        trips = new MutableLiveData<>(new ArrayList<>());
    }

    public MutableLiveData<ArrayList<ClimbingTrip>> getTrips() {
        return trips;
    }
}