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
    private Vertex left;
    private Vertex right;


    public Operation(List<Constraint> applicableConstraints, Edge edge)
    {
        this.applicableConstraints = applicableConstraints;
        this.edge = edge;
        this.leftVertices = new LinkedList<>();
        this.rightVertices = new LinkedList<>();
        this.right = V().label((VertexLabel) this.edge.getConnecting()[1].getLabel()).properties(this.edge.getConnecting()[1].getProperties()).build();
        this.left = V().label((VertexLabel) this.edge.getConnecting()[0].getLabel()).properties(this.edge.getConnecting()[0].getProperties()).build();
    }

    private boolean satisfiesConstraints(Edge e)
    {
        for (Constraint c : this.applicableConstraints)
        {
            if (!c.isSatisfiedBy(e)) return false;
        }
        return true;
    }

    public boolean isValidOn(Vertex v)
    {
        Edge potentialEdge;
        if(v.isSimilar(this.edge.getConnecting()[0]))
        {
            potentialEdge = this.createEdge(v, this.right);
        }
        else if(v.isSimilar(this.edge.getConnecting()[1]))
        {
            potentialEdge = this.createEdge(this.left, v);
        }
        else
        {
            return false;
        }

        boolean satisfies = this.satisfiesConstraints(potentialEdge);
        potentialEdge.delete();
        return satisfies;
    }

    public void add(Vertex v)
    {
            if (v.isSimilar(edge.getConnecting()[0])) {
                this.leftVertices.add(v);
            } else {
                this.rightVertices.add(v);
            }
    }

    private Edge createEdge(Vertex left, Vertex right)
    {
        Edge e;
        if (this.edge.getOrientation() == Edge.Orientation.DIRECTED)
        {
            e = E().label((EdgeLabel) this.edge.getLabel())
                   .from(left)
                   .to(right)
                   .properties(this.edge.getProperties()).build();
        }
        else
        {
            e = E().label((EdgeLabel) this.edge.getLabel())
                   .between(left, right)
                   .properties(this.edge.getProperties()).build();
        }
        return e;
    }

    public List<Edge> execute()
    {
        List<Edge> createdEdges = new LinkedList<>();
        Iterator<Vertex> leftItr = this.leftVertices.iterator();
        Iterator<Vertex> rightItr = this.rightVertices.iterator();

        //Combine all the pairs, checking applicable constraints on edges as well.
        do
        {
            Vertex left = (leftItr.hasNext()) ? leftItr.next() : V().label((VertexLabel) this.edge.getConnecting()[0].getLabel())
                    .properties(this.edge.getConnecting()[0].getProperties()).build();
            Vertex right = (rightItr.hasNext()) ? rightItr.next() : V().label((VertexLabel) this.edge.getConnecting()[1].getLabel())
                    .properties(this.edge.getConnecting()[1].getProperties()).build();
            createdEdges.add(this.createEdge(left,right));
//            Edge potentialEdge = this.createEdge(left, right);
//
//            if(this.satisfiesConstraints(potentialEdge))
//            {
//                createdEdges.add(potentialEdge);
//            }
//            else
//            {
//                potentialEdge.delete();
//            }
        } while(leftItr.hasNext() || rightItr.hasNext());
        this.leftVertices.clear();
        this.rightVertices.clear();
        return createdEdges;
    }

}
