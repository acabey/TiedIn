package edu.neu.tiedin.data;


import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import edu.neu.tiedin.types.ClimbingStyle;
import edu.neu.tiedin.types.openbeta.composedschema.ComposedArea;

public class ClimbingTrip {
    private String organizerUserId;
    private List<String> participantUserIds;
    private Long epochDate;
    private List<ComposedArea> areas;
    private List<ClimbingStyle> styles;
    private String details;

    public ClimbingTrip() {}

    public ClimbingTrip(String organizerUserId, List<String> participantUserIds, Long epochDate, List<ComposedArea> areas, List<ClimbingStyle> styles, String details) {
        this.organizerUserId = organizerUserId;
        this.participantUserIds = participantUserIds;
        this.epochDate = epochDate;
        this.areas = areas;
        this.styles = styles;
        this.details = details;
    }

    public String getOrganizerUserId() {
        return organizerUserId;
    }

    public void setOrganizerUserId(String organizerUserId) {
        this.organizerUserId = organizerUserId;
    }

    public List<String> getParticipantUserIds() {
        return participantUserIds;
    }

    public void setParticipantUserIds(List<String> participantUserIds) {
        this.participantUserIds = participantUserIds;
    }

    public Long getEpochDate() {
        return epochDate;
    }

    public void setEpochDate(Long epochDate) {
        this.epochDate = epochDate;
    }

    public List<ComposedArea> getAreas() {
        return areas;
    }

    public void setAreas(List<ComposedArea> areas) {
        this.areas = areas;
    }

    public List<ClimbingStyle> getStyles() {
        return styles;
    }

    public void setStyles(List<ClimbingStyle> styles) {
        this.styles = styles;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClimbingTrip)) return false;
        ClimbingTrip that = (ClimbingTrip) o;
        return getOrganizerUserId().equals(that.getOrganizerUserId()) && getParticipantUserIds().equals(that.getParticipantUserIds()) && getEpochDate().equals(that.getEpochDate()) && getAreas().equals(that.getAreas()) && getStyles().equals(that.getStyles()) && getDetails().equals(that.getDetails());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrganizerUserId(), getParticipantUserIds(), getEpochDate(), getAreas(), getStyles(), getDetails());
    }
}
