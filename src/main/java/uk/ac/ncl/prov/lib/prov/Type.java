package uk.ac.ncl.prov.lib.prov;

import org.neo4j.graphdb.Label;
import uk.ac.ncl.prov.lib.constraint.Term;
import uk.ac.ncl.prov.lib.graph.vertex.VertexLabel;

/**
 * Created with IntelliJ IDEA.
 * User: hugofirth
 * Date: 10/12/2013
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public enum Type implements VertexLabel, Term, Label {

    ENTITY("Entity"),
    AGENT("Agent"),
    ACTIVITY("Activity");

    private final String name;

    Type(String name)
    {
        this.name = name;
    }

    public static Type withName(String name) throws IllegalArgumentException
    {
        if(name != null) {
            for(Type t : Type.values()) {
                if(name.equalsIgnoreCase(t.name)) {
                    return t;
                }
            }
        }
        throw new IllegalArgumentException("No vertex label with name \"" + name + "\" found");
    }

    public static Boolean existsWithName(String name)
    {
        for(Type t : Type.values()) {
            if(name.equalsIgnoreCase(t.name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString()
    {
        return "Vertex label with name \""+this.name+"\"";
    }

    @Override
    public String getName()
    {
        return this.name;
    }
}
