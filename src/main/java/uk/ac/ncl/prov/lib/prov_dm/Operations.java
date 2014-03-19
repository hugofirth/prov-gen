package uk.ac.ncl.prov.lib.prov_dm;

import uk.ac.ncl.prov.lib.graph.vertex.Vertex;
import uk.ac.ncl.prov.lib.query.CypherQuery.CypherQueryBuilder;
import uk.ac.ncl.prov.lib.query.Query;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hugofirth on 09/03/2014.
 */
public final class Operations {
    public static final List<Query> ALL;
    static {
        List<Query> temp = new LinkedList<>();


        /*
         * TODO: Eventually get to the below
         * PathBuilder().startNode().hasRelationship().with().endNode();
         * PathBuilder().startNode(Type.ENTITY, "a")
         */

        //Entity-[:WasDerivedFrom]->Entity
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").create("(a)-[:WasDerivedFrom]->(new:Entity {})").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").match(new Vertex(Type.ENTITY), "(b:Entity {})").merge("(a)-[:WasDerivedFrom]->(b)").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").create("(a)<-[:WasDerivedFrom]-(new:Entity {})").build());
        //Entity-[:AlternateOf]->Entity
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").create("(a)-[:AlternateOf]->(new:Entity {})").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").match(new Vertex(Type.ENTITY), "(b:Entity {})").merge("(a)-[:AlternateOf]->(b)").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").create("(a)<-[:AlternateOf]-(new:Entity {})").build());
        //Entity-[:SpecializationOf]->Entity
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").create("(a)-[:SpecializationOf]->(new:Entity {})").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").match(new Vertex(Type.ENTITY), "(b:Entity {})").merge("(a)-[:SpecializationOf]->(b)").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").create("(a)<-[:SpecializationOf]-(new:Entity {})").build());
        //Entity-[:HadMember]->Entity
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").create("(a)-[:HadMember]->(new:Entity {})").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").match(new Vertex(Type.ENTITY), "(b:Entity {})").merge("(a)-[:HadMember]->(b)").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").create("(a)<-[:HadMember]-(new:Entity {})").build());
        //Entity-[:WasGeneratedBy]->Activity
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").create("(a)-[:WasGeneratedBy]->(new:Activity {})").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").match(new Vertex(Type.ACTIVITY), "(b:Activity {})").merge("(a)-[:WasGeneratedBy]->(b)").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ACTIVITY), "(a:Activity {})").create("(a)<-[:WasGeneratedBy]-(new:Entity {})").build());
        //Entity-[:WasInvalidatedBy]->Activity
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").create("(a)-[:WasInvalidatedBy]->(new:Activity {})").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").match(new Vertex(Type.ACTIVITY), "(b:Activity {})").merge("(a)-[:WasInvalidatedBy]->(b)").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ACTIVITY), "(a:Activity {})").create("(a)<-[:WasInvalidatedBy]-(new:Entity {})").build());
        //Entity-[:WasAttributedTo]->Agent
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").create("(a)-[:WasAttributedTo]->(new:Agent {})").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").match(new Vertex(Type.AGENT), "(b:Agent {})").merge("(a)-[:WasAttributedTo]->(b)").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.AGENT), "(a:Agent {})").create("(a)<-[:WasAttributedTo]-(new:Entity {})").build());
        //Activity-[:Used]->Entity
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ACTIVITY), "(a:Activity {})").create("(a)-[:Used]->(new:Entity {})").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ACTIVITY), "(a:Activity {})").match(new Vertex(Type.ENTITY), "(b:Entity {})").merge("(a)-[:Used]->(b)").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").create("(a)<-[:Used]-(new:Activity {})").build());
        //Activity-[:WasStartedBy]->Entity
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ACTIVITY), "(a:Activity {})").create("(a)-[:WasStartedBy]->(new:Entity {})").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ACTIVITY), "(a:Activity {})").match(new Vertex(Type.ENTITY), "(b:Entity {})").merge("(a)-[:WasStartedBy]->(b)").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").create("(a)<-[:WasStartedBy]-(new:Activity {})").build());
        //Activity-[:WasEndedBy]->Entity
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ACTIVITY), "(a:Activity {})").create("(a)-[:WasEndedBy]->(new:Entity {})").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ACTIVITY), "(a:Activity {})").match(new Vertex(Type.ENTITY), "(b:Entity {})").merge("(a)-[:WasEndedBy]->(b)").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ENTITY), "(a:Entity {})").create("(a)<-[:WasEndedBy]-(new:Activity {})").build());
        //Activity-[:WasInformedBy]->Activity
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ACTIVITY), "(a:Activity {})").create("(a)-[:WasInformedBy]->(new:Activity {})").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ACTIVITY), "(a:Activity {})").match(new Vertex(Type.ACTIVITY), "(b:Activity {})").merge("(a)-[:WasInformedBy]->(b)").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ACTIVITY), "(a:Activity {})").create("(a)<-[:WasInformedBy]-(new:Activity {})").build());
        //Activity-[:WasAssociatedWith]->Agent
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ACTIVITY), "(a:Activity {})").create("(a)-[:WasAssociatedWith]->(new:Agent {})").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.ACTIVITY), "(a:Activity {})").match(new Vertex(Type.AGENT), "(b:Agent {})").merge("(a)-[:WasAssociatedWith]->(b)").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.AGENT), "(a:Agent {})").create("(a)<-[:WasAssociatedWith]-(new:Activity {})").build());
        //Agent-[:ActedOnBehalfOf]->Agent
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.AGENT), "(a:Agent {})").create("(a)-[:ActedOnBehalfOf]->(new:Agent {})").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.AGENT), "(a:Agent {})").match(new Vertex(Type.AGENT), "(b:Agent {})").merge("(a)-[:ActedOnBehalfOf]->(b)").build());
        temp.add(new CypherQueryBuilder().match(new Vertex(Type.AGENT), "(a:Agent {})").create("(a)<-[:ActedOnBehalfOf]-(new:Agent {})").build());

        //Create the unmodifiable list of Immutable queries.
        ALL = Collections.unmodifiableList(temp);
    }
}
