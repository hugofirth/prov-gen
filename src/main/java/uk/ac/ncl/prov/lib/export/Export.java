package uk.ac.ncl.prov.lib.export;


import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.io.IOException;

/**
 * Created by hugofirth on 04/05/2014.
 */
public interface Export {

    public void setFileDestination(String dest) throws IOException;
    public void export() throws IOException;
    public void serializeNode(Node n);
    public void serializeRelationship(Relationship r);

}
