package edu.neu.tiedin.ui.plantrip;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PlanTripViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PlanTripViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}