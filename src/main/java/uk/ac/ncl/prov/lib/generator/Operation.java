package uk.ac.ncl.prov.lib.generator;


import uk.ac.ncl.prov.lib.constraint.Constraint;
import uk.ac.ncl.prov.lib.graph.edge.Edge;
import uk.ac.ncl.prov.lib.graph.edge.EdgeLabel;
import uk.ac.ncl.prov.lib.graph.vertex.Vertex;
import uk.ac.ncl.prov.lib.graph.vertex.VertexLabel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static uk.ac.ncl.prov.lib.graph.vertex.Vertex.VertexBuilder.V;
import static uk.ac.ncl.prov.lib.graph.edge.Edge.EdgeBuilder.E;

public class Operation {


    //Fields
    private List<Constraint> applicableConstraints;
    private Edge edge;
    private List<Vertex> leftVertices;
    private List<Vertex> rightVertices;
    private List<Edge> createdEdges;


    public Operation(List<Constraint> applicableConstraints, Edge edge)
    {
        this.applicableConstraints = applicableConstraints;
        this.edge = edge;
        this.leftVertices = new LinkedList<>();
        this.rightVertices = new LinkedList<>();
        this.createdEdges = new LinkedList<>();
    }

    public boolean isValidOn(Vertex v)
    {
        Boolean isValid = (this.edge.getConnecting()[0].isSimilar(v) || this.edge.getConnecting()[1].isSimilar(v));

        if(isValid)
        {
            isValid = satisfiesConstraints(v);
        }
        return isValid;
    }

    private boolean satisfiesConstraints(Edge e)
    {
        for (Constraint c : this.applicableConstraints)
        {
            if (!c.isSatisfiedBy(e)) return false;
        }
        return true;
    }

    private boolean satisfiesConstraints(Vertex v)
    {
        for (Constraint c : this.applicableConstraints)
        {
            if (!c.isSatisfiedBy(v)) return false;
        }
        return true;
    }

    public void add(Vertex v)
    {
        if(this.isValidOn(v)) {
            if (v.isSimilar(edge.getConnecting()[0])) {
                this.leftVertices.add(v);
            } else {
                this.rightVertices.add(v);
            }
        }
        else
        {
            throw new IllegalArgumentException("Operation invalid on vertex "+v);
        }
    }

    private void rationalize()
    {
        Iterator<Vertex> leftItr = this.leftVertices.iterator();
        Iterator<Vertex> rightItr = this.rightVertices.iterator();

        //Combine all the pairs, checking applicable constraints on edges as well.
        while(leftItr.hasNext() || rightItr.hasNext())
        {
            Vertex leftVertex = (leftItr.hasNext()) ? leftItr.next() : V().label((VertexLabel) this.edge.getConnecting()[0].getLabel())
                    .properties(this.edge.getConnecting()[0].getProperties()).build();
            Vertex rightVertex = (rightItr.hasNext()) ? rightItr.next() : V().label((VertexLabel) this.edge.getConnecting()[1].getLabel())
                    .properties(this.edge.getConnecting()[1].getProperties()).build();

            Edge potentialEdge;
            if (this.edge.getOrientation() == Edge.Orientation.DIRECTED)
            {
                potentialEdge = E().label((EdgeLabel) this.edge.getLabel())
                        .from(leftVertex)
                        .to(rightVertex)
                        .properties(this.edge.getProperties()).build();
            }
            else
            {
                potentialEdge = E().label((EdgeLabel) this.edge.getLabel())
                        .between(leftVertex, rightVertex)
                        .properties(this.edge.getProperties()).build();
            }

            if(this.satisfiesConstraints(potentialEdge))
            {
                this.createdEdges.add(potentialEdge);
            }
        }
    }

    public List<Edge> execute()
    {
        this.rationalize();
        return this.createdEdges;
    }

}
