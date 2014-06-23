package uk.ac.ncl.prov.lib.prov;

import uk.ac.ncl.prov.lib.constraint.Constraint;
import uk.ac.ncl.prov.lib.graph.edge.Edge;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static uk.ac.ncl.prov.lib.graph.vertex.Vertex.VertexBuilder.V;
import static uk.ac.ncl.prov.lib.graph.edge.Edge.EdgeBuilder.E;

public final class Definition {

    public static final List<Edge> RELATIONS;
    public static final List<Constraint> CONSTRAINTS;
    static {
        List<Edge> tempRelations = new LinkedList<>();
        List<Constraint> tempConstraints = new LinkedList<>();

        //Entity-[:WasDerivedFrom]->Entity
        tempRelations.add( E().label(Relation.WASDERIVEDFROM).from( V().label(Type.ENTITY).build() ).to( V().label(Type.ENTITY).build() ).build() );
        //Entity-[:AlternateOf]->Entity
        tempRelations.add( E().label(Relation.ALTERNATEOF).from( V().label(Type.ENTITY).build() ).to( V().label(Type.ENTITY).build() ).build() );
        //Entity-[:SpecializationOf]->Entity
        tempRelations.add( E().label(Relation.SPECIALIZATIONOF).from( V().label(Type.ENTITY).build() ).to( V().label(Type.ENTITY).build() ).build() );
        //Entity-[:HadMember]->Entity
        tempRelations.add( E().label(Relation.HADMEMBER).from( V().label(Type.ENTITY).build() ).to( V().label(Type.ENTITY).build() ).build() );
        //Entity-[:WasGeneratedBy]->Activity
        tempRelations.add( E().label(Relation.WASGENERATEDBY).from( V().label(Type.ENTITY).build() ).to( V().label(Type.ACTIVITY).build() ).build() );
        //Entity-[:WasInvalidatedBy]->Activity
        tempRelations.add( E().label(Relation.WASINVALIDATEDBY).from( V().label(Type.ENTITY).build() ).to( V().label(Type.ACTIVITY).build() ).build() );
        //Entity-[:WasAttributedTo]->Agent
        tempRelations.add( E().label(Relation.WASATTRIBUTEDTO).from( V().label(Type.ENTITY).build() ).to( V().label(Type.AGENT).build() ).build() );
        //Activity-[:Used]->Entity
        tempRelations.add( E().label(Relation.USED).from( V().label(Type.ACTIVITY).build() ).to( V().label(Type.ENTITY).build() ).build() );
        //Activity-[:WasStartedBy]->Entity
        tempRelations.add( E().label(Relation.WASSTARTEDBY).from( V().label(Type.ACTIVITY).build() ).to( V().label(Type.ENTITY).build() ).build() );
        //Activity-[:WasEndedBy]->Entity
        tempRelations.add( E().label(Relation.WASENDEDBY).from( V().label(Type.ACTIVITY).build() ).to( V().label(Type.ENTITY).build() ).build() );
        //Activity-[:WasInformedBy]->Activity
        tempRelations.add( E().label(Relation.WASINFORMEDBY).from( V().label(Type.ACTIVITY).build() ).to( V().label(Type.ACTIVITY).build() ).build() );
        //Activity-[:WasAssociatedWith]->Agent
        tempRelations.add( E().label(Relation.WASASSOCIATEDWITH).from( V().label(Type.ACTIVITY).build() ).to( V().label(Type.AGENT).build() ).build() );
        //Agent-[:ActedOnBehalfOf]->Agent
        tempRelations.add( E().label(Relation.ACTEDONBEHALFOF).from( V().label(Type.AGENT).build() ).to( V().label(Type.AGENT).build() ).build() );

        //TODO: add constraints implied by PROV-DM

        //Create the unmodifiable list of Immutable edges.
        RELATIONS = Collections.unmodifiableList(tempRelations);
        //Create the unmodifiable list of Constraints.
        CONSTRAINTS = Collections.unmodifiableList(tempConstraints);
    }

}
