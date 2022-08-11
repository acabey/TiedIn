package edu.neu.tiedin.types.openbeta.composedschema;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import edu.neu.tiedin.AreaByUUIDQuery;
import edu.neu.tiedin.AreasByFilterQuery;

public class ComposedArea {
    @Nullable
    public String id;

    @Nullable
    public String uuid;

    @Nullable
    public String area_name;

    @Nullable
    public String areaName;

    @Nullable
    public ComposedAreaMetadata metadata;

    @Nullable
    public List<ComposedClimb> climbs;

    @Nullable
    public List<ComposedClimb> children;

    @Nullable
    public List<String> ancestors;

    @Nullable
    public Object aggregate;

    @Nullable
    public ComposedContent content;

    @Nullable
    public String pathHash;

    @Nullable
    public List<String> pathTokens;

    @Nullable
    public Float density;

    @Nullable
    public Integer totalClimbs;

    @Nullable
    public List<ComposedMediaTagType> media;

    public ComposedArea() {}

    public ComposedArea(@Nullable String id, @Nullable String uuid, @Nullable String area_name, @Nullable String areaName, @Nullable ComposedAreaMetadata metadata, @Nullable List<ComposedClimb> climbs, @Nullable List<ComposedClimb> children, @Nullable List<String> ancestors, @Nullable Object aggregate, @Nullable ComposedContent content, @Nullable String pathHash, @Nullable List<String> pathTokens, Float density, @Nullable Integer totalClimbs, @Nullable List<ComposedMediaTagType> media) {
        this.id = id;
        this.uuid = uuid;
        this.area_name = area_name;
        this.areaName = areaName;
        this.metadata = metadata;
        this.climbs = climbs;
        this.children = children;
        this.ancestors = ancestors;
        this.aggregate = null;
        this.content = content;
        this.pathHash = pathHash;
        this.pathTokens = pathTokens;
        this.density = density;
        this.totalClimbs = totalClimbs;
        this.media = media;
    }

    public static ComposedArea fromAreaByUUIDQuery(AreaByUUIDQuery.Area o) {
        return new ComposedArea(
                o.id,
                o.uuid,
                o.area_name,
                o.areaName,
                ComposedAreaMetadata.fromAreaByUUIDQuery(o.metadata),
                o.climbs.stream().map(ComposedClimb::fromAreaByUUIDQuery).collect(Collectors.toList()),
                o.children.stream().map(ComposedClimb::fromAreaByUUIDQueryChild).collect(Collectors.toList()),
                o.ancestors.stream().collect(Collectors.toList()),
                null, // AggregateType is not implemented, always null
                ComposedContent.fromAreaByUUIDQuery(o.content),
                null,
                null,
                null,
                o.totalClimbs,
                null
        );
    }

    public static ComposedArea fromAreaFilterQuery(AreasByFilterQuery.Area o) {
        return new ComposedArea(
                o.id,
                o.uuid,
                o.areaName,
                o.areaName,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}