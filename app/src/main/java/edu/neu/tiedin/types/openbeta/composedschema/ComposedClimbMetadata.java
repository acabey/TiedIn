package edu.neu.tiedin.types.openbeta.composedschema;

public class ComposedClimbMetadata {
    Double lat, lng;
    Integer left_right_index;
    Integer leftRightIndex;
    String mp_id;
    String climb_id;
    String climbId;

    public ComposedClimbMetadata() {}

    public ComposedClimbMetadata(Double lat, Double lng, Integer left_right_index, Integer leftRightIndex, String mp_id, String climb_id, String climbId) {
        this.lat = lat;
        this.lng = lng;
        this.left_right_index = left_right_index;
        this.leftRightIndex = leftRightIndex;
        this.mp_id = mp_id;
        this.climb_id = climb_id;
        this.climbId = climbId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Integer getLeft_right_index() {
        return left_right_index;
    }

    public void setLeft_right_index(Integer left_right_index) {
        this.left_right_index = left_right_index;
    }

    public Integer getLeftRightIndex() {
        return leftRightIndex;
    }

    public void setLeftRightIndex(Integer leftRightIndex) {
        this.leftRightIndex = leftRightIndex;
    }

    public String getMp_id() {
        return mp_id;
    }

    public void setMp_id(String mp_id) {
        this.mp_id = mp_id;
    }

    public String getClimb_id() {
        return climb_id;
    }

    public void setClimb_id(String climb_id) {
        this.climb_id = climb_id;
    }

    public String getClimbId() {
        return climbId;
    }

    public void setClimbId(String climbId) {
        this.climbId = climbId;
    }
}
