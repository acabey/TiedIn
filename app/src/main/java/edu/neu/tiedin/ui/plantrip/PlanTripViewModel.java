package edu.neu.tiedin.ui.plantrip;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import edu.neu.tiedin.AreaByUUIDQuery;
import edu.neu.tiedin.AreasByFilterQuery;
import edu.neu.tiedin.type.Climb;
import edu.neu.tiedin.types.ClimbingStyle;

public class PlanTripViewModel extends ViewModel {
    private static final String TAG = "PlanTripViewModel";

    private final MutableLiveData<LocalDate> planDate;
    private final MutableLiveData<List<AreasByFilterQuery.Area>> planAreas;
    private final MutableLiveData<List<ClimbingStyle>> planClimbStyles; // TODO Default to user preferences...
    private final MutableLiveData<List<AreaByUUIDQuery.Climb>> planObjectives;
    private final MutableLiveData<String> planDetails;

    public PlanTripViewModel() {
        // Default plan date to today
        planDate = new MutableLiveData<>(LocalDate.now());
        planAreas = new MutableLiveData<>(new ArrayList<>());
        planClimbStyles = new MutableLiveData<>(new ArrayList<>());
        planObjectives = new MutableLiveData<>(new ArrayList<>());
        planDetails = new MutableLiveData<>("");
    }

    public MutableLiveData<LocalDate> getPlanDate() {
        return planDate;
    }

    public void setPlanDate(LocalDate date) {
        planDate.setValue(date);
    }

    public MutableLiveData<List<AreasByFilterQuery.Area>> getPlanAreas() {
        return planAreas;
    }

    public MutableLiveData<List<ClimbingStyle>> getPlanClimbStyles() {
        return planClimbStyles;
    }

    public MutableLiveData<List<AreaByUUIDQuery.Climb>> getPlanObjectives() {
        return planObjectives;
    }

    public MutableLiveData<String> getPlanDetails() {
        return planDetails;
    }

    public void setPlanDetails(String val) {
        planDetails.setValue(val);
    }

    public void addPlannedArea(AreasByFilterQuery.Area area) {
        planAreas.getValue().add(area);
    }

    public void removePlannedArea(AreasByFilterQuery.Area area) {
        planAreas.getValue().remove(area);
    }

    public void setPlanClimbStyles(List<ClimbingStyle> climbTypes) {
        planClimbStyles.getValue().clear();
        planClimbStyles.getValue().addAll(climbTypes);
        Log.d(TAG, "setPlanClimbStyles: " + climbTypes.toArray().toString());
    }

    public void addPlannedObjective(AreaByUUIDQuery.Climb selected) {
        planObjectives.getValue().add(selected);
    }

    public void removePlannedObjective(AreaByUUIDQuery.Climb selected) {
        planObjectives.getValue().remove(selected);
    }
}