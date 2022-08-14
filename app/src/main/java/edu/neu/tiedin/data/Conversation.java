package edu.neu.tiedin.data;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Conversation {
    private String _id;
    private List<User> participants;

    public Conversation() {
        this._id = UUID.randomUUID().toString();
    }

    public Conversation(List<User> participants) {
        this();
        this.participants = participants;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
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
