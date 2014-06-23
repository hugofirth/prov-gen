package uk.ac.ncl.prov.lib.query;


import org.neo4j.cypher.javacompat.*;
import java.util.*;

/**
 * Created by hugofirth on 09/03/2014.
 */
public class CypherResult implements Result {

    private final Map<String, List<Object>> resultSet;
    private final Boolean updated;
    private final String resultString;
    private final Integer netVertices;
    private final Integer netEdges;
    private Boolean empty;

    public CypherResult(ExecutionResult result)
    {
        QueryStatistics stats = result.getQueryStatistics();
        this.netVertices = stats.getNodesCreated() - stats.getDeletedNodes();
        this.netEdges = stats.getRelationshipsCreated() - stats.getDeletedRelationships();
        this.updated = stats.containsUpdates();
        this.resultSet = new HashMap<>();
        this.empty = true;

        for ( Map<String, Object> row : result )
        {
            for ( Map.Entry<String, Object> column : row.entrySet() )
            {
                if(!this.resultSet.containsKey(column.getKey()))
                {
                    this.resultSet.put(column.getKey(), new ArrayList<Object>());
                }
                this.resultSet.get(column.getKey()).add(column.getValue());
                this.empty = false;
            }
        }
        this.resultString = result.dumpToString();
    }

    @Override
    public Boolean isEmpty()
    {
        return this.empty;
    }

    @Override
    public Boolean didUpdate()
    {
        return this.updated;
    }

    @Override
    public Integer netEffectVertices() {
        return this.netVertices;
    }

    @Override
    public Integer netEffectEdges() {
        return this.netEdges;
    }

    @Override
    public Map<String, List<Object>> getResultList()
    {
        return this.resultSet;
    }

    public String toString()
    {
        return this.resultString;
    }
}
