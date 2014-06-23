package uk.ac.ncl.prov.lib.query;

import uk.ac.ncl.prov.lib.graph.vertex.Vertex;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hugofirth on 09/03/2014.
 */
public class CypherQuery implements Query {
    private static final Random RAND = new Random();
    private static final Pattern PARAM_PATTERN = Pattern.compile("\\$.*\\$");
    private final Double probability;
    private final List<String> statements;
    private final List<SimpleEntry<Vertex, String>> matchStatements;
    private final String whereStatement;
    private final String otherStatement;
    private final String createStatement;
    private final String mergeStatement;
    private final String returnStatement;
    private final Map<String, String> params;



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
        this.probability = builder.probability;
        this.params = new HashMap<>();
    }

    @Override
    public String toQueryString()
    {
        StringBuilder query = new StringBuilder();
        for(String statement : statements)
        {
            String param;
            String value;
            Matcher m = PARAM_PATTERN.matcher(statement);
            while(m.find())
            {
               param = m.group(0);
               value = params.get(param.replace("$",""));
               statement = m.replaceAll(value);
            }
            query.append(statement).append(System.getProperty("line.separator"));
        }
        return query.toString();
    }

    @Override
    public Boolean shouldExecute()
    {
        Double p = RAND.nextDouble();
        return (p < this.probability);
    }

    @Override
    public void provide(String key, String val) {
        params.put(key, val);
    }

    public static final class CypherQueryBuilder {
        private final List<String> statements;
        private final List<SimpleEntry<Vertex, String>> matchStatements;
        private final Double probability;
        private String whereStatement;
        private String otherStatement;
        private String createStatement;
        private String mergeStatement;
        private String returnStatement;

        public CypherQueryBuilder(Double probability)
        {
            if(probability<0 || probability>1)
            {
                throw new IllegalArgumentException("Probability of query execution must be between 0 & 1. Provided: "+probability);
            }
            this.probability = probability;
            this.statements = new LinkedList<>();
            this.matchStatements = new LinkedList<>();
        }

        public CypherQueryBuilder()
        {
            this.probability = 1.0;
            this.statements = new LinkedList<>();
            this.matchStatements = new LinkedList<>();
        }

        public CypherQueryBuilder match(String s)
        {
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
