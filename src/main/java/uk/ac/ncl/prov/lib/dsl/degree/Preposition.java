package uk.ac.ncl.prov.lib.dsl.degree;

/**
 * Created with IntelliJ IDEA.
 * User: hugofirth
 * Date: 10/12/2013
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public enum Preposition {

    IN("in"),
    OUT("out"),
    TOTAL("total");

    private final String assoc;

    Preposition(String assoc) {
        this.assoc = assoc;
    }

    public static Preposition withName(String s) throws IllegalArgumentException {
        if(s != null) {
            for(Preposition p : Preposition.values()) {
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

