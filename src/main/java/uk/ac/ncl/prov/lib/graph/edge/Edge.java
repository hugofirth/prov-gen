package uk.ac.ncl.prov.lib.graph.edge;

import uk.ac.ncl.prov.lib.graph.Element;
import uk.ac.ncl.prov.lib.graph.vertex.Vertex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hugofirth on 13/03/2014.
 */
public class Edge extends Element {
    //TODO: refactor this ugly idea of scope out into something like a Graph or Path class
    private static final HashMap<String, Edge> edgeScope = new HashMap<>();

    private final Orientation orientation;
    private final Vertex[] connecting;

    public enum Orientation {
        DIRECTED,
        UNDIRECTED;
    }

    private Edge(EdgeBuilder builder)
    {
        super(builder);
        this.orientation = builder.orientation;
        this.connecting = builder.connecting;
        if(this.variable != null)
        {
            edgeScope.put(this.variable, this);
        }
    }

    public Vertex[] getConnecting()
    {
        return this.connecting;
    }

    public Orientation getOrientation()
    {
        return this.orientation;
    }

    public Boolean isDirected()
    {
        return (this.orientation == Orientation.DIRECTED);
    }

    public Boolean connects(Vertex n)
    {
        return (n.equals(this.connecting[0]) || n.equals(this.connecting[1]));
    }

    public Vertex connectedTo(Vertex n)
    {
        return (n.equals(this.connecting[0]))?this.connecting[1]:this.connecting[0];
    }

    public Vertex from()
    {
        if(this.isDirected())
        {
            return this.connecting[0];
        }

        throw new IllegalStateException("Relationship is undirected!");
    }

    public Vertex to()
    {
        if(this.isDirected())
        {
            return this.connecting[1];
        }

        throw new IllegalStateException("Relationship is undirected!");
    }

    public Vertex other(Vertex v)
    {
       if(this.connects(v))
       {
           return (v.equals(this.connecting[0]))?this.connecting[1]:this.connecting[0];
       }

       throw new IllegalArgumentException("This edge ("+this.getId()+") does not connect the vertex ("+v.getId()+")!");
    }

    public void clearScope()
    {
        edgeScope.clear();
    }

    @Override
    public boolean isSimilar(Object o)
    {
        if(!super.isSimilar(o)) return false;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        if(!edge.getOrientation().equals(this.getOrientation())) return false;
        if(edge.isDirected())
        {
            if(!edge.getConnecting()[0].isSimilar(this.getConnecting()[0])) return false;
            if(!edge.getConnecting()[1].isSimilar(this.getConnecting()[1])) return false;
        }
        else
        {
            if(!(edge.getConnecting()[0].isSimilar(this.getConnecting()[0]) ||
                edge.getConnecting()[0].isSimilar(this.getConnecting()[1]))) return false;
            if(!(edge.getConnecting()[1].isSimilar(this.getConnecting()[0]) ||
                    edge.getConnecting()[1].isSimilar(this.getConnecting()[1]))) return false;
        }
        return true;
    }

    public static class EdgeBuilder extends Element.Builder<Edge> {
        private Orientation orientation;
        private Vertex[] connecting;

        public static EdgeBuilder E()
        {
            return new EdgeBuilder();
        }

        public static EdgeBuilder E(String var)
        {
            return new EdgeBuilder(var);
        }

        private EdgeBuilder()
        {
            super();
            this.connecting = new Vertex[2];
        }

        private EdgeBuilder(String var)
        {
            super();
            this.connecting = new Vertex[2];
            this.variable = var;
        }

        public EdgeBuilder from(Vertex n)
        {
            this.orientation = Orientation.DIRECTED;
            this.connecting[0] = n;
            return this;
        }

        public EdgeBuilder to(Vertex n)
        {
            this.orientation = Orientation.DIRECTED;
            this.connecting[1] = n;
            return this;
        }

        public EdgeBuilder between(Vertex v1, Vertex v2)
        {
            this.orientation = Orientation.UNDIRECTED;
            this.connecting[0] = v1;
            this.connecting[1] = v2;
            return this;
        }

        public EdgeBuilder properties(Map<String, Object> p)
        {
            this.properties = p;
            return this;
        }

        public EdgeBuilder label(EdgeLabel l)
        {
            this.label = l;
            return this;
        }

        public Edge get()
        {
            return (Edge.edgeScope.containsKey(this.variable))?Edge.edgeScope.get(this.variable):null;
        }

        public Edge build()
        {
            return new Edge(this);
        }
    }
}
