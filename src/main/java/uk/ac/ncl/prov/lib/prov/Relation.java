package uk.ac.ncl.prov.lib.prov;

import org.neo4j.graphdb.RelationshipType;
import uk.ac.ncl.prov.lib.graph.edge.EdgeLabel;

/**
 * Created with IntelliJ IDEA.
 * User: hugofirth
 * Date: 10/12/2013
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public enum Relation implements EdgeLabel, RelationshipType {
    WASGENERATEDBY("WasGeneratedBy"),
    USED("Used"),
    WASINFORMEDBY("WasInformedBy"),
    WASDERIVEDFROM("WasDerivedFrom"),
    WASATTRIBUTEDTO("WasAttributedTo"),
    WASASSOCIATEDWITH("WasAssociatedWith"),
    ACTEDONBEHALFOF("ActedOnBehalfOf"),
    ALTERNATEOF("AlternateOf"),
    SPECIALIZATIONOF("SpecializationOf"),
    HADMEMBER("HadMember"),
    WASINVALIDATEDBY("WasInvalidatedBy"),
    WASSTARTEDBY("WasStartedBy"),
    WASENDEDBY("WasEndedBy");

    private final String name;

    Relation(String name)
    {
        this.name = name;
    }

    public static Relation withName(String name) throws IllegalArgumentException
    {
        if(name != null) {
            for(Relation r : Relation.values()) {
                if(name.equalsIgnoreCase(r.name)) {
                    return r;
                }
            }
        }
        throw new IllegalArgumentException("No edge label with name \"" + name + "\" found");
    }

    public static Boolean existsWithName(String name)
    {
        for(Relation r : Relation.values()) {
            if(name.equalsIgnoreCase(r.name)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString()
    {
        return "Edge label with name \""+this.name+"\"";
    }

    @Override
    public String getName()
    {
        return this.name;
    }

}
