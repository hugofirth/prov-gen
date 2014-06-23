package uk.ac.ncl.prov.lib.graph;

import uk.ac.ncl.prov.lib.graph.edge.Edge;
import uk.ac.ncl.prov.lib.graph.edge.Edge.EdgeBuilder;
import uk.ac.ncl.prov.lib.graph.vertex.Vertex;
import uk.ac.ncl.prov.lib.graph.vertex.Vertex.VertexBuilder;
import uk.ac.ncl.prov.lib.graph.vertex.VertexLabel;

import java.util.*;

/**
 * Created by hugofirth on 15/03/2014.
 */
public class Path implements Iterable<Element> {

    private final List<Element> path;
    private final Map<String, Element> variables;
    private final Vertex start;
    private final Vertex end;
    private final List<Vertex> vertices;
    private final List<Edge> edges;

    private Path(PathBuilder builder)
    {
        this.path = builder.path;
        this.variables = builder.variables;
        this.start = builder.start;
        this.end = builder.end;
        this.vertices = builder.vertices;
        this.edges = builder.edges;
    }

    @Override
    public Iterator<Element> iterator() {
        return path.iterator();
    }

    public Map<String, Element> getVariables()
    {
        return this.variables;
    }

    public static final class PathBuilder {

        public static PathBuilder P()
        {
            return new PathBuilder();
        }

        private interface VertexStep
        {
            RelationshipStep vertex(Vertex v);
        }

        private interface RelationshipStep
        {
            OrientationStep hasEdge(Edge e);
            Path build();
        }

        private interface OrientationStep
        {
            VertexStep with();
            VertexStep from();
            VertexStep to();
        }

        private String variable;
        private Vertex start;
        private Vertex end;
        private final Map<String, Element> variables;
        private final LinkedList<Vertex> vertices;
        private final LinkedList<Edge> edges;
        private final LinkedList<Element> path;

        private PathBuilder()
        {
            this.variables = new HashMap<>();
            this.vertices = new LinkedList<>();
            this.edges = new LinkedList<>();
            this.path = new LinkedList<>();
        }

        public RelationshipStep start(Vertex v)
        {
            if(v.getVariable() != null)
            {
                this.variables.put(v.getVariable(), v);
            }
            this.start = v;
            this.vertices.add(v);
            this.path.add(v);
            return new Steps();
        }

        public class Steps implements VertexStep, RelationshipStep, OrientationStep
        {

            @Override
            public VertexStep with() {
                return this;
            }

            @Override
            public VertexStep from() {
                return this;
            }

            @Override
            public VertexStep to() {
                return this;
            }

            @Override
            public OrientationStep hasEdge(Edge e) {
                if(e.getVariable() != null)
                {
                    PathBuilder.this.variables.put(e.getVariable(), e);
                }
                PathBuilder.this.edges.add(e);
                PathBuilder.this.path.add(e);
                return this;
            }

            @Override
            public RelationshipStep vertex(Vertex v) {
                if(v.getVariable() != null)
                {
                    PathBuilder.this.variables.put(v.getVariable(), v);
                }
                PathBuilder.this.vertices.add(v);
                PathBuilder.this.path.add(v);
                return this;
            }

            @Override
            public Path build() {
                PathBuilder.this.end = PathBuilder.this.vertices.getLast();
                return new Path(PathBuilder.this);
            }
        }
    }
}
