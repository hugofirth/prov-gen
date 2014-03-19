package uk.ac.ncl.prov.lib.graph.relationship;

import uk.ac.ncl.prov.lib.graph.Element;
import uk.ac.ncl.prov.lib.graph.vertex.Vertex;

import java.util.Arrays;

/**
 * Created by hugofirth on 13/03/2014.
 */
public class Relationship extends Element {

    private final Orientation orientation;
    private final Vertex[] connecting;

    public enum Orientation {
        DIRECTED,
        UNDIRECTED;
    }

    public Relationship(Builder builder)
    {
        super(builder);
        this.orientation = builder.orientation;
        this.connecting = builder.connecting;
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

    public static class Builder extends Element.Builder {
        private Orientation orientation;
        private Vertex[] connecting;

        public Builder from(Vertex n)
        {
            this.orientation = Orientation.DIRECTED;
            this.connecting[0] = n;
            return this;
        }

        public Builder to(Vertex n)
        {
            this.orientation = Orientation.DIRECTED;
            this.connecting[1] = n;
            return this;
        }

        public Builder between(Vertex... vertices)
        {
            this.connecting = Arrays.copyOfRange(vertices, 0, 1);
            return this;
        }

        public Relationship build()
        {
            return new Relationship(this);
        }
    }
}
