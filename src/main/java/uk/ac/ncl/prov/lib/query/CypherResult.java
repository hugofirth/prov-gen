package uk.ac.ncl.prov.lib.query;


import org.neo4j.cypher.javacompat.*;
import java.util.*;

/**
 * Created by hugofirth on 09/03/2014.
 */
public class CypherResult implements Result {

    private final ExecutionResult result;
    private final Map<String, List<Object>> resultSet;
    private final Boolean updated;
    private final String resultString;
    private Boolean empty;

    public CypherResult(ExecutionResult result)
    {
        this.result = result;
        this.updated = result.getQueryStatistics().containsUpdates();
        this.resultSet = new HashMap<>();

        for ( Map<String, Object> row : this.result )
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
    public Map<String, List<Object>> getResultList()
    {
        return this.resultSet;
    }

    public String toString()
    {
        return this.resultString;
    }
}
