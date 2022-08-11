package edu.neu.tiedin.data;

import java.util.Objects;

import edu.neu.tiedin.types.ClimberProfile;

public class User {

    private String _id;
    private java.lang.String name;
    private java.lang.String email;
    private java.lang.String phoneNumber;
    private ClimberProfile profile;

    public User(java.lang.String _id, java.lang.String name, java.lang.String email, java.lang.String phoneNumber, ClimberProfile profile) {
        this._id = _id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profile = profile;
    }

    public java.lang.String get_id() {
        return _id;
    }

    public void set_id(java.lang.String _id) {
        this._id = _id;
    }

    public java.lang.String getName() {
        return name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }

    public java.lang.String getEmail() {
        return email;
    }

    public void setEmail(java.lang.String email) {
        this.email = email;
    }

    public java.lang.String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(java.lang.String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ClimberProfile getProfile() {
        return profile;
    }

    public void setProfile(ClimberProfile profile) {
        this.profile = profile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return get_id().equals(user.get_id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(get_id());
    }
}
