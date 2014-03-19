package uk.ac.ncl.prov.lib.prov_dm;

import uk.ac.ncl.prov.lib.graph.vertex.VertexLabel;

/**
 * Created with IntelliJ IDEA.
 * User: hugofirth
 * Date: 10/12/2013
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public enum Type implements VertexLabel {

    ENTITY("Entity"),
    AGENT("Agent"),
    ACTIVITY("Activity");

    private final String assoc;

    Type(String assoc) {
        this.assoc = assoc;
    }

    public static Type withName(String s) throws IllegalArgumentException {
        if(s != null) {
            for(Type t : Type.values()) {
                if(s.equalsIgnoreCase(t.assoc)) {
                    return t;
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
