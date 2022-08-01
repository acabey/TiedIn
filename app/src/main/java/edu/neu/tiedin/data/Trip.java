package edu.neu.tiedin.data;

import java.time.LocalDateTime;
import java.util.List;

import edu.neu.tiedin.type.Climb;

public class Trip {
    private User organizer;
    private List<User> participants;
    private String location;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private List<Climb> objectives;
    private String details;
}
