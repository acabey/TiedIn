package edu.neu.tiedin.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import edu.neu.tiedin.type.Area;
import edu.neu.tiedin.types.ClimbingStyle;
import edu.neu.tiedin.types.openbeta.composedschema.ComposedArea;

public class ClimbingTrip {
    private User organizer;
    private List<User> participants;
    private LocalDate date;
    private List<ComposedArea> areas;
    private List<ClimbingStyle> styles;
    private String details;

    public ClimbingTrip(User organizer, List<User> participants, LocalDate date, List<ComposedArea> areas, List<ClimbingStyle> styles, String details) {
        this.organizer = organizer;
        this.participants = participants;
        this.date = date;
        this.areas = areas;
        this.styles = styles;
        this.details = details;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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
        return getOrganizer().equals(that.getOrganizer()) && getParticipants().equals(that.getParticipants()) && getDate().equals(that.getDate()) && getAreas().equals(that.getAreas()) && getStyles().equals(that.getStyles()) && getDetails().equals(that.getDetails());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrganizer(), getParticipants(), getDate(), getAreas(), getStyles(), getDetails());
    }
}
