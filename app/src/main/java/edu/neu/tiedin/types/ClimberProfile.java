package edu.neu.tiedin.types;

import android.media.Image;

import java.util.List;

import edu.neu.tiedin.type.Area;

public class ClimberProfile {
    private Image picture;
    private List<ClimbingStyle> style;
    private ClimbingSkill skillLevel;
    private List<ClimbingEquipment> equipment;
    private String location;
    private List<Area> preferredAreas;
    private List<ClimbTime> preferredTimes;
}
