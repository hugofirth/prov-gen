

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import uk.ac.ncl.prov.lib.query.CypherResult;
import uk.ac.ncl.prov.lib.query.Query;
import uk.ac.ncl.prov.lib.query.CypherQuery;
import uk.ac.ncl.prov.lib.query.CypherQuery.CypherQueryBuilder;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.math3.random.RandomDataGenerator;

/**
 * Pseudo (Manual) implementation at high level - not FINAL!
 */
public class Main {

    private final GraphDatabaseService db;
    private final ExecutionEngine engine;
    private final Integer numVertices;
    private final Integer numEdges;
    private final List<Query> edgeQueries;
    private final List<Query> vertexQueries;
    private final List<Query> constraints;
    private static final RandomDataGenerator rng =  new RandomDataGenerator();


    public static void main(String[] args)
    {
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase("/Users/hugofirth/Desktop/data/graph.db");
        ExecutionEngine engine = new ExecutionEngine( db );
        Main m = new Main(db, engine, 3846, 6352);

        //Vertex queries
        m.vertexQueries.add(new CypherQueryBuilder().create("(a:Activity {prov_type:'edit', inDegree:0, outDegree:0})").build());
        m.vertexQueries.add(new CypherQueryBuilder(0.01).create("(a:Activity {prov_type:'create', inDegree:0, outDegree:0})").build());
        m.vertexQueries.add(new CypherQueryBuilder().create("(e:Entity {prov_type:'Document', ex_version:'derivation', inDegree:0, outDegree:0})").build());
        m.vertexQueries.add(new CypherQueryBuilder(0.01).create("(e:Entity {prov_type:'Document', ex_version:'original', inDegree:0, outDegree:0})").build());
        m.vertexQueries.add(new CypherQueryBuilder(0.423).create("(ag:Agent {prov_type:'Person', desiredAssociations:$distribution_num$, inDegree:0, outDegree:0, associations:0})").build());

        //Edge queries
        m.edgeQueries.add(new CypherQueryBuilder().match("(a:Activity), (b:Entity)")
                .where("NOT (b)<-[:USED]-() AND NOT (a)-[:USED]->()")
                .other("WITH a,b")
                .where("NOT a.prov_type = 'create'")
                .other("WITH a,b LIMIT 1")
                .merge("(a)-[:USED]->(b)")
                .other("SET a.outDegree = a.outDegree+1")
                .other("SET b.inDegree = b.inDegree+1")
                .build());
        m.edgeQueries.add(new CypherQueryBuilder().match("(a:Entity), (b:Activity)")
                .where("NOT (a)-[:WASGENERATEDBY]->() AND NOT (b)<-[:WASGENERATEDBY]-()")
                .other("WITH a,b")
                .where("NOT a.outDegree >=2")
                .other("WITH a,b LIMIT 1")
                .merge("(a)-[:WASGENERATEDBY]->(b)")
                .other("SET a.outDegree = a.outDegree+1")
                .other("SET b.inDegree = b.inDegree+1")
                .build());
       /*m.edgeQueries.add(new CypherQueryBuilder().match("(a:Activity), (b:Agent)")
                .match("(b)<-[r:WASASSOCIATEDWITH]-()")
                .other("WITH count(r) AS c, a, b")
                .where("NOT (a)-[:WASASSOCIATEDWITH]->() AND (a)<-[:WASGENERATEDBY]-()-[:WASDERIVEDFROM*1..10]-()-[:WASGENERATEDBY]->()-[:WASASSOCIATEDWITH]->(b) AND NOT c >= b.associations")
                .other("WITH a,b LIMIT 1")
                .merge("(a)-[:WASASSOCIATEDWITH {prov_role:'contributor'}]->(b)")
                .other("SET a.outDegree = a.outDegree+1")
                .other("SET b.inDegree = b.inDegree+1")
                .build());*/
        m.edgeQueries.add(new CypherQueryBuilder().match("(a:Activity), (b:Agent)")
                .where("NOT (a)-[:WASASSOCIATEDWITH]->()")
                .other("WITH a,b")
                .where("NOT b.associations >= b.desiredAssociations")
                .other("WITH a,b LIMIT 1")
                .merge("(a)-[:WASASSOCIATEDWITH]->(b)")
                .other("SET a.outDegree = a.outDegree+1")
                .other("SET b.inDegree = b.inDegree+1")
                .other("SET b.associations = b.associations+1")
                .build());
        m.edgeQueries.add(new CypherQueryBuilder().match("(a:Entity), (b:Entity)")
                .where("NOT (a)-[:WASDERIVEDFROM]->() AND (b)<-[:USED]-()<-[:WASGENERATEDBY]-(a)")
                .other("WITH a,b")
                .where("NOT a.ex_version = 'original'")
                .other("WITH a,b LIMIT 1")
                .merge("(a)-[:WASDERIVEDFROM]->(b)")
                .other("SET a.outDegree = a.outDegree+1")
                .other("SET b.inDegree = b.inDegree+1")
                .build());

        //Constrain queries
        m.constraints.add(new CypherQueryBuilder().match("(a:Entity)")
                .where("NOT (a)-[:WASDERIVEDFROM]->() AND NOT a.ex_version = 'original'")
                .returns("a")
                .build());
        m.constraints.add(new CypherQueryBuilder().match("(a:Entity)")
                .where("NOT (a)-[:WASGENERATEDBY]->()")
                .returns("a")
                .build());
        m.constraints.add(new CypherQueryBuilder().match("(a:Activity)")
                .where("NOT (a)-[:WASASSOCIATEDWITH]->()")
                .returns("a")
                .build());
        m.constraints.add(new CypherQueryBuilder().match("(a:Activity)")
                .where("NOT (a)-[:USED]->() AND NOT a.prov_type = 'create'")
                .returns("a")
                .build());
        m.constraints.add(new CypherQueryBuilder().match("(a:Activity)")
                .where("NOT (a)<-[:WASGENERATEDBY]-()")
                .returns("a")
                .build());
        m.constraints.add(new CypherQueryBuilder().match("(a:Agent)")
                .where("NOT (a)<-[:WASASSOCIATEDWITH]-()")
                .returns("a")
                .build());


        m.generateVertices(m.vertexQueries);
        m.generateEdges(m.edgeQueries);
    }

    public Main(GraphDatabaseService db, ExecutionEngine engine, Integer numVertices, Integer numEdges)
    {
        this.db = db;
        this.engine = engine;
        this.numVertices = numVertices;
        this.numEdges = numEdges;
        this.vertexQueries = new LinkedList<>();
        this.edgeQueries = new LinkedList<>();
        this.constraints = new LinkedList<>();
    }

    public void generateEdges(List<Query> queries)
    {
        Integer edgesCreated = 0;
        Boolean effective = true;
        while(effective && ((edgesCreated < numEdges) || (!constraintsStatisfied())))
        {
            Integer edgesThisPass = 0;
            if(edgesCreated%100 == 0)
            {
                System.out.println("Number of Edges: "+edgesCreated);
            }
            for(Query query : queries)
            {
                if(query.shouldExecute())
                {
                    CypherResult result;
                    try ( Transaction tx = db.beginTx() )
                    {
                        result = new CypherResult(engine.execute(query.toQueryString()));
                        edgesThisPass+= result.netEffectEdges();
                        edgesCreated += result.netEffectEdges();
                        tx.success();
                    }
                }
            }
            if(edgesThisPass == 0)
            {
                effective = false;
            }
        }
    }

    public void generateVertices(List<Query> queries)
    {
        Integer verticesCreated = 0;
        while(verticesCreated < numVertices)
        {
            if(verticesCreated%100 == 0)
            {
                System.out.println("Number of Vertices: "+verticesCreated);
            }
            for(Query query : queries)
            {
                if(query.shouldExecute())
                {
                    Integer dist_num = (int) Math.round(rng.nextExponential(2.4));
                    query.provide("distribution_num", dist_num.toString());
                    CypherResult result;
                    try ( Transaction tx = db.beginTx() )
                    {
                        result = new CypherResult(engine.execute(query.toQueryString()));
                        verticesCreated += result.netEffectVertices();
                        tx.success();
                    }
                }
            }
        }
    }

    public Boolean constraintsStatisfied()
    {
        System.out.println("Checking Constraints");
        Boolean satisfied = true;
        for(Query query: this.constraints)
        {
            CypherResult result;
            try ( Transaction tx = db.beginTx() )
            {
                result = new CypherResult(engine.execute(query.toQueryString()));
                tx.success();
            }
            if(!result.isEmpty())
            {
                satisfied = false;
            }

        }
        return satisfied;
    }


}
