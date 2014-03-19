package uk.ac.ncl.prov.lib.graph;

import uk.ac.ncl.prov.lib.graph.vertex.Vertex;
import uk.ac.ncl.prov.lib.graph.vertex.VertexLabel;
import uk.ac.ncl.prov.lib.graph.relationship.Relationship;

import java.util.*;

/**
 * Created by hugofirth on 15/03/2014.
 */
public class Path implements Iterable<Element> {

    private List<Element> path;
    private Map<String, Element> variables;

    @Override
    public Iterator<Element> iterator() {
        return path.iterator();
    }

    public Map<String, Element> getVariables()
    {
        return this.variables;
    }

    public static final class Builder {

        private interface StartStep
        {
            RelationshipStep startVertex(Vertex n);
            RelationshipStep startVertex(String var);
            RelationshipStep startVertex(VertexLabel l);
            RelationshipStep startVertex(Map<String, Object> properties);
            RelationshipStep startVertex(String var, VertexLabel l);
            RelationshipStep startVertex(String var, Map<String, Object> properties);
            RelationshipStep startVertex(String var, VertexLabel l, Map<String, Object> properties);
            RelationshipStep startVertex(VertexLabel l, Map<String, Object> properties);
        }

        private interface VertexStep
        {
            RelationshipStep vertex(Vertex n);
            RelationshipStep vertex(String var);
            RelationshipStep vertex(VertexLabel l);
            RelationshipStep vertex(Map<String, Object> properties);
            RelationshipStep vertex(String var, VertexLabel l);
            RelationshipStep vertex(String var, Map<String, Object> properties);
            RelationshipStep vertex(String var, VertexLabel l, Map<String, Object> properties);
            RelationshipStep vertex(VertexLabel l, Map<String, Object> properties);
            Builder endVertex(Vertex n);
            Builder endVertex(String var);
            Builder endVertex(VertexLabel l);
            Builder endVertex(Map<String, Object> properties);
            Builder endVertex(String var, VertexLabel l);
            Builder endVertex(String var, Map<String, Object> properties);
            Builder endVertex(String var, VertexLabel l, Map<String, Object> properties);
            Builder endVertex(VertexLabel l, Map<String, Object> properties);
        }

        private interface RelationshipStep
        {
            OrientationStep hasRelationship(String var);
            OrientationStep hasRelationship(Relationship r, String var);
            OrientationStep hasRelationship(Relationship r);
        }

        private interface OrientationStep
        {
            VertexStep with();
            VertexStep from();
            VertexStep to();
        }

        private Vertex start;
        private Vertex end;
        private final Map<String, Element> variables;
        private final List<Vertex> vertices;
        private final List<Relationship> relationships;
        private final List<Element> path;

        public Builder()
        {
            this.variables = new HashMap<>();
            this.vertices = new LinkedList<>();
            this.relationships = new LinkedList<>();
            this.path = new LinkedList<>();
        }

        public RelationshipStep startVertex(Vertex n)
        {
            if(n.getVariable() != null)
            {
                this.variables.put(n.getVariable(), n);
            }
            this.start = n;
            this.vertices.add(n);
            return new Steps();
        }

        public RelationshipStep startVertex(String variable, VertexLabel l, Map<String, Object> properties)
        {

        }



        private final class Steps implements StartStep, VertexStep, RelationshipStep, OrientationStep
        {


        }
    }
}
