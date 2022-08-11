package edu.neu.tiedin.types.openbeta.composedschema;

import edu.neu.tiedin.AreaByUUIDQuery;

public class ComposedContent {

    public String description, location, protection;

    public ComposedContent() {}

    public ComposedContent(String description, String location, String protection) {
        this.description = description;
        this.location = location;
        this.protection = protection;
    }
    public static ComposedContent fromAreaByUUIDQuery(AreaByUUIDQuery.Content o) {
        return new ComposedContent(o.description, "", "");
    }
}
