package uk.ac.ncl.prov.lib.seed;

import uk.ac.ncl.prov.lib.graph.edge.Edge;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by hugofirth on 14/03/2014.
 */
public class Seed {

    private final List<Edge> edgess;

    public Seed(List<Edge> edges)
    {
        this.edges = edges;
    }

    public List<Relationship> getEdges()
    {
        return this.edges;
    }

    public Set<Relationship> getUniqueEdges()
    {
        return new HashSet<>(this.edges);
    }

}
