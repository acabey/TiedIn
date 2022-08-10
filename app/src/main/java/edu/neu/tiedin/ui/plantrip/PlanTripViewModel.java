package edu.neu.tiedin.ui.plantrip;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.neu.tiedin.type.Area;
import edu.neu.tiedin.type.Climb;
import edu.neu.tiedin.type.ClimbType;

public class PlanTripViewModel extends ViewModel {
    private final MutableLiveData<LocalDate> planDate;
    private final MutableLiveData<List<Area>> planAreas;
    private final MutableLiveData<List<ClimbType>> planClimbTypes; // TODO Default to user preferences...
    private final MutableLiveData<List<Climb>> planObjectives;
    private final MutableLiveData<String> planDetails;

    public PlanTripViewModel() {
        planDate = new MutableLiveData<>();
        planAreas = new MutableLiveData<>();
        planClimbTypes = new MutableLiveData<>();
        planObjectives = new MutableLiveData<>();
        planDetails = new MutableLiveData<>();

        // Default plan date to today
        LocalDate today = LocalDate.now();
        planDate.setValue(today);
    }

    public MutableLiveData<LocalDate> getPlanDate() {
        return planDate;
    }

    public void setPlanDate(LocalDate date) {
        planDate.setValue(date);
    }

    public MutableLiveData<List<Area>> getPlanAreas() {
        return planAreas;
    }

    public MutableLiveData<List<ClimbType>> getPlanClimbTypes() {
        return planClimbTypes;
    }

    public MutableLiveData<List<Climb>> getPlanObjectives() {
        return planObjectives;
    }

    public MutableLiveData<String> getPlanDetails() {
        return planDetails;
    }
}