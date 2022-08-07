package edu.neu.tiedin.ui.findtrip;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FindTripViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public FindTripViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}