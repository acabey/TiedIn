package edu.neu.tiedin.ui.settings;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import edu.neu.tiedin.types.ClimbTime;
import edu.neu.tiedin.types.ClimbingEquipment;
import edu.neu.tiedin.types.ClimbingSkill;
import edu.neu.tiedin.types.ClimbingStyle;
import edu.neu.tiedin.types.openbeta.composedschema.ComposedArea;

public class SettingsViewModel extends ViewModel {
    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();
    private MutableLiveData<String> phoneNumber = new MutableLiveData<>();

    private MutableLiveData<String> icon;
    private MutableLiveData<String> location = new MutableLiveData<>();
    private MutableLiveData<List<ClimbingStyle>> styles = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<ClimbingSkill> skillLevel = new MutableLiveData<>();
    private MutableLiveData<List<ComposedArea>> preferredAreas = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<ClimbTime>> preferredTimes = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<ClimbingEquipment>> equipment = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<String> getName() {
        return name;
    }

    public void setName(MutableLiveData<String> name) {
        this.name = name;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setPassword(MutableLiveData<String> password) {
        this.password = password;
    }

    public MutableLiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(MutableLiveData<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public MutableLiveData<String> getIcon() {
        return icon;
    }

    public void setIcon(MutableLiveData<String> icon) {
        this.icon = icon;
    }

    public MutableLiveData<String> getLocation() {
        return location;
    }

    public void setLocation(MutableLiveData<String> location) {
        this.location = location;
    }

    public MutableLiveData<List<ClimbingStyle>> getStyles() {
        return styles;
    }

    public void setStyles(MutableLiveData<List<ClimbingStyle>> styles) {
        this.styles = styles;
    }

    public MutableLiveData<ClimbingSkill> getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(MutableLiveData<ClimbingSkill> skillLevel) {
        this.skillLevel = skillLevel;
    }

    public MutableLiveData<List<ComposedArea>> getPreferredAreas() {
        return preferredAreas;
    }

    public void setPreferredAreas(MutableLiveData<List<ComposedArea>> preferredAreas) {
        this.preferredAreas = preferredAreas;
    }

    public MutableLiveData<List<ClimbTime>> getPreferredTimes() {
        return preferredTimes;
    }

    public void setPreferredTimes(MutableLiveData<List<ClimbTime>> preferredTimes) {
        this.preferredTimes = preferredTimes;
    }

    public MutableLiveData<List<ClimbingEquipment>> getEquipment() {
        return equipment;
    }

    public void setEquipment(MutableLiveData<List<ClimbingEquipment>> equipment) {
        this.equipment = equipment;
    }
}
