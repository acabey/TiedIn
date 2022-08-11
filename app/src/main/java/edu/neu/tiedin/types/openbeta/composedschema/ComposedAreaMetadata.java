package edu.neu.tiedin.types.openbeta.composedschema;

import java.util.List;
import java.util.stream.Collectors;

import edu.neu.tiedin.AreaByUUIDQuery;

public class ComposedAreaMetadata {

    public Boolean isDestination, leaf;
    public Double lat, lng;
    public List<Double> bbox;

    public Integer left_right_index, leftRightIndex;
    public String mp_id;
    public String area_id, areaId;

    public ComposedAreaMetadata(Boolean isDestination, Boolean leaf, Double lat, Double lng, List<Double> bbox, Integer left_right_index, Integer leftRightIndex, String mp_id, String area_id, String areaId) {
        this.isDestination = isDestination;
        this.leaf = leaf;
        this.lat = lat;
        this.lng = lng;
        this.bbox = bbox;
        this.left_right_index = left_right_index;
        this.leftRightIndex = leftRightIndex;
        this.mp_id = mp_id;
        this.area_id = area_id;
        this.areaId = areaId;
    }

    public static ComposedAreaMetadata fromAreaByUUIDQuery(AreaByUUIDQuery.Metadata o) {
        return new ComposedAreaMetadata(
                o.isDestination,
                o.leaf,
                o.lat,
                o.lng,
                o.bbox.stream().collect(Collectors.toList()),
                o.left_right_index,
                o.leftRightIndex,
                o.mp_id,
                o.area_id,
                o.areaId
                );
    }
}
