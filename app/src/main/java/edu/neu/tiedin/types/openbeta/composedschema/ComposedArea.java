package edu.neu.tiedin.types.openbeta.composedschema;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ComposedArea {
    @NonNull
    public String id;

    @NonNull
    public String uuid;

    @NonNull
    public String area_name;

    @NonNull
    public String areaName;

    @NonNull
    public ComposedMetadata metadata;

    @Nullable
    public List<ComposedClimb> climbs;

    @Nullable
    public List<ComposedChild> children;

    @NonNull
    public List<String> ancestors;

    @Nullable
    public ComposedAggregateType aggregate;

    @Nullable
    public ComposedContent content;

    @NonNull
    public String pathHash;

    @NonNull
    public List<String> pathTokens;

    @NonNull
    public float density;

    @NonNull
    public Integer totalClimbs;

    @Nullable
    public List<ComposedMediaTagType> media;

    public ComposedArea(@NonNull String id, @NonNull String uuid, @NonNull String area_name, @NonNull String areaName, @NonNull ComposedMetadata metadata, @Nullable List<ComposedClimb> climbs, @Nullable List<ComposedChild> children, @NonNull List<String> ancestors, @Nullable ComposedAggregateType aggregate, @Nullable ComposedContent content, @NonNull String pathHash, @NonNull List<String> pathTokens, float density, @NonNull Integer totalClimbs, @Nullable List<ComposedMediaTagType> media) {
        this.id = id;
        this.uuid = uuid;
        this.area_name = area_name;
        this.areaName = areaName;
        this.metadata = metadata;
        this.climbs = climbs;
        this.children = children;
        this.ancestors = ancestors;
        this.aggregate = aggregate;
        this.content = content;
        this.pathHash = pathHash;
        this.pathTokens = pathTokens;
        this.density = density;
        this.totalClimbs = totalClimbs;
        this.media = media;
    }

}
