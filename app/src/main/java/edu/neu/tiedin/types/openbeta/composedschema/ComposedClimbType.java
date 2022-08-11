package edu.neu.tiedin.types.openbeta.composedschema;

import java.util.ArrayList;
import java.util.List;

import edu.neu.tiedin.AreaByUUIDQuery;
import edu.neu.tiedin.types.ClimbingStyle;

public class ComposedClimbType {
    public Boolean trad, sport, bouldering, alpine, mixed, aid, tr;

    public ComposedClimbType(Boolean trad, Boolean sport, Boolean bouldering, Boolean alpine, Boolean mixed, Boolean aid, Boolean tr) {
        this.trad = trad;
        this.sport = sport;
        this.bouldering = bouldering;
        this.alpine = alpine;
        this.mixed = mixed;
        this.aid = aid;
        this.tr = tr;
    }

    public static ComposedClimbType fromAreaByUUIDQuery(AreaByUUIDQuery.Type o) {
        return new ComposedClimbType(
                o.trad,
                o.sport,
                o.bouldering,
                o.alpine,
                o.mixed,
                o.aid,
                o.tr
        );
    }

    public List<ClimbingStyle> toClimbingStyles() {
        ArrayList<ClimbingStyle> styles = new ArrayList<>();

        if (this.trad) styles.add(ClimbingStyle.TRADITIONAL);
        if (this.sport) styles.add(ClimbingStyle.SPORT);
        if (this.bouldering) styles.add(ClimbingStyle.BOULDER);
        if (this.tr) styles.add(ClimbingStyle.TOPROPE);

        return styles;
    }
}
