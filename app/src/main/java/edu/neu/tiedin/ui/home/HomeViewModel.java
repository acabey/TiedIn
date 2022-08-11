package edu.neu.tiedin.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import edu.neu.tiedin.data.ClimbingTrip;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<ClimbingTrip>> trips;

    public HomeViewModel() {
        trips = new MutableLiveData<>(new ArrayList<>());
    }

    public MutableLiveData<ArrayList<ClimbingTrip>> getTrips() {
        return trips;
    }
}