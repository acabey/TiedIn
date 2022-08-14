package edu.neu.tiedin.types;

import java.util.List;

import edu.neu.tiedin.types.openbeta.composedschema.ComposedArea;

public class ClimberProfile {
    private String icon;
    private String location;
    private List<ClimbingStyle> styles;
    private ClimbingSkill skillLevel;
    private List<ComposedArea> preferredAreas;
    private List<ClimbTime> preferredTimes;
    private List<ClimbingEquipment> equipment;

    public ClimberProfile() {}
    public ClimberProfile(String icon, String location, List<ClimbingStyle> styles, ClimbingSkill skillLevel, List<ComposedArea> preferredAreas, List<ClimbTime> preferredTimes, List<ClimbingEquipment> equipment) {
        this.icon = icon;
        this.location = location;
        this.styles = styles;
        this.skillLevel = skillLevel;
        this.preferredAreas = preferredAreas;
        this.preferredTimes = preferredTimes;
        this.equipment = equipment;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<ClimbingStyle> getStyles() {
        return styles;
    }

    public void setStyles(List<ClimbingStyle> styles) {
        this.styles = styles;
    }

    public ClimbingSkill getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(ClimbingSkill skillLevel) {
        this.skillLevel = skillLevel;
    }

    public List<ComposedArea> getPreferredAreas() {
        return preferredAreas;
    }

    public void setPreferredAreas(List<ComposedArea> preferredAreas) {
        this.preferredAreas = preferredAreas;
    }

    public List<ClimbTime> getPreferredTimes() {
        return preferredTimes;
    }

    public void setPreferredTimes(List<ClimbTime> preferredTimes) {
        this.preferredTimes = preferredTimes;
    }

    public List<ClimbingEquipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<ClimbingEquipment> equipment) {
        this.equipment = equipment;
    }
}
