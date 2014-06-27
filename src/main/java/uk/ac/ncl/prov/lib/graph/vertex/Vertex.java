package uk.ac.ncl.prov.lib.graph.vertex;



import com.sun.org.omg.CORBA.ExceptionDescriptionHelper;
import uk.ac.ncl.prov.lib.graph.Element;
import uk.ac.ncl.prov.lib.graph.Label;
import uk.ac.ncl.prov.lib.graph.edge.Edge;
import uk.ac.ncl.prov.lib.graph.edge.EdgeLabel;

import java.util.*;

/**
 * Created by hugofirth on 09/03/2014.
 */
public class Vertex extends Element {

    private static HashMap<String, Vertex> vertexScope = new HashMap<>();

    private Set<Vertex> neighbours;
    private Map<Label, Set<Edge>> inEdges;
    private Map<Label, Set<Edge>> outEdges;
    private Map<Label, Set<Edge>> unEdges;

    private Vertex(Builder builder)
    {
        super(builder);
        inEdges = new HashMap<>();
        outEdges = new HashMap<>();
        unEdges = new HashMap<>();
        neighbours = new HashSet<>();
        if(this.variable != null)
        {
            vertexScope.put(this.variable, this);
        }
    }

    public Set<Edge> getInEdges()
    {
        final Set<Edge> allInEdges = new HashSet<>();
        for(final Collection<Edge> edges : this.inEdges.values())
        {
            allInEdges.addAll(edges);
        }
        return allInEdges;
    }

    public Set<Edge> getOutEdges()
    {
        final Set<Edge> allOutEdges = new HashSet<>();
        for(final Collection<Edge> edges : this.outEdges.values())
        {
            allOutEdges.addAll(edges);
        }
        return allOutEdges;
    }

    public Set<Edge> getEdges()
    {
        final Set<Edge> allEdges = new HashSet<>();
        for(final Collection<Edge> edges : this.unEdges.values())
        {
            allEdges.addAll(edges);
        }
        allEdges.addAll(this.getInEdges());
        allEdges.addAll(this.getOutEdges());
        return allEdges;
    }

    public boolean hasEdgeWithLabels(EdgeLabel... labels)
    {
        for(EdgeLabel label : labels)
        {
            if(this.inEdges.containsKey(label) || this.outEdges.containsKey(label) || this.unEdges.containsKey(label)) return true;
        }
        return false;
    }

    public Set<Edge> getEdgesWithLabels(EdgeLabel... labels)
    {
        final Set<Edge> returnableEdgeSet = new HashSet<>();
        for(EdgeLabel label : labels)
        {
            if(this.inEdges.containsKey(label))
            {
                returnableEdgeSet.addAll(this.inEdges.get(label));
            }
            if(this.outEdges.containsKey(label))
            {
                returnableEdgeSet.addAll(this.outEdges.get(label));
            }
            if(this.unEdges.containsKey(label))
            {
                returnableEdgeSet.addAll(this.unEdges.get(label));
            }
        }
        return returnableEdgeSet;
    }

    public void addInEdge(Edge e)
    {
        Set<Edge> edges = this.inEdges.get(e.getLabel());
        if(edges == null)
        {
            edges = new HashSet<>();
            this.inEdges.put(e.getLabel(), edges);
        }
        edges.add(e);
        this.addNeighbour(e.other(this));
    }

    public void addOutEdge(Edge e)
    {
        Set<Edge> edges = this.outEdges.get(e.getLabel());
        if(edges == null)
        {
            edges = new HashSet<>();
            this.outEdges.put(e.getLabel(), edges);
        }
        edges.add(e);
        this.addNeighbour(e.other(this));
    }

    public void addEdge(Edge e)
    {
        Set<Edge> edges = this.unEdges.get(e.getLabel());
        if(edges == null)
        {
            edges = new HashSet<>();
            this.unEdges.put(e.getLabel(), edges);
        }
        edges.add(e);
        this.addNeighbour(e.other(this));
    }

    public boolean hasNeighbour(Vertex v)
    {
        return this.neighbours.contains(v);
    }

    public Set<Vertex> getNeighbours()
    {
        return this.neighbours;
    }

    private void addNeighbour(Vertex v)
    {
        this.neighbours.add(v);
    }

    public void clearScope()
    {
        vertexScope.clear();
    }

    public void removeEdge(Edge e)
    {
        if(this.getInEdges().contains(e))
        {
            this.inEdges.get(e.getLabel()).remove(e);
        }
        else if(this.getOutEdges().contains(e))
        {
            this.outEdges.get(e.getLabel()).remove(e);
        }
        else if(this.getEdges().contains(e))
        {
            this.unEdges.get(e.getLabel()).remove(e);
        }
        else
        {
            throw new IllegalArgumentException("Vertex -> "+this.toString()+" does not have edge -> "+e.toString()+"!");
        }

    }

    @Override
    public String toString() {
        return "Vertex{" +
                "id=" + this.getId() +
                ", label=" + this.getLabel().getName() +
                ", Degree={i:" + this.getInEdges().size() +
                ", o:" + this.getOutEdges().size() +
                ", t:" + this.getEdges().size() +
                "}, " + this.getProperties() +
                "}";
    }

    public static class VertexBuilder extends Element.Builder<Vertex> {

        public static VertexBuilder V()
        {
            return new VertexBuilder();
        }

        public static VertexBuilder V(String var)
        {
            return new VertexBuilder(var);
        }

        private VertexBuilder()
        {
            super();
        }

        private VertexBuilder(String var)
        {
            super();
            this.variable = var;
        }

        public VertexBuilder properties(Map<String, Object> p)
        {
            this.properties = p;
            return this;
        }

        public VertexBuilder label(VertexLabel l)
        {
            this.label = l;
            return this;
        }

        public Vertex get()
        {
            return (Vertex.vertexScope.containsKey(this.variable))?Vertex.vertexScope.get(this.variable):null;
        }

        public Vertex build(){
            return new Vertex(this);
        }

    }
}
