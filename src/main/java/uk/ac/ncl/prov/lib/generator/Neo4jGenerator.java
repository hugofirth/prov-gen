package uk.ac.ncl.prov.lib.generator;

import uk.ac.ncl.prov.lib.constraint.Constraint;
import uk.ac.ncl.prov.lib.graph.edge.Edge;
import uk.ac.ncl.prov.lib.graph.vertex.Vertex;
import uk.ac.ncl.prov.lib.prov.Definition;
import uk.ac.ncl.prov.lib.seed.Seed;

import java.util.*;


public class Neo4jGenerator implements Generator {

    //Fields
    private List<Operation> operations;
    private Set<Vertex> vertices;
    private Set<Edge> edges;

    public Neo4jGenerator(Seed seed, List<Constraint> constraints)
    {
        this.vertices = new HashSet<>();
        this.edges = new HashSet<>();
        //Collection of operations to be iteratively executed
        this.operations = new LinkedList<>();
        List<Edge> validSeedEdges = new LinkedList<>();
        //Filter edges in Seed by those defined in prov.Definition
        for(Edge seedEdge: seed.getEdges())
        {
            //Loop through edges defined in PROV
            for(Edge definedEdge: Definition.RELATIONS)
            {
                //If Definition.RELATIONS contains edge whose type = seed and between vertices of the same type
                if(seedEdge.isSimilar(definedEdge))
                {
                    validSeedEdges.add(seedEdge);
                    //Collection of constraints applicable to an operation
                    List<Constraint> applicableConstraints = new LinkedList<>();
                    for(Constraint c: constraints)
                    {
                        if(seedEdge.getConnecting()[0].getLabel().equals(c.determiner().provType()) ||
                                seedEdge.getConnecting()[1].getLabel().equals(c.determiner().provType()))
                        {
                            applicableConstraints.add(c);
                        }
                    }

                    //Create new operation with edge and applicableConstraints
                    this.operations.add(new Operation(applicableConstraints, seedEdge));

                }

            }
        }
        this.addEdges(validSeedEdges);
    }

    @Override
    public Set<Vertex> getVertices()
    {
        return this.vertices;
    }

    @Override
    public Set<Edge> getEdges()
    {
        return this.edges;
    }

    @Override
    public void execute(Integer size, Integer order, Integer numGraphs)
    {

        //While execution parameters not met. TODO: improve this generator to account for lots of small graphs
        while(this.edges.size()<=size && this.vertices.size()<=order)
        {
            int one = this.edges.size();
            // loop through vertices, checking each vertex is valid for an operation, and adding it.
            for (Vertex v : this.vertices)
            {
                for (Operation o : this.operations)
                {
                    if (o.isValidOn(v))
                    {
                        o.add(v);
                    }
                }
                //In an iteration if a vertex is not valid for any operation, remove it from the set.
            }

            //At the end of each iteration, execute all operations, adding resultant edges and vertices to set.
            for(Operation o : this.operations)
            {
               List<Edge> newEdges = o.execute();
               this.addEdges(newEdges);
            }
        }
        //Iteration should stop according to execution parameters.
    }

    private void addEdges(Collection<Edge> edges)
    {
        for(Edge e : edges)
        {
            this.vertices.addAll(Arrays.asList(e.getConnecting()));
        }
        this.edges.addAll(edges);
    }
}
