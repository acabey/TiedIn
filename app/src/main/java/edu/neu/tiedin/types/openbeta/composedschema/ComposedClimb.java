package edu.neu.tiedin.types.openbeta.composedschema;

import java.util.List;

import edu.neu.tiedin.AreaByUUIDQuery;

public class ComposedClimb {

    public String id, uuid, name, fa, yds;
    public ComposedClimbType type;
    public ComposedSafetyEnum safety;
    public ComposedClimbMetadata metadata;
    public ComposedContent content;
    public List<String> pathTokens, ancestors;
    public List<ComposedMediaTagType> media;

    public ComposedClimb(String id, String uuid, String name, String fa, String yds, ComposedClimbType type, ComposedSafetyEnum safety, ComposedClimbMetadata metadata, ComposedContent content, List<String> pathTokens, List<String> ancestors, List<ComposedMediaTagType> media) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.fa = fa;
        this.yds = yds;
        this.type = type;
        this.safety = safety;
        this.metadata = metadata;
        this.content = content;
        this.pathTokens = pathTokens;
        this.ancestors = ancestors;
        this.media = media;
    }

    public static ComposedClimb fromAreaByUUIDQuery(AreaByUUIDQuery.Climb o) {
        return new ComposedClimb(
                o.id,
                o.uuid,
                o.name,
                null,
                o.yds,
                ComposedClimbType.fromAreaByUUIDQuery(o.type),
                null,
                null,
                null,
                null,
                null,
                null);
    }

    public static ComposedClimb fromAreaByUUIDQueryChild(AreaByUUIDQuery.Child o) {
        return new ComposedClimb(
                o.id,
                o.uuid,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }
}
