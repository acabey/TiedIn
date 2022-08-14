package edu.neu.tiedin.data;


import java.util.Objects;
import java.util.UUID;

public class Message {
    private String _id;
    private String conversationId;
    private String sender;
    private String payload;
    private long timeSent;

    public Message() {
        _id = UUID.randomUUID().toString();
    }

    public Message(String sender, String conversationId, String payload, long timeSent) {
        this();
        this.sender = sender;
        this.conversationId = conversationId;
        this.payload = payload;
        this.timeSent = timeSent;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void setTimeSent(long timeSent) {
        this.timeSent = timeSent;
    }

    public String getSender() {
        return sender;
    }

    public String getPayload() {
        return payload;
    }

    public long getTimeSent() {
        return timeSent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message = (Message) o;
        return get_id().equals(message.get_id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(get_id());
    }
}
