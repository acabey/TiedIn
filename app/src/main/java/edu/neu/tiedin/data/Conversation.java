package edu.neu.tiedin.data;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Conversation {
    private String _id;
    private List<String> participantIds;

    public Conversation() {
        this._id = UUID.randomUUID().toString();
    }

    public Conversation(List<String> participantIds) {
        this();
        this.participantIds = participantIds;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<String> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conversation)) return false;
        Conversation that = (Conversation) o;
        return get_id().equals(that.get_id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(get_id());
    }
}
