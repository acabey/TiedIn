package edu.neu.tiedin.data;

import java.util.Objects;
import java.util.UUID;

import edu.neu.tiedin.types.ClimberProfile;

public class User {

    private String _id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private ClimberProfile profile;

    public User() {
        this._id = UUID.randomUUID().toString();
    }

    public User(String name, String email, String password, String phoneNumber, ClimberProfile profile) {
        this();
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.profile = profile;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ClimberProfile getProfile() {
        return profile;
    }

    public void setProfile(ClimberProfile profile) {
        this.profile = profile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
