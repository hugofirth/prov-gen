package uk.ac.ncl.prov.lib.generator;


import uk.ac.ncl.prov.lib.constraint.Constraint;
import uk.ac.ncl.prov.lib.graph.edge.Edge;
import uk.ac.ncl.prov.lib.graph.vertex.Vertex;
import uk.ac.ncl.prov.lib.seed.Seed;

import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * Created by hugofirth on 15/01/2014.
 */
public interface Generator {

    public Set<Vertex> getVertices();
    public Set<Edge> getEdges();
    public void execute(Integer size, Integer order, Integer numGraphs);

}
