package uk.ac.ncl.prov.lib.generator;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import uk.ac.ncl.prov.lib.constraint.Constraint;
import uk.ac.ncl.prov.lib.graph.edge.Edge;
import uk.ac.ncl.prov.lib.graph.vertex.Vertex;
import uk.ac.ncl.prov.lib.prov.Definition;
import uk.ac.ncl.prov.lib.seed.Seed;

import java.util.*;


public class Neo4jGenerator implements Generator {

    //Fields
    private List<Operation> operations;
    private Set<Vertex> openVertices;
    private Set<Vertex> closedVertices;
    private Set<Edge> edges;
    private String dbPath;

    public Neo4jGenerator(String dbPath, Seed seed, List<Constraint> constraints)
    {
        this.dbPath = dbPath;
        this.openVertices = new HashSet<>();
        this.closedVertices = new HashSet<>();
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
                if(seedEdge.isSimilarIgnoreProperties(definedEdge))
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
        Set<Vertex> vertices = new HashSet<>(this.closedVertices);
        vertices.addAll(this.openVertices);
        return vertices;
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
        while(this.edges.size()<=size && (this.openVertices.size()+this.closedVertices.size())<=order)
        {

            // loop through vertices, checking each vertex is valid for an operation, and adding it.
            Set<Vertex> toClose = new HashSet<>();
            for (Vertex v : this.openVertices)
            {
                boolean open = false;
                boolean low = true;
                for (Operation op : this.operations)
                {
                    OperationState stateOn = op.stateOn(v);
                    if (stateOn.shouldContinue())
                    {
                        op.add(v);
                        open = true;
                        //break; //TODO: Revert this change if it turns out not be causing issue of preference for creating new nodes.

                    }
                    else if(stateOn.isSatisfied())
                    {
                        open = true;
                    }
                }
                //In an iteration if a vertex is not valid for any operation, remove it from the set.
                if(!open)
                {
                    toClose.add(v);
                }
            }

          this.openVertices.removeAll(toClose);
          this.closedVertices.addAll(toClose);

            //At the end of each iteration, execute all operations, adding resultant edges and vertices to set.
            for(Operation op : this.operations)
            {
               List<Edge> newEdges = op.execute();
               this.addEdges(newEdges);
            }

        }

        //Dump to Neo

        //Setup config
        Map<String, String> config = new HashMap<>();
        //How many nodes are being created? Byte values for nodes taken from: http://bit.ly/1sPnxVo
        Integer nodeMegaBytes = (order*9)/1000000;
        Integer nodeStoreMegaBytes = (nodeMegaBytes>10)?Double.valueOf(Math.ceil(nodeMegaBytes/10)*10).intValue():10;
        //How many relationships are being created?
        Integer relationshipMegaBytes = (size*33)/1000000;
        Integer relationshipStoreMegaBytes = (relationshipMegaBytes > 10)? Double.valueOf(Math.ceil(relationshipMegaBytes/10)*10).intValue():10;
        //Add calculated values to map
        config.put( "neostore.nodestore.db.mapped_memory", nodeStoreMegaBytes+"M" );
        config.put( "neostore.relationshipstore.db.mapped_memory", relationshipStoreMegaBytes+"M" );
        config.put( "neostore.propertystore.db.mapped_memory", "50M" );
        config.put( "neostore.propertystore.db.strings.mapped_memory", "100M" );
        config.put( "neostore.propertystore.db.arrays.mapped_memory", "0M" );

        //Create BatchInserter
        BatchInserter inserter = BatchInserters.inserter(this.dbPath, config);

        //Create Neo4j nodes from vertex set
        for(Vertex v : this.getVertices())
        {
            inserter.createNode(v.getId(), v.getProperties(), (Label) v.getLabel());
        }

        //Create Neo4j relationships from edge set
        for(Edge e : this.getEdges())
        {
            inserter.createRelationship(e.getConnecting()[0].getId(), e.getConnecting()[1].getId(), (RelationshipType) e.getLabel(), e.getProperties());
        }

        inserter.shutdown();
    }

    private void addEdges(Collection<Edge> edges)
    {
        for(Edge e : edges)
        {
            this.openVertices.addAll(Arrays.asList(e.getConnecting()));
        }
        this.edges.addAll(edges);
    }
}
