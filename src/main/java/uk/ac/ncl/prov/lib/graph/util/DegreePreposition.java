package uk.ac.ncl.prov.lib.graph.util;

import uk.ac.ncl.prov.lib.constraint.Term;

public enum DegreePreposition implements Term {

    IN("in"),
    OUT("out"),
    TOTAL("total");

    private final String assoc;

    DegreePreposition(String assoc) {
        this.assoc = assoc;
    }

    public static DegreePreposition withName(String s) throws IllegalArgumentException {
        if(s != null) {
            for(DegreePreposition p : DegreePreposition.values()) {
                if(s.equalsIgnoreCase(p.assoc)) {
                    return p;
                }
            }
        }
        throw new IllegalArgumentException("No constant with text " + s + " found");
    }

    @Override
    public String toString() {
        return this.assoc;
    }
}
