package uk.ac.ncl.prov.lib.dsl.prov;

/**
 * Created with IntelliJ IDEA.
 * User: hugofirth
 * Date: 10/12/2013
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public enum Relation {

    WASGENERATEDBY("WasGeneratedBy"),
    USED("Used"),
    WASINFORMEDBY("WasInformedBy"),
    WASDERIVEDFROM("WasDerivedFrom"),
    WASATTRIBUTEDTO("WasAttributedTo"),
    WASASSOCIATEDWITH("WasAssociatedWith"),
    ACTEDONBEHALFOF("ActedOnBehalfOf");

    private final String assoc;

    Relation(String assoc) {
        this.assoc = assoc;
    }

    public static Relation withName(String s) throws IllegalArgumentException {
        if(s != null) {
            for(Relation r : Relation.values()) {
                if(s.equalsIgnoreCase(r.assoc)) {
                    return r;
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
