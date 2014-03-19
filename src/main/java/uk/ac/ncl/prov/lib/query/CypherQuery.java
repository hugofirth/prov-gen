package uk.ac.ncl.prov.lib.query;

import uk.ac.ncl.prov.lib.graph.vertex.Vertex;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

/**
 * Created by hugofirth on 09/03/2014.
 */
public class CypherQuery implements Query {

    private final List<String> statements;
    private final List<SimpleEntry<Vertex, String>> matchStatements;
    private final String whereStatement;
    private final String otherStatement;
    private final String createStatement;
    private final String mergeStatement;
    private final String returnStatement;


    //TODO: Finish this class
    private CypherQuery(CypherQueryBuilder builder)
    {
        this.statements = builder.statements;
        this.matchStatements = builder.matchStatements;
        this.whereStatement = builder.whereStatement;
        this.otherStatement = builder.otherStatement;
        this.createStatement = builder.createStatement;
        this.mergeStatement = builder.mergeStatement;
        this.returnStatement = builder.returnStatement;
    }

    @Override
    public String toQueryString() {
        StringBuilder query = new StringBuilder();
        for(String statement : statements)
        {
            query.append(statement + System.getProperty("line.separator"));
        }
        return query.toString();
    }

    public static final class CypherQueryBuilder {
        private final List<String> statements;
        private final List<SimpleEntry<Vertex, String>> matchStatements;
        private String whereStatement;
        private String otherStatement;
        private String createStatement;
        private String mergeStatement;
        private String returnStatement;

        public CypherQueryBuilder()
        {
            this.statements = new LinkedList<>();
            this.matchStatements = new LinkedList<>();
        }

        public CypherQueryBuilder match(Vertex n, String s)
        {
            this.matchStatements.add(new SimpleEntry<>(n, s));
            this.statements.add("MATCH " + s);
            return this;
        }

        public CypherQueryBuilder where(String s)
        {
            this.whereStatement = "WHERE "+s;
            this.statements.add(this.whereStatement);
            return this;
        }

        public CypherQueryBuilder other(String s)
        {
            this.otherStatement = s;
            this.statements.add(s);
            return this;
        }

        public CypherQueryBuilder create(String s)
        {
            this.createStatement = s;
            this.statements.add("CREATE "+s);
            return this;
        }

        public CypherQueryBuilder merge(String s)
        {
            this.mergeStatement = s;
            this.statements.add("MERGE "+s);
            return this;
        }

        public CypherQueryBuilder returns(String s)
        {
            this.returnStatement = s;
            this.statements.add("RETURN "+s);
            return this;
        }

        public CypherQuery build()
        {
            return new CypherQuery(this);
        }

    }
}
