package uk.ac.ncl.prov.lib.generator;


import uk.ac.ncl.prov.lib.constraint.Constraint;
import uk.ac.ncl.prov.lib.graph.edge.Edge;
import uk.ac.ncl.prov.lib.graph.edge.EdgeLabel;
import uk.ac.ncl.prov.lib.graph.vertex.Vertex;
import uk.ac.ncl.prov.lib.graph.vertex.VertexLabel;

import java.util.*;

import static uk.ac.ncl.prov.lib.graph.vertex.Vertex.VertexBuilder.V;
import static uk.ac.ncl.prov.lib.graph.edge.Edge.EdgeBuilder.E;

public class Operation {


    //Fields
    private List<Constraint> applicableConstraints;
    private Edge edge;
    private List<Edge> potentialEdges;
    private List<Vertex> leftVertices;
    private List<Vertex> rightVertices;
    private Vertex left;
    private Vertex right;


    public Operation(List<Constraint> applicableConstraints, Edge edge)
    {
        this.applicableConstraints = applicableConstraints;
        this.edge = edge;
        this.potentialEdges = new LinkedList<>();
        this.leftVertices = new LinkedList<>();
        this.rightVertices = new LinkedList<>();
        this.right = V().label((VertexLabel) this.edge.getConnecting()[1].getLabel()).properties(this.edge.getConnecting()[1].getProperties()).build();
        this.left = V().label((VertexLabel) this.edge.getConnecting()[0].getLabel()).properties(this.edge.getConnecting()[0].getProperties()).build();
    }

    private OperationState evaluateAgainstConstraints(Edge e)
    {
        boolean isSatisfied = true;
        boolean shouldContinue = true;

        for (Constraint c : this.applicableConstraints)
        {
            OperationState state = c.evaluate(e);
            isSatisfied = isSatisfied && state.isSatisfied();
            shouldContinue = shouldContinue && state.shouldContinue();
            if (!isSatisfied && !shouldContinue) return new OperationState(false, false);
        }
        return new OperationState(isSatisfied, shouldContinue);
    }

    public OperationState stateOn(Vertex v)
    {
        Edge potentialEdge;
        if(v.isSimilar(this.left))
        {
            potentialEdge = this.createEdge(v, this.getRight());
        }
        else if(v.isSimilar(this.right))
        {
            potentialEdge = this.createEdge(this.getLeft(), v);
        }
        else
        {
            return new OperationState(false, false);
        }

        OperationState opStateOn = this.evaluateAgainstConstraints(potentialEdge);
        if(opStateOn.shouldContinue())
        {
            this.potentialEdges.add(potentialEdge);
        }
        else
        {
            potentialEdge.delete();
        }
        return opStateOn;
    }

    public void add(Vertex v)
    {
        if (v.isSimilar(this.left))
        {
            this.leftVertices.add(v);
        }
        else if(v.isSimilar(this.right))
        {
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
        for(Edge pe: this.potentialEdges)
        {
            pe.delete();
        }
        this.potentialEdges = new LinkedList<>();
        List<Edge> createdEdges = new LinkedList<>();
        Iterator<Vertex> leftItr = this.leftVertices.iterator();
        Iterator<Vertex> rightItr = this.rightVertices.iterator();

        //Combine all the pairs, checking applicable constraints on edges as well.
        do
        {
            Vertex left = (leftItr.hasNext()) ? leftItr.next() : this.getLeft();
            Vertex right = (rightItr.hasNext()) ? rightItr.next() : this.getRight();

            Edge potentialEdge = this.createEdge(left, right);

            if(this.evaluateAgainstConstraints(potentialEdge).shouldContinue())
            {
                createdEdges.add(potentialEdge);
            }
            else
            {
                potentialEdge.delete();
            }
        }
        while(leftItr.hasNext() || rightItr.hasNext());

        this.leftVertices = new LinkedList<>();
        this.rightVertices = new LinkedList<>();
        return createdEdges;
    }

    public List<Edge> execute(boolean createNew)
    {
        if(!createNew && (this.leftVertices.isEmpty() || this.rightVertices.isEmpty()))
        {

            for(Edge pe: this.potentialEdges)
            {
                pe.delete();
            }
            this.potentialEdges = new LinkedList<>();
            return new LinkedList<>();
        }
        return this.execute();
    }

    private Vertex getRight()
    {
        return V().label((VertexLabel) this.right.getLabel()).properties(this.right.getProperties()).build();
    }

    private Vertex getLeft()
    {
        return V().label((VertexLabel) this.left.getLabel()).properties(this.left.getProperties()).build();
    }

}
