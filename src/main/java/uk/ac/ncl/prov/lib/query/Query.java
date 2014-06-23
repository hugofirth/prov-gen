package uk.ac.ncl.prov.lib.query;

import java.util.Map;

/**
 * Created by hugofirth on 09/03/2014.
 */
public interface Query {
    public String toQueryString();
    public Boolean shouldExecute();
    public void provide(String key, String val);
}
